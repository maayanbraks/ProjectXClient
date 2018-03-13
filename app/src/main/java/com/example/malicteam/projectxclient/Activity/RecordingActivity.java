package com.example.malicteam.projectxclient.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordingActivity extends AppCompatActivity {
    private boolean recordingBoolean = false;
    ImageButton pauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        pauseButton = (ImageButton) findViewById(R.id.pause_button);
        Intent intent = getIntent();

        TextView eventTitle = (TextView) findViewById(R.id.event_title);
        TextView description = (TextView)findViewById((R.id.description_recording));
        try {
            eventTitle.setText(intent.getStringExtra(Consts.EVENT_TITLE));
            description.setText(intent.getStringExtra(Consts.EVENT_DESCRIPTION));
        }catch (Exception e)
        {
            Log.d("taggg","asdfa");
        }
        //Date & time
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        String time[] = dateFormat.format(date).split(" "); //16/01/2018 12:08
        TextView startTime = findViewById(R.id.startTime_recording);
        TextView startDate = findViewById(R.id.date_recording);
        startTime.setText(time[1]);
        startDate.setText(time[0]);

        playOrPause();

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO pause recording service - possible to save data also
                playOrPause();
            }
        });

        ImageButton saveButton = (ImageButton) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StopRecording();
            }
        });

    }

    private void playOrPause(){

        //Sisso's Recored
        TextView onAir = findViewById(R.id.onAir_recording);
        if(!recordingBoolean){//if not -> start record
            Toast.makeText(getApplicationContext(), "Start record", Toast.LENGTH_SHORT).show();
            pauseButton.setImageResource(android.R.drawable.ic_media_pause);
            onAir.setVisibility(View.VISIBLE);
            recordingBoolean = true;
        }
        else{
            Toast.makeText(getApplicationContext(), "Pause Record", Toast.LENGTH_SHORT).show();
            pauseButton.setImageResource(android.R.drawable.ic_media_play);
            onAir.setVisibility(View.INVISIBLE);
            recordingBoolean = false;
        }
    }

    private void StopRecording(){
        //Sisso's Records
        //TODO send content data and decode it to text
        Toast.makeText(getApplicationContext(), "Stop Recording", Toast.LENGTH_SHORT).show();
        finish();
    }
}
