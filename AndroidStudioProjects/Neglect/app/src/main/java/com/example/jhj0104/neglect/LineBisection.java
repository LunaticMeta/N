package com.example.jhj0104.neglect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class LineBisection extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_bisection);
    }

    public void onClick_goNext (View view){
        String str = "";

        List<List<String>> strList = new ArrayList<List<String>>();
        strList.add(BestPaintBoard.InfoType);
        strList.add(BestPaintBoard.Repeat);
        strList.add(BestPaintBoard.Mode);
        strList.add(BestPaintBoard.X);
        strList.add(BestPaintBoard.Y);
        strList.add(BestPaintBoard.lastedX);
        strList.add(BestPaintBoard.lastedY);


        str += "-----------------NEW ONE-----------------";
        for(int i=0; i<strList.get(0).size(); i++){
            for(int j=0; j<7;j++){
                str += strList.get(j).get(i);
                if(j!=6)str+=",";
            }
            str+="; ";
        }

        Toast.makeText(getApplicationContext(), "라인 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),LineDecision.class);
        intent.putExtra("LBinfo", str);
        startActivity(intent);
    }
}
