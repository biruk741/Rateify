package com.barlificent.ratify1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.barlificent.ratify1.CustomMethods;
import com.barlificent.ratify1.CustomClasses.FontTextView;
import com.barlificent.ratify1.R;
import com.barlificent.ratify1.CustomClasses.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 2015 on 1/7/2018.
 */
public class LeaderboardAdapter extends ArrayAdapter<User> {
    ListView listView;
    ArrayList<User> words;

    public LeaderboardAdapter(Context context, ArrayList<User> words) {
        super(context, 0, words);
        this.words = words;
    }
    public LeaderboardAdapter(Context context, ArrayList<User> words,ListView listView) {
        super(context, 0, words);
        this.listView = listView;
        this.words = words;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_top_100, parent, false);
        }

        final User currentUser = getItem(position);

        CircleImageView userImage = (CircleImageView) listItemView.findViewById(R.id.image_view_inoutList);
        FontTextView userName = (FontTextView) listItemView.findViewById(R.id.titleUser);
        FontTextView rank = (FontTextView) listItemView.findViewById(R.id.leaderboard_rank);
        FontTextView points = (FontTextView) listItemView.findViewById(R.id.pointsUser);
        CustomMethods.loadPP(getContext(),currentUser.getUserId(),userImage);
        userName.setText(currentUser.getName());
        rank.setText(rankify(position + 1));
        points.setText(String.format("%d Points", currentUser.getPoints()));

        listItemView.findViewById(R.id.linear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return listItemView;
    }
    public String rankify(int rank){
        String suffix;
        switch (rank){
            case 1:suffix = "st";
                break;
            case 2:suffix = "nd";
                break;
            case 3:suffix = "rd";
                break;
            default:suffix= "th";
                break;
        }
        return rank + suffix;
    }
}
