/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.ui;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;
import org.manwhore.displayer.ui.adapter.ConversationAdapter;
import org.manwhore.displayer.ui.adapter.ConversationArrayAdapter;
import org.manwhore.displayer.ui.adapter.IConversationListAdapterFactory;

/**
 *
 * @author sigi
 */
public class UIModule extends AbstractModule{

    @Override
    protected void configure() {
        bind(IConversationListAdapterFactory.class).toProvider(
                FactoryProvider.newFactory(IConversationListAdapterFactory.class, 
                ConversationAdapter.class));
        
        bind(ConversationArrayAdapter.class);
    }
    
}
