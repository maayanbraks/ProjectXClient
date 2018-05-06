package com.example.malicteam.projectxclient.Activity;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.FirebaseModel;
import com.example.malicteam.projectxclient.Model.Model;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.R;

import java.io.IOException;
import java.util.List;

import android.Manifest;

public class RecordingActivity extends AppCompatActivity {

    private boolean mStartPlaying = true;
    private String eventIdTogetIn;
    private ImageButton recordingButton;
    private boolean mStartRecording = true;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;
    private ImageButton playingButton;

    private MediaRecorder mRecorder = null;
    private Event event;
    private MediaPlayer mPlayer = null;
    private int userId;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        eventIdTogetIn = " ";
        userId = getIntent().getIntExtra(Consts.USER_ID, Consts.DEFAULT_UID);
        mFileName = getExternalCacheDir().getAbsolutePath();
        //ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        TextView backButton = (TextView) findViewById(R.id.back_btn_recording);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mStartPlaying)
                    stopPlaying();
                if(!mStartRecording)
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

        if (getIntent().getSerializableExtra("sendNewEvent") != null) {
            event = (Event) getIntent().getSerializableExtra("sendNewEvent");
        }
        if (getIntent().getSerializableExtra("eventidToGetIn") != null) {
            eventIdTogetIn = getIntent().getStringExtra("eventidToGetIn");
        }
        //
        if (!(eventIdTogetIn.equals(" "))) //means got enter by invite
        {
            SetEventFromInvitation(eventIdTogetIn);
        } else {// entered this activity from Creating new one(NewEventActivity)
            SetEventFromNewActivity();
        }
    }


    private void playOrPause() {
        //Sisso's Recored
        /*
        //Maayan Note: I added && mStartRecording for prevent play and hear together.
         */
        if (mStartPlaying && mStartRecording) {//if to start playing
            playingButton.setImageResource(android.R.drawable.ic_media_pause);
            mStartPlaying = false;
            startPlaying();
        } else{
            recordingButton.setImageResource(android.R.drawable.ic_media_play);
            mStartPlaying = true;
            stopPlaying();
        }
    }

    private void recordOrSave() {
        //Sisso's Recored
        TextView onAir = findViewById(R.id.onAir_recording);
        if (mStartRecording) {//if to start record
            recordingButton.setImageResource(android.R.drawable.ic_menu_save);
            onAir.setVisibility(View.VISIBLE);
            mStartRecording = false;
            playingButton.setImageResource(android.R.drawable.ic_media_play);
            playingButton.setClickable(false);
            playingButton.setVisibility(View.INVISIBLE);
            startRecording();
        } else {
            recordingButton.setImageResource(android.R.drawable.ic_btn_speak_now);
            mStartRecording = true;
            stopRecording();
            onAir.setVisibility(View.INVISIBLE);
            playingButton.setClickable(true);
            playingButton.setVisibility(View.VISIBLE);
            uploadFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }


    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }


    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            Toast.makeText(getApplication(), "Recording..", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        if (CheckMeAdmin()) {
            setRecordingStatus();
        }
        Log.d("TAG", "Stop recording func");

        uploadFile();
        //FirebaseModel.addNewEvent(event);

    }


    private void StopRecording() {
        //TODO stop service of recording
    }

    private void SaveConversation() {
        //TODO save pdf file
    }

    private boolean CheckMeAdmin() {
        if (userId != Consts.DEFAULT_UID) {
            Log.d("TAG", "userId=" + userId);
            Log.d("TAG", "event.getadminID=" + event.getAdminId());
            if (String.valueOf(userId).equals(event.getAdminId())) {
                //Log.d("TAG","EQUAL");
                return true;
            }


            return false;
        }
        return false;
    }

    private void uploadFile() {
        Toast.makeText(getApplication(), "Uploading...", Toast.LENGTH_SHORT).show();
        Repository.instance.saveRecord(String.valueOf(userId), mFileName, "" + event.getId(), new Model.SaveAudioListener() {
            @Override
            public void complete(String url) {
//                Toast.makeText(getApplication(), "Upload as succeed" , Toast.LENGTH_SHORT).show();
//                event.setRecordURL(url);
            }

            @Override
            public void fail() {
                Toast.makeText(getApplication(), "Upload failed.", Toast.LENGTH_SHORT).show();
            }
        }, new FirebaseModel.FirebaseCallback<Boolean>() {
            @Override
            public void onComplete(Boolean data) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    public void SetEventFromInvitation(String eventid) {
        Repository.instance.getEventById(Integer.valueOf(eventid), new FirebaseModel.FirebaseCallback<List<Event>>() {

            @Override
            public void onComplete(List<Event> EventList) {
                if (EventList.size() != 0) //
                {
                    //getting event informatio
                    event = EventList.get(0);
                    event.setTitle(EventList.get(0).getTitle());
                    event.setId(EventList.get(0).getId());
                    event.setDate(EventList.get(0).getDate());
                    event.setDescription(EventList.get(0).getDescription());
                    event.setUsersIds(EventList.get(0).getUsersIds());
                    event.setAdminId(EventList.get(0).getAdminId());
                    // setting the layout from the event information
//                    TextView _eventTitle = findViewById(R.id.recording_title);
//                    TextView Date = findViewById(R.id.recording_date);
//                    TextView partici = findViewById(R.id.parti);
//                    _eventTitle.setText(event.getTitle());
//                    Date.setText(event.getDate());
//                    partici.setText(event.getUsersIds());
//                    mFileName += "/outalk" + event.getId() + ".3gp";
                    SetActivity();
                    CheckRecordingStatus();
                    //check if me as admin

                    //


                }
            }

            @Override
            public void onCancel() {

            }
        });


    }

    public void SetActivity() {
        recordingButton = (ImageButton) findViewById(R.id.btnStop);
        TextView eventTitle = findViewById(R.id.recording_title);
        TextView partici = findViewById(R.id.participants_recording);
        playingButton = findViewById(R.id.btnStart);
        eventTitle.setText(event.getTitle());
        String time[] = event.getDate().split(" "); //16/01/2018 12:08
        TextView startTime = findViewById(R.id.recording_date);
        TextView startDate = findViewById(R.id.recording_time);
        startTime.setText(time[1]);
        startDate.setText(time[0]);
        partici.setText(event.getUsersIds());
        mFileName += "/outalk" + event.getId() + ".3gp";

        if (!(CheckMeAdmin())) {
            recordingButton.setClickable(false);
            playingButton.setClickable(false);

        }
        recordOrSave();

    }

    public void SetEventFromNewActivity() {
        SetActivity();

    }

    public void StopRecordingByAdmin() {
        //notify user about that
        Toast.makeText(getApplication(), "The admin has stop the record..", Toast.LENGTH_SHORT).show();
        recordOrSave();
        //stop recording...
    }

    public void CheckRecordingStatus() {
        Repository.instance.getEventRecordingStatus(event.getId(), new FirebaseModel.FirebaseCallback<List<Boolean>>() {
            @Override
            public void onComplete(List<Boolean> data) {
                if ((data.get(0) == false) && (!(CheckMeAdmin()))) {
                    Log.d("TAG", "Stop recording byadmin func");
                    StopRecordingByAdmin();
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    public void setRecordingStatus() {
        Log.d("TAG", "SetRecordingStatus func in recordingacitivty");
        Repository.instance.setRecodrdingStatus(String.valueOf(event.getId()), new FirebaseModel.FirebaseCallback() {
            @Override
            public void onComplete(Object data) {

            }

            @Override
            public void onCancel() {

            }
        });

    }
}



