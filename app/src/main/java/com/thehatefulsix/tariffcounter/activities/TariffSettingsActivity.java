package com.thehatefulsix.tariffcounter.activities;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.ConsumptionInterval;
import com.thehatefulsix.tariffcounter.models.Rate;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.utils.MyDialogFragment;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.ServerConnectivity;
import com.thehatefulsix.tariffcounter.utils.ServiceController;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TariffSettingsActivity extends    AppCompatActivity
                                    implements MyDialogFragment.OnClickListener {

    public static final String EXTRA_SERVICE_ID = "com.thehatefulsix.tariffcounter.service_id";

    public static final String EXTRA_RATE_ID =
            "com.thehatefulsix.tariffcounter.service_provider_activity.rate_id";

    public static final String EXTRA_ON_RESULT_RATE_ID =
            "com.thehatefulsix.tariffcounter.tariff.on_result_rate_id";

    private static final String DIALOG_ON_CLOSE =
            "com.thehatefulsix.tariffcounter.tariff_settings_dialog_on_close";

    static final String CUSTOM_2_VISIBILITY = "com.thehatefulsix.tariffcounter.view2";
    static final String CUSTOM_3_VISIBILITY = "com.thehatefulsix.tariffcounter.view3";


    private Service mService;
    private Rate mRate;

    String mRateUnit;

    private RealmHelper mRealmHelper;

    private boolean mViewCustomRate2IsVisible;
    private boolean mViewCustomRate3IsVisible;

    private RadioButton mRadioButtonFixed;
    private RadioButton mRadioButtonRate;

    private LinearLayout mViewFeeAndExtraFee;
    private TextInputEditText mEditTextFee;
    private TextInputLayout mWrapperExtraFee;
    private TextInputEditText mEditTextExtraFee;

    private LinearLayout mViewRateUnit;

    private TextView mTextViewRateFrom;
    private RadioGroup mRadioGroupRateFrom;
    private RadioButton mRadioButtonCustomRates;
    private RadioButton mRadioButtonDbRates;

    private LinearLayout mViewAllCustomRates;
    private TextInputLayout mWrapperTo1;
    private TextInputLayout mWrapperFrom1;
    private TextInputLayout mWrapperValue1;
    private TextInputEditText mEditTextFrom1;
    private TextInputEditText mEditTextTo1;
    private TextInputEditText mEditTextValue1;
    private LinearLayout mViewCustomRate2;
    private TextInputLayout mWrapperFrom2;
    private TextInputLayout mWrapperTo2;
    private TextInputLayout mWrapperValue2;
    private TextInputEditText mEditTextFrom2;
    private TextInputEditText mEditTextTo2;
    private TextInputEditText mEditTextValue2;
    private LinearLayout mViewCustomRate3;
    private TextInputLayout mWrapperMoreThen;
    private TextInputLayout mWrapperValue3;
    private TextInputEditText mEditTextMoreThen;
    private TextInputEditText mEditTextValue3;

    private ImageButton mButtonDeleteCustomRate2;
    private ImageButton mButtonDeleteCustomRate3;
    private ImageButton mButtonAddRate;

    private LinearLayout mViewRateUpdate;

    private CheckBox mIsDiscount;
    private TextInputEditText mEditTextDiscount;

    private TextInputLayout mWrapperExtraFee2;
    private TextInputEditText mEditTextExtraFee2;

    private LinearLayout mViewPaymentQuantity;
    private EditText mEditTextPaymentFrequency;
    private Spinner mTimeUnits;

    private Button mButtonSave;

    String mFee;
    float mFeeFloat;

    String mExtraFee;
    float mExtraFeeFloat;

    String mExtraFee2;
    float mExtraFee2Float;

    String mTo1;
    int mTo1Int;

    String mValue1;
    float mValue1Float;

    String mFrom2;
    int mFrom2Int;

    String mTo2;
    int mTo2Int;

    String mValue2;
    float mValue2Float;

    String mMoreThen;
    int mMoreThenInt;

    String mValue3;
    float mValue3Float;

    String mDiscount;
    int mDiscountInt;

    String mPaymentFrequency;
    int mPaymentFrequencyInt;

    String mSelectedSpinner;


    public static Intent newIntent(Context packageContext, String serviceId, String rateId) {
        Intent i = new Intent(packageContext, TariffSettingsActivity.class);
        i.putExtra(EXTRA_SERVICE_ID, serviceId);
        i.putExtra(EXTRA_RATE_ID, rateId);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tariff_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mViewCustomRate2IsVisible = savedInstanceState.getBoolean(CUSTOM_2_VISIBILITY);
            mViewCustomRate3IsVisible = savedInstanceState.getBoolean(CUSTOM_3_VISIBILITY);
        }

        mRealmHelper = RealmHelper.getInstance();

        if(getIntent().getStringExtra(EXTRA_SERVICE_ID) != null) {
            String serviceId = getIntent().getStringExtra(EXTRA_SERVICE_ID);
            mService = RealmHelper.getInstance().getObjectById(Service.class, serviceId);

        } else {
            mService = null;
        }

        if(getIntent().getStringExtra(EXTRA_RATE_ID) != null) {
            String rateId = getIntent().getStringExtra(EXTRA_RATE_ID);

            mRate = RealmHelper.getInstance().getObjectById(Rate.class, rateId);

        } else {
            mRate = null;
        }


        String currencySymbol = Currency.getInstance(Locale.getDefault()).getSymbol();

        if(mService != null && mService.getRateUnit() != null) {
            mRateUnit = ServiceController.rateUnitGetString(mService.getRateUnit(),
                    getApplicationContext());
        } else {
            mRateUnit = "";
        }


        final RadioGroup radioGroupTariffType =
                (RadioGroup) findViewById(R.id.radio_group_tariff_type);

        mViewFeeAndExtraFee = (LinearLayout) findViewById(R.id.view_fee_extra_fee);

        TextInputLayout wrapperFee = (TextInputLayout) findViewById(R.id.wrapper_fee);
        mWrapperExtraFee = (TextInputLayout) findViewById(R.id.wrapper_extra_fee);

        mEditTextFee = (TextInputEditText) findViewById(R.id.edit_text_fee);
        mEditTextExtraFee = (TextInputEditText) findViewById(R.id.edit_text_extra_fee);

        mEditTextExtraFee2 = (TextInputEditText) findViewById(R.id.edit_text_extra_fee_2);

        mViewRateUnit = (LinearLayout) findViewById(R.id.view_rate_unit);
        TextView textViewRateUnit = (TextView) findViewById(R.id.text_view_rate_unit);

        mTextViewRateFrom = (TextView) findViewById(R.id.text_view_rate_from);
        mRadioGroupRateFrom = (RadioGroup) findViewById(R.id.radio_group_rate_from);

        mViewAllCustomRates = (LinearLayout) findViewById(R.id.view_all_custom_rates);

        mWrapperFrom1 = (TextInputLayout) findViewById(R.id.wrapper_from_1);
        mWrapperTo1 = (TextInputLayout) findViewById(R.id.wrapper_to_1);
        mWrapperValue1 = (TextInputLayout) findViewById(R.id.wrapper_value_1);

        mEditTextFrom1 = (TextInputEditText) findViewById(R.id.edit_text_from_1);

        mEditTextTo1 = (TextInputEditText) findViewById(R.id.edit_text_to_1);
        mEditTextValue1 = (TextInputEditText) findViewById(R.id.edit_text_unit_value_1);

        mViewCustomRate2 = (LinearLayout) findViewById(R.id.view_custom_rate_2);

        mWrapperFrom2 = (TextInputLayout) findViewById(R.id.wrapper_from_2);
        mWrapperTo2 = (TextInputLayout) findViewById(R.id.wrapper_to_2);
        mWrapperValue2 = (TextInputLayout) findViewById(R.id.wrapper_value_2);

        mEditTextFrom2 = (TextInputEditText) findViewById(R.id.edit_text_from_2);
        mEditTextTo2 = (TextInputEditText) findViewById(R.id.edit_text_to_2);
        mEditTextValue2 = (TextInputEditText) findViewById(R.id.edit_text_unit_value_2);

        mViewCustomRate3 = (LinearLayout) findViewById(R.id.view_custom_rate_3);

        mWrapperMoreThen = (TextInputLayout) findViewById(R.id.wrapper_more_then);
        mWrapperValue3 = (TextInputLayout) findViewById(R.id.wrapper_value_3);

        mEditTextMoreThen = (TextInputEditText) findViewById(R.id.edit_text_more_then);
        mEditTextValue3 = (TextInputEditText) findViewById(R.id.edit_text_unit_value_3);

        mButtonDeleteCustomRate2 = (ImageButton) findViewById(R.id.button_rate_delete_2);

        mButtonDeleteCustomRate3 = (ImageButton) findViewById(R.id.button_rate_delete_3);

        mButtonAddRate = (ImageButton) findViewById(R.id.button_rate_add);

        mViewRateUpdate = (LinearLayout) findViewById(R.id.view_rate_updates);
        Button buttonUpdateRate = (Button) findViewById(R.id.button_update_rates);
        final TextView textDate = (TextView) findViewById(R.id.text_view_update_date);

        mIsDiscount = (CheckBox) findViewById(R.id.checkbox_discount);
        TextInputLayout wrapperDiscount = (TextInputLayout)
                findViewById(R.id.wrapper_discount);
        mEditTextDiscount = (TextInputEditText) findViewById(R.id.edit_text_discount);

        mWrapperExtraFee2 = (TextInputLayout) findViewById(R.id.wrapper_extra_fee_2);
        mEditTextExtraFee2 = (TextInputEditText) findViewById(R.id.edit_text_extra_fee_2);

        mRadioButtonFixed = (RadioButton) findViewById(R.id.radio_fixed_payment);
        mRadioButtonRate = (RadioButton) findViewById(R.id.radio_rate_payment);

        mRadioButtonCustomRates = (RadioButton) findViewById(R.id.radio_custom_rates);
        mRadioButtonDbRates = (RadioButton) findViewById(R.id.radio_db_rates);

        mIsDiscount = (CheckBox) findViewById(R.id.checkbox_discount);
        mEditTextDiscount = (TextInputEditText) findViewById(R.id.edit_text_discount);

        mViewPaymentQuantity = (LinearLayout) findViewById(R.id.view_payment_quantity);
        mEditTextPaymentFrequency = (EditText) findViewById(R.id.edit_text_quantity);
        mTimeUnits = (Spinner) findViewById(R.id.spinner_time_units);

        Button buttonCancel = (Button) findViewById(R.id.button_tariff_cancel);

        mButtonSave = (Button) findViewById(R.id.button_tariff_save);


        if(mService != null && mService.getRateUnit() != null) {
            textViewRateUnit.setText(mRateUnit);
        }

        if(mRate != null) {
            if(mRate.getFixedPayment() != 0) {
                mEditTextFee.setText(String.valueOf(mRate.getFixedPayment()));
            }

            if(mRate.getExtraPayment() != 0) {
                mEditTextExtraFee.setText(String.valueOf(mRate.getExtraPayment()));
                mEditTextExtraFee2.setText(String.valueOf(mRate.getExtraPayment()));
            }

            if(mRate.getRates() != null && mRate.getRates().size() > 0
                    && mRate.getRates().get(0) != null
                    && mRate.getRates().get(0).getIntervalFrom() > 0) {
                mEditTextFrom1.setText("1");

                if(mRate.getRates().get(0).getIntervalTo() > 1) {
                    mEditTextTo1.setText(String.valueOf(mRate.getRates().get(0)
                            .getIntervalTo()));
                }

                mEditTextValue1.setText(String.valueOf(mRate.getRates().get(0).getPrice()));
            } else {
                mEditTextFrom1.setText("1");
            }

            if(mRate.getRates() != null && mRate.getRates().size() > 1
                    && mRate.getRates().get(1) != null
                    && mRate.getRates().get(1).getIntervalFrom() > 0) {
                mViewCustomRate2.setVisibility(View.VISIBLE);
                mEditTextFrom2.setText(String.valueOf(mRate.getRates().get(1)
                        .getIntervalFrom()));

                if(mRate.getRates().get(1).getIntervalTo() > 0) {
                    mEditTextTo2.setText(String.valueOf(mRate.getRates().get(1)
                            .getIntervalTo()));
                }

                mEditTextValue2.setText(String.valueOf(mRate.getRates().get(1).getPrice()));
            }else {
                if(mViewCustomRate2IsVisible) {
                    mViewCustomRate2.setVisibility(View.VISIBLE);
                }else {
                    mViewCustomRate2.setVisibility(View.GONE);
                }
            }

            if(mRate.getRates() != null && mRate.getRates().size() > 2
                    && mRate.getRates().get(2) != null
                    && mRate.getRates().get(2).getIntervalFrom() > 0) {
                mViewCustomRate3.setVisibility(View.VISIBLE);
                mEditTextMoreThen.setText(String.valueOf(mRate.getRates().get(2)
                        .getIntervalFrom()));
                mEditTextValue3.setText(String.valueOf(mRate.getRates().get(2).getPrice()));
            }else {
                if(mViewCustomRate3IsVisible) {
                    mViewCustomRate3.setVisibility(View.VISIBLE);
                }else {
                    mViewCustomRate3.setVisibility(View.GONE);
                }
            }
        } else {
            mEditTextFrom1.setText("1");
        }

        if(mRate != null){
            buttonUpdateRate.setEnabled(true);
        }else {
            buttonUpdateRate.setEnabled(false);
        }

        if(mRate != null && mRate.getUpdateDate() != null) {
            textDate.setText(DateFormat.format("dd-MM-yyyy  hh:mm",
                    mRate.getUpdateDate()).toString());
        }

        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this,
                R.array.period_spinner_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeUnits.setAdapter(adapter);

        if(mRate != null && mRate.getPeriodString() != null) {
            switch(mRate.getPeriod()) {
                case DAY:
                    mTimeUnits.setSelection(1);
                    break;
                case WEEK:
                    mTimeUnits.setSelection(2);
                    break;
                case MONTH:
                    mTimeUnits.setSelection(0);
                    break;
                default:
                    mTimeUnits.setSelection(0);
                    break;
            }
        }

        if(mRate != null) {
            mEditTextPaymentFrequency.setText(String.valueOf(mRate.getAmountOfPayments()));
        }else {
            mEditTextPaymentFrequency.setText("1");
        }

        if(mRate != null && mRate.getDiscount() > 0 && mRate.getDiscount() <= 100) {
            mIsDiscount.setChecked(true);
            mEditTextDiscount.setText(String.valueOf(mRate.getDiscount()));

        } else {
            mIsDiscount.setChecked(false);
        }


        wrapperFee.setHint(getResources().getString(R.string.hint_main_fee)
                + ", " + currencySymbol);

        mWrapperExtraFee.setHint(getResources()
                .getString(R.string.hint_extra_fee) + ", " + currencySymbol);

        mWrapperExtraFee2.setHint(getResources()
                .getString(R.string.hint_extra_fee) + ", " + currencySymbol);

        mWrapperValue1.setHint(getResources().getString(R.string.hint_unit_value) + ", "
                + currencySymbol);

        mWrapperValue2.setHint(getResources().getString(R.string.hint_unit_value) + ", "
                + currencySymbol);

        mWrapperValue3.setHint(getResources().getString(R.string.hint_unit_value) + ", "
                + currencySymbol);


        mEditTextTo1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            @Override
            public void onTextChanged(final CharSequence c, int start, int before, int count) {
                if(mService != null && mService.getRateUnit() != null) {
                    setHintsWithRateUnits();

                } else {
                    setSimpleRateHints();
                }

                if(mEditTextTo1.getText().toString().trim().length() != 0) {
                    mButtonAddRate.setVisibility(View.VISIBLE);

                }else {
                    mButtonAddRate.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable c) {}
        });

        mEditTextTo2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            @Override
            public void onTextChanged(final CharSequence c, int start, int before, int count) {
                if(mService != null && mService.getRateUnit() != null) {
                    setHintsWithRateUnits();

                } else {
                    setSimpleRateHints();
                }

                if(mEditTextTo2.getText().toString().trim().length() != 0) {
                    mButtonAddRate.setVisibility(View.VISIBLE);

                }else {
                    mButtonAddRate.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable c) {}
        });

        mEditTextFrom2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            @Override
            public void onTextChanged(final CharSequence c, int start, int before, int count) {
                if(mEditTextFrom2.getText().toString().trim().length() == 0) {
                    mEditTextTo2.setEnabled(false);
                    mEditTextValue2.setEnabled(false);
                }else {
                    mEditTextTo2.setEnabled(true);
                    mEditTextValue2.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable c) {}
        });

        mEditTextMoreThen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            @Override
            public void onTextChanged(final CharSequence c, int start, int before, int count) {
                if(mEditTextMoreThen.getText().toString().trim().length() == 0) {
                    mEditTextValue3.setEnabled(false);
                }else {
                    mEditTextValue3.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable c) {}
        });

        mButtonDeleteCustomRate2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mViewCustomRate2.setVisibility(View.GONE);
                mViewCustomRate3.setVisibility(View.GONE);
                mButtonDeleteCustomRate2.setVisibility(View.GONE);
                mButtonDeleteCustomRate3.setVisibility(View.GONE);
                mEditTextTo1.setText("");
                mEditTextFrom2.setText("");
                mEditTextTo2.setText("");
                mEditTextValue2.setText("");
                mWrapperTo2.setErrorEnabled(false);
                mWrapperValue2.setErrorEnabled(false);
                mWrapperMoreThen.setErrorEnabled(false);
                mWrapperValue3.setErrorEnabled(false);

                if(mService != null) {
                    setHintsWithRateUnits();
                } else {
                    setSimpleRateHints();
                }

                checkAddButton();
            }
        });

        mButtonDeleteCustomRate3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mViewCustomRate3.setVisibility(View.GONE);
                mButtonDeleteCustomRate3.setVisibility(View.GONE);
                mEditTextTo2.setText("");
                mEditTextMoreThen.setText("");
                mEditTextValue3.setText("");
                mWrapperMoreThen.setErrorEnabled(false);
                mWrapperValue3.setErrorEnabled(false);

                if(mService != null) {
                    setHintsWithRateUnits();
                } else {
                    setSimpleRateHints();
                }

                checkAddButton();
            }
        });

        mButtonAddRate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mViewCustomRate2.isShown()) {
                    mViewCustomRate3.setVisibility(View.VISIBLE);
                    mButtonDeleteCustomRate3.setVisibility(View.VISIBLE);
                } else {
                    mViewCustomRate2.setVisibility(View.VISIBLE);
                    mButtonDeleteCustomRate2.setVisibility(View.VISIBLE);
                }

                try {
                    if(mEditTextFrom2.getText().toString().trim().length() == 0
                            && mEditTextTo1.getText().toString().trim().length() != 0) {
                        int from2 = Integer.parseInt(mEditTextTo1.getText().toString()) + 1;
                        mEditTextFrom2.setText(String.valueOf(from2));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                try {
                    if(mEditTextMoreThen.getText().toString().trim().length() == 0
                            && mEditTextTo2.getText().toString().trim().length() != 0) {
                        int moreThen = Integer.parseInt(mEditTextTo2.getText().toString()) + 1;
                        mEditTextMoreThen.setText(String.valueOf(moreThen));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                checkAddButton();
            }
        });


        buttonUpdateRate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mRate != null) {
                    mRealmHelper.beginTransaction();
                    mRate.setUpdateDate(new Date());
                    mRealmHelper.commitTransaction();

                    textDate.setText(DateFormat.format("dd-MM-yyyy  hh:mm",
                            mRate.getUpdateDate()).toString());


                    ServerConnectivity.getRateById(mRate.getId(), new Callback<Rate>() {
                        @Override
                        public void onResponse(Call<Rate> call, Response<Rate> response) {
//                            Rate rate = response.body();
//
//                            if(rate != null && rate.getRates() != null) {
//                                ///////
//                            }
                        }

                        @Override
                        public void onFailure(Call<Rate> call, Throwable t) {}
                    });
                }
            }
        });


        radioGroupTariffType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch(id) {
                    case R.id.radio_fixed_payment:
                        setFieldsForFixedPayments();

                        mButtonSave.setEnabled(true);
                        break;

                    case R.id.radio_rate_payment:
                        setFieldsForRatePayments();

                        if(mRate != null) {
                            if(mRate.isRateFromDb()) {
                                if(mRate.getRates() != null && mRate.getRates().size() > 0
                                        && mRate.getRates().get(0) != null
                                        && mRate.getRates().get(0).getPrice() > 0) {

                                    setFieldsForDbRates();
                                } else {
                                    setFieldsForNullDbRates();
                                }
                                mRadioButtonDbRates.setChecked(true);
                            }else {
                                setFieldsForCustomRates();
                                mRadioButtonCustomRates.setChecked(true);
                            }
                        } else {
                            mViewRateUpdate.setVisibility(View.GONE);
                            mViewAllCustomRates.setVisibility(View.GONE);
                        }

                        if(mRadioButtonCustomRates.isChecked()) {
                            mViewAllCustomRates.setVisibility(View.VISIBLE);

                            checkAddButton();
                        }

                        if(mRadioButtonDbRates.isChecked()) {
                            mViewRateUpdate.setVisibility(View.VISIBLE);
                        }

                        if(mRadioButtonCustomRates.isChecked()
                                || mRadioButtonDbRates.isChecked()) {
                            mButtonSave.setEnabled(true);
                        } else {
                            mButtonSave.setEnabled(false);
                        }

                        break;

                    default: break;
                }
            }
        });


        mRadioGroupRateFrom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch(id) {
                    case R.id.radio_db_rates:

                        if(mRate == null) {
                            setFieldsForNullDbRates();

                        } else {
                            if(mRate.getRates() != null && mRate.getRates().size() > 0
                                    && mRate.getRates().get(0) != null
                                    && mRate.getRates().get(0).getPrice() > 0) {

                                setFieldsForDbRates();
                            } else {
                                setFieldsForNullDbRates();
                            }
                        }
                        mButtonSave.setEnabled(true);

                        mWrapperTo1.setErrorEnabled(false);
                        mWrapperValue1.setErrorEnabled(false);
                        mWrapperFrom1.setErrorEnabled(false);
                        mWrapperTo2.setErrorEnabled(false);
                        mWrapperValue2.setErrorEnabled(false);
                        mWrapperMoreThen.setErrorEnabled(false);
                        mWrapperValue3.setErrorEnabled(false);

                        break;

                    case R.id.radio_custom_rates:
                        if(mRate != null) {
                            setFieldsForCustomRates();
                            setRestrictionsForCustomRates();
                        } else {
                            setFieldsForNullCustomRates();
                            setRestrictionsForCustomRates();
                        }

                        mButtonSave.setEnabled(true);

                        checkAddButton();

                        break;

                    default:
                        mViewRateUpdate.setVisibility(View.GONE);
                        mViewAllCustomRates.setVisibility(View.GONE);
                        mButtonSave.setEnabled(false);

                        break;
                }
            }
        });


        if(mIsDiscount.isChecked()) {
            mEditTextDiscount.setEnabled(true);
        } else {
            mEditTextDiscount.setEnabled(false);
            wrapperDiscount.clearFocus();
        }

        mIsDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mIsDiscount.isChecked()) {
                    mEditTextDiscount.setEnabled(true);

                } else {
                    mEditTextDiscount.setEnabled(false);
                }
            }
        });


        if(mRate != null) {
            if(mRate.isHasRate()) {

                setFieldsForRatePayments();
                mRadioButtonRate.setChecked(true);

                if(mRate.isRateFromDb()) {
                    setFieldsForDbRates();
                    mRadioButtonDbRates.setChecked(true);

                } else {
                    setFieldsForCustomRates();
                    checkAddButton();
                    mRadioButtonCustomRates.setChecked(true);
                }
            } else {
                setFieldsForFixedPayments();
                mRadioButtonFixed.setChecked(true);
            }
        } else {
            setFieldsForNullRate();
        }
        mEditTextFrom1.setEnabled(false);


        if(mService != null && mService.getRateUnit() != null) {
            setHintsWithRateUnits();
        } else {
            setSimpleRateHints();
        }


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                setResult(RESULT_CANCELED);
                safeExit();
            }
        });

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                initFields();

                if(mService == null) {
                    if(!checkForWarnings()) {

                        if(mRate == null) {
                            mRate = mRealmHelper.createObject(Rate.class);

                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_ON_RESULT_RATE_ID, mRate.getId());
                            setResult(RESULT_OK, intent);

                            saveTariffFields();

                            finish();

                        } else {
                            saveTariffFields();

                            finish();
                        }
                    }
                } else {
                    if(mRate == null) {
                        if(!checkForWarnings()) {
                            mRate = mRealmHelper.createObject(Rate.class);

                            mRealmHelper.beginTransaction();
                            mService.setRate(mRate);
                            mRealmHelper.commitTransaction();

                            saveTariffFields();

                            finish();
                        }

                    } else {
                        if(!checkForWarnings()) {
                            saveTariffFields();

                            finish();
                        }
                    }
                }
            }
        });
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
        checkCustomEditTexts();
        savedInstanceState.putBoolean(CUSTOM_2_VISIBILITY, mViewCustomRate2IsVisible);
        savedInstanceState.putBoolean(CUSTOM_3_VISIBILITY, mViewCustomRate3IsVisible);

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
        final DialogFragment dialogFragment = MyDialogFragment.newInstance(R.string.attention,
                R.string.warning_on_close);

        dialogFragment.setCancelable(true);

        dialogFragment.show(getSupportFragmentManager(), DIALOG_ON_CLOSE);
    }

    protected void checkAddButton() {
        if((mViewCustomRate3.getVisibility() == View.GONE
                && mViewCustomRate2.getVisibility() == View.GONE
                && mEditTextTo1.getText().toString().trim().length() != 0)
                ||
                (mViewCustomRate3.getVisibility() == View.GONE
                        && mViewCustomRate2.getVisibility() == View.VISIBLE
                        && mEditTextTo1.getText().toString().trim().length() != 0
                        && mEditTextTo2.getText().toString().trim().length() != 0)) {

            mButtonAddRate.setVisibility(View.VISIBLE);

        } else {
            mButtonAddRate.setVisibility(View.GONE);
        }
    }

    protected void checkCustomEditTexts() {
        mViewCustomRate2IsVisible = mEditTextFrom2.getText().toString().trim().length() != 0
                || mEditTextTo2.getText().toString().trim().length() != 0
                || mEditTextValue2.getText().toString().trim().length() != 0;
        mViewCustomRate3IsVisible = mEditTextMoreThen.getText().toString().trim().length() != 0
                || mEditTextValue3.getText().toString().trim().length() != 0;
    }

    protected void setFieldsForNullRate() {
        mButtonSave.setEnabled(false);
        mViewFeeAndExtraFee.setVisibility(View.GONE);
        mViewRateUnit.setVisibility(View.GONE);
        mTextViewRateFrom.setVisibility(View.GONE);
        mRadioGroupRateFrom.setVisibility(View.GONE);
        mViewAllCustomRates.setVisibility(View.GONE);
        mViewRateUpdate.setVisibility(View.GONE);
        mWrapperExtraFee2.setVisibility(View.GONE);
        mIsDiscount.setVisibility(View.GONE);
        mEditTextDiscount.setVisibility(View.GONE);
        mViewPaymentQuantity.setVisibility(View.GONE);
    }

    protected void setFieldsForFixedPayments() {
        mButtonSave.setEnabled(true);
        mViewFeeAndExtraFee.setVisibility(View.VISIBLE);
        mViewRateUnit.setVisibility(View.GONE);
        mTextViewRateFrom.setVisibility(View.GONE);
        mRadioGroupRateFrom.setVisibility(View.GONE);
        mViewAllCustomRates.setVisibility(View.GONE);
        mViewRateUpdate.setVisibility(View.GONE);
        mWrapperExtraFee2.setVisibility(View.GONE);
        mIsDiscount.setVisibility(View.VISIBLE);
        mEditTextDiscount.setVisibility(View.VISIBLE);
        mViewPaymentQuantity.setVisibility(View.VISIBLE);
    }

    protected void setFieldsForRatePayments() {
        mButtonSave.setEnabled(true);
        mViewFeeAndExtraFee.setVisibility(View.GONE);
        mViewRateUnit.setVisibility(View.VISIBLE);
        mTextViewRateFrom.setVisibility(View.VISIBLE);
        mRadioGroupRateFrom.setVisibility(View.VISIBLE);
        mViewAllCustomRates.setVisibility(View.VISIBLE);
        mViewRateUpdate.setVisibility(View.VISIBLE);
        mWrapperExtraFee2.setVisibility(View.VISIBLE);
        mIsDiscount.setVisibility(View.VISIBLE);
        mEditTextDiscount.setVisibility(View.VISIBLE);
        mViewPaymentQuantity.setVisibility(View.VISIBLE);
    }

    protected void setFieldsForNullCustomRates() {
        mViewRateUpdate.setVisibility(View.GONE);
        mViewAllCustomRates.setVisibility(View.VISIBLE);
        mViewCustomRate2.setVisibility(View.GONE);
        mViewCustomRate3.setVisibility(View.GONE);
        mEditTextTo1.setEnabled(true);
        mEditTextValue1.setEnabled(true);
        mButtonAddRate.setVisibility(View.GONE);
    }

    protected void setFieldsForCustomRates() {
        mViewRateUpdate.setVisibility(View.GONE);
        mViewAllCustomRates.setVisibility(View.VISIBLE);
        mEditTextTo1.setEnabled(true);
        mEditTextValue1.setEnabled(true);
        mEditTextFrom2.setEnabled(true);
        mEditTextTo2.setEnabled(true);
        mEditTextValue2.setEnabled(true);
        mEditTextMoreThen.setEnabled(true);
        mEditTextValue3.setEnabled(true);
        mButtonDeleteCustomRate2.setVisibility(View.VISIBLE);
        mButtonDeleteCustomRate3.setVisibility(View.VISIBLE);
    }

    protected void setFieldsForNullDbRates() {
        mViewRateUpdate.setVisibility(View.VISIBLE);
        mViewAllCustomRates.setVisibility(View.GONE);
    }

    protected void setFieldsForDbRates() {
        mViewRateUpdate.setVisibility(View.VISIBLE);
        mButtonDeleteCustomRate2.setVisibility(View.GONE);
        mButtonDeleteCustomRate3.setVisibility(View.GONE);
        mButtonAddRate.setVisibility(View.GONE);
        mEditTextTo1.setEnabled(false);
        mEditTextValue1.setEnabled(false);
        mEditTextFrom2.setEnabled(false);
        mEditTextTo2.setEnabled(false);
        mEditTextValue2.setEnabled(false);
        mEditTextMoreThen.setEnabled(false);
        mEditTextValue3.setEnabled(false);
        mEditTextTo1.clearFocus();
        mEditTextValue1.clearFocus();
        mEditTextFrom2.clearFocus();
        mEditTextTo2.clearFocus();
        mEditTextValue2.clearFocus();
        mEditTextMoreThen.clearFocus();
        mEditTextValue3.clearFocus();
    }

    protected void setHintsWithRateUnits() {
        if(mEditTextTo1.getText().toString().trim().length() == 0
                && mEditTextFrom2.getText().toString().trim().length() == 0 ) {
            mWrapperFrom1.setHint(getResources().getString(R.string.hint_more) + ", "
                    + mRateUnit);
            mWrapperTo1.setHint("");

        } else {
            mWrapperFrom1.setHint(getResources().getString(R.string.hint_from) + ", "
                    + mRateUnit);
            mWrapperTo1.setHint(getResources().getString(R.string.hint_to) + ", " + mRateUnit);

            if(mEditTextTo2.getText().toString().trim().length() == 0
                    && mEditTextMoreThen.getText().toString().trim().length() == 0 ) {
                mWrapperFrom2.setHint(getResources().getString(R.string.hint_more) + ", "
                        + mRateUnit);
                mWrapperTo2.setHint("");
            } else {
                mWrapperFrom2.setHint(getResources().getString(R.string.hint_from) + ", "
                        + mRateUnit);
                mWrapperTo2.setHint(getResources().getString(R.string.hint_to) + ", "
                        + mRateUnit);

                mWrapperMoreThen.setHint(getResources().getString(R.string.hint_more) + ", "
                        + mRateUnit);
            }
        }
    }

    protected void setSimpleRateHints() {
        if(mEditTextTo1.getText().toString().trim().length() == 0
                && mEditTextFrom2.getText().toString().trim().length() == 0 ) {
            mWrapperFrom1.setHint(getResources().getString(R.string.hint_more));
            mWrapperTo1.setHint("");
        } else {
            mWrapperFrom1.setHint(getResources().getString(R.string.hint_from));
            mWrapperTo1.setHint(getResources().getString(R.string.hint_to));

            if(mEditTextTo2.getText().toString().trim().length() == 0
                    && mEditTextMoreThen.getText().toString().trim().length() == 0 ) {
                mWrapperFrom2.setHint(getResources().getString(R.string.hint_more));
                mWrapperTo2.setHint("");
            } else {
                mWrapperFrom2.setHint(getResources().getString(R.string.hint_from));
                mWrapperTo2.setHint(getResources().getString(R.string.hint_to));

                mWrapperMoreThen.setHint(getResources().getString(R.string.hint_more));
            }
        }
    }

    protected void setRestrictionsForCustomRates() {
        if(mEditTextFrom1.getText().toString().trim().length() == 0) {
            mEditTextTo1.setEnabled(false);
            mEditTextValue1.setEnabled(false);
        }else {
            mEditTextTo1.setEnabled(true);
            mEditTextValue1.setEnabled(true);
        }

        if(mEditTextFrom2.getText().toString().trim().length() == 0) {
            mEditTextTo2.setEnabled(false);
            mEditTextValue2.setEnabled(false);
        }else {
            mEditTextTo2.setEnabled(true);
            mEditTextValue2.setEnabled(true);
        }

        if(mEditTextMoreThen.getText().toString().trim().length() == 0) {
            mEditTextValue3.setEnabled(false);
        }else {
            mEditTextValue3.setEnabled(true);
        }

        if(mEditTextTo1.getText().toString().trim().length() == 0) {
            mButtonAddRate.setVisibility(View.GONE);
            mEditTextFrom2.setEnabled(false);
            mEditTextTo2.setEnabled(false);
            mEditTextValue2.setEnabled(false);
            mEditTextMoreThen.setEnabled(false);
            mEditTextValue3.setEnabled(false);
        }else {
            mButtonAddRate.setVisibility(View.VISIBLE);
            mEditTextFrom2.setEnabled(true);
            mEditTextTo2.setEnabled(true);
            mEditTextValue2.setEnabled(true);
            mEditTextMoreThen.setEnabled(true);
            mEditTextValue3.setEnabled(true);
        }

        if(mViewCustomRate2.getVisibility() == View.VISIBLE) {
            if(mEditTextTo2.getText().toString().trim().length() == 0) {
                mButtonAddRate.setVisibility(View.GONE);
                mEditTextMoreThen.setEnabled(false);
                mEditTextValue3.setEnabled(false);
            }else {
                mButtonAddRate.setVisibility(View.VISIBLE);
                mEditTextMoreThen.setEnabled(true);
                mEditTextValue3.setEnabled(true);
            }
        }
    }


    private void initFields() {
        mFee = mEditTextFee.getText().toString().trim();
        try {
            mFeeFloat = Float.parseFloat(mFee);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mExtraFee = mEditTextExtraFee.getText().toString().trim();
        try {
            mExtraFeeFloat = Float.parseFloat(mExtraFee);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mExtraFee2 = mEditTextExtraFee2.getText().toString().trim();
        try {
            mExtraFee2Float = Float.parseFloat(mExtraFee2);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mTo1 = mEditTextTo1.getText().toString().trim();
        try {
            mTo1Int = Integer.parseInt(mTo1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mValue1 = mEditTextValue1.getText().toString().trim();
        try {
            mValue1Float = Float.parseFloat(mValue1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mFrom2 = mEditTextFrom2.getText().toString().trim();
        try {
            mFrom2Int = Integer.parseInt(mFrom2);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mTo2 = mEditTextTo2.getText().toString().trim();
        try {
            mTo2Int = Integer.parseInt(mTo2);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mValue2 = mEditTextValue2.getText().toString().trim();
        try {
            mValue2Float = Float.parseFloat(mValue2);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mMoreThen = mEditTextMoreThen.getText().toString().trim();
        try {
            mMoreThenInt = Integer.parseInt(mMoreThen);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mValue3 = mEditTextValue3.getText().toString().trim();
        try {
            mValue3Float = Float.parseFloat(mValue3);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mDiscount = mEditTextDiscount.getText().toString().trim();
        try {
            mDiscountInt = Integer.parseInt(mDiscount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mPaymentFrequency = mEditTextPaymentFrequency.getText().toString().trim();
        try {
            mPaymentFrequencyInt = Integer.parseInt(mPaymentFrequency);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mSelectedSpinner = mTimeUnits.getSelectedItem().toString();
    }

    private boolean checkForWarnings() {
        boolean warnings = false;

        initFields();

        if(mRadioButtonFixed.isChecked()) {
            if(!mExtraFee.equals("")) {
                if(mFeeFloat <= 0) {
                    mWrapperExtraFee.setError(getString(R.string.warning_extra_fee));

                    warnings = true;
                    return warnings;

                } else {
                    mWrapperExtraFee.setErrorEnabled(false);
                }
            } else {
                mWrapperExtraFee.setErrorEnabled(false);
            }

        }else if(mRadioButtonRate.isChecked()) {
            if(mRadioButtonCustomRates.isChecked()) {
                if(mValue1Float <= 0) {
                    mWrapperValue1.setError(getString(R.string.warning_0));

                    warnings = true;
                    return warnings;

                } else {
                    mWrapperValue1.setErrorEnabled(false);
                }

                if(mTo1Int > 1) {
                    if(mViewCustomRate2.getVisibility() == View.VISIBLE) {

                        if((mTo1.equals("") || mTo1Int == 0) && mFrom2Int > 0) {
                            mWrapperTo1.setError(getString(R.string.warning_0));

                            warnings = true;
                            return warnings;

                        } else {
                            mWrapperTo1.setErrorEnabled(false);
                        }

                        if(mFrom2Int < mTo1Int) {
                            mWrapperFrom2.setError(getString(R.string
                                    .warning_less_then_prev_to));

                            warnings = true;
                            return warnings;

                        } else {
                            mWrapperFrom2.setErrorEnabled(false);
                        }

                        if(!mFrom2.equals("") && !mValue2.equals("")) {
                            if(mFrom2Int > 0) {
                                if(mFrom2Int > mTo1Int) {
                                    mWrapperFrom2.setErrorEnabled(false);

                                } else {
                                    mWrapperFrom2.setError(getString(R.string
                                            .warning_less_then_prev_to));

                                    warnings = true;
                                    return warnings;

                                }
                            } else {
                                mWrapperFrom2.setError(getString(R.string.warning_0));

                                warnings = true;
                                return warnings;
                            }

                            if(mFrom2Int > 0) {
                                if(mValue2Float <= 0) {
                                    mWrapperValue2.setError(getString(R.string.warning_0));

                                    warnings = true;
                                    return warnings;

                                } else {
                                    mWrapperValue2.setErrorEnabled(false);
                                }
                            } else {
                                mWrapperValue2
                                        .setError(getString(R.string.warning_absent_from));

                                warnings = true;
                                return warnings;
                            }

                            if((mTo2.equals("") || mTo2Int == 0) && mMoreThenInt > 0) {
                                mWrapperTo2.setError(getString(R.string.warning_0));

                                warnings = true;
                                return warnings;

                            } else {
                                mWrapperTo2.setErrorEnabled(false);
                            }

                            if(!mFrom2.equals("") && !mTo2.equals("") && !mValue2.equals("")
                                    && (mFrom2Int > mTo2Int)) {
                                mWrapperTo2.setError(getString(R.string
                                        .warning_to_more_then_from));

                                warnings = true;
                                return warnings;

                            } else {
                                mWrapperTo2.setErrorEnabled(false);
                            }

                            if(mViewCustomRate3.getVisibility() == View.VISIBLE) {
                                if(mMoreThenInt > 0) {
                                    mWrapperMoreThen.setErrorEnabled(false);
                                } else {
                                    mWrapperMoreThen.setError(getString(R.string.warning_0));

                                    warnings = true;
                                    return warnings;
                                }

                                if(mMoreThenInt > mTo2Int) {
                                    mWrapperMoreThen.setErrorEnabled(false);
                                } else {
                                    mWrapperMoreThen.setError(getString(R.string
                                            .warning_less_then_prev_to));

                                    warnings = true;
                                    return warnings;
                                }

                                if(mValue3Float > 0) {
                                    mWrapperValue3.setErrorEnabled(false);
                                } else {
                                    mWrapperValue3.setError(getString(R.string.warning_0));

                                    warnings = true;
                                    return warnings;

                                }
                            } else {
                                mWrapperMoreThen.setErrorEnabled(false);
                                mWrapperValue3.setErrorEnabled(false);
                            }
                        }
                    }
                }
            }
        }
        return warnings;
    }


    private void saveTariffFields() {
        initFields();

        if(mIsDiscount.isChecked() && !mDiscount.equals("") && mDiscountInt <= 100) {
            mRealmHelper.beginTransaction();
            mRate.setDiscount(mDiscountInt);
            mRealmHelper.commitTransaction();
        }else {
            mRealmHelper.beginTransaction();
            mRate.setDiscount(0);
            mRealmHelper.commitTransaction();
        }

        if(!mPaymentFrequency.equals("") && mPaymentFrequencyInt <= 366) {
            mRealmHelper.beginTransaction();
            mRate.setAmountOfPayments(mPaymentFrequencyInt);
            mRealmHelper.commitTransaction();
        }

        mRealmHelper.beginTransaction();
        switch(mSelectedSpinner) {
            case "month":
                mRate.setPeriod(Rate.Period.MONTH);
                break;
            case "day":
                mRate.setPeriod(Rate.Period.DAY);
                break;
            case "week":
                mRate.setPeriod(Rate.Period.WEEK);
                break;

            default:
                mRate.setPeriod(Rate.Period.MONTH);
        }
        mRealmHelper.commitTransaction();


        if(mRadioButtonFixed.isChecked()) {

            mRealmHelper.beginTransaction();
            mRate.setHasRate(false);
            mRealmHelper.commitTransaction();

            if(!mFee.equals("")) {
                mRealmHelper.beginTransaction();
                mRate.setFixedPayment(mFeeFloat);
                mRealmHelper.commitTransaction();
            }

            if(!mExtraFee.equals("")) {
                mRealmHelper.beginTransaction();
                mRate.setExtraPayment(mExtraFeeFloat);
                mRealmHelper.commitTransaction();
            }

        }else if(mRadioButtonRate.isChecked()) {
            if(!mExtraFee2.equals("")) {
                mRealmHelper.beginTransaction();
                mRate.setExtraPayment(mExtraFee2Float);
                mRealmHelper.commitTransaction();
            }

            mRealmHelper.beginTransaction();
            mRate.setHasRate(true);
            mRealmHelper.commitTransaction();

            if(mRadioButtonCustomRates.isChecked()) {
                mRealmHelper.beginTransaction();
                mRate.getRates().clear();
                mRealmHelper.commitTransaction();

                ConsumptionInterval consumptionInterval0 = null;
                ConsumptionInterval consumptionInterval1 = null;
                ConsumptionInterval consumptionInterval2 = null;

                if(mValue1Float > 0) {
                    consumptionInterval0 = mRealmHelper.createObject(ConsumptionInterval.class);
                }
                if(mValue2Float > 0) {
                    consumptionInterval1 = mRealmHelper.createObject(ConsumptionInterval.class);
                }
                if(mValue3Float > 0) {
                    consumptionInterval2 = mRealmHelper.createObject(ConsumptionInterval.class);
                }

                mRealmHelper.beginTransaction();
                if(mValue1Float > 0) {
                    consumptionInterval0.setIntervalFrom(1);
                    consumptionInterval0.setPrice(mValue1Float);
                }

                if(mViewCustomRate2.getVisibility() == View.VISIBLE) {
                    if((mTo1Int > 1) && consumptionInterval0 != null) {
                        consumptionInterval0.setIntervalTo(mTo1Int);
                    }

                    if((mFrom2Int > mTo1Int) && (mValue2Float > 0)
                            && consumptionInterval1 != null) {

                        consumptionInterval1.setIntervalFrom(mFrom2Int);
                        consumptionInterval1.setPrice(mValue2Float);

                        if(mViewCustomRate3.getVisibility() == View.VISIBLE) {
                            if((mTo2Int > mFrom2Int) && consumptionInterval1 != null) {
                                consumptionInterval1.setIntervalTo(mTo2Int);
                            }

                            if((mMoreThenInt > mTo2Int) && (mValue3Float > 0)
                                    && consumptionInterval2 != null) {
                                consumptionInterval2.setIntervalFrom(mMoreThenInt);
                                consumptionInterval2.setPrice(mValue3Float);
                            }
                        }
                    }
                }
                mRealmHelper.commitTransaction();


                mRealmHelper.beginTransaction();
                mRate.setRateFromDb(false);

                if(consumptionInterval0 != null) {
                    mRate.getRates().add(0, consumptionInterval0);
                }

                if(consumptionInterval1 != null) {
                    mRate.getRates().add(1, consumptionInterval1);
                }

                if(consumptionInterval2 != null) {
                    mRate.getRates().add(2, consumptionInterval2);
                }
                mRealmHelper.commitTransaction();

            } else if(mRadioButtonDbRates.isChecked()) {
                mRealmHelper.beginTransaction();
                mRate.setRateFromDb(true);
                mRealmHelper.commitTransaction();
            }
        }
    }
}