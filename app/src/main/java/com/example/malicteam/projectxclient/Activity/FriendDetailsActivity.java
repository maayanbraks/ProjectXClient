package com.example.malicteam.projectxclient.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.Dialogs.PictureDialogFragment;
import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Model;
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
                new FirebaseModel.Callback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        if (data != null) {
                            profilePicture.setImageBitmap(data);
                        } else {
                            profilePicture.setImageResource(R.drawable.outalk_logo);
                        }
                    }
                }
        );
    }
}
