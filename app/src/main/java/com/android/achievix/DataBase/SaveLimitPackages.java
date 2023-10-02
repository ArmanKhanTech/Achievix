package com.android.achievix.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SaveLimitPackages extends SQLiteOpenHelper {

    private static final String DB_NAME = "focusOnMeDb2";
    private static final int DB_VERSION = 2;
    private static final String TABLE_NAME = "userInfo2";
    private static final String ID_COL = "id2";
    private static final String PACKAGE_COL_LIMIT="packageName2";
    private static final String DURATION="duration";

    public SaveLimitPackages(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PACKAGE_COL_LIMIT + " TEXT, "
                + DURATION + " TEXT ) ";
        db.execSQL(query);
    }

    public void addNewPackageInLimit(String packageName,String duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PACKAGE_COL_LIMIT, packageName);
        values.put(DURATION, duration);
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteLimitPack(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+PACKAGE_COL_LIMIT+"='"+value+"'");
    }

    public ArrayList<String> readLimitPacks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + PACKAGE_COL_LIMIT + " FROM " + TABLE_NAME, null);

        ArrayList<String> packs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                packs.add(cursor.getString(cursor.getColumnIndexOrThrow(PACKAGE_COL_LIMIT)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return packs;
    }

    public String readDuration(String pkgName){
        String duration="";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + DURATION + " FROM " + TABLE_NAME +
                " WHERE " + PACKAGE_COL_LIMIT + "='" + pkgName + "'", null);

        if (cursor.moveToFirst()) {
            duration=cursor.getString(cursor.getColumnIndexOrThrow(DURATION));
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
        Boolean rowExists;

        if (mCursor.moveToFirst())
        {
            rowExists = true;
        }
        else
        {
            rowExists = false;
        }
        mCursor.close();
        return rowExists;
    }
}
