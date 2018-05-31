package com.example.malicteam.projectxclient.Common.Callbacks;

/**
 * Created by Maayan on 12-May-18.
 */

public interface LogInCallback<T> {
    void onBoolean(boolean bool);
    void technicalError();
    void userIsNotExist();
    void login(T data);
    void UseIsAllReadyLoggedIn();
    void IncorrectCredentials();
}
