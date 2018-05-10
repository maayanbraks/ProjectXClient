package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Consts;

import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.malicteam.projectxclient.Model.User;

import Responses.LoginResponseData;


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
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            //  intent.putExtra(Consts.USER_ID, id);
            //  startActivity(intent);
            //   finish();
        }


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
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //get Inputs
        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        //check the inputs
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        //CLOUDMANAGER ////////////////////////////////////////////]
        Repository.instance.logIn(email,password, new Repository.RepositoryCallback() {
            @Override
            public void onComplete(Object data) {
                String respone=data.toString();
                Log.d("TAG","respone="+respone);
                switch (respone)
                {
                    case "TechnicalError":
                        Log.d("TAG","In Login-->LoginActivity ---> Technical error");
                        Toast.makeText(MyApp.getContext(), "Technical error,please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case "UserIsNotExist":
                        Log.d("TAG","In Login-->LoginActivity ---> UserIsNotExist");
                        Toast.makeText(MyApp.getContext(), "Can`t find username.", Toast.LENGTH_SHORT).show();
                        break;

                    case "False":
                        Log.d("TAG","In Login-->LoginActivity ---> False");
                        Toast.makeText(getApplicationContext(), getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                        break;
                        default:
                        Log.d("TAG","Login succefull");
                        //TODO
                        // toast and then new intent
                          Toast.makeText(getApplicationContext(), "logging in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginResponseData loginResponseData= CloudManager.getObjectFromString(data.toString(),LoginResponseData.class);
                        User myuser=new User(loginResponseData.getFirstName(), loginResponseData.getLastName(), loginResponseData.getPhone(), email, null, null,1,loginResponseData.getId());
                         intent.putExtra(Consts.USER, myuser);
                        startActivity(intent);
                        finish();
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
//                            User myuser=new User("Eden","Charcon","0545587734","charcn@aa", null, null);
//
//                            intent.putExtra(Consts.USER, myuser);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//                });


    }


}