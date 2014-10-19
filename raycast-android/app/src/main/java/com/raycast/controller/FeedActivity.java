package com.raycast.controller;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.raycast.R;
import com.raycast.controller.base.RaycastBaseActivity;
import com.raycast.domain.Message;
import com.raycast.service.base.RaycastRESTClient;
import com.raycast.util.CachedImageLoader;
import com.raycast.util.CachedImageLoader_;
import com.raycast.util.FormatUtil;
import com.raycast.util.Preferences;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.web.client.RestClientException;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@EActivity(R.layout.activity_feed)
public class FeedActivity extends RaycastBaseActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,LocationListener, MessageWriteDialogFragment.MessageWriteDialogListener {

    @RestService RaycastRESTClient raycastRESTClient;

    @Bean MessageFeedAdapter messageFeedAdapter;
    @Bean FormatUtil formatUtil;
    @Bean CachedImageLoader loader;

    @ViewById(R.id.feed) ListView feed;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Make refresh invisible if there's no location available yet
        menu.findItem(R.id.action_feed_refresh).setVisible(myLocation != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_feed_refresh:
                listMessages(true);
                break;
        }
        return super.onOptionsItemSelected(item);
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
        if(reload || messages == null){
            try {

                    messages = raycastRESTClient.getMessages(myLocation.getLatitude(), myLocation.getLongitude(), myFeedRadius);
            }catch(RestClientException ex){
                notifyUser("Error while loading messages");
                return;
            }
            if (messages.size() == 0) {
                //TODO: get message string from 'strings'
                notifyUser("No new messages!");
            } else {
                listMessagesUI();
            }
        }
    }

    @UiThread
    void listMessagesUI(){
        messageFeedAdapter.bind(messages);
        messageFeedAdapter.setMyLocation(myLocation);

        feed.setAdapter(messageFeedAdapter);
        feed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Message msg = (Message) adapterView.getItemAtPosition(i);

                MessageDetailActivity_.intent(FeedActivity.this).extra(MessageDetailActivity.EXTRA_MESSAGEDETAIL_MESSAGEID, msg.getId()).start();
                //TODO: Load MessageActivity or Popup and populate it with item data.
            }
        });
    }

    @UiThread
    void notifyUser(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

@EBean
class MessageFeedAdapter extends BaseAdapter {

    @RootContext Context context;

    Location myLocation;
    List<Message> messages;

    public void bind(List<Message> messages) {
        this.messages = messages;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageFeedItemView messageItem;

        if (convertView == null) {
            messageItem = MessageFeedItemView_.build(context);
        } else {
            messageItem = (MessageFeedItemView) convertView;
        }

        messageItem.setLocation(myLocation);
        messageItem.bind(getItem(position));

        return messageItem;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public int getCount() {
        return messages.size();
    }
}

@EViewGroup(R.layout.item_message)
class MessageFeedItemView extends RelativeLayout {

    @Bean FormatUtil formatUtil;
    @Bean CachedImageLoader loader;

    @ViewById(R.id.message_image) ImageView profileImage;
    @ViewById(R.id.message_author) TextView name;
    @ViewById(R.id.message_content) TextView content;
    @ViewById(R.id.message_distance) TextView distance;
    @ViewById(R.id.message_time) TextView time;

    Location myLocation;
    DisplayImageOptions options;
    ImageLoadingListener animateFirstListener;

    @AfterInject
    void afterInjection() {
        options = loader.getImageDisplayOptions();
        animateFirstListener = loader.getAnimateFirstListener();
    }

    public MessageFeedItemView(Context context) {
        super(context);
    }

    public void setLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    public void bind(Message message) {
        ImageLoader.getInstance().displayImage(message.getAuthor().getImage(), profileImage, options, animateFirstListener);

        name.setText(message.getAuthor().getName());
        content.setText(message.getMessage());
        distance.setText(calculateMessageDistanceFromMyLocation(message));
        time.setText(formatUtil.dateFormat.format(message.getTime()));
    }

    private String calculateMessageDistanceFromMyLocation(Message message) {
        Location messageLocation = message.getLocation().toLocation();
        double distanceInKm = messageLocation.distanceTo(myLocation) / 1000.0;

        return formatUtil.rayFormat.format(distanceInKm) + " km";
    }
}