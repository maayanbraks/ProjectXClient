package com.example.malicteam.projectxclient;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import Event.Model.Event;

public class EventDetails extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Event event = (Event) getIntent().getSerializableExtra("sendevent");

        TextView title = findViewById(R.id.event_title);
        TextView _date = findViewById(R.id.details_date);
        TextView _participates = findViewById(R.id.details_partici);
        TextView desc = findViewById(R.id.details_descp);


        title.setText(event._nameEvent);
        _date.setText(event._date);
        _participates.setText("Participats:" + event._participates);
        desc.setText("Description:" + event._descp);


    }
}
