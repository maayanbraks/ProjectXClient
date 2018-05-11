package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Consts;

import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.R;
import com.google.firebase.auth.FirebaseAuth;

import com.example.malicteam.projectxclient.Model.User;

import Requests.LoginRequestData;


public class LoginActivity extends Activity {

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        //Get Firebase auth instance
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            intent.putExtra(Consts.USER_ID, id);
            startActivity(intent);
            finish();
        }

        initButtons();
    }

    private void initButtons() {
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });
    }

    private void tryLogin() {
        //get Inputs
        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        //check the inputs
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(MyApp.getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(MyApp.getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //CLOUDMANAGER //
        Repository.instance.logIn(email, password, new Repository.RepositoryCallback() {
            @Override
            public void onComplete(Object data) {
                String response = data.toString();
                switch (response) {
                    case "TechnicalError":
                        Toast.makeText(MyApp.getContext(), "Technical error,please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case "UserIsNotExist":
                        Toast.makeText(MyApp.getContext(), "Can`t find username.", Toast.LENGTH_SHORT).show();
                        break;
                    case "True":
                        //LoginRequestData lrd = new LoginRequestData();
                        Toast.makeText(MyApp.getContext(), "logging in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "False":
                        Toast.makeText(MyApp.getContext(), getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onCancel() {

            }

        });
        ////////////////////////////
        //authenticate user
//        auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressBar.setVisibility(View.GONE);
//                        if (!task.isSuccessful()) {
//                            if (password.length() < 6) {
//                                inputPassword.setError(getString(R.string.minimum_password));
//                            } else {
//                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
//                            }
//                        } else {
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
//                            intent.putExtra(Consts.USER_ID, id);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//                });
    }
}

