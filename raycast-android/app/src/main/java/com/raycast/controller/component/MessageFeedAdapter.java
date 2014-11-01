package com.raycast.controller.component;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.raycast.domain.Message;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

/**
 * Created by Lucas on 20/10/2014.
 */
@EBean
public class MessageFeedAdapter extends BaseAdapter {

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
