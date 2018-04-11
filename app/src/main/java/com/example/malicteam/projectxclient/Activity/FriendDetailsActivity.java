package com.example.malicteam.projectxclient.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

public class FriendDetailsActivity extends AppCompatActivity {

    private UserViewModel viewModel = null;
    private int userId;

    private ImageView profilePicture;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);
        //Profile Picture
        profilePicture = (ImageView) findViewById(R.id.userPic__friendDetails);

        ProgressBar pb = (ProgressBar)findViewById(R.id.progress_friendDetails);
        pb.setVisibility(View.VISIBLE);

        //User Details
        userId = getIntent().getIntExtra(Consts.USER_ID, Consts.DEFAULT_UID);
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.init(userId, false);
        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                pb.setVisibility(View.INVISIBLE);
                initDetails(user);
            }
        });

        Button deleteButton  =(Button)findViewById(R.id.deleteFriendButton_friendDetails);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewModel.getUser() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendDetailsActivity.this);
                    builder.setTitle("Delete Friend");
                    builder.setMessage("Are you sure you wand delete " + viewModel.getUser().getValue().getFirstName() + " " + viewModel.getUser().getValue().getLastName() + " from your friends?");
                    builder.setPositiveButton("Yes, Delete!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Repository.instance.deleteFromFriends(viewModel.getUser().getValue().getId(), new FirebaseModel.FirebaseCallback<Boolean>() {
                                @Override
                                public void onComplete(Boolean data) {
                                    if (data) {
                                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else
                                        Toast.makeText(getApplicationContext(), "Cannot delete your friend right now, please try later...", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onCancel() {
                                    dialog.cancel();
                                }
                            });

                        }
                    })
                            .setNegativeButton("No, Cancel!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                }
            }
        });
    }

    private void initDetails(User user) {
        //if (viewModel.getUser() != null) {
        TextView firstName = (TextView) findViewById(R.id.firstName_friendDetails);
        TextView lastName = (TextView) findViewById(R.id.lastName_friendDetails);
        TextView email = (TextView) findViewById(R.id.email_friendDetails);
        TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber_friendDetails);

        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());

        //Profile Picture
        Repository.instance.getProfilePicture(user.getPictureUrl(),
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
    }


}
