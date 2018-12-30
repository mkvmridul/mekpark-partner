package com.example.mani.mekparkpartner.ParkingPartner;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

public class BookingPage extends AppCompatActivity  {

    private final String TAG = this.getClass().getSimpleName();
    private List<Fragment> mFragmentList;
    private List<Booking> mBookingList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);


        mFragmentList = new ArrayList<>();
        mBookingList = new ArrayList<>();

        mFragmentList.add(new FragmentNew());
        mFragmentList.add(new FragmentUpcoming());
        mFragmentList.add(new FragmentOngoing());
        mFragmentList.add(new FragmentHistory());


        ViewPager viewPager = findViewById(R.id.viewpager_booking);

        BookingFragmentPagerAdapter adapter = new BookingFragmentPagerAdapter(
                getSupportFragmentManager(),mFragmentList);

        TabLayout tabLayout = findViewById(R.id.tab_layout_booking);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setAdapter(adapter);

        ImageView btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mBookingList.add(new Booking(1,4,"45667",
                "87","",true,"",0,1234,"",
                "","","","",""));

        mBookingList.add(new Booking(1,4,"45667",
                "87","",true,"",0,1234,"",
                "","","","",""));

    }

    private void fetchBookings() {


        String SEND_URL = BASE_URL + "fetch_parking_bookings.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG,response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");

                    if(rc<=0){
                        String mess = jsonArray.getJSONObject(0).getString("mess");
                        Log.e(TAG,mess);
                        return;
                    }

                    for(int i=1;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int bookingId = Integer.parseInt(jsonObject.getString("booking_id"));
                        int cusId = Integer.parseInt(jsonObject.getString("customer_id"));

                        String bookingTime =  jsonObject.getString("time_booking");
                        String parkingTime =  jsonObject.getString("time_parking");
                        String duration    =  jsonObject.getString("duration");
                        String payCon      =  jsonObject.getString("payment_confirmed");

                        boolean pc;
                        if(payCon.equals("1"))
                            pc = true;
                        else
                            pc = false;

                        String fare        = jsonObject.getString("fare");
                        int status         = Integer.parseInt(jsonObject.getString("status"));
                        int pin            = Integer.parseInt(jsonObject.getString("pin"));

                        String brand       = jsonObject.getString("brand");
                        String model       = jsonObject.getString("model");
                        String plateNo     = jsonObject.getString("plate_no");
                        String image       = jsonObject.getString("image");

                        String cusName     = jsonObject.getString("name");
                        String cusMobile   = jsonObject.getString("mobile");


//                        Log.e(TAG, bookingId+" "+cusId+" "+bookingTime+" "+parkingTime+" "+duration+" "+payCon
//                                +" "+fare+" "+status+" "+pin+" "+brand+" "+model+" "+plateNo+" "+cusName+" "+cusMobile+" "+image);

                        mBookingList.add(new Booking(bookingId,cusId,bookingTime,parkingTime,duration,pc,fare,
                                status,pin,brand,model,plateNo,image,cusName,cusMobile));





                    }






                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
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
                Log.e("asdf",i+"");
            }


        }

        return bookingList;
    }

}



















