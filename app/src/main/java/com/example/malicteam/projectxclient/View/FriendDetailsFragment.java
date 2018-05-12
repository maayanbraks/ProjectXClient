package com.example.malicteam.projectxclient.View;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;

import ResponsesEntitys.UserData;

public class FriendDetailsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private User user = null;
    private ImageView profilePicture;
    private Bitmap bitmap = null;


    public FriendDetailsFragment() {
        // Required empty public constructor
    }

    public static FriendDetailsFragment newInstance() {
        FriendDetailsFragment fragment = new FriendDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_details, container, false);
        profilePicture = (ImageView) view.findViewById(R.id.userPic__friendDetails);
        //User Details
        user = (User) getArguments().getSerializable(Consts.USER);
        initButtons(view);
        initDetails(user, view);
        // Inflate the layout for this fragment
        return view;
    }


    private void initButtons(View view) {
        Button deleteButton = (Button) view.findViewById(R.id.deleteFriendButton_friendDetails);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyApp.getContext());
                    builder.setTitle("Delete Friend");
                    builder.setMessage("Are you sure you wand delete " + user.getFirstName() + " " + user.getLastName() + " from your friends?");
                    builder.setPositiveButton("Yes, Delete!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Repository.instance.deleteFromFriends(user.getId(), new CloudManager.CloudCallback<Boolean>() {
                                @Override
                                public void onComplete(Boolean data) {
                                    if (data) {
                                        Toast.makeText(MyApp.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(MyApp.getContext(), "Cannot delete your friend right now, please try later...", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onCancel() {
                                    dialog.cancel();
                                }
                            });

                        }
                    })
                            .setNegativeButton("No, Cancel!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initDetails(User user, View view) {
        //if (viewModel.getUser() != null) {
        TextView firstName = (TextView) view.findViewById(R.id.firstName_friendDetails);
        TextView lastName = (TextView) view.findViewById(R.id.lastName_friendDetails);
        TextView email = (TextView) view.findViewById(R.id.email_friendDetails);
        TextView phoneNumber = (TextView) view.findViewById(R.id.phoneNumber_friendDetails);

        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());

        //Profile Picture
        Repository.instance.getProfilePicture(user.getPictureUrl(),
                new CloudManager.CloudCallback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        if (data != null) {
                            profilePicture.setImageBitmap(data);
                        } else {
                            profilePicture.setImageResource(R.drawable.outalk_logo);
                        }
                    }

                    @Override
                    public void onCancel() {
                        profilePicture.setImageResource(R.drawable.outalk_logo);
                    }
                }

        );
    }


}
