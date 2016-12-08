package com.example.jhj0104.neglect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class NeglectMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
    public void onClick_startTest (View view){
        Loop loop;
        for(int i=11;i>=0;i--){
            if(i<2) loop = new Loop("LineBisection", true, i+1);
            else loop = new Loop("LineBisection", false, i-1);

            Intent intent = new Intent(getApplicationContext(),LineBisection.class);
            intent.putExtra("LoopData", loop);
            startActivity(intent);
        }
    }
    public void onClick_startExperiment (View view){
    }
}
