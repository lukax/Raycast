package com.raycast.util;

/**
 * Created by Lucas on 13/09/2014.
 */
public enum Preferences {
    FEED_RADIUS("FEED_RADIUS");

    private String pref;

    Preferences(String pref) {
        this.pref = pref;
    }

    @Override
    public String toString() {
        return pref;
    }
}
