package com.example.malicteam.projectxclient.View.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.View.BasicInteractionInterface;
import com.example.malicteam.projectxclient.ViewModel.FriendsViewModel;

import java.util.List;

public class AddFriendFragment extends DialogFragment {
    public interface AddFriendInteraction extends BasicInteractionInterface {
        void addFriend(String friendsEmail);

    }
    private AddFriendInteraction mListener;
    private final String title = "Add New Friend";
    private final String message = "Enter Email";
    private String emailString = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        final EditText input = new EditText(MyApp.getContext());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                emailString = input.getText().toString();
                mListener.addFriend(emailString);
            }
        });
        builder.setCancelable(false);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
