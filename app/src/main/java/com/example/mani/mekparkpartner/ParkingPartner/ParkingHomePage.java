package com.example.mani.mekparkpartner.ParkingPartner;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mani.mekparkpartner.CommanPart.LoginSessionManager;
import com.example.mani.mekparkpartner.CommanPart.MySingleton;
import com.example.mani.mekparkpartner.FCMPackage.SharedPrefFcm;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentHistory;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentNew;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentOngoing;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentUpcoming;
import com.example.mani.mekparkpartner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_NAME;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PHONE;


public class ParkingHomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = this.getClass().getSimpleName();
    private List<Fragment> mFragmentList;
    private List<Booking> mBookingList;

    private ProgressDialog mProgressDialog;
    private TabLayout mTabLayout;
    private LoginSessionManager mSession;

    //Navigation Menu
    private ExpandableListView mExpandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    com.example.mani.mekparkpartner.ParkingPartner.ExpandableListAdapter expandableListAdapter;

    private int lastExpandedPosition = -1;

    LoginSessionManager mLoginSession;
    private final String URL_TOKEN = BASE_URL + "storePartnerToken.php";
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_home_page);

        mLoginSession = new LoginSessionManager(ParkingHomePage.this);

        if (!mLoginSession.isLoggedIn()) {
            Log.e(TAG,"Not logged in");
            mLoginSession.checkLogin();
            finish();
            return;
        }

        Log.e(TAG,"logged in");

        String token = SharedPrefFcm.getmInstance(ParkingHomePage.this).getToken();
        if(token!=null){
            Log.e(TAG,"Fcm token from sharedPref: "+token);
            storeTokenToDb(token);
        }
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String token = SharedPrefFcm.getmInstance(ParkingHomePage.this).getToken();
                if(token!=null){
                    Log.e(TAG,"Fcm token broadcast: "+token);
                }
            }
        };



        mFragmentList = new ArrayList<>();

        mBookingList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(ParkingHomePage.this);
        mProgressDialog.setMessage("Please wait...");

        mFragmentList.add(new FragmentNew());
        mFragmentList.add(new FragmentUpcoming());
        mFragmentList.add(new FragmentOngoing());
        mFragmentList.add(new FragmentHistory());

        mTabLayout = findViewById(R.id.tab_layout_booking);
        mSession = new LoginSessionManager(ParkingHomePage.this);

        settingNavigation();

        fetchBookingsFromDb(0);

        bindWidgetsWithAnEvent();
        setupTabLayout();

    }

    private void settingNavigation() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        mExpandableListView = findViewById(R.id.expandableListView);
        prepareMenuData();
        populateExpandableList();

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.setDrawerIndicatorEnabled(false);
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.nav1,ParkingHomePage.this.getTheme());
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
        navigationView.setNavigationItemSelectedListener(this);

        // Set Navigation Header
        View headerView      =  navigationView.getHeaderView(0);

        ImageView iv_profile =  headerView.findViewById(R.id.profile_pic);
        TextView tv_name     =  headerView.findViewById(R.id.name);
        TextView tv_mobile   =  headerView.findViewById(R.id.mobile);

        // need to set a profile pic -
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParkingHomePage.this,ProfilePage.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        tv_name.setText(mSession.getEmpDetailsFromSP().get(KEY_NAME));
        tv_mobile.setText(mSession.getEmpDetailsFromSP().get(KEY_PHONE));



    }

    //Getting all bookings types of booking of parking partner from database
    public void fetchBookingsFromDb(final int fragNo) {

        mProgressDialog.show();
        Log.e(TAG,"called : fetchBookingsFromDb");

        String SEND_URL = BASE_URL + "fetch_parking_bookings.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(mBookingList.size()!=0)
                    mBookingList.clear();

                Log.e(TAG,response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");

                    if(rc<=0){
                        String mess = jsonArray.getJSONObject(0).getString("mess");
                        Log.e(TAG,mess);
                        mProgressDialog.dismiss();
                        return;
                    }

                    for(int i=1;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int bookingId = Integer.parseInt(jsonObject.getString("booking_id"));
                        int cusId = Integer.parseInt(jsonObject.getString("customer_id"));

                        String bookingTime =  jsonObject.getString("time_booking");
                        String parkingTime =  jsonObject.getString("park_in");
                        String parkOut     =  jsonObject.getString("park_out");
                        String duration    =  jsonObject.getString("duration");
                        String payCon      =  jsonObject.getString("payment_confirmed");

                        boolean pc;
                        if(payCon.equals("1"))
                            pc = true;
                        else
                            pc = false;

                        String base_fare    = jsonObject.getString("base_fare");
                        String tax          = jsonObject.getString("tax");
                        String add_charges  = jsonObject.getString("add_charges");
                        String total_fare   = jsonObject.getString("total_fare");


                        int status        = Integer.parseInt(jsonObject.getString("status"));
                        String pin           = jsonObject.getString("pin");

                        String brand      = jsonObject.getString("brand");
                        String model      = jsonObject.getString("model");
                        String plateNo    = jsonObject.getString("plate_no");
                        String image      = jsonObject.getString("image");

                        String cusName    = jsonObject.getString("name");
                        String cusMobile  = jsonObject.getString("mobile");

                        mBookingList.add(new Booking(bookingId,cusId,bookingTime,parkingTime,parkOut,duration,pc,
                                base_fare,tax,add_charges,total_fare,
                                status,pin,brand,model,plateNo,image,cusName,cusMobile));
                    }

                    TabLayout.Tab tab =  mTabLayout.getTabAt(fragNo);
                    tab.select();

                    if(getSupportFragmentManager() != null){
                        getSupportFragmentManager()
                                .beginTransaction()
                                .detach(mFragmentList.get(fragNo))
                                .attach(mFragmentList.get(fragNo))
                                .commit();
                    }

                    mProgressDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.e(TAG,error.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                String empId =new LoginSessionManager(ParkingHomePage.this).getEmpDetailsFromSP().get(KEY_PARTNER_ID);
                params.put("emp_id",empId);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);



    }

    //sending parking booking with particular status to required fragments
    public List<Booking> fetchBookingFromParent (int status) {

        List<Booking> bookingList = new ArrayList<>();

        for(int i=0;i<mBookingList.size();i++){
            Booking booking = mBookingList.get(i);
            if(booking.getStatus() == status){
                bookingList.add(booking);
            }


        }

        return bookingList;
    }

    //sending parking booking with status (1,4,5) to history fragments
    public List<Booking> fetchCompletedParking(){

        List<Booking> tempList = new ArrayList<>();

        for(int i=0;i<mBookingList.size();i++){
            Booking booking = mBookingList.get(i);
            int s = booking.getStatus();
            if(s == 1 || s == 4 || s == 5){
                tempList.add(booking);
            }
        }

        return tempList;

    }

    private void setupTabLayout() {

        Log.e(TAG,"setUpLayout");
        mTabLayout.addTab(mTabLayout.newTab().setText("New"),false);
        mTabLayout.addTab(mTabLayout.newTab().setText("Upcoming"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Ongoing"));
        mTabLayout.addTab(mTabLayout.newTab().setText("History"));
    }

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
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
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
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }




    private void prepareMenuData() {

        MenuModel menuModel = new MenuModel(0,"Profile",false,true,R.drawable.my_account);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel(1,"My Vehicle",false,true,R.drawable.my_vehicle);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel(2,"Bookings", true, true, R.mipmap.bookings);
        headerList.add(menuModel);

        List<MenuModel> childModelsList = new ArrayList<>();

        MenuModel childModel = new MenuModel(21,"New Bookings", false, false, 0);
        childModelsList.add(childModel);
        childModel = new MenuModel(22,"Ongoing Bookings", false, false, 0);
        childModelsList.add(childModel);
        childModel = new MenuModel(23,"History", false, false, 0);
        childModelsList.add(childModel);

        if (menuModel.isHasChildren()) {
            childList.put(menuModel, childModelsList);
        }


        menuModel = new MenuModel(3,"Mekcoins Wallet",false,true,R.drawable.wallet);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel(4,"Notifications",false,true,R.drawable.notifications);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }


        menuModel = new MenuModel(5,"Help",true,true,R.drawable.services);
        headerList.add(menuModel);

        childModelsList = new ArrayList<>();
        childModel = new MenuModel(51,"FAQs & Links", false, false, 0);
        childModelsList.add(childModel);

        if (menuModel.isHasChildren()) {
            childList.put(menuModel, childModelsList);
        }

        menuModel = new MenuModel(6,"Refarrals",false,true,R.drawable.refarrals);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel(7,"Offers",false,true,R.drawable.offer);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
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
                            case 0: //startActivity(new Intent(ParkingHomePage.this,Profile.class));break;
                            case 1: //startActivity(new Intent(ParkingHomePage.this,MyVehiclePage.class));break;
                            case 2: break;
                            case 3: //startActivity(new Intent(ParkingHomePage.this,MekCoinsWallet.class));break;
                            case 4: //Notification
                                break;
                            case 5: //Help
                                break;
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
                                case 0:Toast.makeText(ParkingHomePage.this,"faq and link",Toast.LENGTH_SHORT).show();break;

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

    private void storeTokenToDb(final String refreshedToken) {

        final String empId = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG,response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");

                    if(rc==1)
                        Log.e(TAG,"refreshed token is send to db " + refreshedToken);
                    else
                        Log.e(TAG,"refreshed token cant be send ");


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Exception cougnt "+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("emp_id",empId);
                params.put("fcm_token",refreshedToken);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parking_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.refresh: fetchBookingsFromDb(0); return true;
            case R.id.technical_support: Toast.makeText(ParkingHomePage.this,"Tech Support",Toast.LENGTH_SHORT).show(); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

