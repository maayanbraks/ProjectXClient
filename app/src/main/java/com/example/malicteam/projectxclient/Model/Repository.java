package com.example.malicteam.projectxclient.Model;

import Requests.*;
import Responses.ErrorResponseData;
import Responses.*;
import ResponsesEntitys.EventData;
import ResponsesEntitys.ProtocolLine;
import ResponsesEntitys.UserData;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.malicteam.projectxclient.Common.Callbacks.AddEventCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.AddFriendCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.AgreeToEventCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.CloseEventCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.CreateUserCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.DataSetCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.DeclineToEventCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.EditFriendListCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.EditUserCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.EventListCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.FriendsListCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.LeaveEventCallBack;
import com.example.malicteam.projectxclient.Common.Callbacks.LogInCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.ProtocolRequestCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.RecordingActivityCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.isUserExistResponeCallback;
import com.example.malicteam.projectxclient.Common.MyApp;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.example.malicteam.projectxclient.ViewModel.FriendsViewModel;
//import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Maayan on 11-Mar-18.
 */

//Singleton
public class Repository {
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<List<Event>> eventsLiveData;
    private MutableLiveData<List<User>> friendsLiveData;

    private List<User> friends = null;//holds local users

    public static final Repository instance = new Repository();
    private LocalStorageManager localStorage = new LocalStorageManager();

    public Repository() {
    }

//    public LiveData<User> getUser(int id) {
//        synchronized (this) {
//            if (userLiveData == null) {
//                userLiveData = new MutableLiveData<User>();
//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                FirebaseModel.getUserAndObserve(Integer.toString(id), new CloudManager.CloudManagerCallback<User>() {
//                    @Override
//                    public void onComplete(User data) {
//                        if (data != null)
//                            userLiveData.setValue(data);
//                    }
//                    @Override
//                    public void onCancel() {
//                    }
//                });
//            }
//        }
//        return userLiveData;
//    }

    public LiveData<User> getUserMain(User user) {
        synchronized (this) {
            if (userLiveData == null) {
                userLiveData = new MutableLiveData<>();
                userLiveData.setValue(user);
            }
        }
        return userLiveData;
    }

    public LiveData<List<User>> getFriendsLiveData() {
        synchronized (this) {
            if (friendsLiveData == null) {
                friendsLiveData = new MutableLiveData<List<User>>();
                getFriendsFromServer(new FriendsListCallback<List<User>>() {
                    @Override
                    public void onSuccees(List<User> data) {
                        if (data != null) friendsLiveData.postValue(data);
                    }

                    @Override
                    public void technicalError() {

                    }

                    @Override
                    public void userMustToLogin() {

                    }
                });
            }
        }
        return friendsLiveData;
    }

    public LiveData<List<Event>> getEventsLiveData() {
        synchronized (this) {
            if (eventsLiveData == null) {
                eventsLiveData = new MutableLiveData<List<Event>>();
                getEventsFromServer(new EventListCallback<List<Event>>() {
                    @Override
                    public void onSuccees(List<Event> data) {
                        if (data != null) eventsLiveData.postValue(data);
                    }

                    @Override
                    public void UserIsNotExist() {

                    }

                    @Override
                    public void userMustToLogin() {

                    }
                });
            }
        }
        return eventsLiveData;
    }

    public void disconnectFromServer(CloudManager.CloudManagerCallback<Boolean> callback) {
        CloudManager.instance.disconnect(callback);
    }

    public void getFriendsMain(FriendsViewModel.FriendsViewModelCallback<LiveData<List<User>>> callback) {
        synchronized (this) {
            if (friendsLiveData == null) {
                friendsLiveData = new MutableLiveData<>();
                getFriendsFromServer(new FriendsListCallback<List<User>>() {
                    @Override
                    public void onSuccees(List<User> data) {
                        if (data != null) {
                            friendsLiveData.postValue(data);
                            callback.onComplete(friendsLiveData);
                        }
                    }

                    @Override
                    public void technicalError() {
                        Log.d("TAG", "Repository -> GetFriendsMain -> getFriendsFromServer = Technical error");
                    }

                    @Override
                    public void userMustToLogin() {
                        Log.d("TAG", "Repository -> GetFriendsMain -> getFriendsFromServer = Technical error");
                    }
                });
            }
        }
//        return friendsLiveData;
    }

//    public LiveData<User> getSomeUser(int id) {
//        synchronized (this) {
//            //check if exist in our list
//            if (someUser != null) {
//                for (MutableLiveData<User> u : someUser) {
//                    if (u.getValue().getId() == id)
//                        return u;
//                }
//            } else // list is null
//                someUser = new LinkedList<MutableLiveData<User>>();
//
//            someUser.add(new MutableLiveData<>());
//
//            FirebaseAuth auth = FirebaseAuth.getInstance();
//            FirebaseModel.getSomeUserAndObserve(Integer.toString(id), new CloudManager.CloudManagerCallback<User>() {
//                @Override
//                public void onComplete(User data) {
//                    someUser.get(someUser.size() - 1).setValue(data);
//                }
//
//                @Override
//                public void onCancel() {
//                }
//            });
//
//        }
//        return someUser.get(someUser.size() - 1);
//    }

//    public void getUserById(int id, final CloudManager.CloudManagerCallback<List<User>> callback) {
//        FirebaseModel.getUserById(id, callback);
//    }
//
//    public void getEventById(int id, final CloudManager.CloudManagerCallback<List<Event>> callback) {
//        FirebaseModel.getEventById(id, callback);
//    }

//    public void getEventRecordingStatus(int id, CloudManager.CloudManagerCallback<List<Boolean>> callback) {
//        FirebaseModel.getEventRecordingStatus(id, callback);
//    }

//    public void addFriend(String email, final CloudManager.CloudManagerCallback<Boolean> cloudCallback) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        int userId = User.generateId(auth.getCurrentUser().getEmail());
//        //List<User> _friendsList = new LinkedList<>();
//
//        FirebaseModel.initFriendsList(userId, new CloudManager.CloudManagerCallback<List<User>>() {
//            @Override
//            public void onComplete(List<User> friendsList) {
//                boolean found = false;
//                if (friendsList != null) {//if there is friends
//                    //first we check if it not exist
//                    for (int i = 0; i < friendsList.size() && !found; i++) {
//                        if (friendsList.get(i).getEmail().equals(email))
//                            found = true;
//                    }
//                }
//                //if it not exist
//                if (!found) {
//                    //check if it Signed user
//                    FirebaseModel.isExistUser(User.generateId(email), new CloudManager.CloudManagerCallback<Integer>() {
//                        @Override
//                        public void onComplete(Integer friendId) {//ok - go on
//                            if (friendId != null && friendId > 0) {
//                                LinkedList<Integer> newList = new LinkedList<>();
//                                for (User u : friendsList) {//makes list of IDs
//                                    newList.add(u.getId());
//                                }
//                                newList.add(friendId);
//                                FirebaseModel.setFriends(userId, ProductTypeConverters.toString(newList), cloudCallback);
//                            } else
//                                cloudCallback.onCancel();
//                        }
//
//                        @Override
//                        public void onCancel() {
//                            cloudCallback.onCancel();
//                        }
//                    });
//                } else {
//                    //already friend
//                    cloudCallback.onCancel();
//                }
//            }//END onComplete get friends
//
//            @Override
//            public void onCancel() {
//                cloudCallback.onCancel();
//            }
//        });
//    }
//    public LiveData<List<User>> initFriendsList(int userId) {
//        synchronized (this) {
//            if (FriendsLiveData == null) {
//                FriendsLiveData = new MutableLiveData<List<User>>();
//
//                //1. get the last update date
//                long lastUpdateDate = 0;
//                try {
//                    lastUpdateDate = MyApp.getContext().getSharedPreferences("TAG", MODE_PRIVATE).getLong("lastUpdateDate", 0);
//                } catch (Exception e) {
//
//                }
//
//                //2. get all students records that where updated since last update date
//                FirebaseModel.initFriendsList(userId, new CloudManager.CloudManagerCallback<List<User>>() {
//                    @Override
//                    public void onComplete(List<User> data) {
//                        updateFriendsDataInLocalStorage(data);
//                    }
//
//                    @Override
//                    public void onCancel() {
//                    }
//                });
//            }
//        }
//        return FriendsLiveData;
//    }

    //    public void initFriendsList(int userId, final CloudManager.CloudManagerCallback<List<User>> callback) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseModel.initFriendsList(userId, new CloudManager.CloudManagerCallback<List<User>>() {
//            @Override
//            public void onComplete(List<User> data) {
//                updateFriendsDataInLocalStorage(data);
//                callback.onComplete(data);
//            }
//
//            @Override
//            public void onCancel() {
//                callback.onComplete(friends);
//            }
//        });
//    }


//    public void getEvents(int userId, CloudManager.CloudManagerCallback<List<Event>> cloudCallback) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseModel.getEvents(userId, new CloudManager.CloudManagerCallback<List<Event>>() {
//            @Override
//            public void onComplete(List<Event> data) {
//                cloudCallback.onComplete(data);
//            }
//
//            @Override
//            public void onCancel() {
//                cloudCallback.onCancel();
//            }
//        });
//    }

    private void updateFriendsDataInLocalStorage(List<User> data) {
        MyTask task = new MyTask();
        task.execute(data);
    }

//    public void deleteFromFriends(int friendId, final CloudManager.CloudManagerCallback<Boolean> cloudCallback) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        int userId = User.generateId(auth.getCurrentUser().getEmail());
//        FirebaseModel.initFriendsList(userId, new CloudManager.CloudManagerCallback<List<User>>() {
//            @Override
//            public void onComplete(List<User> data) {
//                boolean found = false;
//                if (data != null) {//if he has friends
//                    for (int i = 0; i < data.size() && !found; i++) {
//                        if (data.get(i).getId() == friendId) {//if the deleted one exist in the list
//                            User u = data.get(i);
//                            found = true;
//                            data.remove(u);
//                            FirebaseModel.setFriends(userId, data, cloudCallback);
//                        }
//                    }
//                    if (!found)
//                        cloudCallback.onCancel();
//                } else {
//                    cloudCallback.onCancel();
//                }
//            }
//
//            @Override
//            public void onCancel() {
//                cloudCallback.onCancel();
//            }
//        });
//    }
//
//    public void changeUserDetails(String firstName, String lastName, String email, String phone, final CloudManager.CloudManagerCallback<String> cloudCallback) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        int userId = User.generateId(auth.getCurrentUser().getEmail());
//
//        String str = "";
//
//        if (firstName != null) {
//            FirebaseModel.setFirstName(userId, firstName);
//            str += "First Name ";
//        }
//        if (lastName != null) {
//            FirebaseModel.setLastName(userId, lastName);
//            str += "Last Name ";
//        }
//        if (email != null) {
//            FirebaseModel.setEmail(userId, email);
//            str += "Email ";
//        }
//        if (phone != null) {
//            FirebaseModel.setPhone(userId, phone);
//            str += "Phone ";
//        }
//
//        cloudCallback.onComplete(str);
//    }
//
//
//    public void setPictureUrl(Bitmap bitmap, final CloudManager.CloudManagerCallback<Boolean> cloudCallback) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        int id = User.generateId(auth.getCurrentUser().getEmail());
//        FirebaseModel.setPictureUrl(id, bitmap, cloudCallback);
//    }
//
//    public void saveRecord(String Path, String eventId, final Model.SaveAudioListener listener, CloudManager.CloudManagerCallback callback) {
//        FirebaseModel.saveRecord(Path, eventId, listener, callback);
//    }
//
//    public void saveRecord(String userId, String Path, String eventId, final Model.SaveAudioListener listener, CloudManager.CloudManagerCallback callback) {
//        FirebaseModel.saveRecord(userId, Path, eventId, listener, callback);
//    }
//
//    public void removeAccount(final CloudManager.CloudManagerCallback<Boolean> cloudCallback) {
//        FirebaseModel.removeAccount(cloudCallback);
//    }
//
//    public void removeInvite(final CloudManager.CloudManagerCallback<Boolean> callback, Invite invite) {
//        FirebaseModel.removeInvite(callback, invite);
//    }
//
//
//    public void addNewUserToDB(User user, final CloudManager.CloudManagerCallback cloudCallback) {
//        FirebaseModel.addUser(user, cloudCallback);
//    }
//
//    public void addNewInvite(Invite invite, final CloudManager.CloudManagerCallback callback) {
//        FirebaseModel.addInvite(invite);
//    }
//
//    public void getInvite(String id, final CloudManager.CloudManagerCallback callback, FirebaseModel.GetInvitation invitation) {
//        FirebaseModel.getInvite(id, invitation);
//
//    }
//
//    public void setEventList(User user, CloudManager.CloudManagerCallback callback) {
//        FirebaseModel.setEventList(user, callback);
//
//    }
//
//    public void setRecodrdingStatus(String eventId, CloudManager.CloudManagerCallback callback) {
//        FirebaseModel.setRecordingStatus(eventId, callback);
//
//    }

//    public LiveData<List<User>> initFriendsList() {
//        synchronized (this) {
//            if (friendsLiveData == null) {
//                friendsLiveData = new MutableLiveData<List<User>>();
//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                FirebaseModel.initFriendsList(User.generateId(auth.getCurrentUser().getEmail()), new FirebaseModel.Callback<List<User>>() {
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

//
//    public void logout() {
//        userLiveData = null;
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.signOut();
//    }

//    public void getProfilePicture(String url, final CloudManager.CloudManagerCallback<Bitmap> cloudCallback) {
//        if (url == null || url.equals("")) {
//            cloudCallback.onCancel();
//        } else {
//            //check if image exsist localy
//            String fileName = URLUtil.guessFileName(url, null, null);
//            Bitmap image = localStorage.loadImageFromFile(fileName);
//
//            if (image != null) {
//                cloudCallback.onComplete(image);
//            } else {
//                FirebaseModel.getImage(url, new CloudManager.CloudManagerCallback<Bitmap>() {
//                    @Override
//                    public void onComplete(Bitmap data) {
//                        cloudCallback.onComplete(data);
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        cloudCallback.onCancel();
//                    }
//                });
//            }
//        }
//    }

//    //Default user (connected user)
//    public void getProfilePicture(final CloudManager.CloudManagerCallback<Bitmap> cloudCallback) {
//        String url = userLiveData.getValue().getPictureUrl();
//        if (url == null || url.equals("")) {
//            cloudCallback.onCancel();
//        } else {
//            //check if image exsist localy
//            String fileName = URLUtil.guessFileName(url, null, null);
//            Bitmap image = localStorage.loadImageFromFile(fileName);
//
//            if (image != null) {
//                cloudCallback.onComplete(image);
//            } else {
//                FirebaseModel.getImage(url, new CloudManager.CloudManagerCallback<Bitmap>() {
//                    @Override
//                    public void onComplete(Bitmap data) {
//                        cloudCallback.onComplete(data);
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        cloudCallback.onCancel();
//                    }
//                });
//            }
//        }
//    }
//
//    public void saveProfilePicture(Bitmap bitmap, String email, final CloudManager.CloudManagerCallback cloudCallback) {
//
//        FirebaseModel.saveImage(bitmap, User.generateId(email), new Model.SaveImageListener() {
//            @Override
//            public void complete(String url) {
//                String fileName = URLUtil.guessFileName(url, null, null);
//                localStorage.saveImageToFile(bitmap, fileName);
//                cloudCallback.onComplete(url);
//                Log.d("sad", "profile pic saved locally & onFirebase & added to gallery");
//            }
//
//            @Override
//            public void fail() {
//                cloudCallback.onCancel();
//            }
//        });
//    }
//

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
        }
        return bitmap;
    }

    //New Server Methods
    public void logIn(String username, String password, final LogInCallback<User> callback) {
        LoginRequestData loginRequestData = new LoginRequestData(username, password);
        CloudManager.instance.loginRequest(loginRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String response) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(response, ResponseData.class);
                //Create To ResponseData
                //Checke type
                //BY the type -> create refrence ResponseData
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(response, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case TechnicalError:
                                callback.technicalError();
                                return;
                            case UserIsNotExist:
                                callback.userIsNotExist();
                            case ConnectionIsAlreadyEstablished:
                                callback.UseIsAllReadyLoggedIn();
                                return;
                            case IncorrectCredentials:
                                callback.IncorrectCredentials();
                                return;
                            default:
                                return;
                        }
                    case Boolean:
                        BooleanResponseData booleanResponseData = ProductTypeConverters.getObjectFromString(response, BooleanResponseData.class);
                        callback.onBoolean(booleanResponseData.getFlag());
                    case Login:
                        LoginResponseData loginResponseData = ProductTypeConverters.getObjectFromString(response, LoginResponseData.class);
                        User user = new User(loginResponseData.getUserData());
                        callback.login(user);
                    default:
                        break;
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

    public void getProtocol(int eventId, final ProtocolRequestCallback<List<ProtocolLine>> callback) {
        EventProtocolRequestData protocolRequestData = new EventProtocolRequestData(userLiveData.getValue().getEmail(), eventId);
        CloudManager.instance.sendToServer("Request", protocolRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String response) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(response, ResponseData.class);
                //Create To ResponseData
                //Checke type
                //BY the type -> create refrence ResponseData
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(response, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case TechnicalError:
                                callback.TechnicalError();
                                return;
                            case ProtocolIsNotExist:
                                callback.ProtocolIsNotExist();
                                return;
                            default:
                                return;
                        }
                    case EventProtocolResponse:
                        EventProtocolResponseData eventProtocolResponseData = ProductTypeConverters.getObjectFromString(response, EventProtocolResponseData.class);
                        callback.onSuccees(eventProtocolResponseData.getProtocolLines());
                        return;
                    default:
                        break;
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

    public void EditFriendList(LinkedList<String> friendList, EditFriendListCallback callback) {
        //ALSO DELETE FRIENDS!!!
        //init request
        EditContactsListRequestData editContactsListRequestData = new EditContactsListRequestData(userLiveData.getValue().getEmail(), friendList);
        //send request
        CloudManager.instance.sendToServer("Request", editContactsListRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String data) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(data, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case UserIsNotExist:
                                callback.UserIsNotExist();
                                return;
                            default:
                                return;
                        }
                    case Boolean:
                        BooleanResponseData response = ProductTypeConverters.getObjectFromString(data.toString(), BooleanResponseData.class);
                        if (response.getFlag()) {
                            callback.onSuccees();
                            return;
                        } else {
                            callback.error();
                            return;
                        }

                    default:
                        break;
                }
            }

            @Override
            public void onCancel() {
            }

        });
    }

    public void addFriend(String email, AddFriendCallback<Boolean> callback) {
        //init request
        AddFriendRequestData addFriendRequestData = new AddFriendRequestData(userLiveData.getValue().getEmail(), email);
        //send request
        CloudManager.instance.sendToServer("Request", addFriendRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String data) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(data, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case UserIsNotExist:
                                callback.userIsNotExist();
                                return;
                            case FriendIsNotExist:
                                callback.friendIsNotExist();
                                return;
                            case BothUsersEquals:
                                callback.bothUsersEquals();
                                return;
                            case AlreadyFriends:
                                callback.alreadyFriends();
                                return;
                            default:
                                return;
                        }
                    case AddFriendResponse:
                        AddFriendResponseData response = ProductTypeConverters.getObjectFromString(data.toString(), AddFriendResponseData.class);
                        User user = new User(response.getUserData());
                        friendsLiveData.getValue().add(user);//Here we add friend
                        callback.onSuccees(true);
                        return;

                    default:
                        break;
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

    //getFriends1
    public void getFriendsFromServer(final FriendsListCallback<List<User>> callback) {
        //Init the get friends/contacts list of  User (by email).
        ContactsListRequestData contactsListRequestData = new ContactsListRequestData(userLiveData.getValue().getEmail());
        //send request
        CloudManager.instance.sendToServer("Request", contactsListRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String data) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(data, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case TechnicalError:
                                callback.technicalError();
                                break;
                            case UserMustToLogin:
                                callback.userMustToLogin();
                                break;
                            default:
                                break;
                        }
                    case Contacts:
                        ContactsListResponseData response = ProductTypeConverters.getObjectFromString(data, ContactsListResponseData.class);
                        //convert UserData to User
                        LinkedList<User> list = new LinkedList<User>();
                        for (UserData userData : response.getContacts()) {
                            list.add(new User(userData));
                        }
                        callback.onSuccees(list);
                        break;

                    default:
                        return;
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

    public void getUserIfExist(String email, final isUserExistResponeCallback callback) {
        //Init the get friends/contacts list of  User (by email).
        IsUserExistRequestData IsUserExistResponseData = new IsUserExistRequestData(userLiveData.getValue().getEmail(), email);
        //send request
        CloudManager.instance.sendToServer("Request", IsUserExistResponseData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String data) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
                // Log.d("TAG","oiasdjadjdjkaspdojaspdojasdojassdoj+gettype="+responseData.getType());
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(data, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case UserIsNotExist:
                                callback.userIsNotExist();
                                break;
                            case FriendIsNotExist:
                                callback.friendIsNotExist();
                                break;
                            default:
                                break;
                        }
                    case IsUserExistResponse:
                        //   Log.d("TAG","IsUserExistResponeseoasjdsodjdsjdodjasd");
                        IsUserExistResponseData response = ProductTypeConverters.getObjectFromString(data, IsUserExistResponseData.class);
                        callback.onSuccees(response.getUserData());
                        break;

                    default:
                        return;
                }
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "on cancel in getuserifexist funcs in Reposotiry");
            }
        });
    }

    private int getDurationAsSecondsFromFile(String filePath) {
        Uri uri = Uri.parse(filePath);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(MyApp.getContext(), uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int duration = Integer.parseInt(durationStr) / 1000; //Milliseconds => Seconds
        Log.d("TAG", "Data set time is " + duration + " Seconds");
        return duration;
    }

    public void uploadDataSet(String filePath, final DataSetCallback callback) {
        //get duration
        int seconds = getDurationAsSecondsFromFile(filePath);

        //Convert File to byte[]
        byte[] audioBytes = null;
        try {
            audioBytes = ProductTypeConverters.convertFileToByte(filePath);
        } catch (Exception e) {
            Log.d("TAG", e.getStackTrace() + e.getMessage());
        }

        try {
            CloudManager.instance.sendDataSet(userLiveData.getValue().getId(), String.valueOf(seconds) + "\n", audioBytes, new CloudManager.CloudManagerCallback<Boolean>() {
                @Override
                public void onComplete(Boolean data) {
                    Log.d("TAG", "Repositoty -> SendDataSet -> On Succees = :)");
                    float newTime = (((float)seconds) / 60) + userLiveData.getValue().getDataSetTime();
                    userLiveData.getValue().setDataSetTime(newTime);
                    callback.onSuccees(newTime);
                }

                @Override
                public void onCancel() {
                    Log.d("TAG", "Repositoty -> SendDataSet -> On Error (Boolean 0)");
                }

            });
        } catch (Exception e) {
            Log.d("TAG", "Repositoty -> SendDataSet -> IO Exception " + e.getMessage());
        }
//        //Create request
//        DataSetRequestData dataSetRequestData = new DataSetRequestData(userLiveData.getValue().getEmail(), audioBytes, minutes);
//        CloudManager.instance.sendToServer("Request", dataSetRequestData, new CloudManager.CloudManagerCallback<String>() {
//            @Override
//            public void onComplete(String response) {
//                ResponseData responseData = ProductTypeConverters.getObjectFromString(response, ResponseData.class);
//                //Create To ResponseData
//                //Checke type
//                //BY the type -> create refrence ResponseData
//                switch (responseData.getType()) {
//                    case Error:
//                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(response, ErrorResponseData.class);
//                        switch (errorResponseData.getErrorType()) {
//                            case TechnicalError:
//                                callback.TechnicalError();
//                                return;
//                            default:
//                                return;
//                        }
//                    case DataSetResponseData:
//                        DataSetResponseData dataSetResponseData = ProductTypeConverters.getObjectFromString(response, DataSetResponseData.class);
//                        userLiveData.getValue().setDataSetTime(dataSetResponseData.getUpdatedLength());
//                        callback.onSuccees(dataSetResponseData.getUpdatedLength());
//                        return;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//        });
    }

    public void closeEvent(String[] protocol, int eventId, String filePath, final CloseEventCallback callback) {
        //init File to byte[]
        byte[] audioBytes = null;
        try {
            audioBytes = ProductTypeConverters.convertFileToByte(filePath);
        } catch (Exception e) {
            Log.d("TAG", e.getStackTrace() + e.getMessage());
        }
        try {
            CloudManager.instance.sendEvent(eventId, audioBytes, new CloudManager.CloudManagerCallback<Boolean>() {
                @Override
                public void onComplete(Boolean data) {
                    Log.d("TAG", "Repositoty -> CloseEvent -> On Succees = :)");
                    callback.onSuccees();
                }

                @Override
                public void onCancel() {
                    Log.d("TAG", "Repositoty -> CloseEvent -> On Error (Boolean 0)");
                }
            });
        } catch (Exception e) {
            Log.d("TAG", "Repositoty -> CloseEvent -> IO Exception " + e.getMessage());
        }
        //audio=audioBytes.
        //Init the get friends/contacts list of  User (by email).
//   OLD     CloseEventRequestData closeEventRequestData = new CloseEventRequestData(userLiveData.getValue().getEmail(), eventId, audioBytes);

        //send request
//        CloudManager.instance.sendToServer("Request", closeEventRequestData, new CloudManager.CloudManagerCallback<String>() {
//            @Override
//            public void onComplete(String data) {
//                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
//                switch (responseData.getType()) {
//                    case Error:
//                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(data, ErrorResponseData.class);
//                        switch (errorResponseData.getErrorType()) {
//                            case UserIsNotExist:
//                                callback.UserIsNotExist();
//                                break;
//                            case FriendIsNotExist:
//                                callback.EventIsNotExist();
//                            case TechnicalError:
//                                callback.TechnicalError();
//                                break;
//                            default:
//                                break;
//                        }
//                    case Boolean: // this momment if boolean it mean ALWAS TRUE
//                        callback.onSuccees();
//                        break;
//                    default:
//                        return;
//                }
//            }
//
//            @Override
//            public void onCancel() {
//            }
//        });
    }

    public void deleteFromFriends(User friend) {
        friendsLiveData.getValue().remove(friend);
    }

    //CLASSES
    class MyTask extends AsyncTask<List<User>, String, List<User>> {
        @Override
        protected List<User> doInBackground(List<User>[] lists) {
            Log.d("TAG", "starting updateEmployeeDataInLocalStorage in thread");
            if (lists.length > 0) {
                List<User> data = lists[0];
                long lastUpdateDate = 0;
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
                //   List<User> friends = AppLocalStoreDb.getLocalDatabase(MyApp.getContext()).UserDao().getAllFriends();

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


    public void getEventsFromServer(final EventListCallback<List<Event>> callback) {
        //Init the get friends/contacts list of  User (by email).
        EventsListRequestData eventsListRequestData = new EventsListRequestData(userLiveData.getValue().getEmail());
        //send request
        CloudManager.instance.sendToServer("Request", eventsListRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String data) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(data, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case UserIsNotExist:
                                callback.UserIsNotExist();
                                return;
                            case UserMustToLogin:
                                callback.userMustToLogin();
                                return;
                            default:
                                return;
                        }
                    case Events:
                        EventsListResponseData response = ProductTypeConverters.getObjectFromString(data, EventsListResponseData.class);
//                        convert UserData to User
                        LinkedList<Event> list = new LinkedList<Event>();
                        if (response.getEvents().size() > 0) {
                            for (EventData eventData : response.getEvents()) {
                                list.add(new Event(eventData));
                            }
                        }
                        callback.onSuccees(list);
                        return;

                    default:
                        return;
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

    public void AgreeToInvite(int eventId, final AgreeToEventCallback<Boolean> callback) {
        //Init the get friends/contacts list of  User (by email).
        ConfirmEventRequestData confirmEventRequestData = new ConfirmEventRequestData(userLiveData.getValue().getEmail(), eventId);
        //send request
        CloudManager.instance.sendToServer("Request", confirmEventRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String data) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(data, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case UserIsNotExist:
                                callback.UserIsNotExist();
                                break;
                            case NoPendingEvents:
                                callback.NoPendingEvents();
                                break;
                            default:
                                break;
                        }
                    case Boolean:
                        callback.onSuccees(true);
                        break;

                    default:
                        return;
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

    public void leaveEventRequest(int eventId, final LeaveEventCallBack<Boolean> callback) {
        //Init the get friends/contacts list of  User (by email).
        LeaveEventRequestData leaveEventRequestData = new LeaveEventRequestData(userLiveData.getValue().getEmail(), eventId);
        //send request
        CloudManager.instance.sendToServer("Request", leaveEventRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String data) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(data, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case TechnicalError:
                                callback.TechnicalError();
                                break;
                            case NoPendingEvents:
                                callback.NoPendingEvents();
                                break;
                            default:
                                break;
                        }
                    case Boolean:
                        callback.onSuccees(true);
                        break;

                    default:
                        return;
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

    public void DeclineToInvite(int eventId, final DeclineToEventCallback<Boolean> callback) {
        //Init the get friends/contacts list of  User (by email).
        DeclineEventRequestData declineEventRequestData = new DeclineEventRequestData(userLiveData.getValue().getEmail(), eventId);
        //send request
        CloudManager.instance.sendToServer("Request", declineEventRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String data) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(data, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case UserIsNotExist:
                                callback.UserIsNotExist();
                                break;
                            case NoPendingEvents:
                                callback.NoPendingEvents();
                                break;
                            case TechnicalError:
                                callback.TechnicalError();
                                break;
                            default:
                                break;
                        }
                    case Boolean:
                        callback.onSuccees(true);
                        break;

                    default:
                        return;
                }
            }

            @Override
            public void onCancel() {
            }
        });
    }

    public void InitCallbacksForCloudManeger(final RecordingActivityCallback callback) {
        CloudManager.instance.setRecordingCallback(callback);

    }

    public void InitMainActivityCallback(final Observer<Event> invitesCallback, final Observer<Integer> protocolCallback) {
        CloudManager.instance.setMainActivityCallback(invitesCallback, protocolCallback);
    }


    public void addEvent(List<String> usersMails, Event event, final AddEventCallback<Integer> callback) {
        CreateEventRequestData createEventRequestData = new CreateEventRequestData(userLiveData.getValue().getEmail(), usersMails, event.getTitle(), event.getDescription());
        CloudManager.instance.sendToServer("Request", createEventRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String response) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(response, ResponseData.class);
                //Create To ResponseData
                //Checke type
                //BY the type -> create refrence ResponseData
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(response, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case TechnicalError:
                                callback.technicalError();
                                return;
                            case UserIsNotExist:
                                callback.userIsNotExist();
                                return;
                            default:
                                return;
                        }
                    case CreateEvent:
                        CreateEventResponseData createEventResponseData = ProductTypeConverters.getObjectFromString(response, CreateEventResponseData.class);
                        callback.onSuccees(createEventResponseData.getId());
                    default:
                        break;
                }
            }

            @Override
            public void onCancel() {

            }

            String url = "";

            //FirebaseModel.addNewEvent(event);


//    public File getContentFile(){
//
//
//    }
        });
    }


    public void editUser(String firstName, String lastName, String email, String phone, EditUserCallback<Boolean> callback) {
        EditUserRequestData editUserRequestData = new EditUserRequestData(email, firstName, lastName, phone, "Israel", userLiveData.getValue().getPictureUrl());
        CloudManager.instance.sendToServer("Request", editUserRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String data) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
                switch (responseData.getType()) {
                    case Error:
                        return;
                    case Boolean:
                        BooleanResponseData booleanResponseData = ProductTypeConverters.getObjectFromString(data, BooleanResponseData.class);
                        if (booleanResponseData.getFlag()) {
                            updateUserLocal(firstName, lastName, phone);
                            callback.onSuccees(true);
                        }
                    default:
                        break;
                }
            }

            private void updateUserLocal(String firstName, String lastName, String phone) {
                userLiveData.getValue().setFirstName(firstName);
                userLiveData.getValue().setLastName(lastName);
                userLiveData.getValue().setPhoneNumber(phone);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public void createUser(User user, String credential, CreateUserCallback<Boolean> callback) {
        CreateUserRequestData createUserRequestData = new CreateUserRequestData(user.getEmail(), credential, user.getFirstName(), user.getLastName(), user.getPhoneNumber(), null, null);
        CloudManager.instance.registerRequest(createUserRequestData, new CloudManager.CloudManagerCallback<String>() {
            @Override
            public void onComplete(String data) {
                ResponseData responseData = ProductTypeConverters.getObjectFromString(data, ResponseData.class);
                switch (responseData.getType()) {
                    case Error:
                        ErrorResponseData errorResponseData = ProductTypeConverters.getObjectFromString(data, ErrorResponseData.class);
                        switch (errorResponseData.getErrorType()) {
                            case TechnicalError:
                                callback.TechnicalError();
                                return;
                            case EmailAlreadyRegistered:
                                callback.EmailAlreadyRegistered();
                                return;
                            default:
                                return;
                        }
                    case Boolean:
                        BooleanResponseData booleanResponseData = ProductTypeConverters.getObjectFromString(data, BooleanResponseData.class);
                        if (booleanResponseData.getFlag())
                            callback.onSuccees(true);
                    default:
                        break;
                }
            }

            @Override
            public void onCancel() {

            }
//    }
        });
    }


}
