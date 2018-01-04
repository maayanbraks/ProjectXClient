package com.example.malicteam.projectxclient;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class AddFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_events_list:
                intent = new Intent(this,EventListActivity.class);
                break;
            case R.id.nav_events_new:
                intent = new Intent(this,NewEventActivity.class);
                break;
            case R.id.nav_friends_add:
                intent = new Intent(this,AddFriendActivity.class);
                break;
            case R.id.nav_friends_list:
                intent = new Intent(this,FriendsListActivity.class);
                break;
            case R.id.nav_settings_account:
                intent = new Intent(this,AccountSettingsActivity.class);
                break;
            case R.id.nav_settings_event:
                intent = new Intent(this,EventSettingsActivity.class);
                break;
            default:
                intent = new Intent(this,MainActivity.class);
                finish();
                break;
        }
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}
