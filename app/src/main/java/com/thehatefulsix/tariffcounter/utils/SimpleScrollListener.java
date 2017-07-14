package com.thehatefulsix.tariffcounter.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;


public class SimpleScrollListener extends RecyclerView.OnScrollListener{

    private OnScrollListener mCallback;

    public SimpleScrollListener(@NonNull OnScrollListener callback) {
        mCallback = callback;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy > 0) {
            mCallback.onDownScrolling();
        } else {
            mCallback.onUpScrolling();
        }
    }

    public interface OnScrollListener {
        void onDownScrolling();

        void onUpScrolling();
    }
}
