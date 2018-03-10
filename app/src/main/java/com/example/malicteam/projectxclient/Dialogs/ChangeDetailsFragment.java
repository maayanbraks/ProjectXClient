package com.example.malicteam.projectxclient.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.malicteam.projectxclient.R;
import com.google.firebase.auth.FirebaseAuth;

import Model.FirebaseModel;
import Model.User;

public class ChangeDetailsFragment extends DialogFragment implements IResultsDialog{
    String _changed = "";
    Activity _activity;
    String _first = null;
    String _last = null;
    String _email = null;
    String _phone = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String str = "Are you sure you want to change your " + getArguments().getInt("changed") + "?";
        builder.setMessage(str)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(_first != null)
                            FirebaseModel.setFirstName(_first);
                        if(_last != null)
                            FirebaseModel.setLastName(_last);
                        if(_email != null)
                            FirebaseModel.setEmail(_email);
                        if(_phone != null)
                            FirebaseModel.setPhone(_phone);

                        Toast.makeText(_activity, "Your profile was changed", Toast.LENGTH_LONG).show();
                        _activity.finish();
                    }
                })
                .setNegativeButton("NO! Cancel.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing, just cancel
                        ChangeDetailsFragment.this.getDialog().cancel();
                    }
                });
        builder.setCancelable(false);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setChanges(String firstName, String lastName, String email, String phone){
        if(firstName != null) {
            _changed += " " + firstName + " ";
            _first = firstName;
        }
        if(lastName != null) {
            _changed += lastName + ", ";
            _last = lastName;
        }
        if(email != null) {
            _changed += email + ", ";
            _email = email;
        }
        if(phone != null) {
            _changed += phone + " ";
            _phone = phone;
        }
    }

    @Override
    public void setContainsActivity(Activity activity) {
        _activity = activity;
    }
}
