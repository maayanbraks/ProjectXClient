package com.example.malicteam.projectxclient.Common.Callbacks;

/**
 * Created by Charcon on 16/05/2018.
 */

public interface CloseEventCallback {
    void onSuccees();
    void UserIsNotExist();
    void EventIsNotExist();
    void TechnicalError();
}
