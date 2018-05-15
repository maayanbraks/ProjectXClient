package com.example.malicteam.projectxclient.Common.Callbacks;

import ResponsesEntitys.UserData;

/**
 * Created by Charcon on 15/05/2018.
 */

public interface isUserExistResponeCallback {
    void onSuccees(UserData data);
    void userIsNotExist();
    void friendIsNotExist();

}
