//package com.example.malicteam.projectxclient.Activity;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.TextView;
//
//import com.example.malicteam.projectxclient.Common.Consts;
//import com.example.malicteam.projectxclient.Model.Event;
//import com.example.malicteam.projectxclient.R;
//
//public class EventDetails extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_event_details);
//        Event event = (Event) getIntent().getSerializableExtra(Consts.SEND_EVENT);
//
//        TextView title = findViewById(R.id.event_title);
//        TextView _date = findViewById(R.id.details_date);
//        TextView _participates = findViewById(R.id.details_partici);
//        TextView desc = findViewById(R.id.details_descp);
//
//
//        title.setText(event.getTitle());
//        _date.setText(event.getDate());
//        String part = "Participats: ";
////        for (int num : event.getUsersIds()) {
////            part.concat(Integer.toString(num));
////        }
//        _participates.setText("part");
//        desc.setText("Description:" + event.getDescription());
//
//
//    }
//
//
//}
