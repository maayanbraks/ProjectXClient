package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.example.malicteam.projectxclient.Common.Callbacks.AgreeToEventCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.DeclineToEventCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.EditFriendListCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.EditUserCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.EventDetailCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.ProtocolRequestCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.isUserExistResponeCallback;
import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.View.Dialogs.AddFriendFragment;
import com.example.malicteam.projectxclient.View.Dialogs.ChangeDetailsFragment;
import com.example.malicteam.projectxclient.View.Dialogs.DataSetAlertDialogFragment;
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

import java.util.List;

import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.ViewModel.FriendsViewModel;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

import ResponsesEntitys.ProtocolLine;
import ResponsesEntitys.UserData;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AccountSettingsFragment.AccountSettingsInteraction, EventsListFragment.EventListListener,
        FriendsListFragment.FriendsFragmentInteraction, NewEventFragment.NewEventInteraction, EventDetailsFragment.EventDetailsInteraction,
        ResetPasswordFragment.ResetPasswordListener, AddFriendFragment.AddFriendInteraction, ChangeDetailsFragment.DetailsDialogInteraction, DataSetAlertDialogFragment.DataSetAlertInteraction {

    //Floating Button
    private FloatingActionButton fab;

    //LiveData
    private UserViewModel currentUser = null;
    private FriendsViewModel currentFriendsList = null;

    private Observer<Integer> convertedObserver = null;

    private final Class _mainFragmentClass = EventsListFragment.class;
    private final int _mainNavId = R.id.nav_events_list;
    private TextView userNameHeader;
    private TextView userEmailHeader;
    private ProgressBar dataSetProgress;
    private NavigationView navigationView;
    private MenuItem currentItem = null;//holds the current item that checked (for un checked it a

    private int userId;
    private View headerLayout;
    private DrawerLayout mDrawer;

    public List<ProtocolLine> getData() {
        return data;
    }

    public void setData(List<ProtocolLine> data) {
        this.data = data;
    }

    private List<ProtocolLine> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        userNameHeader = (TextView) (headerLayout.findViewById(R.id.userName_head));
        userEmailHeader = (TextView) (headerLayout.findViewById(R.id.userMail_head));
        dataSetProgress = (ProgressBar) (headerLayout.findViewById(R.id.progressBar_DataSetTimeHeader));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
        currentUser.initUser((User) getIntent().getSerializableExtra(Consts.USER));
        currentUser.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    if (user.getEmail().equals("MaayanMail"))
                        user.setDataSetTime(15);
                    else if (user.getEmail().equals("EdenMail"))
                        user.setDataSetTime(10);
                    //update details
                    updateProfilePicture(user.getPictureUrl());
                    userNameHeader.setText(user.getFirstName() + " " + user.getLastName());
                    userEmailHeader.setText(user.getEmail());
                    dataSetProgress.setProgress((int) (user.getDataSetTime() * 100 / 15));
                    if (dataSetProgress.getProgress() < 40) {
                        dataSetProgress.setDrawingCacheBackgroundColor(13504528);//RED
                        dataSetProgress.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                    } else if (dataSetProgress.getProgress() < 90) {
                        dataSetProgress.setDrawingCacheBackgroundColor(16772886);//YELLOW
                        dataSetProgress.getProgressDrawable().setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {
                        dataSetProgress.getProgressDrawable().setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
                        dataSetProgress.setDrawingCacheBackgroundColor(4980523);//GREEN
                    }
                    userId = user.getId();
                    if (user.getDataSetTime() == 0) {
                        openDataSetAlert();
                    }
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

        Repository.instance.InitMainActivityCallback(new Observer<Event>() {//Invite ObserverS
                                                         @Override
                                                         public void onChanged(Event data) {
                                                             GetInvation(data);
                                                         }
                                                     },
                new Observer<Integer>() {//Protocol is ready
                    @Override
                    public void onChanged(@Nullable Integer eventId) {
                        makeToastLong("Event id=" + eventId + " protocol is ready");
                        if (convertedObserver != null)
                            convertedObserver.onChanged(eventId);
                    }
                });

        //End of
        //Floating add button
        fab = (FloatingActionButton) findViewById(R.id.record_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFloatingButton(false);
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

        //Show main screen
        loadMainFragment();
    }

    private void loadMainFragment() {
        handleFloatingButton(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentItem != null) {
                    currentItem.setChecked(false);
                }
                currentItem = navigationView.getMenu().findItem(_mainNavId);
                navigationView.getMenu().findItem(_mainNavId).setChecked(true);

            }
        });

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

    private void openDataSetAlert() {
        DataSetAlertDialogFragment dialog = new DataSetAlertDialogFragment();
        dialog.show(getSupportFragmentManager(), "DataSetAlertDialogFragment");
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
                        Repository.instance.disconnectFromServer(new CloudManager.CloudManagerCallback<Boolean>() {
                            @Override
                            public void onComplete(Boolean data) {
                                finish();
                            }

                            @Override
                            public void onCancel() {
                                finish();
                            }
                        });
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
//            Repository.instance.logout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleFloatingButton(boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    fab.setVisibility(View.VISIBLE);
                    fab.setClickable(true);
                } else {
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                }
            }
        });

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
                handleFloatingButton(true);
                bundle.putSerializable(Consts.USER, currentUser.getUser().getValue());
                fragmentClass = AccountSettingsFragment.class;
                break;
            case R.id.nav_events_list:
                handleFloatingButton(true);
                bundle.putInt(Consts.USER_ID, userId);
                fragmentClass = EventsListFragment.class;
                break;
            case R.id.nav_friends_list:
                handleFloatingButton(true);
                bundle.putInt(Consts.USER_ID, userId);
                fragmentClass = FriendsListFragment.class;
                break;
            case R.id.nav_events_new:
                handleFloatingButton(false);
                bundle.putSerializable(Consts.USER, currentUser.getUser().getValue());
                fragmentClass = NewEventFragment.class;
                break;
            case R.id.nav_dataSet:
                goToDataSetActivity();
                return true;
            case R.id.nav_logout:
                logout();
                return true;

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
//        Repository.instance.getProfilePicture(
//                new CloudManager.CloudManagerCallback<Bitmap>() {
//                    @Override
//                    public void onComplete(Bitmap data) {
//                        try {
//                            profilePic.setImageBitmap(data);
//                        } catch (Exception e) {
//                            profilePic.setImageResource(R.drawable.outalk_logo);
//                        }
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        profilePic.setImageResource(R.drawable.outalk_logo);
//                    }
//                }

//        );
    }

    public void agreeToInvite(Event event) {
        //tell server we agreed
        //get in to event.
        Repository.instance.AgreeToInvite(event.getId(), new AgreeToEventCallback<Boolean>() {
            @Override
            public void onSuccees(Boolean data) {
                getInEvent(event);
            }

            @Override
            public void NoPendingEvents() {
                Toast.makeText(MyApp.getContext(), "Error:NoPendingEvents", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void UserIsNotExist() {
                Toast.makeText(MyApp.getContext(), "Error:UserIsNotExist", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("TAG", "invitegetevnetid=" + event.getId());
    }

    public void declineToInvite(Event event) {
        //tell server we declined.
        Repository.instance.DeclineToInvite(event.getId(), new DeclineToEventCallback<Boolean>() {
            @Override
            public void onSuccees(Boolean data) {
                makeToastLong("Refused");
            }

            @Override
            public void TechnicalError() {
                makeToastLong("Error:TechnicalError");
            }

            @Override
            public void NoPendingEvents() {
                makeToastLong("Error:NoPendingEvents");
            }

            @Override
            public void UserIsNotExist() {
                Toast.makeText(MyApp.getContext(), "Error:UserIsNotExist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getInEvent(Event event) {

        Intent intent;
        intent = new Intent(getApplicationContext(), RecordingActivity.class);
        intent.putExtra(Consts.USER, currentUser.getUser().getValue());
        intent.putExtra("eventFromInvitation", event);
        startActivity(intent);
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
    public void deleteFriend(User friend) {
        List<User> friendsListTemp = currentFriendsList.getFriendsList().getValue();
        friendsListTemp.remove(friend);
        Repository.instance.EditFriendList(ProductTypeConverters.GenerateListUserToListMails(friendsListTemp), new EditFriendListCallback() {
            @Override
            public void onSuccees() {
                //Delete from local list
                Repository.instance.deleteFromFriends(friend);
//                currentFriendsList.getFriendsList().getValue().remove(friend);
//                for (int i = 0; i < currentFriendsList.getFriendsList().getValue().size(); i++) {
//                    if (friend.getEmail().equals(friendsList.get(i).getEmail()))
//                        friendsList.remove(i);
//                }
                makeToastLong(friend.getFirstName() + " " + friend.getLastName() + " was Deleted");
                // Log.d("TAG","lalaala"+nunu);
            }

            @Override
            public void UserIsNotExist() {
                Toast.makeText(MyApp.getContext(), "User is not exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error() {
                Toast.makeText(MyApp.getContext(), "ERROR.", Toast.LENGTH_SHORT).show();
            }
        });

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
//////                                Repository.instance.deleteFromFriends(friend.getId(), new CloudManager.CloudManagerCallback<Boolean>() {
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
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(MainActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
//                            finish();
//                        } else {
//                            Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
//                        }
//
//                        progressBar.setVisibility(View.GONE);
//                    }
//                });
    }

    public void GetInvation(Event event) {
        //open dialog with information
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        // set title
        alertDialogBuilder.setTitle("You got new Invitation, from " + event.getAdminId());

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        declineToInvite(event);
                    }
                })
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("TAG", "You have agreed invite");
                        agreeToInvite(event);
                        // GetInEvent(invite.getEventId());
                        // MainActivity.this.finish();
                    }

                });

        // create alert dialog
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
////                              }
//
            }
        });

        // show it


    }

    private CloudManager.CloudManagerCallback<Boolean> addFriendCallback = null;

    @Override
    public void addFriend(CloudManager.CloudManagerCallback<Boolean> callback) {
        addFriendCallback = callback;
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
    public void addFriend(String friendsEmail) {
        Repository.instance.addFriend(friendsEmail, new AddFriendCallback<Boolean>() {
            @Override
            public void onSuccees(Boolean data) {
                if (data)
                    makeToastShort("Added");
                addFriendCallback.onComplete(data);
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

//    @Override
//    public void initFriendsList(FriendsViewModel.FriendsViewModelCallback<List<User>> callback) {
//        currentFriendsList = ViewModelProviders.of(this).get(FriendsViewModel.class);
//        currentFriendsList.getFriendsList().observe(this, new Observer<List<User>>() {
//            @Override
//            public void onChanged(@Nullable List<User> users) {
//                callback.onComplete(users);
//            }
//        });
//    }

    @Override
    public void initFriendsList(Observer<List<User>> observer) {
        currentFriendsList = ViewModelProviders.of(this).get(FriendsViewModel.class);
        currentFriendsList.getFriendsList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                observer.onChanged(users);
            }
        });
    }

    @Override
    public void initEventsList(Observer<List<Event>> observer) {
//        currentEventsList = ViewModelProviders.of(this).get(EventsViewModel.class);
//        currentEventsList.getEventsList().observe(this, new Observer<List<Event>>() {
//            @Override
//            public void onChanged(@Nullable List<Event> events) {
//                observer.onChanged(events);
//            }
//        });
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
                callback.onSuccees(user.getFirstName() + " " + user.getLastName());
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

    @Override
    public void startRecording(Event event) {
        loadMainFragment();//For supply home page after click 'Back' from Recording Activity
        Intent intent = new Intent(MyApp.getContext(), RecordingActivity.class);
        intent.putExtra(Consts.SEND_EVENT, event);
        intent.putExtra(Consts.USER, currentUser.getUser().getValue());
        startActivity(intent);
    }

    @Override
    public void logout() {
        Repository.instance.disconnectFromServer(new CloudManager.CloudManagerCallback<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {

            }
        });
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void wantToEditAccount(String newFirstName, String newLastName, String newPhone) {
        Bundle bundle = new Bundle();
        bundle.putString(Consts.FIRST_NAME, newFirstName);
        bundle.putString(Consts.LAST_NAME, newLastName);
        bundle.putString(Consts.PHONE_NUMBER, newPhone);
        ChangeDetailsFragment changeDialog = new ChangeDetailsFragment();
        changeDialog.setArguments(bundle);
        changeDialog.show(getSupportFragmentManager(), "ChangeDetailsDialog");
    }

    @Override
    public void edit(String firstName, String lastName, String phone) {
        if (firstName == null)
            firstName = currentUser.getUser().getValue().getFirstName();
        if (lastName == null)
            lastName = currentUser.getUser().getValue().getLastName();
        if (phone == null)
            phone = currentUser.getUser().getValue().getPhoneNumber();
        Repository.instance.editUser(firstName, lastName, currentUser.getUser().getValue().getEmail(), phone, new EditUserCallback<Boolean>() {
            @Override
            public void onSuccees(Boolean data) {
                makeToastShort("Your Details Were Updated");
            }
        });
    }

    @Override
    public void initConvertedObserver(Observer<Integer> observer) {
        this.convertedObserver = observer;
    }

    @Override
    public void getProtocol(int eventId, final EventDetailCallback callback) {
        Repository.instance.getProtocol(eventId, new ProtocolRequestCallback<List<ProtocolLine>>() {
            @Override
            public void TechnicalError() {
                makeToastLong("Technical Error.");
            }

            @Override
            public void ProtocolIsNotExist() {
                makeToastLong("Protocol is not exist.");
            }

            @Override
            public void onSuccees(List<ProtocolLine> data) {
                for (int i = 0; i < data.size(); i++) {
                    Log.d("Protocol", data.get(i).getName() + ":" + data.get(i).getText());
                }
                callback.onSuccees(data);
            }
        });
    }

    @Override
    public void goToDataSetActivity() {
        loadMainFragment();//For supply home page after click 'Back' from Recording Activity
        Intent intent = new Intent(MyApp.getContext(), DataSetActivity.class);
        intent.putExtra(Consts.USER, currentUser.getUser().getValue());
        startActivity(intent);
    }
}
