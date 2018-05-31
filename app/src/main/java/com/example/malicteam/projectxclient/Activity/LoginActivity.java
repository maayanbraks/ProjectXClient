package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Callbacks.LogInCallback;
import com.example.malicteam.projectxclient.Common.Consts;

import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.R;

import com.example.malicteam.projectxclient.Model.User;


public class LoginActivity extends Activity {

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;


    private final String DEFAULT_USER = "GalMail";
    private final String DEFAULT_PASSWORD = "B";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //Get Firebase auth instance
//        FirebaseAuth auth = FirebaseAuth.getInstance();

//        if (auth.getCurrentUser() != null) {
//            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
//            //  intent.putExtra(Consts.USER_ID, id);
//            //  startActivity(intent);
//            //   finish();
//        }
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
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//        FirebaseAuth auth = FirebaseAuth.getInstance();

        //get Inputs
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();


        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            //DEFAULT
            email = DEFAULT_USER;
            password = DEFAULT_PASSWORD;

        } else {
            //check the inputs
            if (TextUtils.isEmpty(email)) {
                // Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                makeToastLong("Enter email address!");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                //Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                makeToastLong("Enter password!");
                return;
            }
        }

        progressBar.setVisibility(View.VISIBLE);


        //CLOUD MANAGER - New Server (Sahar & Gal)
        Repository.instance.logIn(email, password, new LogInCallback<User>() {
            @Override
            public void IncorrectCredentials() {
                Log.d("TAG", "In Login-->IncorrectCredentials");
                //Toast.makeText(getApplicationContext(), getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                makeToastLong("IncorrectCredentials");
            }

            @Override
            public void onBoolean(boolean bool) {
                Log.d("TAG", "In Login-->LoginActivity ---> False");
                //Toast.makeText(getApplicationContext(), getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                makeToastLong(getString(R.string.auth_failed));
            }

            @Override
            public void technicalError() {
                Log.d("TAG", "In Login-->LoginActivity ---> Technical error");
               // Toast.makeText(getApplicationContext(), "Technical error,please try again.", Toast.LENGTH_SHORT).show();
                makeToastLong("Technical error,please try again.");
            }

            @Override
            public void userIsNotExist() {
                Log.d("TAG", "In Login-->LoginActivity ---> UserIsNotExist");
             //   Toast.makeText(getApplicationContext(), "Can`t find username.", Toast.LENGTH_SHORT).show();
                makeToastLong("Can`t find username.");
            }

            @Override
            public void login(User data) {

                makeToastLong("Login succefull");

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(Consts.USER, data);
                startActivity(intent);
                finish();
            }

            @Override
            public void UseIsAllReadyLoggedIn() {

                makeToastLong("User is allready Logged in");
                //Toast.makeText(MyApp.getContext(), "User is already loogged in.", Toast.LENGTH_SHORT).show();
            }
//            @Override
//            public void onComplete(Object data) {
//                String respone=data.toString();
//                Log.d("TAG","respone="+respone);
//                switch (respone)
//                {
//                    case "TechnicalError":
//                        Log.d("TAG","In Login-->LoginActivity ---> Technical error");
//                        Toast.makeText(MyApp.getContext(), "Technical error,please try again.", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(MyApp.getContext(), "Technical error,please try again.", Toast.LENGTH_SHORT).show();
//                        break;
//                    case "UserIsNotExist":
//                        Log.d("TAG","In Login-->LoginActivity ---> UserIsNotExist");
//                        Toast.makeText(MyApp.getContext(), "Can`t find username.", Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case "False":
//                        Log.d("TAG","In Login-->LoginActivity ---> False");
//                        Toast.makeText(getApplicationContext(), getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
//                        break;
//                        default:
//                        Log.d("TAG","Login succefull");
//                        //TODO
//                        // toast and then new intent
//                          Toast.makeText(getApplicationContext(), "logging in", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        LoginResponseData loginResponseData= CloudManager.getObjectFromString(data.toString(),LoginResponseData.class);
//                        User myuser=new User(loginResponseData.getFirstName(), loginResponseData.getLastName(), loginResponseData.getPhone(), email, null, null,1,loginResponseData.getId());
//                         intent.putExtra(Consts.USER, myuser);
//                        startActivity(intent);
//                        finish();
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
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

    private void makeToastLong(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}


