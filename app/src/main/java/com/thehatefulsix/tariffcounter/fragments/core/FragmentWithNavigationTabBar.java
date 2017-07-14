package com.thehatefulsix.tariffcounter.fragments.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.thehatefulsix.tariffcounter.activities.core.NavigationTabBarActivity;

import devlight.io.library.ntb.NavigationTabBar;

public abstract class FragmentWithNavigationTabBar extends FragmentWithMethods implements ActionBarTitle,
        NavigationTabBarActivity{

    private NavigationTabBar mNavigationTabBar;
    private NavigationTabBarActivity mNavigationTabBarActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof NavigationTabBarActivity)){
            throw new RuntimeException("Activity " + getActivity().getClass().getSimpleName()
                    + " must implement interface " + NavigationTabBarActivity.class.getSimpleName());
        }

        mNavigationTabBarActivity = (NavigationTabBarActivity) getActivity();
    }

    @NonNull
    @Override
    public NavigationTabBar getNavigationTabBar() {
        if (mNavigationTabBar == null){
            mNavigationTabBar = mNavigationTabBarActivity.getNavigationTabBar();
        }

        return mNavigationTabBar;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mNavigationTabBarActivity = null;
    }
}
