package com.example.malicteam.projectxclient.View.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Activity.LoginActivity;
import com.example.malicteam.projectxclient.R;

public class DataSetAlertDialogFragment extends DialogFragment {

    private DataSetAlertInteraction mListener;

    public DataSetAlertDialogFragment() {
        // Required empty public constructor
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Your Voice Recognition");
        builder.setMessage("You must to record yourself if you want that we know your voice.\n" +
                "Do you want to do it now?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        FirebaseAuth.getInstance().signOut();
                        mListener.goToDataSetActivity();
                    }
                })
                .setNegativeButton("No, Do it later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing, just cancel
                        Toast.makeText(getActivity(), "You can do it later in menu", Toast.LENGTH_LONG).show();
                        DataSetAlertDialogFragment.this.getDialog().cancel();
                    }
                });
        builder.setCancelable(false);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DataSetAlertInteraction) {
            mListener = (DataSetAlertInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DetailsDialogInteraction");
        }
    }

    public interface DataSetAlertInteraction {
        void goToDataSetActivity();
    }
}
