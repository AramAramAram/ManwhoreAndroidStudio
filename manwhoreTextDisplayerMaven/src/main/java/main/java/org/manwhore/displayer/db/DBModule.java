/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.db;

import com.google.inject.AbstractModule;
import org.manwhore.displayer.db.dao.DAOManager;
import org.manwhore.displayer.db.dao.IConversationDAO;
import org.manwhore.displayer.db.dao.IDAOManager;
import org.manwhore.displayer.db.dao.IMessageDAO;
import org.manwhore.displayer.db.dao.impl.ConversationDAO;
import org.manwhore.displayer.db.dao.impl.MessageDAO;

/**
 *
 * @author sigi
 */
public class DBModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IConversationDAO.class).to(ConversationDAO.class);
        bind(IMessageDAO.class).to(MessageDAO.class);
        bind(IDAOManager.class).to(DAOManager.class);
    }

}
