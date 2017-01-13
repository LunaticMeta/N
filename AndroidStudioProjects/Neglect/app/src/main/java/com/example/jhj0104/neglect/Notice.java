package com.example.jhj0104.neglect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.jhj0104.neglect.common.Loop;

/**
 * Created by jhj0104 on 2017-01-10.
 */

public class Notice extends AppCompatActivity {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);
    }
    public void identifyNotice(View view){
        Loop loop = new Loop("LineBisection", true);
        Intent intent = new Intent(this,LineBisection.class);
        intent.putExtra("LoopData", loop);
        startActivity(intent);
        finish();
    }
}
