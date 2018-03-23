package com.example.malicteam.projectxclient.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maayan on 11-Mar-18.
 */

public class Repository {

    public static final Repository instance = new Repository();

    private MutableLiveData<User> userLiveData;
    private List<MutableLiveData<User>> someUser;
    //private MutableLiveData<List<Event>> eventsData;
    //private MutableLiveData<List<User>> friendsLiveData;

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
            //check if exist
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

//    public interface AddFriendsListener {
//        void onSuccess();
//
//        void onFail(String msg);
//    }

    public void getUserById(int id, FirebaseModel.FirebaseCallback<List<User>> callback){

        FirebaseModel.getUserById(id,callback);

        }
    public void getEventById(int id, FirebaseModel.FirebaseCallback<List<Event>> callback){

        FirebaseModel.getEventById(id,callback);

    }
    public void addFriend(String email, FirebaseModel.FirebaseCallback<Boolean> firebaseCallback) {
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
                                for (User u : friendsList) {//make list of IDs
                                    newList.add(u.getId());
                                }
                                newList.add(friendId);
                                FirebaseModel.setFriends(userId, FirebaseModel.generateStringFromList(newList), new FirebaseModel.FirebaseCallback<Boolean>() {
                                    @Override
                                    public void onComplete(Boolean data) {
                                        //Mission is complete
                                        firebaseCallback.onComplete(data);
                                    }

                                    @Override
                                    public void onCancel() {
                                        firebaseCallback.onCancel();
                                    }
                                });
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

    public void getFriends(int friendId, FirebaseModel.FirebaseCallback<List<User>> firebaseCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int userId = User.generateId(auth.getCurrentUser().getEmail());
        FirebaseModel.getFriends(userId, new FirebaseModel.FirebaseCallback<List<User>>() {
            @Override
            public void onComplete(List<User> data) {
                firebaseCallback.onComplete(data);
            }

            @Override
            public void onCancel() {
                firebaseCallback.onCancel();
            }
        });
    }

    public void deleteFromFriends(int friendId, FirebaseModel.FirebaseCallback<Boolean> firebaseCallback) {
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
                            FirebaseModel.setFriends(userId, data, new FirebaseModel.FirebaseCallback<Boolean>() {
                                @Override
                                public void onComplete(Boolean data) {
                                    firebaseCallback.onComplete(data);
                                }

                                @Override
                                public void onCancel() {
                                    firebaseCallback.onCancel();
                                }
                            });

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

    public void changeUserDetails(String firstName, String lastName, String email, String phone, FirebaseModel.FirebaseCallback<String> firebaseCallback) {
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

    public void setPictureUrl(Bitmap bitmap, FirebaseModel.FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int id = User.generateId(auth.getCurrentUser().getEmail());
        FirebaseModel.setPictureUrl(id, bitmap, firebaseCallback);
    }
    public void saveRecord(String Path,String eventId,final Model.SaveAudioListener listener,FirebaseModel.FirebaseCallback callback)
    {
    FirebaseModel.saveRecord(Path,eventId,listener,callback);
    }

    public void removeAccount(FirebaseModel.FirebaseCallback<Boolean> firebaseCallback) {
        FirebaseModel.removeAccount(firebaseCallback);
    }
    public void removeInvite(FirebaseModel.FirebaseCallback<Boolean> callback,Invite invite) {
        FirebaseModel.removeInvite(callback,invite);
    }


    public void addNewUserToDB(User user, FirebaseModel.FirebaseCallback firebaseCallback) {
        FirebaseModel.addUser(user, firebaseCallback);
    }
    public void addNewInvite(Invite invite, FirebaseModel.FirebaseCallback callback) {
        FirebaseModel.addInvite(invite);
    }
    public void getInvite(String id,FirebaseModel.FirebaseCallback callback,FirebaseModel.GetInvitation invitation) {
        FirebaseModel.getInvite(id,invitation);

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

    public void getProfilePicture(String url, FirebaseModel.FirebaseCallback<Bitmap> firebaseCallback) {
        if (url == null || url.equals("")) {
            firebaseCallback.onCancel();
        } else {
            //check if image exsist localy
            String fileName = URLUtil.guessFileName(url, null, null);
            Bitmap image = loadImageFromFile(fileName);

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
    public void getProfilePicture(FirebaseModel.FirebaseCallback<Bitmap> firebaseCallback) {
        String url = userLiveData.getValue().getPictureUrl();
        if (url == null || url.equals("")) {
            firebaseCallback.onCancel();
        } else {
            //check if image exsist localy
            String fileName = URLUtil.guessFileName(url, null, null);
            Bitmap image = loadImageFromFile(fileName);

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

    public void saveProfilePicture(Bitmap bitmap, String email, FirebaseModel.FirebaseCallback firebaseCallback) {

        FirebaseModel.saveImage(bitmap, User.generateId(email), new Model.SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                saveImageToFile(bitmap, fileName);
                firebaseCallback.onComplete(url);
                Log.d("sad", "profile pic saved locally & onFirebase & added to gallery");
            }

            @Override
            public void fail() {
                firebaseCallback.onCancel();
            }
        });
    }

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir, imageFileName);
            imageFile.createNewFile();
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            addPictureToGallery(imageFile);
            Log.d("QQQQQQQQ", "ProfilePicture saved locally ");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPictureToGallery(File imageFile) {
//add the picture to the gallery so we dont need to manage the cache size
        Intent mediaScanIntent = new
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MyApp.getContext().sendBroadcast(mediaScanIntent);
        Log.d("MyApp.getContectx-OK", "MyApp.getContectx-OK---");
    }

    private Bitmap loadImageFromFile(String imageFileName) {
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir, imageFileName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d("GotImageFromMSgtag", "got image from cache: " + imageFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
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

}
