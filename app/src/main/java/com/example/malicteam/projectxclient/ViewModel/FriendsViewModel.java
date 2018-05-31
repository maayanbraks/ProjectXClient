package com.example.malicteam.projectxclient.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

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
        list = Repository.instance.getFriendsLiveData();
    }

    public LiveData<List<User>> getFriendsList() {
        return list;
    }
}
