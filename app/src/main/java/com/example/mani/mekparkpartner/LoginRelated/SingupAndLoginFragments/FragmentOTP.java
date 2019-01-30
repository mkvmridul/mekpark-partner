package com.example.mani.mekparkpartner.LoginRelated.SingupAndLoginFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import com.example.mani.mekparkpartner.CommonForAllPartner.InitialProfilePage;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOTP extends Fragment {

    private final String TAG = "FragmentOTP";
    private View mRootView;

    EditText et1,et2,et3,et4,et5,et6;
    
    private String mPass;
    private int mPartnerType;
    private String mName;
    private String mEmail;
    private String mPhone;
    private String mOtp;

    public FragmentOTP() {}
    


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_otp, container, false);
        
        mPass        = getArguments().getString("pass");
        mPartnerType = getArguments().getInt("partnerType");
        mName        = getArguments().getString("name");
        mEmail       = getArguments().getString("email");
        mPhone       = getArguments().getString("phone");
        mOtp         = getArguments().getString("otp");

        TextView tv_mobile = mRootView.findViewById(R.id.mobile);
        tv_mobile.setText(mPhone);
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

        et1 = mRootView.findViewById(R.id.et1);
        et2 = mRootView.findViewById(R.id.et2);
        et3 = mRootView.findViewById(R.id.et3);
        et4 = mRootView.findViewById(R.id.et4);
        et5 = mRootView.findViewById(R.id.et5);
        et6 = mRootView.findViewById(R.id.et6);

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
            }
        });

        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1)
                    et5.requestFocus();

                else if(s.length()==0)
                    et3.requestFocus();


            }
        });

        et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==1)
                    et6.requestFocus();
                else if(s.length()==0)
                    et4.requestFocus();
            }
        });

        et6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0)
                    et5.requestFocus();
            }
        });

        TextView ok = mRootView.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputedOtp = et1.getText().toString().trim()+
                        et2.getText().toString().trim()+
                        et3.getText().toString().trim()+
                        et4.getText().toString().trim()+
                        et5.getText().toString().trim()+
                        et6.getText().toString().trim();


                if(inputedOtp.equals("") || inputedOtp.length()<6){
                    Toast.makeText(getActivity(),"Enter valid otp",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!inputedOtp.equals(mOtp)){
                    Toast.makeText(getActivity(),"Wrong OTP",Toast.LENGTH_SHORT).show();
                    return;
                }

                partnerSingUp();
            }
        });

    }

    private void partnerSingUp() {

        Log.e(TAG,"called : partnerSingUp");
        Log.e(TAG,mName+" "+mPartnerType+" "+mEmail+" "+mPhone+" "+mPass);

        final String URL = BASE_URL+"partner_signup.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e(TAG,"partnerSignup : "+response );

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            int rc = jsonObject.getInt("response_code");
                            if(rc<=0){
                                String mess = jsonObject.getString("message");
                                Toast.makeText(getActivity(),mess,Toast.LENGTH_SHORT).show();
                                Log.e(TAG,mess);
                                return;
                            }

                            int partnerId = jsonObject.getInt("user_id");
                            LoginSessionManager session = new LoginSessionManager(getActivity());

                            session.createLoginSession(String.valueOf(partnerId), String.valueOf(mPartnerType),
                                    mName,mPhone,mPass,mEmail,"","","");

                            startActivity(new Intent(getActivity(), InitialProfilePage.class));
                            Toast.makeText(getActivity(),"Registered Successfully",Toast.LENGTH_LONG).show();
                            getActivity().finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG,e.toString());
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"userSignup : onErrorResponse :" +error.toString());
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("phone_number",mPhone);
                params.put("email",mEmail);
                params.put("name",mName);
                params.put("password",mPass);
                params.put("partner_type", String.valueOf(mPartnerType));
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS*1000),
                NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

}
