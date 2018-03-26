package com.example.malicteam.projectxclient.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private TextView userNameHeader;
    private TextView userEmailHeader;
    private UserViewModel currentUser = null;
    private View headerLayout;
    protected int userId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra(Consts.USER_ID, Consts.DEFAULT_UID);

        navigationView = (NavigationView) findViewById(R.id.nav_view_base);
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        userNameHeader = (TextView) (headerLayout.findViewById(R.id.userName_head));
        userEmailHeader = (TextView) (headerLayout.findViewById(R.id.userMail_head));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
        currentUser.init(userId, true);
        currentUser.getUser().observe(this, new Observer<User>() {

            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    //update details
                    updateProfilePicture(user.getPictureUrl());
                    userNameHeader.setText(user.getFirstName() + " " + user.getLastName());
                    userEmailHeader.setText(user.getEmail());
                    userId = user.getId();
                } else {
                    finish();
                }
            }
        });



        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Consts.REQUEST_WRITE_STORAGE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    //(3 points button)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout_actionMenu) {
            Repository.instance.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_events_list:
                if (!this.getClass().getName().equals(MainActivity.class.getName())) {
                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_events_new:
                if (!this.getClass().getName().equals(NewEventActivity.class.getName())) {
                    intent = new Intent(getApplicationContext(), NewEventActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_friends_list:
                if (!this.getClass().getName().equals(FriendsListActivity.class.getName())) {
                    intent = new Intent(this, FriendsListActivity.class);
                    intent.putExtra(Consts.USER_ID, userId);
                    startActivity(intent);
                }
                break;
            case R.id.nav_settings_account:
                if (!this.getClass().getName().equals(AccountSettingsActivity.class.getName())) {
                    intent = new Intent(this, AccountSettingsActivity.class);
                    intent.putExtra(Consts.USER_ID, userId);
                    startActivity(intent);
                }
                break;
            case R.id.nav_signup:
                if (!this.getClass().getName().equals(SignupActivity.class.getName())) {
                    intent = new Intent(this, SignupActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_logout:
                Repository.instance.logout();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;

            default:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        //NO need for this. Main Activity wont be BaseClass so always finish()
        finish();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateProfilePicture(String url) {
        ImageView profilePic = (ImageView)(headerLayout.findViewById(R.id.userPic_head));
        Repository.instance.getProfilePicture(
                new FirebaseModel.FirebaseCallback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        if (data != null)
                            profilePic.setImageBitmap(data);
                        else
                            profilePic.setImageResource(R.drawable.outalk_logo);
                    }

                    @Override
                    public void onCancel() {
                        profilePic.setImageResource(R.drawable.outalk_logo);
                    }
                }

        );
    }


}
