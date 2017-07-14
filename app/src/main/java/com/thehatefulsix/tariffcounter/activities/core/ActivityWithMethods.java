package com.thehatefulsix.tariffcounter.activities.core;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;

import butterknife.ButterKnife;


public abstract class ActivityWithMethods extends AppCompatActivity {

    private RealmHelper mRealmHelper;
    private SharedPreferenceHelper mSharedPreferenceHelper;

    private ActionBar mActionBar;

    @Nullable protected abstract String changeActionBarTitle();
    @LayoutRes protected abstract int contentView();
    protected abstract boolean displayHomeAsUpEnabled();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView());

        ButterKnife.bind(this);

        setStatusBarColor(this);

        if (getSupportActionBar() == null && findViewById(R.id.toolbar) != null) {
            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        }
        mActionBar = getSupportActionBar();

        if (changeActionBarTitle() != null) {
            changeTitle(changeActionBarTitle());
        }
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRealmHelper != null) {
            mRealmHelper.initRealm();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRealmHelper != null) {
            mRealmHelper.closeRealm();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (displayHomeAsUpEnabled() && item.getItemId() == android.R.id.home){
            onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void changeTitle(String title) {
        if (mActionBar != null){
            mActionBar.setTitle(title);
        }
    }

    protected void changeTitle(@StringRes int resourceId) {
        if (mActionBar != null){
            mActionBar.setTitle(resourceId);
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
                getSharedPreferenceHelper().getStringObject(SharedPreferenceHelper.Key.SELECTED_HOUSE, null));
    }

    public static void setStatusBarColor(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        }
    }
}
