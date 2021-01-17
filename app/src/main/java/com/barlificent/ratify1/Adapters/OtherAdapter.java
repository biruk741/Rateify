package com.barlificent.ratify1.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barlificent.ratify1.CustomClasses.Favorites;
import com.barlificent.ratify1.CustomClasses.Post;
import com.barlificent.ratify1.CustomClasses.Request;
import com.barlificent.ratify1.CustomClasses.User;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.barlificent.ratify1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;
import ru.bullyboo.view.CircleSeekBar;

/**
 * Created by 2015 on 1/19/2018.
 */

public class OtherAdapter extends PagerAdapter {
    ArrayList<Post> posts = new ArrayList<>();
    Post currentPost;
    Context context;
    int index = 0;
    View view;

    public OtherAdapter(Context context, ArrayList<Post> posts) {
        this.posts = posts;
        this.context = context;
    }

    private int rateValue = 0;

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        view = LayoutInflater.from(context).inflate(R.layout.list_item_rate, null);
        final ImageView image = (ImageView) view.findViewById(R.id.image);
        final LikeButton rateButton = (LikeButton) view.findViewById(R.id.rate_button);
        LikeButton request = (LikeButton) view.findViewById(R.id.send_request_button);
        final CircleSeekBar circleSeekBar = (CircleSeekBar) view.findViewById(R.id.rate_bar_circle);
        final ImageView emoji = (ImageView) view.findViewById(R.id.emojiRate);
        final TextView score = (TextView) view.findViewById(R.id.rate_value);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        loadPicture(context, posts.get(position).getPhotoUrl(), image);
        emoji.setImageResource(R.drawable.emoji_0);

//        rateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                circleSeekBar.setVisibility(View.VISIBLE);
//                rateButton.setLiked(true);
//                Blurry.with(context).radius(10).color(context.getResources().getColor(R.color.trans_blacks)).animate(500).capture(image).into(image);
//            }
//        });
        rateButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                currentPost = posts.get(position);
                circleSeekBar.setVisibility(View.VISIBLE);
                rateButton.setLiked(true);
                Blurry.with(context).radius(10).color(context.getResources().getColor(R.color.trans_blacks)).animate(500).capture(image).into(image);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                currentPost = posts.get(position);
            }
        });
        final int[] current = {0};
        circleSeekBar.setOnValueChangedListener(new CircleSeekBar.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                try {
                    Context context = emoji.getContext();
                    int s = value / 10;
                    if (current[0] != s) {
                        int id = context.getResources().getIdentifier("emoji_" + s, "drawable", context.getPackageName());
                        emoji.setImageResource(id);
                        score.setText(s + "");
                        rateValue = s;
                    }
                    current[0] = value;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        circleSeekBar.setCallback(new CircleSeekBar.Callback() {
            @Override
            public void onStartScrolling(int startValue) {
            }

            @Override
            public void onEndScrolling(int endValue) {
                currentPost = posts.get(position);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("RateId",currentPost.getPhotoUrl() + "");
                        rate(currentPost.getPhotoUrl(), rateValue);
                        circleSeekBar.setVisibility(View.GONE);
                    }
                }, 1000);
            }
        });
        view.findViewById(R.id.report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileReport();
            }
        });
        ((LikeButton) view.findViewById(R.id.fav)).setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                currentPost = posts.get(position);
                Favorites favorites = new Favorites(context);
                favorites.addPost(currentPost);
                new AlertDialog.Builder(context).setTitle("Saved!")
                        .setMessage("Saved to favorites. You can view this upload at any time.")
                        .setIcon(R.drawable.ic_favorite_pink_24dp)
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .create().show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {

            }
        });
        ((LikeButton) view.findViewById(R.id.send_request_button)).setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                currentPost = posts.get(position);
                sendRequest(context,currentPost.getUploader(),true);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                currentPost = posts.get(position);
          //      sendRequest(context,currentPost.getPhotoUrl(),false);
            }
        });


        ((ViewPager) container).addView(view/* position*/);
        return view;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    public void addAll(ArrayList<Post> postss) {
        posts.addAll(postss);
        try {
            if (postss.size() > 0) posts.remove(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        index++;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }


    private void loadPicture(final Context context, String id, final ImageView imageView) {
        StorageReference load =
                FirebaseStorage.getInstance().getReferenceFromUrl("gs://ratify1-c299d.appspot.com").child("posts/" + id);
        view.findViewById(R.id.rate_loading).setVisibility(View.VISIBLE);
        load.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                // Pass it to Picasso to download, show in ImageView and caching
                new Picasso.Builder(context).downloader(new OkHttpDownloader(context, Integer.MAX_VALUE)).build()
                        .load(uri.toString())
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                view.findViewById(R.id.rate_loading).setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void fileReport() {
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_enter_report, null);
        final EditText field = (EditText) view.findViewById(R.id.dialogField);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report");
        builder.setView(view);
        builder.setCancelable(false)
                .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText editText = (EditText) view.findViewById(R.id.reasonReport);
                        DatabaseReference reports = FirebaseDatabase.getInstance().getReference("reports");
                        reports.child(currentPost.getPhotoUrl()).child("id").setValue(currentPost.getPhotoUrl());
                        reports.child(currentPost.getPhotoUrl()).child("reason").setValue(editText.getText().toString());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    private void rate(String id, final int rateCount) {

        Log.w("rateid", id);
        final UserPrefs userPrefs = new UserPrefs(context);
        final boolean[] rated = {false};
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

        FirebaseDatabase.getInstance().getReference("uploads").child(id)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Post p = new Post("", 0, "");
                        if (mutableData.getValue(Post.class) != null) {
                            p = mutableData.getValue(Post.class);
                            Log.w("mutableData", "Running");
                        } else Log.w("mutableData", "isEmpty");

                        //todo:  fishy code here
                        if (!(p.rates.containsKey(userPrefs.getId()))) {
                            rated[0] = true;
                            p.setPointsCount(p.getPointsCount() + rateCount);
                        }
                        p.rates.put(userPrefs.getId(), rateCount);

                        users.child(p.getUploader()).runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                User p = mutableData.getValue(User.class);
                                if (p == null) {
                                    return Transaction.success(mutableData);
                                }
                                if(!rated[0])p.setPoints(p.getPoints() + rateCount);
                                if (p.getUserId() != null && !p.getUserId().equals(""))
                                    mutableData.setValue(p);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                Log.w("userTransaction", databaseError + "");
                            }
                        });
                        // Set value and report transaction success
                        if (allNotNull(p.getDate(), p.getGender(), p.getPhotoUrl()) && !p.getGender().equals("") && !p.getUploader().equals("")) {
                            mutableData.setValue(p);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        Log.w("Rate", "Rated Successfully!          <-----|" + databaseError);
                        // Transaction completed
                    }
                });
    }

    public boolean allNotNull(Object... objects) {
        boolean b = true;
        for (Object a : objects) if (a == null) b = false;
        return b;
    }

    public static void sendRequest(final Context context, final String id, final boolean friend) {
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
        final UserPrefs userPrefs = new UserPrefs(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to send a request to this user?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                users.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.w("requestId",id + "\n");
                        User user = dataSnapshot.getValue(User.class);
                        if(friend) {
                            if (user.canBeSentRequest()) {
                                users.child(id)
                                        .child("friends")
                                        .child(userPrefs.getId())
                                        .setValue(new Request(userPrefs.getName(), userPrefs.getId(), false));
                                users.child(userPrefs.getId())
                                        .child("friends")
                                        .child(id).setValue(new Request(user.getName() + "", user.getUserId() + "", false));
                                makeAlertDialog(R.drawable.unsend, "Request sent!", context);
                            } else {
                                new AlertDialog.Builder(context).setTitle("Sorry").setMessage("This user does not wish to receive requests from people.")
                                        .setIcon(R.drawable.unsend)
                                        .create().show();
                            }
                        }
                        else{
                                users.child(id)
                                        .child("friends")
                                        .child(userPrefs.getId())
                                        .setValue(null);
                                users.child(userPrefs.getId())
                                        .child("friends")
                                        .child(id).setValue(null);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
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

    public static void makeAlertDialog(int res, String title, Context context) {
        new AlertDialog.Builder(context).setIcon(res).setTitle(title).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }
}
