package com.example.malicteam.projectxclient.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;

/**
 * Created by Maayan on 12-Mar-18.
 */

public class EventsViewModel extends ViewModel{
    public interface EventsViewModelCallback<T>{
        void onComplete(T data);
    }
    public LiveData<List<Event>> list = null;

    public EventsViewModel() {
        super();
        list = Repository.instance.getEventsLiveData();
    }

    public LiveData<List<Event>> getEventsList() {
        return list;
    }
}
