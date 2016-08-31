package com.alexandar.gdeveceras;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by Alexandar on 8/21/2016.
 */
public class MarkerDialog extends DialogFragment{





    public static MarkerDialog newInstance() {
        Bundle args = new Bundle();
        MarkerDialog fragment = new MarkerDialog();
        fragment.setArguments(args);

        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
