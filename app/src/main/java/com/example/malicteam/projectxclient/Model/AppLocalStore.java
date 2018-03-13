//package com.example.malicteam.projectxclient.Model;
//
//import android.arch.persistence.room.Database;
//import android.arch.persistence.room.Room;
//import android.arch.persistence.room.RoomDatabase;
//
///**
// * Created by Maayan on 12-Mar-18.
// */
//
//@Database(entities = {Event.class, User.class}, version = 1)
//abstract class AppLocalStoreDb extends RoomDatabase {
//    public abstract Event eventDao();
//
//    public abstract UserDao userDao();
//}
//
//public class AppLocalStore {
//    static public AppLocalStoreDb db = Room.databaseBuilder(Outalk.getMyContext(),
//            AppLocalStoreDb.class,
//            "Outalk-database").fallbackToDestructiveMigration().build();
//}