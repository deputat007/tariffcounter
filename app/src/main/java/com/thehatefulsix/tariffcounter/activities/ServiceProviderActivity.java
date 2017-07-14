package com.thehatefulsix.tariffcounter.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.models.ServiceProvider;
import com.thehatefulsix.tariffcounter.utils.MyDialogFragment;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;

import java.util.Currency;
import java.util.Locale;


public class ServiceProviderActivity extends    AppCompatActivity
                                     implements MyDialogFragment.OnClickListener {

    public static final String EXTRA_HOUSE_ID =
            "com.thehatefulsix.tariffcounter.service_provider_activity.house_id";

    public static final String EXTRA_SERVICE_ID =
            "com.thehatefulsix.tariffcounter.service_provider_activity.service_id";

    public static final String EXTRA_PROVIDER_ID =
            "com.thehatefulsix.tariffcounter.service_provider_activity.provider_id";

    private static final String DIALOG_ON_CLOSE =
            "com.thehatefulsix.tariffcounter.service_provider_dialog_on_close";

    public static final String EXTRA_ON_RESULT_SERVICE_PROVIDER_ID =
            "com.thehatefulsix.tariffcounter.service_provider.on_result_provider_id";

    int SERVICE_PROVIDER_SEARCH_REQUEST_CODE = 701;


    private House mHouse;
    private Service mService;
    private ServiceProvider mServiceProvider;

    private RealmHelper mRealmHelper;

    private TextInputLayout mWrapperServiceProviderTitle;
    private TextInputEditText mEditTextServiceProviderTitle;

    private TextInputEditText mEditTextServiceProviderAccount;

    private TextInputEditText mEditTextServiceProviderMFO;

    private TextInputEditText mEditTextServiceProviderAddress;

    private TextInputEditText mEditTextServiceProviderWeb;

    private TextInputEditText mEditTextServiceProviderPhone;

    private TextInputEditText mEditTextUserAccount;

    private TextInputEditText mEditTextUserBalance;


    public static Intent newIntent(Context packageContext, @Nullable String houseId,
                                   @Nullable String serviceId, @Nullable String providerId) {
        Intent i = new Intent(packageContext, ServiceProviderActivity.class);
        i.putExtra(EXTRA_HOUSE_ID, houseId);
        i.putExtra(EXTRA_SERVICE_ID, serviceId);
        i.putExtra(EXTRA_PROVIDER_ID, providerId);

        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRealmHelper = RealmHelper.getInstance();

        String currencySymbol = Currency.getInstance(Locale.getDefault()).getSymbol();

        if(getIntent().getStringExtra(EXTRA_HOUSE_ID) != null) {
            String houseId = getIntent().getStringExtra(EXTRA_HOUSE_ID);
            mHouse = RealmHelper.getInstance().getObjectById(House.class, houseId);

        } else {
            mHouse = null;
        }

        if(getIntent().getStringExtra(EXTRA_SERVICE_ID) != null) {
            String serviceId = getIntent().getStringExtra(EXTRA_SERVICE_ID);

            mService = RealmHelper.getInstance().getObjectById(Service.class, serviceId);

        } else {
            mService = null;
        }

        if(getIntent().getStringExtra(EXTRA_PROVIDER_ID) != null) {
            String providerId = getIntent().getStringExtra(EXTRA_PROVIDER_ID);

            mServiceProvider = RealmHelper.getInstance()
                    .getObjectById(ServiceProvider.class, providerId);

        } else {
            mServiceProvider = null;
        }


        TextView necessaryFields = (TextView) findViewById(R.id.text_view_provider_necessary);

        mWrapperServiceProviderTitle = (TextInputLayout)
                findViewById(R.id.wrapper_provider_title);
        mEditTextServiceProviderTitle = (TextInputEditText)
                findViewById(R.id.edit_text_provider_title);

        ImageButton buttonServiceProviderSearch = (ImageButton)
                findViewById(R.id.button_find_provider);

        TextInputLayout wrapperServiceProviderAccount = (TextInputLayout)
                findViewById(R.id.wrapper_provider_account);
        mEditTextServiceProviderAccount = (TextInputEditText)
                findViewById(R.id.edit_text_provider_account);

        TextInputLayout wrapperServiceProviderMFO = (TextInputLayout)
                findViewById(R.id.wrapper_provider_mfo);
        mEditTextServiceProviderMFO = (TextInputEditText)
                findViewById(R.id.edit_text_provider_mfo);

        TextInputLayout wrapperServiceProviderAddress = (TextInputLayout)
                findViewById(R.id.wrapper_provider_address);
        mEditTextServiceProviderAddress = (TextInputEditText)
                findViewById(R.id.edit_text_provider_address);

        TextInputLayout wrapperServiceProviderWeb = (TextInputLayout)
                findViewById(R.id.wrapper_provider_web);
        mEditTextServiceProviderWeb = (TextInputEditText)
                findViewById(R.id.edit_text_provider_web);

        TextInputLayout wrapperServiceProviderPhone = (TextInputLayout)
                findViewById(R.id.wrapper_provider_phone);
        mEditTextServiceProviderPhone = (TextInputEditText)
                findViewById(R.id.edit_text_provider_phone);

        TextInputLayout wrapperUserAccount = (TextInputLayout)
                findViewById(R.id.wrapper_user_account);
        mEditTextUserAccount = (TextInputEditText)
                findViewById(R.id.edit_text_user_account);

        TextInputLayout wrapperUserBalance = (TextInputLayout)
                findViewById(R.id.wrapper_user_balance);
        mEditTextUserBalance = (TextInputEditText)
                findViewById(R.id.edit_text_user_balance);

        ImageButton deleteServiceProviderButton = (ImageButton)
                findViewById(R.id.button_provider_delete);

        Button buttonCancel = (Button) findViewById(R.id.button_provider_cancel);
        Button buttonSave = (Button) findViewById(R.id.button_provider_save);


        buttonServiceProviderSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findServiceProvider(SERVICE_PROVIDER_SEARCH_REQUEST_CODE);
            }
        });

        deleteServiceProviderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mService != null){
                    if(mServiceProvider != null) {

                        mRealmHelper.delete(mServiceProvider);
                        setResult(RESULT_OK);
                    }

                    finish();
                }
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
                if(mService == null) {

                    if(!mEditTextServiceProviderTitle.getText().toString().trim().isEmpty()) {
                        mWrapperServiceProviderTitle.setErrorEnabled(false);

                        if(mServiceProvider == null) {
                            mServiceProvider = mRealmHelper.createObject(ServiceProvider.class);

                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_ON_RESULT_SERVICE_PROVIDER_ID,
                                    mServiceProvider.getId());
                            setResult(RESULT_OK, intent);

                            saveServiceProviderFields();

                            finish();

                        } else {
                            if(checkFieldsForChanges()) {
                                saveServiceProviderFields();

                                finish();

                            } else {
                                finish();
                            }
                        }
                    } else {
                        mWrapperServiceProviderTitle.setError(getString(R.string
                                .warning_absent_data));
                    }
                } else {
                    if(mServiceProvider == null) {
                        if(!mEditTextServiceProviderTitle.getText().toString().trim().isEmpty()) {
                            mWrapperServiceProviderTitle.setErrorEnabled(false);

                            mServiceProvider = mRealmHelper.createObject(ServiceProvider.class);

                            mRealmHelper.beginTransaction();
                            mService.setServiceProvider(mServiceProvider);
                            mRealmHelper.commitTransaction();

                            saveServiceProviderFields();

                            finish();

                        } else {
                            mWrapperServiceProviderTitle.setError(getString(R.string
                                    .warning_absent_data));
                        }
                    } else {
                        if(checkFieldsForChanges()) {
                            saveServiceProviderFields();

                            finish();

                        } else {
                            finish();
                        }
                    }
                }
            }
        });


        mWrapperServiceProviderTitle.setHint(getResources()
                .getString(R.string.hint_provider_title));

        wrapperServiceProviderAccount.setHint(getResources()
                .getString(R.string.hint_provider_account));

        wrapperServiceProviderMFO.setHint(getResources().getString(R.string.hint_provider_mfo));

        wrapperServiceProviderAddress.setHint(getResources()
                .getString(R.string.hint_provider_address));

        wrapperServiceProviderWeb.setHint(getResources()
                .getString(R.string.hint_provider_website));

        wrapperServiceProviderPhone.setHint(getResources()
                .getString(R.string.hint_provider_phone));

        wrapperUserAccount.setHint(getResources().getString(R.string.hint_user_account));

        wrapperUserBalance.setHint(getResources().getString(R.string.hint_user_balance)
                + ", " + currencySymbol);


        if(mService != null) {
            mEditTextUserBalance.setEnabled(true);
            mEditTextUserBalance.setText(String.valueOf(mService.getBalance()));

        } else {
            mEditTextUserBalance.setEnabled(false);
        }

        if(mServiceProvider != null) {
            necessaryFields.setVisibility(View.GONE);
            mWrapperServiceProviderTitle
                    .setHint(getResources().getString(R.string.hint_provider_title));

            if(mServiceProvider.getName() != null) {
                mEditTextServiceProviderTitle.setText(mServiceProvider.getName());
            }

            if(mServiceProvider.getProviderAccount() != null) {
                mEditTextServiceProviderAccount
                        .setText(mServiceProvider.getProviderAccount());
            }

            if(mServiceProvider.getMFO() != null) {
                mEditTextServiceProviderMFO.setText(mServiceProvider.getMFO());
            }

            if(mServiceProvider.getAddress() != null) {
                mEditTextServiceProviderAddress.setText(mServiceProvider.getAddress());
            }

            if(mServiceProvider.getWeb() != null) {
                mEditTextServiceProviderWeb.setText(mServiceProvider.getWeb());
            }

            if(mServiceProvider.getPhone() != null) {
                mEditTextServiceProviderPhone.setText(mServiceProvider.getPhone());
            }

            if(mServiceProvider.getAccount() != null) {
                mEditTextUserAccount.setText(mServiceProvider.getAccount());
            }

            deleteServiceProviderButton.setVisibility(View.VISIBLE);

        } else {
            necessaryFields.setVisibility(View.VISIBLE);
            mWrapperServiceProviderTitle
                    .setHint(getResources().getString(R.string.hint_provider_title_null));

            deleteServiceProviderButton.setVisibility(View.GONE);
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

    private boolean checkFieldsForChanges() {
        boolean changed = false;

        String userBalance = mEditTextUserBalance.getText().toString().trim();
        float userBalanceFloat = 0;
        try {
            userBalanceFloat = Float.parseFloat(userBalance);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(mServiceProvider != null) {
            if(mServiceProvider.getName() != null) {
                if(!mServiceProvider.getName()
                        .equals(mEditTextServiceProviderTitle.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            }

            if(mServiceProvider.getProviderAccount() != null) {
                if(!mServiceProvider.getProviderAccount()
                        .equals(mEditTextServiceProviderAccount.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextServiceProviderAccount.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mServiceProvider.getMFO() != null) {
                if(!mServiceProvider.getMFO()
                        .equals(mEditTextServiceProviderMFO.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextServiceProviderMFO.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mServiceProvider.getAddress() != null) {
                if(!mServiceProvider.getAddress()
                        .equals(mEditTextServiceProviderAddress.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextServiceProviderAddress.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mServiceProvider.getWeb() != null) {
                if(!mServiceProvider.getWeb()
                        .equals(mEditTextServiceProviderWeb.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextServiceProviderWeb.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mServiceProvider.getPhone() != null) {
                if(!mServiceProvider.getPhone()
                        .equals(mEditTextServiceProviderPhone.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextServiceProviderPhone.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mServiceProvider.getAccount() != null) {
                if(!mServiceProvider.getAccount()
                        .equals(mEditTextUserAccount.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextUserAccount.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mService != null && mServiceProvider != null
                    && mService.getBalance() != userBalanceFloat) {
                changed = true;
                return changed;
            }

        } else {
            if(!mEditTextServiceProviderTitle.getText().toString().trim().isEmpty()
                    || !mEditTextServiceProviderAccount.getText().toString().trim().isEmpty()
                    || !mEditTextServiceProviderMFO.getText().toString().trim().isEmpty()
                    || !mEditTextServiceProviderAddress.getText().toString().trim().isEmpty()
                    || !mEditTextServiceProviderWeb.getText().toString().trim().isEmpty()
                    || !mEditTextServiceProviderPhone.getText().toString().trim().isEmpty()
                    || !mEditTextUserAccount.getText().toString().trim().isEmpty())            {

                changed = true;
                return changed;
            }
        }
        return changed;
    }


    public void findServiceProvider(int requestCode) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, requestCode);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    private void saveServiceProviderFields() {
        String userBalance = mEditTextUserBalance.getText().toString().trim();
        float userBalanceFloat = 0;
        try {
            userBalanceFloat = Float.parseFloat(userBalance);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(mHouse.getCity() != null) {
            mRealmHelper.beginTransaction();
            mServiceProvider.setCity(mHouse.getCity());
            mRealmHelper.commitTransaction();
        }

        mRealmHelper.beginTransaction();
        mServiceProvider.setName(mEditTextServiceProviderTitle
                .getText().toString().trim());

        mServiceProvider.setProviderAccount(mEditTextServiceProviderAccount
                .getText().toString().trim());

        mServiceProvider.setMFO(mEditTextServiceProviderMFO.getText().toString().trim());

        mServiceProvider.setAddress(mEditTextServiceProviderAddress
                .getText().toString().trim());

        mServiceProvider.setWeb(mEditTextServiceProviderWeb.getText().toString().trim());

        mServiceProvider.setPhone(mEditTextServiceProviderPhone
                .getText().toString().trim());

        mServiceProvider.setAccount(mEditTextUserAccount.getText().toString().trim());
        mRealmHelper.commitTransaction();

        if(mService != null && userBalanceFloat != -0) {
            mRealmHelper.beginTransaction();
            mService.setBalance(userBalanceFloat);
            mRealmHelper.commitTransaction();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SERVICE_PROVIDER_SEARCH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place provider = PlaceAutocomplete.getPlace(this, data);

                mEditTextServiceProviderTitle.setText(provider.getName());

                mEditTextServiceProviderAddress.setText(provider.getAddress());

                if(provider.getWebsiteUri() != null) {
                    mEditTextServiceProviderWeb.setText(provider.getWebsiteUri().toString());
                }

                mEditTextServiceProviderPhone.setText(provider.getPhoneNumber());
            }
        }
    }
}