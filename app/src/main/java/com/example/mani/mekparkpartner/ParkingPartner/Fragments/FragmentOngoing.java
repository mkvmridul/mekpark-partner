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
import com.example.mani.mekparkpartner.ParkingPartner.Adapter.OngoingAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentOngoing extends Fragment {

    private String TAG = "FragmentOngoing";
    private View mRootView;
    private List<Booking> mOngoingList;

    public FragmentOngoing() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate3");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG,"called : onCreateView3");

        mRootView =  inflater.inflate(R.layout.fragment_fragment_ongoing, container, false);
        mOngoingList = new ArrayList<>();

        mOngoingList.add(new Booking(1,4,"45667",
                "87","",true,"",0,1234,"",
                "","","","",""));

        mOngoingList.add(new Booking(1,4,"45667",
                "87","",true,"",0,1234,"",
                "","","","",""));


        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        OngoingAdapter adapter = new OngoingAdapter(getActivity(),mOngoingList);
        recyclerView.setAdapter(adapter);

        return mRootView;
    }

}
