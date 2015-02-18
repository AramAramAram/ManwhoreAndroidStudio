/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.db.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import org.manwhore.displayer.db.entity.Conversation;
import android.database.Cursor;
import org.manwhore.displayer.export.ExportDescriptor;

/**
 *
 * @author sigi
 */
public interface IConversationDAO extends ICursorDAO<Conversation> {
    
    public Cursor getCursor();

    public List<Conversation> getAllAsList();

    public Iterator<Conversation> getAll();
    
    public void saveAsSubmitted(Conversation conversation, String hash, String title, String personName, long lastDate);

    public void loadSubmittedData(ExportDescriptor descriptor);
    
}
