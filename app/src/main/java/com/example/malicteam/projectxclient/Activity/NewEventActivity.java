//package com.example.malicteam.projectxclient.Activity;
//
//import android.content.Intent;
//import android.support.v4.app.FragmentActivity;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//
//import com.example.malicteam.projectxclient.Consts;
//import com.example.malicteam.projectxclient.Model.Repository;
//import com.example.malicteam.projectxclient.R;
//
//public class NewEventActivity extends AppCompatActivity {
//
//    private String saveAsString = "TXT";//DEFAULT
//    private Button startRecord;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_event);
//
//        startRecord = (Button) findViewById(R.id.new_event_start);
//        RadioGroup saveAs = findViewById(R.id.save_as_group);
//        saveAs.check(R.id.radio_pdf);
//
//        startRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText _name = findViewById(R.id.new_event_title);
//                EditText _desc = findViewById(R.id.new_event_description);
//                EditText _part = findViewById(R.id.new_event_participants);
//                String description = _desc.getText().toString();
//                String title = _name.getText().toString();
//                String parti = _part.getText().toString();
//
//                Intent intent = new Intent(NewEventActivity.this, RecordingActivity.class);
//                intent.putExtra(Consts.EVENT_TITLE, title);
//                intent.putExtra(Consts.EVENT_DESCRIPTION, parti);
//                intent.putExtra(Consts.EVENT_USERS, description);
//                intent.putExtra(Consts.SAVE_AS, saveAsString);
//
//                startActivity(intent);
//                finish();
//            }
//        });
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.logout_actionMenu) {
//            Repository.instance.logout();
//            startActivity(new Intent(NewEventActivity.this, LoginActivity.class));
//            finish();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    public void onRadioButtonClicked(View view) {
//        boolean checked = ((RadioButton) view).isChecked();
//
//        switch (view.getId()) {
//            case R.id.radio_pdf:
//                if (checked)
//                    saveAsString = Consts.TXT;//TODO - now its txt default
//                    break;
//            case R.id.radio_txt:
//                if (checked)
//                    saveAsString = Consts.TXT;
//                    break;
//        }
//    }
//}
package com.example.malicteam.projectxclient.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Invite;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;


import java.util.List;


public class NewEventActivity extends AppCompatActivity {
    private int userId;
    private UserViewModel currentUser = null;
    String UsersInvites;
    private Button startRecord;
    private ImageButton fab;
    private Event event;
    private String invitedPpl;
    boolean IsInEventinvites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UsersInvites = " ";
        invitedPpl = new String(" ");
        // _currectUser = (User) getIntent().getSerializableExtra("CurrectUser");
        event = new Event(null, null, null, null, null, null, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        userId = getIntent().getIntExtra(Consts.USER_ID, Consts.DEFAULT_UID);
        currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
        //currentUser.init(userId);
        startRecord = (Button) findViewById(R.id.new_event_start);
        EditText _name = findViewById(R.id.new_event_title);

        EditText _desc = findViewById(R.id.new_event_description);

        EditText _part = findViewById(R.id.new_event_participants);

        RadioGroup _saveAs = findViewById(R.id.save_as_group);
        _saveAs.check(R.id.radio_pdf);
        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText _name = findViewById(R.id.new_event_title);
                EditText _desc = findViewById(R.id.new_event_description);
                EditText _part = findViewById(R.id.new_event_participants);
                TextView _invites = findViewById(R.id.newEvent_Invites);
                String description = _desc.getText().toString();
                String title = _name.getText().toString();
                String parti = _part.getText().toString();

                Time time = new Time();
                time.setToNow();
//                event.setTitle(title);
//                event.setDescription(description);
//                event.setAdminId(_currectUser.getId());

//                event.setDate(time.hour + ":" + time.minute + ":" + time.second);
                event = new Event(null, title, UsersInvites, description, "" + userId, time.hour + ":" + time.minute + ":" + time.second, null);
                sendInvites("" + event.getId());
                Repository.instance.addEvent(event);
                Intent intent = new Intent(getApplicationContext(), RecordingActivity.class);
                intent.putExtra("sendNewEvent", event);
                intent.putExtra("currectuser", userId);
                int id = User.generateId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                intent.putExtra(Consts.USER_ID, id);
                startActivity(intent);
                finish();
            }
        });
        fab = (ImageButton)findViewById(R.id.addInviteButton);

        fab.setOnClickListener(new View.OnClickListener() {
            User user;
            @Override
            public void onClick(View v) {
                String parti = _part.getText().toString();

                FirebaseModel.isExistUser(User.generateId(parti), new FirebaseModel.FirebaseCallback<Integer>() {
                    @Override
                    public void onComplete(Integer id) {
                        if (id >0) {// if found user
                            Log.d("TAG","Found id,");
                            Repository.instance.getUserById(id, new FirebaseModel.FirebaseCallback<List<User>>() {
                                @Override
                                public void onComplete(List<User> data) {
                                    Log.d("TAG","data size=,"+data.size());
                                    if (data.size()>0)
                                   user=data.get(0);
                                    Toast.makeText(getApplication(), "Sending invite to" + user.getFirstName(), Toast.LENGTH_SHORT).show();
                                    if (invitedPpl.equals(" ")) { // if empty
                                        invitedPpl =  user.getFirstName();
                                    } else {
                                        invitedPpl = invitedPpl + "," +  user.getFirstName();
                                    }
                                    InviteTextViewEdit();

                                    if (UsersInvites.equals(" "))
                                        UsersInvites = user.getId() + "";
                                    else
                                        UsersInvites = UsersInvites + "," + user.getId();
                                }

                                @Override
                                public void onCancel() {

                                }
                            });




                        }
                        else
                            Toast.makeText(getApplication(), "Cant find user," + parti, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
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

    public void InviteTextViewEdit() {
        TextView _invites = findViewById(R.id.newEvent_Invites);

        _invites.setText("Participats:" + invitedPpl);
    }

    private static String generateFriendsList(List<Integer> list) {
        String str = "{ ";
        for (int id : list) {
            Log.d("TAG", "" + id);
            str += id + ", ";
        }
        str += "}";
        return str;
    }

    public void sendInvites(String eventId) {

        Log.d("TAG","usrinvites="+UsersInvites);
        String invites = UsersInvites;
        String[] items = invites.split(",");
        for (String item : items) {
            // Log.d("TAG","Myemail:"+Mymail);
            ///  Model.Invite invite= new Model.Invite(eventId,item,Mymail);
            ///     Model.Invite
            Invite invite= new Invite(eventId,item,""+userId);
            Repository.instance.addNewInvite(invite, new FirebaseModel.FirebaseCallback<Invite>() {
                @Override
                public void onComplete(Invite invite) {
                    Log.d("TAG","succeed adding new invite.");
                }

                @Override
                public void onCancel() {

                }
            });
            //  / //    invite = new Model.Invite(eventId, item, _currectUser.getEmail().replace("@", ""));
            ///
        }
    }
}


