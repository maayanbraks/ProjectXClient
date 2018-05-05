package com.example.malicteam.projectxclient.View.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Activity.LoginActivity;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Repository;

public class RemoveAccountDialogFragment extends DialogFragment{

    //private FirebaseModel fm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want be removed???\n")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Repository.instance.removeAccount(new CloudManager.CloudCallback<Boolean>() {
                            @Override
                            public void onComplete(Boolean data) {
                                if(data != null) {
                                    startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
                                    Toast.makeText(getActivity().getApplicationContext(), "We will miss you :(", Toast.LENGTH_LONG).show();
                                    getActivity().finish();
                                }
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(getActivity().getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();
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
