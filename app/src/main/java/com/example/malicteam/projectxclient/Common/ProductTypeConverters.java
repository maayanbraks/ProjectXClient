package com.example.malicteam.projectxclient.Common;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.example.malicteam.projectxclient.Model.CloudManager;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.User;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import ResponsesEntitys.EventData;
import ResponsesEntitys.UserData;

/**
 * Created by Maayan on 12-Mar-18.
 */

public class ProductTypeConverters {
    private static Gson gson = new Gson();

    @TypeConverter
    public static LinkedList<Integer> toList(String list) {
        list = list.replace("{", "").replace("}", "").replace(" ", "");
        String[] ids = list.split(",");
        LinkedList<Integer> finalList = new LinkedList<>();
        for (String s : ids) {
            if (s != null && s != "")
                finalList.add(Integer.parseInt(s));
        }
        return finalList;
    }

    @TypeConverter
    public static String toString(List<Integer> list) {
        String str = "{ ";
        for (int id : list) {
            str += id + ", ";
        }
        str += "}";
        return str;
    }

    public static LinkedList<String> GenerateListUserToListMails(List<User> users) {
        LinkedList<String> list = new LinkedList<>();
        if (users != null) {
            for (User user : users) {
                list.add(user.getEmail());
            }
        }
        return list;
    }

    public static String GenerateStringFromList(List<String> list) {
        String newlist = "";
        for (String num : list) {
            if (!(list.equals(""))) {
                newlist = newlist + "," + num;

            } else newlist = "" + num;
            //newlist=newlist.substring(0,newlist.length()-1);
        }


        return newlist;
    }

    public static List<UserData> GenerateListUserDataFromListUser(List<User> users) {
        List<UserData> usersData = new LinkedList<UserData>();
        for (int i = 0; i < users.size(); i++) {
            usersData.add(new UserData(users.get(i).getFirstName(), users.get(i).getLastName(), users.get(i).getEmail(), users.get(i).getPictureUrl(), users.get(i).getPhoneNumber()));
        }
        return usersData;
    }

    public static List<User> GenerateListUserFromListDataUser(List<UserData> usersData) {
        List<User> users = new LinkedList<>();
        for (int i = 0; i < usersData.size(); i++) {
            //    Log.d("TAG","userdata="+usersData.get(i).getFirstName());
            users.add(new User(usersData.get(i)));
        }
        return users;
    }

    //GSON
    public static <T> T getObjectFromString(String data, Class<T> classOfT) {
        return gson.fromJson(data, classOfT);
    }

    public static String getStringFromObject(Object obj) {
        return gson.toJson(obj);
    }
    //GSON


    public static String getAdminFirstNameByEmail(Event event) {
        if (event != null) {

            for (int i = 0; i < event.getParticipats().size(); i++) {
                Log.d("TAG", "(event.getAdminId()=" + (event.getAdminId()) + "event.getParticipats().get(i).getEmail()" + event.getParticipats().get(i).getEmail());
                if (event.getAdminId().equals(event.getParticipats().get(i).getEmail())) {
                    return event.getParticipats().get(i).getFirstName();
                }
            }
        }

        return null;
    }

    public static byte[] convertFileToByte(String filePath) throws IOException {
        File file = new File(filePath);
        int fileSize = (int) file.length();
        byte[] bytesArray = new byte[fileSize];

        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        buf.read(bytesArray, 0, bytesArray.length);
        buf.close();

        return bytesArray;
    }
}