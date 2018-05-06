package com.example.malicteam.projectxclient.Model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.malicteam.projectxclient.Common.ProductTypeConverters;

/**
 * Created by Maayan on 12-Mar-18.
 */

@Database(entities = {Event.class, User.class}, version = 1)
@TypeConverters(ProductTypeConverters.class)
public abstract class AppLocalStoreDb extends RoomDatabase {
    public abstract EventDao EventDao();
    public abstract UserDao UserDao();

    private static AppLocalStoreDb instance;

    public static AppLocalStoreDb getLocalDatabase(Context context){
        if(instance == null){
            instance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),AppLocalStoreDb.class).allowMainThreadQueries().build();
        }
        return instance;
    }

    public static void killInstance(){
        instance = null;
    }
}