package com.raycast.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Thiago on 29/08/2014.
 */
public final class Tracker implements LocationListener {

    private final Context myContext;

    // Tracker flags
    public boolean isGPSTrackerEnable = false;
    public boolean isNetworkTrackerEnable = false;
    public boolean canGetLocation = false;

    // Position flags
    Location currentLocation;
    double latitude;
    double longitude;

    // Update location request flags
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BETWEEN_UPDATES = 1; // 1 minute

    LocationManager myLocationManager;

    public Tracker(Context context) {
        this.myContext = context;
        getLocation();
    }

    /**
     * Gets the current location.
     *
     * @return Most recent location.
     */
    public Location getLocation() {
        try {
            myLocationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);

            if (canGetLocation()) {
                if (isNetworkTrackerEnable) {
                    currentLocation = null;
                    myLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (myLocationManager != null) {
                        currentLocation = myLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (currentLocation != null) {
                            latitude = currentLocation.getLatitude();
                            longitude = currentLocation.getLongitude();
                        }
                    }
                    return currentLocation;
                }

                if (isGPSTrackerEnable) {
                    currentLocation = null;
                    if (currentLocation == null) {
                        myLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BETWEEN_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (myLocationManager != null) {
                            currentLocation = myLocationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (currentLocation != null) {
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();
                            }
                        }
                    }
                    return currentLocation;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentLocation;
    }

    /**
     * Gets the latitude.
     *
     * @return latitude
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Gets the longitude.
     *
     * @return longitude
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Checks if GPS or Wifi are enabled.
     *
     * @return enabled/disabled
     */
    public boolean canGetLocation() {
        // Checks if providers are enabled.
        isGPSTrackerEnable = myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkTrackerEnable = myLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        this.canGetLocation = (isNetworkTrackerEnable || isGPSTrackerEnable);

        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
        this.latitude = this.currentLocation.getLatitude();
        this.longitude = this.currentLocation.getLongitude();
    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(myContext, "GPSTracking is active", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(myContext, "GPSTracking is down", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }
}
