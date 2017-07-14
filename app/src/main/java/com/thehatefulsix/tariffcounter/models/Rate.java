package com.thehatefulsix.tariffcounter.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.core.IModelWithID;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Rate extends RealmObject implements IModelWithID<String>{

    public enum Period {
        DAY(R.string.day),
        WEEK(R.string.week),
        MONTH(R.string.month);

        @StringRes
        int mStringRes;

        Period(@StringRes int stringRes){
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
    private float mFixedPayment;
    private float mExtraPayment;
    private int mDiscount;
    private RealmList<ConsumptionInterval> mRates = new RealmList<>();
    private String mPeriod;
    private int mAmountOfPayments;
    private Date mUpdateDate;
    private boolean mHasRate;
    private boolean mIsRateFromDb;

    public Rate() {}

    public Rate(int fixedPayment, int amountOfPayments, boolean hasRate,
                RealmList<ConsumptionInterval> rates,
                Period period, boolean isRateFromDb) {
        this.mFixedPayment = fixedPayment;
        this.mHasRate = hasRate;
        this.mRates = rates;
        this.mAmountOfPayments = amountOfPayments;
        this.mPeriod = period.toString();
        this.mIsRateFromDb = isRateFromDb;
    }

    public Rate(int fixedPayment, int extraPayment, int discount, int amountOfPayments,
                Period period, boolean hasRate) {
        mFixedPayment = fixedPayment;
        mExtraPayment = extraPayment;
        mDiscount = discount;
        mAmountOfPayments = amountOfPayments;
        mPeriod = period.toString();
        mHasRate = hasRate;
    }

    @Override
    public String getId() {
        return mId;
    }
    @Override
    public void setId(String id){
        mId = id;
    }

    public void setFixedPayment(float fixedPayment) {
        this.mFixedPayment = fixedPayment;
    }

    public float getExtraPayment() {
        return mExtraPayment;
    }

    public void setExtraPayment(float extraPayment) {
        mExtraPayment = extraPayment;
    }

    public int getDiscount() {
        return mDiscount;
    }

    public void setDiscount(int discount) {
        mDiscount = discount;
    }

    public Period getPeriod() {
        return Period.valueOf(mPeriod);
    }

    public String getPeriodString() {
        return mPeriod;
    }
    public void setPeriod(Period period) {
        this.mPeriod = period.toString();
    }

    public float getFixedPayment() {
        return mFixedPayment;
    }

    public boolean isRateFromDb() {
        return mIsRateFromDb;
    }
    public void setRateFromDb(boolean rateFromDb) {
        mIsRateFromDb = rateFromDb;
    }

    public boolean isHasRate() {
        return mHasRate;
    }
    public void setHasRate(boolean hasRate) {
        mHasRate = hasRate;
    }

    public RealmList<ConsumptionInterval> getRates() {
        return mRates;
    }

    public int getAmountOfPayments() {
        return mAmountOfPayments;
    }
    public void setAmountOfPayments(int amountOfPayments) {
        this.mAmountOfPayments = amountOfPayments;
    }

    public Date getUpdateDate() {
        return mUpdateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.mUpdateDate = updateDate;
    }

    public String getInformation(Context context){
        final StringBuilder detailInformation = new StringBuilder();

        final String currency = "";
        final String from = context.getString(R.string.from) + " ";
        final String to = context.getString(R.string.to) + " ";

        if (isHasRate()){
            if (!getRates().isEmpty()) {
                if (getRates().size() == 1) {
                    final float price = getRates().get(0).getPrice();

                    detailInformation.append(String.valueOf(price)).append(currency);

                }else {
                    final StringBuilder tariff = new StringBuilder();

                    for (ConsumptionInterval consumptionInterval :
                            getRates()) {
                        float price = consumptionInterval.getPrice();

                        if (consumptionInterval.getIntervalTo() == 0 &&
                                consumptionInterval.getIntervalFrom() != 0){
                            tariff.append("\n")
                                    .append(from)
                                    .append(consumptionInterval.getIntervalFrom())
                                    .append(" - ")
                                    .append(price)
                                    .append(currency);
                        }

                        if (consumptionInterval.getIntervalFrom() == 0 &&
                                consumptionInterval.getIntervalTo() != 0){
                            tariff.append("\n")
                                    .append(to)
                                    .append(consumptionInterval.getIntervalTo())
                                    .append(" - ")
                                    .append(price)
                                    .append(currency);
                        }

                        if (consumptionInterval.getIntervalFrom() != 0 &&
                                consumptionInterval.getIntervalTo() != 0){
                            tariff.append("\n")
                                    .append(from)
                                    .append(consumptionInterval.getIntervalFrom())
                                    .append(" ")
                                    .append(to)
                                    .append(consumptionInterval.getIntervalTo())
                                    .append(" - ")
                                    .append(price)
                                    .append(currency);
                        }
                    }

                    detailInformation.append(tariff);
                }
            }
        }

        if (mFixedPayment != 0){
            detailInformation.append(mFixedPayment).append(currency);
        }

        return detailInformation.toString();
    }

    @Override
    public String toString() {
        return "Rate{" +
                "mId='" + mId + '\'' +
                ", mFixedPayment=" + mFixedPayment +
                ", mExtraPayment=" + mExtraPayment +
                ", mDiscount=" + mDiscount +
                ", mRates=" + mRates +
                ", mPeriod='" + mPeriod + '\'' +
                ", mAmountOfPayments=" + mAmountOfPayments +
                ", mUpdateDate=" + mUpdateDate +
                ", mHasRate=" + mHasRate +
                ", mIsRateFromDb=" + mIsRateFromDb +
                '}';
    }
}