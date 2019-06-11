package com.example.mani.mekparkpartner.TowingPartner.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mani.mekparkpartner.R;
import com.example.mani.mekparkpartner.TowingPartner.Model.TowingBooking;
import com.example.mani.mekparkpartner.TowingPartner.details.DialogCompletedTowingDetail;
import com.example.mani.mekparkpartner.TowingPartner.details.DialogNewTowingDetail;
import com.example.mani.mekparkpartner.TowingPartner.details.DialogOngoingTowingDetail;
import com.example.mani.mekparkpartner.TowingPartner.details.DialogUpcomingTowingDetail;

import java.util.List;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;

public class TowingAdapter extends RecyclerView.Adapter<TowingAdapter.NewTowingViewHolder> {

    private String TAG = "TowingAdapter";

    private Context mCtx;
    private List<TowingBooking> mNewTowingList;

    public TowingAdapter(Context mCtx, List<TowingBooking> mNewTowingList) {
        this.mCtx = mCtx;
        this.mNewTowingList = mNewTowingList;
    }

    @NonNull
    @Override
    public NewTowingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NewTowingViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_towing_for_all_booking,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewTowingViewHolder holder, int i) {

        final TowingBooking booking = mNewTowingList.get(i);

        // Same for all type of towing booking
        holder.tv_orderId.setText("#"+booking.getBookingId());

        holder.tv_model.setText(booking.getModel());
        holder.tv_licence_plate.setText(booking.getLicencePlateNo());

        holder.tv_bookingTime.setText(getFormattedTime(TAG, booking.getBookingTime()));
        holder.tv_bookingDate.setText(getFormattedDate(TAG, booking.getBookingTime()));


        //Variable part

        final int status = booking.getStatus();

        /* 0 - New
           2 - Upcoming
           3 - Ongoing

           1,4,5 - (complete)

           1 - Completed successfully
           4 - Rejected by partner
           5 - Cancled by user

         */


        switch (status){

            case 0:
                //New Towing Booking
                holder.tv_comment.setText("Awaiting Confirmation");
                holder.tv_comment.setTextColor(ContextCompat.getColor(mCtx,R.color.red_dark));
                holder.btn1.setText("Accept");
                holder.btn2.setText("Reject");

                holder.btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mCtx,"New - Accept",Toast.LENGTH_SHORT).show();
                    }
                });

                holder.btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mCtx,"New - Reject",Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case 2:
                //Upcoming Towing Booking
                holder.tv_comment.setText("Payment Confirmation");
                holder.tv_comment.setTextColor(ContextCompat.getColor(mCtx,R.color.green));
                holder.btn1.setText("Need Help ?");
                holder.btn2.setText("Connect");

                holder.btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mCtx,"Upcoming - Need help",Toast.LENGTH_SHORT).show();
                     }
                });

                holder.btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mCtx,"Upcoming - Connect",Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case 3:
                //Ongoing Towing Booking
                holder.tv_comment.setText("Service in Progress");
                holder.tv_comment.setTextColor(ContextCompat.getColor(mCtx,R.color.yellow));
                holder.btn1.setText("Need Help ?");
                holder.btn2.setText("Connect");

                holder.btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mCtx,"Ongoing - Need help",Toast.LENGTH_SHORT).show();
                    }
                });

                holder.btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mCtx,"Ongoing - Connect",Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case 1:
                //Towing Completed Successfullu
                holder.tv_comment.setText("Service Completed");
                holder.tv_comment.setTextColor(ContextCompat.getColor(mCtx,R.color.green));
                holder.btn1.setText("Need Help ?");
                holder.btn2.setVisibility(View.GONE);

                holder.btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mCtx,"History1 - Need help",Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case 4:
                //Towing Completed
                holder.tv_comment.setText("Rejected by Partner");
                holder.tv_comment.setTextColor(ContextCompat.getColor(mCtx,R.color.red_dark));
                holder.btn1.setText("Need Help ?");
                holder.btn2.setVisibility(View.GONE);

                holder.btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mCtx,"History4 - Need help",Toast.LENGTH_SHORT).show();
                    }
                });

                break;


            case 5:
                //Towing Completed
                holder.tv_comment.setText("Cancelled by User");
                holder.tv_comment.setTextColor(ContextCompat.getColor(mCtx,R.color.red_dark));
                holder.btn1.setText("Need Help ?");
                holder.btn2.setVisibility(View.GONE);

                holder.btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mCtx,"History5 - Need help",Toast.LENGTH_SHORT).show();
                    }
                });

                break;


        }

        holder.cv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (status){
                    //New
                    case 0:
                        DialogNewTowingDetail detail = new DialogNewTowingDetail();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("new_towing_booking",booking);

                        detail.setArguments(bundle);

                        detail.show( ( (FragmentActivity)mCtx).getSupportFragmentManager(), null);
                        break;

                    //Upcoming
                    case 2:
                        DialogUpcomingTowingDetail detail2 = new DialogUpcomingTowingDetail();
                        bundle = new Bundle();
                        bundle.putSerializable("new_upcoming_booking",booking);
                        detail2.setArguments(bundle);
                        detail2.show( ( (FragmentActivity)mCtx).getSupportFragmentManager(), null);
                        break;

                    //Ongoing
                    case 3:
                        DialogOngoingTowingDetail detail3 = new DialogOngoingTowingDetail();
                        bundle = new Bundle();
                        bundle.putSerializable("new_ongoing_booking",booking);
                        detail3.setArguments(bundle);
                        detail3.show( ( (FragmentActivity)mCtx).getSupportFragmentManager(), null);
                        break;

                    //Completed
                    case 1: case 4: case 5:
                        DialogCompletedTowingDetail detail4 = new DialogCompletedTowingDetail();
                        bundle = new Bundle();
                        bundle.putSerializable("completed_towing_booking",booking);
                        detail4.setArguments(bundle);
                        detail4.show( ( (FragmentActivity)mCtx).getSupportFragmentManager(), null);
                        break;

                }



            }
        });











    }

    @Override
    public int getItemCount() {
        return mNewTowingList.size();
    }


    public class NewTowingViewHolder extends RecyclerView.ViewHolder {

        TextView tv_bookingDate, tv_bookingTime, tv_orderId, tv_model, tv_licence_plate;
        TextView tv_comment, btn1, btn2;
        ImageView iv_icon;

        CardView cv_detail;

        public NewTowingViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_bookingDate = itemView.findViewById(R.id.booking_date);
            tv_bookingTime = itemView.findViewById(R.id.booking_time);
            tv_orderId     = itemView.findViewById(R.id.order_id);

            iv_icon        = itemView.findViewById(R.id.model_logo);

            tv_model         = itemView.findViewById(R.id.model_name);
            tv_licence_plate = itemView.findViewById(R.id.licence_plate);

            tv_comment     = itemView.findViewById(R.id.status_comment);
            btn1           = itemView.findViewById(R.id.btn1);
            btn2           = itemView.findViewById(R.id.btn2);

            cv_detail = itemView.findViewById(R.id.detail);



        }
    }
}
