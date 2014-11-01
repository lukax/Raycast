package com.raycast.controller;

import android.app.Application;

import com.raycast.service.NotificationService_;

import de.greenrobot.event.EventBus;

/**
 * Created by Thiago on 14/09/2014.
 */
public class RaycastApp extends Application{
    public static final EventBus BUS = new EventBus();

    @Override
    public void onCreate() {
        super.onCreate();
        startNotificationService();
    }

    void startNotificationService(){
        NotificationService_.intent(this).start();
    }
}
