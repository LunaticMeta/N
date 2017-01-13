package com.example.jhj0104.neglect.lib.utils;

/**
 * Created by jhj0104 on 2017-01-06.
 */

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

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class PermissionCheck {
    public PermissionCheck() {
    }

    public static final void dumpPermissions(Context context) {
        if(context != null) {
            try {
                PackageManager e = context.getPackageManager();
                List list = e.getAllPermissionGroups(128);
                Iterator var3 = list.iterator();

                while(var3.hasNext()) {
                    PermissionGroupInfo info = (PermissionGroupInfo)var3.next();
//                    Log.d("PermissionCheck", info.name);
                }
            } catch (Exception var5) {
                Log.w("", var5);
            }

        }
    }

    @SuppressLint({"NewApi"})
    public static boolean hasPermission(Context context, String permissionName) {
        if(context == null) {
            return false;
        } else {
            boolean result = false;

            try {
                int e;
                if(BuildCheck.isMarshmallow()) {
                    e = context.checkSelfPermission(permissionName);
                } else {
                    PackageManager pm = context.getPackageManager();
                    e = pm.checkPermission(permissionName, context.getPackageName());
                }

                switch(e) {
                    case -1:
                    default:
                        break;
                    case 0:
                        result = true;
                }
            } catch (Exception var5) {
                Log.w("", var5);
            }

            return result;
        }
    }

    public static boolean hasAudio(Context context) {
        return hasPermission(context, "android.permission.RECORD_AUDIO");
    }

    public static boolean hasNetwork(Context context) {
        return hasPermission(context, "android.permission.INTERNET");
    }

    public static boolean hasWriteExternalStorage(Context context) {
        return hasPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE");
    }

    @SuppressLint({"InlinedApi"})
    public static boolean hasReadExternalStorage(Context context) {
        return BuildCheck.isAndroid4()?hasPermission(context, "android.permission.READ_EXTERNAL_STORAGE"):hasPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE");
    }

    public static boolean hasAccessLocation(Context context) {
        return hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION") && hasPermission(context, "android.permission.ACCESS_FINE_LOCATION");
    }

    public static boolean hasAccessCoarseLocation(Context context) {
        return hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION");
    }

    public static boolean hasAccessFineLocation(Context context) {
        return hasPermission(context, "android.permission.ACCESS_FINE_LOCATION");
    }

    public static boolean hasCamera(Context context) {
        return hasPermission(context, "android.permission.CAMERA");
    }

    public static void openSettings(Context context) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        Uri uri = Uri.fromParts("package", context.getPackageName(), (String)null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static List<String> missingPermissions(Context context, String[] expectations) throws IllegalArgumentException, PackageManager.NameNotFoundException {
        return missingPermissions(context, (List)(new ArrayList(Arrays.asList(expectations))));
    }

    public static List<String> missingPermissions(Context context, List<String> expectations) throws IllegalArgumentException, PackageManager.NameNotFoundException {
        if(context != null && expectations != null) {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 4096);
            String[] info = pi.requestedPermissions;
            if(info != null) {
                String[] var5 = info;
                int var6 = info.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String i = var5[var7];
                    expectations.remove(i);
                }
            }

            return expectations;
        } else {
            throw new IllegalArgumentException("context or expectations is null");
        }
    }
}