package com.thehatefulsix.tariffcounter.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.activities.core.ActivityWithMethods;
import com.thehatefulsix.tariffcounter.utils.DownloadProgressDialog;
import com.thehatefulsix.tariffcounter.utils.PermissionHelper;
import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;
import com.thehatefulsix.tariffcounter.utils.SnackBarHelper;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiWall;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKScopes;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class EditNewsActivity extends ActivityWithMethods {

    private static final int REQUEST_GALLERY = 102;
    private static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";

    private static final int PERMISSIONS_REQUEST_CODE = 505;

    @BindView(R.id.et_content) TextInputEditText mEditTextContent;
    @BindView(R.id.text_input_layout) TextInputLayout mTextInputLayout;
    @BindView(R.id.iv_camera) ImageView mImageViewIcon;

    private Uri mImageUri;
    private MenuItem mItemAddImage;

    private DownloadProgressDialog mProgressDialog;

    @Override
    protected String changeActionBarTitle() {
        return getString(R.string.news);
    }

    @Override
    protected int contentView() {
        return R.layout.activity_edit_news;
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PermissionHelper.shouldWeAsk(
                SharedPreferenceHelper.Key.PERMISSION_READ_EXTERNAL_STORAGE)){
            PermissionHelper.askPermissions(this, "android.permission.READ_EXTERNAL_STORAGE",
                    PERMISSIONS_REQUEST_CODE,
                    SharedPreferenceHelper.Key.PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_news, menu);
        mItemAddImage = menu.findItem(R.id.action_add_image);

        if (!PermissionHelper.hasPermission(this, "android.permission.READ_EXTERNAL_STORAGE")) {
            mItemAddImage.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            default:
                return super.onOptionsItemSelected(item);

            case R.id.action_add_image : {
                final Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent,
                        getResources().getString(R.string.gallery_intent_title)), REQUEST_GALLERY);
                return true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                mProgressDialog = new DownloadProgressDialog(EditNewsActivity.this,
                        getString(R.string.loading), false);
                mProgressDialog.show();

                if (mImageUri != null) {
                    try {
                        final Bitmap photo = MediaStore.Images.Media.getBitmap(
                                EditNewsActivity.this.getContentResolver(), mImageUri);

                        final VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo,
                                        VKImageParameters.jpgImage(0.9f)), 0, getResources().getInteger(R.integer.group_id));

                        request.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                photo.recycle();

                                final VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);

                                addPost(photoModel);
                            }

                            @Override
                            public void onError(VKError error) {
                                mProgressDialog.stop();
                                showSnackBar(error.toString());
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    addPost(null);
                }
            }

            @Override
            public void onError(VKError error) {
                showSnackBar(error.toString());
            }
        })){

            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY && data != null) {
                onSelectFromGalleryResult(data);
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.btn_add)
    public void add() {
        VKSdk.login(this, VKScopes.WALL, VKScopes.GROUPS, VKScopes.PHOTOS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mImageUri != null) {
            outState.putParcelable(KEY_IMAGE_URI, mImageUri);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(KEY_IMAGE_URI)) {
            mImageUri = savedInstanceState.getParcelable(KEY_IMAGE_URI);
            setBitmap(mImageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        switch(permsRequestCode){

            case PERMISSIONS_REQUEST_CODE:

                getSharedPreferenceHelper().saveBooleanObject(
                        SharedPreferenceHelper.Key.PERMISSION_READ_EXTERNAL_STORAGE, false);

                if (PermissionHelper.hasPermission(this, "android.permission.READ_EXTERNAL_STORAGE") &&
                        mItemAddImage != null && !mItemAddImage.isVisible()) {
                    mItemAddImage.setVisible(true);
                }

                break;
        }
    }

    private void onSelectFromGalleryResult(final Intent data) {
        mImageUri = data.getData();
        setBitmap(mImageUri);
    }

    private void setBitmap(final Uri uri) {
        try {
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    getApplicationContext().getContentResolver(), uri);
            final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);

            mImageViewIcon.setImageBitmap(scaledBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSnackBar(final String message) {
        SnackBarHelper.show(this, findViewById(R.id.activity_edit_news), message);
    }

    private void addPost(@Nullable VKApiPhoto photoModel) {
        final String message = mEditTextContent.getText().toString().trim();
        final Map<String, Object> parameters = new HashMap<>();
        int ownerId = -getResources().getInteger(R.integer.group_id);

        parameters.put(VKApiConst.OWNER_ID, ownerId);
        parameters.put(VKApiConst.MESSAGE, message);

        if (photoModel != null) {
            final VKAttachments attachments = new VKAttachments();
            attachments.add(photoModel);
            parameters.put(VKApiConst.ATTACHMENTS, attachments);
        }

        final VKRequest vkRequest = new VKApiWall().post(new VKParameters(parameters));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                mProgressDialog.stop();
                showSnackBar("News suggested");

                EditNewsActivity.this.setResult(RESULT_OK);
            }

            @Override
            public void onError(VKError error) {
                mProgressDialog.stop();
                showSnackBar(error.toString());
            }
        });
    }
}
