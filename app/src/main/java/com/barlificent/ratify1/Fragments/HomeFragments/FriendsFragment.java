package com.barlificent.ratify1.Fragments.HomeFragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.barlificent.ratify1.Adapters.FriendsAdapter;
import com.barlificent.ratify1.CustomClasses.Request;
import com.barlificent.ratify1.R;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    View view;
    int index = 0;
    ListView requestsList;
    TextView requestsCounter;

    public FriendsFragment() {
        // Required empty public constructor
    }

    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    UserPrefs userPrefs;
    ListView friendsList;
    FancyButton friendss;
    FancyButton requestss;

    int rex = 0;
    int fry = 0;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        userPrefs = new UserPrefs(UserPrefs.USER_PREFS, getContext());
        friendsList = (ListView) view.findViewById(R.id.friendsList);
        requestsList = (ListView) view.findViewById(R.id.requestsList);
        friendss = (FancyButton) view.findViewById(R.id.friends_expand);
        requestss = (FancyButton) view.findViewById(R.id.requests_expand);

        getFriends();

        requestss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewd) {
                friendsList.setVisibility(View.GONE);
                if(rex != 0) {
                    requestsList.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.no_friends).setVisibility(View.GONE);
                    view.findViewById(R.id.no_requests).setVisibility(View.GONE);
                }
                else {
                    view.findViewById(R.id.no_friends).setVisibility(View.GONE);
                    view.findViewById(R.id.no_requests).setVisibility(View.VISIBLE);
                }
                requestss.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                friendss.setBackgroundColor(Color.parseColor("#9e9e9e"));
                index = 1;
            }
        });
        friendss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewd) {
                if(fry != 0) {
                    friendsList.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.no_friends).setVisibility(View.GONE);
                    view.findViewById(R.id.no_requests).setVisibility(View.GONE);
                }
                else {
                    view.findViewById(R.id.no_friends).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.no_requests).setVisibility(View.GONE);
                }
                requestsList.setVisibility(View.GONE);
                friendss.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                requestss.setBackgroundColor(Color.parseColor("#9e9e9e"));
                index = 0;
            }
        });

        requestsList.setDivider(null);
        friendsList.setDivider(null);


        return view;
    }

    public void getFriends() {
        users.child(userPrefs.getId()).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Request> requests = new ArrayList<>();
                ArrayList<Request> friends = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Request request = ds.getValue(Request.class);
                    if (!request.isAccepted) requests.add(request);
                    else friends.add(request);

                    Log.w("getFriends", request.isAccepted() + "\n" + request.getName() + "\n" + request.getId());
                    Log.w("Friends", "Number of friends " + requests.size());
                }
                if(getContext() != null && getActivity() != null) {
                    friendsList.setAdapter(new FriendsAdapter(getContext(), friends));
                    requestsList.setAdapter(new FriendsAdapter(getContext(), requests));
                    requestss.setText("Requests " + (requests.size()>0 ? "(" + requests.size() + ")" : ""));
                    rex = requests.size();
                    fry = friends.size();
                    if(rex == 0 && index == 1) view.findViewById(R.id.no_requests).setVisibility(View.VISIBLE);
                    if(fry == 0 && index == 0) view.findViewById(R.id.no_friends).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
