package com.example.jhj0104.neglect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jhj0104.neglect.DB.DBHelper;
import com.example.jhj0104.neglect.common.Loop;
import com.example.jhj0104.neglect.lib.dialog.MessageDialogFragment;
import com.example.jhj0104.neglect.lib.utils.PermissionCheck;
import com.example.jhj0104.neglect.service.ScreenRecorderService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static android.view.View.VISIBLE;
import static com.example.jhj0104.neglect.FileName.fileName;
import static com.example.jhj0104.neglect.MainActivity.REQUEST_PERMISSION_AUDIO_RECORDING;
import static com.example.jhj0104.neglect.MainActivity.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE;
import static com.example.jhj0104.neglect.common.NeglectStatus.REQUEST_CODE_SCREEN_CAPTURE;


/**
 * Created by jhj0104 on 2017-01-17.
 */

public class SLineBisection extends AppCompatActivity {
    File path, file, file_result;
    int loopNum, myIndex, testStringNum, buttonNum;
    double startTime, endTime, runTime, totalScore;
    boolean isPractice;
    TextView SLineText;
    RelativeLayout myLayout;

    // ★▲¤◆※♠♣#
    // 2(practice) + 10(test) = 12개
    private final String[][] SLinePracticeString = {
            {"▲","★","◆","★","※","★","▲","★","#","★","※","★","◆","★","★","♠","★","¤","★", "♣","★","★"
                    ,"♣","★","◆","★","#","★","♣","★","★","♠","★","¤","★","♠","★","¤","★","#","★","※","★","▲","★"},
            {"★","★","♣","★","◆","★","#","★","♣","★","♠","★","¤","★","♣","★","♠","★","▲", "★","◆","★",
                    "♠","★", "★","¤","★","#","★","※","★","★","▲","★","◆","★","※","★","¤","★","▲","★","#","★","※"}
    };
    private final String[][] SLineTestString = {
            {"♠","★","¤","★","#","★","※","★","★","▲","★","◆","★","※","★","♠","★","▲","★","#","★","※", "★"
                    ,"▲","★","◆","★","♠","★","★","♣","★","◆","★","#","★","★","♣","★","♠","★","¤","★","♣","★"},
            {"♠","★","¤","★","¤","★","▲","★","#","★","※","★","▲","★","◆","★","♠","★","★","♣","★","◆","★",
                    "#","★","★","♣","★","♠","★","¤","★","#","★","※","★","▲","★","◆","★","※","★","♣","★","★"},
            {"★","♠","★","★","♣","★","◆","★","#","★","※","★","★","▲","★","◆","★","※","★","¤","★","▲","★","#",
                    "★","※","★","▲","★","★","♣","★","♠","★","¤","★","♠","★","♣","★","¤","★","#","★","◆"},
            {"♣","★","♠","★","¤","★","▲","★","◆","★","※","★","▲","★","◆","★","♠","★","★","¤","★","▲","★","#",
                    "★","※","★","★","#","★","※","★","♣","★","◆","★","#","★","★","♣","★","♠","★","¤","★"},
            {"★","◆","★","※","★","¤","★","▲","★","#","★","※","★","▲","★","◆","★","♠","★","♠","★","¤","★","#",
                    "★","※","★","★","▲","★","★","♣","★","◆","★","#","★","★","♣","★","♠","★","¤","★","♣"},
            {"▲","★","♣","★","◆","★","#","★","★","♣","★","♠","★","◆","★","♠","★","★","#","★","※","★","¤","★",
                    "♣","★","♠","★","¤","★","★","▲","★","◆","★","※","★","¤","★","▲","★","#","★","※","★"},
            {"▲","★","◆","★","♠","★","★","♣","★","◆","★","#","★","★","♣","★","♠","★","¤","★","♣","★","♠","★",
                    "¤","★","#","★","※","★","★","▲","★","◆","★","※","★","¤","★","▲","★","#","★","※","★"},
            {"▲","★","◆","★","★","♣","★","♠","★","¤","★","♣","★","♠","★","¤","★","#","★","♠","★","★","♣","★",
                    "◆","★","#","★","※","★","★","▲","★","◆","★","※","★","¤","★","▲","★","#","★","※","★"},
            {"▲","★","◆","★","※","★","▲","★","#","★","※","★","◆","★","♠","★","★","¤","★","♣","★","★","♣","★",
                    "◆","★","#","★","★","♣","★","♠","★","¤","★","♠","★","¤","★","#","★","※","★","▲","★"},
            {"★","♣","★","◆","★","#","★","♣","★","♠","★","¤","★","♣","★","♠","★","★","▲","★","◆","★","♠","★",
                    "★","¤","★","#","★","※","★","★","▲","★","◆","★","※","★","¤","★","▲","★","#","★","※"}
    };



    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slinebisection);

        myLayout = (RelativeLayout) findViewById(R.id.SLineView);
        testCase_init();
        setCharacterLine_init();
    }

    public void testCase_init(){
        Intent intent = getIntent();
        Loop loop = (Loop) intent.getSerializableExtra("LoopData");
        loopNum = loop.loopNum;
        isPractice = loop.Practice;

        testStringNum=loopNum-1;
        if(loopNum==1) myRecord('s');
        getRunTime('s');

        TextView practicing = (TextView) findViewById(R.id.practiceSLine);
        if(!isPractice) practicing.setVisibility(View.INVISIBLE);
    }
    public void setCharacterLine_init(){

        if(isPractice) buttonNum = SLinePracticeString[testStringNum].length;
        else buttonNum = SLineTestString[testStringNum].length;

        //declare button & buttonParam Array
        ImageView[] images = new ImageView[buttonNum+1];
        RelativeLayout.LayoutParams[] imageParams = new RelativeLayout.LayoutParams[buttonNum+1];

        //set button n param
        for(int i=0; i<buttonNum; i++){
            imageParams[i] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            imageParams[i].addRule(RelativeLayout.CENTER_VERTICAL);
            imageParams[i].setMargins((i+1)*43,0,0,0);

//            buttons[i] = new Button(this);
            images[i] = new ImageView(getApplicationContext(), null, android.R.attr.absListViewStyle);
            images[i].setId(i);
            images[i].setLayoutParams(imageParams[i]);
            images[i].getLayoutParams().width = 45;

            String nowString;
            if(isPractice)nowString = SLinePracticeString[testStringNum][i];
            else nowString = SLineTestString[testStringNum][i];

            if(nowString == "★")   images[i].setImageResource(R.drawable.sc_star);
            else if(nowString == "▲")   images[i].setImageResource(R.drawable.sc_triangle);
            else if(nowString == "¤")   images[i].setImageResource(R.drawable.sc_ball);
            else if(nowString == "◆")   images[i].setImageResource(R.drawable.sc_diamond);
            else if(nowString == "※")   images[i].setImageResource(R.drawable.sc_reference);
            else if(nowString == "#")    images[i].setImageResource(R.drawable.sc_shap);
            else if(nowString == "♠"){
                images[i].setImageResource(R.drawable.sc_spade);
                images[i].getLayoutParams().width = 35;
            }
            else if(nowString == "♣"){
                images[i].setImageResource(R.drawable.sc_clover);
                images[i].getLayoutParams().width = 40;
            }
            images[i].setOnClickListener(buttonListner);
            myLayout.addView(images[i]);
        }
        setContentView(myLayout);
    }

    public View.OnClickListener buttonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() <= 45){
                myIndex = v.getId();
                if((isPractice && SLinePracticeString[testStringNum][myIndex]=="★")
                        || (!isPractice && SLineTestString[testStringNum][myIndex]=="★")){
                    ImageView redCircle = (ImageView) findViewById(R.id.circle);
                    redCircle.setVisibility(VISIBLE);
                    redCircle.setX((myIndex*43f+38));
                    redCircle.setY(730f);

                    Button btn_goNext = (Button) findViewById(R.id.btn_goNextSLine);
                    if(isPractice && loopNum==2) btn_goNext.setText("검사 시작");
                    if (loopNum>=10) btn_goNext.setText("검사 완료");
                    btn_goNext.setVisibility(VISIBLE);
                }
            }
        }
    };

    public void onClick_goNextSLine(View view) throws IOException {
        Loop loop;
        getRunTime('f');

        if(isPractice && loopNum>=2) {loop = new Loop("SLineBisection", false, 1); myRecord('f');}
        else if(loopNum < 10) loop = new Loop("SLineBisection", isPractice, loopNum+1);
        else loop = new Loop("SLineBisection", isPractice, loopNum);

        if(!isPractice) saveCSV();

        if(loopNum>=10) {finish(); myRecord('f');}
        else{
            Intent intent = new Intent(getApplicationContext(),SLineBisection.class);
            intent.putExtra("LoopData", loop);
            startActivity(intent);
            finish();
        }
    }

    public void saveCSV() throws IOException {

        DBHelper dbHelper = new DBHelper(getApplicationContext(),"NEGLECT_DB",1);

        String state = Environment.getExternalStorageState();
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        path = new File((absolutePath+"/Neglect"));
        file_result = new File(path, fileName + "_result.csv");

        FileWriter wr_result = new FileWriter(file_result, true);
        PrintWriter writer_result = new PrintWriter(wr_result);

        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SDcard Not Mounted", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!path.exists()) {
            path.mkdirs();
            Toast.makeText(getApplicationContext(), "create new folder", Toast.LENGTH_SHORT).show();
        }

        if(loopNum==1)writer_result.println("TestName, " + "TestLoopNum, " + "ErrCharacter, " + "runTime, " + "totalScore, ");
        writer_result.println("SLineBisection" + ", " + loopNum + "," + getErrCharacter(myIndex)+", "+getRunTime('w')+", "+getTotalScore()+", ");
        dbHelper.insert_NAGLECT_DB("0", "SLineBisection"); //0 = 주치의 ID
        writer_result.close();
    }


    public double getTotalScore(){
        int total  = 0;
        //환산 점수 계산
        return total;
    }
    public int getErrCharacter(int n){
        int errCharacter = n - 22; // midIndexNum = 22
        return errCharacter;
    }

    public double getRunTime(char mode) {
        if (mode == 's') {
            startTime = System.currentTimeMillis();
            runTime = 0;
        }
        else if(mode == 'f'){
            endTime = System.currentTimeMillis();
            runTime = ((endTime - startTime) * 0.001f); //(mul 0.001, change milliseconds to seconds)
        }
        return runTime;
    }


    //------------------------------ ↓↓ Screen Recording ↓↓ ------------------------------//
    public void CLineButtonMargin(int num){

    }
    public void myRecord(char status) {
        if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
            if (status == 's') {
                final Intent intent = new Intent(this, ScreenRecorderService.class);
                intent.setAction(ScreenRecorderService.ACTION_STOP);
                startService(intent);

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
