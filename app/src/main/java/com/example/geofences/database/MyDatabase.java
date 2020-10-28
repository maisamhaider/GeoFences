package com.example.geofences.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.geofences.annotations.MyAnnotations;

import androidx.annotation.Nullable;

public class MyDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GEO_FENCE_DB";
    private static final String TABLE_NAME_1 = "GEO_FENCES_TABLE";
    private static final String ID = "GEO_FENCE_ID";
    private static final String TITLE = "TITLE";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String RADIUS = "RADIUS";
    private static final String STATE = "STATE";


    public MyDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("Create table " + TABLE_NAME_1 + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,TITLE TEXT,LATITUDE TEXT" +
                ",LONGITUDE TEXT,RADIUS TEXT,STATE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        onCreate(db);
    }

    public long insert(String title, String latitude, String longitude, String radius, String stats) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, title);
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        values.put(RADIUS, radius);
        values.put(STATE, stats);
        return database.insert(TABLE_NAME_1, null, values);
    }

    public int update(String id, String stats) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATE, stats);
        return database.update(TABLE_NAME_1, values, MyDatabase.ID + "=?",
                new String[]{id});
    }

    public boolean delete(String id) {
        SQLiteDatabase database = getWritableDatabase();
         long delete = database.delete(TABLE_NAME_1, MyDatabase.ID + "=?", new String[]{id});
        return delete != -1;
    }

    public int deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME_1, null, null);
    }

    public Cursor retrieve() {

        SQLiteDatabase databaseWritable = getWritableDatabase();
        return databaseWritable.rawQuery("select * from " + TABLE_NAME_1, null, null);
    }

    public long retrieveRowsAmount() {

        SQLiteDatabase databaseReadable = getReadableDatabase();

        long count = DatabaseUtils.queryNumEntries(databaseReadable, TABLE_NAME_1);
        databaseReadable.close();
        return count;
    }


}
