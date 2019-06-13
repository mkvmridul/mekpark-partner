package com.example.mani.mekparkpartner.OffileParkingPartner;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.mani.mekparkpartner.OffileParkingPartner.Model.OfflineParkingBooking;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentHistory;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentOngoing;
import com.example.mani.mekparkpartner.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.setStatusBarColor;

public class OfflineHomepage extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private List<Fragment> mFragmentList;

    private List<OfflineParkingBooking> mOfflineBookingList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_homepage);

        Window window = getWindow();
        setStatusBarColor(window, 0);

        mOfflineBookingList = new ArrayList<>();

        mFragmentList = new ArrayList<>();

        mFragmentList.add(new FragOngoingOffline());
        mFragmentList.add(new FragHistoryOffline());

        ViewPager viewPager = findViewById(R.id.viewpager_booking_offline);

        OfflineBookingFragmentPagerAdapter adapter = new OfflineBookingFragmentPagerAdapter(
                getSupportFragmentManager(),mFragmentList);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout_booking_offline);
        tabLayout.setupWithViewPager(viewPager);


        clickListener();
    }

    private void clickListener() {
        ImageView btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.add_booking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OfflineHomepage.this,AddNewBooking.class));
            }
        });
    }



//    public List<OfflineParkingBooking> fetchOfflineBookingFromParent(int status){
//        List<OfflineParkingBooking> tempList = new ArrayList<>();
//
//        for(int i=0;i<mOfflineBookingList.size();i++){
//            OfflineParkingBooking t = mOfflineBookingList.get(i);
//            if(t.getStatus() == status)
//                tempList.add(t);
//        }
//        return tempList;
//    }




}
