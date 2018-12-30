package com.example.mani.mekparkpartner.ParkingPartner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.NewBookingDetails;
import com.example.mani.mekparkpartner.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.TIME_FORMAT;

public class NewBookingAdapter extends RecyclerView.Adapter<NewBookingAdapter.NewBookingViewHolder> {

    private final String TAG = "NewBookingAdapter";
    private Context mCtx;
    private List<Booking> mNewBookingList;


    public NewBookingAdapter(Context mCtx, List<Booking> mNewBookingList) {
        this.mCtx = mCtx;
        this.mNewBookingList = mNewBookingList;
    }

    @NonNull
    @Override
    public NewBookingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_new_bookings,viewGroup,false);
        return new NewBookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewBookingViewHolder holder, int i) {

        final Booking booking = mNewBookingList.get(i);

//        Date date;
//        SimpleDateFormat sdf = new java.text.SimpleDateFormat(TIME_FORMAT);
//
//        String bookingTime = "NA";
//
//        try {
//            Long unix   = Long.valueOf(booking.getBookingTime());
//            date        = new java.util.Date(unix*1000L);
//            bookingTime = sdf.format(date);
//
//        }catch (Exception e){
//            Log.e(TAG,"Exception cought 1 : "+e.toString());
//
//        }
//
//        String duration = booking.getDuration();
//
//
//        holder.tv_id.setText("#"+String.valueOf(booking.getBookingId()));
//        holder.tv_time.setText(bookingTime);
//        holder.tv_model.setText(booking.getModel());
//        holder.tv_licence_plate.setText(booking.getLicencePlateNo());
//        holder.tv_fare.setText("\u20B9 "+booking.getFare());
//        holder.tv_parking_start.setText(booking.getParkingTime());
//        holder.tv_duration.setText(booking.getDuration()+" Hrs");
//
        holder.ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = booking.getCusPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                mCtx.startActivity(intent);
            }
        });


        holder.tv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx,"to be implemented 1",Toast.LENGTH_SHORT).show();
            }
        });

        holder.tv_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx,"to be implemented 2",Toast.LENGTH_SHORT).show();
            }
        });

        holder.cv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"newBookingDetail");
                Intent i = new Intent(mCtx,NewBookingDetails.class);
                i.putExtra("booking",booking);
                mCtx.startActivity(i);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mNewBookingList.size();
    }

    public class NewBookingViewHolder extends RecyclerView.ViewHolder {

        TextView tv_id, tv_time,tv_model,tv_licence_plate,tv_parking_start,tv_duration,tv_fare;
        TextView tv_reject,tv_accept;
        ImageView iv_logo;
        LinearLayout ll_call;
        CardView cv_detail;

        public NewBookingViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_id             = itemView.findViewById(R.id.booking_id);
            tv_time           = itemView.findViewById(R.id.booking_time);
            //iv_logo           = itemView.findViewById(R.id.model_logo);
            tv_model          = itemView.findViewById(R.id.model_name);

            tv_licence_plate = itemView.findViewById(R.id.licence_plate);
            ll_call        = itemView.findViewById(R.id.call_layout);
            tv_parking_start = itemView.findViewById(R.id.parking_start);
            tv_duration      = itemView.findViewById(R.id.duration);
            tv_fare          = itemView.findViewById(R.id.fare);

            tv_reject        = itemView.findViewById(R.id.reject);
            tv_accept        = itemView.findViewById(R.id.accept);

            cv_detail       = itemView.findViewById(R.id.new_booking_detail);

        }
    }
}
