package com.barlificent.ratify1.Fragments.ProfileFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;

import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.barlificent.ratify1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSettingsFragment extends Fragment {

    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    RadioButton men,women,both;
    Switch requestsSwitch;
    UserPrefs userPrefs;
    boolean updated = false;
    View view;


    public ProfileSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_settings, container, false);
        userPrefs = new UserPrefs(getContext());

        men = (RadioButton) view.findViewById(R.id.men_radio);
        women = (RadioButton) view.findViewById(R.id.women_radio);
        both = (RadioButton) view.findViewById(R.id.both_radio);
        requestsSwitch = (Switch) view.findViewById(R.id.send_request_settings);

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


        return view;
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
