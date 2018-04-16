package com.example.malicteam.projectxclient.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.google.firebase.auth.FirebaseAuth;

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
    private MutableLiveData<List<User>> FriendsLiveData;
    //private MutableLiveData<List<Event>> eventsData;
    //private MutableLiveData<List<User>> friendsLiveData;

    private List<User> friends = null;//holds local users

    public static final Repository instance = new Repository();
    private LocalStorageManager localStorage = new LocalStorageManager();


    public LiveData<User> getUser(int id) {
        synchronized (this) {
            if (userLiveData == null) {
                userLiveData = new MutableLiveData<User>();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseModel.getUserAndObserve(Integer.toString(id), new CloudManager.CloudCallback<User>() {
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
            FirebaseModel.getSomeUserAndObserve(Integer.toString(id), new CloudManager.CloudCallback<User>() {
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

    public void getUserById(int id, final CloudManager.CloudCallback<List<User>> callback) {
        FirebaseModel.getUserById(id, callback);
    }

    public void getEventById(int id, final CloudManager.CloudCallback<List<Event>> callback) {
        FirebaseModel.getEventById(id, callback);
    }

    public void getEventRecordingStatus(int id, CloudManager.CloudCallback<List<Boolean>> callback) {
        FirebaseModel.getEventRecordingStatus(id, callback);
    }

    public void addFriend(String email, final CloudManager.CloudCallback<Boolean> cloudCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int userId = User.generateId(auth.getCurrentUser().getEmail());
        //List<User> _friendsList = new LinkedList<>();

        FirebaseModel.getFriends(userId, new CloudManager.CloudCallback<List<User>>() {
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
                    FirebaseModel.isExistUser(User.generateId(email), new CloudManager.CloudCallback<Integer>() {
                        @Override
                        public void onComplete(Integer friendId) {//ok - go on
                            if (friendId != null && friendId > 0) {
                                LinkedList<Integer> newList = new LinkedList<>();
                                for (User u : friendsList) {//makes list of IDs
                                    newList.add(u.getId());
                                }
                                newList.add(friendId);
                                FirebaseModel.setFriends(userId, ProductTypeConverters.toString(newList), cloudCallback);
                            } else
                                cloudCallback.onCancel();
                        }

                        @Override
                        public void onCancel() {
                            cloudCallback.onCancel();
                        }
                    });
                } else {
                    //already friend
                    cloudCallback.onCancel();
                }
            }//END onComplete get friends

            @Override
            public void onCancel() {
                cloudCallback.onCancel();
            }
        });
    }

    public LiveData<List<User>> getFriends(int userId) {
        synchronized (this) {
            if (FriendsLiveData == null) {
                FriendsLiveData = new MutableLiveData<List<User>>();

                //1. get the last update date
                long lastUpdateDate = 0;
                try {
                    lastUpdateDate = MyApp.getContext().getSharedPreferences("TAG", MODE_PRIVATE).getLong("lastUpdateDate", 0);
                } catch (Exception e) {

                }

                //2. get all students records that where updated since last update date
                FirebaseModel.getFriends(userId, new CloudManager.CloudCallback<List<User>>() {
                    @Override
                    public void onComplete(List<User> data) {
                        updateFriendsDataInLocalStorage(data);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        }
        return FriendsLiveData;
    }

    public void getEvents(int userId, CloudManager.CloudCallback<List<Event>> cloudCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseModel.getEvents(userId, new CloudManager.CloudCallback<List<Event>>() {
            @Override
            public void onComplete(List<Event> data) {
                cloudCallback.onComplete(data);
            }

            @Override
            public void onCancel() {
                cloudCallback.onCancel();
            }
        });
    }

    private void updateFriendsDataInLocalStorage(List<User> data) {
        MyTask task = new MyTask();
        task.execute(data);
    }

    public void deleteFromFriends(int friendId, final CloudManager.CloudCallback<Boolean> cloudCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int userId = User.generateId(auth.getCurrentUser().getEmail());
        FirebaseModel.getFriends(userId, new CloudManager.CloudCallback<List<User>>() {
            @Override
            public void onComplete(List<User> data) {
                boolean found = false;
                if (data != null) {//if he has friends
                    for (int i = 0; i < data.size() && !found; i++) {
                        if (data.get(i).getId() == friendId) {//if the deleted one exist in the list
                            User u = data.get(i);
                            found = true;
                            data.remove(u);
                            FirebaseModel.setFriends(userId, data, cloudCallback);
                        }
                    }
                    if (!found)
                        cloudCallback.onCancel();
                } else {
                    cloudCallback.onCancel();
                }
            }

            @Override
            public void onCancel() {
                cloudCallback.onCancel();
            }
        });
    }

    public void changeUserDetails(String firstName, String lastName, String email, String phone, final CloudManager.CloudCallback<String> cloudCallback) {
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

        cloudCallback.onComplete(str);
    }

    public void setPictureUrl(Bitmap bitmap, final CloudManager.CloudCallback<Boolean> cloudCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int id = User.generateId(auth.getCurrentUser().getEmail());
        FirebaseModel.setPictureUrl(id, bitmap, cloudCallback);
    }

    public void saveRecord(String Path, String eventId, final Model.SaveAudioListener listener, CloudManager.CloudCallback callback) {
        FirebaseModel.saveRecord(Path, eventId, listener, callback);
    }

    public void saveRecord(String userId, String Path, String eventId, final Model.SaveAudioListener listener, CloudManager.CloudCallback callback) {
        FirebaseModel.saveRecord(userId, Path, eventId, listener, callback);
    }

    public void removeAccount(final CloudManager.CloudCallback<Boolean> cloudCallback) {
        FirebaseModel.removeAccount(cloudCallback);
    }

    public void removeInvite(final CloudManager.CloudCallback<Boolean> callback, Invite invite) {
        FirebaseModel.removeInvite(callback, invite);
    }


    public void addNewUserToDB(User user, final CloudManager.CloudCallback cloudCallback) {
        FirebaseModel.addUser(user, cloudCallback);
    }

    public void addNewInvite(Invite invite, final CloudManager.CloudCallback callback) {
        FirebaseModel.addInvite(invite);
    }

    public void getInvite(String id, final CloudManager.CloudCallback callback, FirebaseModel.GetInvitation invitation) {
        FirebaseModel.getInvite(id, invitation);

    }

    public void setEventList(User user, CloudManager.CloudCallback callback) {
        FirebaseModel.setEventList(user, callback);

    }

    public void setRecodrdingStatus(String eventId, CloudManager.CloudCallback callback) {
        FirebaseModel.setRecordingStatus(eventId, callback);

    }

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

    public void getProfilePicture(String url, final CloudManager.CloudCallback<Bitmap> cloudCallback) {
        if (url == null || url.equals("")) {
            cloudCallback.onCancel();
        } else {
            //check if image exsist localy
            String fileName = URLUtil.guessFileName(url, null, null);
            Bitmap image = localStorage.loadImageFromFile(fileName);

            if (image != null) {
                cloudCallback.onComplete(image);
            } else {
                FirebaseModel.getImage(url, new CloudManager.CloudCallback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        cloudCallback.onComplete(data);
                    }

                    @Override
                    public void onCancel() {
                        cloudCallback.onCancel();
                    }
                });
            }
        }
    }

    //Default user (connected user)
    public void getProfilePicture(final CloudManager.CloudCallback<Bitmap> cloudCallback) {
        String url = userLiveData.getValue().getPictureUrl();
        if (url == null || url.equals("")) {
            cloudCallback.onCancel();
        } else {
            //check if image exsist localy
            String fileName = URLUtil.guessFileName(url, null, null);
            Bitmap image = localStorage.loadImageFromFile(fileName);

            if (image != null) {
                cloudCallback.onComplete(image);
            } else {
                FirebaseModel.getImage(url, new CloudManager.CloudCallback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        cloudCallback.onComplete(data);
                    }

                    @Override
                    public void onCancel() {
                        cloudCallback.onCancel();
                    }
                });
            }
        }
    }

    public void saveProfilePicture(Bitmap bitmap, String email, final CloudManager.CloudCallback cloudCallback) {

        FirebaseModel.saveImage(bitmap, User.generateId(email), new Model.SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                localStorage.saveImageToFile(bitmap, fileName);
                cloudCallback.onComplete(url);
                Log.d("sad", "profile pic saved locally & onFirebase & added to gallery");
            }

            @Override
            public void fail() {
                cloudCallback.onCancel();
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
//                FirebaseModel.getEventsAndObserve(userLiveData.getValue().getId(), new FirebaseModel.CloudCallback<List<Event>>() {
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
    class MyTask extends AsyncTask<List<User>, String, List<User>> {
        @Override
        protected List<User> doInBackground(List<User>[] lists) {
            Log.d("TAG", "starting updateEmployeeDataInLocalStorage in thread");
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
                        } catch (Exception e) {
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
