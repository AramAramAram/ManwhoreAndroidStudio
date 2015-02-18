/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.logging;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.manwhore.displayer.util.Constants;

/**
 *
 * @author sigi
 */
public class BreadCrumbs {
    private static String userName;
    private static boolean remoteEnabled;

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        BreadCrumbs.userName = userName;
    }

    public static boolean isRemoteEnabled() {
        return remoteEnabled;
    }

    public static void setRemoteEnabled(boolean remoteEnabled) {
        BreadCrumbs.remoteEnabled = remoteEnabled;
    }
    
    /**
    * Initializes the logger - needs to be run on every resume or create, because the 
    * settings could have changed.
    */
    public static void initBreadCrumbs(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String user = prefs.getString(Constants.SETTINGS_USER, "Undefined");
        Boolean loggingEnabled = prefs.getBoolean(Constants.SETTINGS_SEND_LOGS, false);

        BreadCrumbs.setUserName(user);
        BreadCrumbs.setRemoteEnabled(loggingEnabled);
    }
}
