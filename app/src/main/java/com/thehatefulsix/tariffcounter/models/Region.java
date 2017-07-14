package com.thehatefulsix.tariffcounter.models;


import io.realm.RealmObject;


public class Region extends RealmObject {

    private String mName;

    public Region() {}

    public Region(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }
}
