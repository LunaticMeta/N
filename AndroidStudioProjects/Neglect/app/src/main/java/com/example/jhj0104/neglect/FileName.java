package com.example.jhj0104.neglect;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by jhj0104 on 2016-12-13.
 */

public class FileName {

    public static String fileName;
    public static String Date;
    public static String Time;

    public FileName(){
        Calendar calendar = Calendar.getInstance();
        java.util.Date date = calendar.getTime();
        this.Date= (new SimpleDateFormat("yyyyMMdd").format(date));
        this.Time = (new SimpleDateFormat("HHmmss").format(date));
        this.fileName = Date+"_"+Time;
    }
}