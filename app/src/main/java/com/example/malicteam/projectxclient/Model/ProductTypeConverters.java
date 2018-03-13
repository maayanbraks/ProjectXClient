//package com.example.malicteam.projectxclient.Model;
//
//import android.arch.persistence.room.TypeConverter;
//
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * Created by Maayan on 12-Mar-18.
// */
//
//public class ProductTypeConverters {
//
//    @TypeConverter
//    public static LinkedList<Integer> decodeListFromString(String list) {
//        list = list.replace("{", "").replace("}", "").replace(" ", "");
//        String[] ids = list.split(",");
//        LinkedList<Integer> friendsList = new LinkedList<>();
//        for (String s : ids) {
//            friendsList.add(Integer.parseInt(s));
//        }
//        return friendsList;
//    }
//
//    @TypeConverter
//    public static String generateStringFromList(List<Integer> list) {
//        String str = "{ ";
//        for (int id : list) {
//            str += id + ", ";
//        }
//        str += "}";
//        return str;
//    }
//}