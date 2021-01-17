package com.barlificent.ratify1.Fragments.HomeFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.barlificent.ratify1.Adapters.LeaderboardAdapter;
import com.barlificent.ratify1.CustomMethods;
import com.barlificent.ratify1.R;
import com.barlificent.ratify1.CustomClasses.User;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderBoardFragment extends Fragment {

    ListView top100;
    CircleImageView leaderBoardPP;
    UserPrefs userPrefs;


    public LeaderBoardFragment() {
        // Required empty public constructor
    }

    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        userPrefs = new UserPrefs(UserPrefs.USER_PREFS,getContext());
        top100 = (ListView) convertView.findViewById(R.id.top100);
        leaderBoardPP = (CircleImageView) convertView.findViewById(R.id.leaderboard_pp);

        CustomMethods.loadPP(getContext(),userPrefs.getId(),leaderBoardPP);
        getTop100();

        return convertView;
    }

    public void getTop100() {
        final ArrayList<User> usersArray = new ArrayList<>();
        users.orderByChild("points").limitToLast(100).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    usersArray.add(ds.getValue(User.class));
                }
                if(getContext() != null && getActivity() != null) {
                    top100.setAdapter(new LeaderboardAdapter(getContext(), reverseArrayList(usersArray)));
                }
                CustomMethods.setListViewHeightBasedOnItems(top100);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static ArrayList<User> reverseArrayList(ArrayList<User> b) {
        ArrayList<User> a = new ArrayList<>();
        int c = b.size() - 1;
        for (int i = 0; i < b.size(); i++) {
            a.add(b.get(c));
            c--;
        }
        return a;
    }
}
