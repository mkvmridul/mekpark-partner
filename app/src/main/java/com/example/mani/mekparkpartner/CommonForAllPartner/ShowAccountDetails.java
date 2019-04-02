package com.example.mani.mekparkpartner.CommonForAllPartner;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.CANCLE_CHEQUE_IMAGE_PATH;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.LICENCE_IMAGE_PATH;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.PAN_IMAGE_PATH;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;

public class ShowAccountDetails extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private LoginSessionManager mLoginSession;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_account_details);

        mLoginSession = new LoginSessionManager(ShowAccountDetails.this);
        mProgressDialog = new ProgressDialog(ShowAccountDetails.this);
        mProgressDialog.setMessage("Please wait....");
        mProgressDialog.setCancelable(false);

        clickListener();

        fetchBankAccountDetailsAndSetupPage();



    }

    private void clickListener() {

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void fetchBankAccountDetailsAndSetupPage() {

        mProgressDialog.show();

        String SEND_URL = BASE_URL + "get_partner_bank_account_details.php";
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
                        Log.e(TAG,mess);
                        Toast.makeText(ShowAccountDetails.this,mess,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject jsonObject = jsonArray.getJSONObject(1);

                    String panNumber        = jsonObject.getString("pan_number");
                    String accountNumber    = jsonObject.getString("account_number");
                    String ifseCode         = jsonObject.getString("ifse_code");
                    String cancelChequeNo   = jsonObject.getString("cancel_cheque_no");

                    String panImage         = jsonObject.getString("pan_image");
                    String chequeImage      = jsonObject.getString("cheque_image");

                    setLayout(panNumber,accountNumber,ifseCode,cancelChequeNo,panImage,chequeImage);





                } catch (JSONException e) {

                    e.printStackTrace();
                    Log.e(TAG,e.toString());
                    Toast.makeText(ShowAccountDetails.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.e(TAG,error.toString());
                Toast.makeText(ShowAccountDetails.this,error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                String empId = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);
                params.put("partner_id",empId);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void setLayout(String panNumber, String accountNumber, String ifseCode, final String cancelChequeNo,
                           final String panImage, final String chequeImage) {

        TextView tv_pan  =  findViewById(R.id.pan_number);
        TextView tv_acc  = findViewById(R.id.account_number);
        TextView tv_chec =  findViewById(R.id.cancelled_check);
        TextView tv_ifse = findViewById(R.id.ifse_code);

        ImageView iv_pan    = findViewById(R.id.pan_imageview);
        ImageView iv_cheque = findViewById(R.id.cancelled_check_imageview);

        tv_pan.setText(panNumber);
        tv_acc.setText(accountNumber);
        tv_chec.setText(ifseCode);
        tv_ifse.setText(cancelChequeNo);


        if(!panImage.equals("")){
            Glide.with(ShowAccountDetails.this).load(PAN_IMAGE_PATH+panImage)
                    .into(iv_pan);
        }

        if(!chequeImage.equals("")){
            Glide.with(ShowAccountDetails.this).load(CANCLE_CHEQUE_IMAGE_PATH+chequeImage)
                    .into(iv_cheque);
        }

        iv_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImageDialog(PAN_IMAGE_PATH+panImage);
            }
        });

        iv_cheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImageDialog(CANCLE_CHEQUE_IMAGE_PATH+chequeImage);
            }
        });




    }

    private void setImageDialog(String imagePath){

        AlertDialog.Builder dialog = new AlertDialog.Builder(ShowAccountDetails.this);
        View view = LayoutInflater.from(ShowAccountDetails.this).inflate(R.layout.dialog_image_view
                , null);
        dialog.setView(view);

        ImageView imageView        = view.findViewById(R.id.imageView);
        final ProgressBar imageProgressBar = view.findViewById(R.id.image_progress_bar);

        Glide.with(ShowAccountDetails.this)
                .load(imagePath)
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);

        dialog.show().getWindow().getAttributes().windowAnimations = R.style.dialogImageAnimation;
        dialog.create();



    }
}



