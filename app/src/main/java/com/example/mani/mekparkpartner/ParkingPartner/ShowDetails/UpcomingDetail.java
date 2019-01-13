package com.example.mani.mekparkpartner.ParkingPartner.ShowDetails;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_IMAGE_PATH;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate2;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.sentNotificationToUser;

public class UpcomingDetail extends AppCompatActivity {

    private Booking mBooking;
    final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_detail);

        mBooking = (Booking) getIntent().getSerializableExtra("booking");

        setData();
        clickListener();
    }

    private void setData() {

        TextView tv_bookingId   = findViewById(R.id.booking_id);
        TextView tv_model       = findViewById(R.id.model);
        TextView tv_brand       = findViewById(R.id.brand);
        TextView tv_plate_no    = findViewById(R.id.plate_no);
        ImageView iv_vhicle     = findViewById(R.id.vehicle_image);


        LinearLayout ll_Call    = findViewById(R.id.call_layout);

        TextView  tv_book_date  = findViewById(R.id.booking_date);
        TextView  tv_book_time  = findViewById(R.id.booking_time);
        TextView  tv_park_time  = findViewById(R.id.parking_time);
        TextView  tv_duration   = findViewById(R.id.duration);

        final Switch vehiCleArrived   = findViewById(R.id.switch_vehicle);
        final LinearLayout ll_otp = findViewById(R.id.otp_layout);

        final EditText et1,et2,et3,et4;

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);

        String imageName = mBooking.getVehicleImage();
        if(!imageName.equals("")){
            Glide.with(UpcomingDetail.this).load(BASE_IMAGE_PATH+imageName)
                    .into(iv_vhicle);
        }
        else {
            //default image
            iv_vhicle.setImageDrawable(getResources().getDrawable(R.mipmap.dummy));
        }


        vehiCleArrived.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ll_otp.setVisibility(View.VISIBLE);

                }
                else {
                    ll_otp.setVisibility(View.GONE);
                    et1.setText("");
                    et2.setText("");
                    et3.setText("");
                    et4.setText("");

                }
            }
        });



        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1)
                    et2.requestFocus();

                else if(s.length()==0)
                    et1.clearFocus();
            }
        });

        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1)
                    et3.requestFocus();

                else if(s.length()==0)
                    et1.requestFocus();


            }
        });

        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1)
                    et4.requestFocus();

                else if(s.length()==0)
                    et2.requestFocus();
            }
        });

        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1)
                    et1.clearFocus();

                else if(s.length()==0)
                    et3.requestFocus();
            }
        });


        TextView btn_startBooking = findViewById(R.id.start);

        tv_bookingId.setText("Order #"+mBooking.getBookingId());
        tv_model.setText(mBooking.getModel());
        tv_brand.setText(mBooking.getBrand());
        tv_plate_no.setText(mBooking.getLicencePlateNo());
        String parkinTime =  getFormattedTime(TAG, mBooking.getParkInTime());
        tv_park_time.setText(parkinTime);
        tv_duration.setText(mBooking.getDuration()+" hrs");


        String bookingTime =  getFormattedTime(TAG, mBooking.getBookingTime());
        tv_book_time.setText(bookingTime);

        String bookingDate = getFormattedDate(TAG,mBooking.getBookingTime());
        tv_book_date.setText(bookingDate);



        ll_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mBooking.getCusPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);

            }
        });

        btn_startBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!vehiCleArrived.isChecked()){
                    Toast.makeText(UpcomingDetail.this,"Vehicle did not arrived", Toast.LENGTH_SHORT).show();
                    return;
                }

                String s1 = et1.getText().toString().trim();
                String s2 = et2.getText().toString().trim();
                String s3 = et3.getText().toString().trim();
                String s4 = et4.getText().toString().trim();

                if(s1.equals("") || s2.equals("") || s3.equals("") || s4.equals("")){
                    Toast.makeText(UpcomingDetail.this,"please fill valid pin",Toast.LENGTH_SHORT).show();
                    return;
                }

                String inputedOtp = s1+s2+s3+s4;

                if(!inputedOtp.equals(mBooking.getPin()) )  {
                    Toast.makeText(UpcomingDetail.this,"wrong pin", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateStatus(mBooking.getBookingId(),3,"Booking is moved to Ongoing section");

            }
        });







    }

    private void clickListener() {
        ImageView btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateStatus(final int bookingId, final int status, final String message) {

        Log.e(TAG,"called : updateStatus");

        String SEND_URL = BASE_URL + "update_status_of_parking_booking.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG,response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");
                    String mess = jsonArray.getJSONObject(0).getString("mess");

                    if(rc<=0){
                        Log.e(TAG,mess);
                        Toast.makeText(UpcomingDetail.this,mess,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.e(TAG,mess);
                    Toast.makeText(UpcomingDetail.this,message,Toast.LENGTH_SHORT).show();
                    mBooking.setStatus(status);

                    prepareNotification(mBooking.getCusId(),bookingId);
                    onBackPressed();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
                Toast.makeText(UpcomingDetail.this,error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("booking_id",String.valueOf(bookingId));
                params.put("status",String.valueOf(status));
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(UpcomingDetail.this).addToRequestQueue(stringRequest);

    }

    private void prepareNotification(int cusId, int bookingId) {

        String title = "Parking started";
        String message = "Your parking order for booking id "+bookingId + " is started.";
        sentNotificationToUser(UpcomingDetail.this,cusId,title,message);
    }
}
