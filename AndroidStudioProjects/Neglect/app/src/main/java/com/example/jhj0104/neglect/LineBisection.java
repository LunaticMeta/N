package com.example.jhj0104.neglect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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

import static android.R.attr.centerX;
import static com.example.jhj0104.neglect.DrawView.lineSetsStatic;
import static com.example.jhj0104.neglect.FileName.fileName;

public class LineBisection extends AppCompatActivity {

    float LineLength = 1700f;
    float centerY = 540;
    float LineMargin = 251.6f;

    float[] X = {LineMargin,(LineMargin+LineLength)};
    float[] Y = {centerY,centerY};

    int loopNum;
    boolean isPractice;

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

        if(isPractice==true){
            Toast.makeText(getApplicationContext(), "연습 테스트입니다. "+(3-loopNum)+"회 남음.", Toast.LENGTH_SHORT).show();
            if(loopNum==2){
                Button goNext = (Button) findViewById(R.id.btn_goNext);
                goNext.setText("테스트 시작");
            }
        }
        else{
            if(loopNum!=10)Toast.makeText(getApplicationContext(), "테스트가 "+(11-loopNum)+"번 남았습니다", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getApplicationContext(), "마지막 테스트 입니다", Toast.LENGTH_SHORT).show();
                Button goNext = (Button) findViewById(R.id.btn_goNext);
                goNext.setText("검사 완료");
            }

            TextView practicing = (TextView) findViewById(R.id.practicing);
            practicing.setVisibility(View.INVISIBLE);
        }
    }

    //  참고: http://kitesoft.tistory.com/45
    public void onClick_goNext (View view) throws IOException {
        //마지막에서 loop에서 파일 입력
        if(isPractice == false) {

            String state = Environment.getExternalStorageState();
            File path, path_result;
            File file, file_result;

            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, "SDcard Not Mounted", Toast.LENGTH_SHORT).show();
                return;
            }

            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            path = new File((absolutePath + "/custDir"));
            path_result = new File((absolutePath + "/custDir"));

            file = new File(path, fileName + ".csv");
            file_result = new File(path_result, fileName + "_result.csv");

            try {
                FileWriter wr = new FileWriter(file, true);
                FileWriter wr_result = new FileWriter(file_result, true);
                PrintWriter writer = new PrintWriter(wr);
                PrintWriter writer_result = new PrintWriter(wr_result);

                boolean isContact = false;

                MyLineSet set = lineSetsStatic.get(loopNum-1);
                for (int j = 0; j < set.getLines().size(); ++j) {
                    MyLine l = set.getLines().get(j);

                    double StartX = l.getStartPt().getX();
                    double StartY = l.getStartPt().getY();
                    double EndX = l.getEndPt().getX();
                    double EndY = l.getEndPt().getY();
                    String myTestMode = "LineBisection";
                    String load = myTestMode + "," + loopNum + "," + StartX + "," + StartY + "," + EndX + "," + EndY + ",";

                    if (isContact == false) {
                        isContact = isContacted(StartX, StartY, EndX, EndY);

                        if (isContact == true) {
                            String answer = ContactData(StartX, StartY, EndX, EndY);
                            writer_result.println(answer);
                        }
                    }
                    writer.println(load);
                }

                writer.close();
                writer_result.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        //실제 테스트 시 block
        super.onBackPressed();
    }

    public boolean isContacted(double p1_x, double p1_y, double p2_x, double p2_y){
        //F = float(double로 후에 변경), increase, constant, sameValue;
        double FI1 = 0, FI2 = 0, FC1 = 0, FC2 = 0, FS1 = 0, FS2 = 0;
        double x, y;
        double p3_x = X[0], p3_y = X[1], p4_x = Y[0], p4_y = Y[1];

        if(p1_x == p2_x) FS1 = p1_x;
        else{
            FI1 = (p2_y - p1_y) / (p2_x - p1_x);
            FC1 = p1_y - FI1*p1_x;
        }
        if(p3_x == p4_x) FS2 = p3_x;
        else{
            FI2 = (p4_y - p3_y) / (p4_x - p3_x);
            FC2 = p3_y - FI2*p3_x;
        }

        if(p1_x == p2_x && p3_x == p4_x) return false;
        if(p1_x == p2_x){
            x = FS1;
            y = FI2*FS1+FC2;
        }
        else if(p3_x == p4_x){
            x = FS2;
            y = FI1*FS2+FC1;
        }
        else{
            x = -(FC1-FC2) / (FI1-FI2);
            y = FI1*x+FC1;
        }

        if(x< p3_x || x> p4_x) return false;
        if(x< p1_x || x> p2_x || y < p1_y || y > p2_y) return false;

        return true;
    }

    public Vertex isContacted_1(double p1_x, double p1_y, double p2_x, double p2_y){
        //F = float(double로 후에 변경), increase, constant, sameValue;
        double FI1 = 0, FI2 = 0, FC1 = 0, FC2 = 0, FS1 = 0, FS2 = 0;
        double x, y;
        double p3_x = X[0], p3_y = X[1], p4_x = Y[0], p4_y = Y[1];

        if(p1_x == p2_x) FS1 = p1_x;
        else{
            FI1 = (p2_y - p1_y) / (p2_x - p1_x);
            FC1 = p1_y - FI1*p1_x;
        }
        if(p3_x == p4_x) FS2 = p3_x;
        else{
            FI2 = (p4_y - p3_y) / (p4_x - p3_x);
            FC2 = p3_y - FI2*p3_x;
        }

        if(p1_x == p2_x && p3_x == p4_x) return null;
        if(p1_x == p2_x){
            x = FS1;
            y = FI2*FS1+FC2;
        }
        else if(p3_x == p4_x){
            x = FS2;
            y = FI1*FS2+FC1;
        }
        else{
            x = -(FC1-FC2) / (FI1-FI2);
            y = FI1*x+FC1;
        }

        if(x< p3_x || x> p4_x) return null;
        if(x< p1_x || x> p2_x || y < p1_y || y > p2_y) return null;

        return new Vertex( (float)x, (float)y );
    }


    public String ContactData(double p1_x, double p1_y, double p2_x, double p2_y){

        //F = float(double로 후에 변경), increase, constant, sameValue;
        double FI1 = 0, FI2 = 0, FC1 = 0, FC2 = 0, FS1 = 0, FS2 = 0;
        double x, y;
        double p3_x = X[0];
        double p3_y = X[1];
        double p4_x = Y[0];
        double p4_y = Y[1];

        if(p1_x == p2_x) FS1 = p1_x;
        else{
            FI1 = (p2_y - p1_y) / (p2_x - p1_x);
            FC1 = p1_y - FI1*p1_x;
        }
        if(p3_x == p4_x) FS2 = p3_x;
        else{
            FI2 = (p4_y - p3_y) / (p4_x - p3_x);
            FC2 = p3_y - FI2*p3_x;
        }

        if(p1_x == p2_x && p3_x == p4_x) return "Error code : -1";
        if(p1_x == p2_x){
            x = FS1;
            y = FI2*FS1+FC2;
        }
        else if(p3_x == p4_x){
            x = FS2;
            y = FI1*FS2+FC1;
        }
        else{
            x = -(FC1-FC2) / (FI1-FI2);
            y = FI1*x+FC1;
        }

        if(x< p3_x || x> p4_x) return "Error code : -2 \n" + x + ", " + y;
        if(x< p1_x || x> p2_x || y < p1_y || y > p2_y) return "Error code : -3 \n" + x + ", " + y;

        return x+", "+y+"\n"+ErrorSize(x);
    }

    //교점의 오차 출력
    public String ErrorSize(double x){

        //float DPI = dm.densityDpi;
        float DPI = 560;
        double err_pixel = Math.abs(centerX-x);
        double err_mm = (err_pixel*2.54)/DPI*10;
        double err_per = (err_pixel/LineLength)*100;

        String a = String.format("%.1f", err_pixel)+"pixel";
        String b = String.format("%.1f", err_mm)+"mm";
        String c = String.format("%.1f", err_per)+"%";
        String result = "Error pixel : "+a+"\nError mm : "+b+"\nError per : "+c;

        return result;
    }
}
