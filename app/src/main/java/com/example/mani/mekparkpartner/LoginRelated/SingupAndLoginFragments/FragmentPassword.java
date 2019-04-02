package com.example.mani.mekparkpartner.LoginRelated.SingupAndLoginFragments;


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

import com.example.mani.mekparkpartner.CommonForAllPartner.PartnerNotVerifiedPage;
import com.example.mani.mekparkpartner.CommonForAllPartner.InitialProfilePage;
import com.example.mani.mekparkpartner.LoginRelated.OnBoardingPages.SplashScreen;
import com.example.mani.mekparkpartner.ParkingPartner.ParkingPartnerHomePage;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.FREE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.GARAGE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.PAID_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.launchPartnerAcitvity;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_TYPE;


public class FragmentPassword extends Fragment {

    private final String TAG = "FragmentPassword";
    private View mRootView;
    private ProgressDialog mProgressDialog;

    private LoginSessionManager mLoginSession;

    public FragmentPassword() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_password, container, false);

        String phone = getArguments().getString("phone");
        Log.e(TAG,phone);

        TextView tv_phone = mRootView.findViewById(R.id.phone_no);
        tv_phone.setText(phone);

        mLoginSession = new LoginSessionManager(getActivity());

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

                if(pass.length()==0){
                    Toast.makeText(getActivity(),"Enter password",Toast.LENGTH_SHORT).show();
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

                                TextView tv_pass = mRootView.findViewById(R.id.password);
                                tv_pass.setText("");

                                return;
                            }

                            JSONObject jsonObject = jsonArray.getJSONObject(1);

                            String partnerId   = jsonObject.getString("partnerId");
                            String partnerType = jsonObject.getString("partnerType");
                            String name        = jsonObject.getString("name");
                            String email       = jsonObject.getString("email");

                            int isAccountVerified         = Integer.parseInt(jsonObject.getString("is_account_verified"));
                            int isServiceMangementFilled  = Integer.parseInt(jsonObject.getString("is_service_mangement_filled"));
                            int isPartnerVerified         = Integer.parseInt(jsonObject.getString("is_partner_verified"));

                            String licenceNumber = jsonObject.getString("licence_number");
                            String licenceImage  = jsonObject.getString("licence_image");
                            String executive_id  = jsonObject.getString("executive_id");


                            if(isAccountVerified == 1)
                                mLoginSession.setAccountDetailsFilled();

                            if(isServiceMangementFilled == 1)
                                    mLoginSession.setServiceManFilled();

                            if(isPartnerVerified == 1)
                                mLoginSession.setPartnerActivated();


                            mLoginSession.createLoginSession(partnerId,partnerType,name,phone,pass,email,licenceNumber,licenceImage,executive_id);

                            mProgressDialog.dismiss();

                            if(((partnerType.equals(PAID_PARKING_PROVIDER) || partnerType.equals(FREE_PARKING_PROVIDER) ||
                                    partnerType.equals(GARAGE_PARKING_PROVIDER))
                                    && ( mLoginSession.isServiceManagemantFilled())) ){

                                startActivity(new Intent(getActivity(),InitialProfilePage.class));
                                getActivity().finish();
                                return;
                            }


                            if( !(mLoginSession.isAccountDetailedFIlled())){
                                startActivity(new Intent(getActivity(),InitialProfilePage.class));
                                getActivity().finish();
                                return;

                            }

                            if(!mLoginSession.isPartnerActivated()){
                                startActivity(new Intent(getActivity(),PartnerNotVerifiedPage.class));
                                getActivity().finish();
                                return;
                            }

                            launchPartnerAcitvity(getActivity(),mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE));

                            Toast.makeText(getActivity(),"Welcome "+name,Toast.LENGTH_SHORT).show();
                            getActivity().finish();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                            Log.e(TAG,e.toString());
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
