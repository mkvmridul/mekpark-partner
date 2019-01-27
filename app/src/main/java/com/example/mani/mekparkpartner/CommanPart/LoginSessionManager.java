package com.example.mani.mekparkpartner.CommanPart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.mani.mekparkpartner.LoginRelated.Pages.Login.LoginHomePage;

import java.util.HashMap;

import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.DRIVER_ON_DEMAND;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.FREE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.GARAGE_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.PAID_PARKING_PROVIDER;
import static com.example.mani.mekparkpartner.CommanPart.CoomanVarAndFun.TOWING_PARTNER;

public class LoginSessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME   = "LoginPreference";
    private static final String IS_LOGIN    = "IsLoggedIn";
    private static final String ON_BOARDING_SHOWN    = "onBoardingShown";

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


    /* PartnerType
        1 - Paid Parking type
        2 - Garage Parking type
        3 - Free Parking type
        4 - Driver on demand
        5 - Towing Partner

     */



    public LoginSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String partnerId, String partnerType, String name, String phone,
                                   String pass, String email,String licenceNumber, String licenceImage ){

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

        return user;
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
