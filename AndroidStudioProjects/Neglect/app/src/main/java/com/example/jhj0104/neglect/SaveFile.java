package com.example.jhj0104.neglect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by jhj0104 on 2016-12-08.
 */

public class SaveFile extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    java.util.Date date = calendar.getTime();
    String Date= (new SimpleDateFormat("yyyyMMdd").format(date));
    String Time = (new SimpleDateFormat("HH:mm:ss").format(date));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        File file = new File(getApplicationContext().getFilesDir(), Date + "_" + Time);

        String dirPath = getFilesDir().getAbsolutePath();
        File file = new File(dirPath);

        // 일치하는 폴더가 없으면 생성
        if (!file.exists()) {
            file.mkdirs();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }

        // txt 파일 생성
        String testStr = "ABCDEFGHIJK...";
        File savefile = new File(dirPath + "/test.txt");
        try {
            FileOutputStream fos = new FileOutputStream(savefile);
            fos.write(testStr.getBytes());
            fos.close();
            Toast.makeText(this, "Save Success", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
        }

        // 파일이 1개 이상이면 파일 이름 출력
        if (file.listFiles().length > 0) {
            for (File f : file.listFiles()) {
                String str = f.getName();
                Log.v(null, "fileName : " + str);

                // 파일 내용 읽어오기
                String loadPath = dirPath + "/" + str;
                try {
                    FileInputStream fis = new FileInputStream(loadPath);
                    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis));

                    String content = "", temp = "";
                    while ((temp = bufferReader.readLine()) != null) {
                        content += temp;
                    }
                    Log.v(null, "" + content);
                } catch (Exception e) {
                }
            }
        }
    }
}
