package com.thehatefulsix.tariffcounter.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

public class GlideLoader {

    public static void loadImage(@NonNull Context context, @NonNull final ImageView imageView,
                                 @NonNull String path){
        Glide.with(context)
                .load(path)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                        imageView.setImageBitmap(arg0);
                    }
                });
    }

    public static void loadImage(@NonNull Context context, @NonNull final ImageView imageView,
                                 @DrawableRes int path){
        Glide.with(context)
                .load(path)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                        imageView.setImageBitmap(arg0);
                    }
                });
    }

    public static void loadImage(@NonNull Context context, @NonNull final ImageView imageView,
                                 @NonNull File file){
        Glide.with(context)
                .load(file)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                        imageView.setImageBitmap(arg0);
                    }
                });
    }
}
