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

        Loop loop3 = new Loop("SLineBisection", true);
        Intent intent3 = new Intent(this,SLineBisection.class);
        intent3.putExtra("LoopData", loop3);
        startActivity(intent3);

        Loop loop2 = new Loop("CLineBisection", true);
        Intent intent2 = new Intent(this,CLineBisection.class);
        intent2.putExtra("LoopData", loop2);
        startActivity(intent2);

        Loop loop = new Loop("LineBisection", true);
        Intent intent = new Intent(this,LineBisection.class);
        intent.putExtra("LoopData", loop);
        startActivity(intent);

        finish();
    }
}
