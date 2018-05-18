package com.example.malicteam.projectxclient.Common.Callbacks;

import com.example.malicteam.projectxclient.Common.ProductTypeConverters;

import Notifications.UserJoinEventNotification;
import Notifications.UserLeaveEventNotification;

/**
 * Created by Charcon on 18/05/2018.
 */

public interface RecordingActivityCallback {
    void userJoinEvent(int userId);
    void userLeftEvent(int userId);
    void eventClosed();

}
