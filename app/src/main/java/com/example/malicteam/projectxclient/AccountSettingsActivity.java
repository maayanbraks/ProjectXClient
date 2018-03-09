package com.example.malicteam.projectxclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.malicteam.projectxclient.Dialogs.LogoutDialogFragment;
import com.example.malicteam.projectxclient.Dialogs.ResetPasswordDialogFragment;

public class AccountSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Button resetPassButton = (Button) findViewById(R.id.resetPassword_editAccount_button);
        resetPassButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ResetPasswordDialogFragment dialog = new ResetPasswordDialogFragment();
                dialog.show(getSupportFragmentManager(),
                        "ResetPasswordDialog");
            }
        });
    }
}
