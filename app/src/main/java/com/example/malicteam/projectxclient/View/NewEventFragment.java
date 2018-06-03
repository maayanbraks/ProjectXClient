package com.example.malicteam.projectxclient.View;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Callbacks.AddEventCallback;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class NewEventFragment extends Fragment {
    public interface NewEventInteraction extends BasicInteractionInterface, FriendsListFragment.FriendsFragmentInteraction {
        void isUserExist(String parti, AddEventCallback<String> callback);

        void startRecording(Event event);
    }

    private boolean contactsOpen = false;
    private TextView _invites;
    private NestedScrollView nestedScrollView;

    private NewEventInteraction mListener;
    private UserViewModel currentUser = null;
    private List<String> usersInvitesEmail;
    private List<String> usersInvitesNames;
    private Button startRecord;
    private ImageButton fab;
    private Event event;
    private User myUser;
    private boolean thereIsParti = false;
    private List<User> friendsList;

    public NewEventFragment() {
        // Required empty public constructor
    }

    public static NewEventFragment newInstance() {
        NewEventFragment fragment = new NewEventFragment();
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
        View view = inflater.inflate(R.layout.fragment_new_event, container, false);
        usersInvitesEmail = new LinkedList<>();
        usersInvitesNames = new LinkedList<>();

//        currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
//        currentUser.init(userId, true);
//        currentUser.getUser().observe(this, new Observer<User>() {
//            @Override
//            public void onChanged(@Nullable User user) {
//                if (user != null) {
//                    //update details
//                    userId = user.getId();
//                }
//            }
//        });
//        currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
        //currentUser.init(userId);
        myUser = (User) getArguments().getSerializable(Consts.USER);
        startRecord = (Button) view.findViewById(R.id.new_event_start);
        EditText _name = view.findViewById(R.id.new_event_title);
        EditText _desc = view.findViewById(R.id.new_event_description);
        EditText _part = view.findViewById(R.id.new_event_participants);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.contatsContainer_newEvents);
        _invites = view.findViewById(R.id.newEvent_Invites);
        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                EditText _name = view.findViewById(R.id.new_event_title);
                EditText _desc = view.findViewById(R.id.new_event_description);
                EditText _part = view.findViewById(R.id.new_event_participants);

                String description = _desc.getText().toString();
                String title = _name.getText().toString();
                String parti = _part.getText().toString();

//                if (title == null || title.equals("")) {
//                    Toast.makeText(MyApp.getContext(), "Enter Title", Toast.LENGTH_SHORT).show();
//                    valid = false;
//                }
//                else if (description == null || description.equals("")) {
//                    Toast.makeText(MyApp.getContext(), "Enter Descriptio", Toast.LENGTH_SHORT).show();
//                    valid = false;
//                }
//                else if (!thereIsParti){
//                    Toast.makeText(MyApp.getContext(), "You cant Record alone!", Toast.LENGTH_SHORT).show();
//                    valid = true;
//                }

                //else//
                // if(true) {
                //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = new Date();
                String dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
                event = new Event(null, title, new LinkedList<User>(), description, myUser.getEmail(), dateFormat, 0);
                List<User> participats = new LinkedList<>();
                //participats.add(myUser);
//                usersInvitesEmail.add(myUser.getEmail());
                event.setParticipats(participats);
//                    sendInvites("" + event.getId());
                //Repository.instance.addEvent(event);
                //usersInvitesEmail.add(myUser.getEmail());
                // event.addToParticipats(myUser);
                Repository.instance.addEvent(usersInvitesEmail, event, new AddEventCallback<Integer>() {
                    @Override
                    public void onSuccees(Integer data) {
                        event.setId(data);
                        mListener.startRecording(event);
                    }
                    //   try {
                    //  currentUser.getUser().getValue().addEventToList(Integer.valueOf(event.getId()));
                    //update the userDatabase
//                                Repository.instance.setEventList(currentUser.getUser().getValue(), new CloudManager.CloudManagerCallback() {
//                                    @Override
//                                    public void onComplete(Object data) {
//                                        startActivity(intent);
//                                    }
//
//                                    @Override
//                                    public void onCancel() {

                    //}
                    //});
                    //}

                    @Override
                    public void userIsNotExist() {
                        Toast.makeText(getActivity(), "User is not exist,please try again.", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "In addevent-->neweventfragment----> Technical userIsNotExist");
                    }

                    @Override
                    public void technicalError() {
                        //   Toast.makeText(getActivity(), "Technical error,please try again.", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "In addevent-->neweventfragment ---> Technical error");
                    }
                });


//                    Intent intent = new Intent(MyApp.getContext(), RecordingActivity.class);
//                    intent.putExtra("sendNewEvent", event);
//                    intent.putExtra("currectuser", userId);
                //int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                //intent.putExtra(Consts.USER_ID, id);


//                    }catch (Exception e){
//                        Log.d("sdf", "afdasdf");
//                    }

            }
        });

        fab = (ImageButton) view.findViewById(R.id.addInviteButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parti = _part.getText().toString();
                //check with server is userExist---->if do --->
                mListener.isUserExist(parti, new AddEventCallback<String>() {
                    @Override
                    public void onSuccees(String data) {
                        if (data.equals(myUser.getEmail())) {
                            Log.d("TAG", "You cant add yourself to event");
                        } else {
                            usersInvitesEmail.add(parti);
                            usersInvitesNames.add(data);
                            InviteTextViewEdit(view);
                        }
                        // Log.d("TAG", "In addevent-->neweventfragment----> OnSucess");
                        // Log.d("TAG", "Sucseed found user, added him");
                    }

                    @Override
                    public void userIsNotExist() {

                    }

                    @Override
                    public void technicalError() {

                    }
                });
//
//                                       Repository.instance.getUserIfExist(parti, new isUserExistResponeCallback() {
//                                           @Override
//                                           public void onSuccees(UserData data) {
//                                               User user = new User(data);
//                                               if (invitedPpl.equals(" ")) { // if empty
//                                                   invitedPpl = user.getFirstName();
//                                                    InviteTextViewEdit(view);
//                                                    String maketoast="Adding"+user.getFirstName()+" "+user.getLastName();
//                                                   mListener.makeToastShort(maketoast);
//                                                   Log.d("TAG", "In addevent-->neweventfragment----> OnSucess");
//                                                   Log.d("TAG", "Sucseed found user, added him");
//                                               }
//                                           }
//
//                                           @Override
//                                           public void userIsNotExist() {
//                                              // Toast.makeText(MyApp.getContext(), "User is not exist,please try again.", Toast.LENGTH_SHORT).show();
//                                               Log.d("TAG", "In addevent-->neweventfragment----> Technical userIsNotExist");
//                                           }
//
//                                           @Override
//                                           public void friendIsNotExist() {
//                                               //Toast.makeText(MyApp.getContext(), "User is not exist,please try again.", Toast.LENGTH_SHORT).show();
//                                               Log.d("TAG", "In addevent-->neweventfragment----> friendIsNotExist");
//
//                                           }
//
//                                       });
            }

        });
        // Inflate the layout for this fragment
        ImageButton contactsList = (ImageButton) view.findViewById(R.id.openFriendsList_NewEvent_Button);
        contactsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriendsList(view);
            }
        });
        return view;
    }

    private void showFriendsList(View view) {
        if (contactsOpen) {
            contactsOpen = false;
            nestedScrollView.setVisibility(View.GONE);
        } else {
            contactsOpen = true;
            nestedScrollView.setVisibility(View.VISIBLE);
            friendsList = new LinkedList<>();
            SimpleFriendsAdapter adapter = new SimpleFriendsAdapter();
            ListView friendsListView = (ListView) view.findViewById(R.id.friendsListView_newEvent);
            friendsListView.setAdapter(adapter);
            mListener.initFriendsList(new Observer<List<User>>() {
                @Override
                public void onChanged(List<User> data) {
                    friendsList = data;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    //Adapter
    private class SimpleFriendsAdapter extends BaseAdapter {

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
            final int CHECKED_COLOR = 16777215;//white
            final int NOT_CHECKED_COLOR = 9300220;//kind of blue

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.friend_row_simple_new_event, viewGroup, false);
            }

            User friend = friendsList.get(i);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox_friendRow_newEvent);
            checkBox.setVisibility(View.INVISIBLE);
            if (usersInvitesEmail.contains(friend.getEmail())) {
                checkBox.setChecked(true);
                view.setBackgroundResource(CHECKED_COLOR);
            } else {
                checkBox.setChecked(false);
                view.setBackgroundResource(NOT_CHECKED_COLOR);
            }


            //Friend Details - onClick
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String fullName = friend.getFirstName() + " " + friend.getLastName();
                    if (checkBox.isChecked()) {//if check => do unchecked
                        v.setBackgroundResource(NOT_CHECKED_COLOR);
                        checkBox.setChecked(false);
                        usersInvitesEmail.remove(friend);
                        usersInvitesNames.remove(fullName);
                        _invites.setText("Participats:" + ProductTypeConverters.GenerateStringFromList(usersInvitesNames));
                    } else {
                        v.setBackgroundResource(CHECKED_COLOR);
                        checkBox.setChecked(true);
                        usersInvitesEmail.add(friend.getEmail());
                        usersInvitesNames.add(fullName);
                        _invites.setText("Participats:" + ProductTypeConverters.GenerateStringFromList(usersInvitesNames));
                    }
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
    //End Of Adapter

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewEventInteraction) {
            mListener = (NewEventInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewEventInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


//    private void sendInvites(String eventId) {
//        Log.d("TAG", "usrinvites=" + usersInvitesEmail);
//        // String invites = usersInvitesEmail;
//        //String[] items = invites.split(",");
////        for (String item : items) {
////            Invite invite = new Invite(eventId, item, "" + userId);
////            Repository.instance.addNewInvite(invite, new CloudManager.CloudManagerCallback<Invite>() {
////                @Override
////                public void onComplete(Invite invite) {
////                    Log.d("TAG", "succeed adding new invite.");
////                }
////
////                @Override
////                public void onCancel() {
////                }
////            });
////        }
//    }

    private void InviteTextViewEdit(View view) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                    try {
                thereIsParti = true;
                if (_invites == null)
                    _invites = view.findViewById(R.id.newEvent_Invites);

                _invites.setText("Participants: " + ProductTypeConverters.GenerateStringFromList(usersInvitesNames));
//                    } catch (Exception e) {
//                        Log.d("TAg", e.getMessage());
//                    }
            }

        });

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_pdf:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_txt:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }
}
