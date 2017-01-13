package com.example.jhj0104.neglect;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.jhj0104.neglect.DB.DBHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.example.jhj0104.neglect.R.id.videoView2;

/**
 * Created by jhj0104 on 2017-01-11.
 */

public class csvReader extends AppCompatActivity{

    private  String CSV_URL = "storage/emulated/0/Neglect/20170111_194634.csv";
    private  String VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private  String TEST_VIDEO_URL = "storage/emulated/0/Movies/pororo season5.mp4";

    VideoView NeglectVideo;
    SeekBar seekBar;
    TextView result;
    Handler updateHandler = new Handler();
    String line = "";
    String NeglectSegLog ="";
    String cvsSplitBy = ",";
    String lastDate;
    String[] URL= new String[4];

    DBHelper dbHelper, dbHelper2;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cvs);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;



        result = (TextView) findViewById(R.id.resultText);
        NeglectVideo = (VideoView)findViewById(videoView2);
        seekBar = (SeekBar)findViewById(R.id.seekBar2);
        MediaController mc = new MediaController(this);
        NeglectVideo.setMediaController(mc);

        dbHelper = new DBHelper(getApplicationContext(),"NEGLECT_DB",1);
        dbHelper2 = new DBHelper(getApplicationContext(),"NEGLECT_DATA_DB",1);
        lastDate = dbHelper.get_NEGLECT_LASTDATE();
        URL = dbHelper2.get_NEGLECT_LASTDATE_URI(lastDate);

        CSV_URL = URL[1];
        VIDEO_URL = URL[3];

        //CSV 추출
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_URL))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] word = line.split(cvsSplitBy);

                //\t 안먹어서 이렇게 했다...
                if(word[2].equals(" X")){
                    NeglectSegLog += ("<Neglect Result> \nTestLoop  ContactX(pt)  ErrorPercent(%)\n");
                }
                else if(word[2].equals("fail")){
                    NeglectSegLog += (word[1]+"\t"+word[2]+blank(13)+":(\n");
                }
                else{
                    double[] a = {(Double.parseDouble(word[2])), (Double.parseDouble(word[6]))};
                    double[] b = {Math.round(a[0]*100d)/100d, Math.round(a[1]*100d)/100d};
                    if(word[1].equals(" 10"))NeglectSegLog += (word[1]+blank(18)+b[0]+blank(20)+b[1]+"\n");
                    else NeglectSegLog += (word[1]+blank(20)+b[0]+blank(20)+b[1]+"\n");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.setText(NeglectSegLog);
        result.setMovementMethod(new ScrollingMovementMethod());
    }
    public void onClick_resultIdentify(View view){
        finish();
    }
    public String blank(int n){
        String space = "";
        for(int i=0;i<n;i++) space+=" ";
        return space;
    }

    public void loadResultVideo(View view) {
        Toast.makeText(getApplicationContext(), "Loading Video. Plz wait", Toast.LENGTH_LONG).show();
        NeglectVideo.setVideoURI(Uri.parse(VIDEO_URL));
        NeglectVideo.requestFocus();

        // 토스트 다이얼로그, 버퍼링중임을 알린다.
        NeglectVideo.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                                        @Override
                                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                            switch(what){
                                                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                                    // Progress Diaglog 출력
                                                    Toast.makeText(getApplicationContext(), "Buffering", Toast.LENGTH_LONG).show();
                                                    break;
                                                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                                    // Progress Dialog 삭제
                                                    Toast.makeText(getApplicationContext(), "Buffering finished.\nResume playing", Toast.LENGTH_LONG).show();
                                                    NeglectVideo.start();
                                                    break;
                                            }
                                            return false;
                                        }
                                    }
        );

        // 플레이 준비되면, seekBar와 PlayTime을 세팅하고 플레이
        NeglectVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                NeglectVideo.start();
                long finalTime = NeglectVideo.getDuration();
                TextView tvTotalTime = (TextView) findViewById(R.id.tvTotalTime2);
                tvTotalTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
                seekBar.setMax((int) finalTime);
                seekBar.setProgress(0);
                updateHandler.postDelayed(updateVideoTime, 100);
                //Toast Box
                Toast.makeText(getApplicationContext(), "Playing Video", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void playVideo(View view){
        NeglectVideo.requestFocus();
        NeglectVideo.start();
    }

    public void pauseVideo(View view){
        NeglectVideo.pause();
    }
    // seekBar를 이동시키기 위한 쓰레드 객체
    // 100ms 마다 viewView의 플레이 상태를 체크하여, seekBar를 업데이트 한다.
    private Runnable updateVideoTime = new Runnable(){
        public void run(){
            long currentPosition = NeglectVideo.getCurrentPosition();
            seekBar.setProgress((int) currentPosition);
            updateHandler.postDelayed(this, 100);
        }
    };
}
