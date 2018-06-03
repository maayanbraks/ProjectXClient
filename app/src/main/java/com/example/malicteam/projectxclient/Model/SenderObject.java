package com.example.malicteam.projectxclient.Model;

/**
 * Created by Maayan on 03-Jun-18.
 */

public class SenderObject {
    private int id;
    private String lengthOfRecord;
    private byte[] data;
    public SenderObject(int id, String lengthOfRecord, byte[] data) {
        this.id = id;
        this.lengthOfRecord = lengthOfRecord;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getLengthOfRecord() {
        return lengthOfRecord;
    }

    public byte[] getData() {
        return data;
    }
}
