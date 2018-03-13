package com.example.malicteam.projectxclient.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Consts;

import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Repository;

public class ChangeDetailsFragment extends DialogFragment{
    String _first = null;
    String _last = null;
    String _email = null;
    String _phone = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String str = "Are you sure you want to change your ";
        if(getArguments().getString(Consts.FIRST_NAME)!=null)
            str+=getArguments().getString(Consts.FIRST_NAME);
        if(getArguments().getString(Consts.LAST_NAME)!=null)
            str+=getArguments().getString(Consts.LAST_NAME);
        if(getArguments().getString(Consts.EMAIL)!=null)
            str+=getArguments().getString(Consts.EMAIL);
        if(getArguments().getString(Consts.PHONE_NUMBER)!=null)
            str+=getArguments().getString(Consts.PHONE_NUMBER);
        str+= " ?";
        builder.setMessage(str)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Repository.instance.changeUserDetails(Consts.FIRST_NAME, Consts.LAST_NAME, Consts.EMAIL, Consts.PHONE_NUMBER, new FirebaseModel.Callback<Integer>(){
                            @Override
                            public void onComplete(Integer data) {
                                if(data!=null)
                                    Toast.makeText(getActivity(), "Your profile was changed", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getActivity(), "There is problem", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                        });
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

    public void setArgument(String first, String last, String email, String phone){
        _first = first;
        _last = last;
        _email = email;
        _phone = phone;
    }
}
