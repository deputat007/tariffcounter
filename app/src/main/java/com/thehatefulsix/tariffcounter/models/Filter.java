package com.thehatefulsix.tariffcounter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;

public class Filter implements Parcelable{
    private String mTitle;
    private boolean isChecked;
    private SharedPreferenceHelper.Key mKey;

    public Filter() {}

    private Filter(Parcel in) {
        mTitle = in.readString();
        isChecked = in.readByte() != 0;
        mKey = SharedPreferenceHelper.Key.valueOf(in.readString());
    }

    public static final Creator<Filter> CREATOR = new Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public SharedPreferenceHelper.Key getKey() {
        return mKey;
    }

    public void setKey(SharedPreferenceHelper.Key key) {
        mKey = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeString(getKey().toString());
    }

    @Override
    public String toString() {
        return "Filter{" +
                "mTitle='" + mTitle + '\'' +
                ", isChecked=" + isChecked +
                ", mKey=" + mKey.getKey() +
                '}';
    }
}
