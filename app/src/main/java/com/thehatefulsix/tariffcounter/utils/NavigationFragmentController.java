package com.thehatefulsix.tariffcounter.utils;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


public class NavigationFragmentController {

    public static void moveToNextFragment(@NonNull FragmentManager fragmentManager,
                                          @NonNull Fragment fragment,
                                          @Nullable String tag, @IdRes int containerFragment) {

        fragmentManager.beginTransaction().replace(containerFragment, fragment, tag).commit();
    }

    public static void removeFragment(@NonNull FragmentManager fragmentManager,
                                      @NonNull Fragment fragment) {
        fragmentManager.beginTransaction().remove(fragment).commit();
    }
}
