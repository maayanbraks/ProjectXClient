package com.example.malicteam.projectxclient.Common.Callbacks;

import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.User;

import Notifications.UserJoinEventNotification;
import Notifications.UserLeaveEventNotification;

/**
 * Created by Charcon on 18/05/2018.
 */

public interface RecordingActivityCallback {
    void userJoinEvent(User user);
    void userLeftEvent(User user);
    void eventClosed(Event event);

}
