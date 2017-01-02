package com.example.jhj0104.neglect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

/**
 * Created by jhj0104 on 2016-12-08.
 */

public class LineDecision extends AppCompatActivity{

    String myInfo_str = null;
    TextView memo;
    TextFileManager mTextFileManager = new TextFileManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_decision);

        memo = (TextView) findViewById(R.id.memoMemo);
        memo.setText(myInfo_str);
        memo.setMovementMethod(new ScrollingMovementMethod());
    }


    public LineDecision() throws FileNotFoundException {

    }

    public void onClick_load (View view){
        String log = mTextFileManager.load();
        memo.setText(log);
        Toast.makeText(this, "불러오기 완료", Toast.LENGTH_SHORT).show();
    }
    public void onClick_save (View view){

        mTextFileManager.save(myInfo_str);
        Toast.makeText(this, "Save 완료", Toast.LENGTH_SHORT).show();
    }
    public void onClick_delete (View view){
        mTextFileManager.delete();
        memo.setText("");
        Toast.makeText(this, "delete 완료", Toast.LENGTH_SHORT).show();
    }
    public void onClick_OK(View view){
        finish();
        finish();
    }

}

