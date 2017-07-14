package com.thehatefulsix.tariffcounter.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.thehatefulsix.tariffcounter.R;

public class MyDialogFragment extends DialogFragment {

    public static final String KEY_TITLE_ID = "key_title_id";
    public static final String KEY_MESSAGE_ID = "key_message_id";

    private OnClickListener mOnClickListener;

    private int mTitle;
    private int mMessage;

    public static DialogFragment newInstance(@StringRes int titleId, @StringRes int messageId){
        final Bundle args = new Bundle();

        args.putInt(KEY_TITLE_ID, titleId);
        args.putInt(KEY_MESSAGE_ID, messageId);

        final DialogFragment dialogFragment = new MyDialogFragment();

        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof OnClickListener)){
            throw new RuntimeException("Activity " + getActivity().getClass().getSimpleName()
                    + " must implement interface " + OnClickListener.class.getSimpleName());
        }

        mOnClickListener = (OnClickListener) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = getArguments().getInt(KEY_TITLE_ID);
        mMessage = getArguments().getInt(KEY_MESSAGE_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOnClickListener.onOkClicked(getTag());

                        dismiss();
                    }
                })
                .setNegativeButton(R.string.text_button_cancel, null)
                .setTitle(mTitle)
                .setMessage(mMessage)
                .create();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnClickListener = null;
    }

    public interface OnClickListener{
        void onOkClicked(String tag);
    }
}
