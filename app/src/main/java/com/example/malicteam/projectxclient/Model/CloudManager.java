package com.example.malicteam.projectxclient.Model;

import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.util.Log;

//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import com.example.malicteam.projectxclient.Common.Callbacks.RecordingActivityCallback;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;

import Notifications.EventCloseNotificationData;
import Notifications.EventInvitationNotificationData;
import Notifications.NotificationData;
import Notifications.ProtocolReadyNotificationData;
import Notifications.UserJoinEventNotification;
import Notifications.UserLeaveEventNotification;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Thread.sleep;

/**
 * Created by Charcon on 02/05/2018.
 */

public class CloudManager {
    public interface CloudManagerCallback<T> {
        void onComplete(T data);

        void onCancel();
    }

    public static final CloudManager instance = new CloudManager();

    //    private final String SERVER_ADDRESS = "http://192.168.27.1:8080";
    private final String EVENT_CONNECT = "Connect";//Login + Register
    private final String SERVER_ADDRESS = "http://193.106.55.95:8080";
    private CloudManagerCallback<String> localCallbackCloudManager;
    private RecordingActivityCallback recordingActivityCallback;
    private Observer<Event> mainActivityInvitesCallback;
    private Observer<Integer> mainActivityProtocolCallback;
    private Socket socket;
    static final int PORT = 8888;
    private boolean isConnected;

    public CloudManager() {
        try {
            isConnected = connectToServer();
            if (isConnected) ;
        } catch (Exception e) {
            Log.d("TAG", "Cloud Manager CTOR -> " + e.getMessage());
        }
    }

    //Primitives Methods
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public static int getPORT() {
        return PORT;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setRecordingCallback(final RecordingActivityCallback callback) {
        this.recordingActivityCallback = callback;
    }

    public void setMainActivityCallback(final Observer<Event> invitesCallback, final Observer<Integer> protocolCallback) {
        this.mainActivityInvitesCallback = invitesCallback;
        this.mainActivityProtocolCallback = protocolCallback;
    }


    public boolean connectToServer() throws URISyntaxException {
        IO.Options opts = new IO.Options();
        socket = IO.socket(SERVER_ADDRESS, opts);
        initListeners();
        socket.connect();
        if (isConnected)
            return true;
        return false;
    }

    public void initListeners() { // here we analize all listeners and responses
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                Log.d("TAG", "Connection with server has been establish");
                // RequestData rd = new AddFriendRequestData("test@test.com", "friend@friend.com");
                // String jsonString =new Gson().toJson(rd);
                // socket.emit("toServer", "lalalalall");
                // socket.send("test");
                setConnected(true);
            }
        });
        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                Log.d("TAG", "Socket is been disconnect");
                setConnected(false);
            }
        });
        socket.on("Response", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (localCallbackCloudManager != null) {
                    localCallbackCloudManager.onComplete(args[0].toString());
                } else {
                    Log.d("TAG", "Localcallback is null");
                }
                Log.d("TAG", "" + args[0]);
                //handleLiveData("" + args[0]);
            }
        });

        socket.on("Notification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("TAG", "" + args[0]);
                handleLiveData((args[0].toString()));
                //handleLiveData("" + args[0]);
            }
        });
    }

    public void handleLiveData(String data) { //here Live data responses (need to send to LiveData!
        //Log.d("TAG","StringNotification:="+data);
        NotificationData rs = ProductTypeConverters.getObjectFromString(data, NotificationData.class);
        // Log.d("TAG","StringNotification:="+data);
        Log.d("TAG", "handleliveData--->rs.getnotificationType:=" + rs.getNotificationType());
        switch (rs.getNotificationType()) {
            case EventInvitation:
                EventInvitationNotificationData eventInvitationNotificationData = ProductTypeConverters.getObjectFromString(data, EventInvitationNotificationData.class);
                //MainActivity.GetInvation(eventInvitationNotificationData);
                if (mainActivityInvitesCallback == null) {
                    try {
                        sleep(1000);
                        mainActivityInvitesCallback.onChanged(new Event(eventInvitationNotificationData.getEventData()));
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    mainActivityInvitesCallback.onChanged(new Event(eventInvitationNotificationData.getEventData()));
                    return;
                }

            case UserJoinEvent:
                UserJoinEventNotification userJoinEventNotification = ProductTypeConverters.getObjectFromString(data, UserJoinEventNotification.class);
                if (recordingActivityCallback != null) {
                    //try {
                    //sleep(1000);
                    recordingActivityCallback.userJoinEvent(new User(userJoinEventNotification.getUserData()));
                    return;
                    //  } catch (InterruptedException e) {
                    //  e.printStackTrace();
                    //  }

                } else
                    // recordingActivityCallback.userJoinEvent(new User(userJoinEventNotification.getUserData()));
                    return;


            case UserLeaveEvent:
                UserLeaveEventNotification userLeaveEventNotification = ProductTypeConverters.getObjectFromString(data, UserLeaveEventNotification.class);
                if (recordingActivityCallback != null) {
//                    try {
//                        sleep(1000);
                    recordingActivityCallback.userLeftEvent(new User(userLeaveEventNotification.getUserData()));
                    return;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    //     }else
                    // {
                    // recordingActivityCallback.userLeftEvent(new User(userLeaveEventNotification.getUserData()));
                    //  return;
                    // }
                }

            case EventClosed:
                EventCloseNotificationData eventCloseNotificationData = ProductTypeConverters.getObjectFromString(data, EventCloseNotificationData.class);
                if (recordingActivityCallback == null) {
                    try {
                        sleep(1000);
                        recordingActivityCallback.eventClosed(new Event(eventCloseNotificationData.getEventData()));
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    recordingActivityCallback.eventClosed(new Event(eventCloseNotificationData.getEventData()));
                    return;
                }
            case ProtocolIsReady:
                ProtocolReadyNotificationData protocolReadyNotificationData = ProductTypeConverters.getObjectFromString(data, ProtocolReadyNotificationData.class);
                if (mainActivityInvitesCallback == null) {
                    try {
                        sleep(1000);
                        mainActivityProtocolCallback.onChanged(protocolReadyNotificationData.getEventData().getId());
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    mainActivityProtocolCallback.onChanged(protocolReadyNotificationData.getEventData().getId());
                    return;
                }


            default:
                return;
        }
    }


//    public static ResponseData getObjectFromString(String data) {
//        return gson.fromJson(data, ResponseData.class);
//    }
//

    public void sendToServer(String serverEvent, Object requestObject, final CloudManagerCallback<String> cloudManagerCallback) {
        localCallbackCloudManager = cloudManagerCallback;
        String jsonString = ProductTypeConverters.getStringFromObject(requestObject);
        Log.d("TAG", "sendEvent " + jsonString);
        socket.emit(serverEvent, jsonString);
    }

    public void loginRequest(Object obj, final CloudManagerCallback<String> cloudManagerCallback) {
        if (!isConnected) {
            try {
                isConnected = connectToServer();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        localCallbackCloudManager = cloudManagerCallback;
        String jsonString = ProductTypeConverters.getStringFromObject(obj);
        Log.d("TAG", "sendLoginEvent " + jsonString);
        socket.emit(EVENT_CONNECT, jsonString);
    }

    public void registerRequest(Object obj, final CloudManagerCallback<String> cloudManagerCallback) {
        if (!isConnected) {
            try {
                isConnected = connectToServer();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        localCallbackCloudManager = cloudManagerCallback;
        String jsonString = ProductTypeConverters.getStringFromObject(obj);
        Log.d("TAG", "Register " + jsonString);
        socket.emit(EVENT_CONNECT, jsonString);
    }

    ////////////////////////
    //New Methods For Wav//
    ///////////////////////
    public interface SendAudioCallback<T> {
        void onSuccees(T data);

        void onError(T error);

    }

    private final String SERVER_ADDRESS_Audio = "193.106.55.95";
    private final int SERVER_AUDIO_EVENT_PORT = 8082;
    private final int SERVER_AUDIO_DATASET_PORT = 8081;

    public void sendEvent(int eventId, byte[] data, final CloudManagerCallback<Boolean> callback) throws IOException {
        SenderObject obj = new SenderObject(eventId, null, data, callback);
        new FileSender().execute(obj);
//        boolean res = f.doInBackground(obj);
//        if(res == true)
//            callback.onSuccees(res);
//        else
//            callback.onError(res);
        Log.d("TAG","Call to async");
//        int responseFromServer = 0;
//        java.net.Socket sock = new java.net.Socket(SERVER_ADDRESS_Audio, SERVER_AUDIO_EVENT_PORT);

//        java.net.Socket sock = doInBackground("");

//        DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
//        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        outToServer.writeBytes(eventId + "\n");
//
//        //wait for acknowlage
//        responseFromServer = inFromServer.read();
////        if (responseFromServer == 0) {
////            callback.onError(false);
////            return;
////        }
//        //SendData
//        outToServer.writeBytes(data.length + "\n");
//        responseFromServer = inFromServer.read();
//        baos.write(data);
//        baos.writeTo(outToServer);
////
////        try {
////            outToServer.write(data);
////        } catch (IOException e1) {
////            e1.printStackTrace();
////        }
//        if (responseFromServer == 0) {
//            Log.d("TAG", "responseFromServerError=" + responseFromServer);
//            callback.onError(false);
//        } else if (responseFromServer == 1) {
//            Log.d("TAG", "OK=" + responseFromServer);
//            callback.onSuccees(true);
//        }
//        inFromServer.close();
//        outToServer.close();
//        baos.close();
//        sock.close();

    }

    public void sendDataSet(int userId, String lengthOfRecord, byte[] data, final CloudManagerCallback<Boolean> callback) throws IOException {
        SenderObject obj = new SenderObject(userId, lengthOfRecord, data, callback);
        new FileSender().execute(obj);
//        boolean res = f.doInBackground(obj);
////        if(res == true)
////            callback.onSuccees(res);
////        else
////            callback.onError(res);
//        int responseFromServer = 0;
//        java.net.Socket sock = new java.net.Socket(SERVER_ADDRESS_Audio, SERVER_AUDIO_DATASET_PORT);
//
//        DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
//        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        outToServer.writeBytes(userId + "\n");
//
//        //wait for acknowlage
//        responseFromServer = inFromServer.read();
//
//        //need to send Length of DataSet before the Record
//        outToServer.writeBytes(lengthOfRecord + "\n");
//        responseFromServer = inFromServer.read();
//        if (responseFromServer == 0) //send the AudioByt
//        {
//            callback.onError(false);
//            return;
//        }
//
//        //Send Data
//        outToServer.writeBytes(data.length + "\n");
//        responseFromServer = inFromServer.read();
//        baos.write(data);
//        baos.writeTo(outToServer);
//        responseFromServer = inFromServer.read();
//        if (responseFromServer == 0) {
//            Log.d("TAG", "responseFromServerError=" + responseFromServer);
//            callback.onError(false);
//        } else if (responseFromServer == 1) {
//            Log.d("TAG", "OK=" + responseFromServer);
//            callback.onSuccees(true);
//        }
//        inFromServer.close();
//        outToServer.close();
//        baos.close();
//        sock.close();
    }



    public void disconnect(CloudManagerCallback<Boolean> callback) {
        socket.disconnect();
        isConnected = false;
        callback.onComplete(true);
//        IO.Options opts = new IO.Options();
//        try {
//            socket = IO.socket(SERVER_ADDRESS, opts);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
    }
}