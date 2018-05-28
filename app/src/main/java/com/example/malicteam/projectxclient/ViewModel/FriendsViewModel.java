package com.example.malicteam.projectxclient.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import com.example.malicteam.projectxclient.Common.Callbacks.FriendsListCallback;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;

/**
 * Created by Maayan on 12-Mar-18.
 */

public class FriendsViewModel extends ViewModel {
    public interface FriendsViewModelCallback<T>{
        void onComplete(T data);
    }
    public LiveData<List<User>> list = null;

    public FriendsViewModel() {
        super();
        list = Repository.instance.getFriendsLive();
    }
//
//    public void initFriendsList(FriendsViewModelCallback<Boolean> callback) {
////        this.list = Repository.instance.getFriendsMain();
//        Repository.instance.getFriendsMain(new FriendsViewModelCallback<LiveData<List<User>>>() {
//            @Override
//            public void onComplete(LiveData<List<User>> data) {
//                list = data;
//                callback.onComplete(true);
//            }
//        });
//    }

    public LiveData<List<User>> getFriendsList() {
        return list;
    }
}
