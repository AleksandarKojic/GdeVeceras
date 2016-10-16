package com.alexandar.gdeveceras;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Alexandar on 5/2/2016.
 */


// TODO : Razmisli da dodas i malu slicicu za svako mesto, kao sto ima u "GdeIzaci" aplikaciji. Osim ako ne ispadne prevelika instalacija tad. Razmisli kako oni dovlace fotke, sa adresa sa neta? Mozda da ubacis adrese linkova slicica na njihovim sajtovima?

// TODO : OBAVEZNO SLICICA. Ne moras nista ni da cuvas sam vec da uzmes fotku sa google street view-a za odredjenu lokaciju pa da korisnik lako moze da prepozna kako izgleda mesto s polja?

public class LocationPoint implements Serializable {
    // 2 polja koja se koriste za jedinstveni identifikaciju lokacije
    private UUID mLocationID;
    private String mLatLong; // concatenation Stringova latitude i longitude

    private String mlocationName; // naziv mesta, kluba, kafica, itd...
    private String mLocationType; // TODO : Morace da bude padajuci meni sa predefinisanim tipovima mesta.. Vidi na http://www.gdeizaci.com
    private double mLatitude;
    private double mLongitude;
    private String mWebAdress;
    private int mFavourite; // 0 - NO, 1 - YES  Mora da bude int jer izgleda u bazu ne moze da se upise boolean? Tacnije nema metod Cursor.getBoolean()?
    private float mRating;  // ocena od 1 do 10 koja moze da se da mestu

    // konstruktor koji se koristi pri kreiranju nove instance
    public LocationPoint(){
        this.mLocationID = UUID.randomUUID();
    }

    // konstruktor koji se koristi za kreiranje instance pri ucitavanju iz baze
    public LocationPoint(UUID  mLocationID) {
        this.mLocationID = mLocationID;
    }



    public UUID getLocationID() {
        return mLocationID;
    }

    public String getLatLong() {
        return mLatLong;
    }

    public void setLatLong(String latLong) {
        mLatLong = latLong;
    }


    public String getLocationName() {
        return mlocationName;
    }

    public void setLocationName(String mlocationName) {
        this.mlocationName = mlocationName;
    }

    public String getLocationType() {
        return mLocationType;
    }

    public void setLocationType(String mLocationType) {
        this.mLocationType = mLocationType;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getWebAdress() {
        return mWebAdress;
    }

    public void setWebAdress(String mWebAdress) {
        this.mWebAdress = mWebAdress;
    }

    public int isFavourite() {
        return mFavourite;
    }

    public void setFavourite(int mFavourite) {
        this.mFavourite = mFavourite;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float mRating) {
        this.mRating = mRating;
    }

}
