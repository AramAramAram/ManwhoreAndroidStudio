/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.db.dao;

import org.manwhore.displayer.db.entity.Conversation;
import org.manwhore.displayer.util.PersonResolver;
import android.net.Uri;
import android.util.Log;
import org.manwhore.displayer.filter.Filter;
import org.manwhore.displayer.db.entity.Message;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;

import static org.manwhore.displayer.util.Constants.*;

/**
 *
 * @author sigi
 */
public interface IMessageDAO extends ICursorDAO<Message>{

    public Cursor getCursor(Conversation conversation, Filter filter);
    
}
