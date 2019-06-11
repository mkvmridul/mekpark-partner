package com.example.mani.mekparkpartner.TowingPartner.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mani.mekparkpartner.R;
import com.example.mani.mekparkpartner.TowingPartner.Adapter.TowingAdapter;
import com.example.mani.mekparkpartner.TowingPartner.Model.TowingBooking;
import com.example.mani.mekparkpartner.TowingPartner.TowingPartnerHomePage;

import java.util.ArrayList;
import java.util.List;

public class FragUpcomingTowing extends Fragment {

    private String TAG = "FragmentUpcomingTowing";
    private View mRootView;
    private List<TowingBooking> mUpcomingTowingList;
    private TowingPartnerHomePage mActivity;


    public FragUpcomingTowing() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,"called : onCreate2");
        mActivity = (TowingPartnerHomePage) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.frag_for_all_towing, container, false);

        Log.e(TAG,"called : onCreateView2");

        mUpcomingTowingList = new ArrayList<>();
        mUpcomingTowingList = mActivity.getBookingFromTowingHomePage(2);

        if(mUpcomingTowingList.size() == 0){
            mRootView.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
            return mRootView;
        }

        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TowingAdapter adapter = new TowingAdapter(mActivity,mUpcomingTowingList);
        recyclerView.setAdapter(adapter);

        return mRootView;
    }

}
