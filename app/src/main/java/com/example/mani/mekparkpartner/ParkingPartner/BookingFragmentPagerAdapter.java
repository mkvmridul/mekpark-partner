package com.example.mani.mekparkpartner.ParkingPartner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class BookingFragmentPagerAdapter extends FragmentPagerAdapter {


    List<Fragment> mFragmentList;

    public BookingFragmentPagerAdapter(FragmentManager fm , List<Fragment> fragmentList) {
        super(fm);
        this.mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0: return mFragmentList.get(0);
            case 1: return mFragmentList.get(1);
            case 2: return mFragmentList.get(2);
            case 3: return mFragmentList.get(3);
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
            case 0: return "New";
            case 1: return "Upcoming";
            case 2: return "Ongoing";
            case 3: return "History";

        }

        return null;
    }
}
