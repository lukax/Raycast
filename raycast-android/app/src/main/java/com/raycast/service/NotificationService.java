package com.raycast.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.UiThread;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lucas on 10/27/14.
 */
@EService
public class NotificationService extends Service {
    private String TAG = "NotificationService";
    public static final int INTERVAL = 20000;
    private Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notifyUser("Raycast notification service started!");
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                doStuff();
            }
        }, 0, INTERVAL);
    }

    void doStuff(){
        notifyUser("Raycast periodic task");
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    @UiThread
    void notifyUser(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
