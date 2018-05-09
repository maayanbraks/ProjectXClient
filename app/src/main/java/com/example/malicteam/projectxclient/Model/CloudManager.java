package com.example.malicteam.projectxclient.Model;

import android.os.Bundle;
import android.util.Log;

//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import com.google.gson.Gson;

import Notifications.EventCloseNotificationData;
import Notifications.EventInvitationNotificationData;
import Notifications.NotificationData;
import Notifications.UserJoinEventNotification;
import Notifications.UserLeaveEventNotification;
import Requests.AddFriendRequestData;
import Requests.RequestData;
import Responses.ResponseData;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

/**
 * Created by Charcon on 02/05/2018.
 */

public class CloudManager {
    CloudCallback<String> localCallbackCloudManager;
    private static Gson gson = new Gson();
    private Socket socket;
    static final int PORT = 8888;


    private boolean isConnected;


    public CloudManager() throws URISyntaxException {
        isConnected = connectToServer();
        if (isConnected) ;


    }

    public interface CloudCallback<T> {
        void onComplete(T data);

        void onCancel();
    }

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

    public void onCreate(Bundle savedInstanceState) throws URISyntaxException {


    }

    public void initListeners() { // here we analize all listeners and responses

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                Log.d("TAG", "Connection with server has been establish");
               // RequestData rd = new AddFriendRequestData("test@test.com", "friend@friend.com");
                // String jsonString =new Gson().toJson(rd);
              //  socket.emit("toServer", "lalalalall");
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
                }
                else {
                    Log.d("TAG","Localcallback is null");
                }
                Log.d("TAG", "" + args[0]);
                //handleLiveData("" + args[0]);

            }


        });
    }

    public void handleLiveData(String data) { //here Live data responses (need to send to LiveData!

        NotificationData rs = gson.fromJson(data, NotificationData.class);
        switch (rs.getNotificationType()) {
            case EventInvitation:
                getObjectFromString(data, EventInvitationNotificationData.class);
                return;
            case UserJoinEvent:
                getObjectFromString(data, UserJoinEventNotification.class);
                return;
            case UserLeaveEvent:
                getObjectFromString(data, UserLeaveEventNotification.class);
                return;
            case EventClosed:
                getObjectFromString(data, EventCloseNotificationData.class);
                return;
            default:
                return;
        }
    }

    public boolean connectToServer() throws URISyntaxException {

        IO.Options opts = new IO.Options();
        socket = IO.socket("http://192.168.27.1:8080", opts);
        initListeners();

        socket.connect();
        if (isConnected)
            return true;
        return false;
    }

    public static <T> T getObjectFromString(String data, Class<T> classOfT) {
        return gson.fromJson(data, classOfT);
    }
    public static ResponseData getObjectFromString(String data) {
        return gson.fromJson(data, ResponseData.class);
    }

    public static String getStringFromObject(Object obj) {
        return gson.toJson(obj);
    }

    public void sendToServer(String event, Object obj, final CloudCallback<String> cloudManagercallback) {
        // TODO Auto-generated method stub
        localCallbackCloudManager = cloudManagercallback;
        String jsonString = getStringFromObject(obj);
        Log.d("TAG","sendEvent "+jsonString);
        socket.emit(event, jsonString);
    }
    public void loginRequest(Object obj, final CloudCallback<String> cloudManagercallback) {
        // TODO Auto-generated method stub
        localCallbackCloudManager = cloudManagercallback;
        String jsonString = getStringFromObject(obj);
        Log.d("TAG","sendEvent "+jsonString);
        socket.emit("Login", jsonString);
    }

}