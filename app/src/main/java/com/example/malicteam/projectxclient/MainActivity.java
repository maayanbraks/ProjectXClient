package com.example.malicteam.projectxclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.Date;
import java.util.LinkedList;

import Model.Event;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LinkedList<Event> data;

    EventDetailsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewEventActivity.class);
                startActivity(intent);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //List From Eden

        final ListView list = (ListView) findViewById(R.id._listOfEvents);
        data = new LinkedList<Event>();
        for (int i = 0; i < 20; i++) {
            Date date=new Date();

            Event event=new Event(i,"name"+i,"04/01/2018","blabla","maayan,eden","bussnies talk about moneyy$$$$$$");

            data.add(event);

        }
        MyAdapter myadapter = new MyAdapter();
        list.setAdapter(myadapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = data.get(position);
                //Intent intent=new Intent(getApplicationContext(), EventDetails.class);
                //intent.putExtra("sendevent", event);
                //startActivity(intent);

                //Log.d("tag",event.get_nameEvent());
                addNewFragment();
            }
        });
        //END of Eden


        //Fragment - Blank
//        fragment = (EventDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details_fragment);
//        fragment.setDate("SOME VALUE FROM ACTIVITY");
//        fragment.setListener(new EventDetailsFragment.OnFragmentInteractionListener() {
//            @Override
//            public void onButtonClick() {
//                addNewFragment();
//            }
//        });
    }

    //Fragment Sample
    private void addNewFragment(){
        fragment = new EventDetailsFragment();
        fragment.setDate("SOME VALUE FROM ACTIVITY");
        fragment.setListener(new EventDetailsFragment.OnFragmentInteractionListener() {
            @Override
            public void onButtonClick() {
                addNewFragment();
            }
        });

        android.support.v4.app.FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.add(R.id.details_frame_container,fragment);

        tran.commit();
    }

    //Eden Method
    @Override
    protected void onResume() { //after creating new event.
        super.onResume();
        String nameEvent = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("desc");
        String part = getIntent().getStringExtra("part");
        if (desc != null && part != null && nameEvent != null) {
            Event event = new Event(1, nameEvent, "04/04/2018", "m", part, desc);
            data.add(event);
        }
    }
    //End of Eden Method

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


            Event event= data.get(i);

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
                intent = new Intent(this,EventListActivity.class);
                break;
            case R.id.nav_events_new:
                intent = new Intent(getApplicationContext(),NewEventActivity.class);
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
