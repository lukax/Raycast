package com.raycast.util;

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
}
