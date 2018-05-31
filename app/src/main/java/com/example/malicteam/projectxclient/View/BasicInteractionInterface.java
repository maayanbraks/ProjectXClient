package com.example.malicteam.projectxclient.View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.malicteam.projectxclient.R;

/**
 * Created by Maayan on 17-May-18.
 */

public interface BasicInteractionInterface {
    void makeToastShort(String message);
    void makeToastLong(String message);
}
