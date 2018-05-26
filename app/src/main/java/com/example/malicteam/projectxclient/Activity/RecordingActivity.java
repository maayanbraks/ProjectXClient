package com.example.malicteam.projectxclient.Activity;

import android.Manifest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.malicteam.projectxclient.Common.Callbacks.RecordingActivityCallback;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.IRecorder;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.Model.WavRecorder;
import com.example.malicteam.projectxclient.R;
import com.example.malicteam.projectxclient.ViewModel.UserViewModel;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;

public class RecordingActivity extends AppCompatActivity {
    private final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private boolean mStartPlaying = true;
    private IRecorder recorder = null;
    private String mFileName = null;
    private User myUser;

    private ImageButton playingButton;
    private ImageButton recordingButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        //get user
        myUser = (User) getIntent().getSerializableExtra(Consts.USER);
        //final file name
        mFileName = String.valueOf(Math.abs(System.currentTimeMillis())).hashCode() + ".wav";
        //Init recorder
        recorder = new WavRecorder(mFileName);
        Repository.instance.InitCallbacksForCloudManeger(new RecordingActivityCallback() {
            @Override
            public void userJoinEvent(int userId) {
                userHasJoinTheEvent(userId);
            }

            @Override
            public void userLeftEvent(int userId) {
                userHasLeftTheEvent((userId));
            }

            @Override
            public void eventClosed(int eventId) {
                StopRecordingByAdmin();
            }

        });

        initButtons();
        initEvent();
    }

    private void initEvent(){
        if (getIntent().getSerializableExtra(Consts.SEND_EVENT) != null) {//Does it should be Conts.Event
            eventtemp = (Event) getIntent().getSerializableExtra(Consts.SEND_EVENT);
            event = eventtemp;
            SetEventFromNewActivity();
            FromInvitation = false;
        }
        if (getIntent().getSerializableExtra("eventFromInvitation") != null) {
            event = (Event) getIntent().getSerializableExtra("eventFromInvitation");
            SetEventFromInvitation(event);
            FromInvitation = true;
        }
    }

    private void initButtons(){
        TextView backButton = (TextView) findViewById(R.id.back_btn_recording);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mStartPlaying)
                    stopPlaying();
                if (!mStartRecording)
                    recordOrSave();

                finish();
            }
        });
        recordingButton = (ImageButton) findViewById(R.id.btnStop);
        // Set layout only if admin or not
        recordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckMeAdmin()) {
                    recordOrSave();
                }
            }
        });

        playingButton = (ImageButton) findViewById(R.id.btnStart);
        playingButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStartPlaying && CheckMeAdmin()) {
                            playOrPause();
                        }
                    }
                });
    }

}
