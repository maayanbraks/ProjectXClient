package com.example.malicteam.projectxclient.Dialogs;

import android.app.Activity;
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

import com.example.malicteam.projectxclient.MainActivity;
import com.example.malicteam.projectxclient.R;
import com.google.firebase.auth.FirebaseAuth;

import Model.FirebaseModel;

public class RemoveAccountDialogFragment extends DialogFragment implements IResultsDialog{

    private Activity _activity;
    //private FirebaseModel fm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setMessage("Are you sure you want be removed???\n")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseModel.removeAccount();
                        FirebaseModel.updateCurrentUser();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
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

    public void setContainsActivity(Activity activity){
        _activity = activity;
        //fm = new FirebaseModel(activity);
    }
}
