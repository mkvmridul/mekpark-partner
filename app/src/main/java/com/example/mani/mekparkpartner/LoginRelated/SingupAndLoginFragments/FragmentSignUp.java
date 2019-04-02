package com.example.mani.mekparkpartner.LoginRelated.SingupAndLoginFragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.LoginRelated.SingupAndLoginFragments.FragmentOTP;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;

public class FragmentSignUp extends Fragment {

    private final String TAG = "FragmentSignUp";
    private View mRootView;
    private ProgressDialog mProgressDialog;

    private List<String> mPartnerList;
    private Spinner mSpinnerPartnerType;
    private String mPhone;

    private String mPass;
    private int mPartnerType;
    private String mName;
    private String mEmail;
    private String mOtp;



    public FragmentSignUp() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mPhone = getArguments().getString("phone");
        Log.e(TAG,mPhone);

        TextView tv_phone = mRootView.findViewById(R.id.phone_no);
        tv_phone.setText(mPhone);

        mSpinnerPartnerType = mRootView.findViewById(R.id.partner_type);

        mPartnerList = new ArrayList<>();
        mPartnerList.add("Choose Partner Type");

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);

        fetchPartnerType();

        clickListener();
        return mRootView;
    }



    private void clickListener() {

        mRootView.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        final TextView tv_email    = mRootView.findViewById(R.id.email);
        final TextView tv_name     = mRootView.findViewById(R.id.name);
        final TextView tv_password = mRootView.findViewById(R.id.password);
        final TextView tv_signup   = mRootView.findViewById(R.id.btn_signup);

        CheckBox checkRef    = mRootView.findViewById(R.id.checkbox_referral);
        final EditText et_refaral  = mRootView.findViewById(R.id.referral_code);

        checkRef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    et_refaral.setVisibility(View.VISIBLE);
                else
                    et_refaral.setVisibility(View.GONE);
            }
        });

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEmail    = tv_email.getText().toString().trim();
                mName     = tv_name.getText().toString().trim();
                mPass     = tv_password.getText().toString().trim();

                mPartnerType = mSpinnerPartnerType.getSelectedItemPosition();

                if(mEmail.equals("") || mName.equals("") || mPass.equals("")){
                    Toast.makeText(getActivity(),"All fields are required",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mPass.length()<6){
                    Toast.makeText(getActivity(),"Password length must be atleast 6",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mPartnerType==0){
                    Toast.makeText(getActivity(),"Select partner type",Toast.LENGTH_SHORT).show();
                    return;
                }

                sendOTP();
            }
        });

    }
    public void fetchPartnerType() {

        Log.e(TAG,"called : fetchPartnerType");
        mProgressDialog.show();

        String SEND_URL = BASE_URL + "fetch_partner_type.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG,response);
                mProgressDialog.dismiss();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");
                    if(rc<=0){

                        String mess = jsonArray.getJSONObject(0).getString("mess");
                        Toast.makeText(getActivity(),mess,Toast.LENGTH_SHORT).show();
                        Log.e(TAG,mess);
                        return;
                    }

                    mPartnerList.clear();
                    mPartnerList.add("Choose Partner Type");

                    for(int i=1;i<jsonArray.length();i++){
                        String partnerType = jsonArray.getJSONObject(i).getString("partner_type");
                        mPartnerList.add(partnerType);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            R.layout.spinner_layout_custom,mPartnerList);
                    mSpinnerPartnerType.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();}


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.e(TAG,error.toString());
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void sendOTP() {

        Random random = new Random();
        mOtp = String.format("%06d", random.nextInt(1000000));

        Log.e(TAG,"Otp :" +mOtp);

        String mobile = "+91"+mPhone;
        String message = "From Mekpark Partner App : Your opt is "+mOtp;

        String URL = "http://api.msg91.com/api/sendhttp.php?country=91&sender=MSGIND&route=4" +
                "&mobiles="+mobile+"&authkey=192785A1sPOm0965a584ea5&message="+message;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,"sendOTP : onResponse "+response);
                        otpPage();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"sendOTP : onErrorResponse :" +error.toString());
                Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS*1000),
                NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

    private void otpPage(){

        Log.e(TAG," "+mPass+" "+mPartnerType+" "+mName+" "+mEmail);
        Log.e(TAG, "otpSent : "+mOtp);

        Fragment fragment = new FragmentOTP();
        Bundle bundle = new Bundle();

        bundle.putString("pass",mPass);
        bundle.putInt("partnerType", mPartnerType);
        bundle.putString("name",mName);
        bundle.putString("email",mEmail);
        bundle.putString("phone",mPhone);
        bundle.putString("otp",mOtp);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }


}
