package com.barlificent.ratify1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.barlificent.ratify1.CustomClasses.Post;
import com.barlificent.ratify1.CustomClasses.User;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 2015 on 1/7/2018.
 */
public class CustomMethods {
    public static void selectImage(Activity a, int code) {
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        a.startActivityForResult(Intent.createChooser(intent, "Select Picture"), code);
    }

    public static String generateRandom(int length) {
        String[] letters = new String[]{
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "q", "w", "e", "r", "t", "y", "u", "i", "s", "g", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "A","S","D","F","G","H","J","K","L","Z","X","C","V","B","N","M","z","c","v","b","m",""
        };
        String s = "";
        for (int i = 0; i < length; i++) {
            s += letters[new Random().nextInt(letters.length)];
        }
        return "-" + s;
    }

    public static void loadPP(final Context context, String id, final ImageView imageView) {
        try {
            final StorageReference load =
                    FirebaseStorage.getInstance().getReferenceFromUrl("gs://ratify1-c299d.appspot.com").child(id);

            load.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    // Pass it to Picasso to download, show in ImageView and caching
                    new Picasso.Builder(context).downloader(new OkHttpDownloader(context, Integer.MAX_VALUE)).build()
                            .load(uri.toString())
                            .noFade()
                            .placeholder(R.mipmap.user_icon)
                            .fit()
                            .centerCrop()
                            .into(imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    public static void loadPicture(final Context context, String id, final ImageView imageView) {
        StorageReference load =
                FirebaseStorage.getInstance().getReferenceFromUrl("gs://ratify1-c299d.appspot.com").child("commentImages/" + id);

        load.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                // Pass it to Picasso to download, show in ImageView and caching
                new Picasso.Builder(context).downloader(new OkHttpDownloader(context, Integer.MAX_VALUE)).build()
                        .load(uri.toString())
                        .noFade()
                        .placeholder(R.mipmap.user_icon)
                        .resize(300, 300)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public static void setPP(Context context, final String id, final Bitmap bitmap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            byte[] bytes = outputStream.toByteArray();

            final UserPrefs userPrefs = new UserPrefs(UserPrefs.USER_PREFS, context);
            StorageReference postImages = FirebaseStorage.getInstance().getReferenceFromUrl("gs://ratify1-c299d.appspot.com");
            final UploadTask uploadTask = postImages.child(userPrefs.getId()).putBytes(bytes);

            final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
            final ProgressDialog progressDialog = ProgressDialog.show(context, "", "Uploading...", true, false);
            progressDialog.show();
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.hide();
                    users.child(id).child("image_id").setValue(id);
                    userPrefs.setPpUrl(id);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Post> getPosts(int number, Activity activity) {
        final ArrayList<Post> postsArray = new ArrayList<>();
        DatabaseReference posts = FirebaseDatabase.getInstance().getReference("uploads");
        final Query query = posts.orderByChild("date").limitToLast(5);
        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    postsArray.add(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
        return reverseArrayList(postsArray);
    }

    public static ArrayList<Post> reverseArrayList(ArrayList<Post> b) {
        ArrayList<Post> a = new ArrayList<>();
        int c = b.size() - 1;
        for (int i = 0; i < b.size(); i++) {
            a.add(b.get(c));
            c--;
        }
        return a;
    }

    public static String getNameOf(String id) {
        final String[] name = {""};
        FirebaseDatabase.getInstance().getReference("users")
                .child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name[0] = user != null ? user.getName() : "";
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return name[0];
    }

    public static Bitmap compressBitmap(Bitmap bitmap) {
        int targetWidth = 840; // your arbitrary fixed limit
        int targetHeight = (int) (bitmap.getHeight() * targetWidth / (double) bitmap.getWidth());
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
    }
    public static boolean deviceIsConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean b =
         activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Log.w("Internet",b + "");
        return b;
    }
    public static void makeAndShowDialog(Context context,String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title);
        builder.create().show();
    }
    public static void setBio(String bio,String userId){
        FirebaseDatabase.getInstance().getReference("users").child(userId).child("bio").setValue(bio);
    }
    public static String getLocale(Context context){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkCountryIso();
    }
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }
}
