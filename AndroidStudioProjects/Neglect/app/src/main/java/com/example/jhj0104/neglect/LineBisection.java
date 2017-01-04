package com.example.jhj0104.neglect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jhj0104.neglect.common.MyLine;
import com.example.jhj0104.neglect.common.MyLineSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.example.jhj0104.neglect.FileName.fileName;

//import static com.example.jhj0104.neglect.DrawView.lineSetsStatic;

public class LineBisection extends AppCompatActivity {
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
    long startTime, endTime, runTime;

    File path, file, file_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_bisection);

        Intent intent = getIntent();
        Loop loop = (Loop) intent.getSerializableExtra("LoopData");
        int loopNum = loop.loopNum;
        boolean isPractice = loop.Practice;
        this.loopNum = loopNum;
        this.isPractice = isPractice;

        Button goNext = (Button) findViewById(R.id.btn_goNext);
        TextView practicing = (TextView) findViewById(R.id.practicing);

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
    }


    //  data save in sd carc 참고: http://kitesoft.tistory.com/45
    public void onClick_goNext(View view) throws IOException {
        double StartX, StartY, EndX, EndY;
        runTime = checkTime('f');

        DrawView drawView = (DrawView) findViewById(R.id.view);
        List<MyLineSet> lineSetsStatic = drawView.getLineSets();

        if (!isPractice) {
            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, "SDcard Not Mounted", Toast.LENGTH_SHORT).show();
                return;
            }

            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            path = new File(getExternalCacheDir(), "NewDirectory");
            path = new File((absolutePath+"/Neglect"));
            if (!path.exists()) {
                path.mkdirs();
                Toast.makeText(getApplicationContext(), "create new folder", Toast.LENGTH_SHORT).show();
            }

            file = new File(path, fileName + ".csv");
            file_result = new File(path, fileName + "_result.csv");

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
        }

        if ((isPractice && loopNum <= 2) || (!isPractice && loopNum < 10)) {
            Loop loop;

            if (isPractice && loopNum == 2) loop = new Loop("LineBisection", false, 1);
            else loop = new Loop("LineBisection", isPractice, loopNum + 1);

            Intent intent = new Intent(getApplicationContext(), LineBisection.class);
            intent.putExtra("LoopData", loop);
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

    public long checkTime(char mode) {
        if (mode == 's') {
            startTime = System.currentTimeMillis();
            runTime = 0;
        } else {
            endTime = System.currentTimeMillis();
            runTime = (long) ((endTime - startTime) * 0.001); //(mul 0.001 change milliseconds to seconds)
        }
        return runTime;
    }
}
