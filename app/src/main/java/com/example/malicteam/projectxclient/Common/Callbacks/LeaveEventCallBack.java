package com.example.malicteam.projectxclient.Common.Callbacks;

/**
 * Created by Charcon on 30/05/2018.
 */

public interface LeaveEventCallBack<T> {
    void TechnicalError();
    void NoPendingEvents();
    void onSuccees(T data);

}
