package com.example.malicteam.projectxclient.Common.Callbacks;

/**
 * Created by Maayan on 12-May-18.
 */

public interface FriendsListCallback<T> {
    void onSuccees(T data);
    void technicalError();
    void userMustToLogin();
}


