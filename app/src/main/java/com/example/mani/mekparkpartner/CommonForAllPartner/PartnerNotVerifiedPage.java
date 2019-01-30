package com.example.mani.mekparkpartner.CommonForAllPartner;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.mani.mekparkpartner.ParkingPartner.MenuModel;

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

public class PartnerNotVerifiedPage extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private LoginSessionManager mLoginSession;

    //Navigation Menu
    private ExpandableListView mExpandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    com.example.mani.mekparkpartner.ParkingPartner.ExpandableListAdapter expandableListAdapter;

    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_not_verified_page);

        mLoginSession = new LoginSessionManager(PartnerNotVerifiedPage.this);

        settingNavigation();
        setupBottonNavigation();
        setPage();

    }


    private void fetchParkingInfoAndSaveToSP() {

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
                        Toast.makeText(PartnerNotVerifiedPage.this,mess,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject jsonObject = jsonArray.getJSONObject(1);

                    String address        = jsonObject.getString("address");
                    String openingHrs     = jsonObject.getString("opening_hrs");
                    String parkingType    = jsonObject.getString("parking_type");

                    String bikeCapacity   = jsonObject.getString("bike_capacity");
                    String carCapacity    = jsonObject.getString("car_capacity");
                    String bikeVacancy    = jsonObject.getString("bike_vacancy");
                    String carVacancy     = jsonObject.getString("car_vacancy");
                    String bikeFare       = jsonObject.getString("bike_fare");
                    String carFare        = jsonObject.getString("car_fare");

                    mLoginSession.insertServiceDetailsinSP(address,openingHrs,bikeCapacity,carCapacity,bikeVacancy,
                            carVacancy,bikeFare,carFare);

                    Log.e(TAG, "service details saved to shared preference");

                } catch (JSONException e) {

                    e.printStackTrace();
                    Log.e(TAG,e.toString());
                    Toast.makeText(PartnerNotVerifiedPage.this,e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
                Toast.makeText(PartnerNotVerifiedPage.this,error.toString(),Toast.LENGTH_SHORT).show();

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


    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        fetchParkingInfoAndSaveToSP();
    }

    private void setupBottonNavigation() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.nav_home: return true;
                    case R.id.nav_2:
                    case R.id.nav_transaction:
                        Toast.makeText(PartnerNotVerifiedPage.this,"Wait till Verification...",Toast.LENGTH_SHORT).show();
                        return false;
                    case R.id.nav_profile:
                        startActivity(new Intent(PartnerNotVerifiedPage.this,ProfilePage.class));
                        return true;
                }
                return false;
            }
        });


        findViewById(R.id.fab_sm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(PartnerNotVerifiedPage.this, ShowParkingDetail.class));
            }
        });





    }

    private void setPage() {

        TextView tv_name = findViewById(R.id.name);
        tv_name.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_NAME)+"!");

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
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.nav1,PartnerNotVerifiedPage.this.getTheme());
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
        //navigationView.setNavigationItemSelectedListener(this);

        // Set Navigation Header
        View headerView      =  navigationView.getHeaderView(0);

        ImageView iv_profile =  headerView.findViewById(R.id.profile_pic);
        TextView tv_name     =  headerView.findViewById(R.id.name);
        TextView tv_mobile   =  headerView.findViewById(R.id.mobile);

        // need to set a profile pic -
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PartnerNotVerifiedPage.this,ProfilePage.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        tv_name.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_NAME));
        tv_mobile.setText(mLoginSession.getEmpDetailsFromSP().get(KEY_PHONE));



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_not_verified_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.technical_support) {
            Toast.makeText(PartnerNotVerifiedPage.this,"Technical Support",Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareMenuData() {

        MenuModel menuModel = new MenuModel(0,"Profile",false,true,R.drawable.my_account);
        headerList.add(menuModel);
        if (!menuModel.isHasChildren()) {
            childList.put(menuModel, null);
        }

        List<MenuModel> childModelsList = new ArrayList<>();

        menuModel = new MenuModel(3,"Transaction History",false,true,R.drawable.wallet);
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
        MenuModel childModel = new MenuModel(51,"FAQs & Links", false, false, 0);
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
                mLoginSession.logoutUser();
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
                            case 0: startActivity(new Intent(PartnerNotVerifiedPage.this,ProfilePage.class));break;
                            case 1: //startActivity(new Intent(PartnerNotVerifiedPage.this,MyVehiclePage.class));break;
                            case 2: break;
                            case 3: //startActivity(new Intent(PartnerNotVerifiedPage.this,MekCoinsWallet.class));break;
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
                                case 0:Toast.makeText(PartnerNotVerifiedPage.this,"faq and link",Toast.LENGTH_SHORT).show();break;

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
}
