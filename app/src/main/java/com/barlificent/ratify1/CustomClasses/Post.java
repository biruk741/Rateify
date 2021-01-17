package com.barlificent.ratify1.CustomClasses;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by 2015 on 1/7/2018.
 */
public class Post {
    String uploader;
    long date;
    String photoUrl;
    String gender;
    public HashMap<String,Integer> rates = new HashMap<>();
    int rateCount = 0;
    int pointsCount = 0;
    String locale = "";

    public Post(){}

    public Post(String uploader, long date, String photoUrl) {
        this.uploader = uploader;
        this.date = date;
        this.photoUrl = photoUrl;
    }

    public Post(String photoUrl,long date){
        this.date =date;
        this.photoUrl = photoUrl;
    }

    public Post(String photoUrl, long date, int pointsCount) {
        this.photoUrl = photoUrl;
        this.date = date;
        this.pointsCount = pointsCount;
    }

    public HashMap<String, Integer> getRates() {
        return rates;
    }

    public void setRates(HashMap<String, Integer> rates) {
        this.rates = rates;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getRateCount() {
        return rateCount;
    }

    public void setRateCount(int rateCount) {
        this.rateCount = rateCount;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(int pointsCount) {
        this.pointsCount = pointsCount;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
