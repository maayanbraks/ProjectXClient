package com.example.malicteam.projectxclient.Common.Callbacks;

/**
 * Created by Charcon on 21/05/2018.
 */

public interface CreateUserCallback<T> {
    void onSuccees(T data);
    void EmailAlreadyRegistered();
    void TechnicalError();
}
