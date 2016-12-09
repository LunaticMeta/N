package com.example.jhj0104.neglect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by jhj0104 on 2016-12-09.
 */

public class GetIntersectPoint extends AppCompatActivity{
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

    @Override
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
        x[1] = Float.parseFloat(xx2.getText().toString());
        y[0] = Float.parseFloat(yy1.getText().toString());
        y[1] = Float.parseFloat(yy2.getText().toString());

        boolean answer = GetIntersection(x[0], x[1], y[0], y[1]);
        boolean flag = false;

        TextView textView = (TextView) findViewById(R.id.intersection);
        textView.setText(Boolean.toString(answer));
    }

    public boolean GetIntersection (float x1, float y1, float x2, float y2){
        Intersect = true;
        x[0] = x1;
        x[1] = x2;
        y[0] = y1;
        y[1] = y2;

        double under = ((Y[1]-Y[0])*(x[1]-x[0]))-((X[1]-X[0])*(y[1]-y[0]));
        if(under == 0d) return false;
        under = Math.abs(under);

        double _t = (X[1]-X[0])*(y[0]-Y[0]) - (Y[1]-Y[0])*(x[0]-X[0]);
        double _s = (x[1]-x[0])*(y[0]-Y[0]) - (y[1]-y[0])*(x[0]-X[0]);

        t = _t/under;
        s = _s/under;
        t = Math.abs(t);
        s = Math.abs(s);

        if(t<0.0||t>1.0 ||s<0.0||s>1.0) return false;
        if(_t==0 && _s==0) return false;

        double intersectX = x[0] + t * (double)(x[1]-x[0]);
        double intersectY = y[0] + t * (double)(y[1]-y[0]);
        return true;
    }


}
