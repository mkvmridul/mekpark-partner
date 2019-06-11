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
import android.widget.TextView;

import com.example.mani.mekparkpartner.R;
import com.example.mani.mekparkpartner.TowingPartner.Adapter.TowingAdapter;
import com.example.mani.mekparkpartner.TowingPartner.Model.TowingBooking;
import com.example.mani.mekparkpartner.TowingPartner.TowingPartnerHomePage;

import java.util.ArrayList;
import java.util.List;

public class FragNewTowing extends Fragment {

    private String TAG = "FragmentNewTowing";
    private View mRootView;

    private List<TowingBooking> mNewBookingList;

    private TowingPartnerHomePage mActivity;


    public FragNewTowing() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,"called : onCreate1");
        mActivity = (TowingPartnerHomePage) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_for_all_towing, container, false);

        Log.e(TAG,"called : onCreateView1");

        mNewBookingList = new ArrayList<>();
        mNewBookingList = mActivity.getBookingFromTowingHomePage(0);


        if(mNewBookingList.size() == 0){
            mRootView.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
            ((TextView)mRootView.findViewById(R.id.error_message)).setText("No New Booking Available!");
            return mRootView;
        }

        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TowingAdapter adapter = new TowingAdapter(mActivity,mNewBookingList);
        recyclerView.setAdapter(adapter);

        return mRootView;
    }

}
