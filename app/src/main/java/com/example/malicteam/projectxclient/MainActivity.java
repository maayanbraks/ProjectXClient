package com.example.malicteam.projectxclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.LinkedList;

import Event.Model.Event;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView _navigationView;
    private LinkedList<Event> data;
    private FirebaseAuth _auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _auth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.record_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewEventActivity.class);
                startActivity(intent);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        _navigationView = (NavigationView) findViewById(R.id.nav_view);
        _navigationView.setNavigationItemSelectedListener(this);

        final ListView eventList = (ListView) findViewById(R.id._listOfEvents);
        data = new LinkedList<Event>();
        for (int i = 0; i < 10; i++) {
            Date date = new Date();

            Event event = new Event(i, "name" + i, "04/01/2018", "blabla", "maayan,eden", "bussnies talk about moneyy$$$$$$");

            data.add(event);

        }
        MyAdapter myadapter = new MyAdapter();
        eventList.setAdapter(myadapter);
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = data.get(position);
                Intent intent = new Intent(getApplicationContext(), EventDetails.class);
                intent.putExtra("sendevent", event);
                startActivity(intent);

                //Log.d("tag",event.get_nameEvent());
            }
        });
    }


    @Override
    protected void onResume() { //after creating new event.
        super.onResume();

        FirebaseUser user = _auth.getCurrentUser();
        if (user != null) {
            userLoggedIn(user);
        } else {
            noCurrentUser();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_events_list:
                intent = new Intent(this, EventListActivity.class);
                break;
            case R.id.nav_events_new:
                intent = new Intent(getApplicationContext(), NewEventActivity.class);
                break;
            case R.id.nav_friends_add:
                intent = new Intent(this, AddFriendActivity.class);
                break;
            case R.id.nav_friends_list:
                intent = new Intent(this, FriendsListActivity.class);
                break;
            case R.id.nav_settings_account:
                intent = new Intent(this, AccountSettingsActivity.class);
                break;
            case R.id.nav_settings_event:
                intent = new Intent(this, EventSettingsActivity.class);
                break;
            case R.id.nav_signup:
                intent = new Intent(this, SignupActivity.class);
                break;
            case R.id.nav_login:
                intent = new Intent(this, LoginActivity.class);
                break;

            default:
                intent = new Intent(this, MainActivity.class);
                finish();
                break;
        }
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Handle User Instance
    private void userLoggedIn(FirebaseUser user) {
        //Navigation Header
        View headerLayout = _navigationView.getHeaderView(0);
        TextView userEmail = (TextView) headerLayout.findViewById(R.id.userMail);
        TextView userName = (TextView) headerLayout.findViewById(R.id.userName);
        try {
            userEmail.setText(user.getEmail());
            userName.setText(user.getEmail().split("@")[0]);
        }
        catch (Exception e){
        }

    }
    private  void noCurrentUser(){
        //Navigation Header
        View headerLayout = _navigationView.getHeaderView(0);
        TextView userEmail = (TextView) headerLayout.findViewById(R.id.userMail);
        TextView userName = (TextView) headerLayout.findViewById(R.id.userName);
        try {
            userName.setText("Hello Guest.");
            userEmail.setText("Please Log In if you want full service");
        }
        catch (Exception e){
        }

        //Navigation User Options
    }
    //End - Handle User Instance

    //Eden Class
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.records_list_row, null);
            }

            Event event = data.get(i);

            TextView _nameEvent = view.findViewById(R.id._nameEvent);
            TextView _date = view.findViewById(R.id._date);
            TextView _participates = view.findViewById(R.id._participates);

            _nameEvent.setText(event._nameEvent);
            _date.setText(event._date);
            _participates.setText(event._participates);

            return view;
        }
    }
    //End of Eden Class
}
