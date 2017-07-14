package com.thehatefulsix.tariffcounter.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by user on 24.10.2016.
 */

public class AlarmController {

    private String TAG = "AlarmController";

    private static final int SERVICE_CHECKUP_ID = 0;
    private static AlarmController sAlarmController;
    private AlarmManager mAlarmManager;
    private Context mContext;

    private AlarmController (){}

    private AlarmController (Context context){
        mContext = context;
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static AlarmController getInstance (Context context){
        if (sAlarmController == null){
            sAlarmController = new AlarmController(context);
        }
        return sAlarmController;
    }


    public void scheduleNextServicesCheckup() {
        Log.i(TAG, "scheduleNextServicesCheckup");
        Intent intent = new Intent("com.thehatefulsix.tariffcounter.SERVICE_CHECKUP");
        intent.addCategory("android.intent.category.DEFAULT");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, SERVICE_CHECKUP_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long finishTime = calendar.getTimeInMillis();
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, finishTime, pendingIntent);
    }
}
