package com.android.achievix.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class InternetBlockDatabase extends SQLiteOpenHelper {


    private static final String DB_NAME = "focusOnMeDb6";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "userInfo6";
    private static final String ID_COL = "id6";
    private static final String PACKAGE_INTERNET = "packageName6";
    private static final String DATA="data";

    public InternetBlockDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PACKAGE_INTERNET + " TEXT, "
                + DATA + " TEXT )";
        db.execSQL(query);
    }

    public void addNewPackageInInternet(String packageName, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PACKAGE_INTERNET, packageName);
        values.put(DATA, data);
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteInternetPack(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+PACKAGE_INTERNET+"='"+value+"'");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<String> readInternetPacks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + PACKAGE_INTERNET + " FROM " + TABLE_NAME, null);

        ArrayList<String> packs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                packs.add(cursor.getString(cursor.getColumnIndexOrThrow(PACKAGE_INTERNET)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return packs;
    }

    public String readData(String pkgName){
        String duration="";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + DATA + " FROM " + TABLE_NAME +
                " WHERE " + PACKAGE_INTERNET + "='" + pkgName + "'", null);

        if (cursor.moveToFirst()) {
            duration=cursor.getString(cursor.getColumnIndexOrThrow(DATA));
        }
        cursor.close();
        return duration;
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
