package com.example.jhj0104.neglect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jhj0104.neglect.DB.DBHelper;
import com.example.jhj0104.neglect.common.Loop;
import com.example.jhj0104.neglect.common.MyLine;
import com.example.jhj0104.neglect.common.MyLineSet;
import com.example.jhj0104.neglect.lib.dialog.MessageDialogFragment;
import com.example.jhj0104.neglect.lib.utils.BuildCheck;
import com.example.jhj0104.neglect.lib.utils.PermissionCheck;
import com.example.jhj0104.neglect.service.ScreenRecorderService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.example.jhj0104.neglect.FileName.fileName;
import static com.example.jhj0104.neglect.common.NeglectStatus.REQUEST_CODE_SCREEN_CAPTURE;
import static com.example.jhj0104.neglect.common.NeglectStatus.TEST_STATUS_LINEBISECTION;

//import static com.example.jhj0104.neglect.DrawView.lineSetsStatic;

public class LineBisection extends AppCompatActivity implements MessageDialogFragment.MessageDialogListener {

    int loopNum;
    boolean isPractice;
    boolean isContact;

    private float Width;
    private float Height;
    private float LinePixel;
    private float LineMargin;
    private float centerX, centerY;
    float[] X = {LineMargin, (LineMargin + LinePixel)};
    float[] Y = {centerY, centerY};
    double startTime, endTime, runTime;

    File path, file, file_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_bisection);

        Button goNext = (Button) findViewById(R.id.btn_goNext);
        TextView practicing = (TextView) findViewById(R.id.practicing);

        Intent intent3 = getIntent();
        Loop loop = (Loop) intent3.getSerializableExtra("LoopData");
        loopNum = loop.loopNum;
        isPractice = loop.Practice;

        DisplayMetrics dm = getResources().getDisplayMetrics();
        float densityDpi = dm.densityDpi;
        Width = dm.widthPixels;
        Height = dm.heightPixels;
        centerX = Width / 2;
        centerY = Height / 2;

        int MaxCm = (int) ((Width * 2.54f) / densityDpi);
        if (MaxCm >= 18) LinePixel = (densityDpi * 18f) / 2.54f;
        else LinePixel = (densityDpi * ((float) MaxCm - 1f)) / 2.54f;

        LinePixel *= 0.833f; //오차 보정(임의로 계산한 값)
        LineMargin = (Width - LinePixel) / 2f;

        this.X[0] = LineMargin;
        this.X[1] = (LineMargin + LinePixel);
        this.Y[0] = centerY;
        this.Y[1] = centerY;

        //Set toast & btn
        if (isPractice) {
            Toast.makeText(getApplicationContext(), "연습 테스트입니다. " + (3 - loopNum) + "번 남았습니다.", Toast.LENGTH_SHORT).show();
            if (loopNum == 2) goNext.setText("테스트 시작");
        } else {

            practicing.setVisibility(View.INVISIBLE);
            if (loopNum != 10)
                Toast.makeText(getApplicationContext(), "테스트가 " + (11 - loopNum) + "번 남았습니다", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getApplicationContext(), "마지막 테스트 입니다", Toast.LENGTH_SHORT).show();
                goNext.setText("검사 완료");
            }
        }
        runTime = checkTime('s');
        if(loopNum==1) myRecord('s');
    }


    //  data save in sd carc 참고: http://kitesoft.tistory.com/45
    public void onClick_goNext(View view) throws IOException {

        DBHelper dbHelper = new DBHelper(getApplicationContext(),"NEGLECT_DB",1);
        DBHelper dbHelper2 = new DBHelper(getApplicationContext(),"NEGLECT_DATA_DB",1);
        double StartX, StartY, EndX, EndY;
        runTime = checkTime('f');

        DrawView drawView = (DrawView) findViewById(R.id.view);
        List<MyLineSet> lineSetsStatic = drawView.getLineSets();

        if (!isPractice) {
            String state = Environment.getExternalStorageState();
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();

            path = new File((absolutePath+"/Neglect"));
            file = new File(path, fileName + ".csv");
            file_result = new File(path, fileName + "_result.csv");

            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, "SDcard Not Mounted", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!path.exists()) {
                path.mkdirs();
                Toast.makeText(getApplicationContext(), "create new folder", Toast.LENGTH_SHORT).show();
            }
            try {
                FileWriter wr = new FileWriter(file, true);
                FileWriter wr_result = new FileWriter(file_result, true);
                PrintWriter writer = new PrintWriter(wr);
                PrintWriter writer_result = new PrintWriter(wr_result);

                isContact = false;

                for (int i = 0; i < lineSetsStatic.size(); i++) {
                    MyLineSet set = lineSetsStatic.get(i);

                    for (int j = 0; j < set.getLines().size(); ++j) {

                        MyLine l = set.getLines().get(j);
                        StartX = l.getStartPt().getX();
                        StartY = l.getStartPt().getY();
                        EndX = l.getEndPt().getX();
                        EndY = l.getEndPt().getY();
                        String myTestMode = "LineBisection";

                        if (i == 0 && j == 0 && loopNum == 1) {
                            writer.println("TestName, " + "TestNum, " + "startX, " + "startY, " + "endX, " + "endY");
                            writer_result.println("TestName, " + "TestNum, " + "X, " + "Y, " + "err_pixel, " + "err_mm, " + "err_per, " + "runTime");
                        }

                        if (!isContact) {
                            String xy = calculator(StartX, StartY, EndX, EndY);
                            if (xy != null) {
                                String answer = "LineBisection, " + loopNum + "," + xy + ", " + runTime;
                                writer_result.println(answer);
                                isContact = true;
                            }
                        }
                        writer.println(myTestMode + "," + loopNum + "," + StartX + "," + StartY + "," + EndX + "," + EndY);
                        if (i == lineSetsStatic.size() - 1 && j == set.getLines().size() - 1 && !isContact)
                            writer_result.println(myTestMode + ", " + loopNum + "," + "fail");
                    }
                }
                lineSetsStatic.clear();
                writer.close();
                writer_result.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            dbHelper2.insert_NEGLECT_DATA_DB(fileName, TEST_STATUS_LINEBISECTION, "1", path+"/"+fileName + ".csv");
            dbHelper2.insert_NEGLECT_DATA_DB(fileName, TEST_STATUS_LINEBISECTION, "2", path+"/"+fileName + "_result.csv");
        }

        if ((isPractice && loopNum <= 2) || (!isPractice && loopNum < 10)) {
            Loop loop;
            if (isPractice && loopNum == 2) {myRecord('f'); loop = new Loop("LineBisection", false, 1);}
            else loop = new Loop("LineBisection", isPractice, loopNum + 1);

            Intent intent = new Intent(getApplicationContext(), LineBisection.class);
            intent.putExtra("LoopData", loop);
            startActivity(intent);
        }
        else{
            myRecord('f');
            dbHelper.insert_NAGLECT_DB("0", fileName);
            Intent intent = new Intent(getApplicationContext(), csvReader.class);
            startActivity(intent);
        }
        finish();
    }


    @Override
    public void onBackPressed() {
        //실제 테스트 시 block
        super.onBackPressed();
    }

    public String calculator(double p1_x, double p1_y, double p2_x, double p2_y) {
        double x, y;
        double tmp;

        if (p1_y > p2_y) {
            tmp = p1_y;
            p1_y = p2_y;
            p2_y = tmp;
        }
        if (p1_x > p2_x) {
            tmp = p1_x;
            p1_x = p2_x;
            p2_x = tmp;
        }
        if (p1_y <= centerY && centerY <= p2_y) {
        } else return null;

        double a = Math.abs(p1_y - centerY);
        double b = Math.abs(p2_y - centerY);
        double xx = Math.abs(p2_x - p1_x);

        x = p1_x + (xx * (a / (a + b)));
        y = centerY;

        if (x < LineMargin || x > (Width - LineMargin)) return null;

        return (x + ", " + y + "," + ErrorData(x));
    }

    public String ErrorData(double x) {

        //float DPI = dm.densityDpi;
        float DPI = 560;
        double err_pixel = Math.abs(centerX - x);
        double err_mm = (err_pixel * 2.54) / DPI * 10;
        double err_per = (err_pixel / (Width - LineMargin)) * 100;
        String result = err_pixel + ", " + err_mm + ", " + err_per;
        // add 환산점수

        return result;
    }

    public double checkTime(char mode) {
        if (mode == 's') {
            startTime = System.currentTimeMillis();
            runTime = 0;
        } else {
            endTime = System.currentTimeMillis();
            runTime = (long) ((endTime - startTime) * 0.001); //(mul 0.001, change milliseconds to seconds)
        }
        return runTime;
    }





    //------------------------------ ↓↓ Screen Recording ↓↓ ------------------------------//
    public void myRecord(char status) {
        if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
            if (status == 's') {
                final MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                final Intent permissionIntent = manager.createScreenCaptureIntent();
                startActivityForResult(permissionIntent, REQUEST_CODE_SCREEN_CAPTURE);
            }
            else {
                final Intent intent = new Intent(this, ScreenRecorderService.class);
                intent.setAction(ScreenRecorderService.ACTION_STOP);
                startService(intent);
            }
        }
        else
            Toast.makeText(getApplicationContext(), "권한이 허용되지 않아 화면 녹화 할 수 없습니다.", Toast.LENGTH_SHORT).show();
    }
    private void startScreenRecorder(final int resultCode, final Intent data) {
        Intent intent = new Intent(this, ScreenRecorderService.class);
        intent.setAction(ScreenRecorderService.ACTION_START);
        intent.putExtra(ScreenRecorderService.EXTRA_RESULT_CODE, resultCode);
        intent.putExtras(data);
        startService(intent);
    }

    //인코딩
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_SCREEN_CAPTURE == requestCode) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                return;
            }
            startScreenRecorder(resultCode, data);
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
}
