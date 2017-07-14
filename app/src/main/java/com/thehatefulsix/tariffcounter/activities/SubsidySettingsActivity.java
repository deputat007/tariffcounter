package com.thehatefulsix.tariffcounter.activities;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.models.Subsidy;
import com.thehatefulsix.tariffcounter.utils.DateFormatter;
import com.thehatefulsix.tariffcounter.utils.DatePickerDialogFragment;
import com.thehatefulsix.tariffcounter.utils.MyDialogFragment;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.ServiceController;

import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class SubsidySettingsActivity extends    AppCompatActivity
                                     implements MyDialogFragment.OnClickListener,
                                                DatePickerDialogFragment.OnClickListener {

    public static final String EXTRA_SERVICE_ID =
            "com.thehatefulsix.tariffcounter.subsidy_service_id";

    public static final String EXTRA_SUBSIDY_ID =
            "com.thehatefulsix.tariffcounter.service_subsidy_activity.subsidy_id";

    public static final String EXTRA_ON_RESULT_SUBSIDY_ID =
            "com.thehatefulsix.tariffcounter.subsidy.on_result_subsidy_id";

    private static final String DIALOG_ON_CLOSE =
            "com.thehatefulsix.tariffcounter.subsidy_dialog_on_close";

    private static final String SUBSIDY_DATE_PICKER_FROM =
            "com.thehatefulsix.tariffcounter.subsidy_date_picker_from";
    private static final String SUBSIDY_DATE_PICKER_TO = "subsidy_date_picker_to";

    private static final String STATE_DATE_FROM =
            "com.thehatefulsix.tariffcounter.subsidy_state_date_picker_from";
    private static final String STATE_DATE_TO =
            "com.thehatefulsix.tariffcounter.subsidy_state_date_picker_to";


    private Service mService;
    private Subsidy mSubsidy;

    private RealmHelper mRealmHelper;

    private Button mButtonSubsidyFrom;
    private Button mButtonSubsidyTo;

    private Date mDateSubsidyFrom;
    private Date mDateSubsidyTo;

    private TextInputLayout mWrapperOverpayment;
    private TextInputEditText mEditTextOverpayment;

    private TextInputLayout mWrapperSubsidyMandatoryFee;
    private TextInputEditText mEditTextSubsidyMandatoryFee;

    private TextInputLayout mWrapperSubsidySocialNorm;
    private TextInputEditText mEditTextSubsidySocialNorm;

    String mOverpayment;
    float mOverpaymentFloat;

    String mMandatoryFee;
    float mMandatoryFeeFloat;

    String mSocialNorm;
    float mSocialNormFloat;


    public static Intent newIntent(Context packageContext, String serviceId, String subsidyId) {
        Intent i = new Intent(packageContext, SubsidySettingsActivity.class);
        i.putExtra(EXTRA_SERVICE_ID, serviceId);
        i.putExtra(EXTRA_SUBSIDY_ID, subsidyId);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsidy_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_DATE_FROM)) {
            mDateSubsidyFrom = (Date) savedInstanceState.getSerializable(STATE_DATE_FROM);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_DATE_TO)) {
            mDateSubsidyTo = (Date) savedInstanceState.getSerializable(STATE_DATE_TO);
        }

        mRealmHelper = RealmHelper.getInstance();

        if(getIntent().getStringExtra(EXTRA_SERVICE_ID) != null) {
            String serviceId = getIntent().getStringExtra(EXTRA_SERVICE_ID);

            mService = RealmHelper.getInstance().getObjectById(Service.class, serviceId);

        } else {
            mService = null;
        }

        if(getIntent().getStringExtra(EXTRA_SUBSIDY_ID) != null) {
            String subsidyId = getIntent().getStringExtra(EXTRA_SUBSIDY_ID);

            mSubsidy = RealmHelper.getInstance().getObjectById(Subsidy.class, subsidyId);

        } else {
            mSubsidy = null;
        }


        String currencySymbol = Currency.getInstance(Locale.getDefault()).getSymbol();

        String rateUnit = "";
        if(mService != null && mService.getRateUnit() != null) {
            rateUnit = ServiceController.rateUnitGetString(mService.getRateUnit(),
                    getApplicationContext());
        }


        TextView necessaryFields = (TextView) findViewById(R.id.text_view_subsidy_necessary);

        TextView serviceTypeOrTitle = (TextView) findViewById(R.id.text_service_type_or_title);

        mButtonSubsidyFrom = (Button) findViewById(R.id.button_subsidy_from_date);
        mButtonSubsidyTo = (Button) findViewById(R.id.button_subsidy_to_date);

        ImageButton buttonClearDateFrom = (ImageButton) findViewById(R.id.button_date_from_clear);
        ImageButton buttonClearDateTo = (ImageButton) findViewById(R.id.button_date_to_clear);

        mWrapperSubsidyMandatoryFee = (TextInputLayout)
                findViewById(R.id.wrapper_subsidy_mandatory_fee);
        mEditTextSubsidyMandatoryFee = (TextInputEditText)
                findViewById(R.id.edit_text_subsidy_mandatory_fee);

        mWrapperSubsidySocialNorm = (TextInputLayout)
                findViewById(R.id.wrapper_subsidy_social_norm);
        mEditTextSubsidySocialNorm = (TextInputEditText)
                findViewById(R.id.edit_text_subsidy_social_norm);

        mWrapperOverpayment = (TextInputLayout) findViewById(R.id.wrapper_overpayment);
        mEditTextOverpayment = (TextInputEditText) findViewById(R.id.edit_text_overpayment);

        Button buttonCancel = (Button) findViewById(R.id.button_subsidy_cancel);
        Button buttonSave = (Button) findViewById(R.id.button_subsidy_save);


        mButtonSubsidyFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(SUBSIDY_DATE_PICKER_FROM, mDateSubsidyFrom);
            }
        });

        buttonClearDateFrom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDateSubsidyFrom = null;

                updateDates();
            }
        });

        mButtonSubsidyTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(SUBSIDY_DATE_PICKER_TO, mDateSubsidyTo);
            }
        });

        buttonClearDateTo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDateSubsidyTo = null;

                updateDates();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                setResult(RESULT_CANCELED);
                safeExit();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                initFields();

                if(checkFieldsForChanges()) {
                    if(mService == null) {
                        if(!checkForWarnings()) {

                            if(mSubsidy == null) {
                                mSubsidy = mRealmHelper.createObject(Subsidy.class);

                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_ON_RESULT_SUBSIDY_ID, mSubsidy.getId());
                                setResult(RESULT_OK, intent);

                                saveSubsidyFields();

                                finish();

                            } else {
                                if(checkFieldsForChanges()) {
                                    saveSubsidyFields();

                                    finish();

                                } else {
                                    finish();
                                }
                            }
                        }
                    } else {
                        if(mSubsidy == null) {
                            if(!checkForWarnings()) {
                                mSubsidy = mRealmHelper.createObject(Subsidy.class);

                                mRealmHelper.beginTransaction();
                                mService.setSubsidy(mSubsidy);
                                mRealmHelper.commitTransaction();

                                saveSubsidyFields();

                                finish();

                            }
                        } else {
                            if(!checkForWarnings()) {
                                saveSubsidyFields();

                                finish();
                            }

                        }
                    }
                } else {
                    finish();
                }
            }
        });


        if(mDateSubsidyFrom == null && mSubsidy != null && mSubsidy.getDateGranted() != null) {
            mDateSubsidyFrom = mSubsidy.getDateGranted();
        }

        if(mDateSubsidyTo == null && mSubsidy != null && mSubsidy.getDateFinished() != null) {
            mDateSubsidyTo = mSubsidy.getDateFinished();
        }

        updateDates();


        mWrapperOverpayment.setHint(getResources().getString(R.string.hint_overpayment)
                + ", " + currencySymbol);

        mWrapperSubsidyMandatoryFee.setHint(getResources().getString(R.string.hint_mandatory_fee)
                + ", " + currencySymbol);

        if(mService != null && mService.getRateUnit() != null) {
            mWrapperSubsidySocialNorm.setHint(getResources().getString(R.string.hint_social_norm)
                    + ", " + rateUnit);
        } else {
            mWrapperSubsidySocialNorm.setHint(getResources()
                    .getString(R.string.hint_social_norm));
        }

        if(mService != null) {
            serviceTypeOrTitle.setVisibility(View.VISIBLE);

            if(mService.getName() != null) {
                serviceTypeOrTitle.setText(mService.getName());

            } else if(mService.getType() != null) {
                serviceTypeOrTitle.setText(ServiceController
                        .serviceTypeGetString(mService.getType(), getApplicationContext()));

            } else {
                serviceTypeOrTitle.setVisibility(View.GONE);
            }
        } else {
            serviceTypeOrTitle.setVisibility(View.GONE);
        }

        if(mSubsidy != null) {
            necessaryFields.setVisibility(View.GONE);
            mWrapperSubsidyMandatoryFee
                    .setHint(getResources().getString(R.string.hint_mandatory_fee));
            mWrapperSubsidySocialNorm
                    .setHint(getResources().getString(R.string.hint_social_norm));

            if(mSubsidy.getOverpayment() > 0) {
                mEditTextOverpayment.setText(String.valueOf(mSubsidy.getOverpayment()));
            }

            if(mSubsidy.getMandatoryFee() > 0) {
                mEditTextSubsidyMandatoryFee.setText(String.valueOf(mSubsidy.getMandatoryFee()));
            }

            if(mSubsidy.getSocialNorm() > 0) {
                mEditTextSubsidySocialNorm.setText(String.valueOf(mSubsidy.getSocialNorm()));
            }
        } else {
            necessaryFields.setVisibility(View.VISIBLE);
            mWrapperSubsidyMandatoryFee
                    .setHint(getResources().getString(R.string.hint_mandatory_fee_null));
            mWrapperSubsidySocialNorm
                    .setHint(getResources().getString(R.string.hint_social_norm_null));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRealmHelper.initRealm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealmHelper.closeRealm();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(mDateSubsidyFrom != null) {
            savedInstanceState.putSerializable(STATE_DATE_FROM, mDateSubsidyFrom);
        }
        if(mDateSubsidyTo != null) {
            savedInstanceState.putSerializable(STATE_DATE_TO, mDateSubsidyTo);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                safeExit();

                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {

        safeExit();
    }

    @Override
    public void onOkClicked(String tag) {
        if(tag.equals(DIALOG_ON_CLOSE)) {

            finish();
        }
    }

    private void safeExit() {
        if(checkFieldsForChanges()) {
            final DialogFragment dialogFragment = MyDialogFragment.newInstance(R.string.attention,
                    R.string.warning_on_close);

            dialogFragment.setCancelable(true);

            dialogFragment.show(getSupportFragmentManager(), DIALOG_ON_CLOSE);

        } else {
            finish();
        }
    }

    @Override
    public void onOkClicked(String tag, GregorianCalendar date) {
        switch (tag){
            case SUBSIDY_DATE_PICKER_FROM: {

                mDateSubsidyFrom = date.getTime();
                updateDates();
                break;
            }

            case SUBSIDY_DATE_PICKER_TO: {

                mDateSubsidyTo = date.getTime();
                updateDates();
                break;
            }
        }
    }


    private void updateDates() {
        if(mDateSubsidyFrom != null) {
            mButtonSubsidyFrom.setText(DateFormatter.parseData(mDateSubsidyFrom, this));
        } else {
            mButtonSubsidyFrom.setText(getResources().getString(R.string.text_empty_date));
        }

        if(mDateSubsidyTo != null) {
            mButtonSubsidyTo.setText(DateFormatter.parseData(mDateSubsidyTo, this));
        } else {
            mButtonSubsidyTo.setText(getResources().getString(R.string.text_empty_date));
        }
    }

    private void showDatePicker(String tag, Date date) {

        final long time = date != null ? date.getTime() : System.currentTimeMillis();
        final DialogFragment dialogFragment = DatePickerDialogFragment.newInstance(time);

        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), tag);
    }

    private void showSnackBar(final String message) {
        Snackbar.make(findViewById(R.id.scroll_view_subsidy_settings), message,
                Snackbar.LENGTH_LONG).show();
    }


    private void initFields() {
        mOverpayment = mEditTextOverpayment.getText().toString().trim();
        try {
            mOverpaymentFloat = Float.parseFloat(mOverpayment);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mMandatoryFee = mEditTextSubsidyMandatoryFee.getText().toString().trim();
        try {
            mMandatoryFeeFloat = Float.parseFloat(mMandatoryFee);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mSocialNorm = mEditTextSubsidySocialNorm.getText().toString().trim();
        try {
            mSocialNormFloat = Float.parseFloat(mSocialNorm);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private boolean checkForWarnings() {
        boolean warnings = false;

        initFields();

        if(mDateSubsidyFrom != null && mDateSubsidyTo != null) {
            if(mDateSubsidyTo.getTime() < mDateSubsidyFrom.getTime()) {

                showSnackBar(getResources().getString(R.string.warning_dates_priority));

                warnings = true;
                return warnings;
            }
        }

        if((mDateSubsidyFrom != null && mDateSubsidyTo == null)
                || (mDateSubsidyFrom == null && mDateSubsidyTo != null)) {

            showSnackBar(getResources().getString(R.string.warning_missing_date));

            warnings = true;
            return warnings;
        }

        if(mOverpaymentFloat < 0) {
            mWrapperOverpayment.setError(getString(R.string.warning_0));
            warnings = true;
            return warnings;

        } else {
            mWrapperOverpayment.setErrorEnabled(false);
        }

        if(!mMandatoryFee.equals("")) {
            if(mMandatoryFeeFloat > 0) {
                mWrapperSubsidyMandatoryFee.setErrorEnabled(false);
            } else {
                mWrapperSubsidyMandatoryFee.setError(getString(R.string.warning_0));
                warnings = true;
                return warnings;
            }
        } else {
            mWrapperSubsidyMandatoryFee.setError(getString(R.string.warning_absent_data));
            warnings = true;
            return warnings;
        }

        if(!mSocialNorm.equals("")) {
            if(mSocialNormFloat > 0) {
                mWrapperSubsidySocialNorm.setErrorEnabled(false);
            } else {
                mWrapperSubsidySocialNorm.setError(getString(R.string.warning_0));
                warnings = true;
                return warnings;
            }
        } else {
            mWrapperSubsidySocialNorm.setError(getString(R.string.warning_absent_data));
            warnings = true;
            return warnings;
        }

        return warnings;
    }

    private boolean checkFieldsForChanges() {
        boolean changed = false;

        initFields();

        if(mSubsidy != null) {
            if(mSubsidy.getDateGranted() != null && mDateSubsidyFrom != null) {
                if(mSubsidy.getDateGranted().getTime() != mDateSubsidyFrom.getTime()) {
                    changed = true;
                    return changed;
                }
            } else {
                if(mDateSubsidyFrom != null) {
                    changed = true;
                    return changed;
                }
            }

            if(mDateSubsidyFrom == null && mSubsidy.getDateGranted() != null) {
                changed = true;
                return changed;
            }

            if(mSubsidy.getDateFinished() != null && mDateSubsidyTo != null) {
                if(mSubsidy.getDateFinished().getTime() != mDateSubsidyTo.getTime()) {
                    changed = true;
                    return changed;
                }
            } else {
                if(mDateSubsidyTo != null) {
                    changed = true;
                    return changed;
                }
            }

            if(mDateSubsidyTo == null && mSubsidy.getDateFinished() != null) {
                changed = true;
                return changed;
            }

            if(mSubsidy.getOverpayment() != mOverpaymentFloat) {

                changed = true;
                return changed;
            }

            if(mSubsidy.getMandatoryFee() != mMandatoryFeeFloat) {

                changed = true;
                return changed;
            }

            if(mSubsidy.getSocialNorm() != mSocialNormFloat) {

                changed = true;
                return changed;
            }

        } else {
            if(mDateSubsidyFrom != null
                    || mDateSubsidyTo != null
                    || !mEditTextOverpayment.getText().toString().trim().isEmpty()
                    || !mEditTextSubsidyMandatoryFee.getText().toString().trim().isEmpty()
                    || !mEditTextSubsidySocialNorm.getText().toString().trim().isEmpty()) {

                changed = true;
                return changed;
            }
        }

        return changed;
    }

    private void saveSubsidyFields() {
        mRealmHelper.beginTransaction();
        mSubsidy.setDateGranted(mDateSubsidyFrom);
        mRealmHelper.commitTransaction();

        mRealmHelper.beginTransaction();
        mSubsidy.setDateFinished(mDateSubsidyTo);
        mRealmHelper.commitTransaction();

        if(!mOverpayment.equals("")) {
            mRealmHelper.beginTransaction();
            mSubsidy.setOverpayment(mOverpaymentFloat);
            mRealmHelper.commitTransaction();
        } else {
            mRealmHelper.beginTransaction();
            mSubsidy.setOverpayment(0);
            mRealmHelper.commitTransaction();
        }

        if(!mMandatoryFee.equals("")) {
            mRealmHelper.beginTransaction();
            mSubsidy.setMandatoryFee(mMandatoryFeeFloat);
            mRealmHelper.commitTransaction();
        } else {
            mRealmHelper.beginTransaction();
            mSubsidy.setMandatoryFee(0);
            mRealmHelper.commitTransaction();
        }

        if(!mSocialNorm.equals("")) {
            mRealmHelper.beginTransaction();
            mSubsidy.setSocialNorm(mSocialNormFloat);
            mRealmHelper.commitTransaction();
        } else {
            mRealmHelper.beginTransaction();
            mSubsidy.setSocialNorm(0);
            mRealmHelper.commitTransaction();
        }
    }
}
