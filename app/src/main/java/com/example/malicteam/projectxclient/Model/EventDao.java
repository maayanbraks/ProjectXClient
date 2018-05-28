package com.example.malicteam.projectxclient.Model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Maayan on 12-Mar-18.
 */

@Dao
public interface EventDao {
    @Query("SELECT * FROM events")
    List<Event> getAllEvents();
//
//    @Query("SELECT * FROM events WHERE events.participats LIKE '%'+:userId+'%' ")
//    List<Event> getAllUserEvents(int userId);

//    @Query("SELECT * FROM events WHERE events.id = :eventId")
//    Event getEvent(int eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveEvent(Event event);

    @Delete
    void deleteUser(Event event);
}
