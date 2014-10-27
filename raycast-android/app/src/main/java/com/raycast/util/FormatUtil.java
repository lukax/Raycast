package com.raycast.util;

import android.location.Location;

import com.raycast.domain.Comment;
import com.raycast.domain.Message;

import org.androidannotations.annotations.EBean;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lucas on 12/10/2014.
 */
@EBean
public class FormatUtil {
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM HH:mm");
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
        DateTime now = new DateTime();
        DateTime messageTime = new DateTime(message.getTime());

        Duration timeDiff = new Duration(messageTime, now);

        if (timeDiff.getStandardMinutes() < 60) {
            return  timeDiff.getStandardMinutes() + "m";
        } else if (timeDiff.getStandardHours() < 24) {
            return timeDiff.getStandardHours() + "h";
        } else if (timeDiff.getStandardDays() < 7) {
            return timeDiff.getStandardDays() + "d";
        } else {
            String date = dateFormat.format(message.getTime()); // i.e. 17/out 22:59

            return date.replace(date.substring(3, 4), date.substring(3, 4).toUpperCase()); // i.e 17/Out 22:59
        }
    }

    public String formatDate(Comment comment) {
        DateTime now = new DateTime();
        DateTime commentTime = new DateTime(comment.getTime());

        Duration timeDiff = new Duration(commentTime, now);

        if (timeDiff.getStandardMinutes() < 60) {
            return  timeDiff.getStandardMinutes() + "m";
        } else if (timeDiff.getStandardHours() < 24) {
            return timeDiff.getStandardHours() + "h";
        } else if (timeDiff.getStandardDays() < 7) {
            return timeDiff.getStandardDays() + "d";
        } else {
            String date = dateFormat.format(comment.getTime()); // i.e. 17/out 22:59

            return date.replace(date.substring(3, 4), date.substring(3, 4).toUpperCase()); // i.e 17/Out 22:59
        }
    }
}
