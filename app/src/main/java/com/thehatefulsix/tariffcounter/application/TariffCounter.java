package com.thehatefulsix.tariffcounter.application;

import android.app.Application;

import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;
import com.vk.sdk.VKSdk;

import butterknife.BuildConfig;
import butterknife.ButterKnife;


public class TariffCounter extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ButterKnife.setDebug(BuildConfig.DEBUG);
        RealmHelper.init(this);
        SharedPreferenceHelper.init(this);
        VKSdk.initialize(this);
    }
}
