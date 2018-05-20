package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Callbacks.AddEventCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.AddFriendCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.MainActivityCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.isUserExistResponeCallback;
import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.View.Dialogs.AddFriendFragment;
import com.example.malicteam.projectxclient.View.NewEventFragment;
import com.example.malicteam.projectxclient.View.AccountSettingsFragment;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Model.Invite;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.View.EventDetailsFragment;
import com.example.malicteam.projectxclient.View.EventsListFragment;
import com.example.malicteam.projectxclient.View.FriendDetailsFragment;
import com.example.malicteam.projectxclient.View.FriendsListFragment;
import com.example.malicteam.projectxclient.View.ResetPasswordFragment;

import java.net.URISyntaxException;
import java.util.List;

import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.ViewModel.FriendsViewModel;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import Notifications.EventInvitationNotificationData;
import ResponsesEntitys.UserData;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AccountSettingsFragment.OnFragmentInteractionListener, EventsListFragment.EventListListener,
        FriendsListFragment.FriendsFragmentInteraction, NewEventFragment.NewEventInteraction, FriendDetailsFragment.OnFragmentInteractionListener, EventDetailsFragment.OnFragmentInteractionListener,
        ResetPasswordFragment.ResetPasswordListener {

    private final Class _mainFragmentClass = EventsListFragment.class;

    //    private List<Event> eventsList = new Link
    private TextView userNameHeader;
    private TextView userEmailHeader;
    private NavigationView navigationView;
    private MenuItem currentItem = null;//holds the current item that checked (for un checked it a
    private UserViewModel currentUser = null;
    private FriendsViewModel currentFriendsList = null;
    private int userId;
    private View headerLayout;
    private DrawerLayout mDrawer;

    //New Server
//    private User mUser = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            CloudManager cd = new CloudManager();
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

//        updateProfilePicture(mUser.getPictureUrl());
//        userNameHeader.setText(mUser.getFirstName() + " " + mUser.getLastName());
//        userEmailHeader.setText(mUser.getEmail());
        currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
        currentUser.initUser((User) getIntent().getSerializableExtra(Consts.USER));
        currentUser.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    //update details
                    updateProfilePicture(user.getPictureUrl());
                    userNameHeader.setText(user.getFirstName() + " " + user.getLastName());
                    userEmailHeader.setText(user.getEmail());
                    userId = user.getId();
//                    refreshList();
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

        Repository.instance.InitMainActivityCallback(new MainActivityCallback() {
            @Override
            public void GotInvitation(Event event) {
                GetInvation(event);
            }
        });
//        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(Adapte
// rView<?> parent, View view, int position, long id) {
//                Event event = eventsList.get(position);
//                Intent intent = new Intent(getApplicationContext(), EventDetails.class);
//                intent.putExtra(Consts.SEND_EVENT, event);
//                startActivity(intent);
//            }
//        });

        //End of
        //Floating add button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.record_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = null;
                Bundle bundle = new Bundle();
                bundle.putSerializable(Consts.USER, currentUser.getUser().getValue());
                Class fragmentClass = null;
                fragmentClass = NewEventFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragment.setArguments(bundle);
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });

        //Draw
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        //Show Events list as main screen
        loadMainFragment();
    }

    private void loadMainFragment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Consts.USER, currentUser.getUser().getValue());
        Fragment mainFragment = null;
        try {
            mainFragment = (EventsListFragment) _mainFragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, mainFragment).commit();
//        if(mainFragment.getView() == null) {
//            FragmentTransaction tran = fragmentManager.beginTransaction();
//            tran.add(R.id.flContent, mainFragment);
//            tran.commit();
//        }
//        else
//        {
//            fragmentManager.beginTransaction().replace(R.id.flContent, mainFragment, _mainFragmentClass.getName()).commit();
//        }
    }

    private Class getCurrentShownFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment.getClass();
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getCurrentShownFragment() != _mainFragmentClass) {
            //Back To Main Screen
            loadMainFragment();
        } else {
            openExitAlert();
        }
    }

    private void openExitAlert() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
//        builder.setTitle("Add New Friend");
//        builder.setMessage("Enter Email:");
//        builder.setView(input);
//        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                emailString = input.getText().toString();
//                Repository.instance.addFriend(emailString, new CloudManager.CloudCallback<Boolean>() {
//                    @Override
//                    public void onComplete(Boolean data) {
//                        if (data) {
//                            Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
//                            refreshList();
//                        } else
//                            Toast.makeText(getApplicationContext(), "Cannot add to your friends right now, please try later...", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        dialog.cancel();
//                    }
//                });
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        AlertDialog d = builder.create();
//        d.show();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("What?! Do you want to exit?");
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("No, Stay!!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }

                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;
        Bundle bundle = new Bundle();
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_settings_account:
                bundle.putInt(Consts.USER_ID, userId);
                fragmentClass = AccountSettingsFragment.class;
                break;
            case R.id.nav_events_list:
                bundle.putInt(Consts.USER_ID, userId);
                fragmentClass = EventsListFragment.class;
                break;
            case R.id.nav_friends_list:
                bundle.putInt(Consts.USER_ID, userId);
                fragmentClass = FriendsListFragment.class;
                break;
            case R.id.nav_events_new:
                bundle.putSerializable(Consts.USER, currentUser.getUser().getValue());
                fragmentClass = NewEventFragment.class;
                break;

            default:
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragment.setArguments(bundle);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment, fragmentClass.getName()).commit();

        if (this.currentItem != null) {
            this.currentItem.setChecked(false);
        }
        menuItem.setChecked(true);
        this.currentItem = menuItem;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void updateProfilePicture(String url) {
        ImageView profilePic = (ImageView) headerLayout.findViewById(R.id.userPic_head);
        Repository.instance.getProfilePicture(
                new CloudManager.CloudCallback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        try {
                            profilePic.setImageBitmap(data);
                        } catch (Exception e) {
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

    /////////////////////////////////////////////////////////////////////////
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
                        //  declineToInvite(invite);
                        //Todo make delined to invite
                        // dialog.cancel();
                    }
                })
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("TAG", "You have agreed invite");
                        //   agreeToInvite(invite);
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

    public void agreeToInvite(Event event) {
        //TODO get in to event
        //tell server we agreed

        //get in to event.
        getInEvent(event);

//        Repository.instance.removeInvite(new FirebaseModel.Callback<Boolean>() {
//            @Override
//            public void onComplete(Boolean data) {
//            }
//        }, invite);
        //Add event to myeventlist/
        Log.d("TAG", "invitegetevnetid=" + event.getId());
//        currentUser.getUser().getValue().addEventToList(Integer.valueOf(eventInvitationNotificationData.getEventId()));
        //update the userDatabase
//        Repository.instance.setEventList(currentUser.getUser().getValue(), new CloudManager.CloudCallback() {
//            @Override
//            public void onComplete(Object data) {
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//        });
    }

    public static void declineToInvite(Event event) {
        //TODO
        //tell server we declined.


        //delete from invite DB
//        Repository.instance.removeInvite(new CloudManager.CloudCallback<Boolean>() {
//            @Override
//            public void onComplete(Boolean data) {
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//        }, invite);

    }

    public void getInEvent(Event event) {
        Intent intent;
        intent = new Intent(getApplicationContext(), RecordingActivity.class);
        intent.putExtra("eventFromInvitation", event);
        // Log.d("Tag","eventID="+eventId);
        startActivity(intent);
    }


    //Fragments Interaction
    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO: Acoount
    }

    //FriendsList interface
    @Override
    public void showFriendDetails(User user) {
        FriendDetailsFragment fragment = FriendDetailsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Consts.USER, user);
        fragment.setArguments(bundle);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void deleteFriend(User friend, List<User> friendsList) {


    }

    @Override
    public void onEventSelected(Event event) {
        EventDetailsFragment fragment = EventDetailsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Consts.SEND_EVENT, event);
        fragment.setArguments(bundle);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    //ResetPassword Listener
    @Override
    public void onBackButtonClick() {
        AccountSettingsFragment fragment = AccountSettingsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt(Consts.USER_ID, userId);
        fragment.setArguments(bundle);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    //DeleteFriendListener - open AlertDialog
//    @Override
//    public void deleteFriend(User friend, LinkedList<User>fr) {
////        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MyApp.getContext());//Changed from Activity.this
////        builder.setTitle("Delete Friend");
////        builder.setMessage("Are you sure you wand delete " + friend.getFirstName() + " " + friend.getLastName() + " from your friends?");
////        builder.setPositiveButton("Yes, Delete!", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                Repository.instance.EditFriendList(ProductTypeConverters.GenerateListUserToListMails(currentFriendsList),new EditFriendListCallback() {
////                    @Override
////                    public void onSuccees() {
////
////                    }
////
////                    @Override
////                    public void UserIsNotExist() {
////
////                    }
////
////                    @Override
////                    public void error() {
////
////                    }
////                });
////            }
//////                                Repository.instance.deleteFromFriends(friend.getId(), new CloudManager.CloudCallback<Boolean>() {
//////                                    @Override
//////                                    public void onComplete(Boolean data) {
//////                                        if (data) {
//////                                            Toast.makeText(MyApp.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
//////                                            refreshList();
//////                                        } else
//////                                            Toast.makeText(MyApp.getContext(), "Cannot delete your friend right now, please try later...", Toast.LENGTH_LONG).show();
//////                                    }
//////
//////                                    @Override
//////                                    public void onCancel() {
//////                                        dialog.cancel();
//////                                    }
//////                                });
////
////
////        })
////                .setNegativeButton("No, Cancel!", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        dialog.cancel();
////                    }
////                });
////        builder.show();
//    }

    @Override
    public void sendResetPassword(String email) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void GetInvation(Event event) {

        //todo
        //open dialog with information
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyApp.getContext());

        // set title
        alertDialogBuilder.setTitle("You got new Invitation, from " + ProductTypeConverters.getAdminFirstNameByEmail(event));

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        declineToInvite(event);
                        //Todo make delined to invite
                        // dialog.cancel();
                    }
                })
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("TAG", "You have agreed invite");
                        agreeToInvite(event);
                        //Todo make Agree to evnet
                        // GetInEvent(invite.getEventId());
                        // MainActivity.this.finish();
                    }

                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        if (!((Activity) MyApp.getContext()).isFinishing()) {
            alertDialog.show();
        }


    }

    @Override
    public void addFriend() {
        AddFriendFragment addFriendDialog = new AddFriendFragment();
        addFriendDialog.show(getSupportFragmentManager(), "addFriendDialog");
//        Repository.instance.addFriend("MaayanMail", new AddFriendCallback<User>() {
//            @Override
//            public void onSuccees(User data) {
////                Log.d("TAG", "friendlistsizeBeforeAdding=" + currentFriendsList.size());
////                currentFriendsList.add(data);
////                Log.d("TAG", "friendlistsizeafteradding=" + currentFriendsList.size());
////                refreshList();
////                Log.d("TAG", "n addfriend-->friendlistFragment ---> aasdasd" + data.getFirstName());
//            }
//
//            @Override
//            public void userIsNotExist() {
//                Log.d("TAG", "n addfriend-->friendlistFragment ---> userIsNotExist");
//            }
//
//            @Override
//            public void friendIsNotExist() {
//                Log.d("TAG", "n addfriend-->friendlistFragment ---> friendIsNotExist");
//            }
//
//            @Override
//            public void bothUsersEquals() {
//                Log.d("TAG", "n addfriend-->friendlistFragment ---> bothUsersEquals");
//            }
//
//            @Override
//            public void alreadyFriends() {
//                Log.d("TAG", "n addfriend-->friendlistFragment ---> alreadyFriends");
//            }
//        });
    }

    @Override
    public void addFriend(String friendsEmail){
        Repository.instance.addFriend(friendsEmail, new AddFriendCallback<User>() {
            @Override
            public void onSuccees(User data) {
                makeToastShort("Added");
//                Log.d("TAG", "friendlistsizeBeforeAdding=" + currentFriendsList.size());
//                currentFriendsList.add(data);
//                Log.d("TAG", "friendlistsizeafteradding=" + currentFriendsList.size());
//                refreshList();
//                Log.d("TAG", "n addfriend-->friendlistFragment ---> aasdasd" + data.getFirstName());
            }

            @Override
            public void userIsNotExist() {
                Log.d("TAG", "n addfriend-->friendlistFragment ---> userIsNotExist");
            }

            @Override
            public void friendIsNotExist() {
                Log.d("TAG", "n addfriend-->friendlistFragment ---> friendIsNotExist");
            }

            @Override
            public void bothUsersEquals() {
                Log.d("TAG", "n addfriend-->friendlistFragment ---> bothUsersEquals");
            }

            @Override
            public void alreadyFriends() {
                Log.d("TAG", "n addfriend-->friendlistFragment ---> alreadyFriends");
            }
        });
    }

    @Override
    public void initFriendsList(FriendsViewModel.FriendsViewModelCallback<List<User>> callback) {
        currentFriendsList = ViewModelProviders.of(this).get(FriendsViewModel.class);
        currentFriendsList.getFriendsList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                callback.onComplete(users);
            }
        });
    }

    @Override
    public void isUserExist(String parti, AddEventCallback<String> callback) {
        Repository.instance.getUserIfExist(parti, new isUserExistResponeCallback() {
            @Override
            public void onSuccees(UserData data) {
                User user = new User(data);
                String maketoast = "Adding" + user.getFirstName() + " " + user.getLastName();
                makeToastShort(maketoast);
               // Toast.makeText(MyApp.getContext(), maketoast, Toast.LENGTH_LONG);
                callback.onSuccees(user.getFirstName());
            }

            @Override
            public void userIsNotExist() {
                // Toast.makeText(MyApp.getContext(), "User is not exist,please try again.", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "In addevent-->neweventfragment----> Technical userIsNotExist");
            }

            @Override
            public void friendIsNotExist() {
                //Toast.makeText(MyApp.getContext(), "User is not exist,please try again.", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "In addevent-->neweventfragment----> friendIsNotExist");

            }

        });
    }

    @Override
    public void makeToastShort(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void makeToastLong(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

    }
}
