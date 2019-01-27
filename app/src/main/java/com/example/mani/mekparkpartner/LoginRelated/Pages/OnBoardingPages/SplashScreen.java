package com.example.mani.mekparkpartner.LoginRelated.Pages.OnBoardingPages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.LoginRelated.Pages.PartnerNotVerifiedPage;
import com.example.mani.mekparkpartner.LoginRelated.Pages.InitialProfilePage;
import com.example.mani.mekparkpartner.ParkingPartner.ParkingHomePage;
import com.example.mani.mekparkpartner.R;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.DRIVER_ON_DEMAND;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.FREE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.GARAGE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.PAID_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.TOWING_PARTNER;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_TYPE;

public class SplashScreen extends AppCompatActivity {

    private final String TAG = "SplashScreen";
    LoginSessionManager mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Next two lines help in getting rid off status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        mSession = new LoginSessionManager(getApplicationContext());
        mSession.setOnBoardingShown();
        Log.e(TAG,"setOnboarding");



        SplashScreenLauncher launcher = new SplashScreenLauncher();
        launcher.start();
    }

    private class SplashScreenLauncher extends Thread {

        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if (!mSession.isLoggedIn()) {
                Log.e(TAG,"Not logged in");
                mSession.checkLogin();
                finish();
                return;
            }

            if(! (mSession.isAccountDetailedFIlled() && mSession.isServiceManagemantFilled())  ){
                startActivity(new Intent(SplashScreen.this,InitialProfilePage.class));
                finish();
                return;
            }

            if(! (mSession.isPartnerActivated())  ){
                startActivity(new Intent(SplashScreen.this,PartnerNotVerifiedPage.class));
                finish();
                return;
            }

            String pType = mSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE);

            Intent i = null;

            if(pType.equals(PAID_PARKING_PROVIDER) || pType.equals(GARAGE_PARKING_PROVIDER) ||
                    pType.equals(FREE_PARKING_PROVIDER))
                i = new Intent(SplashScreen.this,ParkingHomePage.class);

            else if(pType.equals(DRIVER_ON_DEMAND)){

            }

            else if(pType.equals(TOWING_PARTNER)){

            }


            startActivity(i);
            SplashScreen.this.finish();
        }

    }
}
