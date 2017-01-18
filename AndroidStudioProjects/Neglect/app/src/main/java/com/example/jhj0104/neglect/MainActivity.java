package com.example.jhj0104.neglect;
/*
 * ScreenRecordingSample
 * Sample project to cature and save audio from internal and video from screen as MPEG4 file.
 *
 * Copyright (c) 2015 saki t_saki@serenegiant.com
 *
 * File name: MainActivity.java
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
 *
 * All files in the folder are under this Apache License, Version 2.0.
*/

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.jhj0104.neglect.common.Loop;
import com.example.jhj0104.neglect.lib.dialog.MessageDialogFragment;
import com.example.jhj0104.neglect.lib.utils.BuildCheck;
import com.example.jhj0104.neglect.lib.utils.PermissionCheck;
import com.example.jhj0104.neglect.service.ScreenRecorderService;

import java.lang.ref.WeakReference;

public final class MainActivity extends Activity implements MessageDialogFragment.MessageDialogListener {

    private static final boolean DEBUG = false;
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_SCREEN_CAPTURE = 1;
    private MyBroadcastReceiver mReceiver;
    volatile boolean isPractice = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.v(TAG, "onCreate:");
        setContentView(R.layout.activity_menu);
        if (mReceiver == null) {
            mReceiver = new MyBroadcastReceiver(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.v(TAG, "onResume:");
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScreenRecorderService.ACTION_QUERY_STATUS_RESULT);
        registerReceiver(mReceiver, intentFilter);
        queryRecordingStatus();
    }

    @Override
    protected void onPause() {
        if (DEBUG) Log.v(TAG, "onPause:");
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (DEBUG) Log.v(TAG, "onActivityResult:resultCode=" + resultCode + ",data=" + data);
        startScreenRecorder(resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_SCREEN_CAPTURE == requestCode) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                return;
            }
            else startScreenRecorder(resultCode, data);
        }
    }

    public void onClick_startTest(View view){
        new FileName();
        Intent intent = new Intent(getApplicationContext(),Notice.class);
        startActivity(intent);
    }
    public void onClick_startExperiment (View view){
//        Intent intent = new Intent(getApplicationContext(),NeglectVideo.class);
        Intent intent = new Intent(getApplicationContext(),csvReader.class);
        startActivity(intent);
    }
    public void CLineBisection_Start(View view){
//        Intent intent = new Intent(getApplicationContext(),CLineBisection.class);
//        startActivity(intent);
        new FileName();
        Loop loop = new Loop("CLineBisection", true);
        Intent intent = new Intent(this,CLineBisection.class);
        intent.putExtra("LoopData", loop);
        startActivity(intent);
    }
    public void SLineBisection_Start(View view){
//        Intent intent = new Intent(getApplicationContext(),CLineBisection.class);
//        startActivity(intent);
        new FileName();
        Loop loop = new Loop("SLineBisection", true);
        Intent intent = new Intent(this,SLineBisection.class);
        intent.putExtra("LoopData", loop);
        startActivity(intent);
    }

    private void queryRecordingStatus() {
        if (DEBUG) Log.v(TAG, "queryRecording:");
        final Intent intent = new Intent(this, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_QUERY_STATUS);
        startService(intent);
    }

    private void startScreenRecorder(final int resultCode, final Intent data) {
        Intent intent = new Intent(this, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_START);
        intent.putExtra(ScreenRecorderService.EXTRA_RESULT_CODE, resultCode);
        intent.putExtras(data);
        startService(intent);
    }

    private static final class MyBroadcastReceiver extends BroadcastReceiver {
        private final WeakReference<MainActivity> mWeakParent;
        public MyBroadcastReceiver(final MainActivity parent) {
            mWeakParent = new WeakReference<MainActivity>(parent);
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (DEBUG) Log.v(TAG, "onReceive:" + intent);
            final String action = intent.getAction();
            if (ScreenRecorderService.ACTION_QUERY_STATUS_RESULT.equals(action)) {
            }
        }
    }

//================================================================================
// methods related to new permission model on Android 6 and later
//================================================================================
    /**
     * Callback listener from MessageDialogFragmentV4
     * @param dialog
     * @param requestCode
     * @param permissions
     * @param result
     */
    @SuppressLint("NewApi")
    @Override
    public void onMessageDialogResult(final MessageDialogFragment dialog, final int requestCode, final String[] permissions, final boolean result) {
        if (result) {
            // request permission(s) when user touched/clicked OK
            if (BuildCheck.isMarshmallow()) {
                requestPermissions(permissions, requestCode);
                return;
            }
        }
        // check permission and call #checkPermissionResult when user canceled or not Android6(and later)
        for (final String permission: permissions) {
            checkPermissionResult(requestCode, permission, PermissionCheck.hasPermission(this, permission));
        }
    }

    /**
     * callback method when app(Fragment) receive the result of permission result from ANdroid system
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);	// 何もしてないけど一応呼んどく
        final int n = Math.min(permissions.length, grantResults.length);
        for (int i = 0; i < n; i++) {
            checkPermissionResult(requestCode, permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }
    }

    /**
     * check the result of permission request
     * if app still has no permission, just show Toast
     * @param requestCode
     * @param permission
     * @param result
     */
    protected void checkPermissionResult(final int requestCode, final String permission, final boolean result) {
        // show Toast when there is no permission
        if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
            onUpdateAudioPermission(result);
            if (!result) {
                Toast.makeText(this, R.string.permission_audio, Toast.LENGTH_SHORT).show();
            }
        }
        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
            onUpdateExternalStoragePermission(result);
            if (!result) {
                Toast.makeText(this, R.string.permission_ext_storage, Toast.LENGTH_SHORT).show();
            }
        }
        if (Manifest.permission.INTERNET.equals(permission)) {
            onUpdateNetworkPermission(result);
            if (!result) {
                Toast.makeText(this, R.string.permission_network, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * called when user give permission for audio recording or canceled
     * @param hasPermission
     */
    protected void onUpdateAudioPermission(final boolean hasPermission) {
    }

    /**
     * called when user give permission for accessing external storage or canceled
     * @param hasPermission
     */
    protected void onUpdateExternalStoragePermission(final boolean hasPermission) {
    }

    /**
     * called when user give permission for accessing network or canceled
     * this will not be called
     * @param hasPermission
     */
    protected void onUpdateNetworkPermission(final boolean hasPermission) {
    }

    protected static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x01;
    protected static final int REQUEST_PERMISSION_AUDIO_RECORDING = 0x02;
    protected static final int REQUEST_PERMISSION_NETWORK = 0x03;

    /**
     * check whether this app has write external storage
     * if this app has no permission, show dialog
     * @return true this app has permission
     */
    protected boolean checkPermissionWriteExternalStorage() {
        if (!PermissionCheck.hasWriteExternalStorage(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
                    R.string.permission_title, R.string.permission_ext_storage_request,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            return false;
        }
        return true;
    }

    /**
     * check whether this app has permission of audio recording
     * if this app has no permission, show dialog
     * @return true this app has permission
     */
    protected boolean checkPermissionAudio() {
        if (!PermissionCheck.hasAudio(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_AUDIO_RECORDING,
                    R.string.permission_title, R.string.permission_audio_recording_request,
                    new String[]{Manifest.permission.RECORD_AUDIO});
            return false;
        }
        return true;
    }

    /**
     * check whether permission of network access
     * if this app has no permission, show dialog
     * @return true this app has permission
     */
    protected boolean checkPermissionNetwork() {
        if (!PermissionCheck.hasNetwork(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_NETWORK,
                    R.string.permission_title, R.string.permission_network_request,
                    new String[]{Manifest.permission.INTERNET});
            return false;
        }
        return true;
    }

}
