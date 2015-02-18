/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.webservice;

import android.app.Application;
import com.google.inject.Inject;
import org.manwhore.displayer.util.ObjectProvider;
import org.manwhore.displayer.util.PreferencesFactory;

/**
 *
 * @author sigi
 */
public class WSManagerProvider implements ObjectProvider <IWSManager>{

    @Inject
    protected Application context;
    
    @Inject
    protected PreferencesFactory prefsProvider;
    
    public IWSManager get() throws Exception {
        return new WSManager(context, prefsProvider.get());
    }
    
}
