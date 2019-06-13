package com.example.mani.mekparkpartner.CommonForAllPartner;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.LOCATION_NOT_FOUND;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.OPEN_24_HRS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_BIKE_CAPACITY;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_BIKE_FARE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_BIKE_VACANCY;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_CAR_CAPACITY;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_CAR_FARE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_CAR_VACANCY;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_OPENING_HRS;

public class ShowParkingDetail extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private LoginSessionManager mSession;
    HashMap<String, String> mServiceInfo;

    TextView t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12;
    LinearLayout ll_from,ll_to;
    TextView textFrom, textTo;
    ImageView c1,c2;
    TextView am,pm;

    EditText et_address;

    EditText et_bikeFare,et_bikeCap,et_bikeVacancy;
    EditText et_carFare,et_carCap,et_carVacancy;

    Switch timeSwitch;
    TextView tv_ohr1;

    LinearLayout ll_ohr2;
    TextView openHr2From,openHr2To;
    TextView openHr2FromAP,openHr2ToAP;

    // timings
    String mOpeningHrs;
    String from="", fromAp="", to="", toAp="";

    // flag = 1(from selected) flag = 2(to selected)
    int flag = 1;

    boolean isChangesMade = false;

    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_parking_detail);

        mSession     = new LoginSessionManager(ShowParkingDetail.this);
        mServiceInfo = mSession.getServiceDetailFromSF();

        mProgressDialog = new ProgressDialog(ShowParkingDetail.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);

        mOpeningHrs = mServiceInfo.get(S_OPENING_HRS);
        Log.e(TAG, "mHrs = "+mOpeningHrs);

        //mOpeningHrs = OPEN_24_HRS;


        setLayout();
        clickListener();

    }


    private void setLayout() {

        t2 = findViewById(R.id.t2);
        t1 = findViewById(R.id.t1);
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);

        t5 = findViewById(R.id.t5);
        t6 = findViewById(R.id.t6);
        t7 = findViewById(R.id.t7);
        t8 = findViewById(R.id.t8);

        t9  = findViewById(R.id.t9);
        t10 = findViewById(R.id.t10);
        t11 = findViewById(R.id.t11);
        t12 = findViewById(R.id.t12);

        ll_from = findViewById(R.id.from);
        textFrom = findViewById(R.id.text_from);
        c1 = findViewById(R.id.c1);

        ll_to  = findViewById(R.id.to);
        textTo = findViewById(R.id.text_to);
        c2     = findViewById(R.id.c2);

        am  = findViewById(R.id.am);
        pm  = findViewById(R.id.pm);

        et_address = findViewById(R.id.address);

        et_bikeFare    = findViewById(R.id.bike_fare);
        et_bikeCap     = findViewById(R.id.bike_capacity);
        et_bikeVacancy = findViewById(R.id.bike_vacancy);

        et_carFare     = findViewById(R.id.car_fare);
        et_carCap      = findViewById(R.id.car_capacity);
        et_carVacancy  = findViewById(R.id.car_vacancy);



        final LinearLayout ll_time_layout = findViewById(R.id.time_layout);

        tv_ohr1 = findViewById(R.id.opening_hrs1);
        ll_ohr2 = findViewById(R.id.opening_hrs2);

        openHr2From   = findViewById(R.id.opening_hrs2_from);
        openHr2To     = findViewById(R.id.opening_hrs2_to);
        openHr2FromAP = findViewById(R.id.opening_hrs2_from_ap);
        openHr2ToAP   = findViewById(R.id.opening_hrs2_to_ap);

        timeSwitch = findViewById(R.id.time_switch);

        timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    ll_time_layout.setVisibility(View.GONE);
                    tv_ohr1.setVisibility(View.VISIBLE);
                    ll_ohr2.setVisibility(View.GONE);

                    Log.e(TAG,"1");
                    changesMade();

                    mOpeningHrs = OPEN_24_HRS;
                }
                else {
                    ll_time_layout.setVisibility(View.VISIBLE);
                    tv_ohr1.setVisibility(View.GONE);
                    ll_ohr2.setVisibility(View.VISIBLE);

                    Log.e(TAG,"2");
                    changesMade();



                }

            }
        });


        if(mOpeningHrs.equals(OPEN_24_HRS))
        {
            tv_ohr1.setVisibility(View.VISIBLE);
            ll_ohr2.setVisibility(View.GONE);

            timeSwitch.setChecked(true);


            ll_time_layout.setVisibility(View.GONE);

        }

        else
            {
                tv_ohr1.setVisibility(View.GONE);
                ll_ohr2.setVisibility(View.VISIBLE);

                timeSwitch.setChecked(false);

                ll_time_layout.setVisibility(View.VISIBLE);

                //Opening hrs syntax 1:00AM To 5:00PM
                //Opening hrs syntax 11:00AM To 11:00PM
                //Opening hrs syntax 6:00AM To 11:00PM
                //Opening hrs syntax 10:00AM To 1:00PM

                String s[] = mOpeningHrs.split("To");
                s[0] = s[0].trim();
                s[1] = s[1].trim();

                if(s[0].length() == 6){
                    from = s[0].substring(0,4);
                    fromAp = s[0].substring(4,6);
                }

                else {
                    from = s[0].substring(0,5);
                    fromAp = s[0].substring(5,7);
                }

                if(s[1].length() == 6){
                    to = s[1].substring(0,4);
                    toAp = s[1].substring(4,6);
                }

                else {
                    to = s[1].substring(0,5);
                    toAp = s[1].substring(5,7);
                }

                openHr2From.setText(from);
                openHr2FromAP.setText(fromAp);
                openHr2To.setText(to);
                openHr2ToAP.setText(toAp);



                Log.e(TAG,from+fromAp+" "+to+toAp);

            }


        et_bikeFare.setText(mServiceInfo.get(S_BIKE_FARE));
        et_bikeCap.setText(mServiceInfo.get(S_BIKE_CAPACITY));
        et_bikeVacancy.setText(mServiceInfo.get(S_BIKE_VACANCY));

        et_carFare.setText(mServiceInfo.get(S_CAR_FARE));
        et_carCap.setText(mServiceInfo.get(S_CAR_CAPACITY));
        et_carVacancy.setText(mServiceInfo.get(S_CAR_VACANCY));

        et_bikeFare.setSelection(et_bikeFare.getText().length());
        et_bikeCap.setSelection(et_bikeCap.getText().length());
        et_bikeVacancy.setSelection(et_bikeVacancy.getText().length());
        et_carFare.setSelection(et_carFare.getText().length());
        et_carCap.setSelection(et_carCap.getText().length());
        et_carVacancy.setSelection(et_carVacancy.getText().length());

    }

    private void clickListener() {

        setFromToLayout((LinearLayout) findViewById(R.id.from),c1,textFrom);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ll_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFromToLayout((LinearLayout) v,c1,textFrom);
            }
        });

        ll_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFromToLayout((LinearLayout) v,c2,textTo);
            }
        });

        am.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAmPmLayoutToMakeChanges((TextView)v);
            }
        });

        pm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAmPmLayoutToMakeChanges((TextView)v);
            }
        });

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });
        t12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeToMakeChanges((TextView)v);
            }
        });



        et_bikeFare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG,"3");
                changesMade();

            }
        });
        et_bikeCap.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG,"3");
                changesMade();

            }
        });
        et_bikeVacancy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG,"4");
                changesMade();

            }
        });

        et_carFare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG,"5");
                changesMade();
            }
        });
        et_carCap.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG,"6");
                changesMade();
            }
        });
        et_carVacancy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG,"7");
                changesMade();
            }
        });





        findViewById(R.id.make_changes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(timeSwitch.isChecked()){
                    mOpeningHrs = tv_ohr1.getText().toString().trim();
                }

                else{
                    mOpeningHrs =  openHr2From.getText().toString() +openHr2FromAP.getText().toString()+
                            " To "+openHr2To.getText().toString()+openHr2ToAP.getText().toString();
                }

                Log.e(TAG,"OpeningHrs "+mOpeningHrs);

                String bikeFare     = et_bikeFare.getText().toString();
                String bikeCapacity = et_bikeCap.getText().toString();
                String bikeVacancy  = et_bikeVacancy.getText().toString();

                String carFare      = et_carFare.getText().toString().trim();
                String carCapacity  = et_carCap.getText().toString().trim();
                String carVacancy   = et_carVacancy.getText().toString().trim();


                if(bikeFare.equals("") || bikeCapacity.equals("") || bikeVacancy.equals("") ||
                        carFare.equals("") || bikeCapacity.equals("") || carVacancy.equals("")){
                    Toast.makeText(ShowParkingDetail.this,"Fill the required filled",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(bikeCapacity.equals("0") || bikeVacancy.equals("0") ||
                        carCapacity.equals("0") || carVacancy.equals("0") ){
                    Toast.makeText(ShowParkingDetail.this,"Capcity or Vacancy can't be zero",Toast.LENGTH_SHORT).show();
                    return;

                }

                updateParkingSlotDetails(bikeFare,bikeCapacity,bikeVacancy,carFare,carCapacity,carVacancy);
            }
        });



    }

    private void setFromToLayout(LinearLayout ll, ImageView iv, TextView tv) {

        resetFromToLayout();
        resetAmPmLayout();
        resetTimings();

        if(ll == findViewById(R.id.from)){
            flag = 1;
        }
        else{
            flag = 2;
        }

        setAmPmLayout();
        setTime();


        ll.setBackground(getApplication().getResources().getDrawable(R.drawable.red_background));
        iv.setImageDrawable(getApplication().getDrawable(R.drawable.time_white));

        tv.setTextColor(ShowParkingDetail.this.getColor(R.color.white));

    }

    private void setAmPmLayout() {

        resetAmPmLayout();
        TextView v;

        if(flag == 1){

            if(mOpeningHrs.equals(OPEN_24_HRS))
                v = findViewById(R.id.am);

            else if(fromAp.equals("AM"))
                v = findViewById(R.id.am);
            else
                v = findViewById(R.id.pm);
        }

        else {

            if(mOpeningHrs.equals(OPEN_24_HRS))
                v = findViewById(R.id.pm);

            else if(toAp.equals("AM"))
                v = findViewById(R.id.am);
            else
                v = findViewById(R.id.pm);
        }




        v.setBackground(getApplication().getResources().getDrawable(R.drawable.red_rounded_background));
        v.setTextColor(getApplication().getColor(R.color.white));
    }
    private void setTime() {

        Log.e(TAG,"from-"+from+" To-"+to);

        resetTimings();
        TextView v;

        if(flag == 1) {

            if(from.equals("1:00"))
                v = t1;
            else if(from.equals("2:00"))
                v =t2;
            else if(from.equals("3:00"))
                v =t3;
            else if(from.equals("4:00"))
                v =t4;
            else if(from.equals("5:00"))
                v =t5;
            else if(from.equals("6:00"))
                v =t6;
            else if(from.equals("7:00"))
                v =t7;
            else if(from.equals("8:00"))
                v =t8;
            else if(from.equals("9:00"))
                v =t9;
            else if(from.equals("10:00"))
                v =t10;
            else if(from.equals("11:00"))
                v = t11;
            else
                v = t12;

        }
        else {

            if(to.equals("1:00"))
                v = t1;
            else if(to.equals("2:00"))
                v =t2;
            else if(to.equals("3:00"))
                v =t3;
            else if(to.equals("4:00"))
                v =t4;
            else if(to.equals("5:00"))
                v =t5;
            else if(to.equals("6:00"))
                v =t6;
            else if(to.equals("7:00"))
                v =t7;
            else if(to.equals("8:00"))
                v =t8;
            else if(to.equals("9:00"))
                v =t9;
            else if(to.equals("10:00"))
                v =t10;
            else if(to.equals("11:00"))
                v =t11;
            else
                v = t12;

        }

        v.setBackground(getApplication().getDrawable(R.drawable.time_background));
        v.setTextColor(getApplication().getResources().getColor(R.color.white));

    }


    private void setAmPmLayoutToMakeChanges(TextView v) {

        resetAmPmLayout();

        Log.e(TAG,"8");
        changesMade();

        if(flag == 1)
            openHr2FromAP.setText(v.getText().toString());
        else
            openHr2ToAP.setText(v.getText().toString());

        v.setBackground(getApplication().getResources().getDrawable(R.drawable.red_rounded_background));
        v.setTextColor(getApplication().getColor(R.color.white));
    }

    private void selectTimeToMakeChanges(TextView v) {

        resetTimings();
        Log.e(TAG,"9");
        changesMade();

        if(flag ==1)
            openHr2From.setText(v.getText().toString());
        else
            openHr2To.setText(v.getText().toString());

        v.setBackground(getApplication().getDrawable(R.drawable.time_background));
        v.setTextColor(getApplication().getResources().getColor(R.color.white));

    }


    private void resetFromToLayout() {

        ll_from.setBackground(getApplication().getResources().getDrawable(R.drawable.border_light_black));
        c1.setImageDrawable(getApplication().getDrawable(R.drawable.time_4));
        textFrom.setTextColor(ShowParkingDetail.this.getColor(R.color.white_2));

        ll_to.setBackground(getApplication().getResources().getDrawable(R.drawable.border_light_black));
        c2.setImageDrawable(getApplication().getDrawable(R.drawable.time_4));
        textTo.setTextColor(ShowParkingDetail.this.getColor(R.color.white_2));

    }
    private void resetTimings(){

        t1.setBackgroundColor(getResources().getColor(R.color.white));
        t1.setTextColor(getApplication().getResources().getColor(R.color.black));

        t2.setBackgroundColor(getApplication().getColor(R.color.white));
        t2.setTextColor(getApplication().getResources().getColor(R.color.black));

        t3.setBackgroundColor(getApplication().getColor(R.color.white));
        t3.setTextColor(getApplication().getResources().getColor(R.color.black));

        t4.setBackgroundColor(getApplication().getColor(R.color.white));
        t4.setTextColor(getApplication().getResources().getColor(R.color.black));

        t5.setBackgroundColor(getApplication().getColor(R.color.white));
        t5.setTextColor(getApplication().getResources().getColor(R.color.black));

        t6.setBackgroundColor(getApplication().getColor(R.color.white));
        t6.setTextColor(getApplication().getResources().getColor(R.color.black));

        t7.setBackgroundColor(getApplication().getColor(R.color.white));
        t7.setTextColor(getApplication().getResources().getColor(R.color.black));

        t8.setBackgroundColor(getApplication().getColor(R.color.white));
        t8.setTextColor(getApplication().getResources().getColor(R.color.black));

        t9.setBackgroundColor(getApplication().getColor(R.color.white));
        t9.setTextColor(getApplication().getResources().getColor(R.color.black));


        t10.setBackgroundColor(getApplication().getColor(R.color.white));
        t10.setTextColor(getApplication().getResources().getColor(R.color.black));


        t11.setBackgroundColor(getApplication().getColor(R.color.white));
        t11.setTextColor(getApplication().getResources().getColor(R.color.black));


        t12.setBackgroundColor(getApplication().getColor(R.color.white));
        t12.setTextColor(getApplication().getResources().getColor(R.color.black));
    }
    private void resetAmPmLayout() {
        am.setBackground(getApplication().getResources().getDrawable(R.drawable.white_rounded_background));
        am.setTextColor(getApplication().getColor(R.color.black));

        pm.setBackground(getApplication().getResources().getDrawable(R.drawable.white_rounded_background));
        pm.setTextColor(getApplication().getColor(R.color.black));
    }

    private void changesMade(){

        Log.e(TAG,"called : changes made");

        TextView btn = findViewById(R.id.make_changes);
        btn.setBackground(getApplication().getDrawable(R.drawable.background_button1));
        btn.setEnabled(true);


    }


    private void updateParkingSlotDetails(final String bikeFare, final String bikeCapacity, final String bikeVacancy, final String carFare,
                                          final String carCapacity, final String carVacancy) {

        String SEND_URL = BASE_URL + "update_parking_slot_detail.php";
        mProgressDialog.show();

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
                        Toast.makeText(ShowParkingDetail.this,mess,Toast.LENGTH_SHORT).show();
                        return;
                    }


                    mSession.updateServiceDeailsinSP(mOpeningHrs,bikeCapacity,carCapacity,bikeVacancy,carVacancy,bikeFare,carFare);

                    Toast.makeText(ShowParkingDetail.this,"Update successfull",Toast.LENGTH_SHORT).show();
                    finish();





                } catch (JSONException e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.e(TAG,error.toString());
                Toast.makeText(ShowParkingDetail.this,error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                String empId = mSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);

                params.put("partner_id",empId);
                params.put("opening_hrs",mOpeningHrs);

                params.put("bike_capacity",bikeCapacity);
                params.put("car_capacity",carCapacity);
                params.put("fare_for_bike",bikeFare);
                params.put("fare_for_car",carFare);
                params.put("bike_vacancy",bikeVacancy);
                params.put("car_vacancy",carVacancy);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }


}
