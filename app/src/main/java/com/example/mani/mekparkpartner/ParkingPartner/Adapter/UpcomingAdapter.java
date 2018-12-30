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

import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.UpcomingDetail;
import com.example.mani.mekparkpartner.R;

import java.util.List;

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder> {

    private final String TAG = "UpcomingAdapter";
    private Context mCtx;
    private List<Booking> mUpcomingList;

    public UpcomingAdapter(Context mCtx, List<Booking> mUpcomingList) {
        this.mCtx = mCtx;
        this.mUpcomingList = mUpcomingList;
    }

    @NonNull
    @Override
    public UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_upcoming,viewGroup,false);
        return new UpcomingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int i) {

        final Booking booking = mUpcomingList.get(i);

        holder.cv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"upcoming detail");
                Intent i = new Intent(mCtx,UpcomingDetail.class);
                i.putExtra("booking",booking);
                mCtx.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUpcomingList.size();
    }

    public class UpcomingViewHolder extends RecyclerView.ViewHolder {

        CardView cv_detail;


        public UpcomingViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_detail = itemView.findViewById(R.id.upcoming_detail);
        }
    }
}
