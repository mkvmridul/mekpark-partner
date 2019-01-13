package com.example.mani.mekparkpartner.FCMPackage;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private final String TAG = "FCM";
    public static final String TOKEN_BROADCAST = "myFcmTokenBroadCast";



    @Override
    public void onTokenRefresh() {
        Log.e(TAG, "onTokennRefresh is called");
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        storeTokenInSP(refreshedToken);
        getApplication().sendBroadcast(new Intent(TOKEN_BROADCAST));

    }



    private void storeTokenInSP(String refreshedToken) {
        SharedPrefFcm.getmInstance(getApplicationContext()).storeToken(refreshedToken);
        Log.e(TAG, "token stored in sharedPref : "+SharedPrefFcm.getmInstance(getApplicationContext()).getToken());
    }

}
