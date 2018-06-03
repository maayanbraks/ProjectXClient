package com.example.malicteam.projectxclient.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Common.Callbacks.CloseEventCallback;
import com.example.malicteam.projectxclient.Common.Callbacks.LeaveEventCallBack;
import com.example.malicteam.projectxclient.Common.Callbacks.RecordingActivityCallback;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.IRecorder;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.Model.WavRecorder;
import com.example.malicteam.projectxclient.R;

import java.io.File;
import java.io.IOException;

import UpdateObjects.CloseEvent;


public class RecordingActivity extends AppCompatActivity {

    private boolean isRecording = false;


    //Record Objects
    private final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private IRecorder recorder = null;
    private String mFileName = null;
    private boolean permissionToRecordAccepted = false;

    //Playing Objects
    private MediaPlayer mPlayer = null;
    private boolean mStartToPlayMedia = true;//if true - next click should start playing

    private ImageButton playingButton;
    private ImageButton recordingButton;
    private User myUser;
    private Event event;
    private Event eventTemp;
    private Boolean fromInvitation;

    private final String LOG_TAG = "AudioRecordTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        //get permission
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        //get user
        myUser = (User) getIntent().getSerializableExtra(Consts.USER);
        //final file name
        mFileName = getExternalCacheDir().getAbsolutePath() + "/" + String.valueOf(Math.abs(System.currentTimeMillis())).hashCode() + ".wav";
        //Init recorder
        recorder = new WavRecorder(mFileName);
        //Init Event
        listenToParticipents();
        initEvent();
        initButtons();
//        startRecordOrSaveIt();
        initView();
    }

    private void initView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView onAir = findViewById(R.id.onAir_recording);
                //Recording Button + "On Air" image
                if (recorder.isRecording()) {//if recording now
                    recordingButton.setImageResource(android.R.drawable.ic_menu_save);
                    onAir.setVisibility(View.VISIBLE);
                    playingButton.setClickable(false);
                    playingButton.setVisibility(View.INVISIBLE);
                } else {
                    recordingButton.setImageResource(android.R.drawable.ic_btn_speak_now);
                    onAir.setVisibility(View.INVISIBLE);
                    playingButton.setImageResource(android.R.drawable.ic_media_play);
                    playingButton.setClickable(true);
                    playingButton.setVisibility(View.VISIBLE);
                }
                //Playing Button
                if (!mStartToPlayMedia) {//if next click should be pause
                    playingButton.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    playingButton.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });
    }

    private void listenToParticipents() {
        Repository.instance.InitCallbacksForCloudManeger(new RecordingActivityCallback() {
            @Override
            public void userJoinEvent(User user) {
                userHasJoinTheEvent(user);
            }

            @Override
            public void userLeftEvent(User user) {
                userHasLeftTheEvent((user));
            }

            @Override
            public void eventClosed(Event event) {
                if(!(checkMeAdmin()))
                    StopRecordingByAdmin();
            }

        });
    }

    private void initEvent() {
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

    private void backClicked() {
        String msg = "";
        if (checkMeAdmin())
            msg = "Are you sure you want to leave? \n" +
                    "If you leave this event will be closed.";
        else
            msg = "Are you want to leave this event?";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(msg);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("No, Stay!!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes :(", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!mStartToPlayMedia)
                            stopPlaying();
                        if (isRecording) {
                            startRecordOrSaveIt();
                        }
                        if (!(checkMeAdmin())) {
                            leaveEvent();
                        }
                        finish();
                    }

                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        backClicked();
    }

    private void initButtons() {
        TextView backButton = (TextView) findViewById(R.id.back_btn_recording);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backClicked();
            }
        });
        recordingButton = (ImageButton) findViewById(R.id.buttonRecordStart);
        // Set layout only if admin or not
        recordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkMeAdmin()) {
                    startRecordOrSaveIt();
                }
            }
        });

        playingButton = (ImageButton) findViewById(R.id.buttonPlayStart);
        playingButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStartToPlayMedia && checkMeAdmin()) {
                            playOrPause();
                        }
                    }
                });
    }

    private void playOrPause() {
        /*
        //Maayan Note: I added && mStartRecording for prevent play and hear together.
         */
        if (mStartToPlayMedia && !recorder.isRecording()) {//if to start playing
            mStartToPlayMedia = false;
            startPlaying();
        } else {
            mStartToPlayMedia = true;
            stopPlaying();
        }
        initView();
    }

    private void startRecordOrSaveIt() {
        if (!recorder.isRecording()) {
            startRecording();
        } else {
            stopRecording();
        }
        initView();
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

    public void StopRecordingByAdmin() {
        //notify user about that
        //Toast.makeText(getApplication(), "The admin has stop the record..", Toast.LENGTH_SHORT).show();
        MakeToastShort("The admin has stop the record..");
        startRecordOrSaveIt();
        finish();
        //stop recording...
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            //  Toast.makeText(getApplication(), "Recording..", Toast.LENGTH_SHORT).show();
            MakeToastShort("Recording..");
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        isRecording = true;
        recorder.startRecording();
    }

    private void stopRecording() {
        isRecording = false;
        recorder.stopRecording();
        if (checkMeAdmin()) {
            shareEvent();
//            finish();
        } else {
            leaveEvent();
        }
        Log.d("TAG", "Stop recording func");
    }

    private void shareEvent() {
        //     Toast.makeText(getApplication(), "Uploading...", Toast.LENGTH_SHORT).show();
        MakeToastShort("Uploading...");
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar_Recording);
        pb.setVisibility(View.VISIBLE);
        Repository.instance.closeEvent(null, event.getId(), mFileName, new CloseEventCallback() {
            @Override
            public void onSuccees() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(getApplication(), "File upload successful", Toast.LENGTH_SHORT).show();
                        new File(mFileName).delete();
                    }
                });
            }

            @Override
            public void UserIsNotExist() {
                MakeToastShort("Error:UserIsNotExist");
                //Toast.makeText(getApplication(), "Error:UserIsNotExist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void EventIsNotExist() {
                MakeToastShort("Error:EventIsNotExist");
                // Toast.makeText(getApplication(), "Error:EventIsNotExist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void TechnicalError() {
                MakeToastShort("Error:TechnicalError");
                // Toast.makeText(getApplication(), "Error:TechnicalError", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkMeAdmin() {
        if ((myUser.getEmail()).equals(event.getAdminId())) {
            return true;
        }
        return false;
    }

    public void leaveEvent() { // this func is leaving the event - NOT ADMIN!
        Repository.instance.leaveEventRequest(event.getId(), new LeaveEventCallBack<Boolean>() {
            @Override
            public void TechnicalError() {
                MakeToastShort("Technical error.");
                //  Toast.makeText(getApplication(), "Technical error.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void NoPendingEvents() {
                MakeToastShort("No pending events.");
                // Toast.makeText(getApplication(), "No pending events.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccees(Boolean data) {
                MakeToastShort("leaving.");
                finish();
                //   Toast.makeText(getApplication(), "leaving.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void SetEventFromInvitation(Event eventtemp) {
//        Repository.instance.getEventById(Integer.valueOf(eventid), new CloudManager.CloudManagerCallback<List<Event>>() {

//            @Override
//            public void onComplete(List<Event> EventList) {
//                if (EventList.size() != 0) //
//                {
        //getting event informatio
        TextView partici = findViewById(R.id.participants_recording);
        event = eventtemp;
        event.addToParticipats(myUser);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                partici.setText(event.getParticipatsFullNames());

            }
        });
        // setting the layout from the event information
        // TextView _eventTitle = findViewById(R.id.recording_title);
        TextView Date = findViewById(R.id.recording_date);
        // _eventTitle.setText(event.getTitle());
        Date.setText(event.getDate());

        // partici.setText(ProductTypeConverters.GenerateStringFromList(ProductTypeConverters.GenerateListUserToListMails(event.getParticipats())));
//        mFileName += "/outalk" + event.getId() + ".acc";
        SetActivity();
//        CheckRecordingStatus();
//        check if me as admin
    }

    public void SetActivity() {
        recordingButton = (ImageButton) findViewById(R.id.buttonRecordStart);
        TextView eventTitle = findViewById(R.id.recording_title);
        TextView DESC = findViewById(R.id.description_recording);
        TextView partici = findViewById(R.id.participants_recording);
        playingButton = findViewById(R.id.buttonPlayStart);
        eventTitle.setText(event.getTitle());
        partici.setText(event.getParticipatsFullNames());

        DESC.setText(event.getDescription());
//        mFileName += "/outalk" + event.getId() + ".acc";

        if (!(checkMeAdmin())) {
            recordingButton.setClickable(false);
            playingButton.setClickable(false);
        }
//        } else {
//            startRecordOrSaveIt();
//        }
    }

    public void userHasJoinTheEvent(User user) {
        Log.d("TAG", "In userhasjointheEvent func");
        TextView partici = findViewById(R.id.participants_recording);
//        for (int i=0;i<event.getParticipats().size();i++)
//        {
//            if (event.getParticipats().get(i).get()==userId)
        //{
        //  Toast.makeText(getApplication(), event.getParticipats().get(i).getFirstName()+" "+event.getParticipats().get(i).getLastName()+",just joined", Toast.LENGTH_LONG).show();
        //}
        //Toast.makeText(getApplication(), user.getFirstName() + " " + user.getLastName() + ",just joined", Toast.LENGTH_LONG).show();
        if (!(event.isUserConatin(user))) {
            MakeToastShort(user.getFirstName() + " " + user.getLastName() + ",just joined");
            event.addToParticipats(user);
//            if (!(event.getParticipats().contains(user)))


            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    partici.setText(event.getParticipatsFullNames());

                }
            });
        }
    }

    public void userHasLeftTheEvent(User user) {
        TextView partici = findViewById(R.id.participants_recording);
//        for (int i=0;i<event.getParticipats().size();i++)
//        {
//            if (event.getParticipats().get(i).getId()==userId)
//            {
        //Toast.makeText(getApplication(), user.getFirstName() + " " + user.getFirstName() + ",just left", Toast.LENGTH_LONG).show();
        MakeToastShort(user.getFirstName() + " " + user.getFirstName() + ",just left");
        event.delFromParticipats(user);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                partici.setText(event.getParticipatsFullNames());

            }
        });

//        }
    }

    public void SetEventFromNewActivity() {
        String time[] = event.getDate().split(" "); //16/01/2018 12:08
        TextView partici = findViewById(R.id.participants_recording);
        TextView startTime = findViewById(R.id.recording_date);
        TextView startDate = findViewById(R.id.recording_time);
        startTime.setText(time[1]);
        startDate.setText(time[0]);
        event.addToParticipats(myUser);
        partici.setText(event.getParticipatsFullNames());
        SetActivity();
    }

    private void MakeToastShort(String toastInfo) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplication(), toastInfo, Toast.LENGTH_SHORT).show();


            }
        });
    }
}