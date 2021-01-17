package com.barlificent.ratify1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barlificent.ratify1.CustomClasses.Request;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.barlificent.ratify1.CustomMethods;
import com.barlificent.ratify1.R;
import com.barlificent.ratify1.UserProfileActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by 2015 on 1/12/2018.
 */

public class FriendsAdapter extends ArrayAdapter<Request> {
    View view;
    private UserPrefs userPrefs;
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    public FriendsAdapter(Context context, ArrayList<Request> requests) {
        super(context, 0, requests);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_friends, parent, false);
            final Request currentRequest = getItem(position);
            CircleImageView image = (CircleImageView) listItemView.findViewById(R.id.friend_image_list);
            LinearLayout requestsLinear = (LinearLayout) listItemView.findViewById(R.id.requests_list_Linear);
            LinearLayout mainLinear = (LinearLayout) listItemView.findViewById(R.id.linear);
            userPrefs = new UserPrefs(getContext());

            FancyButton accept = (FancyButton) listItemView.findViewById(R.id.accept_request);
            FancyButton deny = (FancyButton) listItemView.findViewById(R.id.deny_request);

            TextView name = (TextView) listItemView.findViewById(R.id.friend_name_list);
            CustomMethods.loadPP(getContext(),currentRequest.getId(),image);
            name.setText(currentRequest.getName());
            mainLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getContext(), UserProfileActivity.class);
                    i.putExtra("id",currentRequest.getId());
                    getContext().startActivity(i);
                }
            });
            if(currentRequest.isAccepted){
                requestsLinear.setVisibility(View.GONE);
            }
            else{
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        users.child(userPrefs.getId()).child("friends").child(currentRequest.getId()).child("isAccepted").setValue(true);
                        users.child(currentRequest.getId())
                                .child("friends")
                                .child(userPrefs.getId()).setValue(new Request(userPrefs.getName(),userPrefs.getId(),true));
                        Log.w("Accept","run");
                    }
                });
                deny.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        users.child(userPrefs.getId()).child("friends").child(currentRequest.getId()).setValue(null);
                        Log.w("Deny","run");
                    }
                });
                requestsLinear.setVisibility(View.VISIBLE);
            }
        }

        return listItemView;
    }
}
