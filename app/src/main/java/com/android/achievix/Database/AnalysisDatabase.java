package com.android.achievix.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AnalysisDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "focusOnMeDb4";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "userInfo4";
    private static final String ID_COL = "id4";
    private static final String APP = "app";
    private static final String RESTRICTED = "restricted";
    private static final String UNRESTRICTED = "unrestricted";
    private static final String LIMITED = "limited";
    private static final String UNLIMITED = "unlimited";
    private static final String BLOCKED = "blocked";
    private static final String NOTIBLOCKED = "notiblocked";
    private static final String WEB = "web";
    private static final String WEBBLOCKED = "webblocked";
    private static final String WEBUNBLOCKED = "webunblocked";
    private static final String ACCESSDENIED = "accessdenied";

    public AnalysisDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + APP + " TEXT, "
                + RESTRICTED + " TEXT, "
                + UNRESTRICTED + " TEXT, "
                + LIMITED + " TEXT, "
                + UNLIMITED + " TEXT, "
                + BLOCKED + " TEXT, "
                + NOTIBLOCKED + " TEXT, "
                + WEB + " TEXT, "
                + WEBBLOCKED + " TEXT, "
                + WEBUNBLOCKED + " TEXT, "
                + ACCESSDENIED + " TEXT )" ;
        db.execSQL(query);
    }

    public void addApp(String packageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APP, packageName);
        values.put(RESTRICTED, "1");
        values.put(UNRESTRICTED, "0");
        values.put(LIMITED, "1");
        values.put(UNLIMITED, "0");
        values.put(BLOCKED, "0");
        values.put(NOTIBLOCKED, "0");
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteApp(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+APP+"='"+value+"'");
    }

    public void addWeb(String packageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WEB, packageName);
        values.put(WEBBLOCKED, "1");
        values.put(WEBUNBLOCKED, "0");
        values.put(ACCESSDENIED, "0");
        db.insert(TABLE_NAME, null, values);
    }

    public void deleteWeb(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME+ " WHERE "+WEB+"='"+value+"'");
    }

    public ArrayList<String> readAllApps() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + APP + " FROM " + TABLE_NAME, null);

        ArrayList<String> packs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                packs.add(cursor.getString(cursor.getColumnIndexOrThrow(APP)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return packs;
    }

    public String readWebBlocked(String webName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + WEBBLOCKED + " FROM " + TABLE_NAME + " WHERE "+WEB+"='"+webName+"'", null);
        String temp="";

        if (cursor.moveToFirst()) {
            do {
                temp=cursor.getString(cursor.getColumnIndexOrThrow(WEBBLOCKED));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return temp;
    }

    public String readWebUnlocked(String webName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + WEBUNBLOCKED + " FROM " + TABLE_NAME + " WHERE "+WEB+"='"+webName+"'", null);
        String temp="";

        if (cursor.moveToFirst()) {
            do {
                temp=cursor.getString(cursor.getColumnIndexOrThrow(WEBUNBLOCKED));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return temp;
    }

    public String readAccessDenied(String webName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + ACCESSDENIED + " FROM " + TABLE_NAME + " WHERE "+WEB+"='"+webName+"'", null);
        String temp="";

        if (cursor.moveToFirst()) {
            do {
                temp=cursor.getString(cursor.getColumnIndexOrThrow(ACCESSDENIED));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return temp;
    }

    public ArrayList<String> readAllAWeb() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + WEB + " FROM " + TABLE_NAME, null);

        ArrayList<String> packs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                packs.add(cursor.getString(cursor.getColumnIndexOrThrow(WEB)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return packs;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void inAppRestrict(String pkgName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + RESTRICTED + " FROM " + TABLE_NAME+ " WHERE "+APP+"='"+pkgName+"'",null);

        String value ="";

        if (cursor.moveToFirst()) {
            do {
                value=cursor.getString(cursor.getColumnIndexOrThrow(RESTRICTED));
            } while (cursor.moveToNext());
        }
        int temp=Integer.parseInt(value);
        temp++;
        value=String.valueOf(temp);

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + RESTRICTED + " = " + value + " WHERE " + APP + "='" + pkgName + "'" );
        cursor.close();
    }

    public void inAppLimit(String pkgName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + LIMITED + " FROM " + TABLE_NAME+ " WHERE "+APP+"='"+pkgName+"'",null);

        String value ="";

        if (cursor.moveToFirst()) {
            do {
                value=cursor.getString(cursor.getColumnIndexOrThrow(LIMITED));
            } while (cursor.moveToNext());
        }
        int temp=Integer.parseInt(value);
        temp++;
        value=String.valueOf(temp);

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + LIMITED + " = " + value + " WHERE " + APP + "='" + pkgName + "'" );
        cursor.close();
    }

    public void inAppUnrestrict(String pkgName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + UNRESTRICTED + " FROM " + TABLE_NAME+ " WHERE "+APP+"='"+pkgName+"'",null);

        String value ="";

        if (cursor.moveToFirst()) {
            do {
                value=cursor.getString(cursor.getColumnIndexOrThrow(UNRESTRICTED));
            } while (cursor.moveToNext());
        }
        int temp=Integer.parseInt(value);
        temp++;
        value=String.valueOf(temp);

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + UNRESTRICTED + " = " + value + " WHERE " + APP + "='" + pkgName + "'" );
        cursor.close();
    }

    public void inAppUnlimit(String pkgName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + UNLIMITED + " FROM " + TABLE_NAME+ " WHERE "+APP+"='"+pkgName+"'",null);

        String value ="";

        if (cursor.moveToFirst()) {
            do {
                value=cursor.getString(cursor.getColumnIndexOrThrow(UNLIMITED));
            } while (cursor.moveToNext());
        }
        int temp=Integer.parseInt(value);
        temp++;
        value=String.valueOf(temp);

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + UNLIMITED + " = " + value + " WHERE " + APP + "='" + pkgName + "'" );
        cursor.close();
    }

    public void inAppBlocked(String pkgName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + BLOCKED + " FROM " + TABLE_NAME+ " WHERE "+APP+"='"+pkgName+"'",null);

        String value ="";

        if (cursor.moveToFirst()) {
            do {
                value=cursor.getString(cursor.getColumnIndexOrThrow(BLOCKED));
            } while (cursor.moveToNext());
        }
        int temp=Integer.parseInt(value);
        temp++;
        value=String.valueOf(temp);

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + BLOCKED + " = " + value + " WHERE " + APP + "='" + pkgName + "'" );
        cursor.close();
    }

    public void inAppNotiblocked(String pkgName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + NOTIBLOCKED + " FROM " + TABLE_NAME+ " WHERE "+APP+"='"+pkgName+"'",null);

        String value ="";

        if (cursor.moveToFirst()) {
            do {
                value=cursor.getString(cursor.getColumnIndexOrThrow(NOTIBLOCKED));
            } while (cursor.moveToNext());
        }
        int temp=Integer.parseInt(value);
        temp++;
        value=String.valueOf(temp);

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + NOTIBLOCKED + " = " + value + " WHERE " + APP + "='" + pkgName + "'" );
        cursor.close();
    }

    public void inWebBlocked(String web){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + WEBBLOCKED + " FROM " + TABLE_NAME+ " WHERE "+WEB+"='"+web+"'",null);

        String value ="";

        if (cursor.moveToFirst()) {
            do {
                value=cursor.getString(cursor.getColumnIndexOrThrow(WEBBLOCKED));
            } while (cursor.moveToNext());
        }
        int temp=Integer.parseInt(value);
        temp++;
        value=String.valueOf(temp);

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + WEBBLOCKED + " = " + value + " WHERE " + WEB + "='" + web+ "'" );
        cursor.close();
    }

    public void inWebUnblocked(String web){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + WEBUNBLOCKED + " FROM " + TABLE_NAME+ " WHERE "+WEB+"='"+web+"'",null);

        String value ="";

        if (cursor.moveToFirst()) {
            do {
                value=cursor.getString(cursor.getColumnIndexOrThrow(WEBUNBLOCKED));
            } while (cursor.moveToNext());
        }
        int temp=Integer.parseInt(value);
        temp++;
        value=String.valueOf(temp);

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + WEBUNBLOCKED + " = " + value + " WHERE " + WEB + "='" + web + "'" );
        cursor.close();
    }

    public void inWebAccessDenied(String web){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + ACCESSDENIED + " FROM " + TABLE_NAME+ " WHERE "+WEB+"='"+web+"'",null);

        String value ="";

        if (cursor.moveToFirst()) {
            do {
                value=cursor.getString(cursor.getColumnIndexOrThrow(ACCESSDENIED));
            } while (cursor.moveToNext());
        }
        int temp=Integer.parseInt(value);
        temp++;
        value=String.valueOf(temp);

        db.execSQL("UPDATE " + TABLE_NAME + " SET " + ACCESSDENIED + " = " + value + " WHERE " + WEB + "='" + web + "'" );
        cursor.close();
    }

    public String readAppRestricted(String webName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + RESTRICTED + " FROM " + TABLE_NAME + " WHERE "+APP+"='"+webName+"'", null);
        String temp="";

        if (cursor.moveToFirst()) {
            do {
                temp=cursor.getString(cursor.getColumnIndexOrThrow(RESTRICTED));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return temp;
    }

    public String readAppUnrestricted(String webName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + UNRESTRICTED + " FROM " + TABLE_NAME + " WHERE "+APP+"='"+webName+"'", null);
        String temp="";

        if (cursor.moveToFirst()) {
            do {
                temp=cursor.getString(cursor.getColumnIndexOrThrow(UNRESTRICTED));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return temp;
    }

    public String readAppLimited(String webName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + LIMITED + " FROM " + TABLE_NAME + " WHERE "+APP+"='"+webName+"'", null);
        String temp="";

        if (cursor.moveToFirst()) {
            do {
                temp=cursor.getString(cursor.getColumnIndexOrThrow(LIMITED));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return temp;
    }

    public String readAppUnlimited(String webName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + UNLIMITED + " FROM " + TABLE_NAME + " WHERE "+APP+"='"+webName+"'", null);
        String temp="";

        if (cursor.moveToFirst()) {
            do {
                temp=cursor.getString(cursor.getColumnIndexOrThrow(UNLIMITED));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return temp;
    }

    public String readAppBlocked(String webName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + BLOCKED + " FROM " + TABLE_NAME + " WHERE "+APP+"='"+webName+"'", null);
        String temp="";

        if (cursor.moveToFirst()) {
            do {
                temp=cursor.getString(cursor.getColumnIndexOrThrow(BLOCKED));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return temp;
    }

    public String readAppNotiBlocked(String webName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT " + NOTIBLOCKED + " FROM " + TABLE_NAME + " WHERE "+APP+"='"+webName+"'", null);
        String temp="";

        if (cursor.moveToFirst()) {
            do {
                temp=cursor.getString(cursor.getColumnIndexOrThrow(NOTIBLOCKED));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return temp;
    }
}
