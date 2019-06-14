package com.example.mani.mekparkpartner.OffileParkingPartner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.OffileParkingPartner.Model.OfflineParkingBooking;
import com.example.mani.mekparkpartner.R;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_DESCRIPTION;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_LOCATION;

public class ShareReceipt {

    private String TAG = this.getClass().getSimpleName();

    private Context mCtx;
    private OfflineParkingBooking mBooking;
    private int mIsParkingComplete;

    public ShareReceipt(Context context, OfflineParkingBooking mBokking, int mIsParkingComplete) {
        this.mCtx = context;
        this.mBooking = mBokking;
        this.mIsParkingComplete = mIsParkingComplete;
    }

    public void share() {

        Log.e(TAG,"called : share");

        View shareLayout;
        if(mIsParkingComplete == 1)
            shareLayout = LayoutInflater.from(mCtx).inflate(R.layout.dialog_receipt_parking_complete, null);
        else
            shareLayout = LayoutInflater.from(mCtx).inflate(R.layout.dialog_receipt, null);

        LinearLayout contentView = shareLayout.findViewById(R.id.receipt);
        setView(mCtx, shareLayout,mBooking,mIsParkingComplete);
        shareResult(mCtx,contentView);

    }
    private void shareResult(Context mCtx, LinearLayout contentView){
        Log.e(TAG,"called : shareContent");

        try {
            Bitmap bitmap = getBitmapFromView(contentView);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(mCtx, bitmap));
            shareIntent.setType("image/jpeg");
            mCtx.startActivity(Intent.createChooser(shareIntent,"share"));

        }catch (Exception e){
            e.getMessage();
            Log.e(TAG,e.toString());
        }

    }
    private Bitmap getBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            v.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }


        return null;
    }
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                    inImage, "", "");
            return Uri.parse(path);
        }catch (Exception e){
            e.getMessage();
        }
        return null;
    }
    private void setView(Context mCtx,View view, OfflineParkingBooking mBokking, int mIsParkingComplete) {

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


        tv_fare.setText("Rs "+mBokking.getFarePerHr()+"/hr");
        String partnerId = new LoginSessionManager(mCtx).getEmpDetailsFromSP().get(KEY_PARTNER_ID);
        tv_operator_id.setText(partnerId);

        tv_parking_name.setText(serviceDetail.get(S_DESCRIPTION));
        tv_location.setText(serviceDetail.get(S_LOCATION));
        tv_date.setText(getFormattedDate(TAG, mBokking.getParkInTime()));
        tv_time.setText(getFormattedTime(TAG,mBokking.getBookingTime()));
        tv_number_plate.setText(mBokking.getLicencePlateNo());
        tv_brand.setText(mBokking.getBrand());
        tv_model.setText(mBokking.getModel());

        //Only if parking complete
        if(mIsParkingComplete == 1) {
            ((TextView) view.findViewById(R.id.park_in_time)).setText(getFormattedTime(TAG, mBokking.getParkInTime()));
            ((TextView) view.findViewById(R.id.park_out_time)).setText(getFormattedTime(TAG, mBokking.getParkOutTime()));
            ((TextView) view.findViewById(R.id.duration)).setText(mBokking.getDuration() + " Hrs");
            ((TextView) view.findViewById(R.id.parking_fare)).setText("\u20B9 " + mBokking.getBaseFare());
            ((TextView) view.findViewById(R.id.tax_amount)).setText("\u20B9 " + mBokking.getTax());
            ((TextView) view.findViewById(R.id.additinal_charges)).setText("\u20B9 " + mBokking.getAddCharges());
            ((TextView) view.findViewById(R.id.total_fare)).setText("\u20B9 " + mBokking.getTotalFare());
        }

        String text = "<font color=#5d636b>Parking at owners risk. No responsibility for valuable items <br>like laptop, wallet, cash etc.<br></font>" +
                "<b><font color=#000000>Lost ticket charges RS 20 </font></b><><font color=#5d636b>after verification.</font>";
        tv_message.setText(Html.fromHtml(text));



    }


}
