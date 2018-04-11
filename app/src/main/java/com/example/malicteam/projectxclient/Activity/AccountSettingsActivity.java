package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Dialogs.ChangeDetailsFragment;
import com.example.malicteam.projectxclient.Dialogs.LogoutDialogFragment;
import com.example.malicteam.projectxclient.Dialogs.PictureDialogFragment;
import com.example.malicteam.projectxclient.Dialogs.RemoveAccountDialogFragment;
import com.example.malicteam.projectxclient.Dialogs.ResetPasswordDialogFragment;
import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;

public class AccountSettingsActivity extends AppCompatActivity {


    private UserViewModel viewModel = null;
    private int userId;

    String newFirstName = null;
    String newLastName = null;
    String newEmail = null;
    String newPhone = null;
    private ImageView profilePicture;
    private Bitmap bitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        //setContentView(R.layout.activity_account_settings);
        profilePicture = (ImageView) findViewById(R.id.userPic_editAccount);

        Repository.instance.getProfilePicture(
                new FirebaseModel.FirebaseCallback<Bitmap>() {
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

        userId = getIntent().getIntExtra(Consts.USER_ID, Consts.DEFAULT_UID);

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.init(userId, true);
        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                initDetails(user);
            }
        });
        initButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Handle - take picture
        if (requestCode == Consts.REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                profilePicture.setImageBitmap(bitmap);
            }
        }

        //TODO Handle - upload picture
    }

    private void initDetails(User user) {
        if (viewModel.getUser() != null) {
            EditText firstName = (EditText) findViewById(R.id.firstName_editAccount);
            EditText lastName = (EditText) findViewById(R.id.lastName_editAccount);
            EditText email = (EditText) findViewById(R.id.email_editAccount);
            EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber_editAccount);

            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            email.setText(user.getEmail());
            phoneNumber.setText(user.getPhoneNumber());

            ImageView profilePic = (ImageView) findViewById(R.id.userPic_editAccount);
            if (user.getPictureUrl() != null) {
                Repository.instance.getProfilePicture(
                        new FirebaseModel.FirebaseCallback<Bitmap>() {
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
            } else
                profilePic.setImageResource(R.drawable.outalk_logo);

            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PictureDialogFragment dialog = new PictureDialogFragment();
                    dialog.show(getSupportFragmentManager(), "ProfilePictureDialog");
                }
            });
        }
    }

    private void initButtons() {
        Button changeDetailsAccount = (Button) findViewById(R.id.changeButton_editAccount);
        changeDetailsAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!((EditText) findViewById(R.id.firstName_editAccount)).getText().toString()
                        .equals(viewModel.getUser().getValue().getFirstName()))
                    newFirstName = ((EditText) findViewById(R.id.firstName_editAccount)).getText().toString();

                if (!((EditText) findViewById(R.id.lastName_editAccount)).getText().toString().equals(viewModel.getUser().getValue().getLastName()))
                    newLastName = ((EditText) findViewById(R.id.lastName_editAccount)).getText().toString();

                if (!((EditText) findViewById(R.id.email_editAccount)).getText().toString().equals(viewModel.getUser().getValue().getEmail()))
                    newEmail = ((EditText) findViewById(R.id.email_editAccount)).getText().toString();

                if (!((EditText) findViewById(R.id.phoneNumber_editAccount)).getText().toString().equals(viewModel.getUser().getValue().getPhoneNumber()))
                    newPhone = ((EditText) findViewById(R.id.phoneNumber_editAccount)).getText().toString();


                Bundle bundle = new Bundle();
                bundle.putString(Consts.FIRST_NAME, newFirstName);
                bundle.putString(Consts.LAST_NAME, newLastName);
                bundle.putString(Consts.EMAIL, newEmail);
                bundle.putString(Consts.PHONE_NUMBER, newPhone);

                ChangeDetailsFragment changeDialog = new ChangeDetailsFragment();
                changeDialog.setArguments(bundle);
                changeDialog.show(getSupportFragmentManager(), "ChangeDetailsDialog");

            }
        });

        Button signOutButton = (Button) findViewById(R.id.signOutButton_editAccount);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutDialogFragment logoutDialog = new LogoutDialogFragment();
                logoutDialog.show(getSupportFragmentManager(), "LogoutDialog");
                Intent intent = new Intent(AccountSettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button resetPassButton = (Button) findViewById(R.id.resetPassword_editAccount_button);
        resetPassButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ResetPasswordDialogFragment dialog = new ResetPasswordDialogFragment();
                dialog.show(getSupportFragmentManager(), "ResetPasswordDialog");
            }
        });

        Button deleteAccount = (Button) findViewById(R.id.deleteAccount_button);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RemoveAccountDialogFragment dialog = new RemoveAccountDialogFragment();
                dialog.show(getSupportFragmentManager(), "RemoveAccountDialog");
            }
        });


        Button changePicture = (Button) findViewById(R.id.changePictureButton_editAccount);
        changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    Repository.instance.saveProfilePicture(bitmap, viewModel.getUser().getValue().getEmail(), new FirebaseModel.FirebaseCallback<String>() {
                        @Override
                        public void onComplete(String url) {
                            if (url != null)
                                Repository.instance.setPictureUrl(bitmap, new FirebaseModel.FirebaseCallback<Boolean>() {
                                    @Override
                                    public void onComplete(Boolean data) {
                                        if (data == true) {
                                            changePicture.setClickable(false);
                                            Toast.makeText(AccountSettingsActivity.this, "Your picture uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                            else
                                Toast.makeText(AccountSettingsActivity.this, "There is problem with the picture", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "First you need take a picture.\nPress on the picture...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
