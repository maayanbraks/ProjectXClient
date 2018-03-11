package com.example.malicteam.projectxclient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import Model.Event;

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


        title.setText(event.getTitle());
        _date.setText(event.getDate());
        String part = "Participats: ";
        for (int num : event.getUsersIds()) {
            part.concat(Integer.toString(num));
        }
        _participates.setText("part");
        desc.setText("Description:" + event.getDescription());


    }
}
