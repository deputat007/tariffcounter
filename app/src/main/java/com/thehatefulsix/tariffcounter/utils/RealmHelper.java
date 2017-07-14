package com.thehatefulsix.tariffcounter.utils;

import android.content.Context;

import com.thehatefulsix.tariffcounter.models.Bill;
import com.thehatefulsix.tariffcounter.models.core.IModelWithID;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public final class RealmHelper {

    private static RealmHelper sInstance;
    private Realm mRealm;

    private static final String ID = "mId";
    private static final String IS_PAID = "mIsPaid";
    private static final String ID_SERVICE = "mService.mId";
    private static final String PERIOD_END = "mPeriodEnd";
    private static final String CURRENT_MARK = "mCurrentMark";

    private RealmHelper(Context context){
        buildConfiguration(context);
        initRealm();
    }

    public static void init(Context context){
        sInstance = new RealmHelper(context);
    }

    public static RealmHelper getInstance() {
        if (sInstance.getRealm().isClosed()){
            sInstance.initRealm();
        }

        return sInstance;
    }

    private void buildConfiguration(Context context) {
        Realm.init(context);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
    }

    private String generateId(){
        return UUID.randomUUID().toString();
    }

    private <T extends RealmModel> boolean hasField(Class<T> t, String field) {
        return getRealm().getSchema().get(t.getSimpleName()).hasField(field);
    }

    private <T extends RealmModel> boolean hasPrimaryKey(Class<T> t) {
        return getRealm().getSchema().get(t.getSimpleName()).hasPrimaryKey();
    }

    public void initRealm() {
        mRealm = Realm.getDefaultInstance();
    }

    public <T extends RealmModel> T createObject(Class<T> t) {
        beginTransaction();

        T object;

        if (hasPrimaryKey(t)){
            object = getRealm().createObject(t, generateId());
        }else {
            object = getRealm().createObject(t);
        }

        commitTransaction();

        return object;
    }

    public <T extends RealmModel> T createObjectWithId(Class<T> t, String id) {
        beginTransaction();

        T object;

        object = getRealm().createObject(t, id);

        commitTransaction();

        return object;
    }

    public <T extends RealmModel> T addOrUpdate(T t) {
        beginTransaction();

        if (hasPrimaryKey(t.getClass()) && hasField(t.getClass(), ID) &&
                t instanceof IModelWithID && ((IModelWithID) t).getId() == null){
            ((IModelWithID) t).setId(generateId());
        }

        T object;

        if (hasPrimaryKey(t.getClass())){
            object = getRealm().copyToRealmOrUpdate(t);
        }else {
            object = getRealm().copyToRealm(t);
        }

        commitTransaction();

        return object;
    }

    public <T extends RealmObject> void delete(final T t) {
        beginTransaction();
        t.deleteFromRealm();
        commitTransaction();
    }

    public <T extends RealmModel> RealmResults<T> getAll(Class<T> t) {
        return getRealm().where(t).findAll();
    }

    public <T extends RealmObject> T getObjectById(Class<T> tClass, String id) {
        return getRealm().where(tClass).equalTo(ID, id).findFirst();
    }

    public void beginTransaction() {
        getRealm().beginTransaction();
    }

    public void commitTransaction() {
        getRealm().commitTransaction();
    }

    public void closeRealm() {
        if (!getRealm().isClosed()) {
            getRealm().close();
        }
    }

    public Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()){
            initRealm();
        }

        return mRealm;
    }

    public <T extends RealmObject> T getObjectById(Class<T> tClass, int id) {
        return getRealm().where(tClass).equalTo(ID, id).findFirst();
    }

    public RealmResults<Bill> getAllBills(String serviceId, boolean isPaid) {
        return getRealm().where(Bill.class)
                .equalTo(ID_SERVICE, serviceId)
                .equalTo(IS_PAID, isPaid).findAll();
    }

    public RealmResults<Bill> getAllBills(String serviceId) {
        return getRealm().where(Bill.class).equalTo(ID_SERVICE, serviceId).findAll();
    }

    public RealmResults<Bill> getAllBills(boolean isPaid) {
        return getRealm().where(Bill.class).equalTo(IS_PAID, isPaid).findAll();
    }

    public long getLastPaymentDate(String serviceId){
        final Number number = getRealm().where(Bill.class)
                .equalTo(ID_SERVICE, serviceId)
                .equalTo(IS_PAID, true)
                .max(PERIOD_END);

        return  number == null ? 0 : number.longValue();
    }

    public int getPreviousMark(String serviceId){
        final Number number = getRealm().where(Bill.class)
                .equalTo(ID_SERVICE, serviceId)
                .equalTo(IS_PAID, true)
                .max(CURRENT_MARK);

        return number == null ? 0 : number.intValue();
    }

    public long getDifferent(String serviceId){
        final Number number = getRealm().where(Bill.class)
                .equalTo(ID_SERVICE, serviceId)
                .max("mPeriodEnd");

        long lastPayment = number == null ? 0 : number.longValue();

        if (lastPayment == 0){
            return 0;
        }

        long currentDate = System.currentTimeMillis();
        long different = currentDate - lastPayment;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        return different / daysInMilli;
    }

    public RealmResults<Bill> getAllSortedBills(String serviceId, boolean isPaid, String fieldName,
                                                             Sort sortOrder){
        return getRealm().where(Bill.class).equalTo(ID_SERVICE, serviceId).equalTo(IS_PAID, isPaid)
                .findAllSorted(fieldName, sortOrder);
    }

    public <T extends RealmObject> RealmList<T> addAll(List<T> ts){
        final RealmList<T> realmList = new RealmList<>();

        for (T t :
                ts) {
            realmList.add(addOrUpdate(t));
        }

        return realmList;
    }
}
