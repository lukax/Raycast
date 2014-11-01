package com.raycast.event;

import com.raycast.domain.Comment;
import com.raycast.domain.Message;
import com.raycast.event.base.RaycastEvent;

import java.util.List;

/**
 * Created by Lucas on 31/10/2014.
 */
public class MessageDetailFetchedEvent extends RaycastEvent {

    private Message message;
    private List<Comment> comments;

    public MessageDetailFetchedEvent(){
    }

    public MessageDetailFetchedEvent(Message message, List<Comment> comments) {
        this.message = message;
        this.comments = comments;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
