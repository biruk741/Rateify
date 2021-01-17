package com.barlificent.ratify1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.barlificent.ratify1.Adapters.NonSwipeableViewPager;
import com.barlificent.ratify1.Adapters.ViewPagerAdapter;
import com.barlificent.ratify1.CustomClasses.Favorites;
import com.barlificent.ratify1.CustomClasses.FontTextView;
import com.barlificent.ratify1.CustomClasses.Request;
import com.barlificent.ratify1.CustomClasses.UserPrefs;
import com.barlificent.ratify1.Fragments.HomeFragments.FriendsFragment;
import com.barlificent.ratify1.Fragments.HomeFragments.LeaderBoardFragment;
import com.barlificent.ratify1.Fragments.HomeFragments.PhotosFragment;
import com.barlificent.ratify1.Fragments.HomeFragments.RateFragment;
import com.barlificent.ratify1.Fragments.HomeFragments.UploadFragment;
import com.barlificent.ratify1.Fragments.ProfileFragments.ProfileDetailsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import java.util.Map;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AHBottomNavigation bottomNavigation;
    FontTextView navName;
    TextView navPhone;

    NonSwipeableViewPager mainViewPager;
    BottomNavigation nav;
    DatabaseReference users;
    UserPrefs userPrefs;
    FrameLayout root;
    ImageView hamburger;
    private DatabaseReference posts;
    View guillotineMenu;

    int guilo = 0; // closed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String calString = "-1 + 20 +3+ -24+5";
        Log.w("add",calString +" = \n---->" + ProfileDetailsFragment.add(calString));
        userPrefs = new UserPrefs(UserPrefs.USER_PREFS, this);
        users = FirebaseDatabase.getInstance().getReference("users");
        posts = FirebaseDatabase.getInstance().getReference("uploads");
        root = (FrameLayout) findViewById(R.id.root);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        hamburger = (ImageView) findViewById(R.id.content_hamburger);
        navName = (FontTextView) findViewById(R.id.nav_name);
        navPhone = (TextView) findViewById(R.id.nav_phone);
        mainViewPager = (NonSwipeableViewPager) findViewById(R.id.main_viewpager);
        nav = (BottomNavigation) findViewById(R.id.BottomNavigation);
        users = FirebaseDatabase.getInstance().getReference("users");


        Toolbar toolbarTop = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbarTop);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
        setTitle("");

        // todo setup toolbar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbarTop, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        navigationView.setNavigationItemSelectedListener(this);//todo fab, navigation menu...

//        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//            startActivity(new Intent(this, LoginActivity.class));
//        }
        bottomNavigation.setTitleTypeface(Typeface.createFromAsset(getAssets(), "fonts/ubuntu.ttf"));
        guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger),hamburger)
                .setStartDelay(0)
                .setActionBarViewForAnimation(toolbarTop)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {
                    @Override
                    public void onGuillotineOpened() {
                        guilo = 1;
                    }

                    @Override
                    public void onGuillotineClosed() {
                        guilo = 0;
                    }
                })
                .build();

        TextView mTitles = (TextView) root.findViewById(R.id.toolbar_title_guil);
        mTitles.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));

        navName = (FontTextView) root.findViewById(R.id.nav_name_new);
        navPhone = (FontTextView) root.findViewById(R.id.nav_number_new);
        navName.setText(userPrefs.getName());
        navPhone.setText(userPrefs.getPhoneNumber());

        CustomMethods.loadPP(this,userPrefs.getId(),(ImageView) findViewById(R.id.nav_pp_new));

        findViewById(R.id.profile_nav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });


        nav.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int i, int i1, boolean b) {
                mainViewPager.setCurrentItem(i1);
            }

            @Override
            public void onMenuItemReselect(int i, int i1, boolean b) {

            }
        });

        root.findViewById(R.id.closeButtonNav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guillotineMenu.findViewById(R.id.guillotine_hamburger).performClick();
            }
        });

        setUpNavigationIcons();
        setupViewPager(mainViewPager);
        listenForRequests();
        setMenuListeners();
        mainViewPager.setCurrentItem(2);
    }

    @Override
    public void onBackPressed() {
        final boolean[] pressed = {false};
        if (guilo == 0) {
//            System.exit(0);
            finish();
//            super.onBackPressed();
        } else {
            guillotineMenu.findViewById(R.id.guillotine_hamburger).performClick();
            guilo = 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            // Handle the camera action
        } else if (id == R.id.nav_settings) {
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpToolbar() {
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbarTop);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
        setTitle("");
    }

    public void setUpNavigationIcons() {
        AHBottomNavigationItem[] items = new AHBottomNavigationItem[]{
                new AHBottomNavigationItem("FRIENDS", R.drawable.ic_people_black_36dp),
                new AHBottomNavigationItem("MESSAGES", R.drawable.ic_chat_black_24dp),
                new AHBottomNavigationItem("RATE", R.mipmap.rate_black),
                new AHBottomNavigationItem("UPLOAD", R.drawable.ic_camera_white_48dp),
                new AHBottomNavigationItem("LEADERBOARD", R.mipmap.leaderboard)
        };
        for (AHBottomNavigationItem item : items) {
            item.setColor(Color.parseColor("#e91e63"));
            bottomNavigation.addItem(item);
        }
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        bottomNavigation.setDefaultBackgroundColor(-1);
        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorAccent));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.trans_black));
        createNotification();
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                bottomNavigation.setCurrentItem(position, false);
                mainViewPager.setCurrentItem(position,true);
                Log.w("position",position + "");
                return false;
            }
        });
                bottomNavigation.setCurrentItem(2);

    }

    private void createNotification() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AHNotification notification = new AHNotification.Builder()
                        .setText(userPrefs.getNumberOfNotifications() + "")
                        .setBackgroundColor(getResources().getColor(R.color.colorAccent))
                        .setTextColor(Color.WHITE)
                        .build();
                // Adding notification to last item.
                if(userPrefs.getNumberOfNotifications() != 0)bottomNavigation.setNotification(notification, 4);
            }
        }, 1000);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendsFragment(), "Home");
        adapter.addFragment(new PhotosFragment(), "Photos");
        adapter.addFragment(new RateFragment(), "Upload");
        adapter.addFragment(new UploadFragment(), "People");
        adapter.addFragment(new LeaderBoardFragment(), "Notifications");
//        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setAdapter(adapter);
    }
    public static void write(String id,String key, Object value){
        FirebaseDatabase.getInstance().getReference("users").child(id).child(key).setValue(value);
        Log.w("write","run");
    }
    public static void writeArray(String id,String key, Map<String,Boolean> value){
        FirebaseDatabase.getInstance().getReference("users").child(id).child(key).setValue(value);
        Log.w("write","run");
    }
    public void listenForRequests(){
        users.child(userPrefs.getId()).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Request request = dataSnapshot.getValue(Request.class);
                userPrefs.addRequest(request != null ? request.id : null, request != null && request.isAccepted);
                createNotification();
                Log.w("listenForRequests", "Request has arrived");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setMenuListeners(){
        final Class[] activities = {ProfileActivity.class,SettingsActivity.class, FavoritesActivity.class,ProfileActivity.class};
        int[] menuButtons = {R.id.profile_nav,R.id.settings_nav,R.id.fav_nav,R.id.share_nav};
        for(int i = 0;i<4;i++){
            final int finalI = i;
            findViewById(menuButtons[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    guillotineMenu.findViewById(R.id.guillotine_hamburger).performClick();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(),activities[finalI]));
                        }
                    },500);
                }
            });
        }
    }
}
