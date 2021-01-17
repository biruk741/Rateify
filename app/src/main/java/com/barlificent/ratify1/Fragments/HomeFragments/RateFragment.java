package com.barlificent.ratify1.Fragments.HomeFragments;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.barlificent.ratify1.Adapters.OtherAdapter;
import com.barlificent.ratify1.Adapters.RotateDownTransformer;
import com.barlificent.ratify1.Adapters.SwipeAdapter;
import com.barlificent.ratify1.CustomClasses.FontTextView;
import com.barlificent.ratify1.CustomClasses.Post;
import com.barlificent.ratify1.CustomClasses.RateBar;
import com.barlificent.ratify1.CustomClasses.User;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.barlificent.ratify1.CustomMethods;
import com.barlificent.ratify1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class RateFragment extends Fragment {
    View view;
    ImageView sendRequest, rate, next, photoRate, emojiImageView;
    FontTextView score;
    RateBar ratingSeekBar;
    RelativeLayout rating_layout;
    ArrayList<Integer> used = new ArrayList<>();
    ArrayList<Post> currentPosts = new ArrayList<>();
    Post currentPost = new Post();
    int position = 0;
    UserPrefs userPrefs;
    int rateValue;
    ValueEventListener postsListener;
    Query postQuery;
    AVLoadingIndicatorView loadingRate;
    SwipeFlingAdapterView swipeFlingAdapterView;
    SwipeAdapter adapter;
    OtherAdapter adapterOther;
    String currentNode = null;
    ViewPager images;
    DatabaseReference posts = FirebaseDatabase.getInstance().getReference("uploads");
    Query query;
    ValueEventListener eventListener;


    public RateFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rate, container, false);
        adapterOther = new OtherAdapter(getContext(),currentPosts);

        images = (ViewPager) view.findViewById(R.id.rate_images);
        userPrefs = new UserPrefs(UserPrefs.USER_PREFS, getContext());
        sendRequest = getImageView(R.id.rate_send_request);
        rate = getImageView(R.id.rate_start_rating);
        next = getImageView(R.id.rate_next);
        photoRate = getImageView(R.id.rateImageView);
        emojiImageView = getImageView(R.id.emojiRate);
        ratingSeekBar = (RateBar) view.findViewById(R.id.rateBar);
        score = (FontTextView) view.findViewById(R.id.rate_score);
        rating_layout = (RelativeLayout) view.findViewById(R.id.rating_linear);
        loadingRate = (AVLoadingIndicatorView) view.findViewById(R.id.rate_loading);


//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                goNext();
//                rating_layout.setVisibility(View.INVISIBLE);
//            }
//        });
//        rate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (index == 0) {
//                    rating_layout.setVisibility(View.VISIBLE);
////     todo               view.findViewById(R.id.emoji_container).setVisibility(View.VISIBLE);
//                    ratingSeekBar.setMax(10);
//                    ratingSeekBar.setOnSeekBarChangeListener(new RateBar.OnRateBarChangeListener() {
//                        @Override
//                        public void onProgressChanged(RateBar var1, int var2) {
//                            Log.w("rateValue", var2 + "");
//                            rate(var2);
//                        }
//                    });
//                    rate.setImageResource(R.mipmap.check_circle);
//                    index = 1;
//                } else {
//                    rating_layout.setVisibility(View.INVISIBLE);
//                    final int theScore = rateValue;
//                    final DatabaseReference posts = FirebaseDatabase.getInstance().getReference("uploads");
//                    final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
////                    Log.w("Poster Id",currentPost.getUploader()());
//                    final boolean[] done = {false};
//                    loadingRate.setVisibility(View.VISIBLE);
////                    users.child(currentPost.getUploader()).addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            User user = dataSnapshot.getValue(User.class);
////                            posts.child(currentPost.getPhotoUrl()).child("rates").child(userPrefs.getId()).setValue(theScore);
////                            Log.w("User has", user.getPoints() + " points                      <---------");
////
////                            if (!done[0]) {
////                                users.child(currentPost.getUploader()).child("points").setValue(user.getPoints() + theScore);
////                                done[0] = true;
////                            }
////                            loadingRate.setVisibility(View.GONE);
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
////                    posts.child(currentPost.getPhotoUrl()).addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            Post post = dataSnapshot.getValue(Post.class);
////                            posts.child(currentPost.getPhotoUrl()).child("pointsCount").setValue(post.getPointsCount() + rateValue);
////                            users.child(currentPost.getUploader()).child("posts").child(currentPost.getPhotoUrl())
////                                    .child("pointsCount").setValue(post.getPointsCount() + rateValue);
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
//                    Log.w("currentPost",currentPost.getPhotoUrl());
//                    rate(currentPost.getPhotoUrl(),rateValue);
//
//                    rate.setImageResource(R.mipmap.rate_circle);
//                    index = 0;
////                    goNext();
//                }
//            }
//        });
//        sendRequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
//                AlertDialog alertDialog = (new AlertDialog.Builder(getContext())).create();
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("Confirmation");
//                builder.setMessage("Are you sure you want to send a request to this user?");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
////                        users.child(currentPost.getUploader()).addListenerForSingleValueEvent(new ValueEventListener() {
////                            @Override
////                            public void onDataChange(DataSnapshot dataSnapshot) {
////                                User user = dataSnapshot.getValue(User.class);
////                                if (user.canBeSentRequest()) {
////                                    users.child(currentPost.getUploader())
////                                            .child("friends")
////                                            .child(userPrefs.getId())
////                                            .setValue(new Request(userPrefs.getName(), userPrefs.getId(), false));
//////                        FirebaseDatabase.getInstance().getReference("users").child(userPrefs.getId())
//////                                .child("friends")
//////                                .child(currentPost.getUploader()).setValue(new Request(userPrefs.getName(),userPrefs.getId(),false));
////                                }
////                                else{
////                                    new AlertDialog.Builder(getContext()).setTitle("Sorry").setMessage("This user does not wish to receive requests from people.")
////                                            .create().show();
////                                }
////                            }
////
////                            @Override
////                            public void onCancelled(DatabaseError databaseError) {
////
////                            }
////                        });
//                        users.child(currentPost.getUploader())
//                                .runTransaction(new Transaction.Handler() {
//                                    @Override
//                                    public Transaction.Result doTransaction(MutableData mutableData) {
//                                        User user = new User();
//                                        if (mutableData.getValue(Post.class) != null) {
//                                            user = mutableData.getValue(User.class);
//                                        }
//                                        if (user.canBeSentRequest()) {
//                                            users.child(currentPost.getUploader())
//                                                    .child("friends")
//                                                    .child(userPrefs.getId())
//                                                    .setValue(new Request(userPrefs.getName(), userPrefs.getId(), false));
//                                        }
//                                        // Set value and report transaction success
//                                        mutableData.setValue(user);
//                                        return Transaction.success(mutableData);
//                                    }
//
//                                    @Override
//                                    public void onComplete(DatabaseError databaseError, boolean b,
//                                                           DataSnapshot dataSnapshot) {
//                                        Log.w("Rate", "Rated Successfully!          <-----|");
//                                        // Transaction completed
//                                    }
//                                });
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//                builder.create().show();
//            }
//        });

//        swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
//            @Override
//            public void removeFirstObjectInAdapter() {
//
//            }
//
//            @Override
//            public void onLeftCardExit(Object o) {
//
//            }
//
//            @Override
//            public void onRightCardExit(Object o) {
//
//            }
//
//            @Override
//            public void onAdapterAboutToEmpty(int i) {
//                try {
//                    getPOSTS(adapter.getItem(i).getPhotoUrl());
//                    adapter.notifyDataSetChanged();
//                    Log.w("adapter","about to empty");
//                } catch (Exception ignored) {
//                    Log.w("adapter","about to empty error");
//                }
//            }
//
//            @Override
//            public void onScroll(float v) {
//
//            }
//        });

        if (!CustomMethods.deviceIsConnected(getContext()))
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomMethods.makeAndShowDialog(getContext(), "Error", "No internet connection!");
                }
            }, 2000);

        getPOSTS(currentNode,0);
        images.setPageTransformer(true,new RotateDownTransformer());
        images.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                try{
                    if(position == currentPosts.size() - 1) {
                        getPOSTS("",position+1);
                        Log.w("id",currentPosts.get(currentPosts.size() - 1).getPhotoUrl());
                    }

                    currentPost = currentPosts.get(position);

                    if (position == currentPosts.size() - 1) {
                        Toast.makeText(getContext(), "No more uploads", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return view;
    }

    private void rate(int j) {
        try {
            Context context = emojiImageView.getContext();
            int id = context.getResources().getIdentifier("emoji_" + j, "drawable", context.getPackageName());
            emojiImageView.setImageResource(id);
            rateValue = j;
        } catch (Exception ignored) {
        }
    }
    public ImageView getImageView(@IdRes int resId) {
        return (ImageView) view.findViewById(resId);
    }

    public static void loadPicture(final Context context, String id, final ImageView imageView) {
        StorageReference load =
                FirebaseStorage.getInstance().getReferenceFromUrl("gs://ratify1-c299d.appspot.com").child("posts/" + id);

        Log.w("Image id", id);

        load.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                new Picasso.Builder(context).downloader(new OkHttpDownloader(context, Integer.MAX_VALUE)).build()
                        .load(uri.toString())
                        .placeholder(R.mipmap.loading_placeholder)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public static String convertDate(long dateInMilliseconds) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(dateInMilliseconds);
        String day = cl.get(Calendar.DAY_OF_MONTH) + "";
        String month = cl.get(Calendar.MONTH) + "";
        String time = cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
        String monthString = "January";
        String[] numbers = {"01","02","03","04","05","06","07","08","09","10","11","12"};
        String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        for(int i = 0;i<numbers.length;i++){
            if(numbers[i].equals(month)) monthString = months[i];
        }
        return String.format("%s %s, %s", monthString, day, time);
    }

    public void goNext() {
        try {
            position++;
            currentPost = currentPosts.get(position);
            loadPicture(getContext(), currentPost.getPhotoUrl(), photoRate);
            rateValue = 0;
            ratingSeekBar.setProgress(rateValue);
        } catch (Exception e) {
//            getPOSTS(currentNode);
            Toast.makeText(getContext(), "Getting more uploads...", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.w("OnPause", "disconnected");
        try {
            query.removeEventListener(eventListener);
        } catch (Exception e) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.w("OnStop", "disconnected");
        try {
            query.removeEventListener(eventListener);
        } catch (Exception ignored) {
        }
    }
//    private void rate(String id, final int rateCount) {
//
//        final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
//
//        FirebaseDatabase.getInstance().getReference("uploads").child(id)
//                .runTransaction(new Transaction.Handler() {
//                    @Override
//                    public Transaction.Result doTransaction(MutableData mutableData) {
//                        Post p = new Post("", 0, "");
//                        if (mutableData.getValue(Post.class) != null) {
//                            p = mutableData.getValue(Post.class);
//                            Log.w("Code", "Running");
//                        }
//
//                        //todo:  fishy code here
//                        if (!(p.rates.containsKey(userPrefs.getId())))
//                            p.setPointsCount(p.getPointsCount() + rateCount);
//                        else {
//
//                        }
//                        p.rates.put(userPrefs.getId(), rateCount);
//
//                        users.child(p.getUploader()).runTransaction(new Transaction.Handler() {
//                            @Override
//                            public Transaction.Result doTransaction(MutableData mutableData) {
//                                User p = mutableData.getValue(User.class);
//                                if (p == null) {
//                                    return Transaction.success(mutableData);
//                                }
//                                p.setPoints(p.getPoints() + rateCount);
//                                mutableData.setValue(p);
//                                return Transaction.success(mutableData);
//                            }
//
//                            @Override
//                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//                                Log.w("Error",databaseError + "")
//                            }
//                        });
//                        // Set value and report transaction success
//                        mutableData.setValue(p);
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(DatabaseError databaseError, boolean b,
//                                           DataSnapshot dataSnapshot) {
//                        Log.w("Rate", "Rated Successfully!          <-----|");
//                        loadingRate.setVisibility(View.GONE);
//                        // Transaction completed
//                    }
//                });
//    }
    private void getPOSTS(final String nodeId,int pos) {
        int numberOfPosts = 4;
        Log.w("nodeId", nodeId + "\n" + pos);

        if (nodeId == null)
            query = FirebaseDatabase.getInstance().getReference("uploads")
                    .orderByChild("date")
                    .limitToFirst(numberOfPosts);
        else
            query = FirebaseDatabase.getInstance().getReference("uploads")
                    .orderByChild("date")
                    .startAt(pos)
                    .limitToFirst(numberOfPosts);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Post> postss = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Post post = userSnapshot.getValue(Post.class);
                    switch (userPrefs.getPreference()) {
                        case "men":
                            if (post.getGender().equals("male")) postss.add(post);
                            break;
                        case "women":
                            if (post.getGender().equals("female")) postss.add(post);
                            break;
                        case "both":
                            postss.add(post);
                            break;
                    }
                    Log.w("currentPost", post.getPhotoUrl() + "");
                }

                if (nodeId == null) {
                    currentPosts = postss;
                    adapterOther = new OtherAdapter(getContext(),currentPosts);
                    images.setAdapter(adapterOther);
                    view.findViewById(R.id.rate_main_loading).setVisibility(View.GONE);
                    Log.w("Error", "Code is running\n" + currentPosts.size());
                } else {
                    currentPosts.addAll(postss);
                    adapterOther.addAll(postss);
                    adapterOther.notifyDataSetChanged();
                    Log.w("length",currentPosts.size() + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(eventListener);
    }

    public String generateRandom() {
        StringBuilder d = new StringBuilder();
        String[] s = new String[]{"a", "b", "c", "d", "e"};
        for (String a : s) d.append(s[new Random().nextInt()]);
        return d.toString();
    }
}
