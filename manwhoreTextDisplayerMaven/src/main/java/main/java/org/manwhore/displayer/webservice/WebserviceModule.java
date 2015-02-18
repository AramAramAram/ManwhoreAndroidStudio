/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.webservice;

import com.google.inject.AbstractModule;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import org.manwhore.displayer.util.ObjectProvider;

/**
 *
 * @author sigi
 */
public class WebserviceModule extends AbstractModule {

    @Override
    protected void configure() {
        ThrowingProviderBinder.create(binder())
                .bind(ObjectProvider.class, IWSManager.class).to(WSManagerProvider.class);
    }
    
}
