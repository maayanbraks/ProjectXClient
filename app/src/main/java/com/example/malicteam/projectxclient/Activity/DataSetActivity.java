package com.example.malicteam.projectxclient.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.malicteam.projectxclient.Common.Callbacks.CloseEventCallback;
import com.example.malicteam.projectxclient.Common.Consts;
import com.example.malicteam.projectxclient.Model.IRecorder;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.Model.User;
import com.example.malicteam.projectxclient.Model.WavRecorder;
import com.example.malicteam.projectxclient.R;

import java.io.File;

public class DataSetActivity extends AppCompatActivity {
    //Record Objects
    private final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private IRecorder recorder = null;
    private String mFileName = null;
    private boolean permissionToRecordAccepted = false;

    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_set);
        //get permission
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        //get user
        myUser = (User) getIntent().getSerializableExtra(Consts.USER);
        //file name
        initFileName();
        //Init recorder
        recorder = new WavRecorder(mFileName);

        initButtons();
    }

    private void initFileName() {
        mFileName = getExternalCacheDir().getAbsolutePath() + "/" + myUser.getEmail().hashCode() + String.valueOf(Math.abs(System.currentTimeMillis())).hashCode() + "ds.wav";
    }

    private void initButtons() {
        TextView backButton = (TextView) findViewById(R.id.back_btn_dataSet);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backClicked();
            }
        });
        ToggleButton toggle = (ToggleButton) findViewById(R.id.recordToggleButton_DataSet);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    Toast.makeText(getApplication(), "On", Toast.LENGTH_SHORT).show();
                    startRecord();
                } else {
//                    Toast.makeText(getApplication(), "off", Toast.LENGTH_SHORT).show();
                    stopRecord();
                    uploadDataSet();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backClicked();
    }

    private void backClicked() {
        final String title = "Are you sure you want to leave?";
        final String msg = "If you leave this record will be stopped.";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);
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
                        stopRecord();
                        uploadDataSet();
                        finish();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void stopRecord() {
        if (recorder.isRecording()) {
            recorder.stopRecording();
        }
    }

    private void startRecord() {
        if (!recorder.isRecording() && recorder != null) {
            recorder.startRecording();
        }
    }

    private void uploadDataSet() {
        Toast.makeText(getApplication(), "Uploading...", Toast.LENGTH_SHORT).show();
        initFileName();
        recorder.setFileName(mFileName);
        Repository.instance.uploadDataSet();
    }
}
