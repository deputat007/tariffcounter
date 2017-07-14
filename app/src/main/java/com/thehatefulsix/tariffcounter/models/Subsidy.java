package com.thehatefulsix.tariffcounter.models;

import com.thehatefulsix.tariffcounter.models.core.IModelWithID;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Subsidy extends RealmObject implements IModelWithID<String> {

    @PrimaryKey
    @Required
    private String mId;
    private Date mDateGranted;
    private Date mDateFinished;
    private float mSocialNorm;
    private float mMandatoryFee;
    private float mOverpayment;

    public Subsidy() {}

    public String getId() {
        return mId;
    }
    public void setId(String mId) {
        this.mId = mId;
    }

    public Date getDateGranted() {
        return mDateGranted;
    }
    public void setDateGranted(Date dateGranted) {
        this.mDateGranted = dateGranted;
    }

    public Date getDateFinished() {
        return mDateFinished;
    }
    public void setDateFinished(Date dateFinished) {
        this.mDateFinished = dateFinished;
    }

    public float getOverpayment() {
        return mOverpayment;
    }
    public void setOverpayment(float overpayment) {
        this.mOverpayment = overpayment;
    }

    public float getSocialNorm() {
        return mSocialNorm;
    }
    public void setSocialNorm(float socialNorm) {
        this.mSocialNorm = socialNorm;
    }

    public float getMandatoryFee() {
        return mMandatoryFee;
    }
    public void setMandatoryFee(float mandatoryFee) {
        this.mMandatoryFee = mandatoryFee;
    }
}
