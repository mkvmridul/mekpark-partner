package com.example.mani.mekparkpartner.ParkingPartner.ShowDetails;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.R;
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
        tv_park_time.setText(mBooking.getParkInTime());
        tv_duration.setText(mBooking.getDuration()+" hrs");


        String bookingTime =  getFormattedTime(TAG, mBooking.getBookingTime());
        tv_book_time.setText(bookingTime);

        String bookingDate = getFormattedDate2(TAG,mBooking.getBookingTime());
        tv_book_date.setText(bookingDate);
        tv_park_date.setText(bookingDate); // Parking Date and booking date are same as long as booking is allowed in same day

        tv_from.setText("from: " + mBooking.getParkInTime());
        tv_to.setText("to: "+mBooking.getParkOutTime());






        ll_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mBooking.getCusPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);

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
}
