package com.alexandar.gdeveceras;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alexandar on 5/4/2016.
 */
public class GdeVecerasMapFragment extends SupportMapFragment {

    private static final String TAG = "GdeVecerasMapFragment";
    private static final int MY_PERMISSIONS_REQUEST_FINE_COARSE_LOCATIONS = 1;
    public static final int REQUEST_CODE_NEW_LOCATION = 0;
    private static final String NEW_LOCATION_DIALOG = "newLocationDialog";
    private static final String DIALOG_MARKER = "dialogMarker";

    private GoogleApiClient mClient;
    public Location mCurrentLocation;
    private GoogleMap mMap;
    private float zoomLevel = 17;


    public static GdeVecerasMapFragment newMapFragmentInstance() {
        return new GdeVecerasMapFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //povezivanje na Locations API
        mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                // TODO : ovde mozda ubaci da promeni izgled onog dugmenceta za lociranje, posto to znaci da je nasao lokaciju? Ili to tek uradi kad dobijes location fix kasnije?

                // U zakomentarisani uslov ispod na Note 4 uopste i ne ulazi, verovatno zbog Androida 6 i permissionsa. Zato cuvam poslednju lokaciju u SharedPreferences-u
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
//                    mCurrentLocation = lastLocation;
//                }


                if (!SharedPreferences.checkFirstRun(getActivity()).equals("firstRun")) {
                    mCurrentLocation = SharedPreferences.getStoredLocation(getActivity());
                    lastLocationZoom();
                }

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
                        // Zakomentarisani deo ispod je pokusaj da se dodavanje nove lokacije otvori u Dialog-u, ne u Activity-ju, ali to zezne layout pa sam zasad odustao od ideje
//                        FragmentManager manager = getFragmentManager();
//                        NewLocationDialog dialog =  NewLocationDialog.newInstance(latitude, longitude);
//                        dialog.setTargetFragment(GdeVecerasMapFragment.this, REQUEST_CODE_NEW_LOCATION);
//                        dialog.show(manager, NEW_LOCATION_DIALOG);
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        LatLng markerPosition = marker.getPosition();
                        double markerLatitude = markerPosition.latitude;
                        double markerLongitude = markerPosition.longitude;
                        String markerLatLong = String.valueOf(markerLatitude) + "_" + String.valueOf(markerLongitude); // mora crtica izmedju jer je sa njom i dodajem u bazu u NewLocationActivity.java

//                        LocationLab.getInstance(getActivity()).removeLocationViaLatLong(markerLatLong);
//                        // brisanje markera. Izgleda da sam ovaj metod osvezi prikaz, ne moras da pozivas tvoj metod showLocations()
//                        marker.remove();

                        LocationPoint locationPoint = LocationLab.getInstance(getActivity()).getLocation(markerLatLong);
                        // u slucaju da ne postoji lokacija za marker koji je kliknut, radi se o nasoj trenutnoj lokaciji i to ce ispisati kao Toast
                        if(locationPoint == null) {
                            Toast.makeText(getActivity(), "This Is Your Location", Toast.LENGTH_SHORT).show();
                        } else {
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            MarkerDialog dialog = MarkerDialog.newInstance(locationPoint);
                            dialog.show(manager, DIALOG_MARKER);
                        }


                        return false; // If it returns false, then the default behavior will occur in addition to your custom behavior.
                                     // The default behavior for a marker click event is to show its info window (if available) and move the camera such that the marker is centered on the map
                    }
                });

                //updateUI();
            }
        });



    }



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
                    public void onLocationChanged(Location location) {  // Dakle ovo nije tvoj LocationPoint objekat vec Android-ov Location
                        Log.i(TAG, "Got a fix: " + location);
                        mCurrentLocation = location;
                        SharedPreferences.setStoredLocation(getActivity(), location);
                        updateUI();
                    }
                });
    }


    private void lastLocationZoom() {
        if (mMap == null || mCurrentLocation == null) {  // ne moze bez ove provere da li je mCurentLocation == null jer prvi put kad se pozove updateUI() u onCreate(), bice null i onda puca aplikacija...
            return;
        }

        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        mMap.clear();

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myPoint, 12);
        mMap.animateCamera(update);
    }


    // Metod koji zapravo crta marker samo za trenutnu lokaciju, iako je u pocetku polanirano da crta sve markere. Razmisli da promenis ime metode zbog toga.
    private void updateUI() {
        if (mMap == null || mCurrentLocation == null) {  // ne moze bez ove provere da li je mCurentLocation == null jer prvi put kad se pozove updateUI() u onCreate(), bice null i onda puca aplikacija...
            return;
        }

        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        MarkerOptions myMarker = new MarkerOptions().position(myPoint);
        //myMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_downward_black_24dp));
        myMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.clear();
        mMap.addMarker(myMarker);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(myPoint)
                .build();

        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin); // TODO : mora li ovo podesavanje margine?
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myPoint, zoomLevel);
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

            LocationPoint newLocation = NewLocationActivity.getIntentExtras(data); // ovim dobijamo novi i popunjen LocationPoint objekat
            LocationLab.getInstance(getActivity()).addLocation(newLocation);  // dodavanje nove lokacije u bazu

        }
    }

    /**
     * Use for showing all locations in database
     */
    public void showLocations(){

        List<LocationPoint> locations = LocationLab.getInstance(getActivity()).getLocations();

        if (locations.size() == 0){
            Toast.makeText(getActivity(), "Trenutno ne postoji nijedna lokacija u bazi, prvo ih unesite", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        for(LocationPoint location : locations) {
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(point);
            //marker = setMarkerIcon(marker, location.getLocationType());
            mMap.addMarker(marker);

            bounds.include(point);
        }


        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds.build(), margin);  // kljucna ideja sa "bounds.build()"
        mMap.animateCamera(update);

    }



    /**
     * Use for showing locations in radius chosen by user
     */
    public void showLocationsInCertainRadius(int radius, String locationType) {

        double currentLocationLatitude = mCurrentLocation.getLatitude();
        double currentLocationLongitude = mCurrentLocation.getLongitude();

        if(mCurrentLocation == null) {
            Toast.makeText(getActivity(), "Please first find your location", Toast.LENGTH_SHORT).show();
            return;
        }
        List<LocationPoint> locations = LocationLab.getInstance(getActivity()).getLocations();
        List<LocationPoint> filtratedLocations = new ArrayList<>();

        if (locations.size() == 0){
            Toast.makeText(getActivity(), "Trenutno ne postoji nijedna lokacija u bazi, prvo ih unesite", Toast.LENGTH_SHORT).show();
            return;
        }

        // filtriranje lokacija dodavanje u novu listu samo onih koji se nalaze unutar zadatog radijusa i zadavoljavaju uslov odabranog tipa lokacije(kafic, klub...)
        Iterator<LocationPoint> iterator = locations.iterator();
        while (iterator.hasNext()) {
            LocationPoint locationPoint = iterator.next();
            float[] results = new float[1];
            Location.distanceBetween(currentLocationLatitude, currentLocationLongitude, locationPoint.getLatitude(), locationPoint.getLongitude(), results);
            if (results[0] <= radius) {
                if(locationType.equals("Sva Mesta") ) {
                    filtratedLocations.add(locationPoint);
                } else if(locationPoint.getLocationType().equals(locationType)) {
                    filtratedLocations.add(locationPoint);
                }

            }
        }

        if (filtratedLocations.size() == 0){
            Toast.makeText(getActivity(), "Nijedna lokacija trazenog tipa se ne nalazi u zadatom radijusu", Toast.LENGTH_SHORT).show();
            mMap.clear();
            updateUI();
            return;
        }

        mMap.clear();
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        updateUI();

        for(LocationPoint location : filtratedLocations) {
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(point);
            //marker = setMarkerIcon(marker, location.getLocationType());
            mMap.addMarker(marker);

            bounds.include(point);
        }

        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds.build(), margin);  // kljucna ideja sa "bounds.build()"
        mMap.animateCamera(update);
    }


    /**
     * Method that sets appropriate marker icon, depending on place type
     * @param marker
     * @param locationType
     * @return
     */
    private MarkerOptions setMarkerIcon(MarkerOptions marker, String locationType) {

        int resourceID = 0;
        switch (locationType){
            case "Klub" : resourceID = R.drawable.club;
                break;
            case "Splav" : resourceID = R.drawable.splav;
                break;
            case "Kafana" : resourceID = R.drawable.kafana;
                break;
            case "Restoran" : resourceID = R.drawable.restaurant;
                break;
            case "Caffe" : resourceID = R.drawable.coffee;
                break;
            case "Bar" : resourceID = R.drawable.bar_coktail;
                break;
            case "Festival" : resourceID = R.drawable.festival;
                break;
            case "Fast Food" : resourceID = R.drawable.fast_food;
                break;
            default : resourceID = 0;
                break;
        }

        if(resourceID != 0) {
            marker.icon(BitmapDescriptorFactory.fromResource(resourceID));
        } else {
            return marker;
        }

        return marker;
    }


}
