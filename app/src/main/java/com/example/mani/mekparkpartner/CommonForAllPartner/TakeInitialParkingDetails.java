package com.example.mani.mekparkpartner.CommonForAllPartner;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mani.mekparkpartner.AddressDialog;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.R;
import com.google.android.gms.maps.model.LatLng;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.FREE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.GARAGE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.LOCATION_NOT_FOUND;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.OPEN_24_HRS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.PAID_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_TYPE;

public class TakeInitialParkingDetails extends AppCompatActivity implements AddressDialog.AddressDialogListenr {

    private final String TAG = this.getClass().getSimpleName();

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final int GALLARY_REQUEST = 1;
    private LoginSessionManager mLoginSession;

    //Time widget
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

    // flag = 1(from selected) flag = 2(to selected)
    int flag = 1;



    LatLng mSelectedLatng;
    String mSelectedLocation = LOCATION_NOT_FOUND;
    String mLandmark;
    String mLocality;
    String mCity;

    ImageView iv_addImage;
    Bitmap mParkingBitmap = null;
    private Uri mImageuri;
    private String mParkingImageRealPath ="";

    String mBikeFare,mBikeCapacity,mBikeVacancy;
    String mCarFare,mCarCapacity,mCarVacancy;

    private String mOpeningHrs = OPEN_24_HRS;

    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_initial_parking_slot_detail);

        mLoginSession = new LoginSessionManager(TakeInitialParkingDetails.this);
        mProgressDialog = new ProgressDialog(TakeInitialParkingDetails.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);

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

        ll_to   = findViewById(R.id.to);
        textTo = findViewById(R.id.text_to);
        c2 = findViewById(R.id.c2);

        am  = findViewById(R.id.am);
        pm  = findViewById(R.id.pm);

        et_address = findViewById(R.id.address);


        et_bikeFare    = findViewById(R.id.bike_fare);
        et_bikeCap     = findViewById(R.id.bike_capacity);
        et_bikeVacancy = findViewById(R.id.bike_vacancy);

        et_carFare     = findViewById(R.id.car_fare);
        et_carCap      = findViewById(R.id.car_capacity);
        et_carVacancy  = findViewById(R.id.car_vacancy);

        //iv_addImage    = findViewById(R.id.address);

        iv_addImage    = findViewById(R.id.add_image);


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

                    mOpeningHrs = OPEN_24_HRS;
                }
                else {
                    ll_time_layout.setVisibility(View.VISIBLE);
                    tv_ohr1.setVisibility(View.GONE);
                    ll_ohr2.setVisibility(View.VISIBLE);


                }

            }
        });


        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
            }
        });
        t12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime((TextView)v);
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
                setAmPmLayout((TextView) v);
            }
        });

        pm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAmPmLayout((TextView) v);
            }
        });

        et_bikeFare.setSelection(et_bikeFare.getText().length());
        et_bikeCap.setSelection(et_bikeCap.getText().length());
        et_bikeVacancy.setSelection(et_bikeVacancy.getText().length());
        et_carFare.setSelection(et_carFare.getText().length());
        et_carCap.setSelection(et_carCap.getText().length());
        et_carVacancy.setSelection(et_carVacancy.getText().length());



    }



    private void clickListener() {

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocationPermissionAndOpenAddressDialog();



            }
        });


        iv_addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(TakeInitialParkingDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TakeInitialParkingDetails.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    return;
                }

                Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                gallaryIntent.setType("image/*");
                gallaryIntent.putExtra("flag",1);

                startActivityForResult(gallaryIntent,GALLARY_REQUEST);

            }
        });


        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
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

                mBikeFare     = et_bikeFare.getText().toString();
                mBikeCapacity = et_bikeCap.getText().toString();
                mBikeVacancy  = et_bikeVacancy.getText().toString();

                mCarFare      = et_carFare.getText().toString().trim();
                mCarCapacity  = et_carCap.getText().toString().trim();
                mCarVacancy   = et_carVacancy.getText().toString().trim();

                mSelectedLocation = et_address.getText().toString().trim();

                Log.e(TAG,"mSelectedLocation 2"+mSelectedLocation);

                if(mSelectedLocation.equals("") || mSelectedLatng == null){
                    Toast.makeText(TakeInitialParkingDetails.this,"Enter Address",Toast.LENGTH_SHORT).show();
                    return;
                }


                if(mBikeFare.equals("") || mBikeCapacity.equals("") ||mBikeVacancy.equals("") ||
                        mCarFare.equals("") ||mCarCapacity.equals("") || mCarVacancy.equals("")){
                    Toast.makeText(TakeInitialParkingDetails.this,"Fill the required filled",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mBikeCapacity.equals("0") ||mBikeVacancy.equals("0") ||
                        mCarCapacity.equals("0") ||mCarVacancy.equals("0") ){
                    Toast.makeText(TakeInitialParkingDetails.this,"Capcity or Vacancy can't be zero",Toast.LENGTH_SHORT).show();
                    return;

                }



                if(mSelectedLocation.equals(LOCATION_NOT_FOUND)){
                    Toast.makeText(TakeInitialParkingDetails.this,"Location can't be found",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mParkingBitmap == null || mParkingImageRealPath.equals("")){
                    Toast.makeText(TakeInitialParkingDetails.this,"Add a parking Image",Toast.LENGTH_SHORT).show();
                    return;
                }

                sendParkingSlotDetails();


            }
        });


    }

    private void sendParkingSlotDetails() {

        Log.e(TAG,"called : sendParkingtDetails");

        String partner_id   = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);
        String partner_type = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE);

        EditText et    = findViewById(R.id.address);
        String address = et.getText().toString().trim();

        if(partner_type.equals(PAID_PARKING_PROVIDER))
            partner_type = "1";
        else if (partner_type.equals(GARAGE_PARKING_PROVIDER))
            partner_type = "2";
        else if (partner_type.equals(FREE_PARKING_PROVIDER))
            partner_type = "3";

        Log.e(TAG, "des - "+address);



        final  String UPLOAD_URL = BASE_URL+"upload_parking_slot_details.php";

        try {
            mProgressDialog.show();
            new MultipartUploadRequest(TakeInitialParkingDetails.this,UPLOAD_URL)

                    .addFileToUpload(mParkingImageRealPath, "image")

                    .addParameter("owned_by", partner_id)
                    .addParameter("location",mLocality)
                    .addParameter("des",address)
                    .addParameter("landmark",mLandmark)
                    .addParameter("opening_hrs",mOpeningHrs)
                    .addParameter("latitude", String.valueOf(mSelectedLatng.latitude))
                    .addParameter("longitude", String.valueOf(mSelectedLatng.longitude))

                    .addParameter("partner_type",partner_type)
                    .addParameter("bike_capacity",mBikeCapacity)
                    .addParameter("car_capacity",mCarCapacity)
                    .addParameter("fare_for_bike",mBikeFare)
                    .addParameter("fare_for_car",mCarFare)
                    .addParameter("initial_bike_vacancy",mBikeVacancy)
                    .addParameter("initial_car_vacancy",mCarVacancy)


                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) { }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                            mProgressDialog.dismiss();
                            Log.e("asd1",uploadInfo.toString());
                            if(serverResponse!=null)
                                Log.e("asd",serverResponse.getBodyAsString());
                            if(exception!=null)
                                Log.e("asd",exception.getMessage());
                            Toast.makeText(TakeInitialParkingDetails.this,"Parking slot is not inserted in db",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            mProgressDialog.dismiss();
                            Log.e(TAG,serverResponse.getBodyAsString());
                            Toast.makeText(TakeInitialParkingDetails.this, "done", Toast.LENGTH_SHORT).show();

                            try {
                                JSONArray jsonArray = new JSONArray(serverResponse.getBodyAsString());
                                int allSuccess = Integer.parseInt(jsonArray.getJSONObject(0).getString("all_success"));

                                Log.e(TAG,"all_success "+allSuccess);

                                if(allSuccess == 1){
                                    mLoginSession.setServiceManFilled();
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG,"exception cought "+e.toString());
                            }

                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            mProgressDialog.dismiss();
                        }
                    })
                    .startUpload();


        } catch (Exception e) {
            mProgressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Log.e(TAG, e.toString());
            Toast.makeText(TakeInitialParkingDetails.this,"Error uploading",Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLARY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                try {
                    mParkingBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                    iv_addImage.setImageBitmap(mParkingBitmap);

                    String empId = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);
                    String imageName = empId+".jpg";

                    mImageuri             = getImageUri(mParkingBitmap,imageName);
                    mParkingImageRealPath = getRealPathFromURI(mImageuri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(TakeInitialParkingDetails.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void setAmPmLayout(TextView v) {

        resetAmPmLayout();

        if(flag == 1)
           openHr2FromAP.setText(v.getText().toString());
        else
            openHr2ToAP.setText(v.getText().toString());

        v.setBackground(getApplication().getResources().getDrawable(R.drawable.red_rounded_background));
        v.setTextColor(getApplication().getColor(R.color.white));
    }

    private void resetAmPmLayout() {
        am.setBackground(getApplication().getResources().getDrawable(R.drawable.white_rounded_background));
        am.setTextColor(getApplication().getColor(R.color.black));

        pm.setBackground(getApplication().getResources().getDrawable(R.drawable.white_rounded_background));
        pm.setTextColor(getApplication().getColor(R.color.black));
    }

    private void setFromToLayout(LinearLayout ll, ImageView iv, TextView tv) {
        
        resetFromToLayout();
        resetAmPmLayout();
        resetTimings();

        TextView temp;
        TextView temp2;

        if(ll == findViewById(R.id.from)){

            temp = findViewById(R.id.am);
            temp2 = findViewById(R.id.t6);
            openHr2From.setText("6:00");
            openHr2FromAP.setText("AM");

            flag = 1;
        }
        else{
            temp = findViewById(R.id.pm);
            temp2 = findViewById(R.id.t8);
            openHr2To.setText("8:00");
            openHr2ToAP.setText("PM");

            flag = 2;
        }


        temp.setBackground(getApplication().getResources().getDrawable(R.drawable.red_rounded_background));
        temp.setTextColor(getApplication().getColor(R.color.white));

        temp2.setBackground(getApplication().getDrawable(R.drawable.time_background));
        temp2.setTextColor(getApplication().getResources().getColor(R.color.white));


        ll.setBackground(getApplication().getResources().getDrawable(R.drawable.red_background));
        iv.setImageDrawable(getApplication().getDrawable(R.drawable.time_white));
        tv.setTextColor(TakeInitialParkingDetails.this.getColor(R.color.white));
        
    }

    private void resetFromToLayout() {

        ll_from.setBackground(getApplication().getResources().getDrawable(R.drawable.border_light_black));
        c1.setImageDrawable(getApplication().getDrawable(R.drawable.time_4));
        textFrom.setTextColor(TakeInitialParkingDetails.this.getColor(R.color.white_2));

        ll_to.setBackground(getApplication().getResources().getDrawable(R.drawable.border_light_black));
        c2.setImageDrawable(getApplication().getDrawable(R.drawable.time_4));
        textTo.setTextColor(TakeInitialParkingDetails.this.getColor(R.color.white_2));

    }

    private void selectTime(TextView v) {

        resetTimings();

        if(flag ==1)
            openHr2From.setText(v.getText().toString());
        else
            openHr2To.setText(v.getText().toString());

        v.setBackground(getApplication().getDrawable(R.drawable.time_background));
        v.setTextColor(getApplication().getResources().getColor(R.color.white));

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



    @Override
    public void onBackPressed() {

        Log.e(TAG,"onBackpressed");
        super.onBackPressed();

    }

    public Uri getImageUri(Bitmap inImage,String title) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(TakeInitialParkingDetails.this.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void getAddressViaListener(LatLng selectedLatlng, String completeAddress, String landmark, String locality, String city) {

        mSelectedLatng    = selectedLatlng;
        mSelectedLocation = completeAddress;
        mLandmark         = landmark;
        mLocality         = locality;
        mCity             = city;

        et_address.setText(completeAddress);

        et_address.setSelection(et_address.getText().length());
    }

    private void getLocationPermissionAndOpenAddressDialog() {

        Log.e(TAG, "getLocationPermission");

        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(TakeInitialParkingDetails.this,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(TakeInitialParkingDetails.this,
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                new AddressDialog().show(getSupportFragmentManager(), null);

            } else {
                ActivityCompat.requestPermissions(TakeInitialParkingDetails.this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(TakeInitialParkingDetails.this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
