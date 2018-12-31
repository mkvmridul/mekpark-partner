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
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate2;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;

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
        btn_startBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputedOtp = et1.getText().toString().trim()+
                        et2.getText().toString().trim()+
                        et3.getText().toString().trim()+
                        et4.getText().toString().trim();

                Toast.makeText(UpcomingDetail.this,inputedOtp,Toast.LENGTH_SHORT).show();

            }
        });



        tv_bookingId.setText("Order #"+mBooking.getBookingId());
        tv_model.setText(mBooking.getModel());
        tv_brand.setText(mBooking.getBrand());
        tv_plate_no.setText(mBooking.getLicencePlateNo());
        tv_park_time.setText(mBooking.getParkInTime());
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
