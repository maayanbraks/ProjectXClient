//package com.example.malicteam.projectxclient.Activity;
//
//import android.arch.lifecycle.MutableLiveData;
//import android.arch.lifecycle.Observer;
//import android.arch.lifecycle.ViewModelProviders;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.media.MediaPlayer;
//import android.media.MediaRecorder;
//import android.os.Environment;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.malicteam.projectxclient.Common.Callbacks.CloseEventCallback;
//import com.example.malicteam.projectxclient.Common.Callbacks.RecordingActivityCallback;
//import com.example.malicteam.projectxclient.Common.Consts;
//import com.example.malicteam.projectxclient.Common.ProductTypeConverters;
//import com.example.malicteam.projectxclient.Model.CloudManager;
//import com.example.malicteam.projectxclient.Model.Event;
//import com.example.malicteam.projectxclient.Model.FirebaseModel;
//import com.example.malicteam.projectxclient.Model.Model;
//import com.example.malicteam.projectxclient.Model.Repository;
//import com.example.malicteam.projectxclient.Model.User;
//import com.example.malicteam.projectxclient.R;
//import com.example.malicteam.projectxclient.ViewModel.UserViewModel;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//import android.Manifest;
//
//import UpdateObjects.CloseEvent;
//import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
//import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
//import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
//import cafe.adriel.androidaudioconverter.model.AudioFormat;
//
//public class RecordingActivity extends AppCompatActivity {
//    private UserViewModel currentUser = null;
//    private boolean mStartPlaying = true;
//    private String eventIdTogetIn;
//    private ImageButton recordingButton;
//    private boolean mStartRecording = true;
//    private static final String LOG_TAG = "AudioRecordTest";
//    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
//    private static String mFileName = null;
//    private ImageButton playingButton;
//    Event eventtemp;
//    private MediaRecorder mRecorder = null;
//    private Event event;
//    private MediaPlayer mPlayer = null;
//    private User myUser;
//    private Boolean FromInvitation;
//
//    private boolean permissionToRecordAccepted = false;
//    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_recording);
//        myUser = (User) getIntent().getSerializableExtra(Consts.USER);
//        //        currentUser = ViewModelProviders.of(this).get(UserViewModel.class);
////        currentUser.getUser().observe(this, new Observer<User>() {
////            @Override
////            public void onChanged(@Nullable User user) {
////                if (user != null) {
////        myUser = user;
////
////                }
////            }
////        });
//        eventIdTogetIn = " ";
//
//        mFileName = getExternalCacheDir().getAbsolutePath();
//        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
//
//        TextView backButton = (TextView) findViewById(R.id.back_btn_recording);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!mStartPlaying)
//                    stopPlaying();
//                if (!mStartRecording)
//                    recordOrSave();
//
//                finish();
//            }
//        });
//        Repository.instance.InitCallbacksForCloudManeger(new RecordingActivityCallback() {
//            @Override
//            public void userJoinEvent(int userId) {
//                userHasJoinTheEvent(userId);
//            }
//
//            @Override
//            public void userLeftEvent(int userId) {
//                userHasLeftTheEvent((userId));
//            }
//
//            @Override
//            public void eventClosed(int eventId) {
//                StopRecordingByAdmin();
//            }
//
//        });
//        recordingButton = (ImageButton) findViewById(R.id.btnStop);
//        // Set layout only if admin or not
//        recordingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (CheckMeAdmin()) {
//                    recordOrSave();
//                }
//            }
//        });
//
//        playingButton = (ImageButton) findViewById(R.id.btnStart);
//        playingButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (mStartPlaying && CheckMeAdmin()) {
//                            playOrPause();
//                        }
//                    }
//                });
//
//        if (getIntent().getSerializableExtra("sendNewEvent") != null) {
//            eventtemp = (Event) getIntent().getSerializableExtra(Consts.SEND_EVENT);
//            event = eventtemp;
//            SetEventFromNewActivity();
//            FromInvitation = false;
//        }
//        if (getIntent().getSerializableExtra("eventFromInvitation") != null) {
//            event = (Event) getIntent().getSerializableExtra("eventFromInvitation");
//            SetEventFromInvitation(event);
//            FromInvitation = true;
//        }
//        convertAccToWavInit(); //init the converter.
//        //
////        if (!(eventIdTogetIn.equals(" "))) //means got enter by invite
////        {
////            SetEventFromInvitation(eventtemp);
////        }
//
//    }
//
//
//    private void playOrPause() {
//        //Sisso's Recored
//        /*
//        //Maayan Note: I added && mStartRecording for prevent play and hear together.
//         */
//        if (mStartPlaying && mStartRecording) {//if to start playing
//            playingButton.setImageResource(android.R.drawable.ic_media_pause);
//            mStartPlaying = false;
//            startPlaying();
//        } else {
//            recordingButton.setImageResource(android.R.drawable.ic_media_play);
//            mStartPlaying = true;
//            stopPlaying();
//        }
//    }
//
//    private void recordOrSave() {
//        //Sisso's Recored
//        TextView onAir = findViewById(R.id.onAir_recording);
//        if (mStartRecording) {//if to start record
//            recordingButton.setImageResource(android.R.drawable.ic_menu_save);
//            onAir.setVisibility(View.VISIBLE);
//            mStartRecording = false;
//            playingButton.setImageResource(android.R.drawable.ic_media_play);
//            playingButton.setClickable(false);
//            playingButton.setVisibility(View.INVISIBLE);
//            startRecording();
//        } else {
//            recordingButton.setImageResource(android.R.drawable.ic_btn_speak_now);
//            mStartRecording = true;
//            onAir.setVisibility(View.INVISIBLE);
//            playingButton.setClickable(true);
//            playingButton.setVisibility(View.VISIBLE);
//            stopRecording();
////            uploadFile();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_RECORD_AUDIO_PERMISSION:
//                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                break;
//        }
//        if (!permissionToRecordAccepted) finish();
//    }
//
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
//
//
//    private void startPlaying() {
//        mPlayer = new MediaPlayer();
//        try {
//            mPlayer.setDataSource(mFileName);
//            mPlayer.prepare();
//            mPlayer.start();
//            Toast.makeText(getApplication(), "Recording..", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//    }
//
//    private void stopPlaying() {
//        mPlayer.release();
//        mPlayer = null;
//    }
//
//    private void startRecording() {
//        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
//        mRecorder.setOutputFile(mFileName);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        try {
//            mRecorder.prepare();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//
//        mRecorder.start();
//    }
//
//    private void stopRecording() {
//        mRecorder.stop();
//        mRecorder.release();
//        mRecorder = null;
//        if (CheckMeAdmin()) {
//            setRecordingStatus();
//            //closeevent();
//
//
//            Toast.makeText(getApplication(), "Uploading...", Toast.LENGTH_SHORT).show();
//            try {
//                byte byteFile[] = ProductTypeConverters.convertFileToByte(convertFromAccToWav());
//            } catch (IOException e) {
//                Log.d("TAG", "File in Stop Recording -Making bytefile" + e);
//                e.printStackTrace();
//            }
//            ////change the protocol in closeevent to Bytefile,
//            Repository.instance.closeEvent(null, event.getId(), mFileName, new CloseEventCallback() {
//
//                @Override
//                public void onSuccees() {
//
//                    Toast.makeText(getApplication(), "File upload successful", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void UserIsNotExist() {
//                    Toast.makeText(getApplication(), "Error:UserIsNotExist", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void EventIsNotExist() {
//                    Toast.makeText(getApplication(), "Error:EventIsNotExist", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void TechnicalError() {
//                    Toast.makeText(getApplication(), "Error:TechnicalError", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        Log.d("TAG", "Stop recording func");
//
//    }
//
//
//    private void StopRecording() {
//    }
//
//    private void SaveConversation() {
//    }
//
//    private boolean CheckMeAdmin() {
////        if (userId != Consts.DEFAULT_UID) {
////            Log.d("TAG", "userId=" + userId);
////            Log.d("TAG", "event.getadminID=" + event.getAdminId());
//        if ((myUser.getEmail()).equals(event.getAdminId())) {
//            //Log.d("TAG","EQUAL");
//            return true;
//        }
//        return false;
//    }
//
//    private File convertFromAccToWav() {
//        File flacFile = new File(Environment.getExternalStorageDirectory(), mFileName);
//        IConvertCallback callback = new IConvertCallback() {
//            @Override
//            public void onSuccess(File convertedFile) {
//                // So fast? Love it!
//                Log.d("TAG", "On sucess in convertFromAccToWav ");
//                //run the func that convert into String and then send to Server
//            }
//
//            @Override
//            public void onFailure(Exception error) {
//                Log.d("TAG", "On failure in convertFromAccToWav ");
//                // Oops! Something went wrong
//            }
//        };
//        AndroidAudioConverter.with(this)
//                // Your current audio file
//                .setFile(flacFile)
//
//                // Your desired audio format
//                .setFormat(AudioFormat.WAV)
//
//                // An callback to know when conversion is finished
//                // An callback to know when conversion is finished
//                .setCallback(callback)
//
//                // Start conversion
//                .convert();
//        return flacFile;
//    }
//
//
//
//
////    private byte[] uploadFile() {
////        Toast.makeText(getApplication(), "Uploading...", Toast.LENGTH_SHORT).show();
////        try {
////             byte byteFile[]=ProductTypeConverters.convertFileToByte(convertFromAccToWav());
////            return byteFile;
////            //Send the byteFile to Sahar
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//////        Repository.instance.saveRecord(String.valueOf(userId), mFileName, "" + event.getId(), new Model.SaveAudioListener() {
//////                @Override
//////                public void complete(String url) {
////////                Toast.makeText(getApplication(), "Upload as succeed" , Toast.LENGTH_SHORT).show();
////////                event.setRecordURL(url);
//////            }
//////
//////            @Override
//////            public void fail() {
//////                Toast.makeText(getApplication(), "Upload failed.", Toast.LENGTH_SHORT).show();
//////            }
//////        }, new CloudManager.CloudCallback<Boolean>() {
//////            @Override
//////            public void onComplete(Boolean data) {
//////
//////            }
//////
//////            @Override
//////            public void onCancel() {
//////
//////            }
//////        });
////    }
//
//    public void SetEventFromInvitation(Event eventtemp) {
////        Repository.instance.getEventById(Integer.valueOf(eventid), new CloudManager.CloudCallback<List<Event>>() {
//
////            @Override
////            public void onComplete(List<Event> EventList) {
////                if (EventList.size() != 0) //
////                {
//        //getting event informatio
//        event = eventtemp;
//        // setting the layout from the event information
//        TextView _eventTitle = findViewById(R.id.recording_title);
//        TextView Date = findViewById(R.id.recording_date);
//        TextView partici = findViewById(R.id.participants_recording);
//        _eventTitle.setText(event.getTitle());
//        Date.setText(event.getDate());
//        partici.setText(ProductTypeConverters.GenerateStringFromList(ProductTypeConverters.GenerateListUserToListMails(event.getParticipats())));
//        mFileName += "/outalk" + event.getId() + ".acc";
//        SetActivity();
//        //  CheckRecordingStatus();
//        //check if me as admin
//
//        //
//
//
//    }
//
//
//    public void SetActivity() {
//        recordingButton = (ImageButton) findViewById(R.id.btnStop);
//        TextView eventTitle = findViewById(R.id.recording_title);
//        TextView partici = findViewById(R.id.participants_recording);
//        playingButton = findViewById(R.id.btnStart);
//        eventTitle.setText(event.getTitle());
//        String time[] = event.getDate().split(" "); //16/01/2018 12:08
//        TextView startTime = findViewById(R.id.recording_date);
//        TextView startDate = findViewById(R.id.recording_time);
////        startTime.setText(time[1]);
////        startDate.setText(time[0]);
//        partici.setText(ProductTypeConverters.GenerateStringFromList(ProductTypeConverters.GenerateListUserToListMails(event.getParticipats())));
//        mFileName += "/outalk" + event.getId() + ".acc";
//
//        if (!(CheckMeAdmin())) {
//            recordingButton.setClickable(false);
//            playingButton.setClickable(false);
//
//        } else {
//            recordOrSave();
//        }
//
//
//    }
//
//    public void userHasJoinTheEvent(int userId) {
//        for (int i = 0; i < event.getParticipats().size(); i++) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                    Toast.makeText(getApplication(), event.getParticipats().get(i).getFirstName()+" "+event.getParticipats().get(i).getLastName()+",just joined", Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplication(), userId + ",just joined", Toast.LENGTH_LONG).show();
//                }
//            });
////            if (event.getParticipats().get(i).get()==userId)
//            //{
//
//        }
//    }
//
//    public void userHasLeftTheEvent(int userId) {
//        for (int i = 0; i < event.getParticipats().size(); i++) {
//            if (event.getParticipats().get(i).getId() == userId) {
//                Toast.makeText(getApplication(), event.getParticipats().get(i).getFirstName() + " " + event.getParticipats().get(i).getLastName() + ",just left", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    public void SetEventFromNewActivity() {
//        SetActivity();
//
//    }
//
//    public void StopRecordingByAdmin() {
//        //notify user about that
//        Toast.makeText(getApplication(), "The admin has stop the record..", Toast.LENGTH_SHORT).show();
//        recordOrSave();
//        //stop recording...
//    }
//
//    public void convertAccToWavInit() {
//        AndroidAudioConverter.load(this, new ILoadCallback() {
//            @Override
//            public void onSuccess() {
//                // Great!
//            }
//
//            @Override
//            public void onFailure(Exception error) {
//                // FFmpeg is not supported by device
//            }
//        });
//    }
//
//
////    public void CheckRecordingStatus() {
////        Repository.instance.getEventRecordingStatus(event.getId(), new CloudManager.CloudCallback<List<Boolean>>() {
////            @Override
////            public void onComplete(List<Boolean> data) {
////                if ((data.get(0) == false) && (!(CheckMeAdmin()))) {
////                    Log.d("TAG", "Stop recording byadmin func");
////                    StopRecordingByAdmin();
////                }
////            }
////
////            @Override
////            public void onCancel() {
////
////            }
////        });
////    }
//
//    public void setRecordingStatus() {
//        Log.d("TAG", "SetRecordingStatus func in recordingacitivty");
//        Repository.instance.setRecodrdingStatus(String.valueOf(event.getId()), new CloudManager.CloudCallback() {
//            @Override
//            public void onComplete(Object data) {
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//        });
//
//    }
//}
//
//
//
