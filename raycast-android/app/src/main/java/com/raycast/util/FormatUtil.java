package com.raycast.util;

import android.location.Location;

import com.raycast.domain.Message;

import org.androidannotations.annotations.EBean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Lucas on 12/10/2014.
 */
@EBean
public class FormatUtil {
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM hh:mm");
    public DecimalFormat rayFormat = new DecimalFormat("#.#");

    public String calculateMessageDistanceFromMyLocation(Message message, Location location) {
        Location messageLocation = message.getLocation().toLocation();
        double distance = messageLocation.distanceTo(location);

        if (distance < 1000.0) {
            return rayFormat.format(distance) + " m";
        } else {
            return rayFormat.format(distance / 1000.0) + " km";
        }
    }
}
