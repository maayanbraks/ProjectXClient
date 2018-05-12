package com.example.malicteam.projectxclient.Common.Callbacks;

/**
 * Created by Maayan on 12-May-18.
 */


public interface AddFriendCallback<T>{
    void onSuccees(T data);
    void userIsNotExist();
    void friendIsNotExist();
    void bothUsersEquals();
    void alreadyFriends();
}
