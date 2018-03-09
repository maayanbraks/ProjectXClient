package Model;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Maayan on 09-Mar-18.
 */

public class FirebaseModel implements IFirebaseModel {

    @Override
    public void addUser(User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(Integer.toString(user.getId()));

        Map<String, Object> value = new HashMap<>();
        value.put("FirstName", user.getFirstName());
        value.put("ID", user.getId());
        value.put("JoinDate", user.getJoinDate());
        value.put("LastName", user.getLastName());
        value.put("Mail", user.getEmail());
        value.put("Phone", user.getPhoneNumber());
        value.put("admin", false);

        myRef.setValue(value);
    }

    @Override
    public void addNewEvent(Event event) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events").child(Integer.toString(event.getId()));

        Map<String, Object> value = new HashMap<>();
        value.put("ID", event.getId());
        value.put("Users", event.getUsersIds());
        value.put("Title", event.getTitle());
        value.put("Description", event.getDescription());
        value.put("Date", event.getDate());
        value.put("Content", event.getContent());
        value.put("AdminId", event.getAdminId());

        myRef.setValue(value);
    }

    public void addUserToEvent(int userId, int eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events").child(Integer.toString(eventId)).child("Users");

        //TODO add user to event
    }

    public void addInvite(Invite invite) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events").child("invites").child(invite.getEmail().replace(".", ""));
        Map<String, Object> value = new HashMap<>();
        value.put("Email", invite.getEmail());
        value.put("ID", invite.getEventId());
        Log.d("TAG", "in addinvite--->new eventactivity---invitefrom=" + invite.getInviteFrom());
        value.put("InviteFrom", invite.getInviteFrom());
        myRef.setValue(value);
        //Log.d("TAG","in adduser command"+user.email);
        // myRef.setValue(user);
    }

}
