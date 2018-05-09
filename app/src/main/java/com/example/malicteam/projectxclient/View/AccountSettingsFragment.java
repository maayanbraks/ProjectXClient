package com.example.malicteam.projectxclient.View;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.View.Dialogs.ChangeDetailsFragment;
import com.example.malicteam.projectxclient.View.Dialogs.LogoutDialogFragment;
import com.example.malicteam.projectxclient.View.Dialogs.PictureDialogFragment;
import com.example.malicteam.projectxclient.View.Dialogs.RemoveAccountDialogFragment;
import com.example.malicteam.projectxclient.View.Dialogs.ResetPasswordDialogFragment;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

public class AccountSettingsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private UserViewModel viewModel = null;
    private int _userId;
    private String newFirstName = null;
    private String newLastName = null;
    private String newEmail = null;
    private String newPhone = null;
    private ImageView profilePicture;
    private Bitmap bitmap = null;

    public AccountSettingsFragment() {
        // Required empty public constructor
    }

    public static AccountSettingsFragment newInstance() {
        AccountSettingsFragment fragment = new AccountSettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);
        if (getArguments() != null) {
            profilePicture = (ImageView) view.findViewById(R.id.userPic_editAccount);

            this._userId = getArguments().getInt(Consts.USER_ID, Consts.DEFAULT_UID);
            viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
            //viewModel.initUser(_userId, true);
//            viewModel.getUser().observe(this, new Observer<User>() {
//                @Override
//                public void onChanged(@Nullable User user) {
//                    initDetails(user, view);
//                }
//            });
//        }
            initButtons(view);
            // Inflate the layout for this fragment
            return view;
        }
        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    /*
    * PRIVATES METHODS:
    */
    private void initDetails(User user, View view) {
        if (viewModel.getUser() != null) {
            EditText firstName = (EditText) view.findViewById(R.id.firstName_editAccount);
            EditText lastName = (EditText) view.findViewById(R.id.lastName_editAccount);
            EditText email = (EditText) view.findViewById(R.id.email_editAccount);
            EditText phoneNumber = (EditText) view.findViewById(R.id.phoneNumber_editAccount);

            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            email.setText(user.getEmail());
            phoneNumber.setText(user.getPhoneNumber());

            ImageView profilePic = (ImageView) view.findViewById(R.id.userPic_editAccount);

            if (user.getPictureUrl() != null) {
                initProfilePicture();
            } else
                profilePic.setImageResource(R.drawable.outalk_logo);
        }
    }
    private void initProfilePicture() {
        Repository.instance.getProfilePicture(
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
    private void initButtons(View view) {
        Button changeDetailsAccount = (Button) view.findViewById(R.id.changeButton_editAccount);
        changeDetailsAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!((EditText) view.findViewById(R.id.firstName_editAccount)).getText().toString()
                        .equals(viewModel.getUser().getValue().getFirstName()))
                    newFirstName = ((EditText) view.findViewById(R.id.firstName_editAccount)).getText().toString();

                if (!((EditText) view.findViewById(R.id.lastName_editAccount)).getText().toString().equals(viewModel.getUser().getValue().getLastName()))
                    newLastName = ((EditText) view.findViewById(R.id.lastName_editAccount)).getText().toString();

                if (!((EditText) view.findViewById(R.id.email_editAccount)).getText().toString().equals(viewModel.getUser().getValue().getEmail()))
                    newEmail = ((EditText) view.findViewById(R.id.email_editAccount)).getText().toString();

                if (!((EditText) view.findViewById(R.id.phoneNumber_editAccount)).getText().toString().equals(viewModel.getUser().getValue().getPhoneNumber()))
                    newPhone = ((EditText) view.findViewById(R.id.phoneNumber_editAccount)).getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString(Consts.FIRST_NAME, newFirstName);
                bundle.putString(Consts.LAST_NAME, newLastName);
                bundle.putString(Consts.EMAIL, newEmail);
                bundle.putString(Consts.PHONE_NUMBER, newPhone);
                ChangeDetailsFragment changeDialog = new ChangeDetailsFragment();
                changeDialog.setArguments(bundle);
                changeDialog.show(getActivity().getSupportFragmentManager(), "ChangeDetailsDialog");
            }
        });

        ImageView profilePic = (ImageView) view.findViewById(R.id.userPic_editAccount);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open picture dialog
                PictureDialogFragment pictureDialog = new PictureDialogFragment();
                pictureDialog.show(getActivity().getSupportFragmentManager(), "ProfilePictureDialog");
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.Layout_container, pictureDialog,"ProfilePictureDialog")
//                        .addToBackStack(null)
//                        .commit();

            }
        });

        Button signOutButton = (Button) view.findViewById(R.id.signOutButton_editAccount);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutDialogFragment logoutDialog = new LogoutDialogFragment();
//                logoutDialog.show(getActivity().getSupportFragmentManager(), "LogoutDialog");
//                Intent intent = new Intent(AccountSettingsActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.Layout_container, logoutDialog,"logoutDialog")
//                        .addToBackStack(null)
//                        .commit();

            }
        });

        Button resetPassButton = (Button) view.findViewById(R.id.resetPassword_editAccount_button);
        resetPassButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ResetPasswordDialogFragment resetPasswordDialog = new ResetPasswordDialogFragment();
                resetPasswordDialog.show(getActivity().getSupportFragmentManager(), "ResetPasswordDialog");
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.Layout_container, resetPasswordDialog,"resetPassworddialog")
//                        .addToBackStack(null)
//                        .commit();
            }
        });

        Button deleteAccount = (Button) view.findViewById(R.id.deleteAccount_button);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RemoveAccountDialogFragment removeAccountDialog = new RemoveAccountDialogFragment();
                removeAccountDialog.show(getActivity().getSupportFragmentManager(), "RemoveAccountDialog");
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.Layout_container, removeAccountDialog,"removeAccountDialog")
//                        .addToBackStack(null)
//                        .commit();
            }
        });


        Button changePicture = (Button) view.findViewById(R.id.changePictureButton_editAccount);
        changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    saveProfilePicture(view);
                } else {
                    Toast.makeText(getActivity(), "First you need take a picture.\nPress on the picture...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void saveProfilePicture(View view){
        Repository.instance.saveProfilePicture(bitmap, viewModel.getUser().getValue().getEmail(), new CloudManager.CloudCallback<String>() {
            @Override
            public void onComplete(String url) {
                if (url != null)
                    Repository.instance.setPictureUrl(bitmap, new CloudManager.CloudCallback<Boolean>() {
                        @Override
                        public void onComplete(Boolean data) {
                            if (data == true) {
                                Button changePicture = (Button) view.findViewById(R.id.changePictureButton_editAccount);
                                changePicture.setClickable(false);
                                Toast.makeText(getActivity(), "Your picture uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                else
                    Toast.makeText(getActivity(), "There is problem with the picture", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
            }
        });
    }
}
