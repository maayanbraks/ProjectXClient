package com.example.malicteam.projectxclient.Common.Callbacks;

/**
 * Created by Charcon on 21/05/2018.
 */

public interface DeclineToEventCallback<T> {
    void onSuccees(T data);
    void TechnicalError();
    void UserIsNotExist();
    void NoPendingEvents();

}
