package com.example.mani.mekparkpartner.OffileParkingPartner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class OfflineBookingFragmentPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragmentList;

    public OfflineBookingFragmentPagerAdapter(FragmentManager fm , List<Fragment> fragmentList) {
        super(fm);
        this.mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0: return mFragmentList.get(0);
            case 1: return mFragmentList.get(1);
        }
        return null;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0: return "Ongoing";
            case 1: return "History";

        }

        return null;
    }
}
