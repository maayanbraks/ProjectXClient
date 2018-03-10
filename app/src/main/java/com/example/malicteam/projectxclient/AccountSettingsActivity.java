package com.example.malicteam.projectxclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.malicteam.projectxclient.Dialogs.ChangeDetailsFragment;
import com.example.malicteam.projectxclient.Dialogs.LogoutDialogFragment;
import com.example.malicteam.projectxclient.Dialogs.RemoveAccountDialogFragment;
import com.example.malicteam.projectxclient.Dialogs.ResetPasswordDialogFragment;

import Model.FirebaseModel;
import Model.User;

public class AccountSettingsActivity extends AppCompatActivity {

    private User _currentUser = null;
    private String _msg = "";
    String _first = null;
    String _last = null;
    String _email = null;
    String _phone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        initDetails();

        Button changeDetailsAccount = (Button) findViewById(R.id.changeButton_editAccount);
        changeDetailsAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeDetailsFragment changeDialog = new ChangeDetailsFragment();
                changeDialog.setContainsActivity(AccountSettingsActivity.this);
                changeDialog.setChanges(_first, _last, _email, _phone);
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
                dialog.setContainsActivity(AccountSettingsActivity.this);
                dialog.show(getSupportFragmentManager(), "RemoveAccountDialog");
            }
        });
    }

    private void initDetails() {

        _currentUser = (User) getIntent().getSerializableExtra("user");

        if (_currentUser != null) {
            EditText firstName = (EditText) findViewById(R.id.firstName_editAccount);
            EditText lastName = (EditText) findViewById(R.id.lastName_editAccount);
            EditText email = (EditText) findViewById(R.id.email_editAccount);
            EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber_editAccount);

            firstName.setText(_currentUser.getFirstName());
            lastName.setText(_currentUser.getLastName());
            email.setText(_currentUser.getEmail());
            phoneNumber.setText(_currentUser.getPhoneNumber());

            //TODO picture

            if(firstName.getText().equals(_currentUser.getFirstName()))
                _first = firstName.getText().toString();
            if(lastName.getText().equals(_currentUser.getLastName()))
                _last = lastName.getText().toString();
            if(email.getText().equals(_currentUser.getEmail()))
                _email = email.getText().toString();
            if(phoneNumber.getText().equals(_currentUser.getPhoneNumber()))
                _phone = phoneNumber.getText().toString();
        }
    }
}
