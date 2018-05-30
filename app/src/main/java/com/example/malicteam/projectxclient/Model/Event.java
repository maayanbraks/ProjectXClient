package com.example.malicteam.projectxclient.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import ResponsesEntitys.EventData;
import ResponsesEntitys.UserData;

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

    public List<User> getParticipats() {
        return participats;
    }

    public void setParticipats(List<User> participats) {
        this.participats = participats;
    }

    @Ignore
    private List<User> participats;
    private String adminId;
    private String description;
    private String EventStartTime;
    private boolean isRecording;

    public boolean isConverted() {
        return isConverted;
    }

    public void setConverted(boolean converted) {
        isConverted = converted;
    }

    private boolean isConverted;
    private static final long serialVersionUID = 1L;

    public Event() {
        this.id = 0;
        this.title = "";
        //       this.content = "";
        this.date = "";
        participats = new LinkedList<User>();
        this.adminId = "";
        this.description = "";
        this.EventStartTime = "";
        this.isRecording = true;
        this.isConverted=false;
    }

    @Ignore
    public Event(String content, String title, List<User> participats, String description, String adminId, String time) {
        this.isRecording = true;
        //    this.content = content;
        this.title = title;
        this.participats = participats;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = dateFormat.format(d);
        this.id = Math.abs((this.adminId + this.date).hashCode());
        EventStartTime = time;

    }

    public Event(String content, String title, List<User> participats, String description, String adminId, String time, int id) {
        this.isRecording = true;
        //     this.content = content;
        this.title = title;
        this.participats = participats;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = dateFormat.format(d);
        this.id = id;
        this.EventStartTime = time;

    }

    @Ignore
    public Event(String content, String title, List<User> participats, String description, String adminId, String time, String id) {
        this.isRecording = true;
        //      this.content = content;
        this.title = title;
        this.participats = participats;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = dateFormat.format(d);
        this.id = Integer.valueOf(id);
        this.EventStartTime = time;

    }

    public Event(EventData eventData) {
        this.isRecording = true;
        //this.content = eventData.get;
        this.title = eventData.getTitle();
        this.participats = ProductTypeConverters.GenerateListUserFromListDataUser(eventData.getParticipants());
        this.description = eventData.getDescription();
        this.adminId = eventData.getAdminMail();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = eventData.getDateCreated();
        this.id = eventData.getId();
        //TODO
        //set the starttime to eventdata starttime
        this.EventStartTime = " " ;
        this.isConverted=eventData.getIsConverted();
        this.isRecording=eventData.getIsRecording();
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

    public void addToParticipats(User user) {
        this.participats.add(user);
    }

    public void delFromParticipats(User user)
    {
        this.participats.remove(user);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                ", usersIds='" + participats + '\'' +
                ", adminId='" + adminId + '\'' +
                ", description='" + description + '\'' +
                ", EventStartTime='" + EventStartTime + '\'' +
                '}';
    }
}
