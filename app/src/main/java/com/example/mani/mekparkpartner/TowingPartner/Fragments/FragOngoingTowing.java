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


public class FragOngoingTowing extends Fragment {

    private String TAG = "FragmentOngoingTowing";
    private View mRootView;

    private List<TowingBooking> mOngoingTowingList;

    private TowingPartnerHomePage mActivity;
    public FragOngoingTowing() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,"called : onCreate3");
        mActivity = (TowingPartnerHomePage) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_for_all_towing, container, false);

        Log.e(TAG,"called : onCreateView3");

        mOngoingTowingList = new ArrayList<>();
        mOngoingTowingList = mActivity.getBookingFromTowingHomePage(3);

        if(mOngoingTowingList.size() == 0){
            mRootView.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
            return mRootView;
        }

        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TowingAdapter adapter = new TowingAdapter(mActivity,mOngoingTowingList);
        recyclerView.setAdapter(adapter);

        return mRootView;
    }

}
