package com.example.malicteam.projectxclient.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Activity.RecordingActivity;
import com.example.malicteam.projectxclient.Common.Callbacks.AddEventCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.isUserExistResponeCallback;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Common.MyApp;
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

import ResponsesEntitys.UserData;

public class NewEventFragment extends Fragment {
    private NewEventInteraction mListener;
    private int userId;
    private UserViewModel currentUser = null;
    private List<String> UsersInvites;
    private Button startRecord;
    private ImageButton fab;
    private Event event;
    private User myUser;
    private String invitedPpl;
    private boolean thereIsParti = false;

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
        UsersInvites = new LinkedList<String>();
        invitedPpl = new String(" ");

        userId = getArguments().getInt(Consts.USER_ID, Consts.DEFAULT_UID);
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
        RadioGroup _saveAs = view.findViewById(R.id.save_as_group);
        _saveAs.check(R.id.radio_pdf);
        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                EditText _name = view.findViewById(R.id.new_event_title);
                EditText _desc = view.findViewById(R.id.new_event_description);
                EditText _part = view.findViewById(R.id.new_event_participants);
                TextView _invites = view.findViewById(R.id.newEvent_Invites);
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
                event = new Event(null, title, null, description, "" + userId, dateFormat, null);
                List<User> participats = new LinkedList<>();
                participats.add(myUser);
                UsersInvites.add(myUser.getEmail());
                event.setParticipats(participats);
//                    sendInvites("" + event.getId());
                //Repository.instance.addEvent(event);
                //UsersInvites.add(myUser.getEmail());
                // event.addToParticipats(myUser);
                Repository.instance.addEvent(UsersInvites, event, new AddEventCallback<Boolean>() {
                    @Override
                    public void onSuccees(Boolean data) {
                        if (data) {
                            mListener.startRecording(event);
                        }
                        //   try {
                        //  currentUser.getUser().getValue().addEventToList(Integer.valueOf(event.getId()));
                        //update the userDatabase
//                                Repository.instance.setEventList(currentUser.getUser().getValue(), new CloudManager.CloudCallback() {
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

                    }

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
                                       //TODO
                                       //check with server is userExist---->if do --->
                                       mListener.isUserExist(parti, new AddEventCallback<String>() {
                                           @Override
                                           public void onSuccees(String data) {
                                               if (data.equals(myUser.getEmail()))
                                               {
                                                   Log.d("TAG","You cant add yourself to event");
                                               } else if(invitedPpl.equals(" ")) {
                                                   invitedPpl = data;
                                                   UsersInvites.add(parti);
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
        return view;
    }


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


    public interface NewEventInteraction extends BasicInteractionInterface {
        // TODO: Update argument type and name
        void isUserExist(String parti, AddEventCallback<String> callback);
        void startRecording(Event event);
    }

    private void sendInvites(String eventId) {
        Log.d("TAG", "usrinvites=" + UsersInvites);
        // String invites = UsersInvites;
        //String[] items = invites.split(",");
//        for (String item : items) {
//            Invite invite = new Invite(eventId, item, "" + userId);
//            Repository.instance.addNewInvite(invite, new CloudManager.CloudCallback<Invite>() {
//                @Override
//                public void onComplete(Invite invite) {
//                    Log.d("TAG", "succeed adding new invite.");
//                }
//
//                @Override
//                public void onCancel() {
//                }
//            });
//        }
    }

    private void InviteTextViewEdit(View view) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

//                    try {
                thereIsParti = true;
                TextView _invites = view.findViewById(R.id.newEvent_Invites);

                _invites.setText("Participats:" + invitedPpl);
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
