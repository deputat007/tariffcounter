package com.thehatefulsix.tariffcounter.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.github.clans.fab.FloatingActionMenu;
import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.activities.HouseSettingsActivity;
import com.thehatefulsix.tariffcounter.activities.ServiceSettingsActivity;
import com.thehatefulsix.tariffcounter.adapter.HousePagerAdapter;
import com.thehatefulsix.tariffcounter.adapter.ServiceListAdapter;
import com.thehatefulsix.tariffcounter.fragments.core.FragmentWithNavigationTabBar;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static com.thehatefulsix.tariffcounter.fragments.BillsFragment.ACTION_UPDATE_FRAGMENT;


public class SelectHouseFragment extends FragmentWithNavigationTabBar {

    private static final int REQUEST_CODE_HOUSE_SETTINGS = 101;
    private static final int REQUEST_CODE_SERVICE_SETTINGS = 202;

    @BindView(R.id.rv_services) RecyclerView mRecyclerView;
    @BindView(R.id.empty_container) FrameLayout mEmptyLayout;
    @BindView(R.id.hicvp) HorizontalInfiniteCycleViewPager mViewPager;
    @BindView(R.id.fab_menu) FloatingActionMenu mFloatingActionMenu;

    private ServiceListAdapter mAdapter;
    private RealmResults<House> mHouses;

    public static Fragment getInstance(){
        return new SelectHouseFragment();
    }

    @Override
    public int changeActionBarTitle() {
        return R.string.select_house;
    }

    @Override
    protected int contentView() {
        return R.layout.fragment_select_house;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFloatingActionMenu.setClosedOnTouchOutside(true);
        updateViewPager();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mFloatingActionMenu != null) {
            mFloatingActionMenu.close(false);
        }
    }

    @OnClick(R.id.fab_menu_item_add_service) void addService(){
        if(getCurrentHouse() != null) {
            startActivityForResult(ServiceSettingsActivity
                            .newIntent(getContext(), null, getCurrentHouse().getId()),
                    REQUEST_CODE_SERVICE_SETTINGS);
        }
    }

    @OnClick(R.id.fab_menu_item_add_house) void addHouse(){
        startActivityForResult(HouseSettingsActivity.newIntent(getContext(), null),
                REQUEST_CODE_HOUSE_SETTINGS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_HOUSE_SETTINGS && resultCode == Activity.RESULT_OK){
            updateViewPager();
        }
        if (requestCode == REQUEST_CODE_SERVICE_SETTINGS && resultCode == Activity.RESULT_OK){
            updateServiceList();
        }
    }

    private void updateServiceList() {
        if (getCurrentHouse() != null) {
            if (mAdapter == null) {
                mAdapter = new ServiceListAdapter(getCurrentHouse().getServices(), getContext(),
                        new ServiceListAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(View view, String id) {
                                startActivityForResult(ServiceSettingsActivity.newIntent(
                                        getContext(), id, getCurrentHouse().getId()),
                                        REQUEST_CODE_SERVICE_SETTINGS);
                            }

                            @Override
                            public boolean onItemLongClick(View view, String id) {
                                return false;
                            }
                        });

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setServices(getCurrentHouse().getServices());
            }
        }

        setVisibleEmptyLayout();
    }

    private void updateViewPager() {
        mHouses = getRealmHelper().getAll(House.class);
        final PagerAdapter pagerAdapter = new HousePagerAdapter(getContext(), mHouses,
                new HousePagerAdapter.OnClickListener() {
                    @Override
                    public void onHouseClick(String id) {}

                    @Override
                    public void onSettingsClick(String id) {
                        startActivityForResult(HouseSettingsActivity
                                .newIntent(getActivity(), id), REQUEST_CODE_HOUSE_SETTINGS);
                    }
                });
        mViewPager.setAdapter(pagerAdapter);
        mHouses.addChangeListener(new RealmChangeListener<RealmResults<House>>() {
            @Override
            public void onChange(RealmResults<House> element) {
                if (mViewPager != null && mAdapter != null){
                    mViewPager.notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        mViewPager.setCurrentItem(mHouses.indexOf(getCurrentHouse()), true);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (mViewPager.getRealItem() <= mHouses.size()) {
                    getSharedPreferenceHelper().saveStringObject(SharedPreferenceHelper
                                    .Key.SELECTED_HOUSE,
                            mHouses.get(mViewPager.getRealItem()).getId());

                    updateServiceList();
                    getActivity().sendBroadcast(new Intent(ACTION_UPDATE_FRAGMENT));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        updateServiceList();
    }

    private void setVisibleEmptyLayout() {
        if (getCurrentHouse() != null) {
            if (getCurrentHouse().getServices().isEmpty()) {
                mEmptyLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mEmptyLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }
}
