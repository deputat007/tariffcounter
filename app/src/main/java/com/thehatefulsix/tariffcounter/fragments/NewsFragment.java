package com.thehatefulsix.tariffcounter.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.activities.EditNewsActivity;
import com.thehatefulsix.tariffcounter.adapter.NewsAdapter;
import com.thehatefulsix.tariffcounter.fragments.core.FragmentWithNavigationTabBar;
import com.thehatefulsix.tariffcounter.models.Community;
import com.thehatefulsix.tariffcounter.models.WallPost;
import com.thehatefulsix.tariffcounter.utils.InternetConnectivity;
import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;
import com.thehatefulsix.tariffcounter.utils.SimpleScrollListener;
import com.thehatefulsix.tariffcounter.utils.SnackBarHelper;
import com.thehatefulsix.tariffcounter.utils.VkHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class NewsFragment extends FragmentWithNavigationTabBar{

    private static final int REQUEST_EDIT_NEWS = 101;

    private NewsAdapter mAdapter;

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.context) View mFragmentView;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public int changeActionBarTitle() {
        return R.string.news;
    }

    @Override
    protected int contentView() {
        return R.layout.fragment_news;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSharedPreferenceHelper().saveIntObject(SharedPreferenceHelper.Key.NEWS_BADGE, 0);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NewsFragment.this.onRefresh();
            }
        });

        mRecyclerView.addOnScrollListener(new SimpleScrollListener(
                new SimpleScrollListener.OnScrollListener() {
                    @Override
                    public void onDownScrolling() {
                        getNavigationTabBar().setVisibility(View.GONE);
                    }

                    @Override
                    public void onUpScrolling() {
                        getNavigationTabBar().setVisibility(View.VISIBLE);
                    }
                }));

        final int groupId = getActivity().getResources().getInteger(R.integer.group_id);
        final Community community = getRealmHelper().getObjectById(Community.class, groupId);

        if (community != null && !community.getWallPosts().isEmpty()) {
            updateUI(community);
        } else {
            onRefresh();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_NEWS && resultCode == Activity.RESULT_OK){
            onRefresh();
        }
    }

    @OnClick(R.id.fab)
    public void onClick() {
        startActivityForResult(new Intent(getActivity(), EditNewsActivity.class), REQUEST_EDIT_NEWS);
    }

    private void updateUI(final Community community) {
        if (mAdapter == null) {
            mAdapter = new NewsAdapter(community, getContext(), new NewsAdapter.Callback() {
                @Override
                public void onImageClicked(@NonNull WallPost wallPost, @NonNull View view) {

                }
            });
            mRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onRefresh() {
        if (InternetConnectivity.isOnline(getActivity())){
            refreshNews();
        }else {
            showSnackBar(R.string.no_internet_connection);

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void refreshNews() {
        VkHelper.refreshNews(getContext(), new VkHelper.NewsUpdateCallback() {
            @Override
            protected void onComplete(Community community) {
                updateUI(community);

                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            protected void onError(String error) {
                showSnackBar(error);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showSnackBar(@StringRes final int message) {
        SnackBarHelper.show(getActivity(), mFragmentView, message);
    }

    private void showSnackBar(final String message) {
        SnackBarHelper.show(getActivity(), mFragmentView, message);
    }
}
