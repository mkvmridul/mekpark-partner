package com.example.mani.mekparkpartner.CommanPart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.mani.mekparkpartner.LoginRelated.LoginPage;

import java.util.HashMap;

public class LoginSessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME   = "LoginPreference";
    private static final String IS_LOGIN    = "IsLoggedIn";
    private static final String ON_BOARDING_SHOWN    = "onBoardingShown";

    public static final String KEY_EMP_ID   = "emp_id";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NAME     = "name";
    public static final String KEY_MOBILE   = "mobile";
    public static final String KEY_EMAIL    = "email";


    public LoginSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String empId, String password, String name, String mobile, String email){

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_EMP_ID,empId);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_MOBILE,mobile);
        editor.putString(KEY_EMAIL,email);

        editor.commit();
    }

    public void updatePreference(String password){

        editor.putString(KEY_PASSWORD, password);
        editor.commit();

    }

    public void checkLogin() {

        if (!this.isLoggedIn()) {

            Intent i = new Intent(context, LoginPage.class);

            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public void setOnBoardingShown(){
        editor.putBoolean(ON_BOARDING_SHOWN, true);
        editor.commit();

    }

    public HashMap<String, String> getEmpDetailsFromSP(){

        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_EMP_ID, pref.getString(KEY_EMP_ID, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, "You are Awesome"));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, "someoneawesom@gmail.com"));


        return user;
    }

    public void logoutUser(){

        editor.clear();
        setOnBoardingShown();
        editor.commit();

        Intent i = new Intent(context, LoginPage.class);
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

}
