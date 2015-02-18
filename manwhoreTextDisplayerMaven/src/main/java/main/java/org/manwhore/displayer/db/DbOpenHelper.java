package org.manwhore.displayer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.manwhore.displayer.db.Tables.Filters;

/**
 * Database open helper. Creates the database if it does not exist and inserts preset filters.
 * 
 * @author siglerv
 *
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SMSM_DB";

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Filters.CREATE_TABLE);
        db.execSQL(Tables.Conversations.CREATE_TABLE);
        
        String preset1 = "INSERT INTO " + Filters.TABLE_NAME + "(" +
        Filters.COL_NAME + ", " +
        Filters.COL_TYPE_FROM + ", " +
        Filters.COL_TYPE_TO + ", " +
        Filters.COL_CONV_ORDER + ") VALUES ('All', "+ Filters.DATETYPE_NONE +", "+ Filters.DATETYPE_NONE + ", " + Filters.ORDER_ASC + ");";
        
        String preset3 = "INSERT INTO " + Filters.TABLE_NAME + "(" +
        Filters.COL_NAME + ", " +
        Filters.COL_TYPE_FROM + ", " +
        Filters.COL_TYPE_TO + ", " +
        Filters.COL_DATE_FROM_REL + ", " +
        Filters.COL_DATE_TO_REL + ", " +
        Filters.COL_CONV_ORDER + ") VALUES ('Last week', "+        
        Filters.DATETYPE_RELATIVE +", "+ 
        Filters.DATETYPE_RELATIVE + ", " +
        "'-1ws', '-1we'," +
        Filters.ORDER_ASC + ");";
        
        db.execSQL(preset1);        
        db.execSQL(preset3);
        
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) 
	{
		// nothing to do now		
	}
}