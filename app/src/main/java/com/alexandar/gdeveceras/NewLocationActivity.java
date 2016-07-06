package com.alexandar.gdeveceras;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Alexandar on 11/6/2016.
 *
 * Activity koji se pokrece dugim klikom na mapu i sluzi za unos podataka o novoj lokaciji.
 */

// TODO: Dodaj mozda da moze da se unese i fotka mesta. Recimo slikas ga s polja. Ili je sigurnije da se koristi slika iz google stree view-a.
public class NewLocationActivity extends AppCompatActivity {

    // konstante extra-s za Intent kojim se poziva ovaj Activity
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    //konstante za extra-s koji se stavljaju u Intent koji se vraca pozivajucem fragmentu
    private static final String LOCATION_NAME = "mLocationNameValue";
    private static final String LOCATION_TYPE = "mLocationTypeSpinner";
    private static final String FAVOURITE  = "mFavourite";   // TODO: dodaj i voo dugmence, zasad nemas.
    private static final String RATING = "mRatingBarValue";

    private String mLocationNameValue = ""; // empty string je indikator da korisnik nije uneo ime lokacije
    private String mLocationType = "";
    private double mLatitude;
    private double mLongitude;
    private float mRatingBarValue = -1; // -1 je indikator da korisnik nije dao ocenu
    private int mClicked = 0; // indikator da li je kliknuto na "Add to favourites"

    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private Spinner mLocationTypeSpinner;
    private EditText mLocationNameField;
    private Button mFavouriteButton;
    private RatingBar mRatingBarField;
    private Button mSaveButton;
    private Button mCancelButton;


    public static Intent newIntent(Context packageContext, double latitude, double longitude){
        Intent i = new Intent(packageContext, NewLocationActivity.class);
        i.putExtra(LATITUDE, latitude);
        i.putExtra(LONGITUDE, longitude);

        return i;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        // TODO : napravi da lepo formatira i ispisuje ovo u stepenima, minutama i sekundama
        mLatitude = getIntent().getDoubleExtra(LATITUDE, 0); // 0 vraca kao default value ako nema extra za dati String
        int latitudeInt = (int) (mLatitude*100 / 100);
        mLongitude = getIntent().getDoubleExtra(LONGITUDE, 0);
        int longitudeInt = (int) (mLongitude*100 / 100);

        mLatitudeText = (TextView) findViewById(R.id.latitude);
        mLatitudeText.setText(Integer.toString(latitudeInt));

        mLongitudeText = (TextView) findViewById(R.id.longitude);
        mLongitudeText.setText(Integer.toString(longitudeInt));


        mLocationTypeSpinner = (Spinner) findViewById(R.id.location_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.location_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationTypeSpinner.setAdapter(adapter);

        mLocationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CharSequence charSequence = (CharSequence)parent.getItemAtPosition(position);
                mLocationType = charSequence.toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLocationType = "";

            }
        });

        mLocationNameField = (EditText) findViewById(R.id.locationName);
        mLocationNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLocationNameValue = s.toString();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mFavouriteButton = (Button) findViewById(R.id.favourite_button);
        mFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // uslov je za slucaj da korisnik vise puta klikne dugme pre snimanja
                if(mClicked == 1){
                    mClicked = 0;
                    mFavouriteButton.setText(R.string.add_favourite_button);

                }
                mClicked = 1;
                mFavouriteButton.setText(R.string.remove_favourite_button);
            }
        });

        mRatingBarField = (RatingBar) findViewById(R.id.ratingBar);
        mRatingBarField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mRatingBarValue = rating;  // TODO: proveri da li ovo radi kako treba
            }
        });

        mSaveButton = (Button) findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLocationNameValue != "" && mRatingBarValue != -1 && mLocationType != ""){
                    Intent i = new Intent();
                    i.putExtra(LOCATION_NAME, mLocationNameValue);
                    i.putExtra(LOCATION_TYPE, mLocationType);
                    i.putExtra(LATITUDE, mLatitude); // TODO: ovde koristim isti identifikacioni string i u Intentu kojim je startovan ovaj Activity, kao i ovde. Moze li tako?
                    i.putExtra(LONGITUDE, mLongitude);
                    i.putExtra(FAVOURITE, mClicked);
                    i.putExtra(RATING, mRatingBarValue);
                    setResult(RESULT_OK, i);
                    finish(); // za povratak u pozivajuci Fragment(ili Activity)  TODO: vidi postoji li bolji nacin za ovo
                } else {
                    Toast.makeText(NewLocationActivity.this, "Morate prvo uneti sve vrednosti", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish(); // za povratak u pozjuci Fragment(ili Activity)  TODO: vidi postoji li bolji nacin za ovo
            }
        });






        // ***** Zatvaranje tastature kada se klikne na layout, van EditText-a. Trece po redu resenje sa  : http://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
        //RelativeLayout relativeLayout =  (RelativeLayout) findViewById(R.id.activityNewLocation);
        findViewById(R.id.activityNewLocation).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                return true;
            }
        });

    }


    // metod koji se poziva iz Fragmenta (ili) Activity-ja koji startuju ovaj Activity jer na ovaj nacin oni ne moraju da znaju key-jeve pod kojima su ubaceni extras u Intent.
    public static Location getIntentExtras(Intent result) {

        String locationName = result.getStringExtra(LOCATION_NAME);
        String locationType = result.getStringExtra(LOCATION_TYPE);
        double latitude = result.getDoubleExtra(LATITUDE, 0);
        double longitude = result.getDoubleExtra(LONGITUDE, 0);
        int favourite = result.getIntExtra(FAVOURITE, 0);
        float rating = result.getFloatExtra(RATING, 0);

        Location location = new Location();
        location.setLocationName(locationName);
        location.setLocationType(locationType);
        location.setLatLong(String.valueOf(latitude) + "_" + String.valueOf(longitude)); // crtica izmedju za slucaj ako zatreba split-ovanje Stringa
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setFavourite(favourite);
        location.setRating(rating);

        return location;
    }





}
