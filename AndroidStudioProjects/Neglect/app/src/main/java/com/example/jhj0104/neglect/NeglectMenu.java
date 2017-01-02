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
        new FileName();

        for(int i=10;i>0;i--){ //TEST
            loop = new Loop("LineBisection", false, i);
            Intent intent = new Intent(getApplicationContext(),LineBisection.class);
            intent.putExtra("LoopData", loop);
            startActivity(intent);
        }
        for(int i=2; i>0; i--){ //practice
            loop = new Loop("LineBisection", true, i);

            Intent intent = new Intent(getApplicationContext(),LineBisection.class);
            intent.putExtra("LoopData", loop);
            startActivity(intent);
        }
    }
    public void onClick_startExperiment (View view){
        return;
    }
}
