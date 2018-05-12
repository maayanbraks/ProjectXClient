package com.example.malicteam.projectxclient.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.Model.Repository;

/**
 * Created by Maayan on 12-Mar-18.
 */

//OLD - Live Data
//public class UserViewModel extends ViewModel {
//    private int userId;
//    private LiveData<User> user;
//
//    public void init(int userId, boolean isMainUser) {
//        this.userId = userId;
//        if (isMainUser)
//            user = Repository.instance.getUser(userId);
//        else
//            user = Repository.instance.getSomeUser(userId);
//    }
//    public LiveData<User> getUser() {
//        return user;
//    }
//    public void initUser(User user,boolean isMainUser) {
//        this.user=Repository.instance.getUserMain(user);
//
//    }
//}

public class UserViewModel extends ViewModel {
    private LiveData<User> user = null;

    public void initUser(User user) {
        this.user = Repository.instance.getUserMain(user);
    }

    public LiveData<User> getUser() {
        return user;
    }

}