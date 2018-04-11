package com.example.malicteam.projectxclient.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
/**
 * Created by Maayan on 11-Mar-18.
 */
//Singleton
public class Repository {
    private MutableLiveData<User> userLiveData;
    private List<MutableLiveData<User>> someUser;
    //private MutableLiveData<List<Event>> eventsData;
    //private MutableLiveData<List<User>> friendsLiveData;

    private List<User>friends = null;//holds local users

    public static final Repository instance = new Repository();
    private LocalStorageManager localStorage = new LocalStorageManager();



    public LiveData<User> getUser(int id) {
        synchronized (this) {
            if (userLiveData == null) {
                userLiveData = new MutableLiveData<User>();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseModel.getUserAndObserve(Integer.toString(id), new FirebaseModel.FirebaseCallback<User>() {
                    @Override
                    public void onComplete(User data) {
                        if (data != null)
                            userLiveData.setValue(data);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        }
        return userLiveData;
    }

    public LiveData<User> getSomeUser(int id) {
        synchronized (this) {
            //check if exist in our list
            if (someUser != null) {
                for (MutableLiveData<User> u : someUser) {
                    if (u.getValue().getId() == id)
                        return u;
                }
            } else // list is null
                someUser = new LinkedList<MutableLiveData<User>>();

            someUser.add(new MutableLiveData<>());

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseModel.getSomeUserAndObserve(Integer.toString(id), new FirebaseModel.FirebaseCallback<User>() {
                @Override
                public void onComplete(User data) {
                    someUser.get(someUser.size() - 1).setValue(data);
                }

                @Override
                public void onCancel() {
                }
            });

        }
        return someUser.get(someUser.size() - 1);
    }


    public void getUserById(int id, final FirebaseModel.FirebaseCallback<List<User>> callback) {

        FirebaseModel.getUserById(id, callback);

    }

    public void getEventById(int id, final FirebaseModel.FirebaseCallback<List<Event>> callback) {

        FirebaseModel.getEventById(id, callback);

    }
    public void getEventRecordingStatus(int id, FirebaseModel.FirebaseCallback<List<Boolean>> callback){

        FirebaseModel.getEventRecordingStatus(id,callback);
    }

    public void addFriend(String email, final FirebaseModel.FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int userId = User.generateId(auth.getCurrentUser().getEmail());
        //List<User> _friendsList = new LinkedList<>();

        FirebaseModel.getFriends(userId, new FirebaseModel.FirebaseCallback<List<User>>() {
            @Override
            public void onComplete(List<User> friendsList) {
                boolean found = false;
                if (friendsList != null) {//if there is friends
                    //first we check if it not exist
                    for (int i = 0; i < friendsList.size() && !found; i++) {
                        if (friendsList.get(i).getEmail().equals(email))
                            found = true;
                    }
                }
                //if it not exist
                if (!found) {
                    //check if it Signed user
                    FirebaseModel.isExistUser(User.generateId(email), new FirebaseModel.FirebaseCallback<Integer>() {
                        @Override
                        public void onComplete(Integer friendId) {//ok - go on
                            if (friendId != null && friendId > 0) {
                                LinkedList<Integer> newList = new LinkedList<>();
                                for (User u : friendsList) {//makes list of IDs
                                    newList.add(u.getId());
                                }
                                newList.add(friendId);
                                FirebaseModel.setFriends(userId, ProductTypeConverters.toString(newList), firebaseCallback);
                            } else
                                firebaseCallback.onCancel();
                        }

                        @Override
                        public void onCancel() {
                            firebaseCallback.onCancel();
                        }
                    });
                } else {
                    //already friend
                    firebaseCallback.onCancel();
                }
            }//END onComplete get friends

            @Override
            public void onCancel() {
                firebaseCallback.onCancel();
            }
        });
    }

    public void getFriends(int userId, final FirebaseModel.FirebaseCallback<List<User>> firebaseCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseModel.getFriends(userId, new FirebaseModel.FirebaseCallback<List<User>>() {
            @Override
            public void onComplete(List<User> data) {
                updateFriendsDataInLocalStorage(data);
                firebaseCallback.onComplete(data);
            }
            @Override
            public void onCancel() {
                firebaseCallback.onComplete(friends);
            }
        });
    }
    public void getEvents(int userId, FirebaseModel.FirebaseCallback<List<Event>> firebaseCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseModel.getEvents(userId, new FirebaseModel.FirebaseCallback<List<Event>>() {
            @Override
            public void onComplete(List<Event> data) {
                firebaseCallback.onComplete(data);
            }
            @Override
            public void onCancel() {
                firebaseCallback.onCancel();
            }
        });
    }

    private void updateFriendsDataInLocalStorage(List<User> data)
    {
        MyTask task = new MyTask();
        task.execute(data);
    }

    public void deleteFromFriends(int friendId, final FirebaseModel.FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int userId = User.generateId(auth.getCurrentUser().getEmail());
        FirebaseModel.getFriends(userId, new FirebaseModel.FirebaseCallback<List<User>>() {
            @Override
            public void onComplete(List<User> data) {
                boolean found = false;
                if (data != null) {//if he has friends
                    for (int i = 0; i < data.size() && !found; i++) {
                        if (data.get(i).getId() == friendId) {//if the deleted one exist in the list
                            User u = data.get(i);
                            found = true;
                            data.remove(u);
                            FirebaseModel.setFriends(userId, data, firebaseCallback);
                        }
                    }
                    if (!found)
                        firebaseCallback.onCancel();
                } else {
                    firebaseCallback.onCancel();
                }
            }

            @Override
            public void onCancel() {
                firebaseCallback.onCancel();
            }
        });
    }

    public void changeUserDetails(String firstName, String lastName, String email, String phone, final FirebaseModel.FirebaseCallback<String> firebaseCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int userId = User.generateId(auth.getCurrentUser().getEmail());

        String str = "";

        if (firstName != null) {
            FirebaseModel.setFirstName(userId, firstName);
            str += "First Name ";
        }
        if (lastName != null) {
            FirebaseModel.setLastName(userId, lastName);
            str += "Last Name ";
        }
        if (email != null) {
            FirebaseModel.setEmail(userId, email);
            str += "Email ";
        }
        if (phone != null) {
            FirebaseModel.setPhone(userId, phone);
            str += "Phone ";
        }

        firebaseCallback.onComplete(str);
    }

    public void setPictureUrl(Bitmap bitmap, final FirebaseModel.FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int id = User.generateId(auth.getCurrentUser().getEmail());
        FirebaseModel.setPictureUrl(id, bitmap, firebaseCallback);
    }

    public void saveRecord(String Path, String eventId, final Model.SaveAudioListener listener, FirebaseModel.FirebaseCallback callback) {
        FirebaseModel.saveRecord(Path, eventId, listener, callback);
    }
    public void saveRecord(String userId,String Path,String eventId,final Model.SaveAudioListener listener,FirebaseModel.FirebaseCallback callback)
    {
    FirebaseModel.saveRecord(userId,Path,eventId,listener,callback);
    }

    public void removeAccount(final FirebaseModel.FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseModel.removeAccount(firebaseCallback);
    }

    public void removeInvite(final FirebaseModel.FirebaseCallback<Boolean> callback, Invite invite) {
        FirebaseModel.removeInvite(callback, invite);
    }


    public void addNewUserToDB(User user, final FirebaseModel.FirebaseCallback firebaseCallback) {
        FirebaseModel.addUser(user, firebaseCallback);
    }

    public void addNewInvite(Invite invite, final FirebaseModel.FirebaseCallback callback) {
        FirebaseModel.addInvite(invite);
    }

    public void getInvite(String id, final FirebaseModel.FirebaseCallback callback, FirebaseModel.GetInvitation invitation) {
        FirebaseModel.getInvite(id, invitation);

    }
    public void setEventList(User user,FirebaseModel.FirebaseCallback callback) {
        FirebaseModel.setEventList(user,callback);

    }
    public void setRecodrdingStatus(String eventId,FirebaseModel.FirebaseCallback callback) {
        FirebaseModel.setRecordingStatus(eventId,callback);

    }
//    public LiveData<List<User>> getFriends() {
//        synchronized (this) {
//            if (friendsLiveData == null) {
//                friendsLiveData = new MutableLiveData<List<User>>();
//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                FirebaseModel.getFriends(User.generateId(auth.getCurrentUser().getEmail()), new FirebaseModel.Callback<List<User>>() {
//                    @Override
//                    public void onComplete(List<User> data) {
//                        if (data != null)
//                            friendsLiveData.setValue(data);
//                    }
//                });
//            }
//        }
//        return friendsLiveData;
//    }

    public void logout() {
        userLiveData = null;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
    }

    //Image (Profile Picture)
    public interface GetImageListener {
        void onSuccess(Bitmap image);

        void onFail();
    }

    public void getProfilePicture(String url, final FirebaseModel.FirebaseCallback<Bitmap> firebaseCallback) {
        if (url == null || url.equals("")) {
            firebaseCallback.onCancel();
        } else {
            //check if image exsist localy
            String fileName = URLUtil.guessFileName(url, null, null);
            Bitmap image = localStorage.loadImageFromFile(fileName);

            if (image != null) {
                firebaseCallback.onComplete(image);
            } else {
                FirebaseModel.getImage(url, new FirebaseModel.FirebaseCallback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        firebaseCallback.onComplete(data);
                    }

                    @Override
                    public void onCancel() {
                        firebaseCallback.onCancel();
                    }
                });
            }
        }
    }

    //Default user (connected user)
    public void getProfilePicture(final FirebaseModel.FirebaseCallback<Bitmap> firebaseCallback) {
        String url = userLiveData.getValue().getPictureUrl();
        if (url == null || url.equals("")) {
            firebaseCallback.onCancel();
        } else {
            //check if image exsist localy
            String fileName = URLUtil.guessFileName(url, null, null);
            Bitmap image = localStorage.loadImageFromFile(fileName);

            if (image != null) {
                firebaseCallback.onComplete(image);
            } else {
                FirebaseModel.getImage(url, new FirebaseModel.FirebaseCallback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        firebaseCallback.onComplete(data);
                    }

                    @Override
                    public void onCancel() {
                        firebaseCallback.onCancel();
                    }
                });
            }
        }
    }

    public void saveProfilePicture(Bitmap bitmap, String email, final FirebaseModel.FirebaseCallback firebaseCallback) {

        FirebaseModel.saveImage(bitmap, User.generateId(email), new Model.SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                localStorage.saveImageToFile(bitmap, fileName);
                firebaseCallback.onComplete(url);
                Log.d("sad", "profile pic saved locally & onFirebase & added to gallery");
            }

            @Override
            public void fail() {
                firebaseCallback.onCancel();
            }
        });
    }




    //END of Image (Profile Picture)

    //Events
//    public LiveData<List<Event>> getEvents() {
////
////        List<Event> localEventsList = RoomDatabaseManager.getInstance().getEventsList();
////        if(localEventsList != null){
////            data.setValue(localEventsList);
////        }
//        synchronized (this) {
//            if (eventsData == null) {
//                eventsData = new MutableLiveData<>();
//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                FirebaseModel.getEventsAndObserve(userLiveData.getValue().getId(), new FirebaseModel.FirebaseCallback<List<Event>>() {
//                    @Override
//                    public void onComplete(List<Event> data) {
//                        if (data != null)
//                            eventsData.setValue(data);
//                    }
//                });
//            }
//        }
//        return eventsData;
//    }

    public void addEvent(Event event) {
        //TODO add locally DB
        //TODO create Content file and save locally
        //TODO save firebase content
        String url = "";

        FirebaseModel.addNewEvent(event);
    }

//    public File getContentFile(){
//
//        //TODO get content image
//
//    }



    //CLASSES
    class MyTask extends AsyncTask<List<User>,String,List<User>> {
        @Override
        protected List<User> doInBackground(List<User>[] lists) {
            Log.d("TAG","starting updateEmployeeDataInLocalStorage in thread");
            if (lists.length > 0) {
                List<User> data = lists[0];
                long lastUpdateDate = 0;
//                try {
////                    lastUpdateDate = MyApp.getContext()
////                            .getSharedPreferences("TAG", MODE_PRIVATE).getLong("lastUpdateDate", 0);
//                }catch (Exception e){
//
//                }
                if (data != null && data.size() > 0) {
                    //3. update the local DB
                    long reacentUpdate = lastUpdateDate;
                    for (User u : data) {
                        try {
                            AppLocalStoreDb.getLocalDatabase(MyApp.getContext()).UserDao().saveUser(u);
                        }catch (Exception e)
                        {
                            Log.d("TAG", e.getMessage());
                        }

                        if (u.getLastUpdated() > reacentUpdate) {
                            reacentUpdate = u.getLastUpdated();
                        }
                        Log.d("TAG", "updating: " + u.toString());
                    }
                    SharedPreferences.Editor editor = MyApp.getContext().getSharedPreferences("TAG", MODE_PRIVATE).edit();
                    editor.putLong("lastUpdateDate", reacentUpdate);
                    editor.commit();
                }
                //return the complete student list to the caller
                List<User> friends = AppLocalStoreDb.getLocalDatabase(MyApp.getContext()).UserDao().getAllFriends();

                return friends;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            friends = users;
        }
    }
}
