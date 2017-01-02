package com.example.jhj0104.neglect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by jhj0104 on 2016-12-07.
 */

public class NeglectCover extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
    }
    public void onClick_coverLayout(View view){
        Intent intent = new Intent(getApplicationContext(),NeglectMenu.class);
        startActivity(intent);
    }
}
