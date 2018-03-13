//package com.example.malicteam.projectxclient.Model;
//
//import android.arch.persistence.room.Dao;
//import android.arch.persistence.room.Delete;
//import android.arch.persistence.room.Insert;
//import android.arch.persistence.room.OnConflictStrategy;
//import android.arch.persistence.room.Query;
//
//import java.util.List;
//
///**
// * Created by Maayan on 12-Mar-18.
// */
//
//@Dao
//public interface UserDao {
//    @Query("SELECT * FROM users")
//    List<User> getAllUsers();
//
//    @Query("SELECT * FROM users WHERE id = :userId")
//    User getUser(int userId);
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void saveUser(User user);
//
//    @Delete
//    void deleteUser(User employee);
//}
