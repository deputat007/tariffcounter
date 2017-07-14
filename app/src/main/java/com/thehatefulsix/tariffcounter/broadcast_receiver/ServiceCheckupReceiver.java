package com.thehatefulsix.tariffcounter.broadcast_receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.activities.MainActivity;
import com.thehatefulsix.tariffcounter.models.Bill;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.models.Rate;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.utils.AlarmController;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.realm.RealmResults;



public class ServiceCheckupReceiver extends BroadcastReceiver {
    private static final String TAG = "ServiceCheckupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Service checkup started");
        Toast.makeText(context, "checkup started", Toast.LENGTH_SHORT).show();
        Bill bill;
        RealmResults<Service> services;
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        GregorianCalendar periodStart = new GregorianCalendar();
        Long lastBillEndDateMills;
        Bill lastBill;
        Rate.Period paymentPeriod;
        String houseId;
        boolean billCreated = false;

        RealmResults<House> houses = RealmHelper.getInstance().getAll(House.class);

        for (House house : houses){
            houseId = house.getId();
            services = RealmHelper.getInstance().getAll(Service.class).where().equalTo("mIdHouse", houseId).findAll();

            for (Service service : services){

                try {
                    paymentPeriod = service.getRate().getPeriod();
                } catch (NullPointerException e){
                    Log.i(TAG, "service " + service.getId() + " " + service.getName() + " doesn't have Rate");
                    continue;
                }

                lastBillEndDateMills = (Long) RealmHelper.getInstance().getAll(Bill.class)
                        .where().equalTo("mService.mId", service.getId()).max("mPeriodEnd");
                if (lastBillEndDateMills != null){
                    if (lastBillEndDateMills < today.getTimeInMillis()){
                        lastBill = RealmHelper.getInstance().getAll(Bill.class).where().equalTo("mPeriodEnd", lastBillEndDateMills).findFirst();
                        periodStart.setTimeInMillis(lastBillEndDateMills);
                        periodStart.add(Calendar.DAY_OF_MONTH, 1);
                        switch (paymentPeriod){
                            case MONTH:
                                //TODO створення платіжки в вказаний день оплати?
                                int actualMaxDayOfMonth = today.getActualMaximum(Calendar.DAY_OF_MONTH);
                                int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
                                if (actualMaxDayOfMonth == dayOfMonth){
                                    bill = new Bill(service, service.getRate().getId(), periodStart.getTimeInMillis(),
                                            today.getTimeInMillis(), lastBill.getCurrentMark(), -1, false, -1);
                                    RealmHelper.getInstance().addOrUpdate(bill);
                                    billCreated = true;
                                }
                                break;
                            case WEEK:
                                int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
                                if (Calendar.SUNDAY == dayOfWeek){
                                    bill = new Bill(service, service.getRate().getId(), periodStart.getTimeInMillis(),
                                            today.getTimeInMillis(), lastBill.getCurrentMark(), -1, false, -1);
                                    RealmHelper.getInstance().addOrUpdate(bill);
                                    billCreated = true;
                                }
                                break;

                            case DAY:
                                bill = new Bill(service, service.getRate().getId(), periodStart.getTimeInMillis(),
                                        today.getTimeInMillis(), lastBill.getCurrentMark(), -1, false, -1);
                                RealmHelper.getInstance().addOrUpdate(bill);
                                billCreated = true;
                                break;
                        }
                    }
                } else {
                    GregorianCalendar paymentPeriodStart;
                    switch (paymentPeriod){
                        case MONTH:
                            //TODO створення платіжки в вказаний день оплати?
                            int actualMaxDayOfMonth = today.getActualMaximum(Calendar.DAY_OF_MONTH);
                            int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
                            paymentPeriodStart = new GregorianCalendar();
                            paymentPeriodStart.setTimeInMillis(today.getTimeInMillis());
                            paymentPeriodStart.set(Calendar.DAY_OF_MONTH, 1);
                            if (actualMaxDayOfMonth == dayOfMonth){
                                bill = new Bill(service, service.getRate().getId(), paymentPeriodStart.getTimeInMillis(),
                                        today.getTimeInMillis(), -1, -1, false, -1);
                                RealmHelper.getInstance().addOrUpdate(bill);
                                billCreated = true;
                            }
                            break;
                        case WEEK:
                            int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
                            if (Calendar.SUNDAY == dayOfWeek){
                                paymentPeriodStart = new GregorianCalendar();
                                paymentPeriodStart.setTimeInMillis(today.getTimeInMillis());
                                paymentPeriodStart.add(Calendar.DAY_OF_MONTH, -6);
                                bill = new Bill(service, service.getRate().getId(), paymentPeriodStart.getTimeInMillis(),
                                        today.getTimeInMillis(), -1, -1, false, -1);
                                RealmHelper.getInstance().addOrUpdate(bill);
                                billCreated = true;
                            }
                            break;

                        case DAY:
                            bill = new Bill(service, service.getRate().getId(), today.getTimeInMillis(),
                                    today.getTimeInMillis(), -1, -1, false, -1);
                            RealmHelper.getInstance().addOrUpdate(bill);
                            billCreated = true;
                            break;
                    }
                }

            }

        }


        if (billCreated){
            showNotification(context);
        }
        AlarmController.getInstance(context).scheduleNextServicesCheckup();
    }

    private void showNotification(Context context) {

        Intent notificationIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.new_unpaid_bills))
                .setTicker(context.getResources().getString(R.string.new_unpaid_bills))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
