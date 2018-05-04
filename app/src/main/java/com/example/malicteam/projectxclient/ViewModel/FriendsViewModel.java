//package com.example.malicteam.projectxclient.ViewModel;
//
//import android.arch.lifecycle.LiveData;
//import android.arch.lifecycle.ViewModel;
//
//import java.util.List;
//
//import com.example.malicteam.projectxclient.Model.Repository;
//import com.example.malicteam.projectxclient.Model.User;
//
///**
// * Created by Maayan on 12-Mar-18.
// */
//
//public class FriendsViewModel extends ViewModel {
//    private int userId;
//    private LiveData<List<User>> friends;
//
//    public void init(int userId) {
//        this.userId = userId;
//        friends = Repository.instance.getFriends(userId);
//    }
//    public LiveData<List<User>> getFriends() {
//        return friends;
//    }
//}
