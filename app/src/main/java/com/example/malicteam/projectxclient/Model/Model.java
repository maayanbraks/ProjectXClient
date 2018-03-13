package com.example.malicteam.projectxclient.Model;

import android.graphics.Bitmap;

/**
 * Created by Maayan on 11-Mar-18.
 */

public class Model {
    public final static Model Instace = new Model();

    public interface SaveImageListener {
        void complete(String url);
        void fail();
    }

    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }


}
