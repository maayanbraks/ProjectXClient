package com.example.malicteam.projectxclient.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.malicteam.projectxclient.IActivityWithBitmap;

public class PictureDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        IActivityWithBitmap activity = (IActivityWithBitmap)getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("How do you want to get your picture?")
                .setTitle("Profile Picture")
                .setNegativeButton("Do It Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing, just cancel
                        PictureDialogFragment.this.getDialog().cancel();
                    }
                })
                .setPositiveButton("Take A Picture", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.setAction(activity.REQUEST_IMAGE_CAPTURE);


                    }
                })
                .setNeutralButton("Upload Picture", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO upload picture
                    }
                });
        builder.setCancelable(false);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
