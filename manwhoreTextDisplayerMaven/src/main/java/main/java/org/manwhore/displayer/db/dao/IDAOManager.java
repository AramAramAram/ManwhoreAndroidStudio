/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.db.dao;

/**
 *
 * @author sigi
 */
public interface IDAOManager {
    public IMessageDAO getMessageDAO();
    public IConversationDAO getConversationDAO();
}
