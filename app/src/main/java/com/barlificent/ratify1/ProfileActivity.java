package com.barlificent.ratify1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.barlificent.ratify1.Adapters.NonSwipeableViewPager;
import com.barlificent.ratify1.CustomClasses.FontTextView;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.barlificent.ratify1.Fragments.ProfileFragments.ProfileDetailsFragment;
import com.barlificent.ratify1.Fragments.ProfileFragments.ProfileSettingsFragment;
import com.barlificent.ratify1.Fragments.ProfileFragments.ProfilePhotosFragment;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    FontTextView name;
    CircleImageView pp;
    ProgressDialog progressDialog;

    DatabaseReference users;
    UserPrefs userPrefs;
    StorageReference postImages;
    LruCache<String, Bitmap> mMemoryCache;
    NonSwipeableViewPager viewPager;
    TabLayout tabLayout;
    AHBottomNavigation ahBottom;
    RelativeLayout linearLayout;
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("");
        prepareCache();
        postImages = FirebaseStorage.getInstance().getReferenceFromUrl("gs://ratify1-c299d.appspot.com");


        final Activity activity = this;
        userPrefs = new UserPrefs(UserPrefs.USER_PREFS, this);
        users = FirebaseDatabase.getInstance().getReference("users");

        linearLayout = (RelativeLayout) findViewById(R.id.profile_linear);
        root = (RelativeLayout) findViewById(R.id.root_profile);
        name = (FontTextView) findViewById(R.id.profile_name);
        pp = (CircleImageView) findViewById(R.id.profile_image);
        viewPager = (NonSwipeableViewPager) findViewById(R.id.profile_viewpager);


        final ViewTreeObserver observer = root.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, root.getHeight()));
                    }
                });
        ahBottom = (AHBottomNavigation) findViewById(R.id.ahbottomNav);

//        getCurrentUserFromDatabase();

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbarTop);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpNavigationIcons();
        setupViewPager(viewPager);
        ahBottom.setCurrentItem(1, false);
        viewPager.setCurrentItem(1, false);

        ahBottom.setDefaultBackgroundColor(getResources().getColor(R.color.trans_white));
        ahBottom.setAccentColor(getResources().getColor(R.color.colorAccent));
        ahBottom.setInactiveColor(getResources().getColor(R.color.trans_black_dark));
        ahBottom.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);

        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_profile);
        final boolean[] isPink = {false};



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                    // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
//                    View viewd = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
//                    int diff = (viewd.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
//                    if (diff == 0 && !isPink[0]) {
//                        ahBottom.setDefaultBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                        isPink[0] = true;
//                    } else if(diff != 0 && isPink[0]) {
//                        ahBottom.setDefaultBackgroundColor(getResources().getColor(R.color.colorAccent));
//                        isPink[0] = false;
//                    }
//                }
//            });
//        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        },1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.profile_change_name) {
            changeName();
            Log.w("ChangeName", "Clicked.");
            return true;
        } else if (id == R.id.profile_change_pic) {
            CustomMethods.selectImage(ProfileActivity.this, 1);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                byte[] bytes = outputStream.toByteArray();

                UploadTask uploadTask = postImages.child(userPrefs.getId()).putBytes(bytes);

                progressDialog = ProgressDialog.show(ProfileActivity.this, "", "Uploading...", true, false);
                progressDialog.show();
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.hide();
                        users.child(userPrefs.getId()).child("image_id").setValue(userPrefs.getId());
                        setPPForCache(bitmap);
                        userPrefs.setPpUrl(userPrefs.getId());
                    }
                });

//                pp.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getCurrentUserFromDatabase() {
        users.child(userPrefs.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                String _name = user != null ? user.getName() : "";
//                String _rank = (user != null ? user.getRank() : 0) + "";
//                String _points = (user != null ? user.getPoints() : 0) + "";


                Log.w("Firebase", "Retrieved data.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                finish();
            }
        });
    }

    public void prepareCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public Bitmap loadPPFromCache() {
        return mMemoryCache.get("pp");
    }

    public void setPPForCache(Bitmap bitmap) {
        mMemoryCache.put("pp", bitmap);
    }

    public void changeName() {
        View view = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_enter_name, null);
        final EditText field = (EditText) view.findViewById(R.id.dialogField);
        field.setText(userPrefs.getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Name");
        builder.setView(view);
        builder.setCancelable(false)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        userPrefs.setName(field.getText().toString());
                        users.child(userPrefs.getId()).child("name").setValue(field.getText().toString());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_photo_library_black_36dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_account_circle_black_36dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_notifications_black_24dp);
    }

    public void setUpNavigationIcons() {
        AHBottomNavigationItem[] items = new AHBottomNavigationItem[]{
                new AHBottomNavigationItem("Photos", R.drawable.ic_photo_library_black_36dp),
                new AHBottomNavigationItem("Profile", R.drawable.ic_account_circle_black_24dp),
                new AHBottomNavigationItem("Settings", R.drawable.ic_settings_white_36dp),
        };
        for (AHBottomNavigationItem item : items) {
            item.setColor(getResources().getColor(R.color.colorAccent));
            ahBottom.addItem(item);
        }
        ahBottom.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                ahBottom.setCurrentItem(position, false);
                viewPager.setCurrentItem(position, false);
                return false;
            }
        });
        ahBottom.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfilePhotosFragment(), "Photos");
        adapter.addFragment(new ProfileDetailsFragment(), "Details");
        adapter.addFragment(new ProfileSettingsFragment(), "Notifications");
//        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
