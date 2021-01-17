package com.barlificent.ratify1.CustomClasses;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 2015 on 1/20/2018.
 */

public class Favorites {
    public static final String TABLE_NAME = "favorites_table";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String postId;
    long timeStamp;
    Context context;

    public Favorites(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(TABLE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getJsonString() {
        return sharedPreferences.getString("favorites", "[]");
    }

    public void setJsonString(String s) {
        editor.putString("favorites", s);
        editor.apply();
    }

    public void addPost(Post post) {
        JSONArray jsonArray = new JSONArray(/*todo getJsonString*/);
        try {
            jsonArray = new JSONArray(getJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject currentPost = new JSONObject();
        try {
            currentPost.put("id", post.getPhotoUrl());
            currentPost.put("time", post.getDate());
            jsonArray.put(currentPost);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setJsonString(jsonArray.toString());
    }

    public ArrayList<Post> getPosts() {
        ArrayList<Post> posts = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(/*todo getJsonString*/);
        try {
            jsonArray = new JSONArray(getJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject current = jsonArray.getJSONObject(i);
                posts.add(new Post(current.getString("id"), current.getLong("time")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return posts;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void deletePost(String postId) {
        JSONArray jsonArray = new JSONArray(/*todo getJsonString*/);
        try {
            jsonArray = new JSONArray(getJsonString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("id").equals(postId)) {
                    jsonArray.remove(i);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setJsonString(jsonArray.toString());
    }
}
