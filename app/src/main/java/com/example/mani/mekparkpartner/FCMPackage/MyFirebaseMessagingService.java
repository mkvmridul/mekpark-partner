package com.example.mani.mekparkpartner.FCMPackage;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMMessageing";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


        notifyUser(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());

    }

    public void notifyUser(String title ,String notification){

        MyFcmNotificationManager myFcmNotificationManager = new MyFcmNotificationManager(getApplicationContext());
        myFcmNotificationManager.createNotification(title,notification);
    }


}
