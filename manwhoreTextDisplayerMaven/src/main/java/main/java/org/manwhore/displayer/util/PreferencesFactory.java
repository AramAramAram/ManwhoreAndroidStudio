/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 * @author sigi
 */
@Singleton
public class PreferencesFactory {
    @Inject
    protected Application context;
    
    public SharedPreferences get() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
