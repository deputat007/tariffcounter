package com.thehatefulsix.tariffcounter.adapter;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.activities.EditBillActivity;
import com.thehatefulsix.tariffcounter.models.Bill;
import com.thehatefulsix.tariffcounter.models.Rate;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.utils.GlideLoader;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.ServiceController;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thehatefulsix.tariffcounter.fragments.BillsFragment.ACTION_UPDATE_FRAGMENT;
import static com.thehatefulsix.tariffcounter.fragments.HistoryFragment.RECEIPT_PHOTO;


public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.ViewHolder> {


    private Activity mActivity;
    private Fragment mFragment;
    private ArrayList<Bill> mBills;
    private Bill mSelectedBill;
    private ArrayList<String> mCheckedBillsId;

    public BillsAdapter(Activity activity, Fragment fragment, ArrayList<Bill> bills, ArrayList<String> checkedBillsId) {
        mActivity = activity;
        mFragment = fragment;
        mBills = bills;
        mCheckedBillsId = checkedBillsId;
    }

    public void setContent (ArrayList<Bill> bills){
        mBills = bills;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Bill bill = mBills.get(position);
        setServiceIcon(viewHolder, bill);
        setPaymentPeriod(viewHolder, bill);
        setDate(viewHolder, position);
        setPaymentAmount(viewHolder, bill);
        setServiceType(viewHolder, bill);
        setShowReceiptButtonVisibility(viewHolder, bill);
        setIsPaid (viewHolder, bill);
        setCategoryVisibility (viewHolder, position);
        setBillIsChecked(viewHolder, bill);

    }

    private void setServiceIcon(ViewHolder viewHolder, Bill bill) {
        GlideLoader.loadImage(mActivity, viewHolder.serviceIcon, bill.getService().getIcon());
    }

    private void setBillIsChecked(ViewHolder viewHolder, Bill bill) {
        if(mCheckedBillsId.contains(bill.getId())){
            viewHolder.container.setCardBackgroundColor(mActivity.getResources().getColor(R.color.amber_200));
        } else {
            viewHolder.container.setCardBackgroundColor(0xffffffff);
        }
    }

    private void setCategoryVisibility(ViewHolder viewHolder, int position) {
        boolean isPaidCurrentBill = mBills.get(position).isPaid();
        if (position == 0){
            viewHolder.category.setVisibility(View.VISIBLE);
            if (isPaidCurrentBill) {
                viewHolder.category.setText(R.string.paid_bills);
            } else {
                viewHolder.category.setText(R.string.unpaid_bills);
            }
        } else {
            boolean isPaidPreviousBill = mBills.get(position-1).isPaid();
            if (isPaidPreviousBill != isPaidCurrentBill){
                viewHolder.category.setVisibility(View.VISIBLE);
                viewHolder.category.setText(R.string.paid_bills);
            } else {
                viewHolder.category.setVisibility(View.GONE);
            }
        }
    }

    private void setIsPaid(ViewHolder viewHolder, Bill bill) {
        if(!bill.isPaid()){
            viewHolder.paymentAmount.setTextColor(mActivity.getResources().getColor(R.color.red_600));
            if (bill.getService().getRate().isHasRate() && bill.getCurrentMark() < bill.getPreviousMark()){
                viewHolder.paymentAmount.setText(R.string.enter_meter_reading);
            }
        }
    }

    private void setServiceType(ViewHolder viewHolder, Bill bill) {
        Service.Type serviceType = bill.getService().getType();
        String serviceTypeString;
        if (serviceType != Service.Type.OTHER){
            serviceTypeString = ServiceController.serviceTypeGetString(bill.getService().getType(), mActivity);
        } else {
            serviceTypeString = bill.getService().getName();
        }
        viewHolder.serviceType.setText(serviceTypeString);
    }



    private void setPaymentPeriod(ViewHolder viewHolder, Bill bill) {
        Locale currentLocale = mActivity.getResources().getConfiguration().locale;
        String paymentPeriod;
        GregorianCalendar periodStart = new GregorianCalendar();
        GregorianCalendar periodEnd = new GregorianCalendar();
        periodStart.setTimeInMillis(bill.getPeriodStart());
        periodEnd.setTimeInMillis(bill.getPeriodEnd());
        paymentPeriod = DateFormat.getDateInstance(DateFormat.MEDIUM, currentLocale).format(periodStart.getTime()) +
                " - " +
                DateFormat.getDateInstance(DateFormat.MEDIUM, currentLocale).format(periodEnd.getTime());

        viewHolder.paymentPeriod.setText(paymentPeriod);
    }


    private void setDate(ViewHolder viewHolder, int position) {
        if(position != 0){
            Bill currentBill = mBills.get(position);
            Bill previousBill = mBills.get(position-1);
            GregorianCalendar currentBillStart = new GregorianCalendar();
            GregorianCalendar previousBillStart = new GregorianCalendar();
            currentBillStart.setTimeInMillis(currentBill.getPeriodStart());
            previousBillStart.setTimeInMillis(previousBill.getPeriodStart());
            if (currentBillStart.get(Calendar.MONTH) != previousBillStart.get(Calendar.MONTH) ||
                    previousBill.isPaid() != currentBill.isPaid()){
                String date = getShortDateInstanceWithoutDays(currentBillStart.getTimeInMillis());
                viewHolder.date.setText(date);
                viewHolder.date.setVisibility(View.VISIBLE);
            } else {
                viewHolder.date.setVisibility(View.GONE);
            }
        } else {
            Bill currentBill = mBills.get(position);
            GregorianCalendar currentBillStart = new GregorianCalendar();
            currentBillStart.setTimeInMillis(currentBill.getPeriodStart());
            String date = getShortDateInstanceWithoutDays(currentBillStart.getTimeInMillis());
            viewHolder.date.setText(date);
            viewHolder.date.setVisibility(View.VISIBLE);
        }

    }

    private String getShortDateInstanceWithoutDays(long mills) {
        int flags = DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_NO_MONTH_DAY;
        return DateUtils.formatDateTime(mActivity, mills, flags);
    }

    private void setPaymentAmount(ViewHolder viewHolder, Bill bill) {
        float payment = bill.getSum();
        viewHolder.paymentAmount.setText(Float.toString(payment));
    }

    private void setShowReceiptButtonVisibility(ViewHolder viewHolder, Bill bill){
        String pathToPhoto = bill.getPathToPhoto();
        if (pathToPhoto != null && !pathToPhoto.isEmpty()){
            viewHolder.showReceipt.setVisibility(View.VISIBLE);
        } else {
            viewHolder.showReceipt.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return mBills.size();
    }


    public Bill getSelectedBill() {
        return mSelectedBill;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        //bill elements
        @BindView(R.id.container) CardView container;
        @BindView(R.id.iv_service_icon) ImageView serviceIcon;
        @BindView(R.id.tv_payment_period) TextView paymentPeriod;
        @BindView(R.id.tv_payment_amount) TextView paymentAmount;
        @BindView(R.id.tv_service_type) TextView serviceType;
        @BindView(R.id.tv_month_and_year) TextView date;
        @BindView(R.id.tv_bill_category) TextView category;
        @BindView(R.id.btn_show_receipt_photo) ImageButton showReceipt;
        @BindView(R.id.btn_start_camera) ImageButton startCamera;

        ViewHolder (View v){
            super(v);

            ButterKnife.bind(this, v);

            container.setOnClickListener(this);
            container.setOnLongClickListener(this);
            showReceipt.setOnClickListener(this);
            startCamera.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            int position = getAdapterPosition();
            mSelectedBill = mBills.get(position);
            switch (v.getId()){
                case R.id.container:
                    mActivity.startActivity(EditBillActivity.getInstance(mActivity,
                            mSelectedBill.getService().getId(), mSelectedBill.getId()));
                    break;
                case R.id.btn_start_camera:
                    File path = new File(mActivity.getExternalFilesDir(Environment.DIRECTORY_DCIM), "Receipt");
                    if (!path.exists()) path.mkdirs();
                    File photo = new File(path, mSelectedBill.getId() + ".jpg");
                    Uri photoUri = Uri.fromFile(photo);

                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    mFragment.startActivityForResult(intent, RECEIPT_PHOTO);
                    break;
                case R.id.btn_show_receipt_photo:
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(mSelectedBill.getPathToPhoto()),"image/*");
                    mActivity.startActivity(intent);
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            mSelectedBill = mBills.get(position);
            switch (v.getId()){
                case R.id.container:
                    if (!mSelectedBill.isPaid()){
                        if(!mCheckedBillsId.contains(mSelectedBill.getId())){
                            mCheckedBillsId.add(mSelectedBill.getId());
                            container.setCardBackgroundColor(mActivity.getResources().getColor(R.color.amber_200));
                        } else {
                            mCheckedBillsId.remove(mSelectedBill.getId());
                            container.setCardBackgroundColor(0xffffffff);
                        }
                        mActivity.sendBroadcast(new Intent(ACTION_UPDATE_FRAGMENT));
                    }
                    break;
            }
            return false;
        }
    }
}
