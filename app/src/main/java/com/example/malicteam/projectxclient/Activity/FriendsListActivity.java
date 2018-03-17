package com.example.malicteam.projectxclient.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
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

    private int userId;
    private UserViewModel currentUser = null;
    private List<User> friends = new LinkedList<>();
    private MyAdapter adapter = new MyAdapter();
    private ListView friendsListView = null;
    private User waitForAction = null;//Clicked User
    private String emailString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get user id in intent
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        userId = getIntent().getIntExtra(Consts.USER_ID, Consts.DEFAULT_UID);

        Repository.instance.getFriends(userId, new FirebaseModel.Callback<List<User>>() {
            @Override
            public void onComplete(List<User> data) {
                friends = data;
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        });

        currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
        currentUser.init(userId, true);
//        friendsListData = ViewModelProviders.of(this).get(FriendsViewModel.class);
//        friendsListData.init(userId);

        friendsListView = (ListView) findViewById(R.id.list_friendsList);
        friendsListView.setAdapter(adapter);

        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                waitForAction = friends.get(position);

                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (i == position) {
                        parent.getChildAt(i).setBackgroundColor(Color.WHITE);
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

            view.setBackgroundColor(Color.WHITE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FriendsListActivity.this, FriendDetailsActivity.class);
                    intent.putExtra(Consts.USER_ID, friend.getId());
                    startActivity(intent);
                }
            });

            TextView firstName = (TextView) view.findViewById(R.id.firstName_friendsRow);
            TextView lastName = (TextView) view.findViewById(R.id.lastName_friendsRow);
            TextView email = (TextView) view.findViewById(R.id.email_friendsRow);
            firstName.setText(friend.getFirstName());
            lastName.setText(friend.getLastName());
            email.setText(friend.getEmail());

            return view;
        }
    }


}
