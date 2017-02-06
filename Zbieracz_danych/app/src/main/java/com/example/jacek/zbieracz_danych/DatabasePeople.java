package com.example.jacek.zbieracz_danych;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jacek on 02.01.2017.
 */

public class DatabasePeople extends SQLiteOpenHelper {

    public static final int DATA_BASE_VERSION = 1;
    public static final String TABLE_NAME = "People";
    public static final String DATA_BASE_NAME = "peopledatabase.databasePeople";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_SURNAME = "surname";
    public static final String COL_BIRTHDAY_DATE = "birthday_date";
    public static final String COL_PHOTO_PATH = "photo_path";

    public DatabasePeople(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_PEOPLE = "CREATE TABLE " + TABLE_NAME +"("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_NAME + " TEXT,"
                + COL_SURNAME + " TEXT,"
                + COL_BIRTHDAY_DATE + " TEXT,"
                + COL_PHOTO_PATH + " TEXT);";
        db.execSQL(CREATE_TABLE_PEOPLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addPerson(String name, String surname, String birthdayDate, String photoPath){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME,name);
        values.put(COL_SURNAME,surname);
        values.put(COL_BIRTHDAY_DATE,birthdayDate);
        values.put(COL_PHOTO_PATH,photoPath);
        db.insertOrThrow(TABLE_NAME,null,values);
    }
    public Cursor getRow(){
        String[] columns = {COL_ID,COL_NAME,COL_SURNAME,COL_BIRTHDAY_DATE,COL_PHOTO_PATH};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,columns,null,null,null,null,null);
        return cursor;
    }
    public void deletePerson(int id){
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {""+id};
        db.delete(TABLE_NAME,"id=?",args);
    }
    public void updatePerson(int id, String name, String surname, String birthdayDate, String photoPath){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME,name);
        values.put(COL_SURNAME,surname);
        values.put(COL_BIRTHDAY_DATE,birthdayDate);
        values.put(COL_PHOTO_PATH,photoPath);
        String[] args = {""+id};
        db.update(TABLE_NAME,values,"id=?",args);
    }
}
