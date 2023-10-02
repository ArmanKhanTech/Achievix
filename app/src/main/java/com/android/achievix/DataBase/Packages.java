package com.android.achievix.DataBase;
//DONE
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Packages extends SQLiteOpenHelper {

    private static final String DB_NAME = "focusOnMeDb3";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "userInfo3";
    private static final String ID_COL = "id3";
    private static final String PACKAGE_COL_RESTRICT = "packageName3";

    public Packages(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PACKAGE_COL_RESTRICT + " TEXT ) ";
        db.execSQL(query);
    }

    public void addPackage(String packageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PACKAGE_COL_RESTRICT, packageName);
        db.insert(TABLE_NAME, null, values);
    }

    public void deletePackage(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+PACKAGE_COL_RESTRICT+"='"+value+"'");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<String> readPacks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + PACKAGE_COL_RESTRICT + " FROM " + TABLE_NAME, null);

        ArrayList<String> packs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                packs.add(cursor.getString(cursor.getColumnIndexOrThrow(PACKAGE_COL_RESTRICT)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return packs;
    }
}
