package com.example.mani.mekparkpartner.LoginRelated;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.HomePage;
import com.example.mani.mekparkpartner.R;

public class SplashScreen extends AppCompatActivity {

    private final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Next two lines help in getting rid off status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        String mekpark ="<b><font color=#da1f26>mek</font><font color=#000000>Park</font></b>";
        mekpark = "&#160 "+mekpark;

        TextView tv_mekpark = findViewById(R.id.name);
        tv_mekpark.setText(Html.fromHtml(mekpark));


        LoginSessionManager session = new LoginSessionManager(getApplicationContext());
        session.setOnBoardingShown();
        Log.e(TAG,"setOnboarding");

        SplashScreenLauncher launcher = new SplashScreenLauncher();
        launcher.start();
    }

    private class SplashScreenLauncher extends Thread {

        public void run() {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            startActivity(new Intent(SplashScreen.this, HomePage.class));
            SplashScreen.this.finish();
        }

    }
}
