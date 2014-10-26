package com.raycast.util;

import android.location.Location;

import com.raycast.domain.Message;

import org.androidannotations.annotations.EBean;
import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lucas on 12/10/2014.
 */
@EBean
public class FormatUtil {
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
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

    public String formatDate(Message message) {
        Date messageDate = message.getTime();

        if (new DateTime(messageDate).toLocalDate().equals(new DateTime().toLocalDate())) {
            return new SimpleDateFormat("HH:mm").format(messageDate);
        }

        if (new DateTime(messageDate).toLocalDate().equals(new DateTime().minusDays(1).toLocalDate())) {
            return "1d";
        }

        return dateFormat.format(messageDate);
    }
}
