package com.example.mani.mekparkpartner.CommanPart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CoomanVarAndFun {

    // Keys
    public static final String KEY_BIKE = "bike";
    public static final String KEY_CAR = "car";

    public static final String MANUAL    = "manual";
    public static final String AUTOMATIC = "automatic";

    public static final String HATCHBACK = "hatchback";
    public static final String SEDAN     = "sedan";
    public static final String SUV       = "suv";
    public static final String PREMIUM   = "premium";



    //Mobile
    //public static final String BASE_URL = "http://192.168.43.153/mekPark/partner/";
    //public static final String BASE_URL = "http://192.168.1.11/mekPark/partner/";
    public static final String BASE_URL = "http://mekpark.com/mani14/partner/";



    public static final int RETRY_SECONDS = 5 ;
    public static final int NO_OF_RETRY = 0 ;

    public static final String TIME_FORMAT  = "hh:mm a";


    @SuppressLint("ResourceAsColor")
    public static void setStatusBarColor(Window window, int a){

        // if a=0 => primarydark color else light black

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if(a == 0)
                window.setStatusBarColor(Color.parseColor("#B71C1C"));
            else
                window.setStatusBarColor(Color.argb(255, 133, 146, 158));
        }
    }

    public static void sendNavigateIntent(Context context, double latitude, double longitude){

        String uri = "google.navigation:q="+latitude+","+longitude;

        Uri gmmIntentUri = Uri.parse(uri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);

    }

    public void sendEmailIntent(Context context){

        String email_id = "info@mekpark.com";

        String email_subject = "Feedback for mekPark App";


        Intent emailIntent =  new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto",email_id,null));

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, email_subject);


        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(emailIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if(isIntentSafe)
            context.startActivity(Intent.createChooser(emailIntent,"Send email via ..."));
        else
            Toast.makeText(context,"Email can't be send from here",Toast.LENGTH_SHORT).show();


    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void handleVolleyError(Context context, VolleyError error){

        if(error instanceof TimeoutError){
            Toast.makeText(context,"Timeout Error",Toast.LENGTH_LONG).show();
        }
        else if (error instanceof NoConnectionError){
            Toast.makeText(context,"No Connection Error",Toast.LENGTH_LONG).show();
        }
        else if (error instanceof AuthFailureError){
            Toast.makeText(context,"Authentication Error",Toast.LENGTH_LONG).show();
        }
        else if (error instanceof ServerError){
            Toast.makeText(context,"Server Error",Toast.LENGTH_LONG).show();
        }
        else if (error instanceof NetworkError){
            Toast.makeText(context,"Network Error",Toast.LENGTH_LONG).show();
        }
        else if(error instanceof ParseError){
            Toast.makeText(context,"Parse Error",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show();
        }
    }

    public static String getFormattedTime(String TAG, String s) {

        String time = "NA";
        Date date;
        SimpleDateFormat sdf = new java.text.SimpleDateFormat(TIME_FORMAT);

        try {
            Long unix   = Long.valueOf(s);
            date        = new java.util.Date(unix*1000L);
            time        = sdf.format(date);

        }catch (Exception e){
            Log.e(TAG,"Exception cought while converting time : "+e.toString());

        }

        return time;

    }

    public static String getFormattedDate(String TAG, String unix) {

        String formatedstring = "NA";

        SimpleDateFormat sdf = new SimpleDateFormat("d");
        Long longUnix = null;

        try {

            longUnix = Long.valueOf(unix);
            String date = sdf.format(new Date(longUnix * 1000L));

            if (date.endsWith("1") && !date.endsWith("11"))
                sdf = new SimpleDateFormat("d'st' MMM, yyyy");
            else if (date.endsWith("2") && !date.endsWith("12"))
                sdf = new SimpleDateFormat("d'nd' MMM, yyyy");
            else if (date.endsWith("3") && !date.endsWith("13"))
                sdf = new SimpleDateFormat("d'rd' MMM, yyyy");
            else
                sdf = new SimpleDateFormat("d'th' MMM, yyyy");

        }catch (Exception e){
            Log.e(TAG,"Exception cought while converting time : "+e.toString());
        }


        formatedstring = sdf.format(new Date(longUnix*1000L));


        return formatedstring;



    }
    public static String getFormattedDate2(String TAG, String unix) {

        String formatedstring = "NA";

        SimpleDateFormat sdf = new SimpleDateFormat("d");
        Long longUnix = null;

        try {

            longUnix = Long.valueOf(unix);
            String date = sdf.format(new Date(longUnix * 1000L));

            if (date.endsWith("1") && !date.endsWith("11"))
                sdf = new SimpleDateFormat("EEEE, d'st' MMM");
            else if (date.endsWith("2") && !date.endsWith("12"))
                sdf = new SimpleDateFormat("EEEE, d'nd' MMM");
            else if (date.endsWith("3") && !date.endsWith("13"))
                sdf = new SimpleDateFormat("EEEE, d'rd' MMM");
            else
                sdf = new SimpleDateFormat("EEEE, d'th' MMM");

        }catch (Exception e){
            Log.e(TAG,"Exception cought while converting time : "+e.toString());
        }

        formatedstring = sdf.format(new Date(longUnix*1000L));
        return formatedstring;



    }

}
