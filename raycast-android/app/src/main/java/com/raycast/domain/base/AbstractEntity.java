package com.raycast.domain.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Lucas on 29/08/2014.
 */

//Found no use to have the IP field on this client, server perhaps should NOT return the field as default
@JsonIgnoreProperties({"ip"})
public class AbstractEntity {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
