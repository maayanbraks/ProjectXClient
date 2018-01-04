package com.example.malicteam.projectxclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewEventActivity extends AppCompatActivity {

    private Button startRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        startRecord = (Button) findViewById(R.id.newevent_send);
        EditText _name = findViewById(R.id.newevent_title);

        EditText _desc = findViewById(R.id.newevent_description);

        EditText _part = findViewById(R.id.newevent_participants);
        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText _name = findViewById(R.id.newevent_title);
                EditText _desc = findViewById(R.id.newevent_description);
                EditText _part = findViewById(R.id.newevent_participants);
                String description = _desc.getText().toString();
                String title = _name.getText().toString();
                String parti = _part.getText().toString();
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("part", parti);
                intent.putExtra("desc", description);
                startActivity(intent);

            }
        });
    }
}
