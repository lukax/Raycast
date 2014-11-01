package com.raycast.controller;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.raycast.R;
import com.raycast.controller.base.RaycastBaseActivity;
import com.raycast.controller.component.MessageFeedAdapter;
import com.raycast.domain.Message;
import com.raycast.event.MessagesFetchedEvent;
import com.raycast.service.RaycastService;
import com.raycast.util.CachedImageLoader;
import com.raycast.util.FormatUtil;
import com.raycast.util.Preferences;

import net.danlew.android.joda.JodaTimeAndroid;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import static com.raycast.controller.RaycastApp.BUS;

@EActivity(R.layout.activity_feed)
@OptionsMenu(R.menu.feed)
public class FeedActivity extends RaycastBaseActivity implements
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        MessageWriteDialogFragment.MessageWriteDialogListener {

    public static final String TAG = "FeedActivity";
    @Bean RaycastService raycastService;
    @Bean MessageFeedAdapter messageFeedAdapter;
    @Bean FormatUtil formatUtil;
    @Bean CachedImageLoader loader;
    @ViewById(R.id.feed) ListView feed;
    @ViewById(R.id.swipe_container) SwipeRefreshLayout swipeView;
    DisplayImageOptions options;
    Location myLocation;
    float myFeedRadius;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

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
    void afterViews(){
        swipeView.setColorSchemeResources(R.color.raycast_purple_main, R.color.raycast_purple_sub, R.color.raycast_purple_dark, R.color.raycast_purple_light);
        JodaTimeAndroid.init(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BUS.register(this);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myFeedRadius = Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this).getString(Preferences.FEED_RADIUS.toString(), "50000"));
        Log.d("FeedActivity", "radius " + myFeedRadius);
    }

    @Override
    protected void onStop() {
        BUS.unregister(this);
        mGoogleApiClient.disconnect();
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
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(60000); // Update location every minute
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        if (myLocation != null) {
            Log.d("FeedActivity", "Lat/Long received" + myLocation.getLatitude() + "/" + myLocation.getLongitude());
        } else {
            Log.e("FeedActivity", "Couln't get Location, falling back to default coordinates");
            //Fallback to Rio de Janeiro Center coordinates
            myLocation = new Location("");
            myLocation.setLatitude(-22.9082998);
            myLocation.setLongitude(-43.1970773);
            notifyUser("Não foi possível pegar dados do GPS :(");
        }
        raycastService.getMessages(myLocation.getLatitude(), myLocation.getLongitude(), myFeedRadius);
    }

    @Override
    public void onDisconnected() {
        Log.d("FeedActivity", "Disconnected from LocationListener");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
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
        //TODO When user closes write message dialog...
    }

    @UiThread
    public void onEvent(MessagesFetchedEvent e) {
        if (e.getMessages() == null) {
            notifyUser("Erro ao carregar mensagens :(");
            return;
        }
        else if (e.getMessages().size() == 0) {
            notifyUser("Nenhuma mensagem nova!");
            return;
        }

        messageFeedAdapter.bind(e.getMessages());
        messageFeedAdapter.setMyLocation(myLocation);
        if(feed.getAdapter() != messageFeedAdapter) {
            feed.setAdapter(messageFeedAdapter);
            swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    actionRefresh();
                }
            });
            feed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final Message msg = (Message) adapterView.getItemAtPosition(i);
                    MessageDetailActivity_.intent(FeedActivity.this).extra(MessageDetailActivity.EXTRA_MESSAGEDETAIL_MESSAGEID, msg.getId()).start();
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
        else {
            messageFeedAdapter.notifyDataSetChanged();
        }
    }

    @OptionsItem(R.id.action_refresh)
    void actionRefresh(){
        raycastService.getMessages(myLocation.getLatitude(), myLocation.getLongitude(), myFeedRadius);
        swipeView.setRefreshing(true);
        ( new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeView.setRefreshing(false);
            }
        }, 3000);
    }


    @UiThread
    void notifyUser(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

