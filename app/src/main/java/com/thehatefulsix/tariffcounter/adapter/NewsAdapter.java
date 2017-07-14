package com.thehatefulsix.tariffcounter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.Community;
import com.thehatefulsix.tariffcounter.models.WallPost;
import com.thehatefulsix.tariffcounter.utils.DateFormatter;
import com.thehatefulsix.tariffcounter.utils.GlideLoader;

import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    private Community mCommunity;
    private Context mContext;
    private Callback mCallback;

    public NewsAdapter(@NonNull Community community, @NonNull Context context,
                       @NonNull Callback callback) {
        mCommunity = community;
        mContext = context;
        mCallback = callback;
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(NewsHolder holder, int position) {
        final WallPost wallPost = mCommunity.getWallPosts().get(position);

        holder.bindPost(wallPost);
        holder.bindGroup(mCommunity);
    }

    @Override
    public int getItemCount() {
        return mCommunity.getWallPosts().size();
    }

    class NewsHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_text) TextView mTextView;
        @BindView(R.id.tv_date) TextView mDaTextView;
        @BindView(R.id.tv_group_name) TextView mGroupName;

        @BindView(R.id.iv_group_icon) ImageView mGroupIcon;

        @BindView(R.id.tv_link) TextView mLinkTextView;
        @BindView(R.id.iv_image) ImageView mImageView;

        NewsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressWarnings("deprecation")
        void bindPost(final WallPost wallPost) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(wallPost.getDate());

            mTextView.setText( wallPost.getText());
            mDaTextView.setText(DateFormatter.parseData(calendar.getTime(), mContext,
                    DateFormatter.DATE_WITH_TIME_PATTERN));

            if (wallPost.getLink() != null){
                mLinkTextView.setText(
                        Html.fromHtml("<a href=" + wallPost.getLink() + ">" +
                                wallPost.getLink() +"</a> "));
                mLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
            }else {
                mLinkTextView.setVisibility(View.GONE);
            }

            if (wallPost.getPhotoUrl() != null){
                GlideLoader.loadImage(mContext, mImageView, wallPost.getPhotoUrl());
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.onImageClicked(wallPost, mImageView);
                    }
                });
            }else {
                mImageView.setVisibility(View.GONE);
            }
        }

        @SuppressWarnings("deprecation")
        void bindGroup(Community community) {
            mGroupName.setText(
                    Html.fromHtml("<a href=" + mContext.getString(R.string.group_link) + ">" +
                            community.getName() +"</a> "));
            mGroupName.setMovementMethod(LinkMovementMethod.getInstance());

            GlideLoader.loadImage(mContext, mGroupIcon, community.getIconPath());
        }
    }

    public interface Callback{
        void onImageClicked(final @NonNull WallPost wallPost, final @NonNull View view);
    }
}
