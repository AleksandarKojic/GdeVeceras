package com.alexandar.gdeveceras;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends SingleFragmentActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_ERROR = 0;
    private static final String DIALOG_FIND_PLACES = "dialogFindPlaces";

    private GdeVecerasMapFragment gdeVecerasMapFragmentInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);              // TODO  *** Bitno za razumeti.  Ovaj poziv super-u ce pozvati onCreate() metod SingleFragmentActivity abstraktne klase, posto nju extendujem. Tu ce izvrsiti ucitavanje fragmenta, pa nastavlja dalje sa izvrsavanje koda ispod u ovoj onCreate() metodi.
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                gdeVecerasMapFragmentInstance.findLocation();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle); // TODO : Ako se pojavljuje neka greska, to je zato sto je ovde originalno bio deprecated method setDrawerlistener()
        toggle.syncState();  // TODO : Vidi zasto je ovde ovo uradio, u https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer tutorijalu je to stavio u onPostCreate() i onConfigurationChanged()

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.show_all_locations) {
            gdeVecerasMapFragmentInstance.showLocations();

        } else if (id == R.id.choose_location_type) {

        } else if (id == R.id.find_places_nearby) {
            if(gdeVecerasMapFragmentInstance.mCurrentLocation == null) {
                Toast.makeText(this, "Please first find your location", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                FragmentManager manager = getSupportFragmentManager();
                FindPlacesDialog dialog = FindPlacesDialog.newInstance();
                dialog.show(manager, DIALOG_FIND_PLACES);
            }


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    // *** MY METHODS ***




    @Override
    protected Fragment createFragment() {
        gdeVecerasMapFragmentInstance = GdeVecerasMapFragment.newMapFragmentInstance();
        return gdeVecerasMapFragmentInstance;
    }

    @Override
    protected void onResume() {
        super.onResume();

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);   // TODO : Vidi kako da promenis ovo posto je depricated. Vidi kako ide koncept provere dostupnost Play Servisa u training deo google materijala za Locations i Maps.
        if (errorCode != ConnectionResult.SUCCESS){
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, REQUEST_ERROR, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // Napustamo ako su Play Servisi nedostupni
                    finish(); // TODO : breakpoint pa vidi sta uradi ovaj metod
                }
            });
            errorDialog.show();
        }
    }
}
