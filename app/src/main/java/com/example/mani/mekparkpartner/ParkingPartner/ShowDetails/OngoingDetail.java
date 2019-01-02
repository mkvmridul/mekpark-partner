package com.example.mani.mekparkpartner.ParkingPartner.ShowDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;

public class OngoingDetail extends AppCompatActivity {

    private Booking mBooking;
    final String TAG = this.getClass().getSimpleName();

    private  long START_TIME_IN_MILLIS;

    private TextView mTextViewCountDown;
    //private Button mButtonStartPause;
    //private Button mButtonReset;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mTimeLeftInMillis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_detail);

        mBooking = (Booking) getIntent().getSerializableExtra("booking");

        long curentTimeInMilli = System.currentTimeMillis();
        long timeLast = Long.parseLong(mBooking.getParkOutTime())*1000L;

        START_TIME_IN_MILLIS = timeLast - curentTimeInMilli;
        mTimeLeftInMillis = START_TIME_IN_MILLIS;

        setData();
        clickListener();
        setTimer();
    }

    private void setData() {


        TextView tv_model       = findViewById(R.id.model);
        TextView tv_brand       = findViewById(R.id.brand);
        TextView tv_plate_no    = findViewById(R.id.plate_no);
        ImageView iv_vhicle     = findViewById(R.id.vehicle_image);


        LinearLayout ll_Call    = findViewById(R.id.call_layout);

        TextView  tv_book_date  = findViewById(R.id.booking_date);
        TextView  tv_book_time  = findViewById(R.id.booking_time);
        TextView  tv_park_time  = findViewById(R.id.parking_time);
        TextView  tv_duration   = findViewById(R.id.duration);

        TextView btn_update   = findViewById(R.id.update_customer);
        TextView btn_complete = findViewById(R.id.parking_complete);

        tv_model.setText(mBooking.getModel());
        tv_brand.setText(mBooking.getBrand());
        tv_plate_no.setText(mBooking.getLicencePlateNo());

        tv_duration.setText(mBooking.getDuration()+" hrs");


        String bookingTime =  getFormattedTime(TAG, mBooking.getBookingTime());
        tv_book_time.setText(bookingTime);

        String bookingDate = getFormattedDate(TAG,mBooking.getBookingTime());
        tv_book_date.setText(bookingDate);

        String parkInTime = getFormattedTime(TAG,mBooking.getParkInTime());
        tv_park_time.setText(parkInTime);


        ll_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mBooking.getCusPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);

            }
        });



        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OngoingDetail.this,"to be implemented",Toast.LENGTH_SHORT).show();
            }
        });


        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               updateStatus(mBooking.getBookingId(),1,"Parking Completed Succesfully");
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

    private void setTimer(){

        mTextViewCountDown = findViewById(R.id.countdown);
        startTimer();
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mTextViewCountDown.setText("Time Over");
                findViewById(R.id.min_left).setVisibility(View.GONE);
            }
        }.start();

        mTimerRunning = true;
    }

    private void updateCountDownText() {
        int hrs     = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ( ((mTimeLeftInMillis / 1000) / 60 ) - ( hrs * 60) );
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%2d:%02d:%02d", hrs,minutes, seconds);

        if(hrs == 0){
            timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d",minutes, seconds);
        }


        mTextViewCountDown.setText(timeLeftFormatted);
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
                        Toast.makeText(OngoingDetail.this,mess,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.e(TAG,mess);
                    Toast.makeText(OngoingDetail.this,message,Toast.LENGTH_SHORT).show();

                    mBooking.setStatus(status);
                    onBackPressed();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
                Toast.makeText(OngoingDetail.this,error.toString(),Toast.LENGTH_SHORT).show();

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
        MySingleton.getInstance(OngoingDetail.this).addToRequestQueue(stringRequest);

    }

}
