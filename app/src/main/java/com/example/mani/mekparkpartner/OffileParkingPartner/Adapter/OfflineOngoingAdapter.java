package com.example.mani.mekparkpartner.OffileParkingPartner.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.OffileParkingPartner.AddNewBooking;
import com.example.mani.mekparkpartner.OffileParkingPartner.Details.DialogOngoingOfflineDetail;
import com.example.mani.mekparkpartner.OffileParkingPartner.Model.OfflineParkingBooking;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.OngoingDetail;
import com.example.mani.mekparkpartner.R;
import com.example.mani.mekparkpartner.TowingPartner.details.DialogNewTowingDetail;

import java.util.HashMap;
import java.util.List;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.KEY_CAR;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_BIKE_FARE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_CAR_FARE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_DESCRIPTION;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_LOCATION;

public class OfflineOngoingAdapter extends RecyclerView.Adapter<OfflineOngoingAdapter.OngoingViewHolder> {

    private final String TAG = "OfflineOngoingAdapter";
    private Context mCtx;
    private List<OfflineParkingBooking> mOngoingList;

    public OfflineOngoingAdapter(Context mCtx, List<OfflineParkingBooking> mOngoingList) {
        this.mCtx = mCtx;
        this.mOngoingList = mOngoingList;
    }

    @NonNull
    @Override
    public OngoingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_ongoing_oofline,viewGroup,false);
        return new OngoingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OngoingViewHolder holder, int i) {

        final OfflineParkingBooking booking = mOngoingList.get(i);

        holder.tv_id.setText("#"+booking.getOrderId());

        holder.tv_model.setText(booking.getModel());
        holder.tv_licence_plate.setText(booking.getLicencePlateNo());

        holder.tv_date.setText(getFormattedDate(TAG, booking.getBookingTime()));
        holder.tv_time.setText(getFormattedTime(TAG, booking.getBookingTime()));


        holder.ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = booking.getCustPhone();
                if(phone.equals("")){
                    Toast.makeText(mCtx,"Phone number not provided",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                mCtx.startActivity(intent);
            }
        });

        holder.tv_printReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReceiptPage(booking);
            }
        });


        holder.cv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"ongoing detail");
                DialogOngoingOfflineDetail detail = new DialogOngoingOfflineDetail();
                Bundle bundle = new Bundle();
                bundle.putSerializable("offline_ongoing_detail",booking);
                detail.setArguments(bundle);
                detail.show( ( (FragmentActivity)mCtx).getSupportFragmentManager(), null);
            }
        });



    }



    @Override
    public int getItemCount() {
        return mOngoingList.size();
    }

    public class OngoingViewHolder extends RecyclerView.ViewHolder {

        CardView cv_detail;

        TextView tv_id,tv_date, tv_time,tv_model,tv_licence_plate;
        TextView tv_printReceipt;
        ImageView iv_logo;
        LinearLayout ll_call;

        public OngoingViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_detail = itemView.findViewById(R.id.ongoing_detail);

            tv_id            = itemView.findViewById(R.id.booking_id);
            tv_date          = itemView.findViewById(R.id.booking_date);
            tv_time          = itemView.findViewById(R.id.booking_time);
            //iv_logo        = itemView.findViewById(R.id.model_logo);
            tv_model         = itemView.findViewById(R.id.model_name);

            tv_licence_plate = itemView.findViewById(R.id.licence_plate);
            ll_call          = itemView.findViewById(R.id.call_layout);
            tv_printReceipt  = itemView.findViewById(R.id.print_receipt);

        }
    }

    private void openReceiptPage(OfflineParkingBooking booking) {

        final Dialog dialog = new Dialog(mCtx);
        View view = LayoutInflater.from(mCtx).inflate(R.layout.dialog_receipt, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BillAnimation1;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        HashMap<String, String> serviceDetail = new LoginSessionManager(mCtx).getServiceDetailFromSF();

        TextView tv_parking_name = view.findViewById(R.id.parking_name);
        TextView tv_location     = view.findViewById(R.id.location);
        TextView tv_date         = view.findViewById(R.id.date);
        TextView tv_time         = view.findViewById(R.id.time);
        TextView tv_number_plate = view.findViewById(R.id.number_plate);
        TextView tv_brand        = view.findViewById(R.id.brand);
        TextView tv_model        = view.findViewById(R.id.model);
        TextView tv_fare         = view.findViewById(R.id.fare_per_hr);
        TextView tv_operator_id  = view.findViewById(R.id.operator_id);
        TextView tv_message      = view.findViewById(R.id.message);

        tv_parking_name.setText(serviceDetail.get(S_DESCRIPTION));
        tv_location.setText(serviceDetail.get(S_LOCATION));
        tv_date.setText(getFormattedDate(TAG,booking.getParkInTime()));
        tv_time.setText(getFormattedTime(TAG,booking.getParkInTime()));
        tv_number_plate.setText(booking.getLicencePlateNo());
        tv_brand.setText(booking.getBrand());
        tv_model.setText(booking.getBrand());


        tv_fare.setText("Rs "+booking.getFarePerHr()+"/hr");
        String partnerId = new LoginSessionManager(mCtx).getEmpDetailsFromSP().get(KEY_PARTNER_ID);
        tv_operator_id.setText(partnerId);

        String text = "<font color=#5d636b>Parking at owners risk. No responsibility for valuable items like laptop, wallet, cash etc. </font>" +
                "<b><font color=#000000>Lost ticket charges RS 20 </font></b><><font color=#5d636b>after verification.</font>";
        tv_message.setText(Html.fromHtml(text));

        view.findViewById(R.id.print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx,"printing.......",Toast.LENGTH_SHORT).show();

            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
