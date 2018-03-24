package com.example.malicteam.projectxclient.Model;//package com.example.malicteam.projectxclient.Model;
//
//import java.io.Serializable;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by Maayan on 04-Jan-18.
// */
//
//
//
//
//
////@Entity(tableName = "events")
//public class Event implements Serializable{
////    @PrimaryKey
////    @NonNull
//    private int id;
//    private String contentUrl;
//    private String title;
//    private final String date;
//    private List<Integer> usersIds;
//    private String description;
//    private int adminId;
//
//    private static final long serialVersionUID = 1L;
//
//    public Event(String contentUrl, String title, List<Integer> usersIds, String description, int adminId, Date date) {
//        this.contentUrl = contentUrl;
//        this.title = title;
//        this.usersIds = usersIds;
//        this.description = description;
//        this.adminId = adminId;
//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
//        Date d = date;
//        this.date = dateFormat.format(d);
//        this.id = Math.abs((this.adminId + this.date).hashCode());
//        generateId();
//    }
//
//    public int getAdminId() {
//        return adminId;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public String getContentUrl() {
//        return contentUrl;
//    }
//
//    public void setContent(String url) {
//        this.contentUrl = url;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public List<Integer> getUsersIds() {
//        return usersIds;
//    }
//
//    public void setUsersIds(List<Integer> usersIds) {
//        this.usersIds = usersIds;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public void generateId() {
//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
//        Date d = new Date();
//        String str = dateFormat.format(d);
//        str += Integer.toString(adminId);
//        this.id = Math.abs(str.hashCode());
//    }
//}

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

public class Event implements Serializable{

    private static final long serialVersionUID = 1L;

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    private boolean isRecording;
    private int id;
    private String content;
    private String title;
    private String date;
    private String usersIds;
    private String recordURL;
    private String adminId;
    public String getRecordURL() {
        return recordURL;
    }

    public void setRecordURL(String recordURL) {
        this.recordURL = recordURL;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private String description;

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }




    private String EventStartTime;

    public void setId(int id) {
        this.id = id;
    }

    public Event(String content, String title, String usersIds, String description, String adminId, String time, String url) {
        this.isRecording=true;
        this.content = content;
        this.title = title;
        this.usersIds = usersIds;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = dateFormat.format(d);
        this.id = Math.abs((this.adminId + this.date).hashCode());
        EventStartTime=time;
        this.recordURL=url;

    }
    public Event(String content, String title,String usersIds, String description, String adminId,String time,int id,String url) {
        this.isRecording=true;
        this.content = content;
        this.title = title;
        this.usersIds = usersIds;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = dateFormat.format(d);
        this.id = id;
        this.EventStartTime=time;
        this.recordURL=url;

    }
    public Event(String content, String title,String usersIds, String description, String adminId,String time,String id,String url) {
        this.isRecording=true;
        this.content = content;
        this.title = title;
        this.usersIds = usersIds;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        this.date = dateFormat.format(d);
        this.id = Integer.valueOf(id);
        this.EventStartTime=time;
        this.recordURL=url;

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

    @Override
    public String toString() {
        return "Event{" +
                "isRecording=" + isRecording +
                ", id=" + id +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", usersIds='" + usersIds + '\'' +
                ", recordURL='" + recordURL + '\'' +
                ", adminId='" + adminId + '\'' +
                ", description='" + description + '\'' +
                ", EventStartTime='" + EventStartTime + '\'' +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addContent(String content) {
        this.content.concat("\n" + content);
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



}
