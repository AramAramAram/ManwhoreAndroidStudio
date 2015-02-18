/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer;

import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import com.google.inject.Module;
import org.manwhore.displayer.db.DBModule;
import org.manwhore.displayer.export.ExportModule;
import org.manwhore.displayer.ui.UIModule;
import org.manwhore.displayer.util.UtilModule;
import java.util.List;
import java.util.TimeZone;
import org.manwhore.displayer.logging.BreadCrumbs;
import org.manwhore.displayer.util.Constants;
import org.manwhore.displayer.webservice.WebserviceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.application.RoboApplication;

/**
 *
 * @author sigi
 */
public class DisplayerApplication extends RoboApplication {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DisplayerApplication.class);

    @Override
    public void onCreate() {
        super.onCreate();
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String user = prefs.getString(Constants.SETTINGS_USER, "Undefined");
        Boolean loggingEnabled = prefs.getBoolean(Constants.SETTINGS_SEND_LOGS, false);
        
        BreadCrumbs.setUserName(user);
        BreadCrumbs.setRemoteEnabled(loggingEnabled);
        
        String versionNumber;
        try {
            String pkg = getPackageName();
            versionNumber = getPackageManager().getPackageInfo(pkg, 0).versionName;
        } catch (NameNotFoundException e) {
            versionNumber = "?";
        }
        boolean keyboardPresent = (getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);
        LOGGER.info("Starting ManwhoreTextDisplayer version {}", versionNumber);
        LOGGER.info("OS Version: {} ({})", System.getProperty("os.version"), android.os.Build.VERSION.INCREMENTAL);
        LOGGER.info("OS API Level: {}", android.os.Build.VERSION.SDK);
        LOGGER.info("Device: {}", android.os.Build.DEVICE);
        LOGGER.info("Model (and Product): {} ({})", android.os.Build.MODEL, android.os.Build.PRODUCT);
        LOGGER.info("Contains hardware keyboard: {}", keyboardPresent);
        LOGGER.info("Timezone: {}, Offset: {}", TimeZone.getDefault().getDisplayName(), TimeZone.getDefault().getOffset(System.currentTimeMillis()));
    }
    
    

    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new DBModule());
        modules.add(new UIModule());
        modules.add(new UtilModule());
        modules.add(new ExportModule());
        modules.add(new WebserviceModule());
    }    
}
