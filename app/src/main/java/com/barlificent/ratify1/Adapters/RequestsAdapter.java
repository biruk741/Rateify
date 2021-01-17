package com.barlificent.ratify1.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.barlificent.ratify1.CustomClasses.Request;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.barlificent.ratify1.CustomMethods;
import com.barlificent.ratify1.CustomClasses.FontTextView;
import com.barlificent.ratify1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 2015 on 1/9/2018.
 */
public class RequestsAdapter extends ArrayAdapter<Request> {
    ListView listView;
    ArrayList<Request> words;
    UserPrefs userPrefs;

    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    public RequestsAdapter(Context context, ArrayList<Request> words) {
        super(context, 0, words);
        this.words = words;
    }

    public RequestsAdapter(Context context, ArrayList<Request> words, ListView listView) {
        super(context, 0, words);
        this.listView = listView;
        this.words = words;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        userPrefs = new UserPrefs(UserPrefs.USER_PREFS,getContext());
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_request, parent, false);
        }

        final Request currentRequest = getItem(position);
        final CircleImageView userImage = (CircleImageView) listItemView.findViewById(R.id.image_view_inoutList);
        final CardView cardView = (CardView) listItemView.findViewById(R.id.cardViewList);
        final FontTextView userName = (FontTextView) listItemView.findViewById(R.id.titleUser);
        final FontTextView accept = (FontTextView) listItemView.findViewById(R.id.accept);

        CustomMethods.loadPP(getContext(), currentRequest.getId(), userImage);
        userName.setText(currentRequest.getName());

        if(currentRequest.isAccepted){
            cardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
            accept.setText("Accepted");
        }
        else{
            cardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
            accept.setText("Accept");
        }
        listItemView.findViewById(R.id.linear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users.child(userPrefs.getId()).child("friends").child(currentRequest.getId()).child("isAccepted").setValue(true);
                cardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                accept.setText("Accepted");
            }
        });
        return listItemView;
    }
}