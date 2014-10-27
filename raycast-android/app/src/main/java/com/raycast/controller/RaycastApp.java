package com.raycast.controller;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
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
