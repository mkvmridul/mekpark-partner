package com.example.mani.mekparkpartner.ParkingPartner.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mani.mekparkpartner.ParkingPartner.Adapter.HistoryAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.Adapter.UpcomingAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.R;

import java.util.ArrayList;
import java.util.List;


public class FragmentHistory extends Fragment {

    private String TAG = "FragmentHistory";
    private View mRootView;
    private List<Booking> mHistoryList;


    public FragmentHistory() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"called : onCreate4");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG,"called : onCreateView4");
        mRootView =  inflater.inflate(R.layout.fragment_history, container, false);
        mHistoryList = new ArrayList<>();

        mHistoryList.add(new Booking(1,4,"45667",
                "87","",true,"",0,1234,"",
                "","","","",""));

        mHistoryList.add(new Booking(1,4,"45667",
                "87","",true,"",0,1234,"",
                "","","","",""));

        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        HistoryAdapter adapter = new HistoryAdapter(getActivity(),mHistoryList);
        recyclerView.setAdapter(adapter);







        return mRootView;
    }

}
