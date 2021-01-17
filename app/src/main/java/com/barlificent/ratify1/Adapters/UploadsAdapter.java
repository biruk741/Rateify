package com.barlificent.ratify1.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.barlificent.ratify1.CustomClasses.PictUtil;
import com.barlificent.ratify1.CustomClasses.Post;
import com.barlificent.ratify1.CustomMethods;
import com.barlificent.ratify1.CustomClasses.FontTextView;
import com.barlificent.ratify1.Fragments.HomeFragments.RateFragment;
import com.barlificent.ratify1.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 2015 on 1/10/2018.
 */
public class UploadsAdapter extends ArrayAdapter<Post> {
    ListView listView;
    ArrayList<Post> words;

    ImageView uploadImage;
    TextView date;
    FontTextView points;
    int index;
    CheckBox checkBox;
    public ArrayList<Boolean> booleans = new ArrayList<>();

    Bitmap bitmap;

    DatabaseReference posts = FirebaseDatabase.getInstance().getReference("posts");


    public UploadsAdapter(Context context, ArrayList<Post> words) {
        super(context, 0, words);
        this.words = words;
        init();
    }

    public UploadsAdapter(Context context, ArrayList<Post> words, ListView listView) {
        super(context, 0, words);
        this.listView = listView;
        this.words = words;
        init();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_uploads, parent, false);
        }
        Log.w("GetView", "Called");
        final Post currentPost = getItem(position);

        checkBox = (CheckBox) listItemView.findViewById(R.id.checkbox_uploads);
        uploadImage = (ImageView) listItemView.findViewById(R.id.image_upload);
        date = (TextView) listItemView.findViewById(R.id.date_upload);
        points = (FontTextView) listItemView.findViewById(R.id.points_upload);

        listItemView.findViewById(R.id.save_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "Rateify" + CustomMethods.generateRandom(3);
                PictUtil.saveBitmap(bitmap,name);
                Toast.makeText(getContext(),"Saved successfully.\nFile name: " + name + ".jpg",Toast.LENGTH_SHORT).show();
            }
        });
        listItemView.findViewById(R.id.delete_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "Rateify" + CustomMethods.generateRandom(3);
                PictUtil.saveBitmap(bitmap,name);
                Toast.makeText(getContext(),"Saved successfully.\nFile name: " + name + ".jpg",Toast.LENGTH_SHORT).show();
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                booleans.set(position,b);
            }
        });

        if(index == 1) listItemView.findViewById(R.id.delete_upload).setVisibility(View.GONE);
        else if(index == 2) listItemView.findViewById(R.id.check_holder_uploads).setVisibility(View.VISIBLE);

        loadPicture(getContext(), currentPost.getPhotoUrl(), uploadImage);
        date.setText(RateFragment.convertDate(currentPost.getDate()));

        points.setText(currentPost.getPointsCount() + "");
        return listItemView;
    }

    public void init(){
        for(int i = 0;i<getCount();i++)
            booleans.add(false);
    }

    public void loadPicture(final Context context, String id, final ImageView imageView) {
        StorageReference load =
                FirebaseStorage.getInstance().getReferenceFromUrl("gs://ratify1-c299d.appspot.com").child("posts/" + id);

        load.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                final RequestCreator builder = new Picasso.Builder(context).downloader(new OkHttpDownloader(context, Integer.MAX_VALUE)).build()
                        .load(uri.toString())
                        /*.transform(new Transformation() {
                            @Override
                            public Bitmap transform(Bitmap source) {
                                float scale = (float) source.getWidth() / 500;
                                final int fixedHeight = (int) Math.round(source.getHeight() / scale);
                                final Bitmap rval = Bitmap.createScaledBitmap(source, 500, fixedHeight, true);
                                if (!source.isRecycled())
                                    source.recycle();
                                return rval;
                            }

                            @Override
                            public String key() {
                                return "screensized";
                            }
                        })*/;
//                    new AsyncTask<Void, Void, Void>() {
//                        @Override
//                        protected Void doInBackground(Void... voids) {
//                            try {
//                                bitmap = builder.get();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            return null;
//                        }
//                    }.execute();
                        builder.into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
