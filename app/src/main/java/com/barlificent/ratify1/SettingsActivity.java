package com.barlificent.ratify1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;

import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    RadioButton men,women,both;
    Switch requestsSwitch;
    UserPrefs userPrefs;
    boolean updated = false;
    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Toolbar toolbarTop = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userPrefs = new UserPrefs(getApplicationContext());

        men = (RadioButton) findViewById(R.id.men_radio);
        women = (RadioButton) findViewById(R.id.women_radio);
        both = (RadioButton) findViewById(R.id.both_radio);
        requestsSwitch = (Switch) findViewById(R.id.send_request_settings);

        men.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) userPrefs.setPreference("men");
            }
        });
        women.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) userPrefs.setPreference("women");
            }
        });
        both.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) userPrefs.setPreference("both");
            }
        });
        requestsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                users.child(userPrefs.getId()).child("canBeSentRequest").setValue(b);
                userPrefs.setPeopleCanSendRequests(b);
            }
        });


        updateSettings();

    }
    public void updateSettings(){
        switch (userPrefs.getPreference()){
            case "men":
                men.setChecked(true);
                break;
            case "women":
                women.setChecked(true);
                break;
            case "both":
                both.setChecked(true);
                break;
        }
        if(userPrefs.peopleCanSendRequests())
            requestsSwitch.setChecked(true);
        else requestsSwitch.setChecked(false);
        updated = true;
    }
}
