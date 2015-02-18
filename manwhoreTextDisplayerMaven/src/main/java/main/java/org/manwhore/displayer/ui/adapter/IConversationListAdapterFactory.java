/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.ui.adapter;

import android.database.Cursor;

/**
 *
 * @author sigi
 */
public interface IConversationListAdapterFactory {
    public ConversationAdapter create(Cursor cursor);
}
