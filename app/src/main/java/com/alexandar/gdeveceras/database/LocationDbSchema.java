package com.alexandar.gdeveceras.database;

/**
 * Created by Alexandar on 6/2/2016.
 */
public class LocationDbSchema {

    public static final class LocationTable {
       public static final String NAME = "locations";

        public static final class Columns {
            public static final String UUID = "uuid";
            public static final String LAT_LONG = "lat_long";
            public static final String NAME = "name";
            public static final String LOCATION_TYPE = "location_type";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String WEB_ADRESS = "web_adress";
            public static final String FAVOURITE = "favourite";
            public static final String RATING = "rating";
        }
    }
}
