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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.ParkingPartner.Adapter.NewBookingAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.Adapter.UpcomingAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.BookingPage;
import com.example.mani.mekparkpartner.ParkingPartner.Listener.MyListener;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_EMP_ID;

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

        mNewBookingList.add(new Booking(1,4,"45667",
                "87","",true,"",0,1234,"",
                "","","","",""));

        mNewBookingList.add(new Booking(1,4,"45667",
                "87","",true,"",0,1234,"",
                "","","","",""));

        mNewBookingList.add(new Booking(1,4,"45667",
                "87","",true,"",0,1234,"",
                "","","","",""));

        mNewBookingList.add(new Booking(1,4,"45667",
                "87","",true,"",0,1234,"",
                "","","","",""));


        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        NewBookingAdapter adapter = new NewBookingAdapter(getActivity(),mNewBookingList);
        recyclerView.setAdapter(adapter);


        return mRootView;
    }

}
