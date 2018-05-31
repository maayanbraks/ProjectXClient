package com.example.malicteam.projectxclient.View.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Consts;

import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Repository;

public class ChangeDetailsFragment extends DialogFragment {
    public interface DetailsDialogInteraction {
        void edit(String firstName, String lastName, String email, String phone);
    }
    private DetailsDialogInteraction mListener;
    private String _first = null;
    private String _last = null;
    private String _email = null;
    private String _phone = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsDialogInteraction) {
            mListener = (DetailsDialogInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DetailsDialogInteraction");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        getArgumentsAndInit();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String str = "Are you sure you want to change your ";
        if (_first != null)
            str += " First Name, ";
        if (_last != null)
            str += " Last Name";
        if (_email != null)
            str += " Email";
        if (_phone != null)
            str += " Phone Number";
        str += " ?";


        builder.setMessage(str)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.edit(_first, _last, _email, _phone);
//                        Repository.instance.changeUserDetails(_first, _last, _email, _phone, new CloudManager.CloudCallback<String>() {
//                            @Override
//                            public void onComplete(String data) {
//                                if (data != null && data != "")
//                                    Toast.makeText(getActivity().getApplicationContext(), "Your profile was changed.\n"+data+" was changed.", Toast.LENGTH_LONG).show();
//                                else
//                                    Toast.makeText(getActivity().getApplicationContext(), "There is problem", Toast.LENGTH_LONG).show();
//                                getActivity().finish();
//                            }
//
//                            @Override
//                            public void onCancel() {
//
//                            }
//                        });
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

    private void getArgumentsAndInit() {
        _first = getArguments().getString(Consts.FIRST_NAME);
        _last = getArguments().getString(Consts.LAST_NAME);
        _email = getArguments().getString(Consts.EMAIL);
        _phone = getArguments().getString(Consts.PHONE_NUMBER);
    }
}
