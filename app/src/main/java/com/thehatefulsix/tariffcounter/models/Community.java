package com.thehatefulsix.tariffcounter.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Community extends RealmObject {
    @PrimaryKey
    private int mId;
    private String mName;
    private String mIconPath;
    private RealmList<WallPost> mWallPosts = new RealmList<>();

    public Community() {}

    public Community(int id, String name, String iconPath) {
        mId = id;
        mName = name;
        mIconPath = iconPath;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getIconPath() {
        return mIconPath;
    }

    public void setIconPath(String iconPath) {
        mIconPath = iconPath;
    }

    public RealmList<WallPost> getWallPosts() {
        return mWallPosts;
    }

    public void setWallPosts(RealmList<WallPost> wallPosts) {
        mWallPosts = wallPosts;
    }

    @Override
    public String toString() {
        return "Community{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mIconPath='" + mIconPath + '\'' +
                ", mWallPosts=" + mWallPosts +
                '}';
    }
}
