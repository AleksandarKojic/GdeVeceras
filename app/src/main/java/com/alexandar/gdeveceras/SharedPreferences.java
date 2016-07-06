package com.alexandar.gdeveceras;

import android.content.Context;
import android.location.*;
import android.preference.PreferenceManager;

/**
 * Created by Alexandar on 6/22/2016.
 */
public class SharedPreferences {

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";


    public static void setStoredLocation (Context context, android.location.Location location){
        String latitude = Double.toString(location.getLatitude());
        String longitude = Double.toString(location.getLongitude());
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(LATITUDE,latitude).putString(LONGITUDE, longitude).apply();
    }

    public static android.location.Location getStoredLocation (Context context){
        double latitude = Double.parseDouble( PreferenceManager.getDefaultSharedPreferences(context).getString(LATITUDE , "firstRun")); // String "firstRun" vraca ako SharedPreferences ne sadrzi nista u mapi.
        double longitude = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(context).getString(LONGITUDE, "firstRun"));
        android.location.Location lastLocation = new android.location.Location("");
        lastLocation.setLatitude(latitude);
        lastLocation.setLongitude(longitude);
        return lastLocation;
    }

    public static String checkFirstRun (Context context) {
        String check = PreferenceManager.getDefaultSharedPreferences(context).getString(LATITUDE, "firstRun");
        return check;
    }
}

