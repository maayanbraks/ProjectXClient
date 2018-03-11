package com.example.malicteam.projectxclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class NewEventActivity extends AppCompatActivity {

    private Button startRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        startRecord = (Button) findViewById(R.id.new_event_start);
        EditText _name = findViewById(R.id.new_event_title);

        EditText _desc = findViewById(R.id.new_event_description);

        EditText _part = findViewById(R.id.new_event_participants);

        RadioGroup _saveAs = findViewById(R.id.save_as_group);
        _saveAs.check(R.id.radio_pdf);

        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText _name = findViewById(R.id.new_event_title);
                EditText _desc = findViewById(R.id.new_event_description);
                EditText _part = findViewById(R.id.new_event_participants);
                String description = _desc.getText().toString();
                String title = _name.getText().toString();
                String parti = _part.getText().toString();
                Intent intent = new Intent(getApplicationContext(), RecordingActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("part", parti);
                intent.putExtra("desc", description);
                startActivity(intent);
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_pdf:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_txt:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }
}
