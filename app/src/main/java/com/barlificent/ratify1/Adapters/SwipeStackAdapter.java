package com.barlificent.ratify1.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.barlificent.ratify1.CustomClasses.Post;
import com.barlificent.ratify1.CustomClasses.RateBar;
import com.barlificent.ratify1.CustomClasses.Request;
import com.barlificent.ratify1.CustomClasses.User;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.barlificent.ratify1.CustomMethods;
import com.barlificent.ratify1.MainActivity;
import com.barlificent.ratify1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2015 on 1/6/2018.
 */
public class SwipeStackAdapter extends BaseAdapter {
    UserPrefs userPrefs;
    View view;
    ImageView image_rate,request,rate_button,fav,report,emoji;
    Post currentPost;
    RateBar rateBar;
    ArrayList<Post> posts = new ArrayList<>();
    Context context;

    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    public SwipeStackAdapter(Context context, ArrayList<Post> words) {
        posts = words;
        this.context = context;
    }


    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Post getItem(int i) {
        return posts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addAll(ArrayList<Post> k){
        posts.addAll(k);
        notifyDataSetChanged();
        for(Post s : k){
            Log.w("addAll",s.getPhotoUrl());
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        userPrefs = new UserPrefs(UserPrefs.USER_PREFS,context);
        final int[] index = {0};
        final int[] rateValue = {0};

        view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.list_item_rate, parent, false);
        }

        currentPost = posts.get(position);

//        image_rate = getImage(R.id.image_rate);
        request = getImage(R.id.send_request_button);
        rate_button = getImage(R.id.rate_button);
        fav = getImage(R.id.fav);
        report = getImage(R.id.report);
        rateBar = (RateBar) view.findViewById(R.id.rateBar);
        emoji = getImage(R.id.emojiRate);

        rate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index[0] == 0){
//     todo               view.findViewById(R.id.emoji_container).setVisibility(View.VISIBLE);
                    rateBar.setMax(10);
                    rateBar.setOnSeekBarChangeListener(new RateBar.OnRateBarChangeListener() {
                        @Override
                        public void onProgressChanged(RateBar var1, int var2) {
                            int id = context.getResources().getIdentifier("emoji_" + var2, "drawable", context.getPackageName());
                            emoji.setImageResource(id);
                            rateValue[0] = var2;
                        }
                    });
                    rate_button.setImageResource(R.mipmap.check_circle);
                    index[0] = 1;
                } else {
                    final int theScore = rateValue[0];
                    final DatabaseReference posts = FirebaseDatabase.getInstance().getReference("uploads");
                    final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
                    final boolean[] done = {false};
//                    users.child(currentPost.getUploader()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            User user = dataSnapshot.getValue(User.class);
//                            posts.child(currentPost.getPhotoUrl()).child("rates").child(userPrefs.getId()).setValue(theScore);
//                            Log.w("User has", user.getPoints() + " points                      <---------");
//
//                            if (!done[0]) {
//                                users.child(currentPost.getUploader()).child("points").setValue(user.getPoints() + theScore);
//                                done[0] = true;
//                            }
//                            loadingRate.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                    posts.child(currentPost.getPhotoUrl()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Post post = dataSnapshot.getValue(Post.class);
//                            posts.child(currentPost.getPhotoUrl()).child("pointsCount").setValue(post.getPointsCount() + rateValue);
//                            users.child(currentPost.getUploader()).child("posts").child(currentPost.getPhotoUrl())
//                                    .child("pointsCount").setValue(post.getPointsCount() + rateValue);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
                    Log.w("currentPost",currentPost.getPhotoUrl());

                    rate(currentPost.getPhotoUrl(), rateValue[0]);

                    rate_button.setImageResource(R.mipmap.rate_circle);
                    index[0] = 0;
                }
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to send a request to this user?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        users.child(currentPost.getUploader()).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                User user = dataSnapshot.getValue(User.class);
//                                if (user.canBeSentRequest()) {
//                                    users.child(currentPost.getUploader())
//                                            .child("friends")
//                                            .child(userPrefs.getId())
//                                            .setValue(new Request(userPrefs.getName(), userPrefs.getId(), false));
////                        FirebaseDatabase.getInstance().getReference("users").child(userPrefs.getId())
////                                .child("friends")
////                                .child(currentPost.getUploader()).setValue(new Request(userPrefs.getName(),userPrefs.getId(),false));
//                                }
//                                else{
//                                    new AlertDialog.Builder(context).setTitle("Sorry").setMessage("This user does not wish to receive requests from people.")
//                                            .create().show();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
                        users.child(currentPost.getUploader())
                                .runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        User user = new User();
                                        if (mutableData.getValue(Post.class) != null) {
                                            user = mutableData.getValue(User.class);
                                        }
                                        if (user.canBeSentRequest()) {
                                            users.child(currentPost.getUploader())
                                                    .child("friends")
                                                    .child(userPrefs.getId())
                                                    .setValue(new Request(userPrefs.getName(), userPrefs.getId(), false));
                                        }
                                        else CustomMethods.makeAndShowDialog(context,"Sorry","This user does not want to receive requests.");
                                        mutableData.setValue(user);
                                        return Transaction.success(mutableData);
                                    }
                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                        Log.w("Rate", "Rated Successfully!");
                                    }
                                });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            }
        });
        CustomMethods.loadPicture(context,currentPost.getPhotoUrl(),image_rate);


        return view;
    }


    private ImageView getImage(@IdRes int res){
        return (ImageView) view.findViewById(res);
    }

    private void rate(String id, final int rateCount) {
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
        FirebaseDatabase.getInstance().getReference("uploads").child(id)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Post p = new Post("", 0, "");
                        if (mutableData.getValue(Post.class) != null) {
                            p = mutableData.getValue(Post.class);
                            Log.w("Code", "Running");
                        }

                        //todo:  fishy code here
                        if(!(p.rates.containsKey(userPrefs.getId())))
                            p.setPointsCount(p.getPointsCount() + rateCount);
                        else {

                        }
                        p.rates.put(userPrefs.getId(), rateCount);

                        users.child(p.getUploader()).runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                User p = mutableData.getValue(User.class);
                                if (p == null) {
                                    return Transaction.success(mutableData);
                                }
                                p.setPoints(p.getPoints() + rateCount);
                                mutableData.setValue(p);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
                        mutableData.setValue(p);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        Log.w("Rate", "Rated Successfully!");
                    }
                });
    }
}
