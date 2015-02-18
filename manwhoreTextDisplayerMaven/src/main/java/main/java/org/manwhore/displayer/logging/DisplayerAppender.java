/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.logging;

import cz.sigler.logging.remote.api.LoggingEvent;
import cz.sigler.logging.remote.appender.RemoteAppender;

/**
 *
 * @author sigi
 */
public class DisplayerAppender extends RemoteAppender {
    
    @Override
    public boolean processBreadcrumbs(LoggingEvent evt) {
        evt.setCdata1(BreadCrumbs.getUserName());
        return BreadCrumbs.isRemoteEnabled();
    }
    
}
