package com.example.malicteam.projectxclient.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class FirebaseModel {

    //Data Members
    private static User _currentUser = null;
    private static FirebaseStorage _storage = FirebaseStorage.getInstance();


    //Interfaces
    public interface Callback<T> {
        void onComplete(T data);
    }

    //Firebase Methods
    //Users
    public static void LoggedInUserAndObserve(String id, final Callback<User> callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(id);

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
                String friendsString = (String) value.get("FriendsList");
                if (friendsString != null)
                    friends = decodeListFromString(friendsString);
                List<Integer> events = new LinkedList<Integer>();
                if (value.get("EventsList") != null)
                    events = decodeListFromString((String) value.get("EventsList"));

                _currentUser = (new User(firstName, lastName, phone, email, friends, events, pictureUrl));
                callback.onComplete(_currentUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });
    }
    public static void getSomeUserAndObserve(String id, final Callback<User> callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(id);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            User user = null;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                String firstName = (String) value.get("FirstName");
                String lastName = (String) value.get("LastName");
                String phone = (String) value.get("Phone");
                String email = (String) value.get("Mail");
                String pictureUrl = (String) value.get("PictureUrl");
                List<Integer> friends = new LinkedList<Integer>();
                String friendsString = (String) value.get("FriendsList");
                if (friendsString != null)
                    friends = decodeListFromString(friendsString);
                List<Integer> events = new LinkedList<Integer>();
                if (value.get("EventsList") != null)
                    events = decodeListFromString((String) value.get("EventsList"));

                user = (new User(firstName, lastName, phone, email, friends, events, pictureUrl));
                callback.onComplete(_currentUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });
    }
    public static void addUser(User user, Callback<User> callback) {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(Integer.toString(user.getId()));

            Map<String, Object> value = new HashMap<>();
            value.put("FirstName", user.getFirstName());
            value.put("ID", user.getId());
            value.put("lastLogin", user.getLastLogin());
            value.put("LastName", user.getLastName());
            value.put("Mail", user.getEmail());
            value.put("Phone", user.getPhoneNumber());
            value.put("PictureUrl", user.getPictureUrl());
            value.put("FriendsList", generateStringFromList(user.getFriendsIds()));
            value.put("EventsList", generateStringFromList(user.getEventsIds()));
            value.put("admin", false);

            myRef.setValue(value);

            callback.onComplete(user);
        } catch (Exception e) {
            callback.onComplete(null);
        }
    }

    /*
    remove current user - from DB & Google Auth
     */
    public static void removeAccount(final Callback<Boolean> callback) {
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
                                callback.onComplete(true);
                            } else
                                callback.onComplete(null);
                        }
                    });
        }
    }

    //User Setters
    public static void setFirstName(int id, String name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(Integer.toString(id)).child("FirstName");
        myRef.setValue(name);
    }

    public static void setLastName(int id, String name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(Integer.toString(id)).child("LastName");
        myRef.setValue(name);
    }

    public static void setEmail(int id, String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(Integer.toString(id)).child("Mail");
        myRef.setValue(email);
    }
    public static void setPhone(int id, String phone) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(Integer.toString(id)).child("Phone");
        myRef.setValue(phone);
    }
    /*
    get Picture and change the profile picture of user <id>
     */
    public static void setPictureUrl(int id, Bitmap bitmap, Callback<Boolean> callback){
        saveImage(bitmap, id, new Model.SaveImageListener() {
            @Override
            public void complete(String url) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users").child(Integer.toString(id)).child("PictureUrl");
                myRef.setValue(url);
                callback.onComplete(true);
            }

            @Override
            public void fail() {
                callback.onComplete(false);
            }
        });


    }

    public static void setFriends(int userId, List<User> friends, Repository.AddFriendsListener listener) {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(Integer.toString(userId)).child("FriendsList");
            LinkedList<Integer> ids = new LinkedList<>();
            for (User u : friends) {
                ids.add(u.getId());
            }
            myRef.setValue(generateStringFromList(ids));
            listener.onSuccess();
        } catch (Exception e) {
            listener.onFail("There is a problem. Please try later.\n(error 638)");
        }
    }
    public static void setFriends(int userId, String friends, Repository.AddFriendsListener listener) {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(Integer.toString(userId)).child("FriendsList");
            myRef.setValue(friends);
            listener.onSuccess();
        } catch (Exception e) {
            listener.onFail("There is a probleb. Please try later.\n(error 9876)");
        }
    }

    /*
    save Profile Picture - Default save image as <UserID>
     */
    public static void saveImage(Bitmap imageBmp, int userId, final Model.SaveImageListener listener) {

        StorageReference imagesRef = _storage.getReference().child("Images").child("ProfilePictures").child(Integer.toString(userId));
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
                Log.d("profilePicture", "onSuccess: Profile picture saved on firebase");
            }
        });
    }






    public static void getFriends(int userId, final Callback<List<User>> callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idsListRef = database.getReference("Users").child(Integer.toString(userId)).child("FriendsList");
        final List<Integer>[] ids = new List[]{new LinkedList<Integer>()};
        idsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    ids[0] = new LinkedList<Integer>();
                else {
                    ids[0] = decodeListFromString((String) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });

        DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> finalList = new LinkedList<>();


                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Map<String, Object> value = (Map<String, Object>) snap.getValue();
                    int id = Integer.parseInt(snap.getKey());

                    if (ids[0] != null && ids[0].size() > 0 && ids[0].contains(id)) {
                        String firstName = (String) value.get("FirstName");
                        String lastName = (String) value.get("LastName");
                        String phone = (String) value.get("Phone");
                        String email = (String) value.get("Mail");
                        String pictureUrl = (String) value.get("PictureUrl");
                        //----WE dont need it for now----
                        List<Integer> friends = new LinkedList<Integer>();
                        if (value.get("FriendsList") != null)
                            friends = decodeListFromString((String) value.get("FriendsList"));
                        List<Integer> events = new LinkedList<Integer>();
                        if (value.get("EventsList") != null)
                            events = decodeListFromString((String) value.get("EventsList"));

                        finalList.add(new User(firstName, lastName, phone, email, friends, events, pictureUrl));
                    }
                }
                callback.onComplete(finalList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });
    }

    public static void isExistUser(int id, Callback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(Integer.toString(id));

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    callback.onComplete(id);
                else
                    callback.onComplete(-1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });

    }
    //END of Users

    /*
    Get url & callback return Image from Firebase.
     */
    public static void getImage(String url, Callback<Bitmap> callback) {
        if (url != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference httpsReference = storage.getReferenceFromUrl(url);
            final long ONE_MEGABYTE = 1024 * 1024;
            httpsReference.getBytes(3 * ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    try {
                        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        callback.onComplete(image);
                    }catch (Exception e){
                        Log.d("tag", "asdf");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    callback.onComplete(null);
                }
            });
        }
    }

    //Events
    public static void addNewEvent(Event event) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events").child(Integer.toString(event.getId()));

        Map<String, Object> value = new HashMap<>();
        value.put("ID", event.getId());
        value.put("Title", event.getTitle());
        value.put("Description", event.getDescription());
        value.put("Date", event.getDate());
        value.put("ContentUrl", event.getContentUrl());
        value.put("AdminId", event.getAdminId());
        value.put("UsersList", generateStringFromList(event.getUsersIds()));
        myRef.setValue(value);
    }


    public static void getEventsAndObserve(int userId, final Callback<List<Event>> callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idsListRef = database.getReference("Users").child(Integer.toString(userId)).child("EventsList");
        final List<Integer>[] ids = new List[]{new LinkedList<Integer>()};
        idsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    ids[0] = new LinkedList<Integer>();
                else {
                    ids[0] = decodeListFromString((String) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });

        DatabaseReference myRef = database.getReference("Events");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Event> events = new LinkedList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Map<String, Object> value = (Map<String, Object>) snap.getValue();
                    int id = (int) value.get("ID");
                    if (ids[0].contains(id)) {
                        String title = (String) value.get("Title");
                        String description = (String) value.get("Description");
                        Date date = (Date) value.get("Date");
                        String contentUrl = (String) value.get("ContentUrl");
                        int adminId = (int) value.get("AdminId");
                        List<Integer> users = new LinkedList<Integer>();
                        if (value.get("UsersList") != null)
                            users = decodeListFromString((String) value.get("UsersList"));
                        events.add(new Event(contentUrl, title, users, description, adminId, date));
                    }
                }
                callback.onComplete(events);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });
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




    //Help Methods
    public static LinkedList<Integer> decodeListFromString(String list) {
        list = list.replace("{", "").replace("}", "").replace(" ", "");
        String[] ids = list.split(",");
        LinkedList<Integer> finalList = new LinkedList<>();
        for (String s : ids) {
            if (s != null && s != "")
                finalList.add(Integer.parseInt(s));
        }
        return finalList;
    }

    public static String generateStringFromList(List<Integer> list) {
        String str = "{ ";
        for (int id : list) {
            str += id + ", ";
        }
        str += "}";
        return str;
    }
}
