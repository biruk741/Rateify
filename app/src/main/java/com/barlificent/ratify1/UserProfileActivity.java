package com.barlificent.ratify1;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.barlificent.ratify1.Adapters.UploadsAdapter;
import com.barlificent.ratify1.CustomClasses.FontTextView;
import com.barlificent.ratify1.CustomClasses.Post;
import com.barlificent.ratify1.CustomClasses.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class UserProfileActivity extends AppCompatActivity {
    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    String id;

    TextView bio;
    FancyButton phoneField;
    FontTextView profile_name;
    CircleImageView profile_image;
    FontTextView profile_points;
    ValueEventListener v;
    DatabaseReference currentUser;
    FontTextView uploads;
    ListView photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbarTop);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        // toolbar etc....

        id = getIntent().getStringExtra("id");
        currentUser = users.child(id);

        bio = (TextView) findViewById(R.id.bio);
        phoneField = (FancyButton) findViewById(R.id.phone_field);
        profile_name = (FontTextView) findViewById(R.id.profile_name);
        profile_points = (FontTextView) findViewById(R.id.profile_points);
        photos = (ListView) findViewById(R.id.profile_photos);

        v = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                bio.setText(user.getBio() == null ? "No bio." : user.getBio());
                phoneField.setText(user.getPhoneNumber() + "");
                profile_name.setText(user.getName());
                profile_image = (CircleImageView) findViewById(R.id.profile_image);
                uploads = (FontTextView) findViewById(R.id.profile_uploads);
                CustomMethods.loadPP(getApplicationContext(),user.getUserId(),profile_image);
                profile_points.setText(user.getPoints() + "");
                final ProgressDialog dialog = new ProgressDialog(UserProfileActivity.this);
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.setTitle("Loading");
                dialog.setMessage("Loading profile");
                dialog.show();
                currentUser.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Post> a = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            Post post = ds.getValue(Post.class);
                            a.add(post);
                        }
                        UploadsAdapter adapter = new UploadsAdapter(getApplicationContext(),a);
                        adapter.setIndex(1);
                        photos.setAdapter(adapter);
                        uploads.setText(a.size() + "");
                        CustomMethods.setListViewHeightBasedOnItems(photos);
                        dialog.hide();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        currentUser.addValueEventListener(v);


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentUser.removeEventListener(v);
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentUser.removeEventListener(v);
    }
}
