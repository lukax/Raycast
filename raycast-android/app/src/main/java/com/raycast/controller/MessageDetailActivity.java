package com.raycast.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.raycast.R;
import com.raycast.controller.base.RaycastBaseActivity;
import com.raycast.domain.Message;
import com.raycast.service.base.AbstractCrudService;
import com.raycast.service.base.RaycastRESTClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

@EActivity(R.layout.activity_message_detail)
public class MessageDetailActivity extends RaycastBaseActivity {
    public final static String EXTRA_MESSAGEDETAIL_MESSAGEID = "com.raycast.messagedetail.messageid";

    @ViewById(R.id.messagedetail_message)
    TextView msgText;
    @Extra(EXTRA_MESSAGEDETAIL_MESSAGEID)
    String messageId;

    @RestService
    RaycastRESTClient raycastRESTClient;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_detail, menu);
        return true;
    }

    @AfterViews
    void afterViews(){
        new GetMessageAsyncTask().execute(messageId);
    }

    class GetMessageAsyncTask extends AsyncTask<String, Void, Message>{
        @Override
        protected Message doInBackground(String... params) {
            Message message = raycastRESTClient.getMessageById(params[0]);
            return message;
        }
        @Override
        protected void onPostExecute(Message message) {
            msgText.setText(message.getMessage());
        }
    }
}
