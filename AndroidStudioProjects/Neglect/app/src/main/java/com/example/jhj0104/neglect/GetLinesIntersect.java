package com.example.jhj0104.neglect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by jhj0104 on 2016-12-12.
 * point 사용하세요 point_2 안됨.... ㅡㅡ;;
 */

public class GetLinesIntersect extends AppCompatActivity {

    float LineLength = 1700f;
    float Width = 1920;
    float centerY = 540;
    float centerX = Width/2.0f;
    float LineMargin = 251.6f;

    float[] x = {0f,0f};
    float[] y = {0f,0f};
    float[] X = {LineMargin,(LineMargin+LineLength)};
    float[] Y = {centerY,centerY};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_decision);
    }

    public void onClick_submitXY (View view){
        EditText xx1 = (EditText) findViewById(R.id.xx1);
        EditText xx2 = (EditText) findViewById(R.id.xx2);
        EditText yy1 = (EditText) findViewById(R.id.yy1);
        EditText yy2 = (EditText) findViewById(R.id.yy2);

        x[0] = Float.parseFloat(xx1.getText().toString());
        y[0] = Float.parseFloat(yy1.getText().toString());
        x[1] = Float.parseFloat(xx2.getText().toString());
        y[1] = Float.parseFloat(xx2.getText().toString());

        TextView textView = (TextView) findViewById(R.id.intersection);
        String point = GetIntersect(x[0],y[0],x[1],y[1],X[0],Y[0],X[1],Y[1]);
        textView.setText(point);
    }

    // 교점 & 교점의 좌표 찾기
    // 참고 : http://blog.naver.com/tobsysco/90189606643
    public String GetIntersect(double p1_x, double p1_y, double p2_x, double p2_y, double p3_x, double p3_y, double p4_x, double p4_y){
        //F = float(double로 후에 변경), increase, constant, sameValue;
        double FI1 = 0, FI2 = 0, FC1 = 0, FC2 = 0, FS1 = 0, FS2 = 0;
        double x, y;

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