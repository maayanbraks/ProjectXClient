package com.example.malicteam.projectxclient.Activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.malicteam.projectxclient.Consts;
import com.example.malicteam.projectxclient.Model.Repository;
import com.example.malicteam.projectxclient.R;

public class NewEventActivity extends AppCompatActivity {

    private String saveAsString = "TXT";//DEFAULT
    private Button startRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        startRecord = (Button) findViewById(R.id.new_event_start);
        RadioGroup saveAs = findViewById(R.id.save_as_group);
        saveAs.check(R.id.radio_pdf);

        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText _name = findViewById(R.id.new_event_title);
                EditText _desc = findViewById(R.id.new_event_description);
                EditText _part = findViewById(R.id.new_event_participants);
                String description = _desc.getText().toString();
                String title = _name.getText().toString();
                String parti = _part.getText().toString();

                Intent intent = new Intent(NewEventActivity.this, RecordingActivity.class);
                intent.putExtra(Consts.EVENT_TITLE, title);
                intent.putExtra(Consts.EVENT_DESCRIPTION, parti);
                intent.putExtra(Consts.EVENT_USERS, description);
                intent.putExtra(Consts.SAVE_AS, saveAsString);

                startActivity(intent);
                finish();
            }
        });
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
            startActivity(new Intent(NewEventActivity.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_pdf:
                if (checked)
                    saveAsString = Consts.TXT;//TODO - now its txt default
                    break;
            case R.id.radio_txt:
                if (checked)
                    saveAsString = Consts.TXT;
                    break;
        }
    }
}
