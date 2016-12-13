package com.example.jhj0104.neglect;

import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import static android.R.attr.centerX;

/**
 * Created by jhj0104 on 2016-12-13.
 */

public class GetIntersect extends AppCompatActivity{

    DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

    float LineLength = 1700f;
    float Width = 1920;
    float centerY = 540;
    float LineMargin = 251.6f;
    boolean Intersect = true;

    float[] x = {0f,0f};
    float[] y = {0f,0f};
    float[] X = {LineMargin,(LineMargin+10f)};
    float[] Y = {centerY,centerY};

    double t;
    double s;


    public String GetIntersect2(double p1_x, double p1_y, double p2_x, double p2_y){
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

        float DPI = dm.densityDpi;
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
