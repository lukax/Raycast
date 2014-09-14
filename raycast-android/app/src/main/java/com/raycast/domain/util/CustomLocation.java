package com.raycast.domain.util;

/**
 * Created by Lucas on 29/08/2014.
 */
public class CustomLocation {
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

    public android.location.Location toAndroidLocation(){
        android.location.Location androidLocation = new android.location.Location("");
        androidLocation.setLongitude(getCoordinates().getLongitude());
        androidLocation.setLatitude(getCoordinates().getLatitude());
        return androidLocation;
    }
}
