package com.example.malicteam.projectxclient.Model;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.malicteam.projectxclient.Model.FirebaseModel.GetInvitation;
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
    //private MutableLiveData<List<Event>> eventsData;
    //private MutableLiveData<List<User>> friendsLiveData;

    public LiveData<User> getUser(int id) {
        synchronized (this) {
            if (userLiveData == null) {
                userLiveData = new MutableLiveData<User>();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseModel.getUserAndObserve(Integer.toString(User.generateId(auth.getCurrentUser().getEmail())), new FirebaseModel.Callback<User>() {
                    @Override
                    public void onComplete(User data) {
                        if (data != null)
                            userLiveData.setValue(data);
                    }
                });
            }
        }
        return userLiveData;
    }

    public interface AddFriendsListener {
        void onSuccess();

        void onFail(String msg);
    }
    public void ifExistUser(String mail,FirebaseModel.Callback<Integer> callback) {
        FirebaseModel.isExistUser(User.generateId(mail),callback);
    }

    public void getUserById(int id, FirebaseModel.Callback<List<User>> callback){

        FirebaseModel.getUserById(id,callback);

        }
    public void getEventById(int id, FirebaseModel.Callback<List<Event>> callback){

        FirebaseModel.getEventById(id,callback);

    }
    public void addFriend(String email, FirebaseModel.Callback<List<User>> callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int userId = User.generateId(auth.getCurrentUser().getEmail());
        FirebaseModel.getFriends(userId, new FirebaseModel.Callback<List<User>>() {
            @Override
            public void onComplete(List<User> friendsList) {
                boolean found = false;
                if (friendsList != null) {
                    //first ew check if it not exist
                    for (int i = 0; i < friendsList.size() && !found; i++) {
                        if (friendsList.get(i).getEmail().equals(email))
                            found = true;
                    }
                    //if it not exist
                    if (!found) {
                        //check if it Signed user
                        FirebaseModel.isExistUser(User.generateId(email), new FirebaseModel.Callback<Integer>() {
                            @Override
                            public void onComplete(Integer friendId) {
                                if (friendId != null) {
                                    if (friendId > 0) {
                                        LinkedList<Integer> newList = new LinkedList<>();
                                        for (User u : friendsList) {
                                            newList.add(u.getId());
                                        }
                                        newList.add(friendId);
                                        FirebaseModel.setFriends(userId ,FirebaseModel.generateStringFromList(newList), new AddFriendsListener() {
                                            @Override
                                            public void onSuccess() {
                                                FirebaseModel.getFriends(userId, new FirebaseModel.Callback<List<User>>() {
                                                    @Override
                                                    public void onComplete(List<User> finalNewList) {
                                                        callback.onComplete(finalNewList);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFail(String msg) {
                                                callback.onComplete(null);
                                            }
                                        });
                                    } else
                                        callback.onComplete(null);
                                } else
                                    callback.onComplete(null);
                            }
                        });
                    } else
                        callback.onComplete(null);
                }
            }
        });
    }

    public void getFriends(int friendId, FirebaseModel.Callback<List<User>> callback){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int userId = User.generateId(auth.getCurrentUser().getEmail());
        FirebaseModel.getFriends(userId, new FirebaseModel.Callback<List<User>>(){
            @Override
            public void onComplete(List<User> data) {
                callback.onComplete(data);
            }
        });
    }


    public void deleteFromFriends(int friendId, FirebaseModel.Callback<List<User>> callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int userId = User.generateId(auth.getCurrentUser().getEmail());
        FirebaseModel.getFriends(userId, new FirebaseModel.Callback<List<User>>() {
            @Override
            public void onComplete(List<User> data) {
                boolean found = false;
                if (data != null) {//if he has friends
                    for (int i = 0; i < data.size() && !found; i++) {
                        if (data.get(i).getId() == friendId) {//if the deleted one exist in the list
                            User u = data.get(i);
                            found = true;
                            data.remove(u);
                            FirebaseModel.setFriends(userId, data, new AddFriendsListener() {
                                @Override
                                public void onSuccess() {
                                    callback.onComplete(data);
                                }

                                @Override
                                public void onFail(String msg) {
                                    callback.onComplete(null);
                                }
                            });

                        }
                    }
                    if (!found)
                        callback.onComplete(null);
                } else {
                    callback.onComplete(null);
                }
            }
        });
    }

    public void changeUserDetails(String firstName, String lastName, String email, String phone, FirebaseModel.Callback<String> callback) {
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

        callback.onComplete(str);
    }

    public void setPictureUrl(Bitmap bitmap, FirebaseModel.Callback<Boolean>callback)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int id = User.generateId(auth.getCurrentUser().getEmail());
        FirebaseModel.setPictureUrl(id,bitmap, callback);
    }
    public void saveRecord(String Path,String eventId,final Model.SaveAudioListener listener,FirebaseModel.Callback callback)
    {
    FirebaseModel.saveRecord(Path,eventId,listener,callback);
    }

    public void removeAccount(FirebaseModel.Callback<Boolean> callback) {
        FirebaseModel.removeAccount(callback);
    }
    public void removeInvite(FirebaseModel.Callback<Boolean> callback,Invite invite) {
        FirebaseModel.removeInvite(callback,invite);
    }


    public void addNewUserToDB(User user, FirebaseModel.Callback callback) {
        FirebaseModel.addUser(user, callback);
    }
    public void addNewInvite(Invite invite, FirebaseModel.Callback callback) {
        FirebaseModel.addInvite(invite);
    }
    public void getInvite(String id,FirebaseModel.Callback callback,GetInvitation invitation) {
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

    public void getProfilePicture(FirebaseModel.Callback<Bitmap> callback) {
        //String url = userLiveData.getValue().getPictureUrl();
        String url = userLiveData.getValue().getPictureUrl();
        if (url == null || url.equals("")) {
            callback.onComplete(null);
        } else {
            //check if image exsist localy
            String fileName = URLUtil.guessFileName(url, null, null);
            Bitmap image = loadImageFromFile(fileName);

            if (image != null) {
                callback.onComplete(image);
            } else {
                FirebaseModel.getImage(url, new FirebaseModel.Callback<Bitmap>() {
                    @Override
                    public void onComplete(Bitmap data) {
                        callback.onComplete(data);
                    }
                });
            }
        }
    }

    public void saveProfilePicture(Bitmap bitmap, String email, FirebaseModel.Callback callback) {

        FirebaseModel.saveImage(bitmap, User.generateId(email), new Model.SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                saveImageToFile(bitmap, fileName);
                callback.onComplete(url);
                Log.d("sad", "profile pic saved locally & onFirebase & added to gallery");
            }

            @Override
            public void fail() {
                callback.onComplete(null);
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
//                FirebaseModel.getEventsAndObserve(userLiveData.getValue().getId(), new FirebaseModel.Callback<List<Event>>() {
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
