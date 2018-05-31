package com.example.malicteam.projectxclient.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.malicteam.projectxclient.Common.ProductTypeConverters;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ResponsesEntitys.UserData;

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

    @NonNull
    public int getId() {
        return id;
    }

    private String email;
    private String friendsIds;
    private String eventsIds;
    private String pictureUrl;
    private boolean admin = false;
    private long lastUpdated;

    //    @Ignore
    public User(String firstName, String lastName, String phoneNumber, String email, List<Integer> friendsIds, List<Integer> eventsIds, long lastUpdated) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = generateId(email);
        this.phoneNumber = phoneNumber;
        this.email = email;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.lastLogin = dateFormat.format(date);
        this.pictureUrl = null;
        this.lastUpdated = lastUpdated;

        if (friendsIds != null)
            this.friendsIds = ProductTypeConverters.toString(friendsIds);
        else
            this.friendsIds = "{}";

        if (eventsIds != null)
            this.eventsIds = ProductTypeConverters.toString(eventsIds);
        else
            this.eventsIds = "{}";
    }
    public User(String firstName, String lastName, String phoneNumber, String email, List<Integer> friendsIds, List<Integer> eventsIds, long lastUpdated,int id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.email = email;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.lastLogin = dateFormat.format(date);
        this.pictureUrl = null;
        this.lastUpdated = lastUpdated;

        if (friendsIds != null)
            this.friendsIds = ProductTypeConverters.toString(friendsIds);
        else
            this.friendsIds = "{}";

        if (eventsIds != null)
            this.eventsIds = ProductTypeConverters.toString(eventsIds);
        else
            this.eventsIds = "{}";
    }

    public User(String firstName, String lastName, String phoneNumber, String email, List<Integer> friendsIds, List<Integer> eventsIds, String pictureUrl,long lastUpdated) {
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
            this.friendsIds = ProductTypeConverters.toString(friendsIds);
        else
            this.friendsIds = "{}";

        if (eventsIds != null)
            this.eventsIds = ProductTypeConverters.toString(eventsIds);
        else
            this.eventsIds = "{}";
    }
    public User(UserData userdata) { //
        this.firstName = userdata.getFirstName();
        this.lastName =  userdata.getLastName();
        this.phoneNumber =  userdata.getPhoneNumber();
        this.email = userdata.getEmail();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.lastLogin = dateFormat.format(date);
        this.pictureUrl = userdata.getPictureURL();
        this.id=132;
        //this.eventsIds = new LinkedList<Integer>();

//        if (friendsIds != null)
//            this.friendsIds = ProductTypeConverters.toString(friendsIds);
//        else
//            this.friendsIds = "{}";
//
//        if (eventsIds != null)
//            this.eventsIds = ProductTypeConverters.toString(eventsIds);
//        else
//            this.eventsIds = "{}";
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

        this.lastUpdated = 0;
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
        this.lastUpdated = date.getTime();

        if (friendsIds != null)
            this.friendsIds = ProductTypeConverters.toString(friendsIds);
        else
            this.friendsIds = "{}";

        if (eventsIds != null)
            this.eventsIds = ProductTypeConverters.toString(eventsIds);
        else
            this.eventsIds = "{}";
    }

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
        this.lastUpdated = date.getTime();

        if (friendsIds != null)
            this.friendsIds = ProductTypeConverters.toString(friendsIds);
        else
            this.friendsIds = "{}";

        if (eventsIds != null)
            this.eventsIds = ProductTypeConverters.toString(eventsIds);
        else
            this.eventsIds = "{}";
    }


    public List<User> convertUserDataToUser(List<UserData> userDataList)
    {
        if (userDataList.size()<1)
            return null;
        List<User> users= new LinkedList<>();
        for(UserData item : userDataList){
            int i=0;
            User user=new User(userDataList.get(i));
            users.add(user);
            i++;
        }
        return users;
    }
    public void addEventToList(int event) {
        List<Integer> list = ProductTypeConverters.toList(this.eventsIds);
        list.add(event);
        this.eventsIds = ProductTypeConverters.toString(list);
    }

    public void deleteEventFromList(int event) {
        int index = 0;
        if (eventsIds.contains(Integer.toString(event))) {
            List<Integer> list = ProductTypeConverters.toList(this.eventsIds);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == event)
                    index = list.get(i);
            }
            list.remove(index);
            this.eventsIds = ProductTypeConverters.toString(list);
        }
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }


    public void setEventsIds(List<Integer> eventsIds) {
        this.eventsIds = ProductTypeConverters.toString(eventsIds);
    }

    public void setFriendsIds(List<Integer> friendsIds) {
        this.friendsIds = ProductTypeConverters.toString(friendsIds);
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

//    public int getId() {
//        return 123;
//    }

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
        return ProductTypeConverters.toList(friendsIds);
    }

    public String getFriendsIds() {
        return friendsIds;
    }

    public void addFriend(int id) {
        List<Integer> list = ProductTypeConverters.toList(friendsIds);
        list.add(id);
        friendsIds = ProductTypeConverters.toString(list);
    }


    public List<Integer> getEventsIdsAsList() {
        return ProductTypeConverters.toList(eventsIds);
    }


    public String getEventsIds() {
        return eventsIds;
    }

    public void addEvent(int id) {
        List<Integer> list = ProductTypeConverters.toList(eventsIds);
        list.add(id);
        eventsIds = ProductTypeConverters.toString(list);
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", friendsIds='" + friendsIds + '\'' +
                ", eventsIds='" + eventsIds + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", admin=" + admin +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}

