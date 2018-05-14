package com.example.malicteam.projectxclient.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
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
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FirebaseModel {

    //Data Members
    private static User _currentUser = null;
    private static FirebaseStorage _storage = FirebaseStorage.getInstance();


    //Firebase Methods
    //Users
    public static void getUserAndObserve(String id, final CloudManager.CloudCallback<User> cloudCallback) {
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
                    friends = ProductTypeConverters.toList(friendsString);
                List<Integer> events = new LinkedList<Integer>();
                if (value.get("EventsList") != null)
                    events = ProductTypeConverters.toList((String) value.get("EventsList"));

                _currentUser = (new User(firstName, lastName, phone, email, friends, events, pictureUrl));
                cloudCallback.onComplete(_currentUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                cloudCallback.onCancel();
            }
        });
    }

    public static void getSomeUserAndObserve(String id, final CloudManager.CloudCallback<User> cloudCallback) {
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
                    friends = ProductTypeConverters.toList(friendsString);
                List<Integer> events = new LinkedList<Integer>();
                if (value.get("EventsList") != null)
                    events = ProductTypeConverters.toList((String) value.get("EventsList"));

                user = (new User(firstName, lastName, phone, email, friends, events, pictureUrl));
                cloudCallback.onComplete(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                cloudCallback.onCancel();
            }
        });
    }

    public static void addUser(User user, CloudManager.CloudCallback<User> cloudCallback) {
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
            value.put("FriendsList", ProductTypeConverters.toString(user.getFriendsIdsAsList()));
            value.put("EventsList", ProductTypeConverters.toString(user.getEventsIdsAsList()));
            value.put("admin", false);

            myRef.setValue(value);

            cloudCallback.onComplete(user);
        } catch (Exception e) {
            cloudCallback.onCancel();
        }
    }

    public static void setRecordingStatus(String eventId, CloudManager.CloudCallback<Boolean> cloudCallback) {
        try {
            Log.d("TAG", "In setrecordingstatus func");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Events").child(eventId).child("RecordingStatus");
            Map<String, Object> value = new HashMap<>();
            myRef.setValue("false");
            cloudCallback.onComplete(false);
        } catch (Exception e) {
            cloudCallback.onCancel();
        }
    }

    /*
    remove current user - from DB & Google Auth
     */
    public static void removeAccount(final CloudManager.CloudCallback<Boolean> cloudCallback) {
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
                                cloudCallback.onComplete(true);
                            } else
                                cloudCallback.onCancel();
                        }
                    });
        }
    }

    public static void removeInvite(final CloudManager.CloudCallback<Boolean> callback, Invite invite) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        boolean success = false;
        //Delete from DB
        DatabaseReference myRef = database.getReference("Events").child("invites").child("" + invite.getuserId());
        //Delete picture from storage
        myRef.removeValue();
        callback.onComplete(null);
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
    public static void setPictureUrl(int id, Bitmap bitmap, final CloudManager.CloudCallback<Boolean> cloudCallback) {
        saveImage(bitmap, id, new Model.SaveImageListener() {
            @Override
            public void complete(String url) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users").child(Integer.toString(id)).child("PictureUrl");
                myRef.setValue(url);
                cloudCallback.onComplete(true);
            }

            @Override
            public void fail() {
                cloudCallback.onCancel();
            }
        });


    }

    public static void setFriends(int userId, List<User> friends, final CloudManager.CloudCallback callback) {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(Integer.toString(userId)).child("FriendsList");
            LinkedList<Integer> ids = new LinkedList<>();
            for (User u : friends) {
                ids.add(u.getId());
            }
            myRef.setValue(ProductTypeConverters.toString(ids));
            callback.onComplete(true);
        } catch (Exception e) {
            callback.onCancel();
        }
    }

    public static void setFriends(int userId, String friends, final CloudManager.CloudCallback<Boolean> callback) {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(Integer.toString(userId)).child("FriendsList");
            myRef.setValue(friends);
            callback.onComplete(true);
        } catch (Exception e) {
            callback.onCancel();
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

    public static void getEvents(int userId, final CloudManager.CloudCallback<List<Event>> cloudCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idsListRef = database.getReference("Users").child(Integer.toString(userId)).child("EventsList");
        final List<Integer>[] ids = new List[]{new LinkedList<Integer>()};
        idsListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    ids[0] = new LinkedList<Integer>();
                else {
                    ids[0] = ProductTypeConverters.toList((String) dataSnapshot.getValue());

                    DatabaseReference myRef = database.getReference("Events");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Event> finalList = new LinkedList<>();
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                Map<String, Object> value = (Map<String, Object>) snap.getValue();
                                int id = 0;
                                try {
                                    id = Integer.parseInt(snap.getKey());
                                }catch (Exception e){
                                    //this is invites
                                }
                                if (ids[0] != null && ids[0].size() > 0 && ids[0].contains(id)) {
                                    String eventName = (String) value.get("Title");
                                    String desc = (String) value.get("Description");
                                    String admin = (String) value.get("adminId");
                                    String Date = (String) value.get("Date");
                                    String usersList = (String) value.get("UsersList");

                                    String RecordingStatus = (String) value.get("RecordingStatus");

                                    finalList.add(new Event(null, eventName, usersList, desc, admin, Date, id, null));
                                }
                            }
                            cloudCallback.onComplete(finalList);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            cloudCallback.onCancel();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                cloudCallback.onCancel();
            }
        });
    }


    public static void getFriends(int userId, final CloudManager.CloudCallback<List<User>> cloudCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idsListRef = database.getReference("Users").child(Integer.toString(userId)).child("FriendsList");
        final List<Integer>[] ids = new List[]{new LinkedList<Integer>()};
        idsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    ids[0] = new LinkedList<Integer>();
                else {
                    ids[0] = ProductTypeConverters.toList((String) dataSnapshot.getValue());

                    DatabaseReference myRef = database.getReference("Users");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                        friends = ProductTypeConverters.toList((String) value.get("FriendsList"));
                                    List<Integer> events = new LinkedList<Integer>();
                                    if (value.get("EventsList") != null)
                                        events = ProductTypeConverters.toList((String) value.get("EventsList"));

                                    finalList.add(new User(firstName, lastName, phone, email, friends, events, pictureUrl));
                                }
                            }
                            cloudCallback.onComplete(finalList);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            cloudCallback.onCancel();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                cloudCallback.onComplete(null);
            }
        });

    }

    public static void getUserById(int userId, final CloudManager.CloudCallback<List<User>> callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idsListRef = database.getReference("Users").child(Integer.toString(userId));
        List<User> userList = new LinkedList<>();
        idsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                String firstName = (String) value.get("FirstName");
                String lastName = (String) value.get("LastName");
                String phone = (String) value.get("Phone");
                String email = (String) value.get("Mail");
                String pictureUrl = (String) value.get("PictureUrl");
                userList.add(new User(firstName, lastName, phone, email, null, null, pictureUrl));
                callback.onComplete(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });
    }

    public static void getEventById(int eventId, final CloudManager.CloudCallback<List<Event>> callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idsListRef = database.getReference("Events").child(Integer.toString(eventId));
        List<Event> userList = new LinkedList<>();
        idsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                String eventId = (String) value.get("ID");
                String eventName = (String) value.get("Title");
                String desc = (String) value.get("Description");
                String admin = (String) value.get("adminId");
                String Date = (String) value.get("Date");
                String usersList = (String) value.get("UsersList");
                Event event = new Event(null, eventName, usersList, desc, admin, Date, eventId, null);
                String RecordingStatus = (String) value.get("RecordingStatus");
                userList.add(event);
                callback.onComplete(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });
    }

    public static void getEventRecordingStatus(int eventId, final CloudManager.CloudCallback<List<Boolean>> callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idsListRef = database.getReference("Events").child(Integer.toString(eventId));
        List<Boolean> checkStatus = new LinkedList<>();
        idsListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                String recordingStatus = (String) value.get("RecordingStatus");
                if (recordingStatus.equals("false")) {
                    checkStatus.add(false);
                    callback.onComplete(checkStatus);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onComplete(null);
            }
        });
    }

    public static void isExistUser(int id, final CloudManager.CloudCallback cloudCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(Integer.toString(id));

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    cloudCallback.onComplete(id);
                else
                    cloudCallback.onComplete(-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                cloudCallback.onComplete(null);
            }
        });

    }
    //END of Users

    /*
    Get url & cloudCallback return Image from Firebase.
     */
    public static void getImage(String url, final CloudManager.CloudCallback<Bitmap> cloudCallback) {
        if (url != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference httpsReference = storage.getReferenceFromUrl(url);
            final long ONE_MEGABYTE = 1024 * 1024;
            httpsReference.getBytes(3 * ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    try {
                        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        cloudCallback.onComplete(image);
                    } catch (Exception e) {
                        Log.d("tag", "asdf");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    cloudCallback.onComplete(null);
                }
            });
        }
    }

    public static void addInvite(Invite invite) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events").child("invites").child(invite.getuserId());
        Map<String, Object> value = new HashMap<>();
        value.put("UserId", invite.getuserId());
        value.put("EventId", invite.getEventId());
        // Log.d("TAG", "in addinvite--->new eventactivity---invitefrom=" + invite.getInviteFromId());
        value.put("InviteFromId", invite.getInviteFromId());
        myRef.setValue(value);
    }

    public interface GetInvitation {
        void onComplete(Invite invite);
    }

    public static void getInvite(String id, GetInvitation listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events").child("invites").child(id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //  Invite invite = dataSnapshot.getValue(Invite.class);
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                if (dataSnapshot.getValue() != null) {
                    String userId = (String) value.get("UserId");
                    String eventId = (String) value.get("EventId");
                    String inviteFrom = (String) value.get("InviteFromId");
                    Log.d("TAG", "YOU got an INVITATION!!!" + userId + "eventid" + eventId + "From," + inviteFrom); // here to put INVATION FUNC
                    Invite invite = new Invite(eventId, userId, inviteFrom);
                    listener.onComplete(invite);


                    //  Invitation(invite);
                }


//                if (email.equals(Myemail))
                //         System.out.println("you got a new invitation");


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    //Events
    public static void addNewEvent(Event event) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events").child(Integer.toString(event.getId()));

        Map<String, Object> value = new HashMap<>();
        value.put("ID", Integer.toString(event.getId()));
        value.put("Title", event.getTitle());
        value.put("Description", event.getDescription());
        value.put("Date", event.getDate());
        // value.put("ContentUrl", event.getContentUrl());
        value.put("AdminId", event.getAdminId());
        value.put("UsersList", event.getParticipats());
        if (event.isRecording() == true) {
            value.put("RecordingStatus", "true");

        } else
            value.put("RecordingStatus", "false");

        myRef.setValue(value);
    }


    public static void getEventsAndObserve(int userId, final CloudManager.CloudCallback<List<Event>> cloudCallback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idsListRef = database.getReference("Users").child(Integer.toString(userId)).child("EventsList");
        final List<Integer>[] ids = new List[]{new LinkedList<Integer>()};
        idsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    ids[0] = new LinkedList<Integer>();
                else {
                    ids[0] = ProductTypeConverters.toList((String) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                cloudCallback.onComplete(null);
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
                            users = ProductTypeConverters.toList((String) value.get("UsersList"));
                        events.add(new Event(contentUrl, title, ProductTypeConverters.toString(users), description, "" + adminId, "" + date, id, null));
                    }
                }
                cloudCallback.onComplete(events);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                cloudCallback.onComplete(null);
            }
        });
    }

    public void addUserToEvent(int userId, int eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events").child(Integer.toString(eventId)).child("Users");

        //TODO add user to event
    }


//    //Help Methods
//    public static LinkedList<Integer> decodeListFromString(String list) {
//        list = list.replace("{", "").replace("}", "").replace(" ", "");
//        String[] ids = list.split(",");
//        LinkedList<Integer> finalList = new LinkedList<>();
//        for (String s : ids) {
//            if (s != null && s != "")
//                finalList.add(Integer.parseInt(s));
//        }
//        return finalList;
//    }
//
//    public static String generateStringFromList(List<Integer> list) {
//        String str = "{ ";
//        for (int id : list) {
//            str += id + ", ";
//        }
//        str += "}";
//        return str;
//    }

    public static void setEventList(User user, CloudManager.CloudCallback<Boolean> callback) {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(Integer.toString(user.getId())).child("EventsList");
            myRef.setValue(ProductTypeConverters.toString(user.getEventsIdsAsList()));
            callback.onComplete(true);
        } catch (Exception e) {
            callback.onCancel();
        }
    }


    public static void saveRecord(String Path, String eventId, final Model.SaveAudioListener listener, final CloudManager.CloudCallback callback) {
        StorageReference storageRef = _storage.getReference("Record").child(eventId);
    }

    public static void saveRecord(String userId, String Path, String eventId, final Model.SaveAudioListener listener, CloudManager.CloudCallback callback) {
        StorageReference storageRef = _storage.getReference("Record").child(eventId).child(userId);
        // File or Blob
        Uri file;
        file = Uri.fromFile(new File(Path));

// Create the file metadata
        StorageMetadata metadata;
        metadata = new StorageMetadata.Builder()
                .setContentType("audio")
                .build();

// Upload file and metadata to the path 'audio/audio.mp3'

        UploadTask uploadTask;
        uploadTask = storageRef.child("audio/" + file.getLastPathSegment()).putFile(file, metadata);

// Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                listener.fail();
                callback.onComplete(null);
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                listener.complete(downloadUrl.toString());
                callback.onComplete(null);
            }
        });
    }


}
