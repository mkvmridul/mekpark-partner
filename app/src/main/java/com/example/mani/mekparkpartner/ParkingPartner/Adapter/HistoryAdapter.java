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
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.HistoryDetail;
import com.example.mani.mekparkpartner.R;

import java.util.List;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;


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

        holder.tv_id.setText("#"+booking.getBookingId());

        holder.tv_model.setText(booking.getModel());
        holder.tv_licence_plate.setText(booking.getLicencePlateNo());

        String bookingTime =  getFormattedTime(TAG, booking.getBookingTime());
        holder.tv_time.setText(bookingTime);

        holder.ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = booking.getCusPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                mCtx.startActivity(intent);
            }
        });

        holder.tv_needHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx,"to be implemented",Toast.LENGTH_SHORT).show();
            }
        });


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

        TextView tv_id, tv_time,tv_model,tv_licence_plate;
        TextView tv_needHelp;
        ImageView iv_logo;
        LinearLayout ll_call;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_detail = itemView.findViewById(R.id.history_detail);

            tv_id            = itemView.findViewById(R.id.booking_id);
            tv_time          = itemView.findViewById(R.id.booking_time);
            //iv_logo        = itemView.findViewById(R.id.model_logo);
            tv_model         = itemView.findViewById(R.id.model_name);

            tv_licence_plate = itemView.findViewById(R.id.licence_plate);
            ll_call          = itemView.findViewById(R.id.call_layout);
            tv_needHelp      = itemView.findViewById(R.id.need_help);
        }


    }
}
