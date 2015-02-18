/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.util;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

/**
 *
 * @author sigi
 */
public class UtilModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IPersonResolver.class).to(PersonResolver.class);
        bind(IDelayTextFactory.class).toProvider(
                FactoryProvider.newFactory(IDelayTextFactory.class, DelayText.class));
    }
    
}
