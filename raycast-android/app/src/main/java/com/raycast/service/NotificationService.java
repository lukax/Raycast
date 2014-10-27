package com.raycast.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.raycast.R;
import com.raycast.controller.FeedActivity_;
import com.raycast.domain.Notification;
import com.raycast.service.base.RaycastRESTClient;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.rest.RestService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lucas on 10/27/14.
 */
@EService
public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static final int INTERVAL = 60000;
    private Timer timer = new Timer();
    @SystemService NotificationManager notificationManager;
    @RestService RaycastRESTClient restClient;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                doPeriodically();
            }
        }, 0, INTERVAL);
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    @Background
    void doPeriodically(){
        try {
            List<Notification> userNotifications = restClient.getUserNotifications();
            notifyUser(userNotifications);
        } catch(Exception ex){
            Log.d(TAG, "couldn't get notification list", ex);
            stopSelf();
        }
    }

    @UiThread
    void notifyUser(List<Notification> notifications){
        PendingIntent pendingIntent = PendingIntent.getActivity(this, PendingIntent.FLAG_UPDATE_CURRENT, FeedActivity_.intent(this).get(), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Raycast")
                .setContentText("Você tem " + notifications.size() + " notificações não lidas")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent);
        notificationManager.notify(0, builder.build());
    }
}
