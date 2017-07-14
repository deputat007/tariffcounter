package com.thehatefulsix.tariffcounter.models;

import android.support.annotation.Nullable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WallPost extends RealmObject{
    @PrimaryKey
    private int mId;
    private int mOwnerId;
    private long mDate;
    private String mText;
    private String mLink;
    private String mPhotoUrl;

    public WallPost() {}

    public WallPost(int id, int ownerId, long date, String text) {
        mId = id;
        mOwnerId = ownerId;
        mDate = date;
        mText = text;
    }

    public WallPost(int id, int ownerId, long date, String text,
                    @Nullable String link, @Nullable String photoUrl) {
        mId = id;
        mOwnerId = ownerId;
        mDate = date;
        mText = text;
        mLink = link;
        mPhotoUrl = photoUrl;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(int ownerId) {
        mOwnerId = ownerId;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "WallPost{" +
                "mId=" + mId +
                ", mOwnerId=" + mOwnerId +
                ", mDate=" + mDate +
                ", mText='" + mText + '\'' +
                ", mLink='" + mLink + '\'' +
                ", mPhotoUrl='" + mPhotoUrl + '\'' +
                '}';
    }
}
