package com.thehatefulsix.tariffcounter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.activities.core.ActivityWithMethods;
import com.thehatefulsix.tariffcounter.activities.core.NavigationTabBarActivity;
import com.thehatefulsix.tariffcounter.fragments.BillsFragment;
import com.thehatefulsix.tariffcounter.fragments.HistoryFragment;
import com.thehatefulsix.tariffcounter.fragments.NewsFragment;
import com.thehatefulsix.tariffcounter.fragments.SelectHouseFragment;
import com.thehatefulsix.tariffcounter.fragments.core.FragmentWithNavigationTabBar;
import com.thehatefulsix.tariffcounter.models.Community;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;
import com.thehatefulsix.tariffcounter.utils.VkHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import devlight.io.library.ntb.NavigationTabBar;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends ActivityWithMethods
        implements NavigationTabBarActivity {

    private static final String SERVICE_CHECKUP = "com.thehatefulsix.tariffcounter.SERVICE_CHECKUP";

    @BindView(R.id.ntb) NavigationTabBar mNavigationTabBar;
    @BindView(R.id.view_pager) ViewPager mViewPager;

    @Nullable
    @Override
    protected String changeActionBarTitle() {
        return null;
    }

    @Override
    protected int contentView() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        final List<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_home),
                        getResources().getColor(R.color.current_ntb_item))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_format_list_bulleted),
                        getResources().getColor(R.color.current_ntb_item))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_history),
                        getResources().getColor(R.color.current_ntb_item))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_newspaper),
                        getResources().getColor(R.color.current_ntb_item))
                        .build()
        );
        mNavigationTabBar.setModels(models);
        mNavigationTabBar.setViewPager(mViewPager, getRealmHelper().getAll(House.class).size() == 1 ? 1 : 0);
        setFragmentTitle(getRealmHelper().getAll(House.class).size() == 1 ? 1 : 0);
        mNavigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setFragmentTitle(position);

                if (position == 3){
                    mNavigationTabBar.getModels().get(position).hideBadge();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateNews();

        RealmResults<Service> services = RealmHelper.getInstance().getAll(Service.class);
        services.addChangeListener(new RealmChangeListener<RealmResults<Service>>() {
            @Override
            public void onChange(RealmResults<Service> element) {
                sendBroadcast(new Intent(SERVICE_CHECKUP));
            }
        });
    }

    @NonNull
    @Override
    public NavigationTabBar getNavigationTabBar(){
        return mNavigationTabBar;
    }

    private void updateNews() {
        if (getSharedPreferenceHelper().getIntObject(SharedPreferenceHelper.Key.NEWS_BADGE, 0) > 0){
            showNewsBadge();
        }else {
            final int groupId = getResources().getInteger(R.integer.group_id);
            final Community community = getRealmHelper().getObjectById(Community.class, groupId);
            final int newsCount = community == null ? 0 : community.getWallPosts().size();

            VkHelper.refreshNews(this, new VkHelper.NewsUpdateCallback() {
                @Override
                protected void onComplete(Community community) {
                    final int title = community.getWallPosts().size() - newsCount;
                    getSharedPreferenceHelper().saveIntObject(
                            SharedPreferenceHelper.Key.NEWS_BADGE, title);

                    showNewsBadge();
                }
            });
        }
    }

    private void showNewsBadge() {
        final int title =
                getSharedPreferenceHelper().getIntObject(SharedPreferenceHelper.Key.NEWS_BADGE, 0);

        if (title > 0) {
            mNavigationTabBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final NavigationTabBar.Model model = mNavigationTabBar.getModels().get(3);
                    if (!model.isBadgeShowed()) {
                        model.setBadgeTitle(String.valueOf(title));
                        model.showBadge();
                    } else model.updateBadgeTitle(String.valueOf(title));
                }
            }, 100);
        }
    }

    private void setFragmentTitle(int position){
        final FragmentWithNavigationTabBar currentFragment =
                ((FragmentWithNavigationTabBar)
                        ((ViewPagerAdapter) mViewPager.getAdapter()).getItem(position));

        setTitle(currentFragment.changeActionBarTitle());

        getNavigationTabBar().setVisibility(View.VISIBLE);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                default:
                case 0 :
                    return SelectHouseFragment.getInstance();
                case 1 :
                    return new BillsFragment();
                case 2 :
                    return new HistoryFragment();
                case 3 :
                    return new NewsFragment();
            }
        }
    }
}
