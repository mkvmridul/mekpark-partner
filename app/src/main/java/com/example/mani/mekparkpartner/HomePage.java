package com.example.mani.mekparkpartner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mani.mekparkpartner.ParkingPartner.BookingPage;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;

public class HomePage extends AppCompatActivity {

    private Context mContext = HomePage.this;
    private final String TAG = this.getClass().getSimpleName();

    private LoginSessionManager mLoginSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mLoginSession = new LoginSessionManager(mContext);

        if (!mLoginSession.isLoggedIn()) {
            Log.e(TAG,"Not logged in");
            mLoginSession.checkLogin();
            finish();
            return;
        }

        Log.e(TAG,"logged in");


        findViewById(R.id.parking_partner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this,BookingPage.class));
            }
        });





        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginSession.logoutUser();
                finish();
                
            }
        });






    }
}
