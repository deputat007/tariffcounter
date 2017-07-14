package com.thehatefulsix.tariffcounter.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.utils.GlideLoader;
import com.thehatefulsix.tariffcounter.utils.PictureUtils;

import java.io.File;
import java.util.List;

public class HousePagerAdapter extends PagerAdapter {

    private List<House> mHouses;
    private LayoutInflater mLayoutInflater;
    private OnClickListener mListener;
    private Context mContext;

    public HousePagerAdapter(final Context context, final List<House> houses,
                             final OnClickListener listener) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mHouses = houses;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mHouses.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View v = mLayoutInflater.inflate(R.layout.card_view_house_item, container, false);
        final House house = mHouses.get(position);

        final TextView houseTitle = (TextView) v.findViewById(R.id.tv_house_name);
        final TextView houseAddress = (TextView) v.findViewById(R.id.tv_house_address);
        final TextView houseCity= (TextView) v.findViewById(R.id.tv_house_city);

        final View context = v.findViewById(R.id.context);
        final ImageView settingsButton = (ImageView) v.findViewById(R.id.iv_edit);
        final ImageView icon = (ImageView) v.findViewById(R.id.service_image);

        final File photoFile = PictureUtils.getPhotoFile(house, mContext);

        if(photoFile == null || !photoFile.exists()) {
            GlideLoader.loadImage(mContext, icon, R.mipmap.ic_launcher);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), 300, 300);
            icon.setImageBitmap(PictureUtils.cropToSquare(bitmap));
        }

        houseTitle.setText(house.getTitle());
        houseAddress.setText(house.getAddress(mContext));
        houseCity.setText(house.getCity().getName());

        if (houseAddress.getText().toString().trim().isEmpty()){
            houseAddress.setVisibility(View.GONE);
        }else {
            houseAddress.setVisibility(View.VISIBLE);
        }

        context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onHouseClick(house.getId());
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onSettingsClick(house.getId());
            }
        });

        container.addView(v);
        return v;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

    public interface OnClickListener {
        void onHouseClick(String id);
        void onSettingsClick(String id);
    }
}
