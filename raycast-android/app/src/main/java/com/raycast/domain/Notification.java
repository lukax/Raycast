package com.raycast.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raycast.domain.base.AbstractEntity;

/**
 * Created by lucas on 10/27/14.
 */
public class Notification extends AbstractEntity {
    private String userId;
    private String messageId;
    //TODO: change to enum
    private String type;
    @JsonProperty(required = false)
    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
