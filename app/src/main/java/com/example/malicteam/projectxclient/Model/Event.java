package com.example.malicteam.projectxclient.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Maayan on 04-Jan-18.
 */
////@Entity(tableName = "events")
//public class Event implements Serializable{
////    @PrimaryKey
////    @NonNull
@Entity(tableName = "events")
public class Event implements Serializable {
    @PrimaryKey
    @NonNull
    private int id;

    private String title;
  //  private String content;
    private String date;
    private String usersIds;
    private String recordURL;
    private String adminId;
    private String description;
    private String EventStartTime;
    private boolean isRecording;
    private static final long serialVersionUID = 1L;

    public Event(){
        this.id = 0;
        this.title = "";
 //       this.content = "";
        this.date = "";
        this.usersIds = "";
        this.recordURL = "";
        this.adminId = "";
        this.description = "";
        this.EventStartTime = "";
        this.isRecording = true;
    }

    @Ignore
    public Event(String content, String title, String usersIds, String description, String adminId, String time, String url) {
        this.isRecording = true;
    //    this.content = content;
        this.title = title;
        this.usersIds = usersIds;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = dateFormat.format(d);
        this.id = Math.abs((this.adminId + this.date).hashCode());
        EventStartTime = time;
        this.recordURL = url;

    }

    public Event(String content, String title, String usersIds, String description, String adminId, String time, int id, String url) {
        this.isRecording = true;
   //     this.content = content;
        this.title = title;
        this.usersIds = usersIds;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = dateFormat.format(d);
        this.id = id;
        this.EventStartTime = time;
        this.recordURL = url;

    }
    @Ignore
    public Event(String content, String title, String usersIds, String description, String adminId, String time, String id, String url) {
        this.isRecording = true;
  //      this.content = content;
        this.title = title;
        this.usersIds = usersIds;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = dateFormat.format(d);
        this.id = Integer.valueOf(id);
        this.EventStartTime = time;
        this.recordURL = url;

    }

    public String getEventStartTime() {
        return EventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        EventStartTime = eventStartTime;
    }

    public String getAdminId() {
        return adminId;
    }

    public int getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(String usersIds) {
        this.usersIds = usersIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRecordURL(String recordURL) {
        this.recordURL = recordURL;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecordURL() {
        return recordURL;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    @Override
    public String toString() {
        return "Event{" +
                "isRecording=" + isRecording +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", usersIds='" + usersIds + '\'' +
                ", recordURL='" + recordURL + '\'' +
                ", adminId='" + adminId + '\'' +
                ", description='" + description + '\'' +
                ", EventStartTime='" + EventStartTime + '\'' +
                '}';
    }
}
