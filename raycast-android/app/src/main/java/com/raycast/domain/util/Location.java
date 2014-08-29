package com.raycast.domain.util;

import com.raycast.domain.util.Coordinates;

/**
 * Created by Lucas on 29/08/2014.
 */
public class Location {
    private String type;
    private Coordinates coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
