package com.example.mani.mekparkpartner.CommonForAllPartner;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.FREE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.GARAGE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.LICENCE_IMAGE_PATH;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.PAID_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.TOWING_PARTNER;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_EMAIL;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_EXECUTIVE_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_LICENCE_IMAGE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_LICENCE_NUMBER;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_NAME;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_TYPE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PASSWORD;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PHONE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.S_LOCATION;


public class ProfilePage extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private static final int GALLARY_REQUEST = 1;
    private LoginSessionManager mLoginSession;

    private ProgressDialog mProgressDialog;
    List<SimpleImage> mImageList;

    private String mPartnerType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mLoginSession = new LoginSessionManager(ProfilePage.this);


        mProgressDialog = new ProgressDialog(ProfilePage.this);
        mProgressDialog.setMessage("Please wait....");
        mProgressDialog.setCancelable(false);

        mImageList = new ArrayList<>();
        mPartnerType = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE);

        setValues();
        clickListener();

        fetchPartnerServiceImages();

    }

    private void fetchPartnerServiceImages() {

        String SEND_URL = BASE_URL + "fetch_partner_service_image.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG,response);
                //mProgressDialog.dismiss();

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");

                    if(rc<=0){
                        String mess = jsonArray.getJSONObject(0).getString("mess");
                        Log.e(TAG,mess);
                        Toast.makeText(ProfilePage.this,mess,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(mImageList.size()!=0){
                        mImageList.clear();
                    }

                    for(int i=1;i<jsonArray.length();i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int imageId           = Integer.parseInt(jsonObject.getString("image_id"));
                        String imageName      = jsonObject.getString("image_name");

                        mImageList.add(new SimpleImage(imageId,imageName));
                    }

                    RecyclerView recyclerView = findViewById(R.id.recycler_view_images);
                    recyclerView.setHasFixedSize(true);

                    recyclerView.setLayoutManager(new LinearLayoutManager(ProfilePage.this,LinearLayoutManager.HORIZONTAL,false));
                    SimplemageAdapter adapter = new SimplemageAdapter(ProfilePage.this,mImageList);
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {

                    e.printStackTrace();
                    Log.e(TAG,e.toString());
                    Toast.makeText(ProfilePage.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // mProgressDialog.dismiss();
                Log.e(TAG,error.toString());
                Toast.makeText(ProfilePage.this,error.toString(),Toast.LENGTH_SHORT).show();

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


    private void setValues() {

        TextView tv_name    = findViewById(R.id.name);
        TextView tv_name2   = findViewById(R.id.name2);
        TextView tv_mobile  = findViewById(R.id.mobile);
        TextView tv_email   = findViewById(R.id.email);

        TextView tv_address = findViewById(R.id.address);
        TextView tv_pType   = findViewById(R.id.partner_type);

        TextView tv_exeId   = findViewById(R.id.exe_id);

        TextView tv_licenceId  = findViewById(R.id.licence_id);
        ImageView iv_licenceId = findViewById(R.id.licence_id_image);

        tv_name.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_NAME)+"!");
        tv_name2.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_NAME));
        tv_mobile.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_PHONE));
        tv_email.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_EMAIL));

        tv_address.setText(mLoginSession.getServiceDetailFromSF().get(S_LOCATION));

        tv_pType.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE));


        String exeId = mLoginSession.getEmpDetailsFromSP().get(KEY_EXECUTIVE_ID);
        if(exeId.equals(""))
            tv_exeId.setText("Will be generated");
        else
            tv_exeId.setText(exeId);

        tv_licenceId.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_LICENCE_NUMBER));
        final String licenceImage = mLoginSession.getEmpDetailsFromSP().get(KEY_LICENCE_IMAGE);

        Log.e(TAG,"image_path "+LICENCE_IMAGE_PATH+licenceImage);

        if(!licenceImage.equals("")){
            Glide.with(ProfilePage.this).load(LICENCE_IMAGE_PATH+licenceImage)
                    .into(iv_licenceId);
        }

        iv_licenceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImageDialog(LICENCE_IMAGE_PATH+licenceImage);
            }
        });


        TextView tv_message = findViewById(R.id.message);
        if(mLoginSession.isPartnerActivated()){

            tv_message.setText("Ready to accept booking");
            tv_message.setBackgroundColor(getApplication().getResources().getColor(R.color.green2));

            findViewById(R.id.swith).setEnabled(true);



        }






    }

    private void clickListener() {

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.account_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(ProfilePage.this,ShowAccountDetails.class));
            }
        });

        TextView tv_serviceManagment = findViewById(R.id.service_manage);


        // Hiding service managemnet button in case of emergency towing partner
        if( mPartnerType.equals(PAID_PARKING_PROVIDER) || mPartnerType.equals(GARAGE_PARKING_PROVIDER)
                || mPartnerType.equals(FREE_PARKING_PROVIDER)){
            tv_serviceManagment.setVisibility(View.VISIBLE);
        }

        else {
            tv_serviceManagment.setVisibility(View.GONE);
        }
        tv_serviceManagment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this,ShowParkingDetail.class));
            }
        });

        findViewById(R.id.add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ProfilePage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProfilePage.this,
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
                onBackPressed();
            }
        });

    }

    private void setImageDialog(String imagePath){

        AlertDialog.Builder dialog = new AlertDialog.Builder(ProfilePage.this);
        View view = LayoutInflater.from(ProfilePage.this).inflate(R.layout.dialog_image_view
                , null);
        dialog.setView(view);

        ImageView imageView  = view.findViewById(R.id.imageView);
        final ProgressBar imageProgressBar = view.findViewById(R.id.image_progress_bar);

        Glide.with(ProfilePage.this)
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

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);

                    String empId = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);
                    String imageName = empId+".jpg";

                    Uri imageUri          = getImageUri(bitmap,imageName);
                    String imageRealPath  = getRealPathFromURI(imageUri);

                    uploadImage(imageRealPath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(ProfilePage.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(String imageRealPath) {

        final  String UPLOAD_URL = BASE_URL+"upload_service_image.php";

        String partnerId = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);

        try {
            mProgressDialog.show();
            new MultipartUploadRequest(ProfilePage.this,UPLOAD_URL)

                    .addFileToUpload(imageRealPath, "image")
                    .addParameter("partner_id", partnerId)

                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) { }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                            mProgressDialog.dismiss();
                            if(serverResponse!=null)
                                Log.e(TAG,"Server response = "+serverResponse.getBodyAsString());
                            if(exception!=null)
                                Log.e(TAG,"Exception from server = "+exception.getMessage());
                            Toast.makeText(ProfilePage.this,"Image not uploded",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            mProgressDialog.dismiss();
                            Log.e(TAG,serverResponse.getBodyAsString());
                            Toast.makeText(ProfilePage.this, "Upload successfully", Toast.LENGTH_SHORT).show();

                            try {
                                JSONArray jsonArray = new JSONArray(serverResponse.getBodyAsString());
                                int allSuccess = Integer.parseInt(jsonArray.getJSONObject(0).getString("all_success"));

                                Log.e(TAG,"all_success "+allSuccess);

                                if(allSuccess == 1){
                                    mLoginSession.setServiceManFilled();
                                    recreate();
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
            Toast.makeText(ProfilePage.this,"Error uploading",Toast.LENGTH_SHORT).show();
            finish();
        }






    }

    public Uri getImageUri(Bitmap inImage,String title) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ProfilePage.this.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }




}
