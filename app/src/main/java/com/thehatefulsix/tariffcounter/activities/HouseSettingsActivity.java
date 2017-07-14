package com.thehatefulsix.tariffcounter.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.activities.core.ActivityWithMethods;
import com.thehatefulsix.tariffcounter.models.City;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.models.Region;
import com.thehatefulsix.tariffcounter.utils.GlideLoader;
import com.thehatefulsix.tariffcounter.utils.MyDialogFragment;
import com.thehatefulsix.tariffcounter.utils.PermissionHelper;
import com.thehatefulsix.tariffcounter.utils.PictureUtils;
import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;

import java.io.File;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class HouseSettingsActivity extends ActivityWithMethods
                                   implements MyDialogFragment.OnClickListener {

    public static final String EXTRA_HOUSE_ID = "com.thehatefulsix.tariffcounter.mId";

    private static final String DIALOG_ON_CLOSE =
            "com.thehatefulsix.tariffcounter.house_settings_dialog_on_close";

    private static final int REGION_AUTOCOMPLETE_REQUEST_CODE = 11;
    private static final int CITY_AUTOCOMPLETE_REQUEST_CODE = 22;
    private static final int REQUEST_PHOTO = 33;

    private static final int PERMISSIONS_REQUEST_CODE_CAMERA = 44;
    private static final int PERMISSIONS_REQUEST_CODE_EXTERNAL_STORAGE = 55;


    private House mHouse;

    private RealmHelper mRealmHelper;

    private String mNewHouseId;
    private File mPhotoFile;
    private CircleImageView mPhotoView;

    private TextInputEditText mEditTextHouseTitle;

    private TextInputEditText mEditTextHouseRegion;

    private TextInputLayout mWrapperHouseCity;
    private TextInputEditText mEditTextHouseCity;

    private TextInputEditText mEditTextHouseStreet;

    private Switch mHouseType;

    private TextInputEditText mEditTextHouseNumber;
    private TextInputLayout mWrapperHouseBuilding;
    private TextInputEditText mEditTextHouseBuilding;

    private TextInputLayout mWrapperHouseApartment;
    private TextInputEditText mEditTextHouseApartment;

    private TextInputEditText mEditTextHouseArea;


    public static Intent newIntent(Context packageContext, @Nullable String id) {
        Intent i = new Intent(packageContext, HouseSettingsActivity.class);
        i.putExtra(EXTRA_HOUSE_ID, id);

        return i;
    }

    @Override
    protected String changeActionBarTitle() {
        return getString(R.string.app_settings);
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return true;
    }

    @Override
    protected int contentView() {
        return R.layout.activity_house_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PermissionHelper.shouldWeAsk(
                SharedPreferenceHelper.Key.PERMISSION_CAMERA)){
            PermissionHelper.askPermissions(this, "android.permission.CAMERA",
                    PERMISSIONS_REQUEST_CODE_CAMERA,
                    SharedPreferenceHelper.Key.PERMISSION_CAMERA);
        }
        if (PermissionHelper.shouldWeAsk(
                SharedPreferenceHelper.Key.PERMISSION_READ_EXTERNAL_STORAGE)){
            PermissionHelper.askPermissions(this, "android.permission.READ_EXTERNAL_STORAGE",
                    PERMISSIONS_REQUEST_CODE_EXTERNAL_STORAGE,
                    SharedPreferenceHelper.Key.PERMISSION_READ_EXTERNAL_STORAGE);
        }

        mRealmHelper = RealmHelper.getInstance();

        if(getIntent().getStringExtra(EXTRA_HOUSE_ID) != null) {
            String houseId = getIntent().getStringExtra(EXTRA_HOUSE_ID);
            mHouse = RealmHelper.getInstance().getObjectById(House.class, houseId);
        } else {
            mHouse = null;
        }


        if(mHouse != null) {
            mPhotoFile = PictureUtils.getPhotoFile(mHouse, getApplicationContext());

        } else {
            mNewHouseId = UUID.randomUUID().toString();
            String newFileName = "IMG_" + mNewHouseId + ".jpg";

            mPhotoFile = PictureUtils.getPhotoFile(newFileName, getApplicationContext());
        }


        TextView necessaryFields = (TextView) findViewById(R.id.text_view_house_necessary);
        mEditTextHouseTitle = (TextInputEditText) findViewById(R.id.edit_text_house_title);

        mEditTextHouseRegion = (TextInputEditText) findViewById(R.id.edit_text_house_region);
        final ImageButton findRegion = (ImageButton) findViewById(R.id.findRegion);

        mWrapperHouseCity = (TextInputLayout) findViewById(R.id.wrapper_house_city);
        mEditTextHouseCity = (TextInputEditText) findViewById(R.id.edit_text_house_city);
        final ImageButton findCity = (ImageButton) findViewById(R.id.findCity);

        mEditTextHouseStreet = (TextInputEditText) findViewById(R.id.edit_text_house_street);

        mHouseType = (Switch) findViewById(R.id.house_type_switch);

        mEditTextHouseNumber = (TextInputEditText) findViewById(R.id.edit_text_house_number);

        mWrapperHouseBuilding = (TextInputLayout) findViewById(R.id.wrapper_house_building);
        mEditTextHouseBuilding = (TextInputEditText) findViewById(R.id.edit_text_house_building);

        mWrapperHouseApartment = (TextInputLayout) findViewById(R.id.wrapper_house_apartment);
        mEditTextHouseApartment = (TextInputEditText)
                findViewById(R.id.edit_text_house_apartment);

        mEditTextHouseArea = (TextInputEditText) findViewById(R.id.edit_text_house_area);

        final Button saveButton = (Button) findViewById(R.id.button_save);

        final Button cancelButton = (Button) findViewById(R.id.button_cancel);

        final ImageButton deleteHouseButton = (ImageButton)
                findViewById(R.id.button_house_delete);

        mPhotoView = (CircleImageView) findViewById(R.id.house_photo);


        if(!PermissionHelper.hasPermission(this, "android.permission.CAMERA")) {
            mPhotoView.setVisibility(View.GONE);
        } else {
            mPhotoView.setVisibility(View.VISIBLE);
        }
        if(!PermissionHelper.hasPermission(this, "android.permission.READ_EXTERNAL_STORAGE")) {
            mPhotoView.setVisibility(View.GONE);
        } else {
            mPhotoView.setVisibility(View.VISIBLE);
        }

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(getPackageManager()) != null;

        mPhotoView.setEnabled(canTakePhoto);

        if(canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        findRegion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findPlace(REGION_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        findCity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findPlace(CITY_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        deleteHouseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mHouse != null){
                    if (mRealmHelper.getAll(House.class).size() != 1){
                        if(mPhotoFile.exists()) {
                            getApplicationContext().deleteFile(mHouse.getPhotoFilename());
                        }

                        mRealmHelper.delete(mHouse);

                        SharedPreferenceHelper
                                .getInstance()
                                .saveStringObject(SharedPreferenceHelper.Key.SELECTED_HOUSE,
                                        mRealmHelper.getAll(House.class).get(0).getId());
//                        Intent intent = new Intent(HouseSettingsActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                        finish();
                    }
                }
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                safeExit();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(mEditTextHouseCity.getText().toString().trim().isEmpty()) {
                    mWrapperHouseCity.setError(getString(R.string.warning_house_city));

                } else {
                    mWrapperHouseCity.setErrorEnabled(false);

                    if(mHouse == null) {
                        if(mNewHouseId != null) {
                            mHouse = mRealmHelper.createObjectWithId(House.class, mNewHouseId);
                        } else {
                            mHouse = mRealmHelper.addOrUpdate(new House());
                        }
                        saveHouseFields();

                    } else {
                        if(checkFieldsForChanges()) {
                            saveHouseFields();
                        } else {
                            finish();
                        }
                    }
                }
            }
        });


        if(mHouse == null) {
            necessaryFields.setVisibility(View.VISIBLE);
            mWrapperHouseCity.setHint(getResources().getString(R.string.hint_city_null));

        } else {
            necessaryFields.setVisibility(View.GONE);
            mWrapperHouseCity.setHint(getResources().getString(R.string.hint_city));
        }

        if(mHouse != null) {
            if(mHouse.getTitle() != null) {
                mEditTextHouseTitle.setText(mHouse.getTitle());
            }

            if(mHouse.getRegion() != null) {
                mEditTextHouseRegion.setText(mHouse.getRegion().getName());
            }

            if(mHouse.getCity() != null) {
                mEditTextHouseCity.setText(mHouse.getCity().getName());
            }

            if(mHouse.getStreet() != null) {
                mEditTextHouseStreet.setText(mHouse.getStreet());
            }

            if(mHouse.getHouseNumber() != null) {
                mEditTextHouseNumber.setText(mHouse.getHouseNumber());
            }

            if(mHouse.getHouseBuilding() != null) {
                mEditTextHouseBuilding.setText(mHouse.getHouseBuilding());
            }

            if(mHouse.getApartment() != null) {
                mEditTextHouseApartment.setText(mHouse.getApartment());
            }

            if(mHouse.getArea() != 0) {
                mEditTextHouseArea.setText(String.valueOf(mHouse.getArea()));
            }

            if(mHouse.isPrivateHouse()) {
                setFieldsForPrivateHouse();
                mHouseType.setChecked(true);
            } else {
                setFieldsForApartmentHouse();
                mHouseType.setChecked(false);
            }

        } else {
            setFieldsForApartmentHouse();
            mHouseType.setChecked(false);
        }

        if(mEditTextHouseCity.getText().toString().trim().length() == 0) {
            mEditTextHouseStreet.setEnabled(false);
            mEditTextHouseNumber.setEnabled(false);
            mEditTextHouseBuilding.setEnabled(false);
            mEditTextHouseApartment.setEnabled(false);
            mEditTextHouseArea.setEnabled(false);

        }else {
            mEditTextHouseArea.setEnabled(true);
            mEditTextHouseStreet.setEnabled(true);

            if(mEditTextHouseStreet.getText().toString().trim().length() == 0) {
                mEditTextHouseNumber.setEnabled(false);
                mEditTextHouseBuilding.setEnabled(false);
                mEditTextHouseApartment.setEnabled(false);

            }else {
                mEditTextHouseNumber.setEnabled(true);
                mEditTextHouseBuilding.setEnabled(true);

                if(mEditTextHouseNumber.getText().toString().trim().length() == 0) {
                    mEditTextHouseApartment.setEnabled(false);

                }else {
                    mEditTextHouseApartment.setEnabled(true);
                }
            }
        }

        mEditTextHouseCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            @Override
            public void onTextChanged(final CharSequence c, int start, int before, int count) {
                if(mEditTextHouseCity.getText().toString().trim().length() == 0) {

                    mEditTextHouseStreet.setEnabled(false);
                    mEditTextHouseNumber.setEnabled(false);
                    mEditTextHouseBuilding.setEnabled(false);
                    mEditTextHouseApartment.setEnabled(false);
                    mEditTextHouseArea.setEnabled(false);
                }else {
                    mEditTextHouseArea.setEnabled(true);
                    mEditTextHouseStreet.setEnabled(true);

                    if(mEditTextHouseStreet.getText().toString().trim().length() == 0) {
                        mEditTextHouseNumber.setEnabled(false);
                        mEditTextHouseBuilding.setEnabled(false);
                        mEditTextHouseApartment.setEnabled(false);
                    }else {
                        mEditTextHouseNumber.setEnabled(true);
                        mEditTextHouseBuilding.setEnabled(true);

                        if(mEditTextHouseNumber.getText().toString().trim().length() == 0) {
                            mEditTextHouseApartment.setEnabled(false);
                        }else {
                            mEditTextHouseApartment.setEnabled(true);
                        }
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable c) {}
        });

        mEditTextHouseStreet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            @Override
            public void onTextChanged(final CharSequence c, int start, int before, int count) {
                if(mEditTextHouseStreet.getText().toString().trim().length() == 0) {
                    mEditTextHouseNumber.setEnabled(false);
                    mEditTextHouseBuilding.setEnabled(false);
                    mEditTextHouseApartment.setEnabled(false);
                }else {
                    mEditTextHouseNumber.setEnabled(true);
                    mEditTextHouseBuilding.setEnabled(true);

                    if(mEditTextHouseNumber.getText().toString().trim().length() == 0) {
                        mEditTextHouseApartment.setEnabled(false);
                    }else {
                        mEditTextHouseApartment.setEnabled(true);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable c) {}
        });

        mEditTextHouseNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            @Override
            public void onTextChanged(final CharSequence c, int start, int before, int count) {
                if(mEditTextHouseNumber.getText().toString().trim().length() == 0) {

                    mEditTextHouseBuilding.setEnabled(false);
                }else {
                    mEditTextHouseBuilding.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable c) {}
        });

        mHouseType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    setFieldsForPrivateHouse();
                } else {
                    setFieldsForApartmentHouse();
                }
            }
        });

        mEditTextHouseBuilding.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            @Override
            public void onTextChanged(final CharSequence c, int start, int before, int count) {
                if(mEditTextHouseBuilding.getText().toString().trim().length() == 0) {
                    mEditTextHouseApartment.setEnabled(false);
                }else {
                    mEditTextHouseApartment.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable c) {}
        });


        if (mHouse == null || mRealmHelper.getAll(House.class).size() < 2) {
            deleteHouseButton.setVisibility(View.GONE);
        } else {
            deleteHouseButton.setVisibility(View.VISIBLE);
        }

        updatePhotoView();
    }


    protected void setFieldsForPrivateHouse() {
        mWrapperHouseBuilding.setVisibility(View.GONE);
        mEditTextHouseBuilding.setVisibility(View.GONE);
        mWrapperHouseApartment.setVisibility(View.GONE);
        mEditTextHouseApartment.setVisibility(View.GONE);
    }

    protected void setFieldsForApartmentHouse() {
        mWrapperHouseBuilding.setVisibility(View.VISIBLE);
        mEditTextHouseBuilding.setVisibility(View.VISIBLE);
        mWrapperHouseApartment.setVisibility(View.VISIBLE);
        mEditTextHouseApartment.setVisibility(View.VISIBLE);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            GlideLoader.loadImage(HouseSettingsActivity.this, mPhotoView,
                    R.drawable.ic_action_house_camera);

        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), HouseSettingsActivity.this);
            mPhotoView.setImageBitmap(PictureUtils.cropToSquare(bitmap));
        }
    }

    private boolean checkFieldsForChanges() {
        boolean changed = false;

        String houseArea = mEditTextHouseArea.getText().toString().trim();
        float houseAreaFloat = 0;
        try {
            houseAreaFloat = Float.parseFloat(houseArea);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(mHouse != null) {
            if(!mHouse.getTitle().equals(mEditTextHouseTitle.getText().toString().trim())) {
                changed = true;
                return changed;
            }

            if(mHouse.getRegion() != null) {
                if(!mHouse.getRegion().getName()
                        .equals(mEditTextHouseRegion.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextHouseRegion.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mHouse.getCity() != null) {
                if(!mHouse.getCity().getName()
                        .equals(mEditTextHouseCity.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            }

            if(mHouse.getStreet() != null) {
                if(!mHouse.getStreet().equals(mEditTextHouseStreet.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextHouseStreet.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mHouse.isPrivateHouse() != mHouseType.isChecked()) {
                changed = true;
                return changed;
            }

            if(mHouse.getHouseNumber() != null) {
                if(!mHouse.getHouseNumber()
                        .equals(mEditTextHouseNumber.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextHouseNumber.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mHouse.getHouseBuilding() != null) {
                if(!mHouse.getHouseBuilding()
                        .equals(mEditTextHouseBuilding.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextHouseBuilding.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mHouse.getApartment() != null) {
                if(!mHouse.getApartment()
                        .equals(mEditTextHouseApartment.getText().toString().trim())) {
                    changed = true;
                    return changed;
                }
            } else {
                if(!mEditTextHouseApartment.getText().toString().trim().isEmpty()) {
                    changed = true;
                    return changed;
                }
            }

            if(mHouse.getArea() != houseAreaFloat) {
                changed = true;
                return changed;
            }

        } else {
            if(!mEditTextHouseTitle.getText().toString().trim().isEmpty()
                    || !mEditTextHouseRegion.getText().toString().trim().isEmpty()
                    || !mEditTextHouseCity.getText().toString().trim().isEmpty()
                    || !mEditTextHouseStreet.getText().toString().trim().isEmpty()
                    || !mEditTextHouseNumber.getText().toString().trim().isEmpty()
                    || !mEditTextHouseBuilding.getText().toString().trim().isEmpty()
                    || !mEditTextHouseApartment.getText().toString().trim().isEmpty()
                    || mHouseType.isChecked()
                    || !mEditTextHouseArea.getText().toString().trim().isEmpty()) {

                changed = true;
                return changed;
            }
        }
        return changed;
    }

    private void saveHouseFields() {
        Region region;
        City city;

        String textRegionName = mEditTextHouseRegion.getText().toString().trim();
        String textCityName = mEditTextHouseCity.getText().toString().trim();

        if(mHouse != null && mHouse.getRegion() != null
                && mHouse.getRegion().getName() != null
                && !mHouse.getRegion().getName().equals(textRegionName)) {

            region = mRealmHelper.getRealm().where(Region.class)
                    .equalTo("mName", mHouse.getRegion().getName())
                    .findFirst();

        } else {
            region = mRealmHelper.addOrUpdate(new Region(textRegionName));
        }

        if(mHouse != null && mHouse.getCity() != null
                && mHouse.getCity().getName() != null
                && !mHouse.getCity().getName().equals(textCityName)) {

            city = mRealmHelper.getRealm().where(City.class)
                    .equalTo("mName", mHouse.getCity().getName())
                    .findFirst();

        } else {
            city = mRealmHelper.addOrUpdate(new City(textCityName));
        }


        mRealmHelper.beginTransaction();

        region.setName(textRegionName);
        city.setName(textCityName);

        if(mHouse != null) {
            mHouse.setRegion(region);
        }

        if(mHouse != null) {
            mHouse.setCity(city);
        }
        mRealmHelper.commitTransaction();

        mRealmHelper.beginTransaction();
        if(mEditTextHouseTitle.getText().toString().trim().equals("")){
            mHouse.setTitle(getResources().getString(R.string.text_my_house));
        } else {
            mHouse.setTitle(mEditTextHouseTitle.getText().toString());
        }

        if(mEditTextHouseStreet.isEnabled()) {
            mHouse.setStreet(mEditTextHouseStreet.getText().toString());
        }

        if(mHouseType.isChecked()) {
            mHouse.setPrivateHouse(true);
            if(mEditTextHouseBuilding.isEnabled()) {
                mHouse.setHouseBuilding("");
            }
            if(mEditTextHouseApartment.isEnabled()) {
                mHouse.setApartment("");
            }
        } else {
            mHouse.setPrivateHouse(false);
            if(mEditTextHouseBuilding.isEnabled()) {
                mHouse.setHouseBuilding(mEditTextHouseBuilding.getText().toString());
            }
            if(mEditTextHouseApartment.isEnabled()) {
                mHouse.setApartment(mEditTextHouseApartment.getText().toString());
            }
        }

        if(mEditTextHouseNumber.isEnabled()) {
            mHouse.setHouseNumber(mEditTextHouseNumber.getText().toString());
        }

        try {
            if(!mEditTextHouseArea.getText().toString().equals(""))
                mHouse.setArea(Float.parseFloat(mEditTextHouseArea
                        .getText().toString().trim()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mRealmHelper.commitTransaction();

        // Зберігаєм id активної оселі
        if(mRealmHelper.getAll(House.class).size() == 1) {
            SharedPreferenceHelper
                    .getInstance()
                    .saveStringObject(SharedPreferenceHelper.Key.SELECTED_HOUSE,
                            mHouse.getId());

            startActivity(new Intent(this, MainActivity.class));
        }

        setResult(RESULT_OK);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mRealmHelper.getAll(House.class).size() == 0) {
                    if(mEditTextHouseCity.getText().toString().trim().length() > 0) {
                        mWrapperHouseCity.setError(getString(R.string
                                .warning_unsaved_data));
                    } else if(mEditTextHouseCity.getText().toString().trim().length() == 0){
                        mWrapperHouseCity.setError(getString(R.string
                                .warning_house_city));
                    }
                } else {
                    safeExit();
                }

                return(true);
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        if(mRealmHelper.getAll(House.class).size() != 0) {

            setResult(RESULT_CANCELED);
            safeExit();
        }
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

            if(mHouse == null && mPhotoFile.exists()) {
                getApplicationContext().deleteFile(mHouse.getPhotoFilename());
            }
        } else {
            finish();
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
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch(permsRequestCode){

            case PERMISSIONS_REQUEST_CODE_CAMERA:
                getSharedPreferenceHelper().saveBooleanObject(
                        SharedPreferenceHelper.Key.PERMISSION_CAMERA, false);

                if (PermissionHelper.hasPermission(this, "android.permission.CAMERA")) {
                    mPhotoView.setVisibility(View.VISIBLE);
                }

                break;

            case PERMISSIONS_REQUEST_CODE_EXTERNAL_STORAGE:
                getSharedPreferenceHelper().saveBooleanObject(
                        SharedPreferenceHelper.Key.PERMISSION_READ_EXTERNAL_STORAGE, false);

                if (PermissionHelper.hasPermission(this,
                        "android.permission.READ_EXTERNAL_STORAGE")) {
                    mPhotoView.setVisibility(View.VISIBLE);
                }

                break;
        }
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
    }


    /*
    * In this method, Start PlaceAutocomplete activity
    * PlaceAutocomplete activity provides a -
    * search box to search Google places
    */
    public void findPlace(int requestCode) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, requestCode);

        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REGION_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // retrieve the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);

                mEditTextHouseRegion.setText(place.getName());
            }
//            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                Status status = PlaceAutocomplete.getStatus(this, data);
//            }
        }

        if (requestCode == CITY_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // retrieve the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);

                mEditTextHouseCity.setText(place.getName());
            }
        }

        if (requestCode == REQUEST_PHOTO) {

            setResult(RESULT_OK);
            updatePhotoView();
        }
    }
}