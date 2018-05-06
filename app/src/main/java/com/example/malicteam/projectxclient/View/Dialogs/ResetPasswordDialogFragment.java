package com.example.malicteam.projectxclient.View.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordDialogFragment extends DialogFragment {

    //Activity _activity;//just for showing Toast

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("We will send you Reset Password to your mailbox./n")
                .setPositiveButton("OK?", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        reset();
                    }
                })
                .setNegativeButton("Cancel. Stay login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing, just cancel
                        getDialog().cancel();
                        Toast.makeText(getActivity(), "Canceled!", Toast.LENGTH_LONG).show();
                    }
                });
        builder.setCancelable(false);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void reset() {
        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(auth.getCurrentUser().getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MyApp.getContext(), "Check Your Email.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MyApp.getContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

//    @Override
//    public void setContainsActivity(Activity activity) {
//        _activity = activity;
//    }
}
