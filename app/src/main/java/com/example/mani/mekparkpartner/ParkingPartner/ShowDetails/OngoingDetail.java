package com.example.mani.mekparkpartner.ParkingPartner.ShowDetails;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_IMAGE_PATH;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.formatTimeForBill;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.sentNotificationToUser;


public class OngoingDetail extends AppCompatActivity {

    private Booking mBooking;
    final String TAG = this.getClass().getSimpleName();

    private  long START_TIME_IN_MILLIS;
    private CountDownTimer mCountDownTimer;
    private TextView mTextViewCountDown;

    private boolean mTimerRunning;
    private long mTimeLeftInMillis;

    private IntentIntegrator mQrScan;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_detail);

        mBooking = (Booking) getIntent().getSerializableExtra("booking");
        mProgressBar = findViewById(R.id.progress_bar);

        mQrScan = new IntentIntegrator(OngoingDetail.this);
        mQrScan.setPrompt("");
        mQrScan.setOrientationLocked(true);

        long curentTimeInMilli = System.currentTimeMillis();
        long timeLast = Long.parseLong(mBooking.getParkOutTime())*1000L;



        START_TIME_IN_MILLIS = timeLast - curentTimeInMilli;
        mTimeLeftInMillis = START_TIME_IN_MILLIS;

        setData();
        clickListener();
        setTimer();
    }

    private void setData() {

        TextView tv_bookingId   = findViewById(R.id.booking_id);
        TextView tv_model       = findViewById(R.id.model);
        TextView tv_brand       = findViewById(R.id.brand);
        TextView tv_plate_no    = findViewById(R.id.plate_no);
        ImageView iv_vhicle     = findViewById(R.id.vehicle_image);

        TextView  tv_book_date  = findViewById(R.id.booking_date);
        TextView  tv_book_time  = findViewById(R.id.booking_time);
        TextView  tv_park_time  = findViewById(R.id.parking_time);
        TextView  tv_duration   = findViewById(R.id.duration);
        
        tv_bookingId.setText("Order #"+mBooking.getBookingId());
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

        String imageName = mBooking.getVehicleImage();
        if(!imageName.equals("")){
            Glide.with(OngoingDetail.this).load(BASE_IMAGE_PATH+imageName)
                    .into(iv_vhicle);
        }
        else {
            //default image
            iv_vhicle.setImageDrawable(getResources().getDrawable(R.mipmap.dummy));
        }









    }

    private void clickListener() {

        ImageView btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        LinearLayout ll_Call    = findViewById(R.id.call_layout);
        TextView btn_update   = findViewById(R.id.update_customer);
        TextView btn_complete = findViewById(R.id.parking_complete);



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

                //Scan Qr and finish the ride
                mQrScan.initiateScan();


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
                findViewById(R.id.min_left).setVisibility(View.GONE);

                //Use chrone job for this
                String titleForNotifi = "Parking alloted period is over.";
                String messageForNoti = "Your parking period for booking id "+mBooking.getBookingId()
                        + " is over, you will be charged extra accordingly";
                prepareNotification(mBooking.getCusId(),titleForNotifi,messageForNoti);

                ImageView imageView = findViewById(R.id.clock);
                imageView.setImageDrawable(ContextCompat.getDrawable(OngoingDetail.this,R.drawable.time));

                mTextViewCountDown.setTextColor(getResources().getColor(R.color.colorPrimary));

                setUpTimer();
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

    private void updateBookingStatusToComplete() {

        Log.e(TAG,"called :  updateBookingStatusToComplete");

        String SEND_URL = BASE_URL + "update_status_of_parking_booking.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG,response);

                mProgressBar.setVisibility(View.GONE);

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
                    Toast.makeText(OngoingDetail.this,"Done",Toast.LENGTH_SHORT).show();
                    mBooking.setStatus(1);

                    String titleForNotifi = "Parking Complete";
                    String messageForNoti = "Your parking order for booking id "+mBooking.getBookingId()+ " is completed.";
                    prepareNotification(mBooking.getCusId(),titleForNotifi,messageForNoti);
                    onBackPressed();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG,error.toString());
                Toast.makeText(OngoingDetail.this,error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("booking_id", String.valueOf(mBooking.getBookingId()));
                params.put("status","1");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(OngoingDetail.this).addToRequestQueue(stringRequest);

    }

    private void prepareNotification(int cusId,String title, String message) {
        sentNotificationToUser(OngoingDetail.this,cusId,title,message);
    }

    //For Scanner result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null)
        {
            if (result.getContents() == null)
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            else
                {
                    Log.e(TAG,"Barcode content = "+result.getContents());
                    try {

                        JSONObject jsonObject = new JSONObject(result.getContents());
                        int bookingId = jsonObject.getInt("booking_id");
                        
                        // Comparing bokingId(partnerSide) and mBooking.getBookingId() (userSide)
                        
                        if(bookingId == mBooking.getBookingId()){

                            long curentTimeInMilli = System.currentTimeMillis();
                            long timeLast           = Long.parseLong(mBooking.getParkOutTime())*1000L;

                            mTimeLeftInMillis = timeLast - curentTimeInMilli;

                            Log.e(TAG,"Time Left "+mTimeLeftInMillis/1000+"");

                            if(mTimeLeftInMillis>0){
                                Log.e(TAG,"Completed ride without any fine");
                                updateBookingStatusToComplete();
                            }
                            else
                                calculateExtraChargesAndSendToDb(mTimeLeftInMillis);
                        }
                        
                        else {
                            Toast.makeText(OngoingDetail.this,"Scanning Wrong Booking",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        } else
            {

            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void calculateExtraChargesAndSendToDb(long mTimeLeftInMillis) {

        mProgressBar.setVisibility(View.VISIBLE);

        final long extraDurationInSec = (-mTimeLeftInMillis)/1000;

        final long eExtraBaseFare = extraDurationInSec/60;
        final long eAddiCharges = 10;
        final long eDiscount = eExtraBaseFare/10;
        final long eTotalFare = eExtraBaseFare+eAddiCharges-eDiscount;



        String SEND_URL = BASE_URL + "sendExtraChargesDetail.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG,response);

                mProgressBar.setVisibility(View.GONE);

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject responseJsonObj = jsonArray.getJSONObject(0);

                    int rc = responseJsonObj.getInt("rc");

                    if(rc <= 0){
                        String mess = responseJsonObj.getString("mess");
                        Log.e(TAG,mess);
                        return;
                    }

                    int dataAlreadySaved =  responseJsonObj.getInt("dataAlreadySaved");

                    if(dataAlreadySaved==0){
                        //Show the calculated one in bill receipt
                        String parkingExtraChargesId =  responseJsonObj.getString("parking_extra_charges_id");
                        showExtraBill(OngoingDetail.this, parkingExtraChargesId, extraDurationInSec,eExtraBaseFare,eDiscount,eAddiCharges,eTotalFare);
                        return;
                    }

                    //Means bill was already  generated earlier, so  dont calculate amount once again,
                    // just fetching the previous one

                    if(dataAlreadySaved==1){

                        JSONObject jsonData = jsonArray.getJSONObject(1);

                        String parkingExtraChargesId  = jsonData.getString("parking_extra_charges_id");
                        Long parkingDur             = Long.valueOf(jsonData.getString("parking_dur"));
                        Long extraFare              = Long.valueOf(jsonData.getString("extra_fare"));
                        Long addiCharges            = Long.valueOf(jsonData.getString("e_addi_charges"));
                        Long dis                    = Long.valueOf(jsonData.getString("e_discount"));
                        Long totalFare              = Long.valueOf(jsonData.getString("total_fare"));

                        showExtraBill(OngoingDetail.this,parkingExtraChargesId,parkingDur,extraFare,addiCharges,dis,totalFare);
                    }

                } catch (JSONException e) {
                    Log.e(TAG,e.toString());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG,error.toString());
                Toast.makeText(OngoingDetail.this,error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                params.put("booking_id", String.valueOf(mBooking.getBookingId()));
                params.put("e_parking_dur", String.valueOf(extraDurationInSec));
                params.put("e_extra_fare", String.valueOf(eExtraBaseFare));
                params.put("e_addi_charges", String.valueOf(eAddiCharges));
                params.put("e_discount", String.valueOf(eDiscount));
                params.put("e_total_fare", String.valueOf(eTotalFare));

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);




    }

    private  void showExtraBill(final Context context, final String parkingExtraChargesId, long extraDur, long extraBaseFare, long discount,
                                long extraAddCharges, long totalExtraFare) {

        final Dialog dialog = new Dialog(context);

        View view = LayoutInflater.from(context).inflate(R.layout.due_page, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        TextView tv_totalFare1   = view.findViewById(R.id.total_extra_fare);
        TextView tv_needHelp     = view.findViewById(R.id.need_help);
        TextView tv_eDur         = view.findViewById(R.id.total_extra_duration);
        TextView tv_eBaseFare    = view.findViewById(R.id.extra_base_fare);
        TextView tv_discount     = view.findViewById(R.id.discount);
        TextView tv_eAddCharges  = view.findViewById(R.id.extra_additinal_charges);
        TextView tv_totalFare2   = view.findViewById(R.id.total_extra_fare2);

        tv_eDur.setText(extraDur+"");

        tv_eDur.setText(formatTimeForBill(extraDur*1000));

        tv_eBaseFare.setText(extraBaseFare+"");
        tv_discount.setText(discount+"");
        tv_eAddCharges.setText(extraAddCharges+"");

        tv_totalFare1.setText(totalExtraFare+"");
        tv_totalFare2.setText(totalExtraFare+"");

        tv_needHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Need Help",Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.cash_collected).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraChargedPaid(parkingExtraChargesId);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void extraChargedPaid(final String parkingExtraChargesId) {

        mProgressBar.setVisibility(View.VISIBLE);

        String SEND_URL = BASE_URL + "updateAmountPaidInExtraParking.php";

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

                   updateBookingStatusToComplete();




                } catch (JSONException e) {
                    mProgressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG,error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("extra_charge_id",parkingExtraChargesId);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }


    private void setUpTimer(){

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                long curentTimeInMilli = System.currentTimeMillis();
                long hasToParkOut = Long.parseLong(mBooking.getParkOutTime())*1000L;

                long timeExtra = curentTimeInMilli - hasToParkOut;

                updateUpTimer(timeExtra);


            }
        },0,1000);
    }

    private void updateUpTimer(final long timeExtra) {

        final String formattedTime = formatTimeForWatch(timeExtra);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewCountDown.setText("+"+formattedTime + " extra");
            }
        });

    }
    private String formatTimeForWatch(long timeinMilli) {

        int hrs     = (int) (timeinMilli / 1000) / 3600;
        int minutes = (int) ( ((timeinMilli / 1000) / 60 ) - ( hrs * 60) );
        int seconds = (int) (timeinMilli / 1000) % 60;

        String formattedTime = String.format(Locale.getDefault(), "%2d:%02d:%02d", hrs,minutes, seconds);

        if(hrs == 0){
            formattedTime = String.format(Locale.getDefault(), "%02d:%02d",minutes, seconds);
        }

        return formattedTime;

    }




}
