package com.example.mani.mekparkpartner.ParkingPartner.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mani.mekparkpartner.ParkingPartner.Adapter.NewBookingAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.BookingPage;
import com.example.mani.mekparkpartner.ParkingPartner.Listener.MyListener;
import com.example.mani.mekparkpartner.ParkingPartner.ParkingHomePage;
import com.example.mani.mekparkpartner.R;


import java.util.ArrayList;
import java.util.List;


public class FragmentNew extends Fragment implements MyListener {

    private String TAG = "FragmentNew";
    private View mRootView;

    private List<Booking> mNewBookingList;
    private ParkingHomePage mActivity;

    private boolean shouldExecuteOnResume;

    public FragmentNew() {}



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shouldExecuteOnResume = false;

        Log.e(TAG,"called : onCreate1");
        mActivity = (ParkingHomePage) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        shouldExecuteOnResume = false;

        Log.e(TAG,"called : onCreateView1");
        mRootView = inflater.inflate(R.layout.fragment_new, container, false);

        mNewBookingList = new ArrayList<>();
        mNewBookingList = mActivity.fetchBookingFromParent(0);

        if(mNewBookingList.size() == 0){
            mRootView.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
            return mRootView;
        }

        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        NewBookingAdapter adapter = new NewBookingAdapter(mActivity,mNewBookingList, FragmentNew.this);
        recyclerView.setAdapter(adapter);

        return mRootView;
    }

    @Override
    public void refresh() {
        mActivity.fetchBookingsFromDb(0);
    }



    @Override
    public void onResume() {
        super.onResume();

        if(shouldExecuteOnResume){
            Log.e(TAG,"called : onResume1");
            mActivity.fetchBookingsFromDb(0);
        } else{
            shouldExecuteOnResume = true;
        }

    }
}
