package com.thehatefulsix.tariffcounter.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.activities.core.ActivityWithMethods;
import com.thehatefulsix.tariffcounter.models.Bill;
import com.thehatefulsix.tariffcounter.models.Rate;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.utils.DateFormatter;
import com.thehatefulsix.tariffcounter.utils.DatePickerDialogFragment;
import com.thehatefulsix.tariffcounter.utils.GlideLoader;
import com.thehatefulsix.tariffcounter.utils.MyDialogFragment;
import com.thehatefulsix.tariffcounter.utils.PermissionHelper;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;
import com.thehatefulsix.tariffcounter.utils.SimpleTextWatcher;
import com.thehatefulsix.tariffcounter.utils.SnackBarHelper;

import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;

public class EditBillActivity extends ActivityWithMethods implements MyDialogFragment.OnClickListener,
        DatePickerDialogFragment.OnClickListener {

    private static final String KEY_SELECTED_BILL = "KEY_SELECTED_BILL";
    private static final String KEY_SELECTED_SERVICE = "KEY_SELECTED_SERVICE";

    private static final String TAG_DIALOG_DELETE = "tag_dialog_delete";
    private static final String TAG_DIALOG_ON_CLOSE = "tag_dialog_on_close";

    private static final String TAG_DATE_PICKER_START = "TAG_DATE_PICKER_START";
    private static final String TAG_DATE_PICKER_END = "TAG_DATE_PICKER_END";

    private static final int PERMISSIONS_REQUEST_CODE = 505;

    private static final String KEY_IS_FLASHLIGHT_ENABLED = "KEY_IS_FLASHLIGHT_ENABLED";

    private Bill mSelectedBill;
    private Service mSelectedService;
    private Rate mSelectedRate;
    private boolean mIsFlashlightEnabled = false;
    private boolean mAreSomeChanges = false;

    @BindView(R.id.tv_service_name) TextView mTVServiceName;

    @BindView(R.id.tv_service_provider_name) TextView mTVProviderName;

    @BindView(R.id.tv_personal_account) TextView mTVAccount;

    @BindView(R.id.tv_contract_number) TextView mTVContractNumber;

    @BindView(R.id.tv_last_payment) TextView mTVLastPayment;

    @BindView(R.id.tv_period_start) TextView mTVPeriodStart;

    @BindView(R.id.tv_period_end) TextView mTVPeriodEnd;

    @BindView(R.id.layout_counter_data) LinearLayout mLayoutCounterData;
    @BindView(R.id.et_previous_mark) EditText mETPreviousMark;

    @BindView(R.id.et_current_mark) EditText mETCurrentMark;

    @BindView(R.id.tv_consumed) TextView mTVConsumed;

    @BindView(R.id.et_total_amount) EditText mETTotalAmount;
    @BindView(R.id.tv_currency_total_amount) TextView mTVCurrencyTotalAmount;

    @BindView(R.id.iv_camera) ImageView mIvCamera;
    @BindView(R.id.iv_flashlight) ImageView mIvFlashlight;

    @BindView(R.id.tv_frequency_information) TextView mTVFrequency;

    @BindView(R.id.layout_rate_information) LinearLayout mRateLayout;
    @BindView(R.id.tv_rate_information) TextView mTVRate;

    @BindView(R.id.layout_extra_payment_information) LinearLayout mExtraPaymentLayout;
    @BindView(R.id.tv_extra_payment_information) TextView mTVExtraPayment;

    @BindView(R.id.layout_discount_information) LinearLayout mDiscountLayout;
    @BindView(R.id.tv_discount_information) TextView mTVDiscount;

    @BindView(R.id.layout_subsidy_information) LinearLayout mSubsidyLayout;
    @BindView(R.id.tv_mandatory_fee) TextView mTVMandatoryFee;
    @BindView(R.id.tv_social_norm) TextView mTVSocialNorm;

    @BindView(R.id.tv_status) TextView mTVStatus;
    @BindView(R.id.iv_sum_icon) ImageView mIVSumIcon;

    @BindView(R.id.service_image) CircleImageView mServiceImage;

    public static Intent getInstance(@NonNull Context context, @Nullable String serviceId,
                                     @Nullable String billId){
        final Intent intent = new Intent(context, EditBillActivity.class);

        if (serviceId != null){
            intent.putExtra(KEY_SELECTED_SERVICE, serviceId);
        }
        if (billId != null){
            intent.putExtra(KEY_SELECTED_BILL, billId);
        }

        return intent;
    }

    @Nullable
    @Override
    protected String changeActionBarTitle() {
        return null;
    }

    @Override
    protected int contentView() {
        return R.layout.activity_edit_bill;
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null ){
            if (getIntent().getExtras().containsKey(KEY_SELECTED_BILL)){
                final String id = getIntent().getExtras().getString(KEY_SELECTED_BILL, null);
                mSelectedBill = getRealmHelper().getObjectById(Bill.class, id);
                mSelectedService = mSelectedBill.getService();
                mSelectedRate = getRealmHelper().getObjectById(Rate.class, mSelectedBill.getRateId());
            }else if (getIntent().getExtras().containsKey(KEY_SELECTED_SERVICE)){
                final String id = getIntent().getExtras().getString(KEY_SELECTED_SERVICE, null);
                mSelectedService = getRealmHelper().getObjectById(Service.class, id);
                mSelectedRate = mSelectedService.getRate();
            }
        }

        setTitle(mSelectedService.getName() != null ?
                mSelectedService.getName() : mSelectedService.getType().getName(this));

        if (mSelectedService != null){
            setInformationAboutService(mSelectedService);

            mSelectedService.addChangeListener(new RealmChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel element) {
                    if(!EditBillActivity.this.isDestroyed()) {
                        setInformationAboutService(mSelectedService);
                    }
                }
            });
        }

        if (mSelectedBill != null){
            setInformationAboutBill(mSelectedBill);

            mSelectedBill.addChangeListener(new RealmChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel element) {
                    if(!EditBillActivity.this.isDestroyed()) {
                        setInformationAboutBill(mSelectedBill);
                        setInformationAboutService(mSelectedService);
                    }
                }
            });
        }else {
            setPaymentStatus(false);
        }

        if (mSelectedRate != null){
            setInformationAboutRate(mSelectedRate);

            mSelectedRate.addChangeListener(new RealmChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel element) {
                    if(!EditBillActivity.this.isDestroyed()) {
                        setInformationAboutRate(mSelectedRate);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mAreSomeChanges) {
            final DialogFragment dialogFragment = MyDialogFragment.newInstance(R.string.attention,
                    R.string.warning_on_close);

            dialogFragment.setCancelable(false);

            dialogFragment.show(getSupportFragmentManager(), TAG_DIALOG_ON_CLOSE);
        }else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_bill_activity, menu);

        final MenuItem deleteItem = menu.findItem(R.id.action_delete);

        if (deleteItem != null && mSelectedBill == null){
            deleteItem.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete : {

                if (mSelectedBill != null){
                    DialogFragment dialogFragment = MyDialogFragment
                            .newInstance(R.string.deleting, R.string.delete_payment);

                    dialogFragment.setCancelable(false);

                    dialogFragment.show(getSupportFragmentManager(), TAG_DIALOG_DELETE);
                }

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSIONS_REQUEST_CODE:

                getSharedPreferenceHelper().saveBooleanObject(
                        SharedPreferenceHelper.Key.PERMISSION_CAMERA, false);

                if (PermissionHelper.hasPermission(this, "android.permission.CAMERA")) {
                    mIvCamera.setVisibility(View.VISIBLE);
                    mIvFlashlight.setVisibility(View.VISIBLE);
                }

                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onOkClicked(String tag) {
        switch (tag){
            case TAG_DIALOG_DELETE :{
                getRealmHelper().delete(mSelectedBill);

                finish();
                break;
            }

            case TAG_DIALOG_ON_CLOSE :{
                finish();
                break;
            }
        }
    }

    @Override
    public void onOkClicked(String tag, GregorianCalendar date) {
        switch (tag){
            case TAG_DATE_PICKER_START : {
                mAreSomeChanges = true;

                mTVPeriodStart.setText(DateFormatter.parseData(date.getTime(), this));
                break;
            }

            case TAG_DATE_PICKER_END : {
                mAreSomeChanges = true;

                mTVPeriodEnd.setText(DateFormatter.parseData(date.getTime(), this));
                break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_FLASHLIGHT_ENABLED, mIsFlashlightEnabled);
        super.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mIsFlashlightEnabled = savedInstanceState.getBoolean(KEY_IS_FLASHLIGHT_ENABLED);

        mIvFlashlight.setImageDrawable(mIsFlashlightEnabled ?
                getResources().getDrawable(R.drawable.ic_flashlight_off) :
                getResources().getDrawable(R.drawable.ic_flashlight));

        super.onRestoreInstanceState(savedInstanceState);
    }

    @OnClick(R.id.btn_save) void save(){
        if (!checkValues()){
            return;
        }

        addRate();
    }

    //TODO: camera or gallery;
    @OnClick(R.id.iv_camera) void camera(){
        showSnackBar(R.string.oops);
    }

    @OnClick(R.id.iv_edit) void editService(){
        startActivity(ServiceSettingsActivity.newIntent(
                this, mSelectedService.getId(), getCurrentHouse().getId()));
    }

    @SuppressWarnings("deprecation")
    @OnClick(R.id.iv_flashlight) void flashlight(){
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

            if (!mIsFlashlightEnabled) {
                final Camera camera = Camera.open();
                final Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();

                mIvFlashlight.setImageDrawable(getResources().getDrawable(R.drawable.ic_flashlight_off));
                mIsFlashlightEnabled = true;
            } else {
                final Camera camera = Camera.open();
                final Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.stopPreview();
                camera.release();

                mIvFlashlight.setImageDrawable(getResources().getDrawable(R.drawable.ic_flashlight));
                mIsFlashlightEnabled = false;
            }
        }
    }

    @OnClick({R.id.tv_period_start, R.id.iv_period_start}) void datePikerPeriodStart() {
        showDatePicker(TAG_DATE_PICKER_START, mTVPeriodStart.getText().toString());
    }

    @OnClick({R.id.tv_period_end, R.id.iv_period_end}) void datePikerPeriodEnd() {
        showDatePicker(TAG_DATE_PICKER_END, mTVPeriodEnd.getText().toString());
    }

    private void addRate() {

        final Date dateStart =  DateFormatter.parseString(
                mTVPeriodStart.getText().toString().trim(), this);
        final long periodStart = dateStart != null ? dateStart.getTime() : System.currentTimeMillis();

        final Date dateEnd =  DateFormatter.parseString(
                mTVPeriodEnd.getText().toString().trim(), this);
        final long periodEnd = dateEnd != null ? dateEnd.getTime() : System.currentTimeMillis();

        final float sum =  Float.parseFloat(mETTotalAmount.getText().toString().trim());

        if (mSelectedBill == null){
            final Bill newBill = getRealmHelper().addOrUpdate(
                    new Bill(mSelectedService, mSelectedService.getRate().getId(),
                            periodStart, periodEnd, 0, 0, false, 0));

            getRealmHelper().beginTransaction();
            newBill.setSum(sum);
            getRealmHelper().commitTransaction();

            if (mSelectedService.getRate().isHasRate()){
                final int previousMark = Integer.parseInt(
                        mETPreviousMark.getText().toString().trim());
                final int currentMark = Integer.parseInt(
                        mETCurrentMark.getText().toString().trim());

                getRealmHelper().beginTransaction();
                newBill.setPreviousMark(previousMark);
                newBill.setCurrentMark(currentMark);
                getRealmHelper().commitTransaction();
            }

        }else {
            getRealmHelper().beginTransaction();
            mSelectedBill.setPeriodStart(periodStart);
            mSelectedBill.setPeriodEnd(periodEnd);
            mSelectedBill.setSum(sum);
            getRealmHelper().commitTransaction();

            if (mSelectedService.getRate().isHasRate()){
                final int previousMark = Integer.parseInt(
                        mETPreviousMark.getText().toString().trim());
                final int currentMark = Integer.parseInt(
                        mETCurrentMark.getText().toString().trim());

                getRealmHelper().beginTransaction();
                mSelectedBill.setPreviousMark(previousMark);
                mSelectedBill.setCurrentMark(currentMark);
                getRealmHelper().commitTransaction();
            }
        }

        finish();
    }

    private boolean checkValues() {
        if (mSelectedRate.isHasRate()){
            return validatePreviousMark(true) && validateCurrentMark(true) &&
                    validateCounterData(true) && validatePeriodStart(true) &&
                    validatePeriodEnd(true) && validatePeriod(true) && validateSum(true);
        }

        return validatePeriodStart(true) && validatePeriodEnd(true) && validatePeriod(true)  &&
                validateSum(true) ;
    }

    private void setInformationAboutService(Service selectedService) {

        mServiceImage = (CircleImageView) findViewById(R.id.service_image);

        GlideLoader.loadImage(this, mServiceImage, selectedService.getIcon());

        mTVServiceName.setText(selectedService.getName() != null ?
                selectedService.getName() : selectedService.getType().getName(this));

        long days = RealmHelper.getInstance().getDifferent(selectedService.getId());

        String lastPayment;
        if (days == 1){
            lastPayment = days + " " + getString(R.string.day);
        }else {
            lastPayment = days + " " + getString(R.string.days);
        }

        mTVLastPayment.setText(lastPayment);

        if (selectedService.getServiceProvider() != null) {
            if (selectedService.getServiceProvider().getName() != null &&
                    !selectedService.getServiceProvider().getName().trim().isEmpty()) {
                mTVProviderName.setText(selectedService.getServiceProvider().getName());
            }else {
                mTVProviderName.setText("-");
            }

            if (selectedService.getServiceProvider().getAccount() != null &&
                    !selectedService.getServiceProvider().getAccount().trim().isEmpty()) {
                mTVAccount.setText(selectedService.getServiceProvider().getAccount());
            }else {
                mTVAccount.setText("-");
            }

            if (selectedService.getServiceProvider().getProviderAccount() != null &&
                    !selectedService.getServiceProvider().getProviderAccount().trim().isEmpty()) {
                mTVContractNumber.setText(selectedService.getServiceProvider().getProviderAccount());
            }else {
                mTVContractNumber.setText("-");
            }
        }

        if (selectedService.getRate() != null) {
            if (!selectedService.getRate().isHasRate()) {
                mLayoutCounterData.setVisibility(View.GONE);
            }else {
                mLayoutCounterData.setVisibility(View.VISIBLE);
                int previousMark = getRealmHelper().getPreviousMark(mSelectedService.getId());
                final int serviceCounter = (int) selectedService.getCounter();
                previousMark = serviceCounter > previousMark ? serviceCounter : previousMark;

                if (previousMark != 0){
                    mETPreviousMark.setText(String.valueOf(previousMark));
                }
            }
        }

        final String currency = "$";

        mTVCurrencyTotalAmount.setText(currency);

        mTVPeriodEnd.setText(DateFormatter.parseData(new Date(System.currentTimeMillis()), this));

        final long periodStart = getRealmHelper().getLastPaymentDate(mSelectedService.getId());

        if (periodStart != 0){
            final String dateStart = DateFormatter.parseData(new Date(periodStart), this);
            mTVPeriodStart.setText(dateStart);
        }


        mTVPeriodStart.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                mAreSomeChanges = true;

                if (validatePeriodStart(false) && validatePeriod(false)){
                    calculateSum();
                }
            }
        });
        mTVPeriodEnd.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                mAreSomeChanges = true;

                if (validatePeriodEnd(false) && validatePeriod(false)){
                    calculateSum();
                }
            }
        });

        mETTotalAmount.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mAreSomeChanges = true;
            }
        });

        if (selectedService.isSubsidy() && selectedService.getSubsidy() != null){
            mSubsidyLayout.setVisibility(View.VISIBLE);
            mTVMandatoryFee.setText((String.valueOf(selectedService.getSubsidy().getMandatoryFee())
                    + " " + currency));
            mTVSocialNorm.setText((String.valueOf(selectedService.getSubsidy().getSocialNorm())
                    + " " + (selectedService.getRateUnit() == Service.RateUnit.NULL ?
                    "" : selectedService.getRateUnit().getName(this))));
        }else {
            mSubsidyLayout.setVisibility(View.GONE);
        }
    }

    private void setInformationAboutBill(Bill selectedBill) {
        if (mSelectedService.getRate() != null) {
            if (mSelectedService.getRate().isHasRate()) {
                mETPreviousMark.setText(String.valueOf(selectedBill.getPreviousMark()));
                mETCurrentMark.setText(String.valueOf(selectedBill.getCurrentMark()));
                mTVConsumed.setText(String.valueOf(
                        selectedBill.getCurrentMark() - selectedBill.getPreviousMark()));
            }
        }

        mTVPeriodStart.setText(DateFormatter.parseData(
                new Date(selectedBill.getPeriodStart()), this));
        mTVPeriodEnd.setText(DateFormatter.parseData(
                new Date(selectedBill.getPeriodEnd()), this));

        setPaymentStatus(selectedBill.isPaid());
        mETTotalAmount.setText(String.valueOf(selectedBill.getSum()));

        mAreSomeChanges = false;
    }

    @SuppressWarnings("deprecation")
    private void setPaymentStatus(boolean isPaid) {
        mTVStatus.setText(getString(
                isPaid ? R.string.paid_status : R.string.unpaid_status));
        mTVStatus.setTextColor(getResources().getColor(
                isPaid ? R.color.green : R.color.red));
        mETTotalAmount.setTextColor(getResources().getColor(
                isPaid ? R.color.green : R.color.red));
        mIVSumIcon.setImageDrawable(getResources().getDrawable(
                isPaid ? R.drawable.ic_paid : R.drawable.ic_unpaid));
        mTVCurrencyTotalAmount.setTextColor(getResources().getColor(
                isPaid ? R.color.green : R.color.red));
    }

    private void setInformationAboutRate(Rate selectedRate) {

        if (selectedRate.isHasRate()) {
            mETPreviousMark.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    mAreSomeChanges = true;

                    if (validatePreviousMark(false) && validateCounterData(false)) {
                        calculateSum();
                    }
                }
            });
            mETCurrentMark.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    mAreSomeChanges = true;

                    if (validateCurrentMark(false) && validateCounterData(false)) {
                        calculateSum();
                    }
                }
            });

            if (!PermissionHelper.hasPermission(this, "android.permission.CAMERA") &&
                    PermissionHelper.shouldWeAsk(SharedPreferenceHelper.Key.PERMISSION_CAMERA)){
                PermissionHelper.askPermissions(this, "android.permission.CAMERA",
                        PERMISSIONS_REQUEST_CODE,
                        SharedPreferenceHelper.Key.PERMISSION_CAMERA);
            }else {
                mIvCamera.setVisibility(View.VISIBLE);
                mIvFlashlight.setVisibility(View.VISIBLE);
            }
        }else {
            mIvCamera.setVisibility(View.INVISIBLE);
            mIvFlashlight.setVisibility(View.INVISIBLE);
        }

        mTVFrequency.setText(selectedRate.getPeriod().getName(this));

        mTVRate.setText(selectedRate.getInformation(this));

        if (selectedRate.getExtraPayment() != 0){
            mExtraPaymentLayout.setVisibility(View.VISIBLE);
            mTVExtraPayment.setText(String.valueOf(selectedRate.getExtraPayment()));
        }else {
            mExtraPaymentLayout.setVisibility(View.GONE);
        }

        if (selectedRate.getDiscount() != 0){
            mDiscountLayout.setVisibility(View.VISIBLE);
            mTVDiscount.setText((String.valueOf(selectedRate.getDiscount()) + "%"));
        }else {
            mDiscountLayout.setVisibility(View.GONE);
        }
    }

    private void calculateSum() {

        float sum;

        final Date dateStart = DateFormatter.parseString(
                mTVPeriodStart.getText().toString().trim(), this);
        final Date dateEnd = DateFormatter.parseString(
                mTVPeriodEnd.getText().toString().trim(), this);

        final boolean hasMater = mSelectedRate.isHasRate();
        final boolean hasSubsidy = mSelectedService.isSubsidy() &&
                mSelectedService.getSubsidy() != null;

        if (dateStart != null && dateEnd != null) {
            if (hasMater) {
                try{
                    int previousMark = Integer.parseInt(mETPreviousMark.getText().toString().trim());
                    int currentMark = Integer.parseInt(mETCurrentMark.getText().toString().trim());

                    sum = Bill.calculateSum(mSelectedService, dateStart.getTime(), dateEnd.getTime(),
                            true, currentMark, previousMark, hasSubsidy);

                }catch (NumberFormatException e){
                    mETTotalAmount.setText(String.valueOf(0.0));
                    return;
                }
            } else {
                sum = Bill.calculateSum(mSelectedService, dateStart.getTime(), dateEnd.getTime(),
                        false, 0, 0, hasSubsidy);
            }

            mETTotalAmount.setText(String.valueOf(sum));
        }else {
            mETTotalAmount.setText(String.valueOf(0.0));
        }
    }

    private boolean validatePeriodStart(boolean showSnackBar) {
        if (mTVPeriodStart.getText().toString().trim().isEmpty() ||
                DateFormatter.parseString(
                        mTVPeriodStart.getText().toString().trim(), this) == null) {
            if (showSnackBar) {
                showSnackBar(R.string.enter_the_start_of_period);
            }
            requestFocus(mTVPeriodStart);

            return false;
        }

        return true;
    }

    private boolean validatePeriodEnd(boolean showSnackBar) {
        if (mTVPeriodEnd.getText().toString().trim().isEmpty() ||
                DateFormatter.parseString(
                        mTVPeriodEnd.getText().toString().trim(), this) == null) {
            if (showSnackBar) {
                showSnackBar(R.string.enter_the_end_of_period);
            }
            requestFocus(mTVPeriodEnd);

            return false;
        }

        return true;
    }

    private boolean validatePeriod(boolean showSnackBar) {
        final Date dateStart = DateFormatter.parseString(
                mTVPeriodStart.getText().toString().trim(), this);
        final Date dateEnd = DateFormatter.parseString(
                mTVPeriodEnd.getText().toString().trim(), this);

        final GregorianCalendar calendarStart = new GregorianCalendar();
        final GregorianCalendar calendarEnd = new GregorianCalendar();

        if (dateStart != null && dateEnd != null) {
            calendarStart.setTimeInMillis(dateStart.getTime());
            calendarEnd.setTimeInMillis(dateEnd.getTime());

            if (calendarStart.after(calendarEnd) || calendarStart.equals(calendarEnd)) {
                if (showSnackBar) {
                    showSnackBar(R.string.enter_the_correct_date);
                }

                return false;
            }
        }

        return true;
    }

    private boolean validatePreviousMark(boolean showSnackBar) {
        if (mETPreviousMark.getText().toString().trim().isEmpty()) {
            if (showSnackBar) {
                showSnackBar(R.string.enter_the_previous_mark);
            }
            requestFocus(mETPreviousMark);

            return false;
        }

        return true;
    }

    private boolean validateCurrentMark(boolean showSnackBar) {
        if (mETCurrentMark.getText().toString().trim().isEmpty()) {
            if (showSnackBar) {
                showSnackBar(R.string.enter_the_current_mark);
            }
            requestFocus(mETCurrentMark);

            return false;
        }

        return true;
    }

    private boolean validateCounterData(boolean showSnackBar) {
        try {
            int previousMark = Integer.parseInt(mETPreviousMark.getText().toString().trim());
            int currentMark = Integer.parseInt(mETCurrentMark.getText().toString().trim());

            if (previousMark >= currentMark){
                if (showSnackBar){
                    showSnackBar(R.string.enter_the_correct_current_mark);
                }

                return false;
            }

            mTVConsumed.setText(String.valueOf(currentMark - previousMark));

            return true;

        }catch (NumberFormatException e){
            return false;
        }
    }

    private boolean validateSum(boolean showSnackBar) {
        try {
            return Float.parseFloat(mETTotalAmount.getText().toString().trim()) != 0;
        }catch (NumberFormatException e){
            if (showSnackBar) {
                showSnackBar(R.string.enter_the_correct_sum);
            }
            requestFocus(mETTotalAmount);
            return false;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void showDatePicker(String tag, String text){

        final Date date = DateFormatter.parseString(text, this);
        final long time = date != null ? date.getTime() : System.currentTimeMillis();
        final DialogFragment dialogFragment = DatePickerDialogFragment.newInstance(time);

        dialogFragment.setCancelable(false);
        dialogFragment.show(getSupportFragmentManager(), tag);
    }

    private void showSnackBar(@StringRes int message) {
        SnackBarHelper.show(this, findViewById(R.id.context), message);
    }
}
