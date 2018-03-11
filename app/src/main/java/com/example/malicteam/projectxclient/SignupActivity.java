package com.example.malicteam.projectxclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Dialogs.PictureDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.LinkedList;

import Model.FirebaseModel;
import Model.Model;
import Model.User;

public class SignupActivity extends AppCompatActivity implements IActivityWithBitmap {

    private EditText inputEmail, inputPassword, firstName, lastName, phone;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ImageView profilePicture;
    private Bitmap bitmap = null;
    private int action;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        firstName = (EditText) findViewById(R.id.firstName_signup);
        lastName = (EditText) findViewById(R.id.lastName_signup);
        phone = (EditText) findViewById(R.id.phone_signup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        profilePicture = (ImageView) findViewById(R.id.profilePicture_signUp);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureDialogFragment dialog = new PictureDialogFragment();
                dialog.show(getSupportFragmentManager(), "ProfilePictureDialog");
                //Activity mainActivity = (MainActivity)getActivity();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser() != null) {
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "You already logged in", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String lName = firstName.getText().toString().trim();
                String fName = lastName.getText().toString().trim();
                String phoneNumber = phone.getText().toString().trim();

                if (!checkInputs(fName, lName, password, email, phoneNumber))
                    return;

                final String[] pictureUrl = {null};
                //if (bitmap != null) {
//                    FirebaseModel.saveImage(bitmap, User.generateId(email), new Model.SaveImageListener() {
//                        @Override
//                        public void complete(String url) {

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    //SUCCESS
                                    Toast.makeText(SignupActivity.this, "Thank you for registering", Toast.LENGTH_SHORT).show();

                                    if(bitmap!=null) {
                                        FirebaseModel.saveImage(bitmap, User.generateId(email), new Model.SaveImageListener() {
                                            @Override
                                            public void complete(String url)
                                            {
                                                pictureUrl[0] = url;
                                                addNewUser(pictureUrl[0]);
                                            }

                                            @Override
                                            public void fail() {
                                                addNewUser(null);
                                            }
                                        });

                                    }
                                    else{
                                        addNewUser(null);
                                    }

                                }
                            }
                        })
                        .addOnFailureListener(SignupActivity.this, new OnFailureListener() {
                            //Fail to add User to Firebase.Auth
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignupActivity.this, "Fail to sign up, try again later", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                progressBar.setVisibility(View.VISIBLE);

            }
        });
    }

    private boolean checkInputs(String fName, String lName, String password, String email, String phoneNumber) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(getApplicationContext(), "Your email address is invalid", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phoneNumber.length() != 10) {
            Toast.makeText(getApplicationContext(), "Phone number too short, enter your real phone number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fName.length() == 0) {
            Toast.makeText(getApplicationContext(), "Oops... You Forgot your first name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lName.length() == 0) {
            Toast.makeText(getApplicationContext(), "Oops... You Forgot your last name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void addNewUser(String Url) {
        if (Url != null)
            FirebaseModel.addUser(new User(firstName.getText().toString(), lastName.getText().toString(), phone.getText().toString(), inputEmail.getText().toString(), new LinkedList<>(),Url));
        else
            FirebaseModel.addUser(new User(firstName.getText().toString(), lastName.getText().toString(), phone.getText().toString(), inputEmail.getText().toString(), new LinkedList<>()));

        finish();
    }

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void setAction(int action) {
        this.action = action;
        profilePicture.setImageBitmap(bitmap);
        executePictureAction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                profilePicture.setImageBitmap(bitmap);
            }
        }
    }

    private void executePictureAction() {
        if (action == REQUEST_IMAGE_CAPTURE) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            final Calendar c = Calendar.getInstance();
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
//        else if(action == )
//        {
//            //TODO upload picture
//        }
    }
}
