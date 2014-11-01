package com.raycast.event;

import com.raycast.domain.Message;
import com.raycast.event.base.RaycastEvent;

import java.util.List;

/**
 * Created by lucas on 10/29/14.
 */
public class MessagesFetchedEvent extends RaycastEvent {
    private List<Message> messages;

    public MessagesFetchedEvent(){

    }

    public MessagesFetchedEvent(List<Message> messages){
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

}
