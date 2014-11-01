package com.raycast.event;

import com.raycast.event.base.RaycastEvent;

/**
 * Created by Lucas on 01/11/2014.
 */
public class RaycastErrorEvent extends RaycastEvent {

    private String message;

    public RaycastErrorEvent(){
    }

    public RaycastErrorEvent(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
