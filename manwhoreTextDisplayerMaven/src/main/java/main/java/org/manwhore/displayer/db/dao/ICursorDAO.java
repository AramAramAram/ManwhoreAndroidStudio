/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.db.dao;

import android.database.Cursor;

/**
 *
 * @author sigi
 */
public interface ICursorDAO <T>{
    
    public T convert(Cursor cursor);
    
}
