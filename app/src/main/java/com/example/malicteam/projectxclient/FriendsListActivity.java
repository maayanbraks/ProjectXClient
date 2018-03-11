package com.example.malicteam.projectxclient;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Model.FirebaseModel;
import Model.User;

public class FriendsListActivity extends AppCompatActivity {
    boolean isFriendalready = false;
    private List<Integer> data = null;
    private List<Integer> friends;

    private String emailString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        friends = new LinkedList<>();
        data = new LinkedList<Integer>();
        final ListView list = (ListView) findViewById(R.id.list_friendsList);
        // data.add("lala");
        //TODO load user final User user = new User("4ad2ee5b-f5d5-4fef-b52b-60beff53e31a", "asdsdsd", "eden@gmail.com", "1231231232", null);
        User currentUser = new User("Luka", "Modric", "0525556648", "Luka@modric.com", new LinkedList<Integer>() {{
            add(1);
            add(1877972619);
        }});

        //FirebaseModel.addUser(currentUser);


        Button addButton = (Button) findViewById(R.id.addFriendButton_friendsList);
        Button deleteButton = (Button) findViewById(R.id.deleteFriendButton_friendList);


        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Add New Friend");
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        emailString = input.getText().toString();
                        FirebaseModel.getUserByEmail(emailString, new FirebaseModel.GetUserByIdListener() {
                            @Override
                            public void onComplete(User user) {
                                isFriendalready = false;
                                //Check if is friend already
                                if (user.getFriendsIds().contains(User.generateId(emailString))) {
                                    isFriendalready = true;
                                    if (!isFriendalready) {
                                        //TODO check if exist
                                        Toast.makeText(getApplication(), "Adding " + user.getEmail(), Toast.LENGTH_SHORT).show();
//                                        user.addFriendToUser(UsersList.get(0).getName());
//                                        //TODO update DB
//                                        model.addUser(user);
//                                        data.add(UsersList.get(0).getName());
                                        list.requestLayout();
                                        //update the friend list on firebase./////////////////////
                                    }
                                } else {
                                    Toast.makeText(getApplication(), "Cannot find email,please try again!", Toast.LENGTH_SHORT).show();
                                }

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

                builder.show();
            }
        });
        //
//  TODO      deleteButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Delete a friend,Enter name:");
//                final EditText input = new EditText(context);
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
//                builder.setView(input);
//                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        emailString = input.getText().toString();
//                        if(user.SearchFriendInFriendList(emailString)) {       // if true there is a friend in that name
//
//                            user.removeFriendFromUser((emailString));
//                            data=user.friends;
//                            list.requestLayout();
//                            model.addUser(user);
//                        }
//                        else
//                            Toast.makeText(getApplication(), "Cannot find friend,please try again", Toast.LENGTH_SHORT).show();
//
//
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                builder.show();
//            }
//        });
        //

        MyAdapter myadapter = new MyAdapter();
        list.setAdapter(myadapter);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users").child(Integer.toString(currentUser.getId()));
        //instead of this gibrish here.child("4ad2ee5b-f5d5-4fef-b52b-60beff53e31a") ... put myId
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                if (dataSnapshot.getValue() != null) {
                    List<Integer> friends = FirebaseModel.decodeFriends((String) value.get("FriendsList"));
                    data = friends;
                    list.requestLayout();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplication(), "The read failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


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
            int id = data.get(i);

            TextView friendName = view.findViewById(R.id.friendName_friendsRow);
            TextView friendId = view.findViewById(R.id.friendId_friendsRow);

            FirebaseModel.getUserById(id, new FirebaseModel.GetUserByIdListener() {
                @Override
                public void onComplete(User user) {
                    if (friendName != null)
                        friendName.setText(user.getFirstName() + " " + user.getLastName());
                    if (friendId != null)
                        friendId.setText(user.getId());
                }
            });

            return view;
        }
    }


}
