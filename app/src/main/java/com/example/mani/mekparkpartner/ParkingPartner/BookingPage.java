package com.example.mani.mekparkpartner.ParkingPartner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class BookingPage extends AppCompatActivity  {

    private final String TAG = this.getClass().getSimpleName();
    private List<Fragment> mFragmentList;
    private List<Booking> mBookingList;

    private ViewPager mViewPager;
    ProgressDialog mProgressDialog;
    BookingFragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);


        mFragmentList = new ArrayList<>();
        mBookingList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(BookingPage.this);
        mProgressDialog.setMessage("Please wait...");

        fetchBookingsFromDb();

        mFragmentList.add(new FragmentNew());
        mFragmentList.add(new FragmentUpcoming());
        mFragmentList.add(new FragmentOngoing());
        mFragmentList.add(new FragmentHistory());


        mViewPager = findViewById(R.id.viewpager_booking);

         mAdapter = new BookingFragmentPagerAdapter(
                getSupportFragmentManager(),mFragmentList);

        TabLayout tabLayout = findViewById(R.id.tab_layout_booking);
        tabLayout.setupWithViewPager(mViewPager);


        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchBookingsFromDb();
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

    public void fetchBookingsFromDb() {

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

                        String fare       = jsonObject.getString("fare");
                        int status        = Integer.parseInt(jsonObject.getString("status"));
                        int pin           = Integer.parseInt(jsonObject.getString("pin"));

                        String brand      = jsonObject.getString("brand");
                        String model      = jsonObject.getString("model");
                        String plateNo    = jsonObject.getString("plate_no");
                        String image      = jsonObject.getString("image");

                        String cusName    = jsonObject.getString("name");
                        String cusMobile  = jsonObject.getString("mobile");

                        mBookingList.add(new Booking(bookingId,cusId,bookingTime,parkingTime,parkOut,duration,pc,fare,
                                status,pin,brand,model,plateNo,image,cusName,cusMobile));

                    }

                    mViewPager.setAdapter(mAdapter);
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

                String empId =new LoginSessionManager(BookingPage.this).getEmpDetailsFromSP().get(KEY_EMP_ID);
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

}



















