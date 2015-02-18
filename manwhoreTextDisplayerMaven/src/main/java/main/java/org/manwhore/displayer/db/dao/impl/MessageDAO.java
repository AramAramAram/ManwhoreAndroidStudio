/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.db.dao.impl;

import org.manwhore.displayer.util.MessageAdjuster;
import org.manwhore.displayer.util.IPersonResolver;
import com.google.inject.Inject;
import android.app.Application;
import com.google.inject.Singleton;
import org.manwhore.displayer.db.dao.*;
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
@Singleton
public class MessageDAO implements IMessageDAO {

    
    private Application context;
    
    @Inject
    private IPersonResolver personResolver;
    
    @Inject
    private MessageAdjuster adjuster;

    @Inject
    public MessageDAO(Application context) {
        this.context = context;
    }
    
    

    public Cursor getCursor(Conversation conversation, Filter filter) {
        ContentResolver cr = context.getContentResolver();
        
        String sql_incoming = "(SELECT smsinc.date + " + adjuster.getAdjustment() +" FROM sms smsinc WHERE smsinc._rowid_ = sms._rowid_ AND smsinc.type = 1) AS date_incoming";
        String sql_outgoing = "(SELECT smsout.date FROM sms smsout WHERE smsout._rowid_ = sms._rowid_ AND smsout.type = 2) AS date_outgoing";
        
        String[] projection = new String[]{_ID, SMS_ADDRESS, SMS_BODY, SMS_PERSON, SMS_TYPE, sql_incoming, sql_outgoing};
        StringBuilder selection = new StringBuilder();
        ArrayList<String> arguments = new ArrayList<String>();

        //build the selection
        selection.append("(" + SMS_TYPE + " in (1,2))"); //include both incoming and outgoing

        selection.append(" AND ").append(SMS_THREAD_ID).append(" = ?");

        arguments.add(conversation.getThreadId().toString());

        long dateFrom, dateTo;

        try {
            dateFrom = filter.getDateFromValue(context);
            dateTo = filter.getDateToValue(context);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Error getting date interval: " + e);
            dateFrom = -1;
            dateTo = -1;
        }

        if (dateFrom > -1) {
            selection.append(" AND (" + SMS_DATE + " >= ?)");
            arguments.add(String.valueOf(dateFrom));
        }
        if (dateTo > -1) {
            selection.append(" AND (" + SMS_DATE + " < ?)");
            arguments.add(String.valueOf(dateTo));
        }

        return cr.query(Uri.parse(URI_SMS), projection, selection.toString(), arguments.toArray(new String[]{}),
                "coalesce(date_incoming, date_outgoing)" + (filter.isMsgOrderDesc() ? " DESC" : " ASC"));
    }

    public Message convert(Cursor cursor) {
        int indexId = cursor.getColumnIndex(_ID);
        int indexBody = cursor.getColumnIndex(SMS_BODY);
        int indexPerson = cursor.getColumnIndex(SMS_PERSON);
        int indexAddress = cursor.getColumnIndex(SMS_ADDRESS);
        int indexDate1 = cursor.getColumnIndex("date_incoming");
        int indexDate2 = cursor.getColumnIndex("date_outgoing");
        int indexType = cursor.getColumnIndex(SMS_TYPE);
        
        Message msg = new Message();
        msg.setId(cursor.getInt(indexId));
        msg.setAddress(cursor.getString(indexAddress));        
        msg.setPersonId(cursor.getInt(indexPerson));
        msg.setPerson(personResolver.getPersonByAddress(msg.getAddress()));
        msg.setBody(cursor.getString(indexBody));

        int type = cursor.getInt(indexType);

        if (type == 1) {
            msg.setIncoming(true);
            msg.setDate(cursor.getLong(indexDate1));
        } else if (type == 2) {
            msg.setIncoming(false);
            msg.setDate(cursor.getLong(indexDate2));
        }

        return msg;
    }
}
