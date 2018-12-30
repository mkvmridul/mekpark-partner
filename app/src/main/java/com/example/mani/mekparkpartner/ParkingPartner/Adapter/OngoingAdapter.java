package com.example.mani.mekparkpartner.ParkingPartner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.NewBookingDetails;
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.OngoingDetail;
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.UpcomingDetail;
import com.example.mani.mekparkpartner.R;

import java.util.List;

public class OngoingAdapter extends RecyclerView.Adapter<OngoingAdapter.OngoingViewHolder> {

    private final String TAG = "OngoingAdapter";
    private Context mCtx;
    private List<Booking> mOngoingList;

    public OngoingAdapter(Context mCtx, List<Booking> mOngoingList) {
        this.mCtx = mCtx;
        this.mOngoingList = mOngoingList;
    }

    @NonNull
    @Override
    public OngoingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_ongoing,viewGroup,false);
        return new OngoingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OngoingViewHolder holder, int i) {

        final Booking booking = mOngoingList.get(i);

        holder.cv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"ongoing detail");
                Intent i = new Intent(mCtx,OngoingDetail.class);
                i.putExtra("booking",booking);
                mCtx.startActivity(i);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mOngoingList.size();
    }

    public class OngoingViewHolder extends RecyclerView.ViewHolder {

        CardView cv_detail;

        public OngoingViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_detail = itemView.findViewById(R.id.ongoing_detail);

        }
    }
}
