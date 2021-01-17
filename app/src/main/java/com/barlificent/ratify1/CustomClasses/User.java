package com.barlificent.ratify1.CustomClasses;

import android.util.Log;

/**
 * Created by 2015 on 1/6/2018.
 */
public class User {
    String name;
    String phoneNumber;
    int points;
    int rank;
    String userId;
    String locale = "";
    String bio = "";
    boolean canBeSentRequest;
    String gender;

    public User(){}

    public User(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    public User(String name, String phoneNumber,String userId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
    }

    public User(String name, int rank, int points, String phoneNumber) {
        this.name = name;
        this.rank = rank;
        this.points = points;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPoints() {
        Log.w("getPoints", points + "          <----|||||||||");
        return points;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean canBeSentRequest() {
        return canBeSentRequest;
    }

    public void setCanBeSentRequest(boolean canBeSentRequest) {
        this.canBeSentRequest = canBeSentRequest;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
