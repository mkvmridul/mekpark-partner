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

import com.example.mani.mekparkpartner.ParkingPartner.Adapter.HistoryAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.ParkingPartnerHomePage;
import com.example.mani.mekparkpartner.R;

import java.util.ArrayList;
import java.util.List;


public class FragmentHistory extends Fragment {

    private String TAG = "FragmentHistory";
    private View mRootView;
    private List<Booking> mHistoryList;
    private ParkingPartnerHomePage mActivity;


    public FragmentHistory() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"called : onCreate4");
        mActivity = (ParkingPartnerHomePage) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG,"called : onCreateView4");
        mRootView =  inflater.inflate(R.layout.fragment_history, container, false);

        mHistoryList = new ArrayList<>();
        mHistoryList = mActivity.fetchCompletedParking();

        if(mHistoryList.size() == 0){
            mRootView.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
            return mRootView;
        }



        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        HistoryAdapter adapter = new HistoryAdapter(getActivity(),mHistoryList);
        recyclerView.setAdapter(adapter);


        return mRootView;
    }

}
