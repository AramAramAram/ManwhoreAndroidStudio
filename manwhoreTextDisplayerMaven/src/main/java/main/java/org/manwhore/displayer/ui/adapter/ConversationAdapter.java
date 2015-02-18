/**
 * 
 */
package org.manwhore.displayer.ui.adapter;

import android.database.Cursor;
import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.ResourceCursorAdapter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.manwhore.displayer.R;
import org.manwhore.displayer.db.dao.IMessageDAO;
import org.manwhore.displayer.db.entity.Conversation;
import org.manwhore.displayer.db.entity.Message;
import org.manwhore.displayer.util.DelayText;

/**
 * Adapter for displaying messages in a conversation.
 *
 * @author siglerv
 *
 */
public class ConversationAdapter extends ResourceCursorAdapter {

    @Inject
    private IMessageDAO dao;
    
    /**
     * 
     */
    @Inject
    ConversationAdapter(Context context, @Assisted Cursor cursor) {
        super(context, R.layout.conversation_row, cursor);
    }

    protected void fillSender(View parentView, Message msg) {
        TextView textViewSender = (TextView) parentView.findViewById(R.id.msgSender);
        if (msg.isIncoming()) {
            textViewSender.setText(msg.getPerson());
        } else {
            textViewSender.setText(parentView.getResources().getString(R.string.ui_label_message_me));
        }
    }

    protected void fillDateDelay(View parentView, Message msg, Message msgPrev) {
        String delayText = null;

        if (msgPrev != null) {
            DelayText dt = new DelayText(msgPrev.getDate(), msg.getDate(), parentView.getResources());
            dt.setResAsc(R.string.dt_later);
            dt.setResDesc(R.string.dt_earlier);
            delayText = dt.getText();
        }

        TextView textViewDate = (TextView) parentView.findViewById(R.id.msgDateDelay);
        String date = DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(msg.getDate()));

        if (delayText != null) {
            textViewDate.setText(delayText + " - " + date);
        } else {
            textViewDate.setText(date);
        }
    }

    protected void fillBody(View parentView, Message msg) {
        TextView textViewMsgBody = (TextView) parentView.findViewById(R.id.msgBody);
        if (msg.isIncoming()) {
            textViewMsgBody.setTextAppearance(textViewMsgBody.getContext(), R.style.receiverAppearance);
        } else {
            textViewMsgBody.setTextAppearance(textViewMsgBody.getContext(), R.style.senderAppearance);
        }
        textViewMsgBody.setText(msg.getBody());
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        
        Message msg = dao.convert(cursor);
        Message msgPrev = null;

        if (!cursor.isFirst()) {
            cursor.moveToPrevious();
            msgPrev = dao.convert(cursor);
            cursor.moveToNext();
        }

        fillSender(view, msg);
        fillBody(view, msg);
        fillDateDelay(view, msg, msgPrev);
    }
}
