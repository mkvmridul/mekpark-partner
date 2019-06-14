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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;

public class AddAccountDetails extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private static final int GALLARY_REQUEST = 1;

    private ImageView mIv_pan,mIv_cCheck;
    private Bitmap mPanBitmap, mCcheckBitmap;

    // mFlag = 1(panImage) or 2(cancelledImage)
    private int mFlag = 0;
    private ProgressDialog mProgressDialog;

    private Uri mPanUri;
    private Uri mChequeUri;

//    private File mPanFile;
//    private File mChequeFile;

    private String mPanRealPath;
    private String mChequeRealPath;

    LoginSessionManager mSession;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account_details);

        mProgressDialog = new ProgressDialog(AddAccountDetails.this);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setCancelable(false);

        mSession = new LoginSessionManager(AddAccountDetails.this);

        clickListener();
    }

    private void clickListener() {

        final TextView tv_pan    = findViewById(R.id.pan_number);
        final TextView tv_acc    = findViewById(R.id.account_number);
        final TextView tv_ifse   = findViewById(R.id.ifse_code);
        final TextView tv_cCheck = findViewById(R.id.cancelled_check);

        mIv_pan         = findViewById(R.id.pan_imageview);
        mIv_cCheck      = findViewById(R.id.cancelled_check_imageview);


        mIv_pan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(AddAccountDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddAccountDetails.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    return;
                }

                mFlag = 1;
                Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                gallaryIntent.setType("image/*");
                gallaryIntent.putExtra("flag",1);

                startActivityForResult(gallaryIntent,GALLARY_REQUEST);
            }
        });

        mIv_cCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(AddAccountDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(AddAccountDetails.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    return;
                }

                mFlag = 2;
                Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                gallaryIntent.setType("image/*");
                gallaryIntent.putExtra("flag",1);

                startActivityForResult(gallaryIntent,GALLARY_REQUEST);
            }
        });


        findViewById(R.id.save_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String panPattern = "[A-Z]{3}[ABCFGHLJPT][A-Z][0-9]{4}[A-Z]";
                String accPattern = "[0-9]{9,18}";
                String ifsePattern = "^[A-Za-z]{4}[a-zA-Z0-9]{7}$";

                String pan    = tv_pan.getText().toString().trim().toUpperCase();
                String acc    = tv_acc.getText().toString().trim();
                String ifse   = tv_ifse.getText().toString().trim().toUpperCase();
                String cCheque = tv_cCheck.getText().toString().trim().toUpperCase();

                boolean panMatched = verifyPan(pan,panPattern);
                boolean accMatched = verifyPan(acc,accPattern);
                boolean ifcsMatched = verifyPan(ifse,ifsePattern);

                if(!panMatched){
                    Toast.makeText(AddAccountDetails.this,"Check your pan number",Toast.LENGTH_SHORT).show();
                    tv_pan.setText("");
                    return;
                }

                if(!accMatched){
                    Toast.makeText(AddAccountDetails.this,"Check your bank account number",Toast.LENGTH_SHORT).show();
                    tv_acc.setText("");
                    return;
                }


                if(!ifcsMatched){
                    Toast.makeText(AddAccountDetails.this,"Check your ifse number",Toast.LENGTH_SHORT).show();
                    tv_ifse.setText("");
                    return;
                }

                if(cCheque.equals("")){
                    Toast.makeText(AddAccountDetails.this,"Check your Cheque number number",Toast.LENGTH_SHORT).show();
                    tv_cCheck.setText("");
                    return;
                }

                if(mPanBitmap==null || mCcheckBitmap==null){
                    Toast.makeText(AddAccountDetails.this,"Pan and Cancelled Checque image is required",Toast.LENGTH_SHORT).show();
                    return;
                }

                sendAccountDetails(pan,acc,ifse,cCheque);
                
            }
        });
    }

    private void sendAccountDetails(String pan, String acc, String ifse, String cCheque) {

        Log.e(TAG,"called : sendAccountDetails");
        Log.e(TAG,"pan image "+mPanRealPath+" chequepath "+mChequeRealPath);

        String partner_id = mSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);

        final  String UPLOAD_URL = BASE_URL+"upload_account_details.php";

        try {
            mProgressDialog.show();
            new MultipartUploadRequest(AddAccountDetails.this,UPLOAD_URL)

                    .addFileToUpload(mPanRealPath, "imagePan")
                    .addFileToUpload(mChequeRealPath, "imageCheque")

                    .addParameter("partner_id", partner_id)
                    .addParameter("pan",pan)
                    .addParameter("account",acc)
                    .addParameter("ifse",ifse)
                    .addParameter("cheque",cCheque)


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
                                Log.e("asd",serverResponse.toString());
                            if(exception!=null)
                                Log.e("asd",exception.toString());
                            Toast.makeText(AddAccountDetails.this,"Issue is not reported",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            mProgressDialog.dismiss();
                            Log.e(TAG,serverResponse.getBodyAsString());
                            Toast.makeText(AddAccountDetails.this, "done", Toast.LENGTH_SHORT).show();

                            try {
                                JSONArray jsonArray = new JSONArray(serverResponse.getBodyAsString());
                                //Log.e(TAG,""+jsonArray.getJSONObject(0));
                                int allSuccess = Integer.parseInt(jsonArray.getJSONObject(0).getString("all_success"));

                                Log.e(TAG,"all_success "+allSuccess);

                                if(allSuccess == 1){
                                    mSession.setAccountDetailsFilled();
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
            Toast.makeText(AddAccountDetails.this,"Error uploading",Toast.LENGTH_SHORT).show();
            finish();


        }

    }

    private boolean verifyPan(String s,String reg) {

        Pattern pattern = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);


        return matcher.matches();
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

                    if(mFlag == 1){
                        mPanBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                        mIv_pan.setImageBitmap(mPanBitmap);

                        String imageName = "1245pan"+".jpg";

//                        File file = new File(AddAccountDetails.this.getExternalCacheDir(),imageName);
//                        mFilePanUri = FileProvider.getUriForFile(AddAccountDetails.this,"com.example.mani.mekparkpartner.provider",file);
//                        Log.e(TAG,mFilePanUri.toString());

                        mPanUri = getImageUri(mPanBitmap,imageName);
                        mPanRealPath = getRealPathFromURI(mPanUri);


                    }
                    else if(mFlag == 2){
                        mCcheckBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                        mIv_cCheck.setImageBitmap(mCcheckBitmap);

                        String imageName = "1245cheque"+".jpg";

//                        File file = new File(AddAccountDetails.this.getExternalCacheDir(),imageName);
//                        mFileChequeUri = FileProvider.getUriForFile(AddAccountDetails.this,"com.example.mani.mekparkpartner.provider",file);
//                        Log.e(TAG,mFileChequeUri.toString());

                        mChequeUri = getImageUri(mCcheckBitmap,imageName);
                        mChequeRealPath = getRealPathFromURI(mChequeUri);


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(AddAccountDetails.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }


    public Uri getImageUri( Bitmap inImage,String title) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(AddAccountDetails.this.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
