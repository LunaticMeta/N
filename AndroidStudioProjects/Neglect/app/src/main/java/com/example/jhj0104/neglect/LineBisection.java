package com.example.jhj0104.neglect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class LineBisection extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_bisection);

    }

    public void onClick_goNext (View view){
//        Toast.makeText(getApplicationContext(), "not yet", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),LineBisection.class);
        startActivity(intent);
    }
}
