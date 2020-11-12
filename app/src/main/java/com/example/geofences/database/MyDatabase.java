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
    private static final String MAIN_TABLE = "MAIN_TABLE";
    private static final String ID = "GEO_FENCE_ID";
    private static final String TITLE = "TITLE";
    private static final String LATITUDE = "LATITUDE";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String RADIUS = "RADIUS";
    private static final String FORMATTED_EXPIRATION_TIME = "FORMATTED_EXPIRATION_TIME";
    private static final String GEO_FENCE_TYPE = "GEO_FENCE_TYPE";
    private static final String EXPIRATION_TIME = "EXPIRATION_TIME";
    private static final String STATE = "STATE";
    private static final String DATE = "DATE";

    // on Entered detail table
    private static final String ON_ENTERED = "ON_ENTERED";
    private static final String EN_P_ID = "P_ID";
    private static final String EN_F_ID = "F_ID";
    private static final String EN_BLUETOOTH = "BLUETOOTH";
    private static final String EN_SILENT = "SILENT";
    private static final String EN_WIFI = "WIFI";
    private static final String EN_LOCK_SCREEN = "LOCK_SCREEN";

    // on Exit detail table
    private static final String ON_EXIT = "ON_EXIT";
    private static final String EX_P_ID = "P_ID";
    private static final String EX_F_ID = "F_ID";
    private static final String EX_BLUETOOTH = "BLUETOOTH";
    private static final String EX_SILENT = "SILENT";
    private static final String EX_WIFI = "WIFI";
    private static final String EX_LOCK_SCREEN = "LOCK_SCREEN";

    //
    private static final String PER_DAY_TABLE = "PER_DAY_TABLE";
    private static final String PRIMARY_ID = "PRIMARY_ID";
    private static final String STARTED_TIME = "STARTED_TIME";


    public MyDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + MAIN_TABLE + "(GEO_FENCE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TITLE TEXT" +
                ",LATITUDE TEXT" +
                ",LONGITUDE TEXT" +
                ",RADIUS TEXT" +
                ",FORMATTED_EXPIRATION_TIME TEXT" +
                ",GEO_FENCE_TYPE TEXT" +
                ",EXPIRATION_TIME TEXT" +
                ",STATE TEXT"+
                ",DATE)");

        db.execSQL("create table " + ON_ENTERED + "(EN_P_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "F_ID TEXT" +
                ",BLUETOOTH TEXT" +
                ",SILENT TEXT" +
                ",WIFI TEXT" +
                ",LOCK_SCREEN TEXT)");
        db.execSQL("create table " + ON_EXIT + "(EX_P_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "F_ID TEXT" +
                ",BLUETOOTH TEXT" +
                ",SILENT TEXT" +
                ",WIFI TEXT" +
                ",LOCK_SCREEN TEXT)");
        db.execSQL("create table " + PER_DAY_TABLE + "(PRIMARY_ID INTEGER PRIMARY KEY," +
                "STARTED_TIME)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + MAIN_TABLE);
        db.execSQL(" DROP TABLE IF EXISTS " + ON_ENTERED);
        db.execSQL(" DROP TABLE IF EXISTS " + ON_EXIT);
        db.execSQL(" DROP TABLE IF EXISTS " + PER_DAY_TABLE);
        onCreate(db);
    }

    public long insert(String startTime) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRIMARY_ID, 0);
        values.put(STARTED_TIME, startTime);
        return database.insert(PER_DAY_TABLE, null, values);
    }
    public long insert(String title,
                       String latitude,
                       String longitude,
                       String radius,
                       String FormattedExpirationTime,
                       String geofenceType,
                       String expirationTime,
                       String stats,String date) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, title);
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        values.put(RADIUS, radius);
        values.put(FORMATTED_EXPIRATION_TIME,FormattedExpirationTime );
        values.put(GEO_FENCE_TYPE, geofenceType);
        values.put(EXPIRATION_TIME, expirationTime);
        values.put(STATE, stats);
        values.put(DATE, date);
        return database.insert(MAIN_TABLE, null, values);
    }

    public long insert(String geoType,
                       String f_key,
                       String bluetooth,
                       String silent,
                       String wifi,
                       String lockScreen) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (geoType.matches(MyAnnotations.ENTER)) {
            values.put(MyDatabase.EN_F_ID, f_key);
            values.put(MyDatabase.EN_BLUETOOTH, bluetooth);
            values.put(MyDatabase.EN_SILENT, silent);
            values.put(MyDatabase.EN_WIFI, wifi);
            values.put(MyDatabase.EN_LOCK_SCREEN, lockScreen);
            return database.insert(ON_ENTERED, null, values);
        } else
            values.put(MyDatabase.EX_F_ID, f_key);
        values.put(MyDatabase.EX_BLUETOOTH, bluetooth);
        values.put(MyDatabase.EX_SILENT, silent);
        values.put(MyDatabase.EX_WIFI, wifi);
        values.put(MyDatabase.EX_LOCK_SCREEN, lockScreen);
        return database.insert(ON_EXIT, null, values);
    }

    public int update(String id, String stats) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATE, stats);
        return database.update(MAIN_TABLE, values, MyDatabase.ID + "=?",
                new String[]{id});
    }
    public int updateDate(String id, String date) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STARTED_TIME, date);
        return database.update(PER_DAY_TABLE, values, MyDatabase.PRIMARY_ID + "=?",
                new String[]{id});
    }


    public void delete(String id) {
        SQLiteDatabase database = getWritableDatabase();
        int delete = database.delete(MAIN_TABLE, "GEO_FENCE_ID=?", new String[]{id});
//        return delete != -1;
    }

    public void delete(boolean OnEntered, String id) {
        SQLiteDatabase database = getWritableDatabase();
        if (OnEntered) {

            database.delete(ON_ENTERED, "F_ID=?", new String[]{id});
        } else
            database.delete(ON_EXIT, "F_ID=?", new String[]{id});
//        return delete != -1;
    }

    public int deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(MAIN_TABLE, null, null);
    }

    public Cursor retrieveSingleRow(String id) {
        SQLiteDatabase databaseWritable = getWritableDatabase();
        return databaseWritable.rawQuery("select * from " + MAIN_TABLE+" WHERE GEO_FENCE_ID LIKE ?", new String[]{id});
    }
    public Cursor retrieve() {
        SQLiteDatabase databaseWritable = getWritableDatabase();
        return databaseWritable.rawQuery("select * from " + MAIN_TABLE, null, null);
    }
 public Cursor retrieveDate() {
        SQLiteDatabase databaseWritable = getWritableDatabase();
        return databaseWritable.rawQuery("select * from " + PER_DAY_TABLE, null, null);
    }

    public Cursor retrieve(boolean onEntered, String id) {
        SQLiteDatabase databaseWritable = getWritableDatabase();
        if (onEntered) {
            return databaseWritable.rawQuery("select * from "
                    + ON_ENTERED
                    + " WHERE F_ID LIKE "+id,  /*new String[]{id}*/ null);

        } else
            return databaseWritable.rawQuery("select * from "
                    + ON_EXIT
                    + " WHERE F_ID LIKE "+id, /*new String[]{id}*/ null);
    }

    public long retrieveRowsAmount() {

        SQLiteDatabase databaseReadable = getReadableDatabase();

        long count = DatabaseUtils.queryNumEntries(databaseReadable, MAIN_TABLE);
        databaseReadable.close();
        return count;
    }


}
