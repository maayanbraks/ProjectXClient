package com.example.malicteam.projectxclient.Common.Callbacks;

/**
 * Created by Charcon on 30/05/2018.
 */

public interface ProtocolRequestCallback<T> {
    void TechnicalError();
    void ProtocolIsNotExist();
    void onSuccees(T data);
}
