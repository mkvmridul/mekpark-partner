package com.example.mani.mekparkpartner.ParkingPartner;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import com.example.mani.mekparkpartner.CommonForAllPartner.PartnerNotVerifiedPage;
import com.example.mani.mekparkpartner.CommonForAllPartner.ProfilePage;
import com.example.mani.mekparkpartner.CommonForAllPartner.ShowParkingDetail;
import com.example.mani.mekparkpartner.FCMPackage.SharedPrefFcm;
import com.example.mani.mekparkpartner.OffileParkingPartner.OfflineHomepage;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentHistory;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentNew;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentOngoing;
import com.example.mani.mekparkpartner.ParkingPartner.Fragments.FragmentUpcoming;
import com.example.mani.mekparkpartner.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.BASE_URL;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.NO_OF_RETRY;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.RETRY_SECONDS;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_NAME;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PARTNER_ID;
import static com.example.mani.mekparkpartner.CommanPart.LoginSessionManager.KEY_PHONE;


public class ParkingPartnerHomePage extends AppCompatActivity {

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

    private IntentIntegrator mQrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_home_page);

        mLoginSession = new LoginSessionManager(ParkingPartnerHomePage.this);

        if (!mLoginSession.isLoggedIn()) {
            Log.e(TAG,"Not logged in");
            mLoginSession.checkLogin();
            finish();
            return;
        }

        Log.e(TAG,"logged in Parking Partner");

        String token = SharedPrefFcm.getmInstance(ParkingPartnerHomePage.this).getToken();
        if(token!=null){
            Log.e(TAG,"Fcm token from sharedPref: "+token);
            storeTokenToDb(token);
        }
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String token = SharedPrefFcm.getmInstance(ParkingPartnerHomePage.this).getToken();
                if(token!=null){
                    Log.e(TAG,"Fcm token broadcast: "+token);
                }
            }
        };

        mFragmentList = new ArrayList<>();
        mBookingList  = new ArrayList<>();
        mProgressDialog = new ProgressDialog(ParkingPartnerHomePage.this);
        mProgressDialog.setMessage("Please wait...");

        mQrScan = new IntentIntegrator(ParkingPartnerHomePage.this);
        mQrScan.setPrompt("");
        mQrScan.setOrientationLocked(true);


        mFragmentList.add(new FragmentNew());
        mFragmentList.add(new FragmentUpcoming());
        mFragmentList.add(new FragmentOngoing());
        mFragmentList.add(new FragmentHistory());

        mTabLayout = findViewById(R.id.tab_layout_booking);

        mSession = new LoginSessionManager(ParkingPartnerHomePage.this);

        settingNavigation();
        setupBottonNavigation();

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
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.nav1,ParkingPartnerHomePage.this.getTheme());
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
                startActivity(new Intent(ParkingPartnerHomePage.this,ProfilePage.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        tv_name.setText(mSession.getEmpDetailsFromSP().get(KEY_NAME));
        tv_mobile.setText(mSession.getEmpDetailsFromSP().get(KEY_PHONE));



    }

    @Override
    protected void onResume() {
        super.onResume();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        fetchParkingInfoAndSaveToSP();
    }

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

                    if(mBookingList.size() == 0){
                        Log.e(TAG,"No booking available");
                        findViewById(R.id.main_layout).setVisibility(View.GONE);
                        findViewById(R.id.no_booking_layout).setVisibility(View.VISIBLE);
                    }

                    else {
                        Log.e(TAG,mBookingList.size() + " Booking Available");
                        findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                        findViewById(R.id.no_booking_layout).setVisibility(View.GONE);
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

                String empId =new LoginSessionManager(ParkingPartnerHomePage.this).getEmpDetailsFromSP().get(KEY_PARTNER_ID);
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
        mTabLayout.addTab(mTabLayout.newTab().setText("New"),true);
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
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab){}
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
                            case 0: startActivity(new Intent(ParkingPartnerHomePage.this,ProfilePage.class));break;
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
                                case 0:Toast.makeText(ParkingPartnerHomePage.this,"faq and link",Toast.LENGTH_SHORT).show();break;

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
            case R.id.scanner:  mQrScan.initiateScan(); return true;
            case R.id.refresh: fetchBookingsFromDb(0); return true;
            case R.id.technical_support: Toast.makeText(ParkingPartnerHomePage.this,"Tech Support",Toast.LENGTH_SHORT).show(); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchParkingInfoAndSaveToSP() {

        Log.e(TAG,"called : fetchParkingInfoAndSaveToSP");

        String SEND_URL = BASE_URL + "get_partner_parking_details.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG,response);

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    int rc = jsonArray.getJSONObject(0).getInt("rc");

                    if(rc<=0){
                        String mess = jsonArray.getJSONObject(0).getString("mess");
                        Log.e(TAG,mess);
                        Toast.makeText(ParkingPartnerHomePage.this,mess,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject jsonObject = jsonArray.getJSONObject(1);

                    String location       = jsonObject.getString("location");
                    String description    = jsonObject.getString("description");
                    String openingHrs     = jsonObject.getString("opening_hrs");
                    String parkingType    = jsonObject.getString("parking_type");

                    String bikeCapacity   = jsonObject.getString("bike_capacity");
                    String carCapacity    = jsonObject.getString("car_capacity");
                    String bikeVacancy    = jsonObject.getString("bike_vacancy");
                    String carVacancy     = jsonObject.getString("car_vacancy");
                    String bikeFare       = jsonObject.getString("bike_fare");
                    String carFare        = jsonObject.getString("car_fare");

                    mLoginSession.insertServiceDetailsinSP(location,description,openingHrs,bikeCapacity,carCapacity,bikeVacancy,
                            carVacancy,bikeFare,carFare);

                    Log.e(TAG, "service details saved to shared preference");

                } catch (JSONException e) {

                    e.printStackTrace();
                    Log.e(TAG,e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
                Toast.makeText(ParkingPartnerHomePage.this,error.toString(),Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                String empId = mLoginSession.getEmpDetailsFromSP().get(KEY_PARTNER_ID);
                params.put("partner_id",empId);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(RETRY_SECONDS*1000,NO_OF_RETRY,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void setupBottonNavigation() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.nav_home: return true;
                    case R.id.nav_2: startActivity(new Intent(ParkingPartnerHomePage.this, OfflineHomepage.class));return true;
                    case R.id.nav_transaction:
                        Toast.makeText(ParkingPartnerHomePage.this,"Transation",Toast.LENGTH_SHORT).show();
                        return false;
                    case R.id.nav_profile:
                        startActivity(new Intent(ParkingPartnerHomePage.this,ProfilePage.class));
                        return true;
                }
                return false;
            }
        });


        findViewById(R.id.fab_sm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParkingPartnerHomePage.this, ShowParkingDetail.class));
            }
        });





    }


    //For Scanner result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                try {

                    JSONObject obj = new JSONObject(result.getContents());
                    Toast.makeText(this, obj.toString(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

