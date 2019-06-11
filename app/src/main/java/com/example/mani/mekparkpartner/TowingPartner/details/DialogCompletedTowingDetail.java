package com.example.mani.mekparkpartner.TowingPartner.details;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mani.mekparkpartner.R;
import com.example.mani.mekparkpartner.TowingPartner.Model.TowingBooking;

import java.util.Objects;

public class DialogCompletedTowingDetail extends DialogFragment {

    private String TAG = "CompletedTowingDialog";
    private View mRootView;

    private TowingBooking mTowingBooking;

    public DialogCompletedTowingDetail() {}


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
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         mTowingBooking = (TowingBooking) getArguments().getSerializable("completed_towing_booking");
         Log.e(TAG,"orderId = "+mTowingBooking.getBookingId());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e(TAG, "called : onCreateView");
        mRootView = inflater.inflate(R.layout.dialog_completed_towing_booking_details, container, false);
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

        ((TextView)mRootView.findViewById(R.id.booking_id)).setText("Order #"+mTowingBooking.getBookingId());

        ((TextView)mRootView.findViewById(R.id.model)).setText(mTowingBooking.getModel());
        ((TextView)mRootView.findViewById(R.id.plate_no)).setText(mTowingBooking.getLicencePlateNo());

        ((TextView)mRootView.findViewById(R.id.cus_name)).setText(mTowingBooking.getCusName());

        ;



    }
}
