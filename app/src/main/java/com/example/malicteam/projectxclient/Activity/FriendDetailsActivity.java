package com.example.malicteam.projectxclient.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

public class FriendDetailsActivity extends AppCompatActivity {

    private UserViewModel viewModel = null;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);

        
    }
}
