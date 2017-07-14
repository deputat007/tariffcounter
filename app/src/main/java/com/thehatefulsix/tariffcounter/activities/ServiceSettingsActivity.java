package com.thehatefulsix.tariffcounter.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.models.Rate;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.models.ServiceProvider;
import com.thehatefulsix.tariffcounter.models.Subsidy;
import com.thehatefulsix.tariffcounter.utils.MyDialogFragment;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.ServiceController;

import java.util.Currency;
import java.util.Locale;


public class ServiceSettingsActivity extends    AppCompatActivity
                                     implements MyDialogFragment.OnClickListener {

    public static final String SERVICE_EXTRA_ID =
            "com.thehatefulsix.tariffcounter.service_extra_id";

    public static final String HOUSE_EXTRA_ID =
            "com.thehatefulsix.tariffcounter.house_extra_id";

    private static final String DIALOG_ON_CLOSE =
            "com.thehatefulsix.tariffcounter.service_settings_dialog_on_close";

    private static final String STATE_PROVIDER = "com.thehatefulsix.tariffcounter.state_provider";

    private static final String STATE_RATE = "com.thehatefulsix.tariffcounter.state_rate";

    private static final String STATE_SUBSIDY = "com.thehatefulsix.tariffcounter.state_subsidy";

    private static final int REQUEST_SERVICE_PROVIDER = 303;
    private static final int REQUEST_TARIFF_SETTINGS = 302;
    private static final int REQUEST_SUBSIDY_SETTINGS = 301;

    private Service mService;
    private House mHouse;

    private ServiceProvider mServiceProvider;
    private Rate mRate;
    private Subsidy mSubsidy;

    private RealmHelper mRealmHelper;

    private Spinner mServiceTypeSpinner;

    private TextInputLayout mWrapperServiceTitle;
    private TextInputEditText mEditTextServiceTitle;

    private LinearLayout mViewRateUnit;
    private Spinner mRateUnitSpinner;

    private LinearLayout mViewProvider;
    private TextInputEditText mEditTextBalance;

    private LinearLayout mViewTariffs;
    private TextInputEditText mEditTextCounter;

    private LinearLayout mViewSubsidy;
    private CheckBox mIsSubsidy;
    private Button mSubsidySettingsButton;

    private ImageButton mDeleteServiceButton;

    private Button mSaveButton;


    public static Intent newIntent(Context packageContext, @Nullable String serviceId,
                                   String houseId) {
        Intent i = new Intent(packageContext, ServiceSettingsActivity.class);
        i.putExtra(SERVICE_EXTRA_ID, serviceId);
        i.putExtra(HOUSE_EXTRA_ID, houseId);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_PROVIDER)) {
            String providerId = (String) savedInstanceState.getSerializable(STATE_PROVIDER);
            mServiceProvider = RealmHelper.getInstance()
                    .getObjectById(ServiceProvider.class, providerId);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_RATE)) {
            String rateId = (String) savedInstanceState.getSerializable(STATE_RATE);
            mRate = RealmHelper.getInstance().getObjectById(Rate.class, rateId);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SUBSIDY)) {
            String subsidyId = (String) savedInstanceState.getSerializable(STATE_SUBSIDY);
            mSubsidy = RealmHelper.getInstance().getObjectById(Subsidy.class, subsidyId);
        }


        mRealmHelper = RealmHelper.getInstance();

        if (getIntent().getStringExtra(SERVICE_EXTRA_ID) != null) {
            String serviceId = getIntent().getStringExtra(SERVICE_EXTRA_ID);
            mService = RealmHelper.getInstance().getObjectById(Service.class, serviceId);

        } else {
            mService = null;
        }

        if (getIntent().getStringExtra(HOUSE_EXTRA_ID) != null) {
            String houseId = getIntent().getStringExtra(HOUSE_EXTRA_ID);
            mHouse = RealmHelper.getInstance().getObjectById(House.class, houseId);
        }

        String rateUnit = "";
        if(mService != null && mService.getRateUnit() != null) {
            rateUnit = ServiceController.rateUnitGetString(mService.getRateUnit(),
                    getApplicationContext());
        }

        String currencySymbol = Currency.getInstance(Locale.getDefault()).getSymbol();


        TextView necessaryFields = (TextView) findViewById(R.id.text_view_service_necessary);
        LinearLayout viewServiceType = (LinearLayout) findViewById(R.id.view_service_type);
        TextView serviceType = (TextView) findViewById(R.id.text_view_service_type);
        mServiceTypeSpinner = (Spinner) findViewById(R.id.spinner_service_type);

        mWrapperServiceTitle = (TextInputLayout) findViewById(R.id.wrapper_service_title);
        mEditTextServiceTitle = (TextInputEditText) findViewById(R.id.edit_text_service_title);

        mViewRateUnit = (LinearLayout) findViewById(R.id.view_rate_unit);
        mRateUnitSpinner = (Spinner) findViewById(R.id.spinner_unit_type);

        mViewProvider = (LinearLayout) findViewById(R.id.service_provider_view);
        TextInputLayout wrapperBalance = (TextInputLayout) findViewById(R.id.wrapper_balance);
        mEditTextBalance = (TextInputEditText) findViewById(R.id.edit_text_balance);
        final Button serviceProviderSettingsButton = (Button)
                findViewById(R.id.button_service_provider);

        mViewTariffs = (LinearLayout) findViewById(R.id.view_tariffs);
        TextInputLayout wrapperCounter = (TextInputLayout) findViewById(R.id.wrapper_counter);
        mEditTextCounter = (TextInputEditText) findViewById(R.id.edit_text_counter);
        final Button tariffSettingsButton = (Button)
                findViewById(R.id.button_tariff_settings);

        mViewSubsidy = (LinearLayout) findViewById(R.id.view_subsidy);
        mIsSubsidy = (CheckBox) findViewById(R.id.checkbox_subsidy_granted);
        mSubsidySettingsButton = (Button) findViewById(R.id.button_subsidy_settings);

        mDeleteServiceButton = (ImageButton) findViewById(R.id.button_service_delete);

        mSaveButton = (Button) findViewById(R.id.button_service_save);
        final Button cancelButton = (Button)
                findViewById(R.id.button_service_cancel);


        serviceProviderSettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mService != null) {
                    if(mService.getServiceProvider() != null) {
                        Intent intent = ServiceProviderActivity
                                .newIntent(ServiceSettingsActivity.this, mHouse.getId(),
                                        mService.getId(), mService.getServiceProvider().getId());
                        startActivity(intent);

                    } else {
                        Intent intent = ServiceProviderActivity
                                .newIntent(ServiceSettingsActivity.this, mHouse.getId(),
                                        mService.getId(), null);
                        startActivity(intent);
                    }

                } else {
                    if(mServiceProvider != null) {
                        Intent intent = ServiceProviderActivity
                                .newIntent(ServiceSettingsActivity.this, mHouse.getId(), null,
                                        mServiceProvider.getId());
                        startActivity(intent);

                    } else {
                        startActivityForResult(ServiceProviderActivity
                                        .newIntent(ServiceSettingsActivity.this, mHouse.getId(),
                                                null, null),
                                REQUEST_SERVICE_PROVIDER);
                    }
                }
            }
        });

        tariffSettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mService != null) {
                    if(mService.getRate() != null) {
                        Intent intent = TariffSettingsActivity
                                .newIntent(ServiceSettingsActivity.this, mService.getId(),
                                        mService.getRate().getId());
                        startActivity(intent);

                    } else {
                        Intent intent = TariffSettingsActivity
                                .newIntent(ServiceSettingsActivity.this, mService.getId(), null);
                        startActivity(intent);
                    }

                } else {
                    if(mRate != null) {
                        Intent intent = TariffSettingsActivity
                                .newIntent(ServiceSettingsActivity.this, null, mRate.getId());
                        startActivity(intent);

                    } else {
                        startActivityForResult(TariffSettingsActivity
                                        .newIntent(ServiceSettingsActivity.this, null, null),
                                REQUEST_TARIFF_SETTINGS);
                    }
                }
            }
        });

        mSubsidySettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mService != null) {
                    if(mService.getSubsidy() != null) {
                        Intent intent = SubsidySettingsActivity
                                .newIntent(ServiceSettingsActivity.this, mService.getId(),
                                        mService.getSubsidy().getId());
                        startActivity(intent);

                    } else {
                        Intent intent = SubsidySettingsActivity
                                .newIntent(ServiceSettingsActivity.this, mService.getId(), null);
                        startActivity(intent);
                    }

                } else {
                    if(mSubsidy != null) {
                        Intent intent = SubsidySettingsActivity
                                .newIntent(ServiceSettingsActivity.this, null, mSubsidy.getId());
                        startActivity(intent);

                    } else {
                        startActivityForResult(SubsidySettingsActivity
                                        .newIntent(ServiceSettingsActivity.this, null, null),
                                REQUEST_SUBSIDY_SETTINGS);
                    }
                }
            }
        });

        mDeleteServiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mHouse != null && mService != null){

                    mRealmHelper.delete(mService);

                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                setResult(RESULT_CANCELED);

                safeExit();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mService == null) {
                    if(mRate != null) {
                        if(mIsSubsidy.isChecked() && mSubsidy == null) {
                            showSnackBar(getResources().getString(R.string
                                    .warning_subsidy_absent));

                        } else {
                            mService = mRealmHelper.createObject(Service.class);

                            mRealmHelper.beginTransaction();
                            mHouse.getServices().add(mService);
                            mRealmHelper.commitTransaction();

                            if(mServiceProvider != null) {
                                mRealmHelper.beginTransaction();
                                mService.setServiceProvider(mServiceProvider);
                                mRealmHelper.commitTransaction();
                            }

                            mRealmHelper.beginTransaction();
                            mService.setRate(mRate);
                            mRealmHelper.commitTransaction();

                            if(mIsSubsidy.isChecked() && mSubsidy != null) {
                                mRealmHelper.beginTransaction();
                                mService.setSubsidy(mSubsidy);
                                mRealmHelper.commitTransaction();
                            }

                            saveServiceFields();
                            setResult(RESULT_OK);

                            finish();
                        }
                    } else {
                        showSnackBar(getResources().getString(R.string.warning_tariff_absent));
                    }
                } else {
                    if(checkFieldsForChanges()) {

                        saveServiceFields();
                        setResult(RESULT_OK);

                        finish();

                    } else {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
            }
        });


        ArrayAdapter<?> adapterServiceType = ArrayAdapter.createFromResource(this,
                R.array.service_type_spinner_array,
                android.R.layout.simple_spinner_item);
        adapterServiceType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mServiceTypeSpinner.setAdapter(adapterServiceType);

        ArrayAdapter<?> adapterUnitType = ArrayAdapter.createFromResource(this,
                R.array.rate_unit_spinner_array,
                android.R.layout.simple_spinner_item);
        adapterUnitType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRateUnitSpinner.setAdapter(adapterUnitType);

        if(mService != null && mService.getRateUnit() != null) {
            wrapperCounter.setHint(getResources().getString(R.string.hint_counter) + ", "
                    + rateUnit);
        } else {
            wrapperCounter.setHint(getResources().getString(R.string.hint_counter));
        }

        wrapperBalance.setHint(getResources()
                .getString(R.string.hint_service_provider_balance) + ", " + currencySymbol);

        mIsSubsidy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mIsSubsidy.isChecked()) {
                    mSubsidySettingsButton.setEnabled(true);
                } else {
                    mSubsidySettingsButton.setEnabled(false);
                }
            }
        });


        if(mService != null) {
            necessaryFields.setVisibility(View.GONE);
            serviceType.setText(getResources().getString(R.string.text_view_service_type));

            mServiceTypeSpinner.setEnabled(false);
            mRateUnitSpinner.setEnabled(false);

            if(mService.getType() == Service.Type.OTHER) {
                mWrapperServiceTitle.setVisibility(View.VISIBLE);
                mEditTextServiceTitle.setVisibility(View.VISIBLE);

                if(mService.getName() != null) {
                    mEditTextServiceTitle.setText(mService.getName());

                    viewServiceType.setVisibility(View.GONE);
                } else {
                    viewServiceType.setVisibility(View.VISIBLE);
                }

            } else {
                mWrapperServiceTitle.setVisibility(View.GONE);
                mEditTextServiceTitle.setVisibility(View.GONE);
            }

            if(mService.getType() != null) {
                switch(mService.getType()) {
                    case OTHER: mServiceTypeSpinner.setSelection(1);
                        break;
                    case ELECTRICITY_SUPPLY: mServiceTypeSpinner.setSelection(2);
                        break;
                    case GAS_SUPPLY: mServiceTypeSpinner.setSelection(3);
                        break;
                    case RENT: mServiceTypeSpinner.setSelection(4);
                        break;
                    case HEATING: mServiceTypeSpinner.setSelection(5);
                        break;
                    case HOT_WATER: mServiceTypeSpinner.setSelection(6);
                        break;
                    case WATER_SUPPLY: mServiceTypeSpinner.setSelection(7);
                        break;
                    case WATER_DRAINAGE: mServiceTypeSpinner.setSelection(8);
                        break;
                    case GARBAGE_COLLECTION: mServiceTypeSpinner.setSelection(9);
                        break;
                    default: break;
                }
            }

            if(mService.getRateUnit() != null) {
                switch(mService.getRateUnit()) {
                    case NULL: mRateUnitSpinner.setSelection(0);
                        break;
                    case M2: mRateUnitSpinner.setSelection(1);
                        break;
                    case M3: mRateUnitSpinner.setSelection(2);
                        break;
                    case KWH: mRateUnitSpinner.setSelection(3);
                        break;
                    case GCAL: mRateUnitSpinner.setSelection(4);
                        break;
                    default: mRateUnitSpinner.setSelection(0);
                        break;
                }
            }

            if(mService.getCounter() != 0) {
                mEditTextCounter.setText(String.valueOf(mService.getCounter()));
            }

            updateBalance();

            if(mService.isSubsidy()) {
                mSubsidySettingsButton.setEnabled(true);
                mIsSubsidy.setChecked(true);

            } else {
                mSubsidySettingsButton.setEnabled(false);
                mIsSubsidy.setChecked(false);
            }

        } else {
            necessaryFields.setVisibility(View.VISIBLE);
            serviceType.setText(getResources().getString(R.string.text_view_service_type_null));

            setFieldsForNullService();
            mSubsidySettingsButton.setEnabled(false);

            mServiceTypeSpinner.setOnItemSelectedListener(new AdapterView
                    .OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                           int position, long id) {

                    if(mServiceTypeSpinner.getSelectedItemPosition() == 1) {
                        mRateUnitSpinner.setSelection(0);
                        mRateUnitSpinner.setEnabled(true);
                        mWrapperServiceTitle.setVisibility(View.VISIBLE);
                        mEditTextServiceTitle.setVisibility(View.VISIBLE);
                        mViewRateUnit.setVisibility(View.VISIBLE);
                        mViewProvider.setVisibility(View.VISIBLE);
                        mViewTariffs.setVisibility(View.VISIBLE);
                        mViewSubsidy.setVisibility(View.VISIBLE);
                        mSaveButton.setEnabled(true);

                    } else if(mServiceTypeSpinner.getSelectedItemPosition() == 2) {
                        mRateUnitSpinner.setSelection(3);
                        onSelectedServiceType();

                    } else if(mServiceTypeSpinner.getSelectedItemPosition() == 3) {
                        mRateUnitSpinner.setSelection(2);
                        onSelectedServiceType();

                    } else if(mServiceTypeSpinner.getSelectedItemPosition() == 4) {
                        mRateUnitSpinner.setSelection(1);
                        onSelectedServiceType();

                    } else if(mServiceTypeSpinner.getSelectedItemPosition() == 5) {
                        mRateUnitSpinner.setSelection(4);
                        onSelectedServiceType();

                    } else if(mServiceTypeSpinner.getSelectedItemPosition() == 6) {
                        mRateUnitSpinner.setSelection(2);
                        onSelectedServiceType();

                    } else if(mServiceTypeSpinner.getSelectedItemPosition() == 7) {
                        mRateUnitSpinner.setSelection(2);
                        onSelectedServiceType();

                    } else if(mServiceTypeSpinner.getSelectedItemPosition() == 8) {
                        mRateUnitSpinner.setSelection(2);
                        onSelectedServiceType();

                    } else if(mServiceTypeSpinner.getSelectedItemPosition() == 9) {
                        mRateUnitSpinner.setSelection(1);
                        onSelectedServiceType();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parentView) {}
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRealmHelper.initRealm();

        updateBalance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealmHelper.closeRealm();
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
            cleanInstances();
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        if(mServiceProvider != null) {
            savedInstanceState.putSerializable(STATE_PROVIDER, mServiceProvider.getId());
        }
        if(mRate != null) {
            savedInstanceState.putSerializable(STATE_RATE, mRate.getId());
        }
        if(mSubsidy != null) {
            savedInstanceState.putSerializable(STATE_SUBSIDY, mSubsidy.getId());
        }

        super.onSaveInstanceState(savedInstanceState);
    }


    private void safeExit() {
        if(checkFieldsForChanges()) {
            final DialogFragment dialogFragment = MyDialogFragment
                    .newInstance(R.string.attention, R.string.warning_on_close);

            dialogFragment.setCancelable(true);

            dialogFragment.show(getSupportFragmentManager(), DIALOG_ON_CLOSE);

        } else {
            cleanInstances();
            finish();
        }
    }

    private void showSnackBar(final String message) {
        Snackbar.make(findViewById(R.id.scroll_view_service_settings), message,
                Snackbar.LENGTH_LONG).show();
    }

    protected void setFieldsForNullService() {
        mSaveButton.setEnabled(false);
        mWrapperServiceTitle.setVisibility(View.GONE);
        mEditTextServiceTitle.setVisibility(View.GONE);
        mViewRateUnit.setVisibility(View.GONE);
        mViewProvider.setVisibility(View.GONE);
        mViewTariffs.setVisibility(View.GONE);
        mViewSubsidy.setVisibility(View.GONE);
        mDeleteServiceButton.setVisibility(View.GONE);
    }

    protected void onSelectedServiceType() {
        mSaveButton.setEnabled(true);
        mWrapperServiceTitle.setVisibility(View.GONE);
        mEditTextServiceTitle.setVisibility(View.GONE);
        mViewRateUnit.setVisibility(View.VISIBLE);
        mViewProvider.setVisibility(View.VISIBLE);
        mViewTariffs.setVisibility(View.VISIBLE);
        mViewSubsidy.setVisibility(View.VISIBLE);
        mRateUnitSpinner.setEnabled(false);
    }

    protected void updateBalance() {
        if(mService != null && mService.getBalance() != 0) {
            mEditTextBalance.setText(String.valueOf(mService.getBalance()));
        }
    }

    private void cleanInstances() {
        if(mService == null) {
            if(mServiceProvider != null) {
                mRealmHelper.delete(mServiceProvider);
            }

            if(mRate != null) {
                mRealmHelper.delete(mRate);
            }

            if(mSubsidy != null) {
                mRealmHelper.delete(mSubsidy);
            }
        }
    }


    private boolean checkFieldsForChanges() {
        boolean changed = false;

        String balance = mEditTextBalance.getText().toString().trim();
        float balanceFloat = 0;
        try {
            balanceFloat = Float.parseFloat(balance);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String counter = mEditTextCounter.getText().toString().trim();
        float counterFloat = 0;
        try {
            counterFloat = Float.parseFloat(counter);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(mService != null) {
            if(mService.getBalance() != balanceFloat) {

                changed = true;
                return changed;
            }

            if(mService.getCounter() != counterFloat) {

                changed = true;
                return changed;
            }

            if(mService.isSubsidy() != mIsSubsidy.isChecked()) {

                changed = true;
                return changed;
            }

        } else {
            return true;
        }
        return changed;
    }


    protected void saveServiceFields() {
        int selectedServiceType = mServiceTypeSpinner.getSelectedItemPosition();
        int selectedRateUnit = mRateUnitSpinner.getSelectedItemPosition();

        String title = null;
        if(mEditTextServiceTitle.getVisibility() == View.VISIBLE) {
            title = mEditTextServiceTitle.getText().toString().trim();
        }

        mRealmHelper.beginTransaction();
        switch(selectedServiceType) {
            case 1:
                mService.setType(Service.Type.OTHER);
                mService.setIcon(R.drawable.ic_other_service);
                break;
            case 2:
                mService.setType(Service.Type.ELECTRICITY_SUPPLY);
                mService.setIcon(R.drawable.ic_electricity_supply);
                break;
            case 3:
                mService.setType(Service.Type.GAS_SUPPLY);
                mService.setIcon(R.drawable.ic_gas_supply);
                break;
            case 4:
                mService.setType(Service.Type.RENT);
                mService.setIcon(R.drawable.ic_rent);
                break;
            case 5:
                mService.setType(Service.Type.HEATING);
                mService.setIcon(R.drawable.ic_heating);
                break;
            case 6:
                mService.setType(Service.Type.HOT_WATER);
                mService.setIcon(R.drawable.ic_hot_water);
                break;
            case 7:
                mService.setType(Service.Type.WATER_SUPPLY);
                mService.setIcon(R.drawable.ic_water);
                break;
            case 8:
                mService.setType(Service.Type.WATER_DRAINAGE);
                mService.setIcon(R.drawable.ic_water_drainage);
                break;
            case 9:
                mService.setType(Service.Type.GARBAGE_COLLECTION);
                mService.setIcon(R.drawable.ic_garbage_collection);
                break;
            default: break;
        }

        switch(selectedRateUnit) {
            case 0: mService.setRateUnit(Service.RateUnit.NULL);
                break;
            case 1: mService.setRateUnit(Service.RateUnit.M2);
                break;
            case 2: mService.setRateUnit(Service.RateUnit.M3);
                break;
            case 3: mService.setRateUnit(Service.RateUnit.KWH);
                break;
            case 4: mService.setRateUnit(Service.RateUnit.GCAL);
                break;
            default: break;
        }

        if(title != null) {
            mService.setName(title);
        }
        mRealmHelper.commitTransaction();

        String balance = mEditTextBalance.getText().toString().trim();
        float balanceFloat = 0;
        try {
            balanceFloat = Float.parseFloat(balance);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String counter = mEditTextCounter.getText().toString().trim();
        float counterFloat = 0;
        try {
            counterFloat = Float.parseFloat(counter);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mRealmHelper.beginTransaction();
        if(balanceFloat != -0) {
            mService.setBalance(balanceFloat);
        }
        mService.setCounter(counterFloat);
        mRealmHelper.commitTransaction();

        if(mIsSubsidy.isChecked()) {
            mRealmHelper.beginTransaction();
            mService.setIsSubsidy(true);
            mRealmHelper.commitTransaction();
        }else {
            mRealmHelper.beginTransaction();
            mService.setIsSubsidy(false);
            mRealmHelper.commitTransaction();
        }

        if(mHouse != null && mHouse.getId() != null) {
            mRealmHelper.beginTransaction();
            mService.setIdHouse(mHouse.getId());
            mRealmHelper.commitTransaction();
        }

        setResult(RESULT_OK);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) {
            return;
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SERVICE_PROVIDER) {
            String providerId = (String) data.getSerializableExtra(ServiceProviderActivity
                    .EXTRA_ON_RESULT_SERVICE_PROVIDER_ID);

            mServiceProvider = RealmHelper.getInstance()
                    .getObjectById(ServiceProvider.class, providerId);
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TARIFF_SETTINGS) {
            String rateId = (String) data.getSerializableExtra(TariffSettingsActivity
                    .EXTRA_ON_RESULT_RATE_ID);

            mRate = RealmHelper.getInstance().getObjectById(Rate.class, rateId);
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SUBSIDY_SETTINGS) {
            String subsidyId = (String) data.getSerializableExtra(SubsidySettingsActivity
                    .EXTRA_ON_RESULT_SUBSIDY_ID);

            mSubsidy = RealmHelper.getInstance().getObjectById(Subsidy.class, subsidyId);
        }
    }
}