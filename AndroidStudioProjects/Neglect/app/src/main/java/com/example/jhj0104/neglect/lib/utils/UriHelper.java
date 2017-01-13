package com.example.jhj0104.neglect.lib.utils;

/*
 * libcommon
 * utility/helper classes for myself
 *
 * Copyright (c) 2014-2016 saki t_saki@serenegiant.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public final class UriHelper {
    private static final String TAG = UriHelper.class.getSimpleName();

    public UriHelper() {
    }

    public static String getAbsolutePath(ContentResolver cr, Uri uri) {
        String path = null;

        try {
            String[] columns = new String[]{"_data"};
            Cursor cursor = cr.query(uri, columns, null, null, null);
            if(cursor != null) {
                try {
                    if(cursor.moveToFirst()) {
                        path = cursor.getString(0);
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception var9) {
        }

        return path;
    }

    public static String getPath(Context context, Uri uri) {
        Log.i(TAG, "getPath:uri=" + uri);
        if(BuildCheck.isKitKat() && DocumentsContract.isDocumentUri(context, uri)) {
            Log.i(TAG, "getPath:isDocumentUri,getAuthority=" + uri.getAuthority());
            String docId;
            String[] split;
            String type;
            if(isExternalStorageDocument(uri)) {
                docId = DocumentsContract.getDocumentId(uri);
                Log.i(TAG, "getPath:isDocumentUri,docId=" + docId);
                Log.i(TAG, "getPath:isDocumentUri,getTreeDocumentId=" + DocumentsContract.getTreeDocumentId(uri));
                split = docId.split(":");
                type = split[0];
                Log.i(TAG, "getPath:type=" + type);
                if("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                String contentUri = Environment.getExternalStorageDirectory().getAbsolutePath();
                Log.i(TAG, "getPath:primary=" + contentUri);
                File[] selection = context.getExternalFilesDirs((String)null);
                int selectionArgs = selection != null?selection.length:0;
                StringBuilder sb = new StringBuilder();

                for(int i = 0; i < selectionArgs; ++i) {
                    File dir = selection[i];
                    Log.i(TAG, "getPath:" + i + ")dir=" + dir);
                    if(dir == null || !dir.getAbsolutePath().startsWith(contentUri)) {
                        String dir_path = dir.getAbsolutePath();
                        String[] dir_elements = dir_path.split("/");
                        int m = dir_elements != null?dir_elements.length:0;
                        if(m > 1 && "storage".equalsIgnoreCase(dir_elements[1])) {
                            boolean found = false;
                            sb.setLength(0);
                            sb.append('/').append(dir_elements[1]);

                            for(int path = 2; path < m; ++path) {
                                if("Android".equalsIgnoreCase(dir_elements[path])) {
                                    found = true;
                                    break;
                                }

                                sb.append('/').append(dir_elements[path]);
                            }

                            if(found) {
                                File var20 = new File(new File(sb.toString()), split[1]);
                                Log.i(TAG, "getPath:path=" + var20);
                                if(var20.exists() && var20.canWrite()) {
                                    return var20.getAbsolutePath();
                                }
                            }
                        }
                    }
                }
            } else {
                if(isDownloadsDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri);
                    Uri var16 = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId).longValue());
                    return getDataColumn(context, var16, null, null);
                }

                if(isMediaDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri);
                    split = docId.split(":");
                    type = split[0];
                    Uri var17 = null;
                    if("image".equals(type)) {
                        var17 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if("video".equals(type)) {
                        var17 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if("audio".equals(type)) {
                        var17 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String var18 = "_id=?";
                    String[] var19 = new String[]{split[1]};
                    return getDataColumn(context, var17, "_id=?", var19);
                }
            }
        } else {
            if("content".equalsIgnoreCase(uri.getScheme())) {
                if(isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }

                return getDataColumn(context, uri, null, null);
            }

            if("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = new String[]{"_data"};

        String var8;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if(cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            int column_index = cursor.getColumnIndexOrThrow("_data");
            var8 = cursor.getString(column_index);
        } finally {
            if(cursor != null) {
                cursor.close();
            }

        }

        return var8;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
