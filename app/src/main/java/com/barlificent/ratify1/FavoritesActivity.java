package com.barlificent.ratify1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.barlificent.ratify1.Adapters.UploadsAdapter;
import com.barlificent.ratify1.CustomClasses.Favorites;
import com.barlificent.ratify1.CustomClasses.Post;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends AppCompatActivity {

    @BindView(R.id.my_toolbar) Toolbar toolbarTop;
    @BindView(R.id.fav_list) ListView listView;
    Favorites favorites;
    UploadsAdapter adapter;
    String[] selected;
    int index = 0;
    ArrayList<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.bind(this);

        favorites = new Favorites(this);
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        favorites.addPost(new Post("",0,""));
        posts = favorites.getPosts();
        adapter = new UploadsAdapter(this,posts);
        listView.setAdapter(adapter);

        if(posts.size()>0)
            findViewById(R.id.fav_nothing).setVisibility(View.GONE);

        CustomMethods.setListViewHeightBasedOnItems(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fav_menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_fav:  /*   EDIT   */
                if(index == 0) {
                    adapter.setIndex(2);
                    listView.setAdapter(adapter);
                    item.setIcon(R.drawable.ic_delete_white_24dp);
                    findViewById(R.id.cancel_fav_lin).setVisibility(View.VISIBLE);
                    findViewById(R.id.cancel_fav_lin).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                             item.setIcon(R.drawable.ic_mode_edit_white_24dp);
                             index = 0;
                            adapter.setIndex(1);
                            listView.setAdapter(adapter);
                            findViewById(R.id.cancel_fav_lin).setVisibility(View.GONE);
                        }
                    });
//                    findViewById(R.id.cancel_fav).setVisibility(View.VISIBLE);
                    index = 1;
                }
                else{/*   DELETE   */
                    if(numberOfTrue(adapter.booleans) > 0) {
                        new AlertDialog.Builder(FavoritesActivity.this).setIcon(R.drawable.ic_delete_black_24dp)
                                .setTitle("Delete").setMessage("Are you sure you want to delete?")
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int j) {
                                        for (int i = 0;i<adapter.booleans.size();i++)
                                            if(adapter.booleans.get(i))
                                                favorites.deletePost(posts.get(i).getPhotoUrl());
                                        adapter.setIndex(1);
                                        posts = favorites.getPosts();
                                        adapter = new UploadsAdapter(getApplicationContext(),posts);
                                        listView.setAdapter(adapter);
                                        CustomMethods.setListViewHeightBasedOnItems(listView);
                                        if(posts.size() == 0) findViewById(R.id.fav_nothing).setVisibility(View.VISIBLE);
                                        item.setIcon(R.drawable.ic_mode_edit_white_24dp);
                                        findViewById(R.id.cancel_fav_lin).setVisibility(View.GONE);
                                    }
                                }).create().show();
                    }else Toast.makeText(getApplicationContext(),"Select items first",Toast.LENGTH_SHORT).show();
                    index = 0;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public int numberOfTrue(List<Boolean> k){
        int s=0;
        for (boolean b:k)s+=b?1:0;
        return s;
    }
}
