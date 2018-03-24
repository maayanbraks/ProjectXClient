package com.example.malicteam.projectxclient.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Maayan on 10-Jan-18.
 */

//@Entity(tableName = "users")
public class User implements Serializable {
//    @PrimaryKey
//    @NonNull
    private int id;//hash code from email

    private String firstName;
    private String lastName;
    private String lastLogin;
    private String phoneNumber;
    private String email;

//    @TypeConverters(ProductTypeConverters.class)
    private List<Integer> friendsIds;

//    @TypeConverters(ProductTypeConverters.class)



    private List<Integer> eventsIds;

    private String pictureUrl;
    private boolean admin = false;

//    @Ignore
    public User(String firstName, String lastName, String phoneNumber, String email, List<Integer> friendsIds, List<Integer> eventsIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = generateId(email);
        this.phoneNumber = phoneNumber;
        this.email = email;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.lastLogin = dateFormat.format(date);
        this.pictureUrl = null;

        if (friendsIds != null)
            this.friendsIds = friendsIds;
        else
            this.friendsIds = new LinkedList<Integer>();

        if (eventsIds != null)
            this.eventsIds = eventsIds;
        else
            this.eventsIds = new LinkedList<Integer>();
    }

    public User(String firstName, String lastName, String phoneNumber, String email, List<Integer> friendsIds, List<Integer> eventsIds, String pictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = generateId(email);
        this.phoneNumber = phoneNumber;
        this.email = email;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.lastLogin = dateFormat.format(date);
        this.pictureUrl = pictureUrl;
        this.eventsIds = new LinkedList<Integer>();

        if (friendsIds != null)
            this.friendsIds = friendsIds;
        else
            this.friendsIds = new LinkedList<Integer>();

        if (eventsIds != null)
            this.eventsIds = eventsIds;
        else
            this.eventsIds = new LinkedList<Integer>();
    }
    public User() {
        this.firstName = "";
        this.lastName = "";
        this.phoneNumber = null;
        this.email = email;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.lastLogin = dateFormat.format(date);
        this.eventsIds = new LinkedList<Integer>();
        if (friendsIds != null)
            this.friendsIds = friendsIds;
        else
            this.friendsIds = new LinkedList<Integer>();

        if (eventsIds != null)
            this.eventsIds = eventsIds;
        else
            this.eventsIds = new LinkedList<Integer>();
    }
    public void addEventToList(int event) {
        this.eventsIds.add(event);
    }
    public void deleteEventFromList(int event) {
        int index=0;
        if (eventsIds.contains(event))
            for (int i=0;i<eventsIds.size();i++) {
            if (eventsIds.get(i)==event)
                index=eventsIds.get(i);
            }
            eventsIds.remove(index);
    }
    @Override
    public String toString() {
        String str = "";
        str += "First Name: " + firstName + "\n";
        str += "Last Name: " + lastName + "\n";
        str += "Email: " + email + "\n";
        str += "Phone: " + phoneNumber + "\n";
        str += "Id: " + id + "\n";
        str += "Date: " + lastLogin + "\n";
        if (pictureUrl != null)
            str += "PictureUrl:" + pictureUrl + "\n";

        str += "Friends Count:" + friendsIds.size() + "\n";
        int index = 1;
        for (int id : friendsIds) {
            str += "\t" + index + ") " + id + "\n";
            index++;
        }
        str += "Events Count:" + eventsIds.size() + "\n";
        index = 1;
        for (int id : eventsIds) {
            str += "\t" + index + ") " + id + "\n";
            index++;
        }
        return str;

    }

    public void setEventsIds(List<Integer> eventsIds) {
        this.eventsIds = eventsIds;
    }

    public void setFriendsIds(List<Integer> friendsIds) {
        this.friendsIds = friendsIds;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId() {
        this.id = generateId(email);
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String url) {
        this.pictureUrl = url;
    }

    public List<Integer> getFriendsIds() {
        return friendsIds;
    }

    public void addFriend(int id) {
        friendsIds.add(id);
    }

    public List<Integer> getEventsIds() {
        return eventsIds;
    }

    public void addEvent(int id) {
        eventsIds.add(id);
    }

    public static int generateId(String email) {
        return Math.abs(email.hashCode());
    }
}
