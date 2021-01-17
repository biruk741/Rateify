package com.barlificent.ratify1.CustomClasses;

/**
 * Created by 2015 on 1/7/2018.
 */
public class Request {
    String name;
    public String id;
    public boolean isAccepted;

    public Request(){}
    public Request(String id, boolean isAccepted) {
        this.id = id;
        this.isAccepted = isAccepted;
    }

    public Request(String name, String id, boolean isAccepted) {
        this.name = name;
        this.id = id;
        this.isAccepted = isAccepted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
