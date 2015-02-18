package org.manwhore.displayer.db;

import android.text.NoCopySpan;

/**
 * Class containing column names and other info on application database tables.
 * 
 * @author siglerv
 *
 */
public class Tables {
	
	private Tables()
	{}

	public static class Filters
	{
		public static final String TABLE_NAME = "filters";
		public static final String COL_ID = "_id";
		public static final String COL_NAME = "name";
		public static final String COL_TYPE_FROM = "typeFrom"; //dateFrom type - 0 = none, 1 = absolute, 2 = relative
		public static final String COL_TYPE_TO = "typeTo"; //dateTo type - 0 = none, 1 = absolute, 2 = relative
		public static final String COL_DATE_FROM_REL = "dateFromRel"; // relative date
		public static final String COL_DATE_TO_REL = "dateToRel"; // relative date
		public static final String COL_DATE_FROM_ABS = "dateFromAbs"; // absolute date
		public static final String COL_DATE_TO_ABS = "dateToAbs"; // absolute date
		public static final String COL_SORT_COL = "sortColumn"; // 0 - by contact, 1 - by last message date 
		public static final String COL_CONV_ORDER = "convOrdering"; //0 - asc, 1 - desc
		public static final String COL_MSG_ORDER = "msgOrdering"; //0 - asc, 1 - desc
		public static final String COL_PHONE = "phone"; // phone number
		public static final String COL_EXPORT_MODE = "exportType"; //0 - regular, 1 - simple
		
		public static final int CONV_SORT_CONTACT = 0;
		public static final int CONV_SORT_DATE = 1;
		
		public static final int DATETYPE_NONE = 0;
		public static final int DATETYPE_ABSOLUTE = 1;
		public static final int DATETYPE_RELATIVE = 2;
		
		public static final int ORDER_ASC = 1;
		public static final int ORDER_DESC = 2;
		
		public static final int EXPORT_REGULAR = 0;
		public static final int EXPORT_SIMPLE = 1;
		
		
		public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COL_NAME + " TEXT NOT NULL," +
        COL_TYPE_FROM + " INTEGER NOT NULL DEFAULT "+ DATETYPE_NONE + "," +
        COL_TYPE_TO + " INTEGER NOT NULL DEFAULT "+ DATETYPE_NONE + "," +
        COL_DATE_FROM_ABS + " INTEGER," +
        COL_DATE_TO_ABS + " INTEGER," +
        COL_DATE_FROM_REL + " TEXT," +
        COL_DATE_TO_REL + " TEXT," +
        COL_SORT_COL + " INTEGER NOT NULL DEFAULT "+ CONV_SORT_CONTACT + "," +
        COL_CONV_ORDER + " INTEGER NOT NULL DEFAULT "+ ORDER_ASC + "," +
        COL_MSG_ORDER + " INTEGER NOT NULL DEFAULT "+ ORDER_ASC + "," +
        COL_PHONE + " TEXT," +
        COL_EXPORT_MODE + " INTEGER NOT NULL DEFAULT " + EXPORT_REGULAR+ ");";
		
		private Filters() {}
	}

    public static class Conversations
    {
        public static final String TABLE_NAME = "conversations";

        public static final String COL_ID = "_id";
		public static final String COL_ADDRESS = "address";
		public static final String COL_CONVHASH = "convhash";
        public static final String COL_PERSON = "person";
        public static final String COL_TITLE = "title";
        public static final String COL_LASTDATE = "lastdate";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COL_ADDRESS + " TEXT NOT NULL," +
        COL_CONVHASH + " TEXT NOT NULL," +
        COL_TITLE +   " TEXT NOT NULL," +
        COL_PERSON + " TEXT NOT NULL," +
        COL_LASTDATE + " INTEGER NOT NULL );";

        private Conversations() {}
    }
	
}
