package com.example.malicteam.projectxclient.Model;

/**
 * Created by Maayan on 03-Jun-18.
 */

public class SenderObject {
    private int id;
    private String lengthOfRecord;
    private byte[] data;
    private CloudManager.CloudManagerCallback<Boolean> callback;
    public SenderObject(int id, String lengthOfRecord, byte[] data, CloudManager.CloudManagerCallback<Boolean> callback) {
        this.id = id;
        this.lengthOfRecord = lengthOfRecord;
        this.data = data;
        this.callback = callback;
    }

    public int getId() {
        return id;
    }

    public CloudManager.CloudManagerCallback<Boolean> getCallback() {
        return callback;
    }

    public String getLengthOfRecord() {
        return lengthOfRecord;
    }

    public byte[] getData() {
        return data;
    }
}
