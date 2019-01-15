package com.example.mani.mekparkpartner.ParkingPartner;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentHistory;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentNew;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentOngoing;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentUpcoming;
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

public class BookingPage extends AppCompatActivity  {

    private final String TAG = this.getClass().getSimpleName();
    private List<Fragment> mFragmentList;
    private List<Booking> mBookingList;

    private ProgressDialog mProgressDialog;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);

        mFragmentList = new ArrayList<>();

        mBookingList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(BookingPage.this);
        mProgressDialog.setMessage("Please wait...");

        mFragmentList.add(new FragmentNew());
        mFragmentList.add(new FragmentUpcoming());
        mFragmentList.add(new FragmentOngoing());
        mFragmentList.add(new FragmentHistory());

        mTabLayout = findViewById(R.id.tab_layout_booking);

        fetchBookingsFromDb(0);

        bindWidgetsWithAnEvent();
        setupTabLayout();

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchBookingsFromDb(0);

            }
        });

        ImageView btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    public void fetchBookingsFromDb(final int fragNo) {

        mProgressDialog.show();
        Log.e(TAG,"called : fetchBookingsFromDb");

        String SEND_URL = BASE_URL + "fetch_parking_bookings.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(mBookingList.size()!=0)
                    mBookingList.clear();

                Log.e(TAG,response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");

                    if(rc<=0){
                        String mess = jsonArray.getJSONObject(0).getString("mess");
                        Log.e(TAG,mess);
                        mProgressDialog.dismiss();
                        return;
                    }

                    for(int i=1;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int bookingId = Integer.parseInt(jsonObject.getString("booking_id"));
                        int cusId = Integer.parseInt(jsonObject.getString("customer_id"));

                        String bookingTime =  jsonObject.getString("time_booking");
                        String parkingTime =  jsonObject.getString("park_in");
                        String parkOut     =  jsonObject.getString("park_out");
                        String duration    =  jsonObject.getString("duration");
                        String payCon      =  jsonObject.getString("payment_confirmed");

                        boolean pc;
                        if(payCon.equals("1"))
                            pc = true;
                        else
                            pc = false;

                        String base_fare    = jsonObject.getString("base_fare");
                        String tax          = jsonObject.getString("tax");
                        String add_charges  = jsonObject.getString("add_charges");
                        String total_fare   = jsonObject.getString("total_fare");


                        int status        = Integer.parseInt(jsonObject.getString("status"));
                        String pin           = jsonObject.getString("pin");

                        String brand      = jsonObject.getString("brand");
                        String model      = jsonObject.getString("model");
                        String plateNo    = jsonObject.getString("plate_no");
                        String image      = jsonObject.getString("image");

                        String cusName    = jsonObject.getString("name");
                        String cusMobile  = jsonObject.getString("mobile");

                        mBookingList.add(new Booking(bookingId,cusId,bookingTime,parkingTime,parkOut,duration,pc,
                                base_fare,tax,add_charges,total_fare,
                                status,pin,brand,model,plateNo,image,cusName,cusMobile));

                    }
                    TabLayout.Tab tab =  mTabLayout.getTabAt(fragNo);
                    tab.select();

                    if(getSupportFragmentManager() != null){
                        getSupportFragmentManager()
                                .beginTransaction()
                                .detach(mFragmentList.get(fragNo))
                                .attach(mFragmentList.get(fragNo))
                                .commit();
                    }

                    mProgressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.e(TAG,error.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                String empId =new LoginSessionManager(BookingPage.this).getEmpDetailsFromSP().get(KEY_PARTNER_ID);
                params.put("emp_id",empId);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);



    }

    public List<Booking> fetchBookingFromParent (int status) {

        List<Booking> bookingList = new ArrayList<>();

        for(int i=0;i<mBookingList.size();i++){
            Booking booking = mBookingList.get(i);
            if(booking.getStatus() == status){
                bookingList.add(booking);
            }


        }

        return bookingList;
    }

    public List<Booking> fetchCompletedParking(){

        List<Booking> tempList = new ArrayList<>();

        for(int i=0;i<mBookingList.size();i++){
            Booking booking = mBookingList.get(i);
            int s = booking.getStatus();
            if(s == 1 || s == 4 || s == 5){
                tempList.add(booking);
            }
        }

        return tempList;

    }

    private void setupTabLayout() {

        Log.e(TAG,"setUpLayout");
        mTabLayout.addTab(mTabLayout.newTab().setText("New"),false);
        mTabLayout.addTab(mTabLayout.newTab().setText("Upcoming"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Ongoing"));
        mTabLayout.addTab(mTabLayout.newTab().setText("History"));
    }

    private void bindWidgetsWithAnEvent()
    {
        Log.e(TAG,"bindWidgetsWithEvent");

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e(TAG,"onTabSelected");
                setCurrentTabFragment(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
    private void setCurrentTabFragment(int tabPosition)
    {
        Log.e(TAG,"setCurrenttabFragment");
        switch (tabPosition)
        {
            case 0 :
                Log.e(TAG,"fragment0");
                replaceFragment(mFragmentList.get(0));

                break;
            case 1 :
                Log.e(TAG,"fragment1");
                replaceFragment(mFragmentList.get(1));
                break;

            case 2 :
                Log.e(TAG,"fragment2");
                replaceFragment(mFragmentList.get(2));

                break;

            case 3 :
                Log.e(TAG,"fragment3");
                replaceFragment(mFragmentList.get(3));
                break;
        }
    }


    public void replaceFragment(Fragment fragment) {
        Log.e(TAG, "replaceFragment");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }


}



















