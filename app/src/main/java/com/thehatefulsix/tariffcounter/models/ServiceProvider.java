package com.thehatefulsix.tariffcounter.models;

import com.thehatefulsix.tariffcounter.models.core.IModelWithID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class ServiceProvider extends RealmObject implements IModelWithID<String>{

    @PrimaryKey
    @Required
    private String mId;
    private String mName;
    private String mPhone;
    private Rate mRate;
    private City mCity;
    private String mAddress;
    private String mWeb;
    private String mAccount;
    private String mProviderAccount;
    private String mMFO;

    public ServiceProvider() {}

    @Override
    public String getId() {
        return mId;
    }
    @Override
    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }
    public void setPhone(String phone) {
        mPhone = phone;
    }

    public City getCity() {
        return mCity;
    }
    public void setCity(City city) {
        mCity = city;
    }

    public Rate getRate() {
        return mRate;
    }
    public void setRate(Rate rate) {
        mRate = rate;
    }

    public String getAddress() {
        return mAddress;
    }
    public void setAddress(String address) {
        mAddress = address;
    }

    public String getWeb() {
        return mWeb;
    }
    public void setWeb(String Web) {
        this.mWeb = Web;
    }

    public String getAccount() {
        return mAccount;
    }
    public void setAccount(String account) {
        mAccount = account;
    }

    public String getProviderAccount() {
        return mProviderAccount;
    }
    public void setProviderAccount(String providerAccount) {
        this.mProviderAccount = providerAccount;
    }

    public String getMFO() {
        return mMFO;
    }
    public void setMFO(String MFO) {
        this.mMFO = MFO;
    }
}
