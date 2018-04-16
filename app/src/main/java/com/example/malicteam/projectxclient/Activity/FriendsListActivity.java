package com.example.malicteam.projectxclient.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.R;
//import com.example.malicteam.projectxclient.ViewModel.FriendsViewModel;
import com.example.malicteam.projectxclient.ViewModel.FriendsViewModel;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

import java.util.LinkedList;
import java.util.List;

import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;

public class FriendsListActivity extends FragmentActivity {

    private int userId;
    //private UserViewModel currentUser = null;
    private List<User> friendsList = new LinkedList<>();
    private MyAdapter adapter = new MyAdapter();
    private ListView friendsListView = null;
    private User waitForAction = null;//Clicked User
    private String emailString = "";

    private FriendsViewModel friendsViewModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get user id in intent
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        userId = getIntent().getIntExtra(Consts.USER_ID, Consts.DEFAULT_UID);

        friendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
        friendsViewModel.init(userId);
        friendsViewModel.getFriends().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                friendsList = users;
                refreshList();
            }
        });

        friendsListView = (ListView) findViewById(R.id.list_friendsList);
        friendsListView.setAdapter(adapter);

        //initButtons();
    }

    private void initButtons() {
        ImageButton addButton = (ImageButton) findViewById(R.id.addFriendButton_friendsList);
        ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteFriendButton_friendList);

//Add friend
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
                        Repository.instance.addFriend(emailString, new CloudManager.CloudCallback<Boolean>() {
                            @Override
                            public void onComplete(Boolean data) {
                                if (data) {
                                    Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                                    refreshList();
                                } else
                                    Toast.makeText(getApplicationContext(), "Cannot add to your friends right now, please try later...", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancel() {
                                dialog.cancel();
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
//delete from the main delete button
        deleteButton.setVisibility(View.INVISIBLE);
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (waitForAction != null) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
//                    builder.setTitle("Delete Friend");
//                    builder.setMessage("Are you sure you wand delete " + waitForAction.getFirstName() + " " + waitForAction.getLastName() + " from your friends?");
//                    builder.setPositiveButton("Yes, Delete!", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Repository.instance.deleteFromFriends(waitForAction.getId(), new FirebaseModel.CloudCallback<Boolean>() {
//                                @Override
//                                public void onComplete(Boolean data) {
//                                    if (data != null) {
//                                        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
//                                        refreshList();
//                                    } else
//                                        Toast.makeText(getApplicationContext(), "Cannot delete your friend right now, please try later...", Toast.LENGTH_LONG).show();
//                                }
//
//                                @Override
//                                public void onCancel() {
//                                    dialog.cancel();
//                                }
//                            });
//
//                        }
//                    })
//                            .setNegativeButton("No, Cancel!", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//                    builder.show();
//                }
//            }
//        });
    }

    private void refreshList() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return friendsList.size();
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
                view = getLayoutInflater().inflate(R.layout.friends_list_row, viewGroup, false);
            }
            User friend = friendsList.get(i);

            //Friend Details - onClick
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FriendsListActivity.this, FriendDetailsActivity.class);
                    intent.putExtra(Consts.USER, friend);
                    startActivity(intent);
                }
            });

            //Delete Friend - Button (X button) of every friend on th list
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.deleteFriendButton_friendRowList);
            if (deleteButton != null)
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
                        builder.setTitle("Delete Friend");
                        builder.setMessage("Are you sure you wand delete " + friend.getFirstName() + " " + friend.getLastName() + " from your friends?");
                        builder.setPositiveButton("Yes, Delete!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Repository.instance.deleteFromFriends(friend.getId(), new CloudManager.CloudCallback<Boolean>() {
                                    @Override
                                    public void onComplete(Boolean data) {
                                        if (data) {
                                            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                            refreshList();
                                        } else
                                            Toast.makeText(getApplicationContext(), "Cannot delete your friend right now, please try later...", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancel() {
                                        dialog.cancel();
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
