package com.example.mani.mekparkpartner.ParkingPartner.ShowDetails;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.R;

public class NewBookingDetails extends AppCompatActivity {

    Booking mNewBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking_details);

        //mNewBooking = (Booking) getIntent().getSerializableExtra("booking");

        setData();
        clickListener();
    }

    private void setData() {

        TextView tv_orderId     = findViewById(R.id.order_id);
        TextView tv_model       = findViewById(R.id.model);
        TextView tv_brand       = findViewById(R.id.brand);
        TextView tv_plate_no    = findViewById(R.id.plate_no);
        ImageView iv_vhicle     = findViewById(R.id.vehicle_image);
        TextView  iv_cus_name   = findViewById(R.id.cus_name);

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
}
