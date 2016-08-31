package com.alexandar.gdeveceras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alexandar.gdeveceras.database.LocationCursorWrapper;
import com.alexandar.gdeveceras.database.LocationDatabaseHelper;
import com.alexandar.gdeveceras.database.LocationDbSchema.LocationTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Alexandar on 5/25/2016.
 *
 * Singleton klasa zaduzena za cuvanje i operacije sa LocationPoint podacima
 * // TODO: razmisli kako da zamenis koriscenje singletona kao cache-a za Locations necim drugim. Pominju dependancy injection i slicno...
 *
 * // TODO:  dodaj opciju da moze da se klikne na mapu i unese lokacija u bazu itd...
 */
public class LocationLab {

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static LocationLab sLocationLab;



    public static LocationLab getInstance(Context context){
        if(sLocationLab == null){
            sLocationLab = new LocationLab(context);
        }
        return sLocationLab;
    }


    private LocationLab(Context context){
        mContext = context.getApplicationContext(); // Big Nerd Ranch pg. 272.

        LocationDatabaseHelper myDbHelper = new LocationDatabaseHelper(mContext);

        try {
            // check if database exists in app path, if not copy it from assets
            myDbHelper.create();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        mDatabase = myDbHelper.getWritableDatabase(); // TODO: Proveri da li na ovaj nacin sama SQLiteOpenHelper klasa kontrolise close() metod na bazi ili ja treba negde pozovem mDatabase.close(). Mozda za ovaj tip aplikacije, u signeltonu, nema razloga?
        //TODO : Vidi u "Enterprise Android" na 72. str. kako da drzis referencu na bazu u Application subklasi i pozivas sa NE-UI threada!
    }


    public List<LocationPoint> getLocations(){
        List<LocationPoint> locations = new ArrayList<>();

        LocationCursorWrapper cursor = queryLocations(null, null); // null, null, vraca sve redove iz baze
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                locations.add(cursor.getLocation());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return locations;
    }


    public LocationPoint getLocation(UUID locationID){
        LocationCursorWrapper cursor = queryLocations(LocationTable.Columns.UUID + "=?", new String[]{locationID.toString()} );

        try {
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getLocation();
        } finally {
            cursor.close();
        }

    }



    public void addLocation(LocationPoint location) {
        ContentValues values = getContentValues(location);
        mDatabase.insert(LocationTable.NAME, null, values);
    }

    // Za brisanje lokacije iz baze na osnovu UUID-a
    public void removeLocationViaUUID(LocationPoint location) {
        String uuidString = location.getLocationID().toString();
        mDatabase.delete(LocationTable.NAME, LocationTable.Columns.UUID + " =?", new String[] {uuidString});

    }

    /**
     *  Za brisanje lokacije iz baze na osnovu Latitude i Longitude tacke (brisanje klikom na marker lokacije na mapi).
     *  Mora na ovako neelegantan nacin jer ne mogu da brisem iz baze na osnovu 2 kolone za Latitude i Longitude loje vec postoje,
     *  vec samo na osnovu jedne, pa moram da je dodam ovako?
     *
     * @param latLong
     *
     */
    public void removeLocationViaLatLong(String latLong){
        mDatabase.delete(LocationTable.NAME, LocationTable.Columns.LAT_LONG + " =?", new String[] {latLong} );

    }


    public void updateLocation(LocationPoint location) {
        String uuidString = location.getLocationID().toString();
        ContentValues values = getContentValues(location);
        mDatabase.update(LocationTable.NAME, values,
                LocationTable.Columns.UUID + " = ?",
                new String[] { uuidString });
    }


    private LocationCursorWrapper queryLocations(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(  // TODO:  Da li ih odmah sve ucita i da li je ovaj query() asinhron? Vodi racuna ako se poziva na UI thread-u.Proveri velicinu Cursor-a, moze da bude ogroman za velike baze kada vrati sve redove?
                LocationTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new LocationCursorWrapper(cursor);
    }


    private ContentValues getContentValues(LocationPoint location) {  // TODO: vidi treba li ovaj metoda da bude static? U knjizi jeste.
        ContentValues values = new ContentValues();
        values.put(LocationTable.Columns.UUID, location.getLocationID().toString());
        values.put(LocationTable.Columns.NAME, location.getLocationName());
        values.put(LocationTable.Columns.LOCATION_TYPE, location.getLocationType());
        values.put(LocationTable.Columns.LAT_LONG, location.getLatLong());
        values.put(LocationTable.Columns.LATITUDE, location.getLatitude());
        values.put(LocationTable.Columns.LONGITUDE, location.getLongitude());
        values.put(LocationTable.Columns.WEB_ADRESS, location.getWebAdress());
        values.put(LocationTable.Columns.FAVOURITE, location.isFavourite());
        values.put(LocationTable.Columns.RATING, location.getRating());

        return values;
    }





}
