package com.thehatefulsix.tariffcounter.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.thehatefulsix.tariffcounter.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerDialogFragment extends DialogFragment {

    private static final String KEY_CURRENT_DATE = "KEY_CURRENT_DATE";
    private GregorianCalendar mCalendar;

    private OnClickListener mOnClickListener;

    public static DialogFragment newInstance(long currentDate){
        final Bundle args = new Bundle();

        final DialogFragment dialogFragment = new DatePickerDialogFragment();

        args.putLong(KEY_CURRENT_DATE, currentDate);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(getActivity() instanceof DatePickerDialogFragment.OnClickListener)){
            throw new RuntimeException("Activity " + getActivity().getClass().getSimpleName()
                    + " must implement interface " + OnClickListener.class.getSimpleName());
        }

        mOnClickListener = (OnClickListener) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendar = new GregorianCalendar();

        if (getArguments() != null && getArguments().containsKey(KEY_CURRENT_DATE)) {
            mCalendar.setTimeInMillis(getArguments().getLong(KEY_CURRENT_DATE));
        }else {
            mCalendar.setTimeInMillis(System.currentTimeMillis());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);

        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);

        datePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCalendar.set(datePicker.getYear(), datePicker.getMonth(),
                                datePicker.getDayOfMonth());

                        mOnClickListener.onOkClicked(getTag(), mCalendar);

                        dismiss();
                    }
                })
                .setNegativeButton(R.string.text_button_cancel, null)
                .create();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnClickListener = null;
    }

    public interface OnClickListener{
        void onOkClicked(String tag, GregorianCalendar date);
    }
}
