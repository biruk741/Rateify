package com.barlificent.ratify1.Fragments.HomeFragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.barlificent.ratify1.CameraActivity;
import com.barlificent.ratify1.CustomMethods;
import com.barlificent.ratify1.CustomClasses.Post;
import com.barlificent.ratify1.Manifest;
import com.barlificent.ratify1.R;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment {

    int cameraPermission = 21;
    ImageView photoTaken, photoCancel, photoRetake, photoUpload;
    StorageReference postImages = FirebaseStorage.getInstance().getReferenceFromUrl("gs://ratify1-c299d.appspot.com");
    DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference posts = FirebaseDatabase.getInstance().getReference("uploads");
    UserPrefs userPrefs;
    View view;

    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_upload, container, false);
        userPrefs = new UserPrefs(UserPrefs.USER_PREFS, getContext());
        photoTaken = (ImageView) view.findViewById(R.id.takenPhoto);
        photoCancel = (ImageView) view.findViewById(R.id.cancel_photo);
        photoRetake = (ImageView) view.findViewById(R.id.take_photo_again);
        photoUpload = (ImageView) view.findViewById(R.id.choose_photo);

        view.findViewById(R.id.cardView_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != 0) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            21);
                } else {
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                }
            }
        });
        view.findViewById(R.id.cardView_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != 0) {
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{android.Manifest.permission.CAMERA},
//                            21);
//                } else {
//                    Intent cameraIntent = new Intent(
//                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    getActivity().startActivityForResult(cameraIntent, 69);
//
//                }
                startActivity(new Intent(getContext(), CameraActivity.class));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w("Request", requestCode + "");
        if ((requestCode == 1 || requestCode == 69) && resultCode == -1 && data != null && data.getData() != null) {
            Uri uri = data.getData();
            view.findViewById(R.id.upload_beginning_linear).setVisibility(View.GONE);
            view.findViewById(R.id.upload_camera_linear).setVisibility(View.VISIBLE);

            try {
                final Bitmap bitmap = requestCode == 1 ? MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri) : (Bitmap) data.getExtras().get("data");
                final String identifier = userPrefs.getId() + CustomMethods.generateRandom(5);
                photoTaken.setImageBitmap(bitmap);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                CustomMethods.compressBitmap(bitmap).compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                final byte[] bytes = outputStream.toByteArray();

                photoUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View vd) {
                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Uploading...");
                        progressDialog.show();

                        UploadTask uploadTask = postImages.child("posts/" + identifier).putBytes(bytes);

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                users.child(userPrefs.getId()).child("posts").child(identifier).child("date").setValue(System.currentTimeMillis());
                                users.child(userPrefs.getId()).child("posts").child(identifier).child("photoUrl").setValue(identifier);
                                userPrefs.addPost(System.currentTimeMillis() + "", identifier);
                                Post post = new Post(userPrefs.getId(),System.currentTimeMillis(),identifier);
                                post.setRateCount(0);
                                post.setPointsCount(0);
                                post.setLocale(userPrefs.getLocale());
                                post.setGender(userPrefs.getGender());
                                posts.child(identifier).setValue(post);
                                view.findViewById(R.id.upload_beginning_linear).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.upload_camera_linear).setVisibility(View.GONE);
                                progressDialog.hide();
                            }
                        });
                    }
                });
                photoCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.findViewById(R.id.upload_beginning_linear).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.upload_camera_linear).setVisibility(View.GONE);
                    }
                });
                photoRetake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode==1?1:69);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else Log.w("OnActivityResult", "Error");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 21:            //todo:           cameraPermission
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                } else {
                    CustomMethods.makeAndShowDialog(getContext(), "Sorry!", "Permissions are required to be granted in order to access photos.");
                }
                break;
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 3);
        }
    }
}
