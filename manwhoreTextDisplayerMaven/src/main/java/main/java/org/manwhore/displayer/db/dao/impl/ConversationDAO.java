/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.db.dao.impl;

import org.manwhore.displayer.export.ExportDescriptor;
import org.manwhore.displayer.util.IPersonResolver;
import android.app.Application;
import com.google.inject.Singleton;
import com.google.inject.Inject;
import org.manwhore.displayer.db.dao.*;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import org.manwhore.displayer.db.DbOpenHelper;
import android.database.sqlite.SQLiteOpenHelper;
import org.manwhore.displayer.db.Tables;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import org.manwhore.displayer.db.entity.Conversation;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import static org.manwhore.displayer.util.Constants.*;

/**
 *
 * @author sigi
 */
@Singleton
public class ConversationDAO implements IConversationDAO {


    private Application context;
    
    @Inject
    private IPersonResolver personResolver;

    @Inject
    public ConversationDAO(Application context) {
        this.context = context;
    }
        
    
    
    /**
     * Returns a list of conversations according to supplied filter.
     * 
     * @param context 
     * @param filter filter to apply
     * @return
     */
    public Cursor getCursor() {
        ContentResolver cr = context.getContentResolver();

        String[] projection = new String[]{CONVERSATION_THREAD_ID + " as " + _ID, CONVERSATION_MSG_COUNT, "sms.address as address", "group_date as date"};
        
        Cursor cursor = cr.query(Uri.parse(URI_SMS_CONVERSATIONS), projection, null, null, "date DESC");

        return cursor;
    }

    public Conversation convert(Cursor cursor) {
        Integer indexThreadId = cursor.getColumnIndex(_ID);
        Integer indexMsgCount = cursor.getColumnIndex(CONVERSATION_MSG_COUNT);
        Integer indexAddress = cursor.getColumnIndex(CONVERSATION_ADDRESS);
        
        Long threadId = cursor.getLong(indexThreadId);
        Integer msgCount = cursor.getInt(indexMsgCount);
        String address = cursor.getString(indexAddress);
            
        Conversation conversation = new Conversation();
        
        conversation.setAddress(address);
        conversation.setMsgCount(msgCount);
        conversation.setThreadId(threadId);

        return conversation;
    }

    public List<Conversation> getAllAsList() {
        List<Conversation> result = new LinkedList<Conversation>();

        Iterator<Conversation> it = getAll();
        while (it.hasNext()) {
            result.add(it.next());
        }

        return result;
    }

    public Iterator<Conversation> getAll() {
        return new ConversationIterator(getCursor());
    }
    
    
    public void saveAsSubmitted(Conversation conversation, String hash, String title, String personName, long lastDate)
    {
        SQLiteOpenHelper soh = new DbOpenHelper(context);
        SQLiteDatabase db = soh.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Tables.Conversations.COL_ADDRESS, personResolver.getKeyAddressPart(conversation.getAddress()));
        values.put(Tables.Conversations.COL_CONVHASH, hash);
        values.put(Tables.Conversations.COL_TITLE, title);
        values.put(Tables.Conversations.COL_PERSON, personName);
        values.put(Tables.Conversations.COL_LASTDATE, lastDate);


        if (getSubmittedHash(conversation) == null) // new record
                db.insert(Tables.Conversations.TABLE_NAME, null, values);
        else
                db.update(Tables.Conversations.TABLE_NAME, values, Tables.Conversations.COL_ADDRESS + " = ?", 
                        new String[]{personResolver.getKeyAddressPart(conversation.getAddress())});

        db.close();
    }

    public Map<String, Object> getSubmittedData(Conversation conversation)
    {
        HashMap<String, Object> result = new HashMap<String, Object>();

        SQLiteOpenHelper soh = new DbOpenHelper(context);
		SQLiteDatabase db = soh.getWritableDatabase();

        String selection = Tables.Conversations.COL_ADDRESS + " = ?";
        String[] args = new String[] {personResolver.getKeyAddressPart(conversation.getAddress())};
        String[] cols = new String[] {Tables.Conversations.COL_CONVHASH, Tables.Conversations.COL_TITLE, Tables.Conversations.COL_ADDRESS, Tables.Conversations.COL_PERSON, Tables.Conversations.COL_LASTDATE};
        Cursor cursor = db.query(Tables.Conversations.TABLE_NAME, cols, selection, args, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst())
            {
                int indexHash = cursor.getColumnIndex(Tables.Conversations.COL_CONVHASH);
                int indexTitle = cursor.getColumnIndex(Tables.Conversations.COL_TITLE);
                int indexAddress = cursor.getColumnIndex(Tables.Conversations.COL_ADDRESS);
                int indexPerson = cursor.getColumnIndex(Tables.Conversations.COL_PERSON);
                int indexLD = cursor.getColumnIndex(Tables.Conversations.COL_LASTDATE);

                result.put(Tables.Conversations.COL_CONVHASH, cursor.getString(indexHash));
                result.put(Tables.Conversations.COL_TITLE, cursor.getString(indexTitle));
                result.put(Tables.Conversations.COL_ADDRESS, cursor.getString(indexAddress));
                result.put(Tables.Conversations.COL_PERSON, cursor.getString(indexPerson));
                result.put(Tables.Conversations.COL_LASTDATE, cursor.getLong(indexLD));						
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        db.close();

        return result;
    }

    public String getSubmittedHash(Conversation conversation)
    {
        Map<String, Object> data = getSubmittedData(conversation);

        return (String) data.get(Tables.Conversations.COL_CONVHASH);
    }

    public void loadSubmittedData(ExportDescriptor descriptor) {
        //conversation can potentially be null
        if (descriptor.getConversation() != null) {
            Map<String, Object> data = getSubmittedData(descriptor.getConversation());

            if (data != null && !data.isEmpty()) {
                descriptor.setHash((String) data.get(Tables.Conversations.COL_CONVHASH));
                descriptor.setTitle((String) data.get(Tables.Conversations.COL_TITLE));
                descriptor.setPersonName((String) data.get(Tables.Conversations.COL_PERSON));
                descriptor.setLastExportDate((Long) data.get(Tables.Conversations.COL_LASTDATE));            
            }
        }
    }

    private class ConversationIterator implements Iterator<Conversation> {

        private Cursor cursor;

        public ConversationIterator(Cursor cursor) {
            this.cursor = cursor;
        }

        public boolean hasNext() {
            return !cursor.isAfterLast();
        }

        public Conversation next() {
            cursor.moveToNext();
            return convert(cursor);
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
