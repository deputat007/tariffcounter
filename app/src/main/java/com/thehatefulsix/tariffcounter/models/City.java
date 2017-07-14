package com.thehatefulsix.tariffcounter.models;


import com.thehatefulsix.tariffcounter.models.core.IModelWithID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class City extends RealmObject implements IModelWithID<String>{

    @PrimaryKey
    @Required
    private String mId;
    private String mName;

    public City() {}

    public City(String id, String name) {
        mId = id;
        mName = name;
    }

    public City(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void setId(String id) {
        mId = id;
    }
}
