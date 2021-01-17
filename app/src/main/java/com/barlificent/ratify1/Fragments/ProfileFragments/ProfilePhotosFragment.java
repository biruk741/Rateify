package com.barlificent.ratify1.Fragments.ProfileFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.barlificent.ratify1.Adapters.UploadsAdapter;
import com.barlificent.ratify1.CustomClasses.Post;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.barlificent.ratify1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilePhotosFragment extends Fragment {

    ListView gridView;
    View view;
    UserPrefs userPrefs;


    public ProfilePhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile_photos, container, false);

        gridView = (ListView) view.findViewById(R.id.uploads_grid);
        userPrefs = new UserPrefs(getContext());

        getPosts();


        gridView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scroll_profile);
                if(!scrollView.canScrollVertically(1))
                    scrollView.post(new Runnable() {

                        @Override
                        public void run() {
                            getActivity().findViewById(R.id.profile_scroll).getParent().requestDisallowInterceptTouchEvent(true);
                        }

                    });
                return false;
            }
        });

        return view;
    }
    public void getPosts(){
        final ArrayList<Post> postsArray = new ArrayList<>();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
        final DatabaseReference posts = users.child(userPrefs.getId()).child("posts");
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Post post = ds.getValue(Post.class);
                    postsArray.add(new Post(post.getPhotoUrl(),post.getDate(),post.getPointsCount()));
                }
                Log.w("lengthA",postsArray.size() + "");
                gridView.setAdapter(new UploadsAdapter(getContext(),postsArray));
                view.findViewById(R.id.loading_profile_photos).setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
