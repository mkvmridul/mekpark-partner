package com.example.mani.mekparkpartner.OffileParkingPartner.Details;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.audiofx.DynamicsProcessing;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.OffileParkingPartner.Model.OfflineParkingBooking;
import com.example.mani.mekparkpartner.OffileParkingPartner.ShareReceipt;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.CUSTOMER_CARE;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_DESCRIPTION;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_LOCATION;

public class DialogHistoryOfflineDetail extends DialogFragment {

    private String TAG = "DialogOngoingOfflineDetail";
    private View mRootView;

    private OfflineParkingBooking mBooking;
    private ProgressBar mProgressBar;

    public DialogHistoryOfflineDetail() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "called : onCreateDialog");

        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BillAnimation1;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = (OfflineParkingBooking) getArguments().getSerializable("offline_history_detail");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e(TAG, "called : onCreateView");
        mRootView = inflater.inflate(R.layout.dialog_offline_history_detail, container, false);
        mProgressBar = mRootView.findViewById(R.id.progress_bar);
        setViewsAndClickListener();
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG,"called : onDestroyView");
        super.onDestroyView();
        assert getFragmentManager() != null;
        Fragment fragment = this;
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void setViewsAndClickListener() {

        mRootView.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroyView();
            }
        });

        ((TextView)mRootView.findViewById(R.id.booking_id)).setText("Order #"+mBooking.getOrderId());

        ((TextView)mRootView.findViewById(R.id.brand)).setText(mBooking.getBrand());
        ((TextView)mRootView.findViewById(R.id.model)).setText(mBooking.getModel());
        ((TextView)mRootView.findViewById(R.id.plate_no)).setText(mBooking.getLicencePlateNo());

        String location = new LoginSessionManager(getActivity()).getServiceDetailFromSF().get(S_LOCATION);
        ((TextView)mRootView.findViewById(R.id.location)).setText(location);

        mRootView.findViewById(R.id.share_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareReceipt shareReceipt = new ShareReceipt(getActivity(),mBooking,1);
                shareReceipt.share();
            }
        });

        ((TextView)mRootView.findViewById(R.id.park_in_date)).setText(getFormattedDate(TAG,mBooking.getParkInTime()));
        ((TextView)mRootView.findViewById(R.id.park_in_time)).setText(getFormattedTime(TAG,mBooking.getParkInTime()));
        ((TextView)mRootView.findViewById(R.id.park_out_date)).setText(getFormattedDate(TAG,mBooking.getParkOutTime()));
        ((TextView)mRootView.findViewById(R.id.park_out_time)).setText(getFormattedTime(TAG,mBooking.getParkOutTime()));

        ((TextView)mRootView.findViewById(R.id.duration)).setText(mBooking.getDuration()+" Hrs");

        ((TextView)mRootView.findViewById(R.id.parking_fare)).setText("\u20B9 "+mBooking.getBaseFare());
        ((TextView)mRootView.findViewById(R.id.tax_amount)).setText("\u20B9 "+mBooking.getTax());
        ((TextView)mRootView.findViewById(R.id.additinal_charges)).setText("\u20B9 "+mBooking.getAddCharges());
        ((TextView)mRootView.findViewById(R.id.total_fare)).setText("\u20B9 "+mBooking.getTotalFare());

        mRootView.findViewById(R.id.print_receipt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReceiptPage();
            }
        });

        mRootView.findViewById(R.id.need_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = CUSTOMER_CARE;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });



    }

    private void openReceiptPage() {

        final Dialog dialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_receipt_parking_complete, null);

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

        setViewInReceipt(view);

        view.findViewById(R.id.print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareReceipt shareReceipt = new ShareReceipt(getActivity(),mBooking,1);
                shareReceipt.share();

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


    private void setViewInReceipt(View view) {

        HashMap<String, String> serviceDetail = new LoginSessionManager(getActivity()).getServiceDetailFromSF();

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


        tv_fare.setText("Rs "+mBooking.getFarePerHr()+"/hr");
        String partnerId = new LoginSessionManager(getActivity()).getEmpDetailsFromSP().get(KEY_PARTNER_ID);
        tv_operator_id.setText(partnerId);

        tv_parking_name.setText(serviceDetail.get(S_DESCRIPTION));
        tv_location.setText(serviceDetail.get(S_LOCATION));
        tv_date.setText(getFormattedDate(TAG, mBooking.getParkInTime()));
        tv_time.setText(getFormattedTime(TAG,mBooking.getBookingTime()));
        tv_number_plate.setText(mBooking.getLicencePlateNo());
        tv_brand.setText(mBooking.getBrand());
        tv_model.setText(mBooking.getModel());

        ((TextView)view.findViewById(R.id.park_in_time)).setText(getFormattedTime(TAG,mBooking.getParkInTime()));
        ((TextView)view.findViewById(R.id.park_out_time)).setText(getFormattedTime(TAG,mBooking.getParkOutTime()));
        ((TextView)view.findViewById(R.id.duration)).setText(mBooking.getDuration()+" Hrs");
        ((TextView)view.findViewById(R.id.parking_fare)).setText("\u20B9 "+mBooking.getBaseFare());
        ((TextView)view.findViewById(R.id.tax_amount)).setText("\u20B9 "+mBooking.getTax());
        ((TextView)view.findViewById(R.id.additinal_charges)).setText("\u20B9 "+mBooking.getAddCharges());
        ((TextView)view.findViewById(R.id.total_fare)).setText("\u20B9 "+mBooking.getTotalFare());

        String text = "<font color=#5d636b>Parking at owners risk. No responsibility for valuable items like laptop, wallet, cash etc.</font>" +
                    "<b><font color=#000000>Lost ticket charges RS 20 </font></b><><font color=#5d636b>after verification.</font>";

        tv_message.setText(Html.fromHtml(text));



    }









}
