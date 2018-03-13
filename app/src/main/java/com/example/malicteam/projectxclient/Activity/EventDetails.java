package com.example.malicteam.projectxclient.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.Model.Event;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.R;

public class EventDetails extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Event event = (Event) getIntent().getSerializableExtra(Consts.SEND_EVENT);

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout_actionMenu) {
            Repository.instance.logout();
            startActivity(new Intent(EventDetails.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
