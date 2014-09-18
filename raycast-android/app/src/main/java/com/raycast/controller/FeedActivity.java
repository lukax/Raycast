package com.raycast.controller;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.raycast.R;
import com.raycast.controller.base.RaycastBaseActivity;
import com.raycast.domain.Message;
import com.raycast.service.MessageService;
import com.raycast.util.Preferences;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FeedActivity extends RaycastBaseActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, MessageWriteDialogFragment.MessageWriteDialogListener {
    public final static String EXTRA_MESSAGEDETAIL_MESSAGEID = "com.raycast.messagedetail.messageid";

    LocationClient locationClient;
    Location myLocation;
    float myFeedRadius;

    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        locationClient = new LocationClient(this, this, this);
        Button msgWriteBtn = (Button) findViewById(R.id.feed_messagewrite);
        msgWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an instance of the dialog fragment and show it
                Bundle dialogArgs = new Bundle();
                dialogArgs.putString(MessageWriteDialogFragment.ARGUMENT_USERID, "54051e25a3d4380200c795d2");
                dialogArgs.putParcelable(MessageWriteDialogFragment.ARGUMENT_MYLOCATION, myLocation);
                DialogFragment dialog = new MessageWriteDialogFragment();
                dialog.setArguments(dialogArgs);
                dialog.show(getFragmentManager(), "MessageWriteDialog");
            }
        });
        if (!ImageLoader.getInstance().isInited()) {
            RaycastApp.initImageLoader(this);
        }
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_action_refresh)
                .showImageForEmptyUri(R.drawable.ic_plusone_small_off_client)
                .showImageOnFail(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
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
        switch(item.getItemId()){
            case R.id.action_feed_refresh:
                new HttpRequestTask().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        //Get List of messages async
        new HttpRequestTask().execute();
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
            Toast.makeText(this, String.valueOf(connectionResult.getErrorCode()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishedDialog() {
        new HttpRequestTask().execute();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, List<Message>> {
        @Override
        protected List<Message> doInBackground(Void... params) {
            //Get message within 100000 radius
            return new MessageService().list(myLocation, myFeedRadius);
        }

        @Override
        protected void onPostExecute(List<Message> message) {
            if (message == null) {
                //TODO: get message string from 'strings'
                Toast.makeText(getApplicationContext(), "Error while loading messages", Toast.LENGTH_SHORT).show();
            } else if (message.size() == 0) {
                //TODO: get message string from 'strings'
                Toast.makeText(getApplicationContext(), "No new messages!", Toast.LENGTH_SHORT).show();
            } else {
                //Build ListView in here so it doesn't block the UI because doInBackground() takes too long to complete
                final ListView listView = (ListView) findViewById(R.id.feed);
                final FeedAdapter feedAdapter = new FeedAdapter(listView.getContext(), R.layout.message_compact, message);
                listView.setAdapter(feedAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final Message msg = (Message) adapterView.getItemAtPosition(i);
                        Intent msgDetailIntent = new Intent(FeedActivity.this, MessageDetailActivity.class);
                        msgDetailIntent.putExtra(EXTRA_MESSAGEDETAIL_MESSAGEID, msg.getId());
                        startActivity(msgDetailIntent);
                        //TODO: Load MessageActivity or Popup and populate it with item data.
                    }
                });
            }
        }
    }

    private class FeedAdapter extends ArrayAdapter<Message> {
        private HashMap<Message, Integer> idMap = new HashMap<Message, Integer>();
        private final Context context;
        private final List<Message> messages;

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        public FeedAdapter(Context context, int textViewResourceId, List<Message> messages) {
            super(context, textViewResourceId, messages);
            this.context = context;
            this.messages = messages;
            for (int i = 0; i < messages.size(); i++) {
                idMap.put(messages.get(i), i);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.message_compact, parent, false);

            ImageView profileImage = (ImageView) rowView.findViewById(R.id.profile_image);

            TextView name = (TextView) rowView.findViewById(R.id.message_creator);
            TextView content = (TextView) rowView.findViewById(R.id.message_content);
            TextView distance = (TextView) rowView.findViewById(R.id.message_distance);

            ImageLoader.getInstance().displayImage(messages.get(position).getAuthor().getImage(), profileImage, options, animateFirstListener);

            name.setText(messages.get(position).getAuthor().getName());

            content.setText(messages.get(position).getMessage());

            Location messageLocation = messages.get(position).getLocation().toLocation();
            //TODO: there are better ways to do it
            distance.setText(String.format("%.1f", messageLocation.distanceTo(myLocation) / 1000) + " km");

            return rowView;
        }

        @Override
        public long getItemId(int position) {
            Message item = getItem(position);
            return idMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
