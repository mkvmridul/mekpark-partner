package com.example.mani.mekparkpartner.TowingPartner;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommonForAllPartner.ProfilePage;
import com.example.mani.mekparkpartner.CommonForAllPartner.ShowParkingDetail;
import com.example.mani.mekparkpartner.ParkingPartner.Booking;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentHistory;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentNew;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentOngoing;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentUpcoming;
import com.example.mani.mekparkpartner.ParkingPartner.MenuModel;
import com.example.mani.mekparkpartner.ParkingPartner.ParkingPartnerHomePage;
import com.example.mani.mekparkpartner.TowingPartner.Fragments.FragHistoryTowing;
import com.example.mani.mekparkpartner.TowingPartner.Fragments.FragNewTowing;
import com.example.mani.mekparkpartner.TowingPartner.Fragments.FragOngoingTowing;
import com.example.mani.mekparkpartner.TowingPartner.Fragments.FragUpcomingTowing;
import com.example.mani.mekparkpartner.TowingPartner.TowingPartnerHome;
import com.example.mani.mekparkpartner.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_NAME;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_TYPE;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PHONE;

public class TowingPartnerHome extends AppCompatActivity {

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
        setContentView(R.layout.activity_towing_partner_home);

        mSession = new LoginSessionManager(TowingPartnerHome.this);

        if (!mSession.isLoggedIn())
        {
            Log.e(TAG,"Not logged in");
            mSession.checkLogin();
            finish();
            return;
        }

        Log.e(TAG,"logged in as Towing Partner");


        mFragmentList = new ArrayList<>();

        mFragmentList.add(new FragNewTowing());
        mFragmentList.add(new FragUpcomingTowing());
        mFragmentList.add(new FragOngoingTowing());
        mFragmentList.add(new FragHistoryTowing());

        mTabLayout = findViewById(R.id.tab_layout_towing);



        bindWidgetsWithAnEvent();
        setupTabLayout();


        settingNavigation();

    }



    /*--------------------------------Tab and Fragmnet----------------------------------*/

    private void bindWidgetsWithAnEvent()
    {
        Log.e(TAG,"bindWidgetsWithEvent");

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e(TAG,"onTabSelected");
                setCurrentTabFragment(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab){}
        });
    }

    private void setupTabLayout() {

        Log.e(TAG,"setUpLayout");
        mTabLayout.addTab(mTabLayout.newTab().setText("New"),true);
        mTabLayout.addTab(mTabLayout.newTab().setText("Upcoming"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Ongoing"));
        mTabLayout.addTab(mTabLayout.newTab().setText("History"));
    }

    private void setCurrentTabFragment(int tabPosition)
    {
        Log.e(TAG,"setCurrenttabFragment");
        switch (tabPosition)
        {
            case 0 :
                Log.e(TAG,"fragment0");
                replaceFragment(mFragmentList.get(0));

                break;
            case 1 :
                Log.e(TAG,"fragment1");
                replaceFragment(mFragmentList.get(1));
                break;

            case 2 :
                Log.e(TAG,"fragment2");
                replaceFragment(mFragmentList.get(2));

                break;

            case 3 :
                Log.e(TAG,"fragment3");
                replaceFragment(mFragmentList.get(3));
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        Log.e(TAG, "replaceFragment");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container_towing, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }



    /*---------------------------------------------Navigation Drawer------------------------------------------------------*/

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
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.nav1,TowingPartnerHome.this.getTheme());
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
                startActivity(new Intent(TowingPartnerHome.this, ProfilePage.class));
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


        menuModel = new MenuModel(1,"Bookings",true,true,R.drawable.services);
        headerList.add(menuModel);

        childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel(11,"New Bookings", false, false, 0);
        childModelsList.add(childModel);

        childModel = new MenuModel(12,"Upcomings Bookings", false, false, 0);
        childModelsList.add(childModel);

        childModel = new MenuModel(13,"Ongoing Bookings", false, false, 0);
        childModelsList.add(childModel);

        childModel = new MenuModel(14,"History", false, false, 0);
        childModelsList.add(childModel);

        if (menuModel.isHasChildren()) {
            childList.put(menuModel, childModelsList);
        }


        menuModel = new MenuModel(2,"Transaction History",false,true,R.drawable.refarrals);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel(3,"Notifications",false,true,R.drawable.notifications);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel(4,"About us",false,true,R.drawable.wallet);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }


        menuModel = new MenuModel(5,"Support",true,true,R.drawable.services);
        headerList.add(menuModel);

        childModelsList = new ArrayList<>();
        childModel = new MenuModel(51,"FAQs & Links", false, false, 0);
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
                            case 0: startActivity(new Intent(TowingPartnerHome.this,ProfilePage.class));break;
                            case 1: break;
                            case 2: break;
                            case 3: break;
                            case 4: break;
                            case 5: break;
                        }
                        //onBackPressed();
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
                        case 1:
                            switch (childPosition){
                                case 0:setCurrentTabFragment(0);break;
                                case 1: setCurrentTabFragment(1);break;
                                case 2: setCurrentTabFragment(2);break;
                                case 3: setCurrentTabFragment(3);break;
                            }
                            break;

                        case 5:
                            switch (childPosition){
                                case 0:
                                    Toast.makeText(TowingPartnerHome.this,"faq and link",Toast.LENGTH_SHORT).show();break;

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
