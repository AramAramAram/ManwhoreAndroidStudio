package org.manwhore.displayer.filter;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.manwhore.displayer.db.DbOpenHelper;
import org.manwhore.displayer.db.Tables.Filters;

/**
 * Class for managing filters - load from db, save, delete.
 * 
 * @author siglerv
 *
 */
public class FilterManager {

	private TreeMap<Integer, Filter> filterMap;
		
	public FilterManager(Context context)
	{
		
		try 
		{
			loadFromDB(context);
		}
		catch (Exception e)
		{
			Log.e(this.getClass().getName(), "Error loading filters: " + e.getMessage());
		}
	}
	
	public void refreshFilters(Context context)
	{
		this.filterMap.clear();
		
		try 
		{
			loadFromDB(context);
		}
		catch (Exception e)
		{
			Log.e(this.getClass().getName(), "Error refreshing filters: " + e.getMessage());
		}
	}
	
	/**
	 * Loads all available filters from the database.
	 * 
	 * @param context
	 * @throws Exception
	 */
	protected void loadFromDB(Context context) throws Exception
	{
		SQLiteOpenHelper soh = new DbOpenHelper(context); 
		
		SQLiteDatabase db = soh.getReadableDatabase();
		
		this.filterMap = new TreeMap<Integer, Filter>();
		
		Cursor cursor = db.query(Filters.TABLE_NAME, null, null, null, null, null, Filters.COL_NAME + " ASC");
		
		if (cursor.moveToFirst())
		{
			int indexId = cursor.getColumnIndex(Filters.COL_ID);
			int indexName = cursor.getColumnIndex(Filters.COL_NAME);
			int indexFromType = cursor.getColumnIndex(Filters.COL_TYPE_FROM);
			int indexToType = cursor.getColumnIndex(Filters.COL_TYPE_TO);
			
			int indexFromAbs = cursor.getColumnIndex(Filters.COL_DATE_FROM_ABS);
			int indexToAbs = cursor.getColumnIndex(Filters.COL_DATE_TO_ABS);
			
			int indexFromRel = cursor.getColumnIndex(Filters.COL_DATE_FROM_REL);
			int indexToRel = cursor.getColumnIndex(Filters.COL_DATE_TO_REL);
			
			int indexSortColumn = cursor.getColumnIndex(Filters.COL_SORT_COL);
			int indexConvOrder = cursor.getColumnIndex(Filters.COL_CONV_ORDER);
			int indexMsgOrder = cursor.getColumnIndex(Filters.COL_MSG_ORDER);
			
			int indexPhone = cursor.getColumnIndex(Filters.COL_PHONE);
			int indexExportMode = cursor.getColumnIndex(Filters.COL_EXPORT_MODE);
			
			do
			{
				int id = cursor.getInt(indexId);
				String name = cursor.getString(indexName);
				
				int fromType = cursor.getInt(indexFromType);
				int toType = cursor.getInt(indexToType);
				
				Filter filter = new Filter();
				filter.setName(name);
				filter.setId(id);
				
				switch (fromType)
				{
					case Filters.DATETYPE_ABSOLUTE:
					{
						long date = cursor.getLong(indexFromAbs);
						filter.setAbsDateFrom(date);						
						break;
					}
					case Filters.DATETYPE_RELATIVE:
					{	
						filter.setRelDateFrom(cursor.getString(indexFromRel));						
						break;
					}
					default: 
					{
						filter.setTypeDateFrom(Filters.DATETYPE_NONE);
					}
				}
				
				switch (toType)
				{
					case Filters.DATETYPE_ABSOLUTE:
					{
						long date = cursor.getLong(indexToAbs);
						filter.setAbsDateTo(date);
						break;
					}
					case Filters.DATETYPE_RELATIVE:
					{
						String relDate = cursor.getString(indexToRel);						
						filter.setRelDateTo(relDate);
						break;
					}
					default: 
					{
						filter.setTypeDateTo(Filters.DATETYPE_NONE);
					}
				}
				
				int sortColumn = cursor.getInt(indexSortColumn);
				filter.setSortCol(sortColumn);
				
				int order = cursor.getInt(indexConvOrder);
				if (order == Filters.ORDER_ASC)
					filter.setConvOrderAsc();
				else
					filter.setConvOrderDesc();
				
				int msgOrder = cursor.getInt(indexMsgOrder);
				if (msgOrder == Filters.ORDER_ASC)
					filter.setMsgOrderAsc();
				else
					filter.setMsgOrderDesc();
				
				String phone = cursor.getString(indexPhone);
				if (phone != null)
					filter.setPhone(phone);
				
				filter.setExportMode(cursor.getInt(indexExportMode)); 
				
				this.filterMap.put(filter.getId(), filter);				
			}
			while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
	}
	
	/**
	 * Save a filter to db.
	 * 
	 * @param filter
	 * @param context
	 */
	public static void saveFilter(Filter filter, Context context)
	{
		SQLiteOpenHelper soh = new DbOpenHelper(context);
		SQLiteDatabase db = soh.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		values.put(Filters.COL_NAME, filter.getName());
		values.put(Filters.COL_PHONE, filter.getPhone());
		
		values.put(Filters.COL_TYPE_FROM, filter.getTypeDateFrom());
		values.put(Filters.COL_TYPE_TO, filter.getTypeDateTo());
		
		if (filter.getTypeDateFrom() == Filters.DATETYPE_ABSOLUTE)
			values.put(Filters.COL_DATE_FROM_ABS, filter.getAbsDateFrom());
		else if (filter.getTypeDateFrom() == Filters.DATETYPE_RELATIVE)
			values.put(Filters.COL_DATE_FROM_REL, filter.getRelDateFrom());
		
		if (filter.getTypeDateTo() == Filters.DATETYPE_ABSOLUTE)			
			values.put(Filters.COL_DATE_TO_ABS, filter.getAbsDateTo());
		else if (filter.getTypeDateTo() == Filters.DATETYPE_RELATIVE)
			values.put(Filters.COL_DATE_TO_REL, filter.getRelDateTo());
		
		values.put(Filters.COL_SORT_COL, filter.getSortCol());
		values.put(Filters.COL_CONV_ORDER, filter.isConvOrderDesc() ? Filters.ORDER_DESC : Filters.ORDER_ASC);
		values.put(Filters.COL_MSG_ORDER, filter.isMsgOrderDesc() ? Filters.ORDER_DESC : Filters.ORDER_ASC);
		values.put(Filters.COL_EXPORT_MODE, filter.getExportMode());
				
		if (filter.getId() == -1) // new record
			db.insert(Filters.TABLE_NAME, null, values);
		else
			db.update(Filters.TABLE_NAME, values, Filters.COL_ID + " = ?", new String[]{String.valueOf(filter.getId())});
		
		db.close();
	}
	
	/**
	 * Delete filter from db.
	 * 
	 * @param idFilter
	 * @param context
	 */
	public static void deleteFilter(int idFilter, Context context)
	{
		SQLiteOpenHelper soh = new DbOpenHelper(context);
		SQLiteDatabase db = soh.getWritableDatabase();
		
		db.delete(Filters.TABLE_NAME, Filters.COL_ID + " = ?", new String[]{String.valueOf(idFilter)});
		db.close();
	}
	
	/**
	 * Return count of filters in the db.
	 * 
	 * @return
	 */
	public int getFilterCount()
	{
		return this.filterMap.size();
	}
	
	/**
	 * Get all available filters.
	 * 
	 * @return
	 */
	public Collection<Filter> getFilters()
	{
		return getFilterMap().values();
	}
		
	/**
	 * Gets a map of filterId-filter pairs.
	 * 
	 * @return
	 */
	protected Map<Integer, Filter> getFilterMap()
	{
		return this.filterMap;
	}
	
	/**
	 * Gets a filter with given ID.
	 * 
	 * @param filterId
	 * @return
	 */
	public Filter getFilter(int filterId)
	{
		return getFilterMap().get(filterId);
	}
	
}
