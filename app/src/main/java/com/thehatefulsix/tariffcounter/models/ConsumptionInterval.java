package com.thehatefulsix.tariffcounter.models;

import io.realm.RealmObject;


public class ConsumptionInterval extends RealmObject{

    private int mIntervalFrom;
    private int mIntervalTo;
    private float mPrice;

    public ConsumptionInterval() {}

    ConsumptionInterval(int intervalFrom, int intervalTo, int price) {
        this.mIntervalFrom = intervalFrom;
        this.mIntervalTo = intervalTo;
        this.mPrice = price;
    }

    public int getIntervalFrom() {
        return mIntervalFrom;
    }

    public int getIntervalTo() {
        return mIntervalTo;
    }

    public float getPrice() {
        return mPrice;
    }

    public void setIntervalFrom(int intervalFrom) {
        mIntervalFrom = intervalFrom;
    }

    public void setIntervalTo(int intervalTo) {
        mIntervalTo = intervalTo;
    }

    public void setPrice(float price) {
        mPrice = price;
    }

    @Override
    public String toString() {
        return "ConsumptionInterval{" +
                "mIntervalFrom=" + mIntervalFrom +
                ", mIntervalTo=" + mIntervalTo +
                ", mPrice=" + mPrice +
                '}';
    }
}
