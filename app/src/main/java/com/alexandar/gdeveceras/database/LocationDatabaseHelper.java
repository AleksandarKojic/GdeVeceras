package com.alexandar.gdeveceras.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alexandar.gdeveceras.database.LocationDbSchema.LocationTable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Created by Alexandar on 6/7/2016.
 */
public class LocationDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "LocationDatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locationDataBase.db";
    private static String DATABASE_PATH;
    private static final String ASSET_DB_PATH = "databases";
    private final Context context;

    public LocationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
        DATABASE_PATH = context.getFilesDir().getParentFile().getPath() + "/databases/";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

//        // TODO: ovde nisi definisao tipove podataka za kolone, to SQLiteOpenHelper radi automatski, ali ipak je pozeljno da definises...
//        db.execSQL("create table " + LocationTable.NAME + "(" +
//                " _id integer primary key autoincrement, " +
//                LocationTable.Columns.UUID + ", " +
//                LocationTable.Columns.LAT_LONG + ", " +
//                LocationTable.Columns.NAME + ", " +
//                LocationTable.Columns.LOCATION_TYPE + ", " +
//                LocationTable.Columns.LATITUDE + ", " +
//                LocationTable.Columns.LONGITUDE + ", " +
//                LocationTable.Columns.WEB_ADRESS + ", " +
//                LocationTable.Columns.FAVOURITE + ", " +
//                LocationTable.Columns.RATING +
//                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     * */
    public void create() throws IOException {
        boolean check = checkDataBase();

        SQLiteDatabase db_Read = null;

        // Creates empty database default system path
        db_Read = this.getWritableDatabase();
        db_Read.close();
        try {
            if (!check) {
                copyDataBase();
            }
        } catch (IOException e) {
            throw new Error("Error copying database");
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            // database does't exist yet.
            Log.d(TAG, "database does't exist yet.");
        }

        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }


    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     *
     */
    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = context.getAssets().open(ASSET_DB_PATH + "/" + DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }


}
