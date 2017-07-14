package com.thehatefulsix.tariffcounter.models;

import android.content.Context;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.core.IModelWithID;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class House extends RealmObject implements IModelWithID<String> {

    @PrimaryKey
    private String mId;
    private String mTitle;
    private Region mRegion;
    private City mCity;
    private String mStreet;
    private String mHouseNumber;
    private String mHouseBuilding;
    private String mApartment;
    private float mArea;
    private boolean mPrivateHouse;
    private RealmList<Service> mServices = new RealmList<>();
    private int mIcon;

    public House() {}

    public House(String title, float area) {
        mTitle = title;
        mArea = area;
    }

    @Override
    public String getId() {
        return mId;
    }
    @Override
    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    public Region getRegion() {
        return mRegion;
    }
    public void setRegion(Region region) {
        mRegion = region;
    }

    public City getCity() {
        return mCity;
    }
    public void setCity(City city) {
        this.mCity = city;
    }

    public String getStreet() {
        return mStreet;
    }
    public void setStreet(String street) {
        mStreet = street;
    }

    public String getHouseNumber() {
        return mHouseNumber;
    }
    public void setHouseNumber(String mHouse) {
        this.mHouseNumber = mHouse;
    }

    public String getHouseBuilding() {
        return mHouseBuilding;
    }
    public void setHouseBuilding(String houseBuilding) {
        mHouseBuilding = houseBuilding;
    }

    public String getApartment() {
        return mApartment;
    }
    public void setApartment(String apartment) {
        mApartment = apartment;
    }

    public float getArea() {
        return mArea;
    }
    public void setArea(float area) {
        mArea = area;
    }

    public boolean isPrivateHouse() {
        return mPrivateHouse;
    }
    public void setPrivateHouse(boolean isPrivateHouse) {
        this.mPrivateHouse = isPrivateHouse;
    }

    public List<Service> getServices() {
        return mServices;
    }

    public int getIcon() {
        return mIcon;
    }
    public void setIcon(int icon) {
        this.mIcon = icon;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId() + ".jpg";
    }


    public String getAddress(Context context){
        final StringBuilder address = new StringBuilder();
        if (getStreet() != null && !getStreet().isEmpty()){
            address.append(context.getResources().getString(R.string.street))
                    .append(" ")
                    .append(getStreet())
                    .append(" ");
        }
        if (getHouseNumber() != null && !getHouseNumber().isEmpty()){
            address.append(context.getResources().getString(R.string.house))
                    .append(" ")
                    .append(getHouseNumber())
                    .append(" ");
        }
        if (getApartment() != null && !getApartment().isEmpty()){
            address.append(context.getResources().getString(R.string.apartment))
                    .append(" ")
                    .append(getApartment());
        }

        return address.toString();
    }
}
