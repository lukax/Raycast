package com.raycast.controller;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.raycast.R;
import com.raycast.domain.Message;
import com.raycast.service.MessageService;

public class MessageDetailActivity extends Activity {
    String messageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        messageId = getIntent().getStringExtra(FeedActivity.EXTRA_MESSAGEDETAIL_MESSAGEID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new HttpRequestTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_detail, menu);
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

    private class HttpRequestTask extends AsyncTask<Void, Void, Message> {
        @Override
        protected Message doInBackground(Void... params) {
            return new MessageService().get(messageId);
        }

        @Override
        protected void onPostExecute(Message message) {
            if (message == null) {
                //TODO properly show an error
                Log.e(getClass().toString(), "couldn't get message");
            }

            TextView msgText = (TextView) findViewById(R.id.messagedetail_message);
            msgText.setText(message.getMessage());
        }
    }
}
