package com.raycast.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Lucas on 29/08/2014.
 */

//Found no use to have the IP field on this client, server perhaps should NOT return the field as default
//Don't throw exception on fields that are not specified
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractEntity implements Serializable{
    @JsonProperty("_id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
