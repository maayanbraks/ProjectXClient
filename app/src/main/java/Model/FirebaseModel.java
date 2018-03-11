package Model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.example.malicteam.projectxclient.LoginActivity;
import com.example.malicteam.projectxclient.MainActivity;
import com.example.malicteam.projectxclient.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class FirebaseModel {

    private static FirebaseDatabase _database = FirebaseDatabase.getInstance();
    ;
    private static FirebaseAuth _auth = FirebaseAuth.getInstance();
    ;
    private static MainActivity _activity;
    private static User _currentUser = null;
    private static FirebaseStorage _storage = FirebaseStorage.getInstance();


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
        value.put("PictureUrl", user.getPictureUrl());
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean success = false;
        if (user != null) {
            //Delete from DB
            DatabaseReference myRef = database.getReference("Users").child(Integer.toString(User.generateId(user.getEmail())));
            //Delete picture from storage
            if (_currentUser.getPictureUrl() != null) {
                StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(_currentUser.getPictureUrl());
                httpsReference.delete();
            }
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

    //Setters + Getters in DB


    public static void setFirstName(String name) {
        DatabaseReference myRef = _database.getReference("Users").child(Integer.toString(_currentUser.getId())).child("FirstName");
        myRef.setValue(name);
    }

    public static void setLastName(String name) {
        DatabaseReference myRef = _database.getReference("Users").child(Integer.toString(_currentUser.getId())).child("LastName");
        myRef.setValue(name);
    }

    public static void setEmail(String email) {
        DatabaseReference myRef = _database.getReference("Users").child(Integer.toString(_currentUser.getId())).child("Mail");
        myRef.setValue(email);
    }

    public static void setPhone(String phone) {
        DatabaseReference myRef = _database.getReference("Users").child(Integer.toString(_currentUser.getId())).child("Phone");
        myRef.setValue(phone);
    }

    public static void getProfilePicture(final Model.GetImageListener listener) {
        String url = _currentUser.getPictureUrl();
        if (url != null) {
            //TODO sizes of pictures & default picture
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference httpsReference = storage.getReferenceFromUrl(url);
            final long ONE_MEGABYTE = 1024 * 1024;
            httpsReference.getBytes(3 * ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    listener.onSuccess(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    Log.d("TAG", exception.getMessage());
                    listener.onFail();
                }
            });
        }
    }
    // End DB


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
                String pictureUrl = (String) value.get("PictureUrl");
                List<Integer> friends = new LinkedList<Integer>();
                friends = (LinkedList<Integer>) value.get("FriendsList");

                if (pictureUrl != null)
                    listener.onComplete(new User(firstName, lastName, phone, email, friends, pictureUrl));
                else
                    listener.onComplete(new User(firstName, lastName, phone, email, friends));
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
        } else {
            getUserByEmail(firebaseUser.getEmail(), new FirebaseModel.GetUserByIdListener() {
                @Override
                public void onComplete(User user) {
                    //currentUser = new User(user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getEmail());
                    _currentUser = new User(user.getFirstName(), user.getLastName(), user.getPhoneNumber(), user.getEmail(), user.getFriendsIds(), user.getPictureUrl());
                    _activity.setCurrentUser(_currentUser);
                }
            });
        }
    }

    public interface GetUserByIdListener {
        void onComplete(User user);
    }

    public static void saveImage(Bitmap imageBmp, int name, final Model.SaveImageListener listener) {

        StorageReference imagesRef = _storage.getReference().child("Images").child("ProfilePictures").child(Integer.toString(name));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.fail();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.complete(downloadUrl.toString());
            }
        });
    }

    public interface GetUserListener {
        void onComplete(List<User> UsersList);
    }


}
