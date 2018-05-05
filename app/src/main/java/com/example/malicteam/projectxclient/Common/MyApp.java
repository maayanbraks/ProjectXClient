package com.example.malicteam.projectxclient.Common;

import android.app.Application;
import android.content.Context;

/**
 * Created by Maayan on 12-Mar-18.
 */

public class MyApp extends Application {
    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

}
