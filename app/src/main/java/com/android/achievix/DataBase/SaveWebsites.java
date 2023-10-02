package com.android.achievix.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SaveWebsites extends SQLiteOpenHelper {

    private static final String DB_NAME = "focusOnMeDb5";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "userInfo5";
    private static final String ID_COL = "id5";
    private static final String WEBSITES = "website";

    public SaveWebsites(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WEBSITES + " TEXT ) ";
        db.execSQL(query);
    }

    public void addNewWebsite(String packageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WEBSITES, packageName);
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteWebsite(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+WEBSITES+"='"+value+"'");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<String> readWebsites() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + WEBSITES + " FROM " + TABLE_NAME, null);

        ArrayList<String> packs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                packs.add(cursor.getString(cursor.getColumnIndexOrThrow(WEBSITES)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return packs;
    }

    public boolean isDbEmpty(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        Boolean rowExists;

        if (mCursor.moveToFirst())
        {
            rowExists = true;

        } else
        {
            rowExists = false;
        }
        mCursor.close();
        return rowExists;
    }
}
