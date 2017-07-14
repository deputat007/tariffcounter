package com.thehatefulsix.tariffcounter.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.adapter.PaymentHistoryAdapter;
import com.thehatefulsix.tariffcounter.fragments.core.FragmentWithNavigationTabBar;
import com.thehatefulsix.tariffcounter.models.Bill;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.ServiceController;
import com.thehatefulsix.tariffcounter.utils.SharedPreferenceHelper;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;

import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;
import static com.thehatefulsix.tariffcounter.fragments.BillsFragment.ACTION_UPDATE_FRAGMENT;

public class HistoryFragment extends FragmentWithNavigationTabBar {

    private ArrayList<Bill> mPaidBills;
    private ArrayList<String> mServicesToExclude;
    private PaymentHistoryAdapter mPaymentHistoryAdapter;
    private RecyclerView mHistory;
    private TextView mNoBills;
    private String mCurrentHouseId;

    public static final int RECEIPT_PHOTO = 0;
    private BroadcastReceiver mUpdateReceiver;

    private final static String TAG = "HistoryFragment";


    @Override
    public void onResume() {
        super.onResume();
        mUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateFragment();
                Log.i(TAG, "in Receiver");
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_FRAGMENT);
        getActivity().registerReceiver(mUpdateReceiver, intentFilter);
        Log.i(TAG, "onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "pause");
        getActivity().unregisterReceiver(mUpdateReceiver);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        mServicesToExclude = getSharedPreferenceHelper().getObject(SharedPreferenceHelper.Key.FILTER_PAYMENT_HISTORY, type);
        if (mServicesToExclude == null){
            mServicesToExclude = new ArrayList<>();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_payment_history, menu);
        String selectedHouseId = SharedPreferenceHelper.getInstance().getStringObject(SharedPreferenceHelper.Key.SELECTED_HOUSE, "");
        RealmResults<Bill> bills = RealmHelper.getInstance().getAllBills(true).where().equalTo("mService.mIdHouse", selectedHouseId).findAll();
        HashSet<String> menuItemLabels = new HashSet<>();
        String serviceName;
        for (Bill bill : bills){
            if (bill.getService().getType().equals(Service.Type.OTHER)){
                serviceName = bill.getService().getName();
            } else {
                serviceName = ServiceController.serviceTypeGetString(bill.getService().getType(), getActivity());
            }
            menuItemLabels.add(serviceName);
        }
        int id = 0;
        SubMenu filterMenu = menu.findItem(R.id.menu_payment_history_filter).getSubMenu();
        for (String itemLabel : menuItemLabels){
            if (mServicesToExclude.contains(itemLabel)){
                filterMenu.add(R.id.menu_payment_history_filter_item_group, id, Menu.NONE, itemLabel).setChecked(false);
            } else {
                filterMenu.add(R.id.menu_payment_history_filter_item_group, id, Menu.NONE, itemLabel).setChecked(true);
            }
            id++;
        }
        filterMenu.setGroupCheckable(R.id.menu_payment_history_filter_item_group, true, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isCheckable()){
            item.setChecked(!item.isChecked());
            if(!item.isChecked()){
                mServicesToExclude.add(item.getTitle().toString());
            } else {
                mServicesToExclude.remove(item.getTitle().toString());
            }
            getSharedPreferenceHelper().saveObject(SharedPreferenceHelper.Key.FILTER_PAYMENT_HISTORY, mServicesToExclude);
            mPaidBills = getPaidBills();
            mPaymentHistoryAdapter.setContent(mPaidBills);
            return true;
        }
        return false;
    }

    private void sortBills(ArrayList<Bill> paidBills) {
        Collections.sort(paidBills, new Comparator<Bill>() {
            @Override
            public int compare(Bill o1, Bill o2) {
                GregorianCalendar start1 = new GregorianCalendar();
                GregorianCalendar start2 = new GregorianCalendar();
                start1.setTimeInMillis(o1.getPeriodStart());
                start2.setTimeInMillis(o2.getPeriodStart());
                if ((start1.get(Calendar.MONTH)+start1.get(Calendar.YEAR)*12) < (start2.get(Calendar.MONTH)+start2.get(Calendar.YEAR)*12)){
                    return 1;
                }
                if ((start1.get(Calendar.MONTH)+start1.get(Calendar.YEAR)*12) > (start2.get(Calendar.MONTH)+start2.get(Calendar.YEAR)*12)){
                    return -1;
                }
                String serviceType1 = ServiceController.serviceTypeGetString(o1.getService().getType(), getActivity());
                String serviceType2 = ServiceController.serviceTypeGetString(o2.getService().getType(), getActivity());
                int serviceTypeComparison = serviceType1.compareTo(serviceType2);
                if (serviceTypeComparison != 0){
                    return serviceTypeComparison;
                } else {
                    if (start1.before(start2)){
                        return 1;
                    }
                    if (start2.before(start1)){
                        return -1;
                    }
                    return 0;
                }
            }
        });
    }

    @Override
    public int changeActionBarTitle() {
        return R.string.fragment_history_tittle;
    }

    @Override
    protected int contentView() {
        return R.layout.fragment_history;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);

        House currentHouse = getCurrentHouse();
        if (currentHouse != null){
            mCurrentHouseId = currentHouse.getId();
            mHistory = (RecyclerView) v.findViewById(R.id.rv_payment_history);
            mNoBills = (TextView) v.findViewById(R.id.tv_no_bills);

            updateContent();
            mHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        return v;
    }

    private void updateContent() {
        mPaidBills = getPaidBills();
        if (mPaymentHistoryAdapter == null){
            mPaymentHistoryAdapter = new PaymentHistoryAdapter(getActivity(), this, mPaidBills);
            mHistory.setAdapter(mPaymentHistoryAdapter);
        } else {
            mPaymentHistoryAdapter.setContent(mPaidBills);
        }
        if (mPaidBills.size() > 0){
            setHasOptionsMenu(true);
            mNoBills.setVisibility(View.GONE);
        } else {
            setHasOptionsMenu(false);
            mNoBills.setVisibility(View.VISIBLE);
        }
    }

    public void updateFragment (){
        String currentHouseId = getCurrentHouse().getId();
        mCurrentHouseId = currentHouseId;
        updateContent();
    }

    private ArrayList<Bill> getPaidBills() {
        ArrayList<Bill> paidBillsList = new ArrayList<>();
        String currentHouseId = SharedPreferenceHelper.getInstance().getStringObject(SharedPreferenceHelper.Key.SELECTED_HOUSE, "");
        RealmResults<Bill> paidBills = RealmHelper.getInstance().getAllBills(true).where().equalTo("mService.mIdHouse", currentHouseId).findAll();
        for (String serviceTypeName : mServicesToExclude){
            Service.Type serviceType = ServiceController.getServiceTypeFromName(serviceTypeName, getActivity());
            paidBills = paidBills.where().notEqualTo("mService.mType", serviceType.toString()).findAll();
        }
        paidBillsList.addAll(paidBills.subList(0, paidBills.size()));
        sortBills(paidBillsList);
        return paidBillsList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RECEIPT_PHOTO:
                if (resultCode == RESULT_OK){
                    Bill selectedBill = mPaymentHistoryAdapter.getSelectedBill();
                    File path = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM), "Receipt");
                    if (!path.exists()) path.mkdirs();
                    File photo = new File(path, selectedBill.getId() + ".jpg");
                    Uri photoUri = Uri.fromFile(photo);
                    getRealmHelper().beginTransaction();
                    mPaymentHistoryAdapter.getSelectedBill().setPathToPhoto(photoUri.toString());
                    getRealmHelper().commitTransaction();
                    updateFragment();
                }
                break;
        }
    }
}
