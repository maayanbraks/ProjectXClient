package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
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
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.R;

import java.io.IOException;
import java.util.List;

import android.Manifest;


//public class RecordingActivity extends AppCompatActivity {
//    private boolean recordingBoolean = false;
//    String eventIdTogetIn;
//    ImageButton pauseButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_recording);
//
//        pauseButton = (ImageButton) findViewById(R.id.pause_button);
//        Intent intent = getIntent();
//        TextView eventTitle = (TextView) findViewById(R.id.title_recording);
//        eventIdTogetIn= getIntent().getStringExtra("eventidToGetIn");
//        if (eventIdTogetIn!=null) {
//            eventTitle.setText("Title from invitation");
//        }
//
//        TextView description = (TextView)findViewById((R.id.description_recording));
//        try {
//            eventTitle.setText(intent.getStringExtra(Consts.EVENT_TITLE));
//            description.setText(intent.getStringExtra(Consts.EVENT_DESCRIPTION));
//        }catch (Exception e)
//        {
//            Log.d("taggg","asdfa");
//        }
//        //Date & time
//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//        Date date = new Date();
//        String time[] = dateFormat.format(date).split(" "); //16/01/2018 12:08
//        TextView startTime = findViewById(R.id.startTime_recording);
//        TextView startDate = findViewById(R.id.date_recording);
//        startTime.setText(time[1]);
//        startDate.setText(time[0]);
//
//        playOrPause();
//
//        pauseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO pause recording service - possible to save data also
//                playOrPause();
//            }
//        });
//
//        ImageButton saveButton = (ImageButton) findViewById(R.id.save_button);
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                StopRecording();
//            }
//        });
//
//    }
//
//    private void playOrPause(){
//
//        //Sisso's Recored
//        TextView onAir = findViewById(R.id.onAir_recording);
//        if(!recordingBoolean){//if not -> start record
//            Toast.makeText(getApplicationContext(), "Start record", Toast.LENGTH_SHORT).show();
//            pauseButton.setImageResource(android.R.drawable.ic_media_pause);
//            onAir.setVisibility(View.VISIBLE);
//            recordingBoolean = true;
//        }
//        else{
//            Toast.makeText(getApplicationContext(), "Pause Record", Toast.LENGTH_SHORT).show();
//            pauseButton.setImageResource(android.R.drawable.ic_media_play);
//            onAir.setVisibility(View.INVISIBLE);
//            recordingBoolean = false;
//        }
//    }
//
//    private void StopRecording(){
//        //Sisso's Records
//        //TODO send content data and decode it to text
//        Toast.makeText(getApplicationContext(), "Stop Recording", Toast.LENGTH_SHORT).show();
//        finish();
//    }
//}


public class RecordingActivity extends AppCompatActivity {

    //    private static final String LOG_TAG = "AudioRecordTest";
//    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
//    private static String mFileName = null;
//
//    //private RecordButton mRecordButton = null;
//    private MediaRecorder mRecorder = null;
//
//   // private PlayButton   mPlayButton = null;
//    private MediaPlayer mPlayer = null;
//    private boolean permissionToRecordAccepted = false;
    boolean mStartPlaying = true;
    String eventIdTogetIn;
    private Button StartRecording;
    boolean mStartRecording = true;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private Event event;
    private MediaPlayer mPlayer = null;
    private int currectUserid;
    private String invitedPpl;
    private int userId;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

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
        Log.d("TAG","Stop recording func");
        uploadFile();
        //FirebaseModel.addNewEvent(event);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        eventIdTogetIn=" ";
        userId = getIntent().getIntExtra(Consts.USER_ID, Consts.DEFAULT_UID);
        mFileName = getExternalCacheDir().getAbsolutePath();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        StartRecording = findViewById(R.id.btnStart);
        if (getIntent().getSerializableExtra("sendNewEvent")!=null)
        {
            event = (Event) getIntent().getSerializableExtra("sendNewEvent");
        }
        if (getIntent().getSerializableExtra("eventidToGetIn")!=null)
        {
            eventIdTogetIn=getIntent().getStringExtra("eventidToGetIn");
        }
        //
        if (!(eventIdTogetIn.equals(" "))) //means got enter by invite
        {
            SetEventFromInvitation(eventIdTogetIn);
        }
        else {// entered this activity from Creating new one(NewEventActivity)
            SetEventFromNewActivity();
        }

//         mFileName += "/outalk" + event.getId() + ".3gp";

        invitedPpl = getIntent().getStringExtra("participatsbyName");
        // LinearLayout ll = new LinearLayout(this);
        // mRecordButton = new RecordButton(this);

        // Set layout if admin or not/


        StartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);

                if (CheckMeAdmin()) {
                    if ((mStartRecording)) {
                        StartRecording.setText("Stop recording");
                        uploadFile();
                    } else {
                        StartRecording.setText("Start recording");
                    }
                    mStartRecording = !mStartRecording;
                }
            }
        });
        Button PlayRecording = findViewById(R.id.btnStop);
        PlayRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if ((mStartPlaying) && (CheckMeAdmin())) {
                    PlayRecording.setText("Stop playing");
                } else {
                    PlayRecording.setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        });
        //


        TextView _eventTitle = findViewById(R.id.recording_title);
        TextView Date = findViewById(R.id.recording_date);
        TextView partici = findViewById(R.id.parti);
//        _eventTitle.setText(event.getTitle());
//        Date.setText(event.getDate());
//        partici.setText("Participats:" + event.getUsersIds());


    }

    private void StopRecording(){
        //TODO stop service of recording
    }

    private void SaveConversation(){
        //TODO save pdf file
    }
    private boolean CheckMeAdmin() {
        if (userId != Consts.DEFAULT_UID) {
            Log.d("TAG","userId="+userId);
            Log.d("TAG","event.getadminID="+event.getAdminId());
            if (String.valueOf(userId).equals(event.getAdminId())) {
                //Log.d("TAG","EQUAL");
                return true;
            }


            return false;
        }
        return false;
    }

    private void StartRecordAll() {
        onRecord(mStartRecording);
        mStartRecording = !mStartRecording;

    }
    private void uploadFile() {
        Toast.makeText(getApplication(), "Uploading...", Toast.LENGTH_SHORT).show();
        Repository.instance.saveRecord(mFileName, "" + event.getId(), new Model.SaveAudioListener() {
            @Override
            public void complete(String url) {
//                Toast.makeText(getApplication(), "Upload as succeed" , Toast.LENGTH_SHORT).show();
//                event.setRecordURL(url);
            }

            @Override
            public void fail() {
                Toast.makeText(getApplication(), "Upload failed.", Toast.LENGTH_SHORT).show();
            }
//            @Override
//            public void complete(String url) {
//                Toast.makeText(getApplication(), "Upload as succeed" , Toast.LENGTH_SHORT).show();
//                event.setRecordURL(url);
//            }
//
//            @Override
//            public void fail() {
//                Toast.makeText(getApplication(), "Upload failed." , Toast.LENGTH_SHORT).show();
//            }
//        }),new FirebaseModel.Callback<asd>();
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
        Repository.instance.getEventById(Integer.valueOf(eventid),new FirebaseModel.FirebaseCallback<List<Event>>() {

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
        Button PlayRecording = findViewById(R.id.btnStop);
        TextView _eventTitle = findViewById(R.id.recording_title);
        TextView Date = findViewById(R.id.recording_date);
        TextView partici = findViewById(R.id.parti);
        StartRecording = findViewById(R.id.btnStart);
        _eventTitle.setText(event.getTitle());
        Date.setText(event.getDate());
        Date.setFocusable(false);
        Date.setEnabled(false);
        Date.setClickable(false);
        Date.setFocusableInTouchMode(false);
        partici.setText(event.getUsersIds());
        Log.d("TAG","eventid="+event.getId());
        mFileName += "/outalk" + event.getId() + ".3gp";
        if(!(CheckMeAdmin())){
            StartRecording.setText("Recording...");
            StartRecording.setClickable(false);
            PlayRecording.setClickable(false);

        }
        StartRecordAll();
    }
    public void SetEventFromNewActivity() {
        SetActivity();

    }
}



