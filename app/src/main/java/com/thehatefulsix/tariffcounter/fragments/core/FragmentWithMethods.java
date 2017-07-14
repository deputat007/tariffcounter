package com.thehatefulsix.tariffcounter.fragments.core;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class FragmentWithMethods extends Fragment {

    private RealmHelper mRealmHelper;
    private SharedPreferenceHelper mSharedPreferenceHelper;
    private Unbinder mUnbinder;

    @LayoutRes protected abstract int contentView();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(contentView(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mUnbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUnbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mRealmHelper != null) {
            mRealmHelper.initRealm();
        }
    }

    public void setTitle(@StringRes int title) {
        if (getActivity() != null){
            getActivity().setTitle(title);
        }
    }

    public void setTitle(String title) {
        if (getActivity() != null){
            getActivity().setTitle(title);
        }
    }

    protected RealmHelper getRealmHelper() {
        if (mRealmHelper == null){
            mRealmHelper = RealmHelper.getInstance();
        }
        return mRealmHelper;
    }

    protected SharedPreferenceHelper getSharedPreferenceHelper() {
        if (mSharedPreferenceHelper == null){
            mSharedPreferenceHelper = SharedPreferenceHelper.getInstance();
        }
        return mSharedPreferenceHelper;
    }

    protected House getCurrentHouse() {
        return getRealmHelper().getObjectById(House.class,
                getSharedPreferenceHelper().getStringObject(
                        SharedPreferenceHelper.Key.SELECTED_HOUSE, null));
    }
}
