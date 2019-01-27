package com.example.mani.mekparkpartner.LoginRelated.Pages;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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
import android.widget.ImageView;
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
import com.example.mani.mekparkpartner.ParkingPartner.ParkingHomePage;
import com.example.mani.mekparkpartner.R;
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
import java.util.HashMap;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.FREE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.GARAGE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.PAID_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_EMAIL;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_NAME;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_TYPE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PHONE;

public class InitialProfilePage extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    LoginSessionManager mLoginSession;

    private BroadcastReceiver mBroadcastReceiver;
    private static final int GALLARY_REQUEST = 1;

    private TextView mTv_id;
    private ImageView mIv_id;

    private Bitmap mIdBitmap;
    private Uri mIduri;
    private String mIdRealPath;

    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_profile_page);

        mLoginSession = new LoginSessionManager(InitialProfilePage.this);

        mProgressDialog = new ProgressDialog(InitialProfilePage.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);

        String token = SharedPrefFcm.getmInstance(InitialProfilePage.this).getToken();
        if(token!=null){
            Log.e(TAG,"Fcm token from sharedPref: "+token);
            storeTokenToDb(token);
        }
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String token = SharedPrefFcm.getmInstance(InitialProfilePage.this).getToken();
                if(token!=null){
                    Log.e(TAG,"Fcm token broadcast: "+token);
                }
            }
        };

        setDetails();
        clickListener();
    }




    private void setDetails() {

        TextView tv_name    = findViewById(R.id.name);
        TextView tv_name2   = findViewById(R.id.name2);
        TextView tv_mobile  = findViewById(R.id.mobile);
        TextView tv_email   = findViewById(R.id.email);
        TextView tv_pType   = findViewById(R.id.partner_type);

        tv_name.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_NAME)+"!");
        tv_name2.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_NAME));
        tv_mobile.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_PHONE));
        tv_email.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_EMAIL));
        tv_pType.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE));



    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView tv_add_accout = findViewById(R.id.add_account_details);

        if(mLoginSession.isAccountDetailedFIlled()) {
            tv_add_accout.setText("Account Details Added");
            tv_add_accout.setTextColor(ContextCompat.getColor(this, R.color.green));
            tv_add_accout.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }

        if(mLoginSession.isServiceManagemantFilled()) {
            TextView sm = findViewById(R.id.service_manage);
            sm.setText("Service Details Added");
            sm.setBackground(ContextCompat.getDrawable(this, R.drawable.background_button3));

        }
    }

    private void clickListener() {

        mTv_id = findViewById(R.id.id_number);
        mIv_id = findViewById(R.id.id_imageview);


        findViewById(R.id.change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InitialProfilePage.this,"Change Password",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.service_manage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLoginSession.isServiceManagemantFilled()) {
                    Toast.makeText(InitialProfilePage.this, "Service Details is already filled", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(new Intent(InitialProfilePage.this,ParkingSlotDetail.class));

            }
        });

        findViewById(R.id.add_account_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLoginSession.isAccountDetailedFIlled()) {
                    Toast.makeText(InitialProfilePage.this, "Account Details already filled", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(new Intent(InitialProfilePage.this,AccountDetails.class));
            }
        });


        mIv_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(InitialProfilePage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InitialProfilePage.this,
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

                if(mLoginSession.isServiceManagemantFilled() && mLoginSession.isAccountDetailedFIlled()){

                    String licenceId = mTv_id.getText().toString().trim();

                    if(!licenceId.equals("")){
                        if(mIdBitmap == null){
                            Toast.makeText(InitialProfilePage.this,"Attach your id",Toast.LENGTH_SHORT).show();
                            mTv_id.setText("");
                            return;
                        }
                        uploadLicence(licenceId);
                    
                    }
                    else {
                        intentToPageNotVerified();
                    }

                }

                else{
                    Toast.makeText(InitialProfilePage.this, "Fill Service management and Account Details first", Toast.LENGTH_SHORT).show();
                }
            }
        });





    }


    private void uploadLicence(String licenceId) {

        Log.e(TAG,"called : uploadLicence");

        String partner_id   = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);

        final  String UPLOAD_URL = BASE_URL+"upload_licence.php";

        try {
            mProgressDialog.show();
            new MultipartUploadRequest(InitialProfilePage.this,UPLOAD_URL)

                    .addFileToUpload(mIdRealPath, "image")

                    .addParameter("partner_id", partner_id)
                    .addParameter("licence_id",licenceId)

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
                            Toast.makeText(InitialProfilePage.this,"Licence Id Uploaded",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            mProgressDialog.dismiss();
                            Log.e(TAG,serverResponse.getBodyAsString());
                            Toast.makeText(InitialProfilePage.this, "done", Toast.LENGTH_SHORT).show();

                            try {
                                JSONArray jsonArray = new JSONArray(serverResponse.getBodyAsString());
                                int allSuccess = Integer.parseInt(jsonArray.getJSONObject(0).getString("all_success"));

                                Log.e(TAG,"all_success "+allSuccess);

                                if(allSuccess == 1){
                                   intentToPageNotVerified();
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
            Toast.makeText(InitialProfilePage.this,"Error uploading",Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    private void storeTokenToDb(final String refreshedToken) {

        final String URL_TOKEN = BASE_URL + "storePartnerToken.php";
        final String empId = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);

        Log.e(TAG, "EMP ID "+empId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG,response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");

                    if(rc==1)
                        Log.e(TAG,"refreshed token is send to db " + refreshedToken);
                    else
                        Log.e(TAG,"refreshed token cant be send ");


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Exception cougnt "+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("emp_id",empId);
                params.put("fcm_token",refreshedToken);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
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
                    mIdBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                    mIv_id.setImageBitmap(mIdBitmap);

                    String empId = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);
                    String imageName = empId+".jpg";

                    mIduri      = getImageUri(mIdBitmap,imageName);
                    mIdRealPath = getRealPathFromURI(mIduri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(InitialProfilePage.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }


    public Uri getImageUri(Bitmap inImage,String title) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(InitialProfilePage.this.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void intentToPageNotVerified() {
        startActivity(new Intent(InitialProfilePage.this,PartnerNotVerifiedPage.class));
        finish();

    }


}
