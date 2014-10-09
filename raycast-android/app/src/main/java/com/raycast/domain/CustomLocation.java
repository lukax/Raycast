package com.raycast.domain;

import android.location.Location;

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

    public static CustomLocation fromLocation(Location location){
        CustomLocation loc = new CustomLocation();
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(location.getLatitude());
        coordinates.setLongitude(location.getLongitude());
        loc.setCoordinates(coordinates);
        return loc;
    }

    public Location toLocation(){
        android.location.Location androidLocation = new android.location.Location("");
        androidLocation.setLongitude(getCoordinates().getLongitude());
        androidLocation.setLatitude(getCoordinates().getLatitude());
        return androidLocation;
    }
}
