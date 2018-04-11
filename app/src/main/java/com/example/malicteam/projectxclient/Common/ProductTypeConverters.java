package com.example.malicteam.projectxclient.Common;

import android.arch.persistence.room.TypeConverter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maayan on 12-Mar-18.
 */

public class ProductTypeConverters {

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
}