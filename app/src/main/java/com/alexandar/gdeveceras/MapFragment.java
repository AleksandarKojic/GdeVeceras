package com.alexandar.gdeveceras;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by Alexandar on 5/4/2016.
 */
public class MapFragment extends SupportMapFragment {

    private static final String TAG = "MapFragment";
    private static final int MY_PERMISSIONS_REQUEST_FINE_COARSE_LOCATIONS = 1;
    public static final int REQUEST_CODE_NEW_LOCATION = 0;

    private GoogleApiClient mClient;
    private android.location.Location mCurrentLocation;
    private GoogleMap mMap;


    public static MapFragment newInstance() {
        return new MapFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //povezivanje na Locations API
        mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                // TODO : ovde mozda ubaci da promeni izgled onog dugmenceta za lociranje, posto to znaci da je nasao lokaciju? Ili to tek uradi kad dobijes location fix kasnije?
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        }).build();

        //Uzimanje reference GoogleMap objekta
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        double latitude = latLng.latitude;
                        double longitude = latLng.longitude;

                        Intent i = NewLocationActivity.newIntent(getActivity(), latitude, longitude); // TODO: razmisli da li da mu prosledis ovde Activity ili Context
                        startActivityForResult(i, REQUEST_CODE_NEW_LOCATION);
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        LatLng markerPosition = marker.getPosition();
                        double markerLatitude = markerPosition.latitude;
                        double markerLongitude = markerPosition.longitude;
                        String markerLatLong = String.valueOf(markerLatitude) + "_" + String.valueOf(markerLongitude); // mora crtica izmedju jer je sa njom i dodajem u bazu u NewLocationActivity.java

                        LocationLab.getInstance(getActivity()).removeLocationViaLatLong(markerLatLong);

                        // brisanje markera. Izgleda da sam ovaj metod osvezi prikaz, ne moras da pozivas tvoj metod showLocations()
                        marker.remove();



    // TODO : Dodaj da se na klik markera otvori mali prozorcic (Dialog, vidi u knjizi), koji te pita da li si siguran da zelis da brsies tacku. OBAVEZNO da ti da i opciju da je editujes.

                        return false; // If it returns false, then the default behavior will occur in addition to your custom behavior.
                                     // The default behavior for a marker click event is to show its info window (if available) and move the camera such that the marker is centered on the map
                    }
                });

                updateUI();
            }
        });



    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // TODO: onCreateView vec overrideuje SupportMapFragment, proveri da li ovaj tvoj override kvari to?
//
//        // listener koju u slucaju dugog klika na poziciju na mapi otvara novi prozor sa LatLng te kliknute pozicije, koji sluzi za unos te nove Lokacije u bazu.
//        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                double latitude = latLng.latitude;
//                double longitude = latLng.longitude;
//
//                Intent i = NewLocationActivity.newIntent(getActivity(), latitude, longitude); // TODO: razmisli da li da mu prosledis ovde Activity ili Context
//                startActivityForResult(i, REQUEST_CODE_NEW_LOCATION);
//            }
//        });
//
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }

    @Override
    public void onStart() {
        super.onStart();

        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mClient.disconnect();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_COARSE_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    findLocation();  // TODO: Razmisli moze li ovako. Ako pri prvom prozivu findLocation() ne prodje dalje i zatrazi permission, onda dodje ovde i ako odobrimo, pozivamo je opet, ako ne onda nista.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.


                    Snackbar.make(this.getView(),"You have to give Locations permission in order to determinate your location", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
                return;
            }

        }

    }





    // *** MY METHODS ***







    public void findLocation() {
        // TODO: Na ovaj nacin nece osvezavati lokacije u realnom vremenu, vec ce korisnik svaki put kliknuti da se locira kad pozeli. Bolje tako jer se manje trosi baterija?
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // Should we show an explanation?
            // TODO: vidi da li u prvi if uslov treba da ubacis i za FINE_LOCATION i za COARSE_LOCATION
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //TODO: Vidi u Google tutorijalu kako da izbacis dijalog sa objasnjenjem na AsyncTask-u da ne blokiras UI thread
                Snackbar.make(this.getView(),"You have to give Locations permission in order to determinate your location", Snackbar.LENGTH_LONG).setAction("Action", null).show();


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_COARSE_LOCATIONS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.


            }
            return;
        }

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(android.location.Location location) {  // Dakle ovo nije tvoj Location objekat vec Android-ov
                        Log.i(TAG, "Got a fix: " + location);
                        mCurrentLocation = location;
                        updateUI();
                    }
                });
    }


    private void updateUI() {
        if (mMap == null || mCurrentLocation == null) {  // ne moze bez ove provere da li je mCurentLocation == null jer prvi put kad se pozove updateUI() u onCreate(), bice null i onda puca aplikacija...
            return;
        }

        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        MarkerOptions myMarker = new MarkerOptions().position(myPoint);
        mMap.clear();
        mMap.addMarker(myMarker);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(myPoint)
                .build();

        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin); // TODO : mora li ovo podesavanje margine?
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.animateCamera(update);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_NEW_LOCATION){
            if(data == null) {
                return;
            }

            Location newLocation = NewLocationActivity.getIntentExtras(data); // ovim dobijamo novi i popunjen Location objekat
            LocationLab.getInstance(getActivity()).addLocation(newLocation);  // dodavanje nove lokacije u bazu

        }
    }

    public void showLocations(){

        List<Location> locations = LocationLab.getInstance(getActivity()).getLocations();

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        for(Location location : locations) {
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(point);
            mMap.addMarker(marker);

            bounds.include(point);
        }


        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds.build(), margin);  // kljucna ideja sa "bounds.build()"
        mMap.animateCamera(update);

    }
}
