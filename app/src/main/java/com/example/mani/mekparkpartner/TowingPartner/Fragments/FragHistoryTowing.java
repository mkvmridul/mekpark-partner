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

public class FragHistoryTowing extends Fragment {

    private String TAG = "FragmentHistoryTowing";
    private View mRootView;

    private List<TowingBooking> mHistoryTowingList;
    private TowingPartnerHomePage mActivity;


    public FragHistoryTowing() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,"called : onCreate4");
        mActivity = (TowingPartnerHomePage) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frag_for_all_towing, container, false);

        Log.e(TAG,"called : onCreateView4");

        mHistoryTowingList = new ArrayList<>();
        mHistoryTowingList = mActivity.getAllCompletedParking();

        if(mHistoryTowingList.size() == 0){
            TextView emptyTextView = mRootView.findViewById(R.id.error_layout);
            emptyTextView.setText("No History Available");
            emptyTextView.setVisibility(View.VISIBLE);
            return mRootView;
        }

        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TowingAdapter adapter = new TowingAdapter(mActivity,mHistoryTowingList);
        recyclerView.setAdapter(adapter);

        return mRootView;
    }

}
