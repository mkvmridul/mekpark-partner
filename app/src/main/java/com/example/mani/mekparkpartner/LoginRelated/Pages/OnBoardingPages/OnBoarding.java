package com.example.mani.mekparkpartner.LoginRelated.Pages.OnBoardingPages;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.R;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

public class OnBoarding extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private List<Fragment> mFragmentList;
    private TextView tv_next, tv_skip;
    private ViewPager viewPager;
    private TextView dot1,dot2,dot3;

    LoginSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        // Next two lines help in getting rid off status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        session = new LoginSessionManager(getApplicationContext());

        if(session.onBoardingShown()){
            startActivity(new Intent(OnBoarding.this,SplashScreen.class));
            finish();
        }



        setContentView(R.layout.activity_on_boarding);

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new SCPage1());
        mFragmentList.add(new SCPage2());
        mFragmentList.add(new SCPage3());

        viewPager = findViewById(R.id.viewpager_onBoarding);
        viewPager.addOnPageChangeListener(this);

        SCFragmentPagerAdapter adapter = new SCFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        viewPager.setAdapter(adapter);

        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        dot1.setBackground(getResources().getDrawable(R.drawable.indicator_selected));

        tv_next = findViewById(R.id.next);
        tv_skip = findViewById(R.id.skip);

        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = viewPager.getCurrentItem();
                if(id == 2){
                    startActivity(new Intent(OnBoarding.this,SplashScreen.class));
                    finish();
                }
                else{
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }

            }
        });


        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OnBoarding.this,SplashScreen.class));
                finish();
            }
        });

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {}

    @Override
    public void onPageSelected(int i) {

        switch (i){

            case 0 :
                dot1.setBackground(getResources().getDrawable(R.drawable.indicator_selected));
                dot2.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));
                dot3.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));

                tv_next.setText("Next");
                tv_skip.setVisibility(View.VISIBLE);
                break;

            case 1:
                dot1.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));
                dot2.setBackground(getResources().getDrawable(R.drawable.indicator_selected));
                dot3.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));
                tv_next.setText("Next");
                tv_skip.setVisibility(View.VISIBLE);
                break;

            case 2:
                dot1.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));
                dot2.setBackground(getResources().getDrawable(R.drawable.indicator_unselected));
                dot3.setBackground(getResources().getDrawable(R.drawable.indicator_selected));
                tv_next.setText("Next");
                tv_next.setText("Start Parking");
                tv_skip.setVisibility(View.INVISIBLE);
                break;

        }

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
