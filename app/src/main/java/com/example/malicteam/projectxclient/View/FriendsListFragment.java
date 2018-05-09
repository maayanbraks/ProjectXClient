package com.example.malicteam.projectxclient.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.malicteam.projectxclient.Activity.LoginActivity;
import com.example.malicteam.projectxclient.Activity.MainActivity;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;
//import com.example.malicteam.projectxclient.ViewModel.FriendsViewModel;

import java.util.LinkedList;
import java.util.List;

import Responses.ContactsListResponseData;
import Responses.LoginResponseData;


public class FriendsListFragment extends Fragment {
    private OnFriendSelected mListener;
    private int userId;
    private List<User> friendsList = new LinkedList<>();
    private MyAdapter adapter = null;
    private ListView friendsListView = null;
//    private FriendsViewModel friendsViewModel = null;
    private String emailString = "";
    //private UserViewModel currentUser = null;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    public static FriendsListFragment newInstance() {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        if(getArguments() != null) {
            friendsListView = (ListView) view.findViewById(R.id.list_friendsList);
            adapter = new MyAdapter();
            friendsListView.setAdapter(adapter);
            this.userId = getArguments().getInt(Consts.USER_ID, Consts.DEFAULT_UID);
            Repository.instance.getFriends(this.userId, new CloudManager.CloudCallback<List<User>>() {
                @Override
                public void onComplete(List<User> data) {
                    friendsList = data;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancel() {
                }
            });
            Repository.instance.getFriends1(new Repository.RepositoryCallback() {


                                                @Override
                                                public void onComplete(Object data) {
                                                    String respone = data.toString();
                                                    Log.d("TAG", "respone=" + respone);
                                                    switch (respone) {
                                                        case "TechnicalError":
                                                            Log.d("TAG", "In getfriends1-->friendlistFragment ---> Technical error");
                                                            //Toast.makeText(getApplicationContext(), "Technical error,please try again.", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        case "UserMustToLogin":
                                                            Log.d("TAG", "n getfriends1-->friendlistFragment ---> UserMustToLogin");
                                                            // Toast.makeText(getApplicationContext(), "Can`t find username.", Toast.LENGTH_SHORT).show();
                                                            break;

                                                        default:
                                                            ContactsListResponseData contactsListResponseData =CloudManager.getObjectFromString(data.toString(),ContactsListResponseData.class);
                                                            Log.d("TAG","List= "+CloudManager.getStringFromObject(contactsListResponseData));
                                                           // friendsList=friendsList.get(0).convertUserDataToUser(((ContactsListResponseData) data).getContacts());
                                                            User user=new User();
                                                            friendsList = user.convertUserDataToUser(contactsListResponseData.getContacts());

//                                                            for (User userr:friendsList)
//                                                            {
//                                                                Log.d("TAG","a"+userr.getFirstName());
//                                                            }
                                                      //     if (adapter != null) {
//                                                                adapter.notifyDataSetChanged();
                                                            //}
                                                            Log.d("TAG", "friend list obtaib sucful");

                                                            //TODO
                                                            // toast and then new intent
                                                            //  Toast.makeText(getApplication(), "logging in", Toast.LENGTH_SHORT).show();loginResponseData
                                                            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            //  LoginResponseData loginResponseData= CloudManager.getObjectFromString(data.toString(),LoginResponseData.class);
                                                            //  User myuser=new User(loginResponseData.getFirstName(), loginResponseData.getLastName(), loginResponseData.getPhone(), email, null, null,1,loginResponseData.getId());
                                                            // intent.putExtra(Consts.USER, myuser);
                                                            //  startActivity(intent);
                                                            //  finish();
                                                            break;
                                                    }
                                                }

                                                @Override
                                                public void onCancel() {

                                                }
                                            });



                                                //            friendsViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
//            friendsViewModel.init(userId);
//            friendsViewModel.getFriends().observe(this, new Observer<List<User>>() {
//                @Override
//                public void onChanged(@Nullable List<User> users) {
//                    friendsList = users;
//                    refreshList();
//                }
//            });
            initButtons(view);
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFriendSelected) {
            mListener = (OnFriendSelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        userId = getArguments().getInt(Consts.USER_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFriendSelected {
        void showFriendDetails(User user);
    }

    private void refreshList() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void initButtons(View view) {
        ImageButton addButton = (ImageButton) view.findViewById(R.id.addFriendButton_friendsList);
        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.deleteFriendButton_friendList);

//Add friend
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsListActivity.this);
//                builder.setTitle("Add New Friend");
//                builder.setMessage("Enter Email:");
//                final EditText input = new EditText(MyApp.getContext());
//                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//                builder.setView(input);
//                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        emailString = input.getText().toString();
//                        Repository.instance.addFriend(emailString, new CloudManager.CloudCallback<Boolean>() {
//                            @Override
//                            public void onComplete(Boolean data) {
//                                if (data) {
//                                    Toast.makeText(MyApp.getContext(), "Added", Toast.LENGTH_SHORT).show();
//                                    refreshList();
//                                } else
//                                    Toast.makeText(MyApp.getContext(), "Cannot add to your friends right now, please try later...", Toast.LENGTH_LONG).show();
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                dialog.cancel();
//                            }
//                        });
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                AlertDialog d = builder.create();
//                d.show();
//            }
//        });
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

    private class MyAdapter extends BaseAdapter {

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
                    mListener.showFriendDetails(friend);
                }
            });

            //Delete Friend - Button (X button) of every friend on th list
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.deleteFriendButton_friendRowList);
            if (deleteButton != null)
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyApp.getContext());//Changed from Activity.this
                        builder.setTitle("Delete Friend");
                        builder.setMessage("Are you sure you wand delete " + friend.getFirstName() + " " + friend.getLastName() + " from your friends?");
                        builder.setPositiveButton("Yes, Delete!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Repository.instance.deleteFromFriends(friend.getId(), new CloudManager.CloudCallback<Boolean>() {
                                    @Override
                                    public void onComplete(Boolean data) {
                                        if (data) {
                                            Toast.makeText(MyApp.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                            refreshList();
                                        } else
                                            Toast.makeText(MyApp.getContext(), "Cannot delete your friend right now, please try later...", Toast.LENGTH_LONG).show();
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
