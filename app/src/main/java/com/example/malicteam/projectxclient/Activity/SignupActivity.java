package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Callbacks.CreateUserCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.LogInCallback;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.View.Dialogs.PictureDialogFragment;
import com.example.malicteam.projectxclient.R;


import com.example.malicteam.projectxclient.Model.Repository;

import java.util.LinkedList;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, firstName, lastName, phone;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private ImageView profilePicture;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        firstName = (EditText) findViewById(R.id.firstName_signup);
        lastName = (EditText) findViewById(R.id.lastName_signup);
        phone = (EditText) findViewById(R.id.phone_signup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        initButtons();
    }

    private void initButtons() {
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        profilePicture = (ImageView) findViewById(R.id.profilePicture_signUp);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureDialogFragment dialog = new PictureDialogFragment();
                dialog.show(getSupportFragmentManager(), "ProfilePictureDialog");
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Input to Strings
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String lName = firstName.getText().toString().trim();
                String fName = lastName.getText().toString().trim();
                String phoneNumber = phone.getText().toString().trim();
                //Verify Inputs
                if (!checkInputs(fName, lName, password, email, phoneNumber))
                    return;


                progressBar.setVisibility(View.VISIBLE);

                final String[] pictureUrl = {null};
                //Create User Object WITHOUT Profile Picture
                User newUser = new User(fName, lName, phoneNumber, email, new LinkedList<Integer>(), new LinkedList<Integer>());
                Repository.instance.createUser(newUser, password, new CreateUserCallback<Boolean>() {
                    @Override
                    public void onSuccees(Boolean data) {
                        if (data) {
                            makeToastLong("Welcome...");
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            intent.putExtra(Consts.USER, newUser);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void EmailAlreadyRegistered() {
                        makeToastLong("This Email Already Exist");
                    }

                    @Override
                    public void TechnicalError() {
                        makeToastLong("Ooops.. There Is Technical Problem, Please Try Later");
                    }
                });

                //create user
//                auth.createUserWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                progressBar.setVisibility(View.GONE);
//                                if (!task.isSuccessful()) {
//                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
//                                } else {
//                                    //Google Auth Succeed
//                                    Toast.makeText(SignupActivity.this, "Thank you for registering", Toast.LENGTH_SHORT).show();
//
//                                    if (bitmap != null) {
//                                        Repository.instance.saveProfilePicture(bitmap, email, new CloudManager.CloudManagerCallback<String>() {
//                                            @Override
//                                            public void onComplete(String url) {
//                                                User newUser;
//                                                if (url != null)
//                                                    newUser = new User(firstName.getText().toString(), lastName.getText().toString(), phone.getText().toString(), inputEmail.getText().toString(),
//                                                            new LinkedList<Integer>(), new LinkedList<Integer>(), url);
//                                                else
//                                                    newUser = new User(firstName.getText().toString(), lastName.getText().toString(), phone.getText().toString(), inputEmail.getText().toString(),
//                                                            new LinkedList<Integer>(), new LinkedList<Integer>());
//
//                                                Repository.instance.addNewUserToDB(newUser, new CloudManager.CloudManagerCallback<User>() {
//                                                    @Override
//                                                    public void onComplete(User data) {
//
//                                                        if (data == null)
//                                                            Toast.makeText(SignupActivity.this, "Action failed." + task.getException(), Toast.LENGTH_SHORT).show();
//                                                        else {
//                                                            Toast.makeText(SignupActivity.this, "Welcome!!! " + newUser.getFirstName(), Toast.LENGTH_SHORT).show();
//                                                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//                                                            intent.putExtra(Consts.USER_ID, newUser.getId());
//                                                            startActivity(intent);
//                                                            finish();
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancel() {
//                                                        Toast.makeText(SignupActivity.this, "Cancel!!! " + newUser.getFirstName(), Toast.LENGTH_SHORT).show();
//                                                        finish();
//                                                    }
//                                                });
//                                            }
//
//                                            @Override
//                                            public void onCancel() {
//                                                Toast.makeText(SignupActivity.this, "Action Cancaled." + task.getException(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    } else {
//                                        Repository.instance.addNewUserToDB(new User(firstName.getText().toString(), lastName.getText().toString(), phone.getText().toString(), inputEmail.getText().toString(),
//                                                new LinkedList<Integer>(), new LinkedList<Integer>()), new CloudManager.CloudManagerCallback<User>() {
//                                            @Override
//                                            public void onComplete(User data) {
//                                                if (data == null)
//                                                    Toast.makeText(SignupActivity.this, "Action failed." + task.getException(), Toast.LENGTH_SHORT).show();
//                                                else {
//                                                    Toast.makeText(SignupActivity.this, "Welcome!!! from //HOPAPA", Toast.LENGTH_SHORT).show();
//                                                    //HOPAPA
//                                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//                                                    intent.putExtra(Consts.USER_ID, data.getId());
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
//
//                                            }
//
//                                            @Override
//                                            public void onCancel() {
//                                                Toast.makeText(SignupActivity.this, "Cancel!!! from //HOPAPA", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//
//                                    }
//                                }
//                            }
//                        })
//                        .addOnFailureListener(SignupActivity.this, new OnFailureListener() {
//                            //Fail to add User to Firebase.Auth
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(SignupActivity.this, "Fail to sign up, try again later", Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        });
            }
        });
    }

    private void makeToastLong(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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
    }
}
