package com.thehatefulsix.tariffcounter.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.activities.EditBillActivity;
import com.thehatefulsix.tariffcounter.adapter.BillsAdapter;
import com.thehatefulsix.tariffcounter.fragments.core.FragmentWithNavigationTabBar;
import com.thehatefulsix.tariffcounter.models.Bill;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.models.Service;
import com.thehatefulsix.tariffcounter.utils.ServiceController;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;
import static com.thehatefulsix.tariffcounter.fragments.HistoryFragment.RECEIPT_PHOTO;

public class BillsFragment extends FragmentWithNavigationTabBar implements View.OnClickListener {

    private static final String KEY_SELECTED_SERVICE = "KEY_SELECTED_SERVICE";
    private static final String KEY_CHECKED_BILLS_ID = "KEY_CHECKED_BILLS_ID";

    public static final String ACTION_UPDATE_FRAGMENT = "com.thehatefulsix.tariffcounter.update_fragment";

    private RecyclerView mRecyclerView;
    private FloatingActionMenu mFloatingActionMenu;
    private BillsAdapter mBillsAdapter;
    private String mCurrentHouseId;
    private TextView mNoBills;
    private BroadcastReceiver mUpdateReceiver;
    private Menu mMenu;
    private ArrayList<String> mCheckedBillsId;

    private final static String TAG = "BillsFragment";

    public static BillsFragment newInstance() {

        Bundle args = new Bundle();

        BillsFragment fragment = new BillsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int changeActionBarTitle() {
        return R.string.bills;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume11111");
        super.onResume();
        updateFragment();
        Log.i(TAG, "onResume22222");
        mUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateFragment();
                Log.i(TAG, "in Receiver");
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_FRAGMENT);
        getActivity().registerReceiver(mUpdateReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "pause");
        getActivity().unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "onCreateMenu");
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        inflater.inflate(R.menu.menu_fragment_bills, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.i(TAG, "onPrepareMenu");
        if (!mCheckedBillsId.isEmpty()) {
            menu.findItem(R.id.menu_bills_fragment_mark_paid).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_bills_fragment_mark_paid:{
                Bill bill = null;
                for(String billId : mCheckedBillsId){
                    bill = getRealmHelper().getObjectById(Bill.class, billId);
                    if (bill.getService().getRate().isHasRate()){
                        if (bill.getCurrentMark() >= bill.getPreviousMark()){
                            getRealmHelper().beginTransaction();
                            bill.setPaid(true);
                            bill.setPaymentTime(new GregorianCalendar().getTimeInMillis());
                            getRealmHelper().commitTransaction();

                            if (bill.getService().isSubsidy()) {
                                getRealmHelper().beginTransaction();
                                float consumedAmount = bill.getCurrentMark() - bill.getPreviousMark();

                                bill.getService().getSubsidy().setOverpayment(
                                        bill.getService().getSubsidy().getOverpayment() +
                                                bill.getService().getSubsidy().getSocialNorm() - consumedAmount);
                                getRealmHelper().commitTransaction();
                            }
                        }
                    } else {
                        getRealmHelper().beginTransaction();
                        bill.setPaid(true);
                        bill.setPaymentTime(new GregorianCalendar().getTimeInMillis());
                        getRealmHelper().commitTransaction();
                    }
                }

                mCheckedBillsId.clear();
                getActivity().sendBroadcast(new Intent(ACTION_UPDATE_FRAGMENT));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int contentView() {
        return R.layout.fragment_bills;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_bills);
        mFloatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        mNoBills = (TextView) view.findViewById(R.id.tv_no_bills);

        if (savedInstanceState != null){
            mCheckedBillsId = savedInstanceState.getStringArrayList(KEY_CHECKED_BILLS_ID);
            if (mCheckedBillsId == null){
                mCheckedBillsId = new ArrayList<>();
            }
        } else {
            mCheckedBillsId = new ArrayList<>();
        }

        House currentHouse = getCurrentHouse();
        if (currentHouse != null){
            mCurrentHouseId = currentHouse.getId();
            //setContent();
            //initFabMenu();
        }
        return view;
    }

    private void initFabMenu() {
        House currentHouse = getCurrentHouse();
        List<Service> services = currentHouse.getServices();

        if(!services.isEmpty()){
            mFloatingActionMenu.setVisibility(View.VISIBLE);
            FloatingActionButton customFab;
            Service.Type serviceType;
            String serviceName;
            for(Service service:services){
                serviceType = service.getType();
                if (serviceType != Service.Type.OTHER){
                    serviceName = ServiceController.serviceTypeGetString(service.getType(), getActivity());
                } else {
                    serviceName = service.getName();
                }
                customFab = new FloatingActionButton(getActivity());
                customFab.setButtonSize(FloatingActionButton.SIZE_MINI);
                customFab.setLabelText(serviceName);
                customFab.setTag(service.getId());
                customFab.setOnClickListener(this);
                customFab.setImageResource(R.drawable.fab_add);
                customFab.setColorNormalResId(R.color.colorPrimaryDark);
                customFab.setColorPressed(R.color.colorPrimaryDark);
                mFloatingActionMenu.addMenuButton(customFab);
            }
        } else {
            mFloatingActionMenu.setVisibility(View.GONE);
        }
    }


    public void updateFabMenu() {
        Log.i(TAG, "is mFloatingActionMenu null = " + (mFloatingActionMenu == null));
        if (mFloatingActionMenu != null){
            mFloatingActionMenu.close(false);
            mFloatingActionMenu.removeAllMenuButtons();
            initFabMenu();
        }
    }

    private void setContent() {
        House house = getCurrentHouse();

        ArrayList<Bill> bills;
        bills = getBillsToDisplay(mCurrentHouseId);



        if (mBillsAdapter == null){
            mBillsAdapter = new BillsAdapter (getActivity(), this, bills, mCheckedBillsId);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mBillsAdapter);
        } else {
            mBillsAdapter.setContent(bills);
        }

        if (bills.size() > 0){
            mNoBills.setVisibility(View.GONE);
        } else {
            mNoBills.setVisibility(View.VISIBLE);
        }

    }


    private void updateFragment() {
        Log.i(TAG, "updateFragment");
        String newHouseId = getCurrentHouse().getId();
        if (!newHouseId.equals(mCurrentHouseId)){
            mCheckedBillsId.clear();
        }
        mCurrentHouseId = newHouseId;
        updateFabMenu();
        setContent();
        updateOptionsMenu();

    }

    private void updateOptionsMenu() {
        if (mMenu != null){
            MenuItem markAsPaid = mMenu.findItem(R.id.menu_bills_fragment_mark_paid);
            if(markAsPaid != null){
                if (mCheckedBillsId.isEmpty()){
                    markAsPaid.setVisible(false);
                } else {
                    markAsPaid.setVisible(true);
                }
            }
        }
    }

    private ArrayList<Bill> getBillsToDisplay(String houseId) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, -1);

        RealmResults<Bill> unpaidBills = getRealmHelper().getAllBills(false).where().equalTo("mService.mIdHouse", houseId).findAll();
        RealmResults<Bill> paidBills = getRealmHelper().getAllBills(true).where().equalTo("mService.mIdHouse", houseId).greaterThan("mPaymentTime", calendar.getTimeInMillis()).findAll();


        ArrayList<Bill> bills = new ArrayList<>();
        bills.addAll(unpaidBills.subList(0, unpaidBills.size()));
        bills.addAll(paidBills.subList(0, paidBills.size()));

        sortBills (bills);

        return bills;
    }

    private void sortBills(ArrayList<Bill> bills) {
        Collections.sort(bills, new Comparator<Bill>() {
            @Override
            public int compare(Bill o1, Bill o2) {

                int paymentComparison = 0;
                if (o1.isPaid()){
                    paymentComparison +=1;
                }
                if (o2.isPaid()){
                    paymentComparison -=1;
                }
                if (paymentComparison != 0){
                    return paymentComparison;
                }

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
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null){
            if (tag instanceof String){
                String serviceId = (String) tag;
                Intent intent = new Intent(getActivity(), EditBillActivity.class);
                intent.putExtra(KEY_SELECTED_SERVICE, serviceId);
                getActivity().startActivity(intent);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RECEIPT_PHOTO:
                if (resultCode == RESULT_OK){
                    Bill selectedBill = mBillsAdapter.getSelectedBill();
                    File path = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM), "Receipt");
                    if (!path.exists()) path.mkdirs();
                    File photo = new File(path, selectedBill.getId() + ".jpg");
                    Uri photoUri = Uri.fromFile(photo);
                    getRealmHelper().beginTransaction();
                    mBillsAdapter.getSelectedBill().setPathToPhoto(photoUri.toString());
                    getRealmHelper().commitTransaction();
                    updateFragment();
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(KEY_CHECKED_BILLS_ID, mCheckedBillsId);
        super.onSaveInstanceState(outState);
    }
}
