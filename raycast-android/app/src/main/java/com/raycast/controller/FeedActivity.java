package com.raycast.controller;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.raycast.R;
import com.raycast.domain.Message;
import com.raycast.domain.util.Coordinates;
import com.raycast.service.ImageLoader;
import com.raycast.service.MessageService;
import com.raycast.service.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    LocationClient locationClient;
    Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        locationClient = new LocationClient(this, this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationClient.connect();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        myLocation = locationClient.getLastLocation();
        if(myLocation != null){
            Log.d("FeedActivity", "Lat/Long received" + myLocation.getLatitude() + "/" + myLocation.getLongitude());
        }
        else{
            Log.e("FeedActivity", "Couln't get Location, falling back to default coordinates");
            //Fallback to Rio de Janeiro Center coordinates
            myLocation = new Location("");
            myLocation.setLatitude(-22.9082998);
            myLocation.setLongitude(-43.1970773);
        }
        //Get List of messages async
        new HttpRequestTask().execute(myLocation);
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

    private class HttpRequestTask extends AsyncTask<Location, Void, List<Message>> {
        @Override
        protected List<Message> doInBackground(Location... params) {
            //Get message within 100000 radius
            return new MessageService().list(params[0], 100000);
        }

        @Override
        protected void onPostExecute(List<Message> message) {
            if(message == null){
                //TODO: get message string from 'strings'
                Toast.makeText(getApplicationContext(), "Error while loading messages", Toast.LENGTH_SHORT).show();
            }
            else if(message.size() == 0){
                //TODO: get message string from 'strings'
                Toast.makeText(getApplicationContext(), "No new messages!", Toast.LENGTH_SHORT).show();
            }
            else {
                //Build ListView in here so it doesn't block the UI because doInBackground() takes too long to complete
                final ListView listView = (ListView) findViewById(R.id.feed);
                final FeedAdapter feedAdapter = new FeedAdapter(listView.getContext(), R.layout.message_compact, message);
                listView.setAdapter(feedAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final Message item = (Message) adapterView.getItemAtPosition(i);
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
            new ImageLoader(messages.get(position).getAuthor().getImage(), profileImage).execute(null, null);
            name.setText(messages.get(position).getAuthor().getName());
            content.setText(messages.get(position).getMessage());
            Location messageLocation = messages.get(position).getLocation().toAndroidLocation();
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
}
