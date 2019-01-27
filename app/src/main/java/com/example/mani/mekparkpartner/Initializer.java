package com.example.mani.mekparkpartner;

import android.app.Application;

import net.gotev.uploadservice.UploadService;

public class Initializer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // setup the broadcast action namespace string which will
        // be used to notify upload status.
        // Gradle automatically generates proper variable as below.
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

    }

}
