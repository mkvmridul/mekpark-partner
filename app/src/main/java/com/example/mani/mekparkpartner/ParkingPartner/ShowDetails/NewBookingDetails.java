package com.example.mani.mekparkpartner.ParkingPartner.ShowDetails;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.ParkingPartner.Adapter.NewBookingAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate2;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;


public class NewBookingDetails extends AppCompatActivity {



    private Booking mBooking;
    final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking_details);

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
        TextView  tv_cus_name   = findViewById(R.id.cus_name);

        LinearLayout ll_Call    = findViewById(R.id.call_layout);
        TextView  tv_book_date  = findViewById(R.id.booking_date);
        TextView  tv_book_time  = findViewById(R.id.booking_time);
        TextView  tv_park_date  = findViewById(R.id.parking_date);
        TextView  tv_park_time  = findViewById(R.id.parking_time);
        TextView  tv_duration   = findViewById(R.id.duration);
        TextView  tv_from       = findViewById(R.id.from);
        TextView  tv_to         = findViewById(R.id.to);
        TextView  btn_reject    = findViewById(R.id.reject);
        TextView  btn_accept    = findViewById(R.id.accept);

        tv_bookingId.setText("Order #"+mBooking.getBookingId());
        tv_model.setText(mBooking.getModel());
        tv_brand.setText(mBooking.getBrand());
        tv_plate_no.setText(mBooking.getLicencePlateNo());
        tv_cus_name.setText(mBooking.getCusName());

        tv_duration.setText(mBooking.getDuration()+" hrs");

        String parkinTime =  getFormattedTime(TAG, mBooking.getParkInTime());
        tv_park_time.setText(parkinTime);


        String bookingTime =  getFormattedTime(TAG, mBooking.getBookingTime());
        tv_book_time.setText(bookingTime);

        String bookingDate = getFormattedDate2(TAG,mBooking.getBookingTime());
        tv_book_date.setText(bookingDate);
        tv_park_date.setText(bookingDate); // Parking Date and booking date are same as long as booking is allowed in same day

        tv_from.setText("from: "+parkinTime);
        String parkout =  getFormattedTime(TAG, mBooking.getParkOutTime());
        tv_to.setText("to: "+parkout
        +  0);

        ll_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mBooking.getCusPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);

            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus(mBooking.getBookingId(),2,"Order Accepted and moved to upcoming");
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sent to complete(rejected by partner)
                setRejectDialog(mBooking.getBookingId(), 4,"Order Rejected by partner");
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

    private void setRejectDialog(final int bookingId, final int status, final String message) {

        final AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(NewBookingDetails.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(NewBookingDetails.this);
        }
        builder.setTitle("Reject order")
                .setMessage("Are you sure you want to reject this order?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateStatus(bookingId,status,message);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
                        Toast.makeText(NewBookingDetails.this,mess,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.e(TAG,mess);
                    Toast.makeText(NewBookingDetails.this,message,Toast.LENGTH_SHORT).show();

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
                Toast.makeText(NewBookingDetails.this,error.toString(),Toast.LENGTH_SHORT).show();

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
        MySingleton.getInstance(NewBookingDetails.this).addToRequestQueue(stringRequest);

    }
}
