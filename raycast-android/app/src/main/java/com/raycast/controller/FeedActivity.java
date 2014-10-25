package com.raycast.controller;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.raycast.R;
import com.raycast.controller.base.RaycastBaseActivity;
import com.raycast.controller.component.MessageFeedAdapter;
import com.raycast.domain.Message;
import com.raycast.service.base.RaycastRESTClient;
import com.raycast.util.CachedImageLoader;
import com.raycast.util.FormatUtil;
import com.raycast.util.Preferences;

import net.danlew.android.joda.JodaTimeAndroid;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.web.client.RestClientException;

import java.util.List;

@EActivity(R.layout.activity_feed)
@OptionsMenu(R.menu.feed)
public class FeedActivity extends RaycastBaseActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, MessageWriteDialogFragment.MessageWriteDialogListener {

    public static final String TAG = "FeedActivity";

    @RestService RaycastRESTClient raycastRESTClient;

    @Bean MessageFeedAdapter messageFeedAdapter;
    @Bean FormatUtil formatUtil;
    @Bean CachedImageLoader loader;

    @ViewById(R.id.feed) ListView feed;
    @ViewById(R.id.swipe_container) SwipeRefreshLayout swipeView;

    LocationClient locationClient;
    DisplayImageOptions options;

    Location myLocation;
    float myFeedRadius;
    List<Message> messages;

    @Click(R.id.feed_messagewrite)
    void writeMessageButton() {
        Bundle dialogArgs = new Bundle();
        dialogArgs.putString(MessageWriteDialogFragment.ARGUMENT_USERID, "54051e25a3d4380200c795d2");
        dialogArgs.putParcelable(MessageWriteDialogFragment.ARGUMENT_MYLOCATION, myLocation);
        DialogFragment dialog = new MessageWriteDialogFragment_();
        dialog.setArguments(dialogArgs);
        dialog.show(getFragmentManager(), "MessageWriteDialog");
    }

    @AfterInject
    void getImageDisplayOptions() {
        options = loader.getImageDisplayOptions();
    }

    @AfterViews
    void startLocationRequests() {
        locationClient = new LocationClient(this, this, this);

        LocationRequest locationRequest = LocationRequest.create();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(300000); // 5 minutes
        locationRequest.setFastestInterval(10000); // 1 minute
    }

    @AfterViews
    void getTimeZone() {
        JodaTimeAndroid.init(getApplicationContext());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(provider, " changed to status: " + status);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(provider, " enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(provider, " disabled");
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO make pref come as a float already
        myFeedRadius = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString(Preferences.FEED_RADIUS.toString(), "50000"));
        Log.d("FeedActivity", "radius " + myFeedRadius);
    }

    @Override
    protected void onStop() {
        locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        myLocation = locationClient.getLastLocation();
        if (myLocation != null) {
            Log.d("FeedActivity", "Lat/Long received" + myLocation.getLatitude() + "/" + myLocation.getLongitude());
        } else {
            Log.e("FeedActivity", "Couln't get Location, falling back to default coordinates");
            //Fallback to Rio de Janeiro Center coordinates
            myLocation = new Location("");
            myLocation.setLatitude(-22.9082998);
            myLocation.setLongitude(-43.1970773);
        }
        //Get List of messages
        listMessages(false);
    }

    @Override
    public void onDisconnected() {
        Log.d("FeedActivity", "Disconnected from LocationListener");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 9000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            notifyUser(String.valueOf(connectionResult.getErrorCode()));
        }
    }

    @Override
    public void onFinishedDialog() {
        listMessages(true);
    }

    @Background
    void listMessages(boolean reload) {
        if(!reload && messages != null)
            return;

        swipeView.setRefreshing(true);
        try {
            messages = raycastRESTClient.getMessages(myLocation.getLatitude(), myLocation.getLongitude(), myFeedRadius);
        }catch(RestClientException ex){
            notifyUser("Erro ao carregar mensagens :(");
        }
        if (messages.size() == 0) {
            notifyUser("Nenhuma mensagem nova!");
        }
        listMessagesUI();
        swipeView.setRefreshing(false);
    }

    @UiThread
    void listMessagesUI(){
        messageFeedAdapter.bind(messages);
        messageFeedAdapter.setMyLocation(myLocation);

        feed.setAdapter(messageFeedAdapter);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listMessages(true);
            }
        });

        feed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Message msg = (Message) adapterView.getItemAtPosition(i);

                MessageDetailActivity_.intent(FeedActivity.this).extra(MessageDetailActivity.EXTRA_MESSAGEDETAIL_MESSAGEID, msg.getId()).start();
                //TODO: Load MessageActivity or Popup and populate it with item data.
            }
        });

        feed.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    swipeView.setEnabled(true);
                } else {
                    swipeView.setEnabled(false);
                }
            }
        });
    }

    @UiThread
    void notifyUser(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

