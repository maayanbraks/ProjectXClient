package Model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.malicteam.projectxclient.LoginActivity;
import com.example.malicteam.projectxclient.MainActivity;
import com.example.malicteam.projectxclient.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class FirebaseModel {

    private static FirebaseDatabase _database = FirebaseDatabase.getInstance();;
    private static FirebaseAuth _auth = FirebaseAuth.getInstance();;
    private static MainActivity _activity;
    private static User _currentUser = null;


    public FirebaseModel(MainActivity activity) {
        //this._database =
        this._activity = activity;
//        this._auth =
        updateCurrentUser();
    }

    public static void addUser(User user) {
        DatabaseReference myRef = _database.getReference("Users").child(Integer.toString(user.getId()));

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

    public void addNewEvent(Event event) {
        DatabaseReference myRef = _database.getReference("Events").child(Integer.toString(event.getId()));

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
        DatabaseReference myRef = _database.getReference("Events").child(Integer.toString(eventId)).child("Users");

        //TODO add user to event
    }

    public void addInvite(Invite invite) {
        DatabaseReference myRef = _database.getReference("Events").child("invites").child(invite.getEmail().replace(".", ""));
        Map<String, Object> value = new HashMap<>();
        value.put("Email", invite.getEmail());
        value.put("ID", invite.getEventId());
        Log.d("TAG", "in addinvite--->new eventactivity---invitefrom=" + invite.getInviteFrom());
        value.put("InviteFrom", invite.getInviteFrom());
        myRef.setValue(value);
        //Log.d("TAG","in adduser command"+user.email);
        // myRef.setValue(user);
    }

    public static void removeAccount() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean success = false;
        if (user != null) {
            //Delete from DB
            DatabaseReference myRef = database.getReference("Users").child(Integer.toString(User.generateId(user.getEmail())));
            //delete from auth (first signout)
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                myRef.removeValue();
                                //Toast.makeText(, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        updateCurrentUser();
    }

    //Setters in DB
    public static void setFirstName(String name){
        DatabaseReference myRef = _database.getReference("Users").child(Integer.toString(_currentUser.getId())).child("FirstName");
        myRef.setValue(name);
    }

    public static void setLastName(String name){
        DatabaseReference myRef = _database.getReference("Users").child(Integer.toString(_currentUser.getId())).child("LastName");
        myRef.setValue(name);
    }

    public static void setEmail(String email){
        DatabaseReference myRef = _database.getReference("Users").child(Integer.toString(_currentUser.getId())).child("Mail");
        myRef.setValue(email);
    }

    public static void setPhone(String phone){
        DatabaseReference myRef = _database.getReference("Users").child(Integer.toString(_currentUser.getId())).child("Phone");
        myRef.setValue(phone);
    }


    public static void getUserById(final String id, final GetUserByIdListener listener) {
        DatabaseReference myRef = _database.getReference("Users").child(id);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                String firstName = (String) value.get("FirstName");
                String lastName = (String) value.get("LastName");
                String phone = (String) value.get("Phone");
                String email = (String) value.get("Mail");
                listener.onComplete(new User(firstName, lastName, phone, email));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onComplete(null);
            }
        });
    }

    //the same but id comes as string
    public static void getUserById(final int id, final GetUserByIdListener listener) {
        getUserById(Integer.toString(id), listener);
    }

    public static void getUserByEmail(final String email, final GetUserByIdListener listener) {
        getUserById(User.generateId(email), listener);
    }

    public static void updateCurrentUser() {
        FirebaseUser firebaseUser = _auth.getCurrentUser();
        if (firebaseUser == null) {
            _currentUser = null;
            _activity.setCurrentUser(null);
        }

        else {
            getUserByEmail(firebaseUser.getEmail(), new FirebaseModel.GetUserByIdListener() {
                @Override
                public void onComplete(User user) {
                    //currentUser = new User(user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getEmail());
                    _currentUser = new User(user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getEmail());
                    _activity.setCurrentUser(_currentUser);
                }
            });
        }
    }

    public interface GetUserByIdListener {
        void onComplete(User user);
    }



    private void showToast(String msg) {
        Toast.makeText(_activity, msg, Toast.LENGTH_LONG).show();
    }
}
