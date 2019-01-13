package com.example.mani.mekparkpartner.LoginRelated;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class SCFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private long baseId = 0;


    public SCFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch(position){
            case 0: return mFragmentList.get(0); // SCPage1
            case 1: return mFragmentList.get(1); // SCPage2
            case 2: return mFragmentList.get(2); // SCPage3
        }
        return null;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }


}
