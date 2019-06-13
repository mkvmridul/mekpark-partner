package com.example.mani.mekparkpartner.OffileParkingPartner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
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

import com.example.mani.mekparkpartner.OffileParkingPartner.Details.DialogHistoryOfflineDetail;
import com.example.mani.mekparkpartner.OffileParkingPartner.Details.DialogOngoingOfflineDetail;
import com.example.mani.mekparkpartner.OffileParkingPartner.Model.OfflineParkingBooking;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.HistoryDetail;
import com.example.mani.mekparkpartner.R;

import java.util.List;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.CUSTOMER_CARE;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;


public class OfflineHistoryAdapter extends RecyclerView.Adapter<OfflineHistoryAdapter.HistoryViewHolder> {

    private final String TAG = "HistoryAdapter";
    private Context mCtx;
    private List<OfflineParkingBooking> mHistoryList;

    public OfflineHistoryAdapter(Context mCtx, List<OfflineParkingBooking> mHistoryList) {
        this.mCtx = mCtx;
        this.mHistoryList = mHistoryList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_history_offline,viewGroup,false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int i) {

        final OfflineParkingBooking booking = mHistoryList.get(i);

        holder.tv_id.setText("#"+booking.getOrderId());

        holder.tv_model.setText(booking.getModel());
        holder.tv_licence_plate.setText(booking.getLicencePlateNo());

        holder.tv_time.setText(getFormattedDate(TAG, booking.getBookingTime()));
        holder.tv_time.setText(getFormattedTime(TAG, booking.getBookingTime()));

        holder.tv_needHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = CUSTOMER_CARE;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                mCtx.startActivity(intent);
            }
        });

        holder.cv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"offline history detail");
                DialogHistoryOfflineDetail detail = new DialogHistoryOfflineDetail();
                Bundle bundle = new Bundle();
                bundle.putSerializable("offline_history_detail",booking);
                detail.setArguments(bundle);
                detail.show( ( (FragmentActivity)mCtx).getSupportFragmentManager(), null);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        CardView cv_detail;

        TextView tv_id, tv_date,tv_time,tv_model,tv_licence_plate;
        TextView tv_needHelp;
        ImageView iv_logo;
        TextView tv_message;
        LinearLayout ll_share;


        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_detail = itemView.findViewById(R.id.history_detail);

            tv_id            = itemView.findViewById(R.id.booking_id);
            tv_date          = itemView.findViewById(R.id.booking_date);
            tv_time          = itemView.findViewById(R.id.booking_time);
            //iv_logo        = itemView.findViewById(R.id.model_logo);
            tv_model         = itemView.findViewById(R.id.model_name);

            tv_licence_plate = itemView.findViewById(R.id.licence_plate);
            ll_share         = itemView.findViewById(R.id.share_layout);
            tv_needHelp      = itemView.findViewById(R.id.need_help);
            tv_message       = itemView.findViewById(R.id.message);
        }


    }
}
