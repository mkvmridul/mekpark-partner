package com.example.mani.mekparkpartner.ParkingPartner.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mani.mekparkpartner.ParkingPartner.Adapter.NewBookingAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.Adapter.UpcomingAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.BookingPage;
import com.example.mani.mekparkpartner.ParkingPartner.ParkingHomePage;
import com.example.mani.mekparkpartner.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentUpcoming extends Fragment {

    private String TAG = "FragmentUpcoming";
    private View mRootView;
    private List<Booking> mUpcomingList;
    private ParkingHomePage mActivity;
    private boolean shouldExecuteOnResume;

    public FragmentUpcoming() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate2");
        mActivity = (ParkingHomePage) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG,"called : onCreateView2");

        shouldExecuteOnResume = false;

        mRootView = inflater.inflate(R.layout.fragment_upcoming, container, false);
        mUpcomingList = new ArrayList<>();
        mUpcomingList = mActivity.fetchBookingFromParent(2);

        if(mUpcomingList.size() == 0){
            mRootView.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
            return mRootView;
        }

        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        UpcomingAdapter adapter = new UpcomingAdapter(getActivity(),mUpcomingList);
        recyclerView.setAdapter(adapter);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(shouldExecuteOnResume){
            Log.e(TAG,"called : onResume1");
            mActivity.fetchBookingsFromDb(1);
        } else{
            shouldExecuteOnResume = true;
        }

    }

}
