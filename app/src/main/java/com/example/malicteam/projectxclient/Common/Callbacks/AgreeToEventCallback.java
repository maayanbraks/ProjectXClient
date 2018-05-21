package com.example.malicteam.projectxclient.Common.Callbacks;

/**
 * Created by Charcon on 21/05/2018.
 */

public interface AgreeToEventCallback<T> {
    void onSuccees(T data);
    void NoPendingEvents();
    void UserIsNotExist();
}
