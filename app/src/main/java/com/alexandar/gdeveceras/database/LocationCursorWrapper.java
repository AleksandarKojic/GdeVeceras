package com.alexandar.gdeveceras.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.alexandar.gdeveceras.LocationPoint;
import com.alexandar.gdeveceras.database.LocationDbSchema.LocationTable;

import java.util.UUID;

/**
 * Created by Alexandar on 6/9/2016.
 */
public class LocationCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public LocationCursorWrapper(Cursor cursor) {
        super(cursor);
    }


    public LocationPoint getLocation() {
        String uuidString = getString(getColumnIndex(LocationTable.Columns.UUID));
        String latLong = getString(getColumnIndex(LocationTable.Columns.LAT_LONG));
        String locationName = getString(getColumnIndex(LocationTable.Columns.NAME));
        String locationType = getString(getColumnIndex(LocationTable.Columns.LOCATION_TYPE));
        double latitude = getDouble(getColumnIndex(LocationTable.Columns.LATITUDE));
        double longitude = getDouble(getColumnIndex(LocationTable.Columns.LONGITUDE));
        String  webAdress = getString(getColumnIndex(LocationTable.Columns.WEB_ADRESS));
        int favourite = getInt(getColumnIndex(LocationTable.Columns.FAVOURITE));
        float rating = getFloat(getColumnIndex(LocationTable.Columns.RATING));

        LocationPoint location = new LocationPoint(UUID.fromString(uuidString));
        location.setLatLong(latLong);
        location.setLocationName(locationName);
        location.setLocationType(locationType);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setWebAdress(webAdress);
        location.setFavourite(favourite);
        location.setRating(rating);

        return location;
    }


}
