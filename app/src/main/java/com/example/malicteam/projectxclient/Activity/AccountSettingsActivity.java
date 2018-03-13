package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.Dialogs.ChangeDetailsFragment;
import com.example.malicteam.projectxclient.Dialogs.LogoutDialogFragment;
import com.example.malicteam.projectxclient.Dialogs.PictureDialogFragment;
import com.example.malicteam.projectxclient.Dialogs.RemoveAccountDialogFragment;
import com.example.malicteam.projectxclient.Dialogs.ResetPasswordDialogFragment;
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
        profilePicture = (ImageView) findViewById(R.id.userPic_editAccount);

        Repository.instance.getProfilePicture(new Repository.GetImageListener() {
            @Override
            public void onSuccess(Bitmap image) {
                profilePicture.setImageBitmap(bitmap);
            }

            @Override
            public void onFail() {
                profilePicture.setImageResource(R.drawable.outalk_logo);
            }
        });

        userId = getIntent().getIntExtra(Consts.UID_KEY, Consts.DEFAULT_UID);

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.init(userId);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout_actionMenu) {
            Repository.instance.logout();
            startActivity(new Intent(AccountSettingsActivity.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
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
                Repository.instance.getProfilePicture(new Repository.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        profilePic.setImageBitmap(image);
                    }
                    @Override
                    public void onFail() {
                        profilePic.setImageResource(R.drawable.outalk_logo);
                    }
                });
            }else
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

                if(!newFirstName.equals(viewModel.getUser().getValue().getFirstName()))
                    newFirstName = ((EditText)findViewById(R.id.firstName_editAccount)).getText().toString();
                if(!newLastName.equals(viewModel.getUser().getValue().getLastName()))
                    newLastName = ((EditText)findViewById(R.id.lastName_editAccount)).getText().toString();
                if(!newEmail.equals(viewModel.getUser().getValue().getEmail()))
                    newEmail = ((EditText)findViewById(R.id.email_editAccount)).getText().toString();
                if(!newPhone.equals(viewModel.getUser().getValue().getPhoneNumber()))
                    newPhone = ((EditText)findViewById(R.id.phoneNumber_editAccount)).getText().toString();


                Bundle bundle = new Bundle();
                bundle.putString(Consts.FIRST_NAME, newFirstName );
                bundle.putString(Consts.FIRST_NAME, newFirstName );
                bundle.putString(Consts.FIRST_NAME, newFirstName );
                bundle.putString(Consts.FIRST_NAME, newFirstName );

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
                logoutDialog.setContainsActivity(AccountSettingsActivity.this);
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
    }


}