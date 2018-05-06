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
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.multidex.MultiDex;
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
import android.widget.Toast;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Invite;
import com.example.malicteam.projectxclient.R;
//import com.example.malicteam.projectxclient.ViewModel.EventsViewModel;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.Model.Repository;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Event> eventsList = new LinkedList<>();
    private TextView userNameHeader;
    private TextView userEmailHeader;
    private NavigationView navigationView;
    //    private EventsViewModel eventsData = null;
    private UserViewModel currentUser = null;
    private int userId;
    private EventAdapter adapter;
    private View headerLayout;

    //private User myuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView eventListView = (ListView) findViewById(R.id._listOfEvents);
        adapter = new EventAdapter();
        eventListView.setAdapter(adapter);
        userId = getIntent().getIntExtra(Consts.USER_ID, Consts.DEFAULT_UID);

        //invitation code:
        Repository.instance.getInvite("" + userId, new FirebaseModel.FirebaseCallback<Invite>() {
            @Override
            public void onComplete(Invite invite) {
                Invitation(invite);
            }

            @Override
            public void onCancel() {

            }
        }, new FirebaseModel.GetInvitation() {
            @Override
            public void onComplete(Invite invite) {
                Invitation(invite);
            }
        });
        //End of - invitation code
////////////////////startin testing the socketio



        ///////////


        //Get Events
        Repository.instance.getEvents(userId, new FirebaseModel.FirebaseCallback<List<Event>>() {
            @Override
            public void onComplete(List<Event> data) {
                eventsList = data;
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancel() {
            }
        });

        try {
            CloudManager cd=new CloudManager();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
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
                    refreshList();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        //Permission
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Consts.REQUEST_WRITE_STORAGE);
        }

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = eventsList.get(position);
                Intent intent = new Intent(getApplicationContext(), EventDetails.class);
                intent.putExtra(Consts.SEND_EVENT, event);
                startActivity(intent);
            }
        });

        //End of
        //Floating add button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.record_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewEventActivity.class);
                int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                intent.putExtra(Consts.USER_ID, id);
                startActivity(intent);
            }
        });

        //Draw
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }


    private void refreshList(){
        Repository.instance.getEvents(userId, new FirebaseModel.FirebaseCallback<List<Event>>() {
            @Override
            public void onComplete(List<Event> data) {
                eventsList = data;
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    protected void onResume() { //after creating new event.
        super.onResume();
        refreshList();
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
           // startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

        if (this.getClass() != MainActivity.class)
            finish();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateProfilePicture(String url) {
        ImageView profilePic = (ImageView)headerLayout.findViewById(R.id.userPic_head);
        Repository.instance.getProfilePicture(
                new FirebaseModel.FirebaseCallback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        try {
                            profilePic.setImageBitmap(data);
                        }
                        catch (Exception e) {
                            profilePic.setImageResource(R.drawable.outalk_logo);
                        }
                    }

                    @Override
                    public void onCancel() {
                        profilePic.setImageResource(R.drawable.outalk_logo);
                    }
                }

        );
    }


    public void Invitation(final Invite invite) {
        final Context context = this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("You got new Invitation, from " + invite.getInviteFromId());

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        declineToInvite(invite);
                        //Todo make delined to invite
                        // dialog.cancel();
                    }
                })
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("TAG", "You have agreed invite");
                        agreeToInvite(invite);
                        //Todo make Agree to evnet
                        // GetInEvent(invite.getEventId());
                        // MainActivity.this.finish();
                    }

                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        if (!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }

    public void agreeToInvite(Invite invite) {
        //TODO get in to event
        //get in to event.
        getInEvent(invite.getEventId());
        //delete from invite DB
//        Repository.instance.removeInvite(new FirebaseModel.Callback<Boolean>() {
//            @Override
//            public void onComplete(Boolean data) {
//            }
//        }, invite);
        //Add event to myeventlist/
        Log.d("TAG", "invitegetevnetid=" + invite.getEventId());
        currentUser.getUser().getValue().addEventToList(Integer.valueOf(invite.getEventId()));
        //update the userDatabase
        Repository.instance.setEventList(currentUser.getUser().getValue(), new FirebaseModel.FirebaseCallback() {
            @Override
            public void onComplete(Object data) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    public void declineToInvite(Invite invite) {
        //TODO
        //delete from invite DB
        Repository.instance.removeInvite(new FirebaseModel.FirebaseCallback<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
            }

            @Override
            public void onCancel() {

            }
        }, invite);

    }

    public void getInEvent(String eventId) {
        Intent intent;

        intent = new Intent(getApplicationContext(), RecordingActivity.class);
        intent.putExtra("eventidToGetIn", eventId);
        int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        intent.putExtra(Consts.USER_ID, id);
        // Log.d("Tag","eventID="+eventId);
        startActivity(intent);
    }

    //Adapter class
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
                view = getLayoutInflater().inflate(R.layout.records_list_row, viewGroup, false);
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

}
