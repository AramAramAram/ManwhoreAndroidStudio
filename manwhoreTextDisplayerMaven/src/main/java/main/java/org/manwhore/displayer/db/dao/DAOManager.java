/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.db.dao;

import com.google.inject.Inject;

/**
 *
 * @author sigi
 */
public class DAOManager implements IDAOManager {
    @Inject
    private IMessageDAO messageDAO;
    
    @Inject
    private IConversationDAO conversationDAO;

    public IConversationDAO getConversationDAO() {
        return conversationDAO;
    }

    public IMessageDAO getMessageDAO() {
        return messageDAO;
    }
    
}
