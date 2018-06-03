package com.example.malicteam.projectxclient.Model;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Maayan on 03-Jun-18.
 */

public class FileSender extends AsyncTask<SenderObject, Void, Boolean> {
    private final String SERVER_ADDRESS_Audio = "193.106.55.95";
    private final int SERVER_AUDIO_EVENT_PORT = 8082;
    private final int SERVER_AUDIO_DATASET_PORT = 8081;

    int responseFromServer = 0;


    @Override
    protected Boolean doInBackground(SenderObject... senderObjects) {
        java.net.Socket sock = null;
        SenderObject obj = senderObjects[0];
        int port = SERVER_AUDIO_DATASET_PORT;

        if (obj.getLengthOfRecord() == null)
            port = SERVER_AUDIO_EVENT_PORT;
        try {
            sock = new java.net.Socket(SERVER_ADDRESS_Audio, port);

            DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            outToServer.writeBytes(obj.getId() + "\n");

            //wait for acknowlage
            responseFromServer = inFromServer.read();
            Log.d("TAG","GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG  " + responseFromServer);


            if (obj.getLengthOfRecord() != null) {
                //need to send Length of DataSet before the Record
                outToServer.writeBytes(obj.getLengthOfRecord() + "\n");
                responseFromServer = inFromServer.read();
                if (responseFromServer == 0) //send the AudioByt
                {
                    return false;
                }
            }

            //Send Data
            outToServer.writeBytes(obj.getData().length + "\n");
            responseFromServer = inFromServer.read();
            baos.write(obj.getData());
            baos.writeTo(outToServer);
            responseFromServer = inFromServer.read();
            if (responseFromServer == 0) {
                Log.d("TAG", "responseFromServerError=" + responseFromServer);
                return false;
            } else if (responseFromServer == 1) {
                Log.d("TAG", "OK=" + responseFromServer);
                return true;
            }
            inFromServer.close();
            outToServer.close();
            baos.close();
            sock.close();
        } catch (Exception e) {
            Log.d("TAG", "File Sender Failed because " + e.getMessage());
            return false;
        }
        return true;
    }
}
