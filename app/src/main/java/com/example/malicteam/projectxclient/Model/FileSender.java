package com.example.malicteam.projectxclient.Model;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Maayan on 01-Jun-18.
 */

public class FileSender {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void koko() {
        Path fileLocation = Paths.get("C:\\Users\\Gal\\Desktop\\last2.wav");
        byte[] data = null;
        try {
            data = Files.readAllBytes(fileLocation);
            System.out.println(data.length);

            java.net.Socket sock = new java.net.Socket("localhost", 2244);
            DataOutputStream outToClient = new DataOutputStream(sock.getOutputStream());
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bao.write(data);
            bao.writeTo(outToClient);
            sock.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

