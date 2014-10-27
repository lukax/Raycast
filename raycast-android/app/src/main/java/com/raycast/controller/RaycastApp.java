package com.raycast.controller;

import android.app.Application;

import com.raycast.service.NotificationService_;

/**
 * Created by Thiago on 14/09/2014.
 */
public class RaycastApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        startNotificationService();
    }

    void startNotificationService(){
        NotificationService_.intent(this).start();
    }
}
