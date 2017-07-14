package com.thehatefulsix.tariffcounter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public final class SharedPreferenceHelper {
    private static SharedPreferenceHelper sInstance;

    private SharedPreferences mSharedPreferences;
    private Gson mGson;

    private SharedPreferenceHelper(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mGson = new Gson();
    }

    public static SharedPreferenceHelper getInstance(){
        return sInstance;
    }

    public static String getFileName(Context context){
        return context.getPackageName() + "_preferences";
    }

    public static void init(Context context){
        sInstance = new SharedPreferenceHelper(context);
    }

    private SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    private Gson getGson(){
        return mGson;
    }

    public void saveStringObject(Key key, String value) {
        getSharedPreferences()
                .edit().putString(key.getKey(), value)
                .apply();
    }

    public void saveBooleanObject(Key key, boolean value) {
        getSharedPreferences()
                .edit().putBoolean(key.getKey(), value)
                .apply();
    }

    public void saveLongObject(Key key, long value) {
        getSharedPreferences()
                .edit().putLong(key.getKey(), value)
                .apply();
    }

    public void saveIntObject(Key key, int value) {
        getSharedPreferences()
                .edit().putInt(key.getKey(), value)
                .apply();
    }

    public void saveFloatObject(Key key, float value) {
        getSharedPreferences()
                .edit().putFloat(key.getKey(), value)
                .apply();
    }

    public String getStringObject(Key key, String defValue) {
        return getSharedPreferences().getString(key.getKey(), defValue);
    }

    public boolean getBooleanObject(Key key, boolean defValue) {
        return getSharedPreferences().getBoolean(key.getKey(), defValue);
    }

    public long getLongObject(Key key, long defValue) {
        return getSharedPreferences().getLong(key.getKey(), defValue);
    }

    public int getIntObject(Key key, int defValue) {
        return getSharedPreferences().getInt(key.getKey(), defValue);
    }

    public float getFloatObject(Key key, float defValue) {
        return getSharedPreferences().getFloat(key.getKey(), defValue);
    }

    public void removeObject(Key key) {
        getSharedPreferences()
                .edit().remove(key.getKey())
                .apply();
    }

    public <T> void saveObject(Key key, T value) {
        Type type = new TypeToken<T>(){}.getType();
        String objectJson = getGson().toJson(value, type);
        saveStringObject(key, objectJson);
    }

    public <T> T getObject(Key key, Type type) {
        String objectJson = getStringObject(key, "");
        if (!objectJson.isEmpty()){
            return getGson().fromJson(objectJson, type);
        } else {
            return null;
        }

    }

    public boolean containsKey(Key key) {
        return getSharedPreferences().contains(key.getKey());
    }

    public enum Key {
        SELECTED_HOUSE("selected_house_key"),
        HISTORY_SORTING("history_sorting_key"),
        BILL_SORTING("bill_sorting_key"),

        FILTER_PAID("filter_paid"),
        FILTER_UNPAID("filter_unpaid"),
        FILTER_PAYMENT_HISTORY("filter_payment_history"),

        HOUSE_VIEW_PAGER("house_view_pager_key"),

        PERMISSION_READ_EXTERNAL_STORAGE("permission_key_read_external_storage"),
        PERMISSION_CAMERA("permission_camera"),

        NEWS_BADGE("news_badge_key");

        String key;

        Key(String key){
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
