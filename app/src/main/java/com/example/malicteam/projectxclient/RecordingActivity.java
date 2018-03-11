package com.example.malicteam.projectxclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class RecordingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        Intent intent = getIntent();

        TextView _eventTitle = findViewById(R.id.event_title);
        _eventTitle.setText(intent.getStringExtra("title"));

        ImageButton saveButton = (ImageButton) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StopRecording();
                SaveConversation();
            }
        });
    }

    private void StopRecording(){
        //TODO stop service of recording
    }

    private void SaveConversation(){
        //TODO save pdf file
    }
}
