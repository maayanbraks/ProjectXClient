package com.example.malicteam.projectxclient.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.malicteam.projectxclient.R;
import com.google.firebase.auth.FirebaseAuth;


public class LogoutDialogFragment extends DialogFragment {

    private Activity _activity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure?\n" +
                "If you logout, you cant enjoy from full service.")
                .setPositiveButton("Yes, Log me out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(_activity, getString(R.string.massage_logout), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel. Stay login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing, just cancel
                        LogoutDialogFragment.this.getDialog().cancel();
                    }
                });
        builder.setCancelable(false);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setContainsActivity(Activity activity) {
        _activity = activity;
    }
}