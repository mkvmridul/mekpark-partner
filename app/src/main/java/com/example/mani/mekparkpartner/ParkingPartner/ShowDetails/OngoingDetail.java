package com.example.mani.mekparkpartner.ParkingPartner.ShowDetails;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.R;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;

public class OngoingDetail extends AppCompatActivity {

    private Booking mBooking;
    final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_detail);

        mBooking = (Booking) getIntent().getSerializableExtra("booking");

        setData();
        clickListener();
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
        tv_park_time.setText(mBooking.getParkInTime());
        tv_duration.setText(mBooking.getDuration()+" hrs");


        String bookingTime =  getFormattedTime(TAG, mBooking.getBookingTime());
        tv_book_time.setText(bookingTime);

        String bookingDate = getFormattedDate(TAG,mBooking.getBookingTime());
        tv_book_date.setText(bookingDate);


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OngoingDetail.this,"to be implemented",Toast.LENGTH_SHORT).show();
            }
        });


        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OngoingDetail.this,"to be implemented",Toast.LENGTH_SHORT).show();

            }
        });

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
