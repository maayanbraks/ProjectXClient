package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Invite;
import com.example.malicteam.projectxclient.R;
//import com.example.malicteam.projectxclient.ViewModel.EventsViewModel;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

import java.util.LinkedList;
import java.util.List;

import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.Model.Repository;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView userNameHeader;
    private TextView userEmailHeader;
    private NavigationView navigationView;
    //    private EventsViewModel eventsData = null;
    private UserViewModel currentUser = null;
    private int userId;
    private List<Event> eventsList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView eventListView = (ListView) findViewById(R.id._listOfEvents);
        EventAdapter adapter = new EventAdapter();
        eventListView.setAdapter(adapter);

        userId = getIntent().getIntExtra(Consts.UID_KEY, Consts.DEFAULT_UID);

        currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
        currentUser.init(userId);
//        eventsData = ViewModelProviders.of(this).get(EventsViewModel.class);
//        eventsData.init(userId);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        userNameHeader = (TextView) (headerLayout.findViewById(R.id.userName_head));
        userEmailHeader = (TextView) (headerLayout.findViewById(R.id.userMail_head));

        currentUser.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    //update details
                    updateProfilePicture(user.getPictureUrl());
                    try {
                        userNameHeader.setText(user.getFirstName() + " " + user.getLastName());
                        userEmailHeader.setText(user.getEmail());
                    } catch (Exception e) {
                        Log.d("tag", e.getMessage());
                    }
                    userId = user.getId();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

//        eventsData.getEvents().observe(this, new Observer<List<Event>>() {
//            @Override
//            public void onChanged(@Nullable List<Event> events) {
//                eventsList = events;
//                if (adapter != null) adapter.notifyDataSetChanged();
//            }
//        });

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Consts.REQUEST_WRITE_STORAGE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            /////////
//////////////////////////////// invitation code -
        Repository.instance.getInvite("" + userId, new FirebaseModel.Callback<Invite>() {
            @Override
            public void onComplete(Invite invite) {
                Invitation(invite);
            }
        }, new FirebaseModel.GetInvitation() {
            @Override
            public void onComplete(Invite invite) {
                Invitation(invite);
            }
        });
        ///////////////
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.record_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewEventActivity.class);
                int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                intent.putExtra(Consts.UID_KEY, id);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = eventsList.get(position);
                Intent intent = new Intent(getApplicationContext(), EventDetails.class);
                intent.putExtra(Consts.SEND_EVENT, event);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() { //after creating new event.
        super.onResume();
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
        if (id == R.id.logout_actionMenu) {
            Repository.instance.logout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
                    intent.putExtra(Consts.UID_KEY, userId);
                    startActivity(intent);
                }
                break;
            case R.id.nav_settings_account:
                if (!this.getClass().getName().equals(AccountSettingsActivity.class.getName())) {
                    intent = new Intent(this, AccountSettingsActivity.class);
                    intent.putExtra(Consts.UID_KEY, userId);
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

        if (this.getClass() != MainActivity.class)
            finish();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateProfilePicture(String url) {
        ImageView profilePic = findViewById(R.id.userPic_head);
        Repository.instance.getProfilePicture(
                new FirebaseModel.Callback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        if (data != null)
                            profilePic.setImageBitmap(data);
                        else
                            profilePic.setImageResource(R.drawable.outalk_logo);
                    }
                }

        );
    }


    //Events List adapter
    class EventAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return eventsList.size();
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
            if (i < eventsList.size()) {
                Event event = eventsList.get(i);

                TextView _nameEvent = view.findViewById(R.id._nameEvent);
                TextView _date = view.findViewById(R.id._date);

                _nameEvent.setText(event.getTitle());
                _date.setText(event.getDate());
            }

            return view;
        }
    }
    public void Invitation(final Invite invite) {
        final Context context = this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("You got new Invitation, from "+invite.getInviteFromId());

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Decline",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        declineToInvite(invite);
                        //Todo make delined to invite
                        // dialog.cancel();
                    }})
                .setPositiveButton("Agree",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Log.d("TAG","You have agreed invite");
                        agreeToInvite(invite);
                        //Todo make Agree to evnet
                       // GetInEvent(invite.getEventId());
                        // MainActivity.this.finish();
                    }

                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        if(!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }
    public void agreeToInvite(Invite invite ) {
        //TODO
        //get in to event.
        getInEvent(invite.getEventId());
        //delete from invite DB
//        Repository.instance.removeInvite(new FirebaseModel.Callback<Boolean>() {
//            @Override
//            public void onComplete(Boolean data) {
//            }
//        }, invite);


    }
    public void declineToInvite(Invite invite ) {
        //TODO
        //delete from invite DB
        Repository.instance.removeInvite(new FirebaseModel.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
            }
        }, invite);

    }
    public void getInEvent (String eventId) {
        Intent intent;
        intent = new Intent(getApplicationContext(),RecordingActivity.class);
        intent.putExtra("eventidToGetIn",eventId);
        int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        intent.putExtra(Consts.UID_KEY, id);
       // Log.d("Tag","eventID="+eventId);
        startActivity(intent);
    }



}
