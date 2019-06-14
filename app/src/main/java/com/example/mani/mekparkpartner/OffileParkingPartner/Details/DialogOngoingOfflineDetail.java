package com.example.mani.mekparkpartner.OffileParkingPartner.Details;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.OffileParkingPartner.AddNewBooking;
import com.example.mani.mekparkpartner.OffileParkingPartner.Model.OfflineParkingBooking;
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.OngoingDetail;
import com.example.mani.mekparkpartner.R;
import com.example.mani.mekparkpartner.TowingPartner.Model.TowingBooking;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.CUSTOMER_CARE;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_DESCRIPTION;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_LOCATION;

public class DialogOngoingOfflineDetail extends DialogFragment {

    private String TAG = "DialogOngoingOfflineDetail";
    private View mRootView;

    private OfflineParkingBooking mBooking;
    private ProgressBar mProgressBar;

    private  long START_TIME_IN_MILLIS;
    private CountDownTimer mCountDownTimer;
    private TextView mTextViewCountDown;
    private TextView m_tv_min_left_text;

    private boolean mTimerRunning;
    private long mTimeLeftInMillis;


    public DialogOngoingOfflineDetail() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "called : onCreateDialog");

        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BillAnimation1;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = (OfflineParkingBooking) getArguments().getSerializable("offline_ongoing_detail");


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e(TAG, "called : onCreateView");
        mRootView = inflater.inflate(R.layout.dialog_offline_ongoing_detail, container, false);
        mProgressBar = mRootView.findViewById(R.id.progress_bar);
        setViewsAndClickListener();

        setDurationAndTimer();



        return mRootView;
    }

    private void setDurationAndTimer() {

        long parkingInUnix     = Long.parseLong( mBooking.getParkInTime());
        long curentTime        = System.currentTimeMillis()/1000L;
        long timePastInSec     = curentTime - parkingInUnix;

        int hrsToDisply        = (int) Math.ceil((double) timePastInSec / 3600);
        TextView tv_duration   = mRootView.findViewById(R.id.duration);
        tv_duration.setText(hrsToDisply+"hrs");

        mTimeLeftInMillis = (int) ( (3600-(timePastInSec%3600)) *1000);

        setTimer(hrsToDisply);


    }

    @Override
    public void onDestroyView() {
        Log.e(TAG,"called : onDestroyView");
        super.onDestroyView();
        assert getFragmentManager() != null;
        Fragment fragment = this;
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void setViewsAndClickListener() {

        mRootView.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroyView();
            }
        });

        ((TextView)mRootView.findViewById(R.id.booking_id)).setText("Order #"+mBooking.getOrderId());

        ((TextView)mRootView.findViewById(R.id.model)).setText(mBooking.getModel());
        ((TextView)mRootView.findViewById(R.id.plate_no)).setText(mBooking.getLicencePlateNo());

        String location = new LoginSessionManager(getActivity()).getServiceDetailFromSF().get(S_LOCATION);
        ((TextView)mRootView.findViewById(R.id.location)).setText(location);

        mRootView.findViewById(R.id.call_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mBooking.getCustPhone();
                if(phone.equals("")){
                    Toast.makeText(getActivity(),"Phone number not provided",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                getActivity().startActivity(intent);

            }
        });

        ((TextView)mRootView.findViewById(R.id.parking_date)).setText(getFormattedDate(TAG,mBooking.getParkInTime()));
        ((TextView)mRootView.findViewById(R.id.parking_time)).setText(getFormattedTime(TAG,mBooking.getParkInTime()));

        mRootView.findViewById(R.id.update_customer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String custPhone = mBooking.getCustPhone();
                if(custPhone.equals("")){
                    Toast.makeText(getActivity(),"Phone number not provided by Customer",Toast.LENGTH_SHORT).show();
                    return;
                }

                updateCustomerViaSms(custPhone);
            }
        });

        mRootView.findViewById(R.id.parking_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateDurationAndFareAndSendInfoToDb();
            }
        });



    }



    private void calculateDurationAndFareAndSendInfoToDb() {

        final long parkIn,parkOut, timeinSec;
        final int hrs;
        final double baseFare,t_tax,tax,t_addi,addi,totalFare;

        parkIn   = Long.parseLong(mBooking.getParkInTime());
        parkOut  = System.currentTimeMillis() / 1000L; //Need to get from database
        timeinSec= parkOut - parkIn;

        hrs      = (int) Math.ceil((double) timeinSec / 3600);
        baseFare = Double.parseDouble(mBooking.getFarePerHr()) * hrs;
        t_tax    = baseFare * 0.18;
        tax      = new BigDecimal(t_tax).setScale(1, RoundingMode.HALF_UP).doubleValue();
        t_addi   = 12;
        addi     = new BigDecimal(t_addi).setScale(1, RoundingMode.HALF_UP).doubleValue();

        totalFare = baseFare + tax + addi;

        Log.e(TAG,baseFare+" "+tax +" "+addi+" "+totalFare);


        //Due Dialog
        final Dialog dialog = new Dialog(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.due_page_offline, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BillAnimation1;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView tv_totalFare1  = view.findViewById(R.id.total_extra_fare);
        TextView tv_needHelp    = view.findViewById(R.id.need_help);
        TextView tv_dur         = view.findViewById(R.id.total_extra_duration);
        TextView tv_baseFare    = view.findViewById(R.id.extra_base_fare);
        TextView tv_discount    = view.findViewById(R.id.tax_amount);
        TextView tv_addCharges  = view.findViewById(R.id.extra_additinal_charges);
        TextView tv_totalFare2  = view.findViewById(R.id.total_extra_fare2);

        tv_totalFare1.setText(""+totalFare);
        tv_dur.setText(""+hrs+" Hrs");
        tv_baseFare.setText(""+baseFare);
        tv_discount.setText(""+tax);
        tv_addCharges.setText(""+addi);
        tv_totalFare2.setText(""+totalFare);



        tv_needHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = CUSTOMER_CARE;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                getActivity().startActivity(intent);
            }
        });

        final double finalTax = tax;
        final double finalAddi = addi;

        view.findViewById(R.id.cash_collected).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendParkingCompleteDataToDb(baseFare, finalTax, finalAddi,totalFare,parkOut,hrs);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void sendParkingCompleteDataToDb(final double baseFare, final double tax, final double addi,
                                             final double totalFare, final long parkout, final int duration) {

        Log.e(TAG,"called : parkingComplete");
        String URL = BASE_URL + "offline_parking_complete.php";

        mProgressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       mProgressBar.setVisibility(View.GONE);
                        Log.e(TAG,response);

                        try {
                            int rc = new JSONArray(response).getJSONObject(0).getInt("rc");
                            if(rc == 1){
                                onDestroyView();
                                getActivity().finish();
                            }
                        } catch (JSONException e) {
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

                Map<String, String> params = new HashMap<>();

                params.put("offline_booking_id", String.valueOf(mBooking.getId()));
                params.put("parkout", String.valueOf(parkout));
                params.put("duration", String.valueOf(duration));
                params.put("base_fare", String.valueOf(baseFare));
                params.put("tax", String.valueOf(tax));
                params.put("add_fare", String.valueOf(addi));
                params.put("total_fare", String.valueOf(totalFare));
                params.put("status","1");
                params.put("payment_complete","1");

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS*1000),
                NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);


    }

    private void setTimer(int hrsToDisply){
        mTextViewCountDown = mRootView.findViewById(R.id.countdown);
        m_tv_min_left_text = mRootView.findViewById(R.id.min_left);
        m_tv_min_left_text.setText("min left in "+hrsToDisply+" hrs");
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
                Log.e(TAG,"hua.........");

                long parkingInUnix     = Long.parseLong( mBooking.getParkInTime());
                long curentTime        = System.currentTimeMillis()/1000L;
                long timePastInSec     = curentTime - parkingInUnix;

                int hrsToDisply        = (int) Math.ceil((double) timePastInSec / 3600);
                TextView tv_duration   = mRootView.findViewById(R.id.duration);
                tv_duration.setText(hrsToDisply+"hrs");

                setDurationAndTimer();
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

    private void updateCustomerViaSms(String custPhone) {

        Log.e(TAG,"called : updateCustomerViaSms");
        mProgressBar.setVisibility(View.VISIBLE);

        TextView textView1 = mRootView.findViewById(R.id.countdown);
        TextView textView2 = mRootView.findViewById(R.id.min_left);
        String mess1       = textView1.getText().toString().trim();
        String mess2       = textView2.getText().toString().trim();

        String parkingName = new LoginSessionManager(getActivity()).getServiceDetailFromSF().get(S_DESCRIPTION);

        String mobile = "+91"+custPhone;
        String message = "From "+parkingName+" :\n "+
                mess1+" "+mess2+" "+
                "for your parking with order id +"+mBooking.getOrderId();

        String URL = "http://api.msg91.com/api/sendhttp.php?country=91&sender=MSGIND&route=4" +
                "&mobiles="+mobile+"&authkey=192785A1sPOm0965a584ea5&message="+message+".";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        Log.e(TAG,response);
                        Toast.makeText(getActivity(),"Thankyou for updating customer.",Toast.LENGTH_SHORT).show();
                        onDestroyView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG,error.toString());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS*1000),
                NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }


}
