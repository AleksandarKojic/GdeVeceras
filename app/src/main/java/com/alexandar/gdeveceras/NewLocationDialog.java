package com.alexandar.gdeveceras;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
 * Created by Alexandar on 8/11/2016.  // TODO : Ovo je pokusaj da Activity za dodavanje nove lokacije zapravo bude u Dialog-u, ali se onda poremeti layout. Brisi ovu klasu i dialog_new_location.xml
 */
public class NewLocationDialog extends DialogFragment {

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


    public static NewLocationDialog newInstance(double latitude, double longitude){
        Bundle args = new Bundle();
        args.putDouble(LATITUDE, latitude);
        args.putDouble(LONGITUDE, longitude);
        NewLocationDialog fragment = new  NewLocationDialog();
        fragment.setArguments(args);

        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_location, null);

        mLatitude = getArguments().getDouble(LATITUDE);
        mLongitude = getArguments().getDouble(LONGITUDE);

        // TODO : napravi da lepo formatira i ispisuje ovo u stepenima, minutama i sekundama
        int latitudeInt = (int) (mLatitude*100 / 100);
        int longitudeInt = (int) (mLongitude*100 / 100);

        mLatitudeText = (TextView) view.findViewById(R.id.latitude);
        mLatitudeText.setText(Integer.toString(latitudeInt));

        mLongitudeText = (TextView) view.findViewById(R.id.longitude);
        mLongitudeText.setText(Integer.toString(longitudeInt));


        mLocationTypeSpinner = (Spinner) view.findViewById(R.id.location_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.location_type_array, android.R.layout.simple_spinner_item);
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

        mLocationNameField = (EditText) view.findViewById(R.id.locationName);
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


        mFavouriteButton = (Button) view.findViewById(R.id.favourite_button);
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

        mRatingBarField = (RatingBar) view.findViewById(R.id.ratingBar);
        mRatingBarField.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mRatingBarValue = rating;  // TODO: proveri da li ovo radi kako treba
            }
        });





        // ***** Zatvaranje tastature kada se klikne na layout, van EditText-a. Trece po redu resenje sa  : http://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
        //RelativeLayout relativeLayout =  (RelativeLayout) findViewById(R.id.activityNewLocation);
        view.findViewById(R.id.activityNewLocation).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                return true;
            }
        });






        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.selected_location)
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(mLocationNameValue != "" && mRatingBarValue != -1 && mLocationType != ""){
                            Intent i = new Intent();
                            i.putExtra(LOCATION_NAME, mLocationNameValue);
                            i.putExtra(LOCATION_TYPE, mLocationType);
                            i.putExtra(LATITUDE, mLatitude); // TODO: ovde koristim isti identifikacioni string i u Intentu kojim je startovan ovaj Activity, kao i ovde. Moze li tako?
                            i.putExtra(LONGITUDE, mLongitude);
                            i.putExtra(FAVOURITE, mClicked);
                            i.putExtra(RATING, mRatingBarValue);

                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,i);
                        } else {
                            Toast.makeText(getActivity(), "Morate prvo uneti sve vrednosti", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                    }
                });
        builder.setView(view);
        return builder.create();

    }



    // metod koji se poziva iz Fragmenta (ili) Activity-ja koji startuju ovaj Activity jer na ovaj nacin oni ne moraju da znaju key-jeve pod kojima su ubaceni extras u Intent.
    public static LocationPoint getIntentExtras(Intent result) {

        String locationName = result.getStringExtra(LOCATION_NAME);
        String locationType = result.getStringExtra(LOCATION_TYPE);
        double latitude = result.getDoubleExtra(LATITUDE, 0);
        double longitude = result.getDoubleExtra(LONGITUDE, 0);
        int favourite = result.getIntExtra(FAVOURITE, 0);
        float rating = result.getFloatExtra(RATING, 0);

        LocationPoint location = new LocationPoint();
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
