package com.thehatefulsix.tariffcounter.models;

import com.thehatefulsix.tariffcounter.models.core.IModelWithID;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Bill extends RealmObject implements IModelWithID<String> {

    @PrimaryKey
    private String mId;
    private Service mService;
    private String mRateId;
    private long mPeriodStart;
    private long mPeriodEnd;
    private int mPreviousMark;
    private int mCurrentMark;
    private boolean mIsPaid;
    private long mPaymentTime;
    private String mPathToPhoto;
    private String mPathToPDF;
    private float mSum;

    public Bill(){}

    public Bill(Service service, String rateId, long periodStart,
                long periodEnd, int previousMark, int currentMark, boolean isPaid,
                long paymentTime) {
        mService = service;
        mRateId = rateId;
        mPeriodStart = periodStart;
        mPeriodEnd = periodEnd;
        mPreviousMark = previousMark;
        mCurrentMark = currentMark;
        mIsPaid = isPaid;
        mPaymentTime = paymentTime;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void setId(String id) {
        mId = id;
    }

    public long getPeriodStart() {
        return mPeriodStart;
    }

    public void setPeriodStart(long periodStart) {
        mPeriodStart = periodStart;
    }

    public long getPeriodEnd() {
        return mPeriodEnd;
    }

    public void setPeriodEnd(long periodEnd) {
        mPeriodEnd = periodEnd;
    }

    public int getPreviousMark() {
        return mPreviousMark;
    }

    public void setPreviousMark(int previousMark) {
        mPreviousMark = previousMark;
    }

    public int getCurrentMark() {
        return mCurrentMark;
    }

    public void setCurrentMark(int currentMark) {
        mCurrentMark = currentMark;
    }

    public boolean isPaid() {
        return mIsPaid;
    }

    public void setPaid(boolean paid) {
        mIsPaid = paid;
    }

    public long getPaymentTime() {
        return mPaymentTime;
    }

    public void setPaymentTime(long paymentTime) {
        mPaymentTime = paymentTime;
    }

    public String getPathToPhoto() {
        return mPathToPhoto;
    }

    public void setPathToPhoto(String pathToPhoto) {
        mPathToPhoto = pathToPhoto;
    }

    public String getPathToPDF() {
        return mPathToPDF;
    }

    public void setPathToPDF(String pathToPDF) {
        mPathToPDF = pathToPDF;
    }

    public float getSum() {
        return mSum;
    }

    public void setSum(float sum) {
        mSum = sum;
    }

    public Service getService() {
        return mService;
    }

    public void setService(Service service) {
        mService = service;
    }

    public String getRateId() {
        return mRateId;
    }

    public void setRateId(String rateId) {
        mRateId = rateId;
    }


    public static float calculateSum(Service service, long periodStart, long periodEnd,
                                     boolean hasMeter, int currentMark, int previousMark,
                                     boolean hasSubsidy){
        float sum = 0.0f;

        final Rate rate = service.getRate();
        final Subsidy subsidy = service.getSubsidy();

        final float fixedPayment = rate.getFixedPayment();
        final Rate.Period period = rate.getPeriod();
        final int discount = rate.getDiscount();
        final float extraPayment = rate.getExtraPayment();

        final GregorianCalendar dateStart = new GregorianCalendar();
        final GregorianCalendar dateEnd = new GregorianCalendar();

        dateStart.setTimeInMillis(periodStart);
        dateEnd.setTimeInMillis(periodEnd);

        if (fixedPayment != 0.0 || extraPayment != 0.0) {
            switch (period) {
                case DAY:
                    while (dateStart.before(dateEnd) || dateStart.equals(dateEnd)) {
                        if (hasSubsidy){
                            sum += subsidy.getMandatoryFee();
                        }else {
                            sum += fixedPayment + extraPayment;
                        }
                        dateStart.add(Calendar.DAY_OF_WEEK, 1);
                    }

                    break;
                case WEEK:
                    while (dateStart.before(dateEnd) || dateStart.equals(dateEnd)) {
                        if (dateStart.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                            if (hasSubsidy){
                                sum += subsidy.getMandatoryFee();
                            }else {
                                sum += fixedPayment + extraPayment;
                            }
                        }

                        dateStart.add(Calendar.DAY_OF_WEEK, 1);
                    }

                    break;
                case MONTH:
                    while (dateStart.before(dateEnd)) {
                        if (hasSubsidy){
                            sum += subsidy.getMandatoryFee();
                        }else {
                            sum += fixedPayment + extraPayment;
                        }
                        dateStart.add(Calendar.MONTH, 1);
                    }

                    break;
            }
        }

        if (hasMeter){
            int consumedAmount = currentMark - previousMark;

            if (hasSubsidy){
                sum += subsidy.getMandatoryFee();

                if (consumedAmount < subsidy.getSocialNorm()){
                    consumedAmount = 0;
                }else {
                    consumedAmount -= subsidy.getSocialNorm();
                }
            }

            if (consumedAmount != 0){
                for (ConsumptionInterval consumptionInterval : rate.getRates()){
                    if (consumedAmount < consumptionInterval.getIntervalTo()  ||
                            consumptionInterval.getIntervalTo() == 0){
                        sum += (consumedAmount -
                                consumptionInterval.getIntervalFrom() + 1) *
                                consumptionInterval.getPrice();

                        break;
                    } else {
                        sum += (consumptionInterval.getIntervalTo() -
                                consumptionInterval.getIntervalFrom() + 1) *
                                consumptionInterval.getPrice();
                    }
                }
            }
        }

        sum -= sum * (discount / 100f);

        return sum;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "mId='" + mId + '\'' +
                ", mService=" + mService +
                ", mRateId='" + mRateId + '\'' +
                ", mPeriodStart=" + mPeriodStart +
                ", mPeriodEnd=" + mPeriodEnd +
                ", mPreviousMark=" + mPreviousMark +
                ", mCurrentMark=" + mCurrentMark +
                ", mIsPaid=" + mIsPaid +
                ", mPaymentTime=" + mPaymentTime +
                ", mPathToPhoto='" + mPathToPhoto + '\'' +
                ", mPathToPDF='" + mPathToPDF + '\'' +
                ", mSum=" + mSum +
                '}';
    }
}
