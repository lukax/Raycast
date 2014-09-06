package com.raycast.controller;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.raycast.R;
import com.raycast.domain.Message;
import com.raycast.domain.util.Coordinates;
import com.raycast.service.ImageLoader;
import com.raycast.service.MessageService;
import com.raycast.service.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new HttpRequestTask().execute();
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

    private class HttpRequestTask extends AsyncTask<Void, Void, List<Message>> {
        @Override
        protected List<Message> doInBackground(Void... params) {
            Coordinates c = new Coordinates();
            c.setLongitude(-43.417882);
            c.setLatitude(-22.885069);
            double r = 10000;
            return new MessageService().list(c, r);
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

            ImageLoader loader = new ImageLoader(messages.get(position).getAuthor().getImage(), profileImage);
            loader.execute(null, null);

            Tracker tracker = new Tracker(rowView.getContext());

            Location messageLocation = new Location("");
            messageLocation.setLongitude(messages.get(position).getLocation().getCoordinates().getLongitude());
            messageLocation.setLatitude(messages.get(position).getLocation().getCoordinates().getLatitude());

            Location myLocation = new Location("");

            if (tracker.canGetLocation()) {
                myLocation = tracker.getLocation();
            }

            name.setText(messages.get(position).getAuthor().getName());
            content.setText(messages.get(position).getMessage());
            //TODO: there are better ways to do it
            distance.setText(String.format("%.1f", messageLocation.distanceTo(myLocation) /1000) + " km");


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
