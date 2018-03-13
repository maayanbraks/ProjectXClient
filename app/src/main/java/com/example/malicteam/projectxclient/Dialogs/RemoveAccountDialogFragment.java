package com.example.malicteam.projectxclient.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Repository;

public class RemoveAccountDialogFragment extends DialogFragment{

    private Activity _activity;
    //private FirebaseModel fm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setMessage("Are you sure you want be removed???\n")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Repository.instance.removeAccount(new FirebaseModel.Callback<Boolean>() {
                            @Override
                            public void onComplete(Boolean data) {
                                if(data != null)
                                    getActivity().finish();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel and Stay login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing, just cancel
                        RemoveAccountDialogFragment.this.getDialog().cancel();
                    }
                });
        builder.setCancelable(false);

        return builder.create();
    }
}
