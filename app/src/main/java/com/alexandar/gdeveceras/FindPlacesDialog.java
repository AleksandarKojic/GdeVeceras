package com.alexandar.gdeveceras;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Alexandar on 9/7/2016.
 */
public class FindPlacesDialog extends DialogFragment{

    private Spinner mLocationTypeSpinner;
    private SeekBar mLocationRadius;
    private TextView seekBarText;
    private Button dismissButton;

    private String mLocationType = null;


    public static FindPlacesDialog newInstance(){
        Bundle args = new Bundle();

        FindPlacesDialog fragment = new  FindPlacesDialog();
        fragment.setArguments(args);

        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Dialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_find_places);

        mLocationTypeSpinner = (Spinner) dialog.findViewById(R.id.spinnerLocationType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.location_type_array_alternate, R.layout.spinner_item);
        mLocationTypeSpinner.setAdapter(adapter);
        mLocationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CharSequence charSequence = (CharSequence)parent.getItemAtPosition(position);
                mLocationType = charSequence.toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLocationType = null;
            }
        });

        mLocationRadius = (SeekBar) dialog.findViewById(R.id.seekBar);
        seekBarText = (TextView) dialog.findViewById(R.id.seekBarText);
        seekBarText.setText(mLocationRadius.getProgress() +  "m" + "/" + mLocationRadius.getMax() + "m");
        mLocationRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
                seekBarText.setText(progress + "m" + "/" + seekBar.getMax() + "m");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText( getActivity(),"Odabrali ste radijus od:"+progressChanged + "m", Toast.LENGTH_SHORT).show();

                // TODO :  Ovde dodaj logiku za pretrazivanje svih mesta iz baze... Vec ih imas pokupljene u LocationLab singletonu kada pozoves "Show all locations"
                // TODO : najbolje da prebacis da se prvo poziva GdeVecerasMapFragment pa onda iz njega pozove ovaj Dialog. To da bi pokupio vrednosti za Lokaciju gde se nalazis. Ako treba

                FragmentManager manager = getActivity().getSupportFragmentManager();
                GdeVecerasMapFragment gdeVecerasMapFragment = (GdeVecerasMapFragment) manager.findFragmentById(R.id.fragment_container);
                //gdeVecerasMapFragment.findLocation();

                gdeVecerasMapFragment.showLocationsInCertainRadius(progressChanged, mLocationType);

                dialog.dismiss();


            }
        });

        dismissButton = (Button) dialog.findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });




        dialog.show();
        return dialog;

    }
}
