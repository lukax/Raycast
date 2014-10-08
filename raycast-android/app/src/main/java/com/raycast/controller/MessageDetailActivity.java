package com.raycast.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.raycast.R;
import com.raycast.controller.base.RaycastBaseActivity;
import com.raycast.domain.Message;
import com.raycast.service.MessageService;
import com.raycast.service.base.AbstractCrudService;

public class MessageDetailActivity extends RaycastBaseActivity {
    String messageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        messageId = getIntent().getStringExtra(FeedActivity.EXTRA_MESSAGEDETAIL_MESSAGEID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMessage();
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
        return super.onOptionsItemSelected(item);
    }

    private void loadMessage(){
        new MessageService().get(messageId, new AbstractCrudService.ResponseListener<Message>() {
            @Override
            public void onSuccess(Message message) {
                TextView msgText = (TextView) findViewById(R.id.messagedetail_message);
                msgText.setText(message.getMessage());
            }

            @Override
            public void onFail() {
                //TODO properly show an error
                Log.e(getClass().toString(), "couldn't get message");
            }
        });
    }

}
