package com.example.mani.mekparkpartner.ParkingPartner;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.LoginRelated.Pages.MyParkingImage;
import com.example.mani.mekparkpartner.LoginRelated.Pages.MyParkingImageAdapter;
import com.example.mani.mekparkpartner.ParkingPartner.ShowDetails.HistoryDetail;
import com.example.mani.mekparkpartner.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_IMAGE_PATH;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.LICENCE_IMAGE_PATH;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_EMAIL;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_LICENCE_IMAGE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_LICENCE_NUMBER;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_NAME;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_TYPE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PHONE;

public class ProfilePage extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private LoginSessionManager mLoginSession;

    List<MyParkingImage> mImageList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mLoginSession = new LoginSessionManager(ProfilePage.this);

        setValues();

        mImageList = new ArrayList<>();

        mImageList.add(new MyParkingImage(1,R.mipmap.park4));
        mImageList.add(new MyParkingImage(1,R.mipmap.park5));
        mImageList.add(new MyParkingImage(1,R.mipmap.park6));



        RecyclerView recyclerView = findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(ProfilePage.this,LinearLayoutManager.HORIZONTAL,false));
        MyParkingImageAdapter adapter = new MyParkingImageAdapter(ProfilePage.this,mImageList);
        recyclerView.setAdapter(adapter);

    }

    private void setValues() {

        TextView tv_name    = findViewById(R.id.name);
        TextView tv_name2   = findViewById(R.id.name2);
        TextView tv_mobile  = findViewById(R.id.mobile);
        TextView tv_email   = findViewById(R.id.email);

        TextView tv_pType   = findViewById(R.id.partner_type);

        TextView tv_licenceId  = findViewById(R.id.licence_id);
        ImageView iv_licenceId = findViewById(R.id.licence_id_image);


        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        tv_name.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_NAME)+"!");
        tv_name2.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_NAME));
        tv_mobile.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_PHONE));
        tv_email.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_EMAIL));

        tv_pType.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE));

        tv_licenceId.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_LICENCE_NUMBER));
        String licenceImage = mLoginSession.getEmpDetailsFromSP().get(KEY_LICENCE_IMAGE);

        Log.e(TAG,"image_path "+LICENCE_IMAGE_PATH+licenceImage);

        if(!licenceImage.equals("")){
            Glide.with(ProfilePage.this).load(LICENCE_IMAGE_PATH+licenceImage)
                    .into(iv_licenceId);
        }






    }
}
