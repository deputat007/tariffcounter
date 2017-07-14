package com.thehatefulsix.tariffcounter.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.fragments.core.FragmentWithNavigationTabBar;

public class StatisticFragment extends FragmentWithNavigationTabBar {

    @Override
    public int changeActionBarTitle() {
        return R.string.statistic;
    }

    @Override
    protected int contentView() {
        return R.layout.fragment_statistic;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
