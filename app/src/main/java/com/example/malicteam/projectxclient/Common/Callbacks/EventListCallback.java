package com.example.malicteam.projectxclient.Common.Callbacks;

/**
 * Created by Maayan on 12-May-18.
 */

public interface EventListCallback<T> {
    void onSuccees(T data);
    void UserIsNotExist();
    void userMustToLogin();
}


