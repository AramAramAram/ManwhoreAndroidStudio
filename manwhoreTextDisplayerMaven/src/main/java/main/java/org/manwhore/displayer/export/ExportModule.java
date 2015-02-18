/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.export;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;
import org.manwhore.displayer.export.web.IWebSubmitTaskFactory;
import org.manwhore.displayer.export.web.WebSubmitTask;

/**
 *
 * @author sigi
 */
public class ExportModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IWebSubmitTaskFactory.class).toProvider(
                FactoryProvider.newFactory(IWebSubmitTaskFactory.class, WebSubmitTask.class));        
    }
    
}
