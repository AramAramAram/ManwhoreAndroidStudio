/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.util;

import android.app.Application;
import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used for adjusting incoming time for 
 * incoming mesages.
 * @author sigi
 */
@Singleton
public class MessageAdjuster {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAdjuster.class);
    
    private static final Integer HOUR_IN_MILLIS = Integer.valueOf(3600000);
    
    @Inject
    private PreferencesFactory prefProvider;
    
    @Inject
    private Application context;
    
    public int getAdjustment() {
        SharedPreferences preferences = prefProvider.get();
        
        boolean adjust = preferences.getBoolean(Constants.SETTINGS_ADJUST, false);
        Integer value = Integer.valueOf(0);
        
        if (adjust) {
            //string, because integer arrays are not supported
            /*
            String stringValue = preferences.getString(Constants.SETTINGS_ADJUST_VALUE, "0");
            try {
                value = Integer.parseInt(stringValue);
            } catch (NumberFormatException nfe) {
                value = Integer.valueOf(0);
            } 
             * 
             */
            
            TimeZone tz = TimeZone.getDefault();
            LOGGER.info("Adjusting message time. Timezone: {}, Offset: {}", tz.getDisplayName(), tz.getOffset(System.currentTimeMillis()));            
            return tz.getOffset(System.currentTimeMillis());
        }
        return Integer.valueOf(0);
    }    
}
