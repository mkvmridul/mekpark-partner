package com.example.mani.mekparkpartner.OffileParkingPartner;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.example.mani.mekparkpartner.OffileParkingPartner.Model.KeyValueAdapter;
import com.example.mani.mekparkpartner.OffileParkingPartner.Model.KeyValueClass;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL_USER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.KEY_BIKE;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.KEY_CAR;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedDate;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.getFormattedTime;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_BIKE_FARE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_CAR_FARE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_DESCRIPTION;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_LOCATION;

public class AddNewBooking extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();


   // TextView myVehicle;
    LinearLayout llCar, llBike;
    ImageView imageCar, imageBike;
    TextView tv_car, tv_bike;
    EditText et_vehicleNo,et_ContactNumber;
    Spinner spinnerBrand,spinnerModel,spinnerTowingType;

    List<KeyValueClass> mBrandCustomeList;
    List<String> mModelsList;
    TextView mProceed;

    TextView tv_car_parking_fare;
    TextView tv_bike_parking_fare;


    String mVehicleType = KEY_CAR;
    private ProgressBar mProgressBar;

    HashMap<String,String> mServiceDetail;

    //Will serve as operator id as well
    String mOperatorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_booking);

        mProgressBar = findViewById(R.id.progress_bar);

        mBrandCustomeList = new ArrayList<>();
        mBrandCustomeList.add(new KeyValueClass(0,"Choose Brand"));

        mModelsList = new ArrayList<>();
        mModelsList.add("Choose Model");

        mOperatorId = new LoginSessionManager(AddNewBooking.this).getEmpDetailsFromSP().get(KEY_PARTNER_ID);

        mServiceDetail = new LoginSessionManager(AddNewBooking.this).getServiceDetailFromSF();

        setViews();
        clickListener();


        KeyValueAdapter adapter = new KeyValueAdapter(this,
                R.layout.spinner_layout_custom, mBrandCustomeList);
        spinnerBrand.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(AddNewBooking.this,
                R.layout.spinner_layout_custom,mModelsList);
        spinnerModel.setAdapter(adapter2);

        String [] towingTypeArray = new String[]{"Type","Type 1","Type 2","Type 3"};

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(AddNewBooking.this,
                R.layout.spinner_layout_custom2,towingTypeArray);
        adapter3.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerTowingType.setAdapter(adapter3);

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerModel.setSelection(0);

                int brandId = mBrandCustomeList.get(position).getKey();
                Log.e(TAG,"BrandId "+brandId);

                fetchModelsFromDatabase(brandId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fetchBrandsFromDatabase(mVehicleType);
    }

    private void setViews(){

        TextView tv_parking_name     = findViewById(R.id.parking_name);
        TextView tv_parking_location = findViewById(R.id.location);
        TextView tv_operator_id      = findViewById(R.id.operator_id);


        tv_car_parking_fare  = findViewById(R.id.car_parking_fare);
        tv_bike_parking_fare = findViewById(R.id.bike_parking_fare);

        llCar     = findViewById(R.id.ll_car);
        imageCar  = findViewById(R.id.image_car);
        tv_car    = findViewById(R.id.tv_car);

        llBike    = findViewById(R.id.ll_bike);
        imageBike = findViewById(R.id.image_bike);
        tv_bike   = findViewById(R.id.tv_bike);

        et_vehicleNo        = findViewById(R.id.vehicle_no);
        et_ContactNumber    = findViewById(R.id.contact_no);

        spinnerBrand        = findViewById(R.id.choose_brand);
        spinnerModel        = findViewById(R.id.choose_model);
        spinnerTowingType   = findViewById(R.id.towing_type);

        mProceed  = findViewById(R.id.proceed);



        imageCar.setImageResource(R.drawable.car_white);
        imageBike.setImageResource(R.drawable.bike_black);

        String carFare      = mServiceDetail.get(S_CAR_FARE);
        String bikeFare     = mServiceDetail.get(S_BIKE_FARE);
        String parkingName  = mServiceDetail.get(S_DESCRIPTION);
        String location     = mServiceDetail.get(S_LOCATION);


        tv_car_parking_fare.setText("[ "+carFare+"/ hr ]");
        tv_bike_parking_fare.setText("[ "+bikeFare+"/ hr ]");
        tv_parking_name.setText(mServiceDetail.get(S_DESCRIPTION));
        tv_parking_location.setText(mServiceDetail.get(S_LOCATION));
        tv_operator_id.setText(mOperatorId);

    }

    private void clickListener() {

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        llCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carSelected();
            }
        });

        llBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bikeSelected();
            }
        });

        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int brandId = spinnerBrand.getSelectedItemPosition();
                int modelId = spinnerModel.getSelectedItemPosition();
                int towId   = spinnerTowingType.getSelectedItemPosition();

                String vehicleNo    = et_vehicleNo.getText().toString().toUpperCase();
                String contactNo    = et_ContactNumber.getText().toString(); //Contact number is optional

                if(brandId == 0 || modelId == 0 || towId == 0 || vehicleNo.equals("")){
                    Toast.makeText(AddNewBooking.this,"Except contact and details, all fields are required",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(contactNo.length()>0 && contactNo.length()<10){
                    Toast.makeText(AddNewBooking.this,"Enter valid number",Toast.LENGTH_SHORT).show();
                    return;
                }

               // mProgressBar.setVisibility(View.VISIBLE);

                String brand        = mBrandCustomeList.get(brandId).getValue();
                String model        = (String) spinnerModel.getSelectedItem();
                String parkingType  = (String) spinnerTowingType.getSelectedItem();


                //Optional field
                TextView tv_des = findViewById(R.id.des);
                String des = tv_des.getText().toString().trim();

                Log.e(TAG,brand+" "+model+" "+parkingType+" "+vehicleNo+" "+contactNo+" "+mVehicleType+" des="+des);


                sendOfflineParkingDetailToDb(mVehicleType,brand,model,vehicleNo,contactNo,parkingType,des);

            }
        });

    }


    private void carSelected() {

        mVehicleType = KEY_CAR;

        spinnerBrand.setSelection(0);
        spinnerModel.setSelection(0);
        fetchBrandsFromDatabase(mVehicleType);

        llCar.setBackground(ContextCompat.getDrawable(AddNewBooking.this,R.drawable.background_button1));
        imageCar.setImageResource(R.drawable.car_white);
        tv_car.setTextColor(ContextCompat.getColor(AddNewBooking.this,R.color.white));
        tv_car_parking_fare.setTextColor(ContextCompat.getColor(AddNewBooking.this,R.color.black));

        llBike.setBackground(ContextCompat.getDrawable(AddNewBooking.this,R.drawable.white_background));
        imageBike.setImageResource(R.drawable.bike_black);
        tv_bike.setTextColor(ContextCompat.getColor(AddNewBooking.this,R.color.black));
        tv_bike_parking_fare.setTextColor(ContextCompat.getColor(AddNewBooking.this,R.color.black4));

    }

    private void bikeSelected(){

        mVehicleType = KEY_BIKE;

        llBike.setBackground(getResources().getDrawable(R.drawable.background_button1));
        imageBike.setImageResource(R.drawable.bike_white);
        tv_bike.setTextColor(getResources().getColor(R.color.white));
        tv_bike_parking_fare.setTextColor(ContextCompat.getColor(AddNewBooking.this,R.color.black));

        llCar.setBackground(getResources().getDrawable(R.drawable.white_background));
        imageCar.setImageResource(R.drawable.car_black);
        tv_car.setTextColor(getResources().getColor(R.color.black));
        tv_car_parking_fare.setTextColor(ContextCompat.getColor(AddNewBooking.this,R.color.black4));


        spinnerBrand.setSelection(0);
        spinnerModel.setSelection(0);
        fetchBrandsFromDatabase(mVehicleType);

    }

    private void fetchBrandsFromDatabase(final String vehicleType) {

        Log.e(TAG,"fetchBrandsFromDatabase :called");

        mProgressBar.setVisibility(View.VISIBLE);

        int type;
        if(vehicleType.equals(KEY_CAR))
            type = 2;
        else type = 1;



        final String URL =  BASE_URL_USER +"fetch_vehicle_brands.php";
        final int finalType = type;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        Log.e(TAG,"fetchBrandsFromDatabase : response " +response);
                        try {

                            mBrandCustomeList.clear();
                            mBrandCustomeList.add(new KeyValueClass(0,"Choose Brand"));

                            JSONArray jsonArray = new JSONArray(response);

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                int brandId = jsonObject.getInt("brand_id");
                                String name = jsonObject.getString("brand_name");

                                mBrandCustomeList.add(new KeyValueClass(brandId,name));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG,"fetchBrandsFromDatabase : onErrorResponse :" +error.toString());
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.e(TAG,"userSignup : getParams");

                Map<String, String> params = new HashMap<>();
                params.put("vehicle_type", String.valueOf(finalType));
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS*1000),
                NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(AddNewBooking.this).addToRequestQueue(stringRequest);

    }

    private void fetchModelsFromDatabase(final int brandId) {

        mProgressBar.setVisibility(View.VISIBLE);

        final String URL = BASE_URL_USER +"fetch_vehicle_models.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        Log.e(TAG,"fetchModelsFromDatabase : response " +response);

                        try {

                            mModelsList.clear();
                            mModelsList.add("Choose Model");

                            JSONArray jsonArray = new JSONArray(response);

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("brand_name");
                                mModelsList.add(name);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG,"fetchModelsFromDatabase : onErrorResponse :" +error.toString());
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.e(TAG,"fetchModelsFromDatabase  : getParams");

                Map<String, String> params = new HashMap<>();
                params.put("brand_id",String.valueOf(brandId));
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS*1000),
                NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(AddNewBooking.this).addToRequestQueue(stringRequest);

    }

    private void sendOfflineParkingDetailToDb(final String vehicleType, final String brand, final String model,
                                              final String plateNo, final String contactNo, final String parkingType, final String des) {

        Log.e(TAG,"called : sendOfflineParkingDetailToDb");

        String URL = BASE_URL + "insertOfflineParkingBooking.php";

        mProgressBar.setVisibility(View.VISIBLE);

        final String fare_per_hr;
        if(mVehicleType.equals(KEY_CAR))
            fare_per_hr = mServiceDetail.get(S_CAR_FARE);
        else
            fare_per_hr = mServiceDetail.get(S_BIKE_FARE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        Log.e(TAG,response);

                        try {
                            int rc = new JSONArray(response).getJSONObject(0).getInt("rc");
                            String mess = new JSONArray(response).getJSONObject(0).getString("mess");
                            Toast.makeText(AddNewBooking.this,mess,Toast.LENGTH_SHORT).show();
                            if(rc == 1){
                                String unixTime = new JSONArray(response).getJSONObject(0).getString("unixTime");
                                openReceiptPage(plateNo,brand,model,unixTime,fare_per_hr);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG,error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                String partnerId = new LoginSessionManager(AddNewBooking.this).getEmpDetailsFromSP().get(KEY_PARTNER_ID);

                params.put("partner_id",partnerId);
                params.put("vehicle_type",vehicleType);
                params.put("brand",brand);
                params.put("model",model);
                params.put("plate_no",plateNo);
                params.put("cust_contact",contactNo);
                params.put("parking_type",parkingType);
                params.put("fare_per_hr",fare_per_hr);
                params.put("remarks",des);

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS*1000),
                NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(AddNewBooking.this).addToRequestQueue(stringRequest);
    }


    private void openReceiptPage(String plateNo, String brand, String model,String unixTime,String fare_per_hr) {

        final Dialog dialog = new Dialog(AddNewBooking.this);
        View view = LayoutInflater.from(AddNewBooking.this).inflate(R.layout.dialog_receipt, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BillAnimation1;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();

            }
        });


        TextView tv_parking_name = view.findViewById(R.id.parking_name);
        TextView tv_location     = view.findViewById(R.id.location);
        TextView tv_date         = view.findViewById(R.id.date);
        TextView tv_time         = view.findViewById(R.id.time);
        TextView tv_number_plate = view.findViewById(R.id.number_plate);
        TextView tv_brand        = view.findViewById(R.id.brand);
        TextView tv_model        = view.findViewById(R.id.model);
        TextView tv_fare         = view.findViewById(R.id.fare_per_hr);
        TextView tv_operator_id  = view.findViewById(R.id.operator_id);
        TextView tv_message      = view.findViewById(R.id.message);

        tv_parking_name.setText(mServiceDetail.get(S_DESCRIPTION));
        tv_location.setText(mServiceDetail.get(S_LOCATION));
        tv_date.setText(getFormattedDate(TAG,unixTime));
        tv_time.setText(getFormattedTime(TAG,unixTime));
        tv_number_plate.setText(plateNo);
        tv_brand.setText(brand);
        tv_model.setText(model);



        tv_fare.setText("Rs "+fare_per_hr+"/hr");
        tv_operator_id.setText(mOperatorId);


        String text = "<font color=#5d636b>Parking at owners risk. No responsibility for valuable items like laptop, wallet, cash etc. </font>" +
                "<b><font color=#000000>Lost ticket charges RS 20 </font></b><><font color=#5d636b>after verification.</font>";
        tv_message.setText(Html.fromHtml(text));

        view.findViewById(R.id.print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddNewBooking.this,"printing.......",Toast.LENGTH_SHORT).show();

            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });


        dialog.show();

    }

}
