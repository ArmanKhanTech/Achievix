package com.android.achievix.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class JobDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "db7";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "taskInfo";
    private static final String ID_COL = "id";
    private static final String TASK="task";
    private static final String DATE="date";
    private static final String TIME="time";
    private static final String DESC="descc";

    public JobDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TASK + " TEXT, "
                + DESC + " TEXT, "
                + DATE + " TEXT, "
                + TIME + " TEXT ) ";
        db.execSQL(query);
    }

    public void addReminder(String task, String desc, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK, task);
        values.put(DATE, date);
        values.put(DESC, desc);
        values.put(TIME, time);
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteTask(String task)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+TASK+"='"+task+"'");
    }

    public ArrayList<String> readTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + TASK + " FROM " + TABLE_NAME, null);

        ArrayList<String> packs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                packs.add(cursor.getString(cursor.getColumnIndexOrThrow(TASK)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return packs;
    }

    public String readDate(String task){
        String duration="";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + DATE + " FROM " + TABLE_NAME +
                " WHERE " + TASK + "='" + task + "'", null);

        if (cursor.moveToFirst()) {
            duration=cursor.getString(cursor.getColumnIndexOrThrow(DATE));
        }
        cursor.close();
        return duration;
    }

    public String readDesc(String task){
        String duration="";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + DESC + " FROM " + TABLE_NAME +
                " WHERE " + TASK + "='" + task + "'", null);

        if (cursor.moveToFirst()) {
            duration=cursor.getString(cursor.getColumnIndexOrThrow(DESC));
        }
        cursor.close();
        return duration;
    }

    public String readTime(String task){
        String duration="";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + TIME + " FROM " + TABLE_NAME +
                " WHERE " + TASK + "='" + task + "'", null);

        if (cursor.moveToFirst()) {
            duration=cursor.getString(cursor.getColumnIndexOrThrow(TIME));
        }
        cursor.close();
        return duration;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean isDbEmpty(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        boolean rowExists;
        rowExists = mCursor.moveToFirst();
        mCursor.close();
        return rowExists;
    }
}
