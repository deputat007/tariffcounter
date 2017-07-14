package com.thehatefulsix.tariffcounter.models;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.core.IModelWithID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Service extends RealmObject implements IModelWithID<String>{

    public enum Type {
        OTHER(R.string.service_type_other),
        ELECTRICITY_SUPPLY(R.string.service_type_electricity_supply),
        GAS_SUPPLY(R.string.service_type_gas_supply),
        RENT(R.string.service_type_rent),
        HEATING(R.string.service_type_heating),
        HOT_WATER(R.string.service_type_hot_water),
        WATER_SUPPLY(R.string.service_type_water_supply),
        WATER_DRAINAGE(R.string.service_type_water_drainage),
        GARBAGE_COLLECTION(R.string.service_type_garbage_collection);

        @StringRes int mStringRes;

        Type(@StringRes int stringRes){
            mStringRes = stringRes;
        }

        @NonNull
        public String getName(@NonNull Context context){
            return context.getString(mStringRes);
        }

        @StringRes
        public int getStringRes(){
            return mStringRes;
        }
    }

    public enum RateUnit {
        NULL(R.string.rate_unit_absent),
        M2(R.string.rate_unit_m2),
        M3(R.string.rate_unit_m3),
        KWH(R.string.rate_unit_kW_h),
        GCAL(R.string.rate_unit_Gcal);

        @StringRes int mStringRes;

        RateUnit(@StringRes int stringRes){
            mStringRes = stringRes;
        }

        @NonNull
        public String getName(@NonNull Context context){
            return context.getString(mStringRes);
        }

        @StringRes
        public int getStringRes(){
            return mStringRes;
        }
    }

    @PrimaryKey
    @Required
    private String mId;
    private String mIdHouse;
    private String mName;
    private String mType;
    private ServiceProvider mServiceProvider;
    private String mRateUnit;
    private float mBalance;
    private float mCounter;
    private Rate mRate;
    private Subsidy mSubsidy;
    private boolean mIsSubsidy;
    private int mIcon;

    public Service() {}

    public Service(String idHouse, String name,
                   ServiceProvider serviceProvider,
                   long balance, Rate rate, int icon) {
        this.mIdHouse = idHouse;
        this.mName = name;
        this.mServiceProvider = serviceProvider;
        this.mBalance = balance;
        this.mRate = rate;
        this.mIcon = icon;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void setId(String id) {
        mId = id;
    }

    public void setIdHouse(String idHouse) {
        this.mIdHouse = idHouse;
    }

    public Type getType() {
        if(mType != null){
            return Type.valueOf(mType);
        } else {
            return null;
        }
    }
    public void setType(Type type) {
        this.mType = type.toString();
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    public ServiceProvider getServiceProvider() {
        return mServiceProvider;
    }
    public void setServiceProvider(ServiceProvider serviceProvider) {
        mServiceProvider = serviceProvider;
    }

    public float getBalance() {
        return mBalance;
    }
    public void setBalance(float balance) {
        mBalance = balance;
    }

    public float getCounter() {
        return mCounter;
    }
    public void setCounter(float counter) {
        this.mCounter = counter;
    }

    public Rate getRate() {
        return mRate;
    }
    public void setRate(Rate rate) {
        mRate = rate;
    }

    public Subsidy getSubsidy() {
        return mSubsidy;
    }
    public void setSubsidy(Subsidy subsidy) {
        mSubsidy = subsidy;
    }

    public boolean isSubsidy() {
        return mIsSubsidy;
    }
    public void setIsSubsidy(boolean isSubsidy) {
        this.mIsSubsidy = isSubsidy;
    }

    public int getIcon() {
        return mIcon;
    }
    public void setIcon(int icon) {
        mIcon = icon;
    }

    public RateUnit getRateUnit() {
        if(mRateUnit != null) {
            return RateUnit.valueOf(mRateUnit);
        } else {
            return null;
        }
    }
    public void setRateUnit(RateUnit unit) {
        this.mRateUnit = unit.toString();
    }

    @Override
    public String toString() {
        return "Service{" +
                "mId='" + mId + '\'' +
                ", mIdHouse='" + mIdHouse + '\'' +
                ", mName='" + mName + '\'' +
                ", mType='" + mType + '\'' +
                ", mServiceProvider=" + mServiceProvider +
                ", mRateUnit='" + mRateUnit + '\'' +
                ", mBalance=" + mBalance +
                ", mCounter=" + mCounter +
                ", mRate=" + mRate +
                ", mSubsidy=" + mSubsidy +
                ", mIsSubsidy=" + mIsSubsidy +
                ", mIcon=" + mIcon +
                '}';
    }
}
