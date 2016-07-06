package com.alexandar.gdeveceras.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alexandar.gdeveceras.database.LocationDbSchema.LocationTable;

/**
 * Created by Alexandar on 6/7/2016.
 */
public class LocationDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locationDataBase.db";

    public LocationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TODO: ovde nisi definisao tipove podataka za kolone, to SQLiteOpenHelper radi automatski, ali ipak je pozeljno da definises...
        db.execSQL("create table " + LocationTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                LocationTable.Columns.UUID + ", " +
                LocationTable.Columns.LAT_LONG + ", " +
                LocationTable.Columns.NAME + ", " +
                LocationTable.Columns.LOCATION_TYPE + ", " +
                LocationTable.Columns.LATITUDE + ", " +
                LocationTable.Columns.LONGITUDE + ", " +
                LocationTable.Columns.WEB_ADRESS + ", " +
                LocationTable.Columns.FAVOURITE + ", " +
                LocationTable.Columns.RATING +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
