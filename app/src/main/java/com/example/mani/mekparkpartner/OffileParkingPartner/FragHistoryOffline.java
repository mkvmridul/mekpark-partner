package com.example.mani.mekparkpartner.OffileParkingPartner;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.OffileParkingPartner.Adapter.OfflineHistoryAdapter;
import com.example.mani.mekparkpartner.OffileParkingPartner.Adapter.OfflineOngoingAdapter;
import com.example.mani.mekparkpartner.OffileParkingPartner.Model.OfflineParkingBooking;
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
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;


public class FragHistoryOffline extends Fragment {

    private final String TAG = "FragHistoryOffline";
    private OfflineHomepage mActivity;
    private View mRootView;

    private List<OfflineParkingBooking> mHistoryList;
    private ProgressBar mProgressbar;

    public FragHistoryOffline() { }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (OfflineHomepage) getActivity();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //same layout is used in frag_ongoing_offline and history part
        mRootView    =  inflater.inflate(R.layout.frag_ongoing_offline, container, false);
        mProgressbar = mRootView.findViewById(R.id.progress_bar);
        mHistoryList = new ArrayList<>();
        fetchOfflineHistoryBooking();
        return mRootView;
    }

    private void fetchOfflineHistoryBooking() {

        Log.e(TAG,"called : fetchOfflineHistoryBooking");
        mProgressbar.setVisibility(View.VISIBLE);

        String SEND_URL = BASE_URL + "get_offline_history_parking.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,response);
                        mProgressbar.setVisibility(View.GONE);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            int rc = jsonArray.getJSONObject(0).getInt("rc");
                            if(rc ==0){
                                Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for(int i=1;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                int id             =  Integer.parseInt(jsonObject.getString("id"));
                                String orderId     =  jsonObject.getString("order_id");
                                String timeBooking =  jsonObject.getString("time_booking");
                                String parkIn      =  jsonObject.getString("park_in");
                                String vehicleType =  jsonObject.getString("vehicle_type");
                                String brand       =  jsonObject.getString("brand");
                                String model       =  jsonObject.getString("model");
                                String numberPlate =  jsonObject.getString("number_plate");
                                String custContact =  jsonObject.getString("cust_contact");
                                String parkingType =  jsonObject.getString("parking_type");
                                String paymentCompleted =  jsonObject.getString("payment_completed");
                                String farePerHr   =  jsonObject.getString("fare_per_hr");
                                int status         =  Integer.parseInt(jsonObject.getString("status"));
                                String remarks     =  jsonObject.getString("remarks");

                                boolean payCom = false;
                                if(paymentCompleted.equals("1"))
                                    payCom = true;

                                String parkOut      =  jsonObject.getString("park_out");
                                String duration     =  jsonObject.getString("duration");
                                String baseFare     =  jsonObject.getString("base_fare");
                                String tax          =  jsonObject.getString("tax");
                                String addFare      =  jsonObject.getString("add_fare");
                                String totalFare    =  jsonObject.getString("total_fare");

                                mHistoryList.add(new OfflineParkingBooking(id,orderId,timeBooking,parkIn,vehicleType,
                                        brand,model,numberPlate,custContact,parkingType, payCom,status, remarks,farePerHr,
                                        parkOut,duration,baseFare,tax,addFare,totalFare));
                            }

                            if(mHistoryList.size() <= 0){
                                mRootView.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
                                return;
                            }
                            else
                                mRootView.findViewById(R.id.error_layout).setVisibility(View.GONE);


                            RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view_ongoing_offline);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            OfflineHistoryAdapter adapter = new OfflineHistoryAdapter(getActivity(),mHistoryList);

                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG,e.toString());
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
                mProgressbar.setVisibility(View.GONE);
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                String partnerId = new LoginSessionManager(getActivity()).getEmpDetailsFromSP().get(KEY_PARTNER_ID);
                params.put("partner_id",partnerId);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

}
