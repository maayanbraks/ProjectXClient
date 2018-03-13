package com.example.malicteam.projectxclient.Model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Maayan on 04-Jan-18.
 */





//@Entity(tableName = "events")
public class Event implements Serializable{
//    @PrimaryKey
//    @NonNull
    private int id;
    private String contentUrl;
    private String title;
    private final String date;
    private List<Integer> usersIds;
    private String description;
    private int adminId;

    private static final long serialVersionUID = 1L;

    public Event(String contentUrl, String title, List<Integer> usersIds, String description, int adminId, Date date) {
        this.contentUrl = contentUrl;
        this.title = title;
        this.usersIds = usersIds;
        this.description = description;
        this.adminId = adminId;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = date;
        this.date = dateFormat.format(d);
        this.id = Math.abs((this.adminId + this.date).hashCode());
        generateId();
    }

    public int getAdminId() {
        return adminId;
    }

    public int getId() {
        return id;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContent(String url) {
        this.contentUrl = url;
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

    public List<Integer> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<Integer> usersIds) {
        this.usersIds = usersIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void generateId() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:MM:SS");
        Date d = new Date();
        String str = dateFormat.format(d);
        str += Integer.toString(adminId);
        this.id = Math.abs(str.hashCode());
    }
}
