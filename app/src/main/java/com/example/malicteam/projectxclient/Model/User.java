package com.example.malicteam.projectxclient.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Maayan on 10-Jan-18.
 */

@Entity(tableName = "users")
public class User implements Serializable {
    @PrimaryKey
    @NonNull
    private int id;//hash code from email

    private String firstName;
    private String lastName;
    private String lastLogin;
    private String phoneNumber;
    private String email;

//    @TypeConverters(ProductTypeConverters.class)
//    private List<Integer> friendsIds;

//    @TypeConverters(ProductTypeConverters.class)
//    private List<Integer> eventsIds;

    private String friendsIds;
    private String eventsIds;

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
            this.friendsIds = FirebaseModel.generateStringFromList(friendsIds);
        else
            this.friendsIds = "{}";

        if (eventsIds != null)
            this.eventsIds = FirebaseModel.generateStringFromList(eventsIds);
        else
            this.eventsIds = "{}";
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
        //this.eventsIds = new LinkedList<Integer>();

        if (friendsIds != null)
            this.friendsIds = FirebaseModel.generateStringFromList(friendsIds);
        else
            this.friendsIds = "{}";

        if (eventsIds != null)
            this.eventsIds = FirebaseModel.generateStringFromList(eventsIds);
        else
            this.eventsIds = "{}";
    }

    public User() {
        this.firstName = "";
        this.lastName = "";
        this.phoneNumber = null;
        this.email = email;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.lastLogin = dateFormat.format(date);
        //this.eventsIds = new LinkedList<Integer>();
        if (friendsIds != null)
            this.friendsIds = friendsIds;
        else
            this.friendsIds = "{}";

        if (eventsIds != null)
            this.eventsIds = eventsIds;
        else
            this.eventsIds = "{}";
    }

    public void addEventToList(int event) {
        List<Integer> list = FirebaseModel.decodeListFromString(this.eventsIds);
        list.add(event);
        this.eventsIds = FirebaseModel.generateStringFromList(list);
    }

    public void deleteEventFromList(int event) {
        int index = 0;
        if (eventsIds.contains(Integer.toString(event))) {
            List<Integer> list = FirebaseModel.decodeListFromString(this.eventsIds);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == event)
                    index = list.get(i);
            }
            list.remove(index);
            this.eventsIds = FirebaseModel.generateStringFromList(list);
        }
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

        int size = 0;
        size = (friendsIds.split(",").length - 1);
        str += "Friends Count:" + size +"\n";
        str += "Friends:" + friendsIds;
        size = (eventsIds.split(",").length - 1);
        str += "Events Count:" + size + "\n";
        str += "Events:" + eventsIds;

        return str;

    }

    public void setEventsIds(List<Integer> eventsIds) {
        this.eventsIds = FirebaseModel.generateStringFromList(eventsIds);
    }

    public void setFriendsIds(List<Integer> friendsIds) {
        this.friendsIds = FirebaseModel.generateStringFromList(friendsIds);
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

    public List<Integer> getFriendsIdsAsList() {
        return FirebaseModel.decodeListFromString(friendsIds);
    }

    public String getFriendsIds() {
        return friendsIds;
    }

    public void addFriend(int id) {
        List<Integer> list = FirebaseModel.decodeListFromString(friendsIds);
        list.add(id);
        friendsIds = FirebaseModel.generateStringFromList(list);
    }


    public List<Integer> getEventsIdsAsList() {
        return FirebaseModel.decodeListFromString(eventsIds);
    }


    public String getEventsIds() {
        return eventsIds;
    }

    public void addEvent(int id) {
        List<Integer> list = FirebaseModel.decodeListFromString(eventsIds);
        list.add(id);
        eventsIds = FirebaseModel.generateStringFromList(list);
    }

    public void setFriendsIds(String friendsIds) {
        this.friendsIds = friendsIds;
    }

    public void setEventsIds(String eventsIds) {
        this.eventsIds = eventsIds;
    }

    public static int generateId(String email) {
        return Math.abs(email.hashCode());
    }
}
