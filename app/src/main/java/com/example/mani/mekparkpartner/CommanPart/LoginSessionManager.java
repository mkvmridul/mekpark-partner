package com.example.mani.mekparkpartner.CommanPart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.example.mani.mekparkpartner.LoginRelated.SingupAndLoginFragments.*;


import java.util.HashMap;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.DRIVER_ON_DEMAND;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.FREE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.GARAGE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.OPEN_24_HRS;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.PAID_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.TOWING_PARTNER;

public class LoginSessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME                = "LoginPreference";
    private static final String IS_LOGIN                 = "IsLoggedIn";
    private static final String ON_BOARDING_SHOWN        = "onBoardingShown";

    private static final String IS_ACCOUNT_DETAIL_FILLED = "isAccountDetailFilled";
    private static final String IS_SERVICE_MAN_FILLED    = "isServiceManagemantFilled";
    private static final String IS_PARTNER_ACTIVATED     = "isPartnerActivated";

    public static final String KEY_PARTNER_ID     = "partner_id";
    public static final String KEY_PARTNER_TYPE   = "partner_type";
    public static final String KEY_PASSWORD       = "password";
    public static final String KEY_NAME           = "name";
    public static final String KEY_PHONE          = "mobile";
    public static final String KEY_EMAIL          = "email";
    public static final String KEY_LICENCE_NUMBER = "licence_number";
    public static final String KEY_LICENCE_IMAGE  = "licence_image";

    public static final String KEY_EXECUTIVE_ID   = "executive_id";


    /* PartnerType
        1 - Paid Parking type
        2 - Garage Parking type
        3 - Free Parking type
        4 - Driver on demand
        5 - Towing Partner

     */

    // Service info for parking provider

    public static final String S_ADDRESS       = "s_address";
    public static final String S_OPENING_HRS   = "s_opening_hrs";
    public static final String S_BIKE_CAPACITY = "s_bike_capacity";
    public static final String S_CAR_CAPACITY  = "s_car_capacity";

    public static final String S_BIKE_VACANCY  = "s_bike_vacancy";
    public static final String S_CAR_VACANCY   = "s_car_vacancy";

    public static final String S_BIKE_FARE     = "s_bike_fare";
    public static final String S_CAR_FARE      = "s_car_fare";



    public LoginSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String partnerId, String partnerType, String name, String phone,
                                   String pass, String email,String licenceNumber, String licenceImage,String executiveId ){

        editor.putBoolean(IS_LOGIN, true);

        if(partnerType.equals("1"))
            partnerType = PAID_PARKING_PROVIDER;
        if(partnerType.equals("2"))
            partnerType = GARAGE_PARKING_PROVIDER;
        if(partnerType.equals("3"))
            partnerType = FREE_PARKING_PROVIDER;
        if(partnerType.equals("4"))
            partnerType = DRIVER_ON_DEMAND;
        if(partnerType.equals("5"))
            partnerType = TOWING_PARTNER;

        editor.putString(KEY_PARTNER_ID,partnerId);
        editor.putString(KEY_PARTNER_TYPE, partnerType);
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_PHONE,phone);
        editor.putString(KEY_PASSWORD,pass);
        editor.putString(KEY_EMAIL,email);

        editor.putString(KEY_LICENCE_NUMBER,licenceNumber);
        editor.putString(KEY_LICENCE_IMAGE,licenceImage);

        editor.putString(KEY_EXECUTIVE_ID,executiveId);



        editor.commit();

    }

    public void insertServiceDetailsinSP(String address, String openigHrs, String bikeCapacity, String carCapacity,
                                   String bikeVacancy, String carVacancy ,String bikeFare, String carFare){


        editor.putString(S_ADDRESS,address);
        editor.putString(S_OPENING_HRS, openigHrs);
        editor.putString(S_BIKE_CAPACITY,bikeCapacity);
        editor.putString(S_CAR_CAPACITY,carCapacity);
        editor.putString(S_BIKE_VACANCY,bikeVacancy);
        editor.putString(S_CAR_VACANCY,carVacancy);
        editor.putString(S_BIKE_FARE,bikeFare);
        editor.putString(S_CAR_FARE,carFare);

        editor.commit();

    }

    public void updatePassword(String password){
        editor.putString(KEY_PASSWORD, password);
        editor.commit();

    }

    public void checkLogin() {

        if (!this.isLoggedIn()) {

            Intent i = new Intent(context, LoginHomePage.class);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public void setOnBoardingShown(){
        editor.putBoolean(ON_BOARDING_SHOWN, true);
        editor.commit();

    }

    public void setAccountDetailsFilled() {
        editor.putBoolean(IS_ACCOUNT_DETAIL_FILLED, true);
        editor.commit();
    }

    public void setServiceManFilled() {
        editor.putBoolean(IS_SERVICE_MAN_FILLED, true);
        editor.commit();
    }

    public void setPartnerActivated() {
        editor.putBoolean(IS_PARTNER_ACTIVATED, true);
        editor.commit();
    }


    public HashMap<String, String> getEmpDetailsFromSP(){

        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_PARTNER_ID, pref.getString(KEY_PARTNER_ID, null));
        user.put(KEY_PARTNER_TYPE, pref.getString(KEY_PARTNER_TYPE, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, "You are Awesome"));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, "someoneawesom@gmail.com"));

        user.put(KEY_LICENCE_NUMBER, pref.getString(KEY_LICENCE_NUMBER, ""));
        user.put(KEY_LICENCE_IMAGE, pref.getString(KEY_LICENCE_IMAGE, ""));

        user.put(KEY_EXECUTIVE_ID, pref.getString(KEY_EXECUTIVE_ID, ""));

        return user;
    }

    public HashMap<String, String> getServiceDetailFromSF(){

        HashMap<String, String> info = new HashMap<String, String>();

        info.put(S_ADDRESS, pref.getString(S_ADDRESS, "addres not available"));
        info.put(S_OPENING_HRS, pref.getString(S_OPENING_HRS, OPEN_24_HRS));

        info.put(S_BIKE_CAPACITY, pref.getString(S_BIKE_CAPACITY, ""));
        info.put(S_BIKE_VACANCY, pref.getString(S_BIKE_VACANCY, ""));
        info.put(S_BIKE_FARE, pref.getString(S_BIKE_FARE, null));

        info.put(S_CAR_CAPACITY, pref.getString(S_CAR_CAPACITY, ""));
        info.put(S_CAR_VACANCY, pref.getString(S_CAR_VACANCY, ""));
        info.put(S_CAR_FARE, pref.getString(S_CAR_FARE, ""));

        return info;
    }

    public void logoutUser(){

        editor.clear();
        setOnBoardingShown();
        editor.commit();

        Intent i = new Intent(context, LoginHomePage.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Toast.makeText(context,"Logged Out",Toast.LENGTH_SHORT).show();
        context.startActivity(i);

    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean onBoardingShown(){
        return pref.getBoolean(ON_BOARDING_SHOWN, false);
    }

    public boolean isAccountDetailedFIlled(){
        return pref.getBoolean(IS_ACCOUNT_DETAIL_FILLED, false);
    }

    public boolean isServiceManagemantFilled(){
        return pref.getBoolean(IS_SERVICE_MAN_FILLED, false);
    }

    public boolean isPartnerActivated(){
        return pref.getBoolean(IS_PARTNER_ACTIVATED, false);
    }




}
