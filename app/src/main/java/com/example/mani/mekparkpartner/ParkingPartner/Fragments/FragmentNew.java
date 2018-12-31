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
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.BookingPage;
import com.example.mani.mekparkpartner.R;


import java.util.ArrayList;
import java.util.List;


public class FragmentNew extends Fragment {

    private String TAG = "FragmentNew";
    private View mRootView;

    private List<Booking> mNewBookingList;
    private BookingPage mActivity;

    public FragmentNew() {}



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,"called : onCreate1");
        mActivity = (BookingPage) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG,"called : onCreateView1");
        mRootView = inflater.inflate(R.layout.fragment_new, container, false);

        mNewBookingList = new ArrayList<>();
        mNewBookingList = mActivity.fetchBookingFromParent(0);

        for(int i=0;i<mNewBookingList.size();i++)
            Log.e(TAG, "booking Id = "+ mNewBookingList.get(i).getBookingId());

        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        NewBookingAdapter adapter = new NewBookingAdapter(getActivity(),mNewBookingList);
        recyclerView.setAdapter(adapter);


        return mRootView;
    }

}
