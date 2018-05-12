package com.example.malicteam.projectxclient.View;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Invite;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class NewEventFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private int userId;
    private UserViewModel currentUser = null;
    String UsersInvites;
    private Button startRecord;
    private ImageButton fab;
    private Event event;
    private String invitedPpl;
    boolean thereIsParti = false;

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
        UsersInvites = " ";
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

                if (title == null || title.equals("")) {
                    Toast.makeText(MyApp.getContext(), "Enter Title", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                else if (description == null || description.equals("")) {
                    Toast.makeText(MyApp.getContext(), "Enter Descriptio", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                else if (!thereIsParti){
                    Toast.makeText(MyApp.getContext(), "You cant Record alone!", Toast.LENGTH_SHORT).show();
                    valid = false;
                }

                else if(valid) {
//                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//                    Date date = new Date();
                    String dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
                    event = new Event(null, title, UsersInvites, description, "" + userId, dateFormat, null);
                    sendInvites("" + event.getId());
                    Repository.instance.addEvent(event);
                    Intent intent = new Intent(MyApp.getContext(), RecordingActivity.class);
                    intent.putExtra("sendNewEvent", event);
                    intent.putExtra("currectuser", userId);
                    int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    intent.putExtra(Consts.USER_ID, id);

                    try {
                        currentUser.getUser().getValue().addEventToList(Integer.valueOf(event.getId()));
                        //update the userDatabase
                        Repository.instance.setEventList(currentUser.getUser().getValue(), new CloudManager.CloudCallback() {
                            @Override
                            public void onComplete(Object data) {
                                startActivity(intent);
                            }
                            @Override
                            public void onCancel() {

                            }
                        });
                    }catch (Exception e){
                        Log.d("sdf", "afdasdf");
                    }

                }
            }
        });

        fab = (ImageButton) view.findViewById(R.id.addInviteButton);
        fab.setOnClickListener(new View.OnClickListener() {
            User user;

            @Override
            public void onClick(View v) {
                String parti = _part.getText().toString();

                FirebaseModel.isExistUser(User.generateId(parti), new CloudManager.CloudCallback<Integer>() {
                    @Override
                    public void onComplete(Integer id) {
                        if (id > 0) {// if found user
                            Log.d("TAG", "Found id,");
                            Repository.instance.getUserById(id, new CloudManager.CloudCallback<List<User>>() {
                                @Override
                                public void onComplete(List<User> data) {
                                    Log.d("TAG", "data size=," + data.size());
                                    if (data.size() > 0)
                                        user = data.get(0);
                                    Toast.makeText(MyApp.getContext(), "Sending invite to" + user.getFirstName(), Toast.LENGTH_SHORT).show();
                                    if (invitedPpl.equals(" ")) { // if empty
                                        invitedPpl = user.getFirstName();
                                    } else {
                                        invitedPpl = invitedPpl + "," + user.getFirstName();
                                    }
                                    InviteTextViewEdit(view);

                                    if (UsersInvites.equals(" "))
                                        UsersInvites = user.getId() + "";
                                    else
                                        UsersInvites = UsersInvites + "," + user.getId();
                                }

                                @Override
                                public void onCancel() {

                                }
                            });


                        } else
                            Toast.makeText(MyApp.getContext(), "Cant find user," + parti, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void sendInvites(String eventId) {
        Log.d("TAG", "usrinvites=" + UsersInvites);
        String invites = UsersInvites;
        String[] items = invites.split(",");
        for (String item : items) {
            Invite invite = new Invite(eventId, item, "" + userId);
            Repository.instance.addNewInvite(invite, new CloudManager.CloudCallback<Invite>() {
                @Override
                public void onComplete(Invite invite) {
                    Log.d("TAG", "succeed adding new invite.");
                }
                @Override
                public void onCancel() {
                }
            });
        }
    }

    private void InviteTextViewEdit(View view) {
        thereIsParti = true;
        TextView _invites = view.findViewById(R.id.newEvent_Invites);

        _invites.setText("Participats:" + invitedPpl);
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
