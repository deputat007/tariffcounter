package com.thehatefulsix.tariffcounter.utils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

public class PermissionHelper {

    public static boolean hasPermission(@NonNull Context context, @NonNull String permission){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    public static boolean shouldWeAsk(@NonNull final SharedPreferenceHelper.Key key){
        return SharedPreferenceHelper.getInstance().getBooleanObject(key, true);
    }

    /**
     * Don't forget to override method onRequestPermissionsResult() in your activity!
     * @param activity;
     * @param permission;
     * @param requestCode;
     * @param key;
     */
    public static void askPermissions(@NonNull final Activity activity,
                                      @NonNull final String permission,
                                      final int requestCode,
                                      @NonNull final SharedPreferenceHelper.Key key){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }else {
            SharedPreferenceHelper.getInstance().saveBooleanObject(key, false);
        }
    }
}
