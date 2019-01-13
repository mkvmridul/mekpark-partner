package com.example.mani.mekparkpartner.ParkingPartner.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentNew;
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.NewBookingDetails;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.sentNotificationToUser;
import static javax.net.ssl.SSLEngineResult.Status.OK;


public class NewBookingAdapter extends RecyclerView.Adapter<NewBookingAdapter.NewBookingViewHolder> {

    private final String TAG = "NewBookingAdapter";
    private Context mCtx;
    private List<Booking> mNewBookingList;
    private FragmentNew listener;

    public NewBookingAdapter(Context mCtx, List<Booking> mNewBookingList, FragmentNew fragmentNew) {
        this.mCtx = mCtx;
        this.mNewBookingList = mNewBookingList;
        this.listener = fragmentNew;
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

        holder.tv_id.setText("#"+booking.getBookingId());

        holder.tv_model.setText(booking.getModel());
        holder.tv_licence_plate.setText(booking.getLicencePlateNo());

        holder.tv_duration.setText(booking.getDuration()+" hrs");
        holder.tv_fare.setText(mCtx.getString(R.string.rupee_symbol)+" " + booking.getTotalFare());

        String parkinTime =  getFormattedTime(TAG, booking.getParkInTime());
        holder.tv_parking_start.setText(parkinTime);

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


        holder.tv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sent to upcoming
                updateStatus(booking.getCusId(),booking.getBookingId(),2,"Order Accepted and moved to upcoming");
            }
        });

        holder.tv_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sent to complete(rejected by partner)
                setRejectDialog(booking.getCusId(),booking.getBookingId(), 4,"Order Rejected by partner");
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

    private void setRejectDialog(final int cusId, final int bookingId, final int status, final String message) {

        final AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mCtx, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mCtx);
        }
        builder.setTitle("Reject order")
                .setMessage("Are you sure you want to reject this order?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateStatus(cusId,bookingId,status,message);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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

    private void updateStatus(final int cusId, final int bookingId, final int status, final String message) {

        Log.e(TAG,"called : updateStatus");

        String SEND_URL = BASE_URL + "update_status_of_parking_booking.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG,response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");
                    String mess = jsonArray.getJSONObject(0).getString("mess");

                    if(rc<=0){
                        Log.e(TAG,mess);
                        Toast.makeText(mCtx,mess,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.e(TAG,mess);
                    Toast.makeText(mCtx,message,Toast.LENGTH_SHORT).show();

                    prepareNotification(status,cusId,bookingId);
                    listener.refresh();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
                Toast.makeText(mCtx,error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("booking_id",String.valueOf(bookingId));
                params.put("status",String.valueOf(status));
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(mCtx).addToRequestQueue(stringRequest);

    }

    private void prepareNotification(int status, int cusId, int bookingId) {
        String title = "";
        String message = "";

        if(status == 2){
            title = "Accepted";
            message = "Your parking order for booking id "+bookingId + " is accepted.";
        }
        else if(status == 4){
            title = "Rejected";
            message = "Your parking order for booking id "+bookingId + " is rejected.";
        }

        sentNotificationToUser(mCtx,cusId,title,message);
    }

}
