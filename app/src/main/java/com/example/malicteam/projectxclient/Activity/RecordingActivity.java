package com.example.malicteam.projectxclient.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Callbacks.CloseEventCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.RecordingActivityCallback;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.IRecorder;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.Model.WavRecorder;
import com.example.malicteam.projectxclient.R;

import java.io.IOException;

public class RecordingActivity extends AppCompatActivity {
    private final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private boolean mStartPlaying = true;
//    private boolean mStartRecording = true;
    private IRecorder recorder = null;
    private String mFileName = null;
    private User myUser;
    private MediaPlayer mPlayer = null;

    private ImageButton playingButton;
    private ImageButton recordingButton;

    private Event event;
    private Event eventTemp;
    private Boolean fromInvitation;
    private boolean permissionToRecordAccepted = false;
    private final String LOG_TAG = "AudioRecordTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        //get user
        myUser = (User) getIntent().getSerializableExtra(Consts.USER);
        //final file name
        mFileName = getExternalCacheDir().getAbsolutePath() + "/" + String.valueOf(Math.abs(System.currentTimeMillis())).hashCode() + ".wav";
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


        initEvent();
        initButtons();
        recordOrSave();
    }

    private void initEvent(){
        if (getIntent().getSerializableExtra(Consts.SEND_EVENT) != null) {//Does it should be Conts.Event
            eventTemp = (Event) getIntent().getSerializableExtra(Consts.SEND_EVENT);
            event = eventTemp;
            SetEventFromNewActivity();
            fromInvitation = false;
        }
        if (getIntent().getSerializableExtra("eventFromInvitation") != null) {
            event = (Event) getIntent().getSerializableExtra("eventFromInvitation");
            SetEventFromInvitation(event);
            fromInvitation = true;
        }
    }

    private void initButtons(){
        TextView backButton = (TextView) findViewById(R.id.back_btn_recording);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mStartPlaying)
                    stopPlaying();
                if (recorder.isRecording())
                    recordOrSave();

                finish();
            }
        });
        recordingButton = (ImageButton) findViewById(R.id.buttonRecordStart);
        // Set layout only if admin or not
        recordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckMeAdmin()) {
                    recordOrSave();
                }
            }
        });

        playingButton = (ImageButton) findViewById(R.id.buttonPlayStart);
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

    private void playOrPause() {
        //Sisso's Recored
        /*
        //Maayan Note: I added && mStartRecording for prevent play and hear together.
         */
        if (mStartPlaying && !recorder.isRecording()) {//if to start playing
            playingButton.setImageResource(android.R.drawable.ic_media_pause);
            mStartPlaying = false;
            startPlaying();
        } else {
            recordingButton.setImageResource(android.R.drawable.ic_media_play);
            mStartPlaying = true;
            stopPlaying();
        }
    }

    private void recordOrSave() {
        //Sisso's Recored
        TextView onAir = findViewById(R.id.onAir_recording);
        if (!recorder.isRecording()) {//if to start record
            recordingButton.setImageResource(android.R.drawable.ic_menu_save);
            onAir.setVisibility(View.VISIBLE);
//            mStartRecording = false;
            playingButton.setImageResource(android.R.drawable.ic_media_play);
            playingButton.setClickable(false);
            playingButton.setVisibility(View.INVISIBLE);
            startRecording();
        } else {
            recordingButton.setImageResource(android.R.drawable.ic_btn_speak_now);
//            mStartRecording = true;
            onAir.setVisibility(View.INVISIBLE);
            playingButton.setClickable(true);
            playingButton.setVisibility(View.VISIBLE);
            stopRecording();
//            uploadFile();
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
//
//    private void onRecord(boolean start) {
//        if (start) {
//            startRecording();
//        } else {
//            stopRecording();
//        }
//    }
//
//    private void onPlay(boolean start) {
//        if (start) {
//            startPlaying();
//        } else {
//            stopPlaying();
//        }
//    }

    public void StopRecordingByAdmin() {
        //notify user about that
        Toast.makeText(getApplication(), "The admin has stop the record..", Toast.LENGTH_SHORT).show();
        recordOrSave();
        //stop recording...
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
        recorder.startRecording();
    }

    private void stopRecording() {
        recorder.stopRecording();
        if (CheckMeAdmin()) {
            Toast.makeText(getApplication(), "Uploading...", Toast.LENGTH_SHORT).show();
            Repository.instance.closeEvent(null, event.getId(), mFileName, new CloseEventCallback() {
                @Override
                public void onSuccees() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplication(), "File upload successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void UserIsNotExist() {
                    Toast.makeText(getApplication(), "Error:UserIsNotExist", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void EventIsNotExist() {
                    Toast.makeText(getApplication(), "Error:EventIsNotExist", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void TechnicalError() {
                    Toast.makeText(getApplication(), "Error:TechnicalError", Toast.LENGTH_SHORT).show();
                }
            });
        }
        Log.d("TAG", "Stop recording func");

    }

    private boolean CheckMeAdmin() {
//        if (userId != Consts.DEFAULT_UID) {
//            Log.d("TAG", "userId=" + userId);
//            Log.d("TAG", "event.getadminID=" + event.getAdminId());
        if ((String.valueOf(myUser.getId())).equals(event.getAdminId())) {
            //Log.d("TAG","EQUAL");
            return true;
        }
        return false;
    }

    public void SetEventFromInvitation(Event eventtemp) {
//        Repository.instance.getEventById(Integer.valueOf(eventid), new CloudManager.CloudCallback<List<Event>>() {

//            @Override
//            public void onComplete(List<Event> EventList) {
//                if (EventList.size() != 0) //
//                {
        //getting event informatio
        event = eventtemp;
        // setting the layout from the event information
        TextView _eventTitle = findViewById(R.id.recording_title);
        TextView Date = findViewById(R.id.recording_date);
        TextView partici = findViewById(R.id.participants_recording);
        _eventTitle.setText(event.getTitle());
        Date.setText(event.getDate());
        partici.setText(ProductTypeConverters.GenerateStringFromList(ProductTypeConverters.GenerateListUserToListMails(event.getParticipats())));
//        mFileName += "/outalk" + event.getId() + ".acc";
        SetActivity();
        //  CheckRecordingStatus();
        //check if me as admin
    }

    public void SetActivity() {
        recordingButton = (ImageButton) findViewById(R.id.buttonRecordStart);
        TextView eventTitle = findViewById(R.id.recording_title);
        TextView partici = findViewById(R.id.participants_recording);
        playingButton = findViewById(R.id.buttonPlayStart);
        eventTitle.setText(event.getTitle());
        String time[] = event.getDate().split(" "); //16/01/2018 12:08
        TextView startTime = findViewById(R.id.recording_date);
        TextView startDate = findViewById(R.id.recording_time);
//        startTime.setText(time[1]);
//        startDate.setText(time[0]);
        partici.setText(ProductTypeConverters.GenerateStringFromList(ProductTypeConverters.GenerateListUserToListMails(event.getParticipats())));
//        mFileName += "/outalk" + event.getId() + ".acc";

        if (!(CheckMeAdmin())) {
            recordingButton.setClickable(false);
            playingButton.setClickable(false);

        } else {
            recordOrSave();
        }
    }

    public void userHasJoinTheEvent(int userId) {
        for (int i = 0; i < event.getParticipats().size(); i++) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplication(), event.getParticipats().get(i).getFirstName()+" "+event.getParticipats().get(i).getLastName()+",just joined", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplication(), userId + ",just joined", Toast.LENGTH_LONG).show();
                }
            });
//            if (event.getParticipats().get(i).get()==userId)
            //{

        }
    }

    public void userHasLeftTheEvent(int userId) {
        for (int i = 0; i < event.getParticipats().size(); i++) {
            if (event.getParticipats().get(i).getId() == userId) {
                Toast.makeText(getApplication(), event.getParticipats().get(i).getFirstName() + " " + event.getParticipats().get(i).getLastName() + ",just left", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void SetEventFromNewActivity() {
        SetActivity();

    }
}
