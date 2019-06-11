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
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mani.mekparkpartner.R;
import com.example.mani.mekparkpartner.TowingPartner.Model.TowingBooking;

import java.util.Objects;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;

public class DialogUpcomingTowingDetail extends DialogFragment {

    private String TAG = "UpcomingTowingDialog";
    private View mRootView;

    private TowingBooking mTowingBooking;

    public DialogUpcomingTowingDetail() {}


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

         mTowingBooking = (TowingBooking) getArguments().getSerializable("new_upcoming_booking");
         Log.e(TAG,"orderId = "+mTowingBooking.getBookingId());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e(TAG, "called : onCreateView");
        mRootView = inflater.inflate(R.layout.dialog_upcoming_towing_booking_details, container, false);

        setViewAndClickListener();
        return mRootView;
    }

    private void setViewAndClickListener() {

        mRootView.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroyView();
            }
        });

        ((TextView)mRootView.findViewById(R.id.booking_id)).setText("Order #"+mTowingBooking.getBookingId());

        ((TextView)mRootView.findViewById(R.id.brand)).setText(mTowingBooking.getBrand());
        ((TextView)mRootView.findViewById(R.id.model)).setText(mTowingBooking.getModel());
        ((TextView)mRootView.findViewById(R.id.plate_no)).setText(mTowingBooking.getLicencePlateNo());

        ((TextView)mRootView.findViewById(R.id.cus_name)).setText(mTowingBooking.getCusName());
        mRootView.findViewById(R.id.call_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mTowingBooking.getCusPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                getActivity().startActivity(intent);

            }
        });


        ((TextView)mRootView.findViewById(R.id.address)).setText(mTowingBooking.getPickUpAddress());

        ((TextView)mRootView.findViewById(R.id.pickup_date)).setText(getFormattedDate(TAG, mTowingBooking.getPickupTime()));
        ((TextView)mRootView.findViewById(R.id.pickup_time)).setText(getFormattedTime(TAG, mTowingBooking.getPickupTime()));


        mRootView.findViewById(R.id.navigate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Upcoming - Navigate",Toast.LENGTH_SHORT).show();
            }
        });

        mRootView.findViewById(R.id.add_customer_support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Upcoming - Add Customer Support",Toast.LENGTH_SHORT).show();
            }
        });

        mRootView.findViewById(R.id.add_more_pics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Upcoming - Add More Pics",Toast.LENGTH_SHORT).show();
            }
        });


        final EditText et1,et2,et3,et4;

        et2 = mRootView.findViewById(R.id.et2);
        et3 = mRootView.findViewById(R.id.et3);
        et1 = mRootView.findViewById(R.id.et1);
        et4 = mRootView.findViewById(R.id.et4);

        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1)
                    et2.requestFocus();

                else if(s.length()==0)
                    et1.clearFocus();

                setConfirmButton();
            }
        });

        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1)
                    et3.requestFocus();

                else if(s.length()==0)
                    et1.requestFocus();

                setConfirmButton();


            }
        });

        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1)
                    et4.requestFocus();

                else if(s.length()==0)
                    et2.requestFocus();

                setConfirmButton();
            }
        });

        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1) {
                    et1.clearFocus();
                }

                else if(s.length()==0)
                    et3.requestFocus();
                
                setConfirmButton();
            }
        });

        mRootView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Upcoming - Confirming....",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setConfirmButton() {

        final EditText et1,et2,et3,et4;

        et2 = mRootView.findViewById(R.id.et2);
        et3 = mRootView.findViewById(R.id.et3);
        et1 = mRootView.findViewById(R.id.et1);
        et4 = mRootView.findViewById(R.id.et4);

        TextView tv_confirm = mRootView.findViewById(R.id.confirm);

        if(et1.getText().length() == 1 && et2.getText().length() == 1 &&
                et3.getText().length() == 1 && et4.getText().length() == 1) {
            tv_confirm.setEnabled(true);
            tv_confirm.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        else{
           tv_confirm.setEnabled(false);
            tv_confirm.setBackgroundColor(getResources().getColor(R.color.lignt_pink));
        }

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
}
