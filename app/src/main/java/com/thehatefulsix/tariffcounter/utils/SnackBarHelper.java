package com.thehatefulsix.tariffcounter.utils;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.thehatefulsix.tariffcounter.R;

public class SnackBarHelper {

    public static void show(final @NonNull Context context, final @NonNull View view,
                            @StringRes int message){
        show(context, view, message, Snackbar.LENGTH_SHORT, R.color.colorPrimaryDark);
    }

    public static void show(final @NonNull Context context, final @NonNull View view,
                             final @NonNull String message){
        show(context, view, message, Snackbar.LENGTH_SHORT, R.color.colorPrimaryDark);
    }

    @SuppressWarnings("deprecation")
    public static void show(final @NonNull Context context, final @NonNull View view,
                            @StringRes int message, int duration, @ColorRes int color){
        final Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(color));
        snackbar.show();
    }

    @SuppressWarnings("deprecation")
    public static void show(final @NonNull Context context, final @NonNull View view,
                            final @NonNull String message, int duration, @ColorRes int color){
        final Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(color));
        snackbar.show();
    }

}
