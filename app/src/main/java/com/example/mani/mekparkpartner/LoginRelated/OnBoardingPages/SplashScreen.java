package com.example.mani.mekparkpartner.LoginRelated.OnBoardingPages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommonForAllPartner.PartnerNotVerifiedPage;
import com.example.mani.mekparkpartner.CommonForAllPartner.InitialProfilePage;
import com.example.mani.mekparkpartner.R;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.FREE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.GARAGE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.PAID_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.launchPartnerAcitvity;
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

            Log.e(TAG,"iSesionFilled "+mSession.isServiceManagemantFilled());

            String partnerType =  mSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE);



            if(((partnerType.equals(PAID_PARKING_PROVIDER) || partnerType.equals(FREE_PARKING_PROVIDER) ||
                    partnerType.equals(GARAGE_PARKING_PROVIDER))
                    && ( !mSession.isServiceManagemantFilled())) ){
                Log.e(TAG, "opening initialProfilePage");
                startActivity(new Intent(SplashScreen.this,InitialProfilePage.class));

                finish();
                return;
            }
            if(! (mSession.isAccountDetailedFIlled() )  ){
                Log.e(TAG, "opening initialProfilePage");
                startActivity(new Intent(SplashScreen.this,InitialProfilePage.class));
                finish();
                return;
            }



            if(! (mSession.isPartnerActivated())  ){
                Log.e(TAG, "opening PartnerNotVerifiedPage");
                startActivity(new Intent(SplashScreen.this,PartnerNotVerifiedPage.class));
                finish();
                return;
            }

            String ptye = mSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE);
            Log.e(TAG, "opening respective page");
            launchPartnerAcitvity(SplashScreen.this,ptye);
            finish();
        }

    }


}
