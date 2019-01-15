package com.example.mani.mekparkpartner.LoginRelated.OnBoardingPages;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;

public class FragmentPhone extends Fragment {

    private final String TAG = "FragmentPhone";
    private View mRootView;
    private ProgressDialog mProgressDialog;


    public FragmentPhone() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_phone, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);

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


        final TextView tv_pn = mRootView.findViewById(R.id.phone_no);

        mRootView.findViewById(R.id.proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = tv_pn.getText().toString();

                if(phone.length()<10){
                    Toast.makeText(getActivity(),"Enter 10-digit mobile number",Toast.LENGTH_SHORT).show();
                    return;
                }
                isPartnerRegistered(phone);

            }
        });

    }

    private void isPartnerRegistered(final String phone) {

        Log.e(TAG,"isPartnerRegistered : called");
        String SEND_URL = BASE_URL + "isPartnerResgistered.php";
        mProgressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG,"isPhoneRegistered : response"+response );

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int responseCode = jsonObject.getInt("response_code");
                    String message   = jsonObject.getString("message");
                    Log.e(TAG,message);

                    if(responseCode == 1)
                        replaceFragment(new FragmentPassword(),phone);
                    else
                        replaceFragment(new FragmentSignUp(),phone);

                    mProgressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.e(TAG,"isPhoneRegistered : onErrorResponse :" +error.toString());
                Toast.makeText(getActivity(),"Something went wrong",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phone_number",phone);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS*1000),
                NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

    private void replaceFragment(Fragment fragment,String phone){

        Bundle bundle = new Bundle();
        bundle.putString("phone",phone);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

    }
}
