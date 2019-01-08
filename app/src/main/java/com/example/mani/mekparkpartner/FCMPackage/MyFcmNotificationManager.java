package com.example.mani.mekparkpartner.FCMPackage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.example.mani.mekparkpartner.R;

public class MyFcmNotificationManager {

    private Context mCtx;
    public static final int NOTIFICATION_ID = 234;

    public MyFcmNotificationManager(Context context){
        this.mCtx = context;
    }

    public void showNotification(String from, String notification, Intent intent){
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mCtx,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder =  new NotificationCompat.Builder(mCtx);
        Notification mNotification = builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setContentTitle(from)
                .setContentText(notification)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(),R.mipmap.ic_launcher))
                .build();

        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,mNotification);
    }

}
