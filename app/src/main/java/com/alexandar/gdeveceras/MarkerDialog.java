package com.alexandar.gdeveceras;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by Alexandar on 8/21/2016.
 */
public class MarkerDialog extends DialogFragment{

    public static final String ARG_LATLONG = "latLong";

    private TextView locationName;
    private Button addToFavourites;
    private RatingBar ratingBar;
    private Button websiteLink;
    private Button dismissButton;

    private int isFavourite = 0; // indikator da li je kliknuto na "Add to favourites"
    private float mRatingBarValue = -1; // -1 je indikator da korisnik nije dao ocenu


    public static MarkerDialog newInstance(LocationPoint latLong) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LATLONG, latLong);

        MarkerDialog fragment = new MarkerDialog();
        fragment.setArguments(args);

        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LocationPoint locationPoint = (LocationPoint)getArguments().getSerializable(ARG_LATLONG);

        //TODO : Moras kasnije napraviti tako da imaju 2 naloga, obican korisnik i Administrator koji se dobija ako platis. Ili ga imam samo ja zasad. Samo Admin moze da dodaje nova mesta.
        //TODO : Ostali korisnici mogu samo da daju ocenu, dodaju u favourites i slicno...

        //View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_marker, null);



        final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_marker);

        locationName = (TextView) dialog.findViewById(R.id.locationName);
        locationName.setText(locationPoint.getLocationName());

        addToFavourites = (Button) dialog.findViewById(R.id.addToFavourites);
        if(locationPoint.isFavourite() == 1) {
            addToFavourites.setText(R.string.remove_favourite_button);
            isFavourite = 1;
        } else {
            addToFavourites.setText(R.string.add_favourite_button);
            isFavourite = 0;
        }
        addToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // uslov je za slucaj da korisnik vise puta klikne dugme pre snimanja
                if(isFavourite == 1){
                    isFavourite = 0;
                    addToFavourites.setText(R.string.add_favourite_button);
                    // updating value in database
                    locationPoint.setFavourite(0);
                } else {
                    isFavourite = 1;
                    addToFavourites.setText(R.string.remove_favourite_button);
                    // updating value in database
                    locationPoint.setFavourite(1);
                }

            }
        });


        ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mRatingBarValue = rating;  // TODO: proveri da li ovo radi kako treba
                locationPoint.setRating(rating);

            }
        });

        websiteLink = (Button) dialog.findViewById(R.id.websiteLink);
        websiteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationPoint.getWebAdress()));
                startActivity(intent);
            }
        });

        dismissButton = (Button) dialog.findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                LocationLab.getInstance(getActivity()).updateLocation(locationPoint);
//            }
//        });

        dialog.show();



//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(view);



        // Create the AlertDialog object and return it
        return dialog;


    }
}
