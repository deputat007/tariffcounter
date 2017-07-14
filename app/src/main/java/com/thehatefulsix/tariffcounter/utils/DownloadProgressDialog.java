package com.thehatefulsix.tariffcounter.utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thehatefulsix.tariffcounter.R;

public class DownloadProgressDialog {
    private ProgressDialog mProgressBar;

    private Context mContext;
    private String mMessage;
    private boolean mCancelable;

    public DownloadProgressDialog(@NonNull Context context, @Nullable String message,
                                  boolean cancelable) {
        this.mContext = context;
        this.mMessage = message;
        mCancelable = cancelable;

        buildProgressBar();
    }

    @SuppressWarnings("deprecation")
    private void buildProgressBar() {
        mProgressBar = new ProgressDialog(mContext);
        if (mMessage != null){
            mProgressBar.setMessage(mMessage);
        }
        mProgressBar.setCancelable(mCancelable);
        mProgressBar.setIndeterminateDrawable(
                mContext.getResources().getDrawable(R.drawable.progress_animation));
        mProgressBar.setIndeterminate(true);
    }

    public void show(){
        mProgressBar.show();
    }

    public void stop(){
        mProgressBar.dismiss();
    }
}
