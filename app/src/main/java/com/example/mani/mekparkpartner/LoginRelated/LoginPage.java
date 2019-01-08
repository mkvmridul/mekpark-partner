package com.example.mani.mekparkpartner.LoginRelated;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.example.mani.mekparkpartner.FCMPackage.SharedPrefFcm;
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

public class LoginPage extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Context mContext = LoginPage.this;
    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mProgressDialog = new ProgressDialog(LoginPage.this);
        mProgressDialog.setMessage("Login...");
        clickListener();
    }

    private void clickListener() {

        Log.i(TAG,"called : clickListener");

        final EditText et_empId    = findViewById(R.id.emp_id);
        final EditText et_password = findViewById(R.id.password);

        TextView tv_submit = findViewById(R.id.submit);

        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String empId = et_empId.getText().toString().trim();
                String pass  = et_password.getText().toString().trim();

                if(empId.equals("") || pass.equals("")){
                    Toast.makeText(LoginPage.this,"Enter both field",Toast.LENGTH_SHORT).show();
                    et_password.setText("");
                    return;
                }

                login(empId,pass);

            }
        });

    }

    private void login(final String empId, final String pass) {
        Log.i(TAG,"called : checkForLogin");

        mProgressDialog.show();
        String SEND_URL = BASE_URL + "partner_login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG,response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");
                    if(rc<=0){
                        String mess = jsonArray.getJSONObject(0).getString("mess");
                        mProgressDialog.dismiss();
                        Toast.makeText(mContext,mess,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject jsonObject = jsonArray.getJSONObject(1);

                    String name   = jsonObject.getString("name");
                    String mobile = jsonObject.getString("mobile");
                    String email  = jsonObject.getString("email");

                    LoginSessionManager session = new LoginSessionManager(LoginPage.this);
                    session.createLoginSession(empId,pass,name,mobile,email);

                    startActivity(new Intent(mContext, HomePage.class));
                    mProgressDialog.dismiss();
                    Toast.makeText(mContext,"Welcome "+name,Toast.LENGTH_SHORT).show();
                    finish();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
                mProgressDialog.dismiss();
                Toast.makeText(LoginPage.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("emp_id",empId);
                params.put("pass",pass);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(LoginPage.this).addToRequestQueue(stringRequest);

    }
}
