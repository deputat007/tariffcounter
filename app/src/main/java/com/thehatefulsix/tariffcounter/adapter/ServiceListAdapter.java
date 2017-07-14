package com.thehatefulsix.tariffcounter.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.utils.GlideLoader;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.ServiceController;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder>{

    private List<Service> mServices;
    private Context mContext;
    private OnClickListener mOnClickListener;

    public ServiceListAdapter(List<Service> services, Context context,
                              OnClickListener onClickListener) {
        mServices = services;
        mContext = context;
        mOnClickListener = onClickListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mServices.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_item_service, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Service service = mServices.get(position);

        GlideLoader.loadImage(mContext, viewHolder.mIcon, service.getIcon());
        viewHolder.mTVServiceName.setText(
                service.getName() != null ? service.getName() :
                        ServiceController.serviceTypeGetString(service.getType(), mContext));

        long days = RealmHelper.getInstance().getDifferent(service.getId());

        String lastPayment;
        if (days == 1){
            lastPayment = days + " " + mContext.getString(R.string.day);
        }else {
            lastPayment = days + " " + mContext.getString(R.string.days);
        }

        viewHolder.mTVLastPayment.setText(lastPayment);
    }

    public void setServices(List<Service> services) {
        mServices = services;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.context) CardView mCardView;
        @BindView(R.id.iv_service_icon) ImageView mIcon;
        @BindView(R.id.tv_service_name) TextView mTVServiceName;
        @BindView(R.id.tv_last_payment) TextView mTVLastPayment;

        ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemClick(v, mServices.get(getAdapterPosition()).getId());
                }
            });

            mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mOnClickListener.onItemLongClick(v,
                            mServices.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    public interface OnClickListener {
        void onItemClick(View view, String id);

        boolean onItemLongClick(View view, String id);
    }
}
