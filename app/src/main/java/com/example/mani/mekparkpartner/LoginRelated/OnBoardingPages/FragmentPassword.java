package com.example.mani.mekparkpartner.LoginRelated.OnBoardingPages;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.HomePage;
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
public class FragmentPassword extends Fragment {

    private final String TAG = "FragmentPassword";
    private View mRootView;
    private ProgressDialog mProgressDialog;

    public FragmentPassword() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_password, container, false);

        String phone = getArguments().getString("phone");
        Log.e(TAG,phone);

        TextView tv_phone = mRootView.findViewById(R.id.phone_no);
        tv_phone.setText(phone);

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

        mRootView.findViewById(R.id.login_to_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_pass = mRootView.findViewById(R.id.password);
                String pass = tv_pass.getText().toString().trim();

                if(pass.length()<6){
                    Toast.makeText(getActivity(),"Password must be atleast 6 digit long",Toast.LENGTH_SHORT).show();
                    return;
                }

                TextView tv_phone = mRootView.findViewById(R.id.phone_no);
                String phone = tv_phone.getText().toString().trim();
                loginToApp(phone,pass);
            }
        });



    }

    private void loginToApp(final String phone, final String pass) {

        Log.e(TAG,"called : checkToLogin");

        mProgressDialog.show();
        String SEND_URL = BASE_URL + "partner_login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG,response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            int rc = jsonArray.getJSONObject(0).getInt("rc");
                            if(rc<=0){
                                String mess = jsonArray.getJSONObject(0).getString("mess");
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(),mess,Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONObject jsonObject = jsonArray.getJSONObject(1);

                            String partnerId   = jsonObject.getString("partnerId");
                            String partnerType = jsonObject.getString("partnerType");

                            String name        = jsonObject.getString("name");
                            String email       = jsonObject.getString("email");

                            LoginSessionManager session = new LoginSessionManager(getActivity());
                            session.createLoginSession(partnerId,partnerType,name,phone,pass,email);

                            startActivity(new Intent(getActivity(), HomePage.class));
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(),"Welcome "+name,Toast.LENGTH_SHORT).show();
                            getActivity().finish();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("phone",phone);
                params.put("pass",pass);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);



    }

}
