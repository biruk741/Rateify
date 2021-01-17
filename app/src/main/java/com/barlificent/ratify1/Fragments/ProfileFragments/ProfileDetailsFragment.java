package com.barlificent.ratify1.Fragments.ProfileFragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.barlificent.ratify1.Adapters.UploadsAdapter;
import com.barlificent.ratify1.CustomClasses.FontTextView;
import com.barlificent.ratify1.CustomClasses.Post;
import com.barlificent.ratify1.CustomClasses.User;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.barlificent.ratify1.CustomMethods;
import com.barlificent.ratify1.R;
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
public class ProfileDetailsFragment extends Fragment {
    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    UserPrefs userPrefs;
    View view;
    TextView uploads, rank, points;
    TextView bioView;
    EditText bioEnter;
    ListView list;
    CircleImageView pp;
    FontTextView name;
    Context c;


    public ProfileDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userPrefs = new UserPrefs(UserPrefs.USER_PREFS, getContext());
        view = inflater.inflate(R.layout.fragment_profile_details, container, false);

        c = getContext();
        pp = (CircleImageView) view.findViewById(R.id.profile_image);
        points = getTextView(R.id.profile_points);
        rank = getTextView(R.id.profile_rank);
        uploads = getTextView(R.id.profile_uploads);
        bioEnter = (EditText) view.findViewById(R.id.enter_bio);
        bioView = (TextView) view.findViewById(R.id.bio_view);
        list = (ListView) view.findViewById(R.id.profile_uploads_list);
        name = (FontTextView) view.findViewById(R.id.profile_name);

        view.findViewById(R.id.setBio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bioText = bioEnter.getText().toString();
                if (!bioText.equals("")) {
                    users.child(userPrefs.getId()).child("bio").setValue(bioText);
                } else {
                    Snackbar.make(rank.getRootView(), "Please enter your bio first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.edit_bio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeBio();
            }
        });

        getCurrentUserFromDatabase();
        getPosts();

        list.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scroll_profile);
                if (!scrollView.canScrollVertically(1))
                    scrollView.post(new Runnable() {

                        @Override
                        public void run() {
                            getActivity().findViewById(R.id.profile_scroll).getParent().requestDisallowInterceptTouchEvent(true);
                        }

                    });
                return false;
            }

        });
        pp.setImageResource(R.mipmap.user_icon);
        if (userPrefs.getPpUrl().equals("")) {
            pp.setImageResource(R.mipmap.insert_photo);
            pp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomMethods.selectImage(getActivity(), 1);
                }
            });

        } else {
            CustomMethods.loadPP(c, userPrefs.getId(), pp);
        }


        // Inflate the layout for this fragment
        return view;
    }

    public void changeBio() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_enter_name, null);
        final EditText field = (EditText) view.findViewById(R.id.dialogField);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Bio");
        builder.setView(view);
        builder.setCancelable(false)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        userPrefs.setBio(field.getText().toString());
                        users.child(userPrefs.getId()).child("bio").setValue(field.getText().toString());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public void getCurrentUserFromDatabase() {
        users.child(userPrefs.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String _name = user != null ? user.getName() : "";
                String _rank = (user != null ? user.getRank() : 0) + "";
                String _points = (user != null ? user.getPoints() : 0) + "";

                points.setText(_points);
                rank.setText(_rank);

                name.setText(userPrefs.getName());
                CustomMethods.loadPP(c, userPrefs.getId(), pp);

                if (dataSnapshot.hasChild("bio") && !user.getBio().equals("")) {
                    bioView.setText(user.getBio());
                    bioView.setVisibility(View.VISIBLE);
                    bioEnter.setVisibility(View.GONE);
                    view.findViewById(R.id.setBio).setVisibility(View.GONE);
                    view.findViewById(R.id.edit_bio).setVisibility(View.VISIBLE);
                }
                Log.w("Firebase", "Retrieved data.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getPosts() {
        final ArrayList<Post> postsArray = new ArrayList<>();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
        final DatabaseReference posts = users.child(userPrefs.getId()).child("posts");
        posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    postsArray.add(new Post(post.getPhotoUrl(), post.getDate(), post.getPointsCount()));
                }
                list.setAdapter(new UploadsAdapter(getContext(), postsArray));
//                view.findViewById(R.id.loading_profile_photos).setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
                uploads.setText(postsArray.size() + "");
                Log.w("postsLength", postsArray.size() + "");
                CustomMethods.setListViewHeightBasedOnItems(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static int add(String string) {
        int finalInt = 0;
        String f = "";
        ArrayList<Integer> positions = new ArrayList<>();
        int start = 0;
        for(char a : string.toCharArray())
            if(!(a+"").equals(" ")) f+=a;
        for (int i = 0; i < f.length(); i++)
            if ((f.charAt(i) + "").equals("+")) positions.add(i);
        for (int i = 0; i < positions.size() + 1; i++) {
            try {
                String number = f.substring(start, positions.get(i));
                finalInt += Integer.parseInt(number);
                start = positions.get(i) + 1;
            } catch (IndexOutOfBoundsException e) {
                String finalNumber = f.substring(positions.get(positions.size() - 1) + 1, f.length());
                finalInt += Integer.parseInt(finalNumber);
            }
        }
        return finalInt;
    }

    public TextView getTextView(int res) {
        return (TextView) view.findViewById(res);
    }
}
