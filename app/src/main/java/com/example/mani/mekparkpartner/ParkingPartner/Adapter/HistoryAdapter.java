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
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.HistoryDetail;
import com.example.mani.mekparkpartner.R;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final String TAG = "HistoryAdapter";
    private Context mCtx;
    private List<Booking> mHistoryList;

    public HistoryAdapter(Context mCtx, List<Booking> mHistoryList) {
        this.mCtx = mCtx;
        this.mHistoryList = mHistoryList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recycler_view_history,viewGroup,false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int i) {

        final Booking booking = mHistoryList.get(i);

        holder.cv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"history detail");
                Intent i = new Intent(mCtx,HistoryDetail.class);
                i.putExtra("booking",booking);
                mCtx.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        CardView cv_detail;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_detail = itemView.findViewById(R.id.history_detail);
        }


    }
}
