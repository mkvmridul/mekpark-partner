package com.example.mani.mekparkpartner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommonForAllPartner.ProfilePage;
import com.example.mani.mekparkpartner.ParkingPartner.MenuModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_NAME;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_TYPE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PHONE;

public class EmptyActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private List<Fragment> mFragmentList;

    //private List<Booking> mBookingList;

    private ProgressDialog mProgressDialog;
    private TabLayout mTabLayout;
    private LoginSessionManager mSession;

    //Navigation Menu
    private ExpandableListView mExpandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    com.example.mani.mekparkpartner.ParkingPartner.ExpandableListAdapter expandableListAdapter;

    private int lastExpandedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        mSession = new LoginSessionManager(EmptyActivity.this);

        if (!mSession.isLoggedIn())
        {
            Log.e(TAG,"Not logged in");
            mSession.checkLogin();
            finish();
            return;
        }

        Log.e(TAG,"logged in as Empty Partner");
        settingNavigation();


        TextView textView  = findViewById(R.id.partner_type);
        String partnerType = mSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE);

        textView.setText(partnerType+" will be implemented soon.");

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginSessionManager(EmptyActivity.this).logoutUser();
                finishAffinity();
            }
        });
    }

    private void settingNavigation() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String ptype = mSession.getEmpDetailsFromSP().get(KEY_PARTNER_TYPE);
        getSupportActionBar().setTitle(ptype);

        mExpandableListView = findViewById(R.id.expandableListView);
        prepareMenuData();
        populateExpandableList();

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.setDrawerIndicatorEnabled(false);
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.nav1, EmptyActivity.this.getTheme());
        toggle.setHomeAsUpIndicator(icon);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView      =  navigationView.getHeaderView(0);

        ImageView iv_profile =  headerView.findViewById(R.id.profile_pic);
        TextView tv_name     =  headerView.findViewById(R.id.name);
        TextView tv_mobile   =  headerView.findViewById(R.id.mobile);

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmptyActivity.this, ProfilePage.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        tv_name.setText(mSession.getEmpDetailsFromSP().get(KEY_NAME));
        tv_mobile.setText(mSession.getEmpDetailsFromSP().get(KEY_PHONE));



    }

    private void prepareMenuData() {

        MenuModel menuModel = new MenuModel(0,"Profile",false,true,R.drawable.my_account);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }

        List<MenuModel> childModelsList = new ArrayList<>();

        menuModel = new MenuModel(1,"Transaction History",false,true,R.drawable.refarrals);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel(2,"Notifications",false,true,R.drawable.notifications);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel(3,"About us",false,true,R.drawable.wallet);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }


        menuModel = new MenuModel(4,"Support",true,true,R.drawable.services);
        headerList.add(menuModel);

        childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel(41,"FAQs & Links", false, false, 0);
        childModelsList.add(childModel);

        if (menuModel.isHasChildren()) {
            childList.put(menuModel, childModelsList);
        }




    }

    private void populateExpandableList() {

        expandableListAdapter = new com.example.mani.mekparkpartner.ParkingPartner.ExpandableListAdapter(this, headerList, childList);


        ViewGroup footerView = (ViewGroup) getLayoutInflater().inflate(R.layout.expandableview_footer, mExpandableListView, false);
        mExpandableListView.addFooterView(footerView);

        footerView.findViewById(R.id.footer_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSession.logoutUser();
                finishAffinity();
            }
        });


        mExpandableListView.setAdapter(expandableListAdapter);

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                MenuModel menuModel = headerList.get(groupPosition);
                int id_int = (int) id;

                if (menuModel.isGroup()) {

                    if (!menuModel.isHasChildren()) {
                        switch (id_int){
                            case 0: startActivity(new Intent(EmptyActivity.this,ProfilePage.class));break;
                            case 1: break;
                            case 2: break;
                            case 3: break;
                            case 4: break;
                            case 5: break;
                        }
                        onBackPressed();
                    }
                }

                return false;
            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {

                if (childList.get(headerList.get(groupPosition)) != null) {

                    MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);

                    switch (groupPosition){
                        case 2:
                            switch (childPosition){
                                case 0:

                                    break;
                                case 1:

                                    break;
                                case 2:
                                    break;
                            }
                            break;

                        case 5:
                            switch (childPosition){
                                case 0:
                                    Toast.makeText(EmptyActivity.this,"faq and link",Toast.LENGTH_SHORT).show();break;

                            }

                            break;
                    }
                    onBackPressed();


                }

                return false;
            }
        });

        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    mExpandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
