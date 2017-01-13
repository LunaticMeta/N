package com.example.jhj0104.neglect.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jhj0104 on 2017-01-11.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE DOCTOR_DB (_id INTEGER PRIMARY KEY AUTOINCREMENT, DOCTOR_NAME TEXT)");
        db.execSQL("CREATE TABLE PATIENT_DB (_id INTEGER PRIMARY KEY AUTOINCREMENT, PATIENT_NAME TEXT, PATIENT_BIRTH TEXT, PATIENT_GENDER TEXT, PATIENT_DOCTOR_ID TEXT)");
        db.execSQL("CREATE TABLE NEGLECT_DB (_id INTEGER PRIMARY KEY AUTOINCREMENT, PATIENT_ID TEXT, TEST_DATE TEXT)"); //DATE 에 분초까지 표시
        db.execSQL("CREATE TABLE NEGLECT_DATA_DB (_id INTEGER PRIMARY KEY AUTOINCREMENT, TEST_DATE TEXT, TEST_TYPE TEXT, DATA_TYPE TEXT, DATA_URI TEXT)");
    }

    //------------------------------ ↓↓ INSERT ↓↓ ------------------------------//
    public void insert_DOCTOR_DB(String DOCTOR_NAME ) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO DOCTOR_DB VALUES(null, '"+DOCTOR_NAME +"');");
        db.close();
    }
    public void insert_PATIENT_DB(String PATIENT_NAME, String PATIENT_BIRTH, String PATIENT_GENDER, String PATIENT_DOCTOR_ID) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO PATIENT_DB VALUES(null, '"+PATIENT_NAME+"', '"+PATIENT_BIRTH+"', '"+PATIENT_GENDER+"', '"+PATIENT_DOCTOR_ID+"');");
        db.close();
    }
    public void insert_NAGLECT_DB(String PATIENT_ID, String TEST_DATE) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO NEGLECT_DB VALUES(null, '"+PATIENT_ID+"', '"+TEST_DATE+"');");
        db.close();
    }
    public void insert_NEGLECT_DATA_DB(String TEST_DATE, int TEST_TYPE, String DATA_TYPE, String DATA_URI) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO NEGLECT_DATA_DB VALUES(null, '"+TEST_DATE+"', '"+TEST_TYPE+"', '"+DATA_TYPE+"', '"+DATA_URI+"');");
        db.close();
    }

    //------------------------------ ↓↓ DELETE ↓↓ ------------------------------//
    public void delete_ND(String ND_TITLE, String ND_TEST_NAME, String ND_TYPE, String ND_PATH) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM NEGLECT_DB VALUES WHERE ND_TITLE='"+ND_TITLE+ "'AND ND_TEST_NAME='"
                +ND_TEST_NAME+"'AND ND_TYPE='"+ND_TYPE+ "'AND ND_PATH='"+ND_PATH+"';");
        db.close();
    }

    //------------------------------ ↓↓ GET!! GET!!↓↓ ------------------------------//
    public String get_NEGLECT_LASTDATE(){
        SQLiteDatabase db = getReadableDatabase();
        String lastDate="";

        Cursor cursor = db.rawQuery("SELECT * FROM NEGLECT_DB;", null);
        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()){
                lastDate = cursor.getString(2);
            }
        }
        cursor.close();
        db.close();

        return lastDate;
    }
    public String[] get_NEGLECT_LASTDATE_URI(String lastDate){
        SQLiteDatabase db = getReadableDatabase();
        String[] lastDateURI = new String[4];

        Cursor cursor = db.rawQuery("SELECT * FROM NEGLECT_DATA_DB WHERE TEST_DATE ='"+lastDate+"';", null);
        String DataType,b;
        while (cursor.moveToNext()){
            DataType = cursor.getString(3);
            b = cursor.getString(4);
            if(DataType.equals("1")) lastDateURI[0] = cursor.getString(4);
            else if(DataType.equals("2")) lastDateURI[1] = cursor.getString(4);
            else if(DataType.equals("3")) lastDateURI[2] = cursor.getString(4);
            else if(DataType.equals("4")) lastDateURI[3] = cursor.getString(4);
        }
        return lastDateURI;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

