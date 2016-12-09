package com.example.jhj0104.neglect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class LineBisection extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_bisection);

        Intent intent = getIntent();
        Loop loop = (Loop) intent.getSerializableExtra("LoopData");
        int loopNum = loop.loopNum;
        boolean isPractice = loop.Practice;

        if(isPractice==true){
            Toast.makeText(getApplicationContext(), "연습 테스트입니다. "+(3-loopNum)+"회 남음.", Toast.LENGTH_SHORT).show();
            if(loopNum==2){
                Button goNext = (Button) findViewById(R.id.btn_goNext);
                goNext.setText("테스트 시작");
            }
        }
        else{
            if(loopNum!=10)Toast.makeText(getApplicationContext(), "테스트가 "+(11-loopNum)+"번 남았습니다", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getApplicationContext(), "마지막 테스트 입니다", Toast.LENGTH_SHORT).show();
                Button goNext = (Button) findViewById(R.id.btn_goNext);
                goNext.setText("검사 완료");
            }

            TextView practicing = (TextView) findViewById(R.id.practicing);
            practicing.setVisibility(View.INVISIBLE);
        }
    }

    public void onClick_goNext (View view){
        String str = "";

        List<List<String>> strList = new ArrayList<List<String>>();
        strList.add(DrawView.DInfoType);
        strList.add(DrawView.DRepeat);
        strList.add(DrawView.DMode);
        strList.add(DrawView.DX);
        strList.add(DrawView.DY);
        strList.add(DrawView.DlastedX);
        strList.add(DrawView.DlastedY);


        for(int i=0; i<strList.get(0).size(); i++){
            for(int j=0; j<7;j++){
                    str += strList.get(j).get(i);
                if(j!=6)str+=",";
            }
            str+=", ";
        }

        Toast.makeText(getApplicationContext(), "라인 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),LineDecision.class);
        intent.putExtra("LBinfo", str);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        //실제 테스트 시 막아놓는다.
        super.onBackPressed();
    }

}
