/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.ui;

import android.database.Cursor;
import org.manwhore.displayer.db.entity.Conversation;
import android.os.AsyncTask;
import android.util.Log;
import org.manwhore.displayer.db.dao.IConversationDAO;

/**
 *
 * @author sigi
 */
public class ConversationListLoadTask extends AsyncTask<Cursor, Conversation, Integer> {
    
    private IConversationDAO dao;

    public ConversationListLoadTask(IConversationDAO dao) {
        this.dao = dao;
    }
        
    @Override
    protected Integer doInBackground(Cursor... cursors) {
        Integer counter = 0;        
        
        for (Cursor cursor: cursors) {
            if (cursor == null) {
                continue;
            }
            
            while (cursor.moveToNext()) {
                Conversation conversation = dao.convert(cursor);
                publishProgress(conversation);
                counter++;
            }
            cursor.close();
        }
        
        Log.i(getClass().getSimpleName(), "Loaded " + counter + " conversations.");
        
        return counter;
    }                
}
