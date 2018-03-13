package com.example.malicteam.projectxclient.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.R;
//import com.example.malicteam.projectxclient.ViewModel.FriendsViewModel;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

import java.util.LinkedList;
import java.util.List;

import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;

public class FriendsListActivity extends FragmentActivity {
    boolean isFriendAlready = false;
    private List<User> friends = new LinkedList<>();
    private int userId;
    private User waitForAction = null;
    //private FriendsViewModel friendsListData = null;
    private UserViewModel currentUser = null;
    private MyAdapter adapter = new MyAdapter();
    ListView friendsListView = null;

    private String emailString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get user id in intent
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        userId = getIntent().getIntExtra(Consts.UID_KEY, Consts.DEFAULT_UID);

        Repository.instance.getFriends(userId, new FirebaseModel.Callback<List<User>>() {
            @Override
            public void onComplete(List<User> data) {
                friends = data;
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        });

        currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
        currentUser.init(userId);
//        friendsListData = ViewModelProviders.of(this).get(FriendsViewModel.class);
//        friendsListData.init(userId);

        friendsListView = (ListView) findViewById(R.id.list_friendsList);
        try {
            friendsListView.setAdapter(adapter);
            Log.d("tag", "dsfdsfds");
        } catch (Exception e) {
            Log.d("tag", "dsfdsfds");
        }

        Log.d("ok", "so far so good");


//        friendsListData.getFriends().observe(this, new Observer<List<User>>() {
//            @Override
//            public void onChanged(@Nullable List<User> users) {
//                friends = users;
//                if (adapter != null)
//                    adapter.notifyDataSetChanged();
//            }
//        });

        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                waitForAction = friends.get(position);

                for(int i=0; i<parent.getChildCount(); i++)
                {
                    if(i == position)
                    {
                        parent.getChildAt(i).setBackgroundColor(10200754);
                    }
                }
            }


        });

        currentUser.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    //update details
                    userId = user.getId();
                    initButtons();
                } else {
                    Intent intent = new Intent(FriendsListActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        //initButtons();
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
            startActivity(new Intent(FriendsListActivity.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initButtons() {
        Button addButton = (Button) findViewById(R.id.addFriendButton_friendsList);
        Button deleteButton = (Button) findViewById(R.id.deleteFriendButton_friendList);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
                builder.setTitle("Add New Friend");
                builder.setMessage("Enter Email:");
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        emailString = input.getText().toString();
                        Repository.instance.addFriend(emailString, new FirebaseModel.Callback<List<User>>() {
                            @Override
                            public void onComplete(List<User> data) {
                                if (data != null) {
                                    Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                                    friends = data;
                                    if (adapter != null)
                                        adapter.notifyDataSetChanged();
                                } else
                                    Toast.makeText(getApplicationContext(), "Cannot add to your friends right now, please try later...", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog d = builder.create();
                d.show();


                Log.d("okkk", "okkkkkk");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (waitForAction != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
                    builder.setTitle("Delete Friend");
                    builder.setMessage("Are you sure you wand delete " + waitForAction.getFirstName() + " " + waitForAction.getLastName() + " from your friends?");
                    builder.setPositiveButton("Yes, Delete!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Repository.instance.deleteFromFriends(waitForAction.getId(), new FirebaseModel.Callback<List<User>>() {
                                @Override
                                public void onComplete(List<User> data) {
                                    if (data != null) {
                                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                        friends = data;
                                        if (adapter != null)
                                            adapter.notifyDataSetChanged();
                                    } else
                                        Toast.makeText(getApplicationContext(), "Cannot delete your friend right now, please try later...", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })
                            .setNegativeButton("No, Cancel!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                }
                waitForAction = null;
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return friends.size();
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
                view = getLayoutInflater().inflate(R.layout.friends_list_row, null);
            }
            User friend = friends.get(i);


            TextView firstName = (TextView) view.findViewById(R.id.firstName_friendsRow);
            TextView lastName = (TextView)view.findViewById(R.id.lastName_friendsRow);
            TextView email = (TextView)view.findViewById(R.id.email_friendsRow);
            firstName.setText(friend.getFirstName());
            lastName.setText(friend.getLastName());
            email.setText(friend.getEmail());


            return view;
        }
    }


}
