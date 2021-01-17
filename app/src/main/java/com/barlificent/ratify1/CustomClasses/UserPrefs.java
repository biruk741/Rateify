package com.barlificent.ratify1.CustomClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 2015 on 1/6/2018.
 */
public class UserPrefs {
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    public String id;
    public String phoneNumber;
    int numberOfNotifications;
    boolean canPeopleSendRequests;
    String ppUrl;
    Context context;
    String requests = "[]";

    public static final String USER_PREFS = "userPrefs";


    public UserPrefs(String prefsName, Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(prefsName, 0);
        editor = sharedPreferences.edit();
    }
    public UserPrefs(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(USER_PREFS, 0);
        editor = sharedPreferences.edit();
    }


    public void setName(String name) {
        editor.putString("name", name);
        editor.commit();
    }

    public String getName() {
        return sharedPreferences.getString("name", "user");
    }

    public void setId(String id) {
        editor.putString("id", id);
        editor.commit();
    }

    public String getId() {
        String id = sharedPreferences.getString("id", "0000000000");
        Log.w("id", id);
        return id;
    }
    public void setLocale(String id) {
        editor.putString("locale", id);
        editor.commit();
    }

    public String getLocale() {
        return sharedPreferences.getString("locale", "ET");
    }

    public void setPhoneNumber(String id) {
        editor.putString("phoneNumber", id);
        editor.commit();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString("phoneNumber", "+555-5555");
    }

    public int getNumberOfNotifications() {
        int num = sharedPreferences.getInt("notifications", 0);
        Log.w("numberOfNotifications", num + "");
        return num;
    }

    public int setNumberOfNotifications(int numberOfNotifications) {
        int i = 0;
        ArrayList<Request> requests = getRequests();
        for(Request request : requests){
            if(!request.isAccepted) i++;
        }
        return i;
    }

    public String getPpUrl() {
        String num = sharedPreferences.getString("ppUrl", "");
        Log.w("ppUrl", num + "");
        return num;
    }

    public void setPpUrl(String ppUrl) {
        editor.putString("ppUrl", ppUrl);
        editor.commit();
    }

    public void addRequest(final String s, final boolean b) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray songs = new JSONArray(getRequestsString());
                    JSONObject song = new JSONObject();
                    song.put("id", s);
                    song.put("accepted",b);
                    songs.put(song);
                    editor.putString("requests", songs.toString());
                    editor.commit();
                    Log.w("requests",songs.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public String getRequestsString() {
        return sharedPreferences.getString("requests", "[]");
    }

    public ArrayList<Request> getRequests() {
        final ArrayList<Request> requests = new ArrayList<>();
        JSONArray requestsJson = new JSONArray();
        try {
            requestsJson = new JSONArray(getRequestsString());
            final JSONArray finalRequestsJson = requestsJson;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    for (int i = 0; i < finalRequestsJson.length(); i++) {
                        try {
                            JSONObject current = finalRequestsJson.getJSONObject(i);
                            requests.add(new Request(current.getString("id"), current.getBoolean("accepted")));
                        }
                        catch (Exception e){}
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception ignored) {
        }
        return requests;
    }
    public void addPost(final String s, final String b) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONArray posts = new JSONArray(getPostsString());
                    JSONObject song = new JSONObject();
                    song.put("id", s);
                    song.put("time",b);
                    posts.put(song);
                    editor.putString("posts", posts.toString());
                    editor.commit();
                    Log.w("requests",posts.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public String getPostsString() {
        return sharedPreferences.getString("posts", "[]");
    }

    public ArrayList<Post> getPosts() {
        final ArrayList<Post> posts = new ArrayList<>();
        JSONArray requestsJson = new JSONArray();
        try {
            requestsJson = new JSONArray(getPostsString());
            final JSONArray finalRequestsJson = requestsJson;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    for (int i = 0; i < finalRequestsJson.length(); i++) {
                        try {
                            JSONObject current = finalRequestsJson.getJSONObject(i);
                            posts.add(new Post(current.getString("id"),current.getLong("time"),""));
                        }
                        catch (Exception e){}
                    }
                    return null;
                }
            }.execute();
        }
        catch (Exception ignored) {
        }
        return posts;
    }

    public void setBio(String bio) {
        editor.putString("bio", bio);
        editor.commit();
    }
    public String getBio(){
        return sharedPreferences.getString("bio","");
    }
    public void setGender(String gender) {
        editor.putString("gender", gender);
        editor.commit();
    }
    public String getGender(){
        return sharedPreferences.getString("gender","male");
    }
    public void setPreference(String preference) {
        editor.putString("preference", preference);
        editor.commit();
    }
    public String getPreference(){
        return sharedPreferences.getString("preference","both");
    }

    public boolean peopleCanSendRequests() {
        return sharedPreferences.getBoolean("peopleCanSendRequests",true);
    }

    public void setPeopleCanSendRequests(boolean canPeopleSendRequests) {
        editor.putBoolean("peopleCanSendRequests", canPeopleSendRequests);
        editor.commit();
    }
}