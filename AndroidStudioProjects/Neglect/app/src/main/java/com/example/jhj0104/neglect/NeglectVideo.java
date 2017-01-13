package com.example.jhj0104.neglect;

import android.Manifest;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.concurrent.TimeUnit;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * Created by jhj0104 on 2017-01-11.
 */

public class NeglectVideo extends AppCompatActivity {

    final static String SAMPLE_VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    final static String SAMPLE_VIDEO_URL2 = "storage/emulated/0/Movies/Neglect Screen Recorder/2017-01-10-15-59-39.mp4";
    VideoView videoView;
    SeekBar seekBar;
    Handler updateHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        EditText tvURL = (EditText)findViewById(R.id.etVieoURL);
        tvURL.setText(SAMPLE_VIDEO_URL);

        videoView = (VideoView)findViewById(R.id.videoView);
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);

        seekBar = (SeekBar)findViewById(R.id.seekBar);

        String[] permissions = {Manifest.permission.INTERNET, Manifest.permission.WAKE_LOCK, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_GRANTED);
    }

    public void loadVideo(View view) {
        EditText tvURL = (EditText) findViewById(R.id.etVieoURL);
        String url = tvURL.getText().toString();

        Toast.makeText(getApplicationContext(), "Loading Video. Plz wait", Toast.LENGTH_LONG).show();
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();

        // 토스트 다이얼로그, 버퍼링중임을 알린다.
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {

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
                                                    videoView.start();
                                                    break;
                                            }
                                            return false;
                                        }
                                    }
        );

        // 플레이 준비가 되면, seekBar와 PlayTime을 세팅하고 플레이
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                long finalTime = videoView.getDuration();
                TextView tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);
                tvTotalTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );
                seekBar.setMax((int) finalTime);
                seekBar.setProgress(0);
                updateHandler.postDelayed(updateVideoTime, 100);
                //Toast Box
                Toast.makeText(getApplicationContext(), "Playing Video", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void playVideo(View view){
        videoView.requestFocus();
        videoView.start();
    }

    public void pauseVideo(View view){
        videoView.pause();
    }

    // seekBar를 이동시키기 위한 쓰레드 객체
    // 100ms 마다 viewView의 플레이 상태를 체크하여, seekBar를 업데이트 한다.
    private Runnable updateVideoTime = new Runnable(){
        public void run(){
            long currentPosition = videoView.getCurrentPosition();
            seekBar.setProgress((int) currentPosition);
            updateHandler.postDelayed(this, 100);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_GRANTED) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                // save file
            } else {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
