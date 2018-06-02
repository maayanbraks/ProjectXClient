package com.example.malicteam.projectxclient.View;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.malicteam.projectxclient.Common.Callbacks.EditFriendListCallback;
import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.FriendsViewModel;
//import com.example.malicteam.projectxclient.ViewModel.FriendsViewModel;

import java.util.LinkedList;
import java.util.List;


public class FriendsListFragment extends Fragment {
    public interface FriendsFragmentInteraction extends BasicInteractionInterface {
        void showFriendDetails(User user);

        void initFriendsList(Observer<List<User>> observer);

        void addFriend();

        void deleteFriend(User friend);
    }

    private FriendsFragmentInteraction mListener;
    private List<User> friendsList = new LinkedList<>();
    private MyAdapter adapter = null;
    private ListView friendsListView = null;

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
        if (getArguments() != null) {
            friendsListView = (ListView) view.findViewById(R.id.list_friendsList);
            adapter = new MyAdapter();
            friendsListView.setAdapter(adapter);
//            mListener.initFriendsList(new FriendsViewModel.FriendsViewModelCallback<List<User>>() {
//                @Override
//                public void onComplete(List<User> data) {
//                    friendsList = data;
//                    refreshList();
//                }
//            });
            mListener.initFriendsList(new Observer<List<User>>() {
                @Override
                public void onChanged(List<User> data) {
                    friendsList = data;
                    refreshList();
                }
            });
            initButtons(view);
        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FriendsFragmentInteraction) {
            mListener = (FriendsFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewEventInteraction");
        }
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


    private void refreshList() {
        try {
//            friendsViewModel.getFriendsList().observe(this, new Observer<List<User>>() {
//                @Override
//                public void onChanged(@Nullable List<User> list) {
//                    friendsList = list;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
//                    try {
                        adapter.notifyDataSetChanged();
//                    } catch (Exception e) {
//                        Log.d("TAg", e.getMessage());
//                    }
                    }
                }
            });
//                }
//            });

        } catch (Exception e) {
            Log.d("REFRESH_LIST_TAG", e.getMessage());
        }
    }

//    private void openDialog(){
//        AlertDialog ad = new AlertDialog.Builder(getActivity())
//                .create();
//        ad.setCancelable(false);
//        ad.setTitle("Test Dialog");
//        ad.setMessage("this is test");
//        ad.setButton();
//        ad.show();
//    }

    private void initButtons(View view) {
        ImageButton addButton = (ImageButton) view.findViewById(R.id.addFriendButton_friendsList);
        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.deleteFriendButton_friendList);
        //Add friend
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.addFriend();
//                openDialog();

//                AlertDialog.Builder builder = new AlertDialog.Builder(MyApp.getContext());
//                builder.setTitle("Add New Friend");
//                builder.setMessage("Enter Email:");
//                final EditText input = new EditText(MyApp.getContext());
//                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//                builder.setView(input);
//                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        emailString = input.getText().toString();
////                        Repository.instance.addFriend(emailString, new CloudManager.CloudCallback<Boolean>() {
////                            @Override
////                            public void onComplete(Boolean data) {
////                                if (data) {
////                                    Toast.makeText(MyApp.getContext(), "Added", Toast.LENGTH_SHORT).show();
////                                    refreshList();
////                                } else
////                                    Toast.makeText(MyApp.getContext(), "Cannot add to your friends right now, please try later...", Toast.LENGTH_LONG).show();
////                            }
////
////                            @Override
////                            public void onCancel() {
////                                dialog.cancel();
////                            }
////                        });
//                        Repository.instance.addFriend(emailString, new AddFriendCallback<User>() {
//                            @Override
//                            public void onSuccees(User data) {
//                                //contactsListResponseData.getContacts()
//                                //Log.d("TAG","List= "+contactsListResponseData.getContacts());
//                                // friendsList=friendsList.get(0).convertUserDataToUser(((ContactsListResponseData) data).getContacts());
//                                friendsList.add(data);
////                                for (User userr : friendsList) {
////                                    Log.d("TAG", "a" + userr.getFirstName());
////                                }
////                                if (adapter != null) {
////                                    adapter.notifyDataSetChanged();
////                                }
//                                Log.d("TAG", "Friend added Succeful");
//                                refreshList();
//                            }
//
//                            @Override
//                            public void userIsNotExist() {
////                                Log.d("TAG", "In getfriends1-->friendlistFragment ---> Technical error");
//                                Toast.makeText(MyApp.getContext(), "EARROROR", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void friendIsNotExist() {
////                                Log.d("TAG", "n getfriends1-->friendlistFragment ---> userMustToLogin");
//                                Toast.makeText(MyApp.getContext(), "Cant found email,please try again.", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void bothUsersEquals() {
////                                Log.d("TAG", "n getfriends1-->friendlistFragment ---> userMustToLogin");
//                                Toast.makeText(MyApp.getContext(), "You cant add yourself as friend.", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void alreadyFriends() {
//                                Toast.makeText(MyApp.getContext(), "Stupid Boy.. You already friends.", Toast.LENGTH_SHORT).show();
//                            }
//
////                            public void onComplete(String data) {
////                                String respone = data.toString();
////                                Log.d("TAG", "respone=" + respone);
////                                switch (respone) {
////                                    case "UserIsNotExist":
////                                        //Log.d("TAG", "In getfriends1-->friendlistFragment ---> Technical error");
////                                        Toast.makeText(MyApp.getContext(), "EARROROR", Toast.LENGTH_SHORT).show();
////                                        break;
////                                    case "FriendIsNotExist":
////                                        //  Log.d("TAG", "n getfriends1-->friendlistFragment ---> userMustToLogin");
////                                        Toast.makeText(MyApp.getContext(), "Cant found email,please try again.", Toast.LENGTH_SHORT).show();
////                                        break;
////                                    case "BothUsersEquals":
////                                        //Log.d("TAG", "n getfriends1-->friendlistFragment ---> userMustToLogin");
////                                        Toast.makeText(MyApp.getContext(), "You cant add yourself as friend.", Toast.LENGTH_SHORT).show();
////                                        break;
////                                    default:
////                                        // AddFrie contactsListResponseData =CloudManager.getObjectFromString(data.toString(),ContactsListResponseData.class);
////                                        AddFriendResponseData addFriendResponseData = CloudManager.getObjectFromString(data.toString(), AddFriendResponseData.class);
////                                        //contactsListResponseData.getContacts()
////                                        //Log.d("TAG","List= "+contactsListResponseData.getContacts());
////                                        // friendsList=friendsList.get(0).convertUserDataToUser(((ContactsListResponseData) data).getContacts());
////                                        friendsList.add(new User(addFriendResponseData.getUserData()));
//////                                                            for (User userr:friendsList)
//////                                                            {
//////                                                                Log.d("TAG","a"+userr.getFirstName());
//////                                                            }
////                                        //     if (adapter != null) {
//////                                                                adapter.notifyDataSetChanged();
////                                        //}
////                                        Log.d("TAG", "Friend added Succeful");
////
////
////                                        break;
////                                }
////
////                            }
////
////                            @Override
////                            public void onCancel() {
////
////                            }
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
//        });        return View;

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
//                        mListener.deleteFriend(friend,friendsList);
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());//Changed from Activity.this
//                        builder.setTitle("Delete Friend");
//                        builder.setMessage("Are you sure you wand delete " + friend.getFirstName() + " " + friend.getLastName() + " from your friends?");
//                        builder.setPositiveButton("Yes, Delete!", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
                        mListener.deleteFriend(friend);
                        refreshList();
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
