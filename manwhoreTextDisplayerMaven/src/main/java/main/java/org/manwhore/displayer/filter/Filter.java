package org.manwhore.displayer.filter;

import java.io.Serializable;

import android.content.Context;
import org.manwhore.displayer.db.Tables;
import org.manwhore.displayer.db.Tables.Filters;

/**
 * 
 * Class representing conversation filter.
 * 
 * @author siglerv
 *
 */
public class Filter implements Serializable {
	
	private static final long serialVersionUID = 5909768116573893985L;

	private int id = -1;
	
	private long absDateFrom = -1;
	private long absDateTo = -1;
	
	private String relDateFrom;
	private String relDateTo;
	
	private int typeDateFrom;
	private int typeDateTo;
		
	private int sortCol;
	private boolean convOrder;
	
	private boolean msgOrder;
	
	private int exportMode;
	
	private String phone = null;
	
	private String name;
		
	public void setName(String name) {
		this.name = name;
	}

	public long getDateFromValue(Context context) throws Exception
	{
		if (this.typeDateFrom == Tables.Filters.DATETYPE_ABSOLUTE)
			return getAbsDateFrom();
		else if (this.typeDateFrom == Tables.Filters.DATETYPE_RELATIVE)
			return new RelativeDate(getRelDateFrom(), context).getTimeInMillis();
		else 
			return -1;
	}
	
	public long getDateToValue(Context context) throws Exception
	{
		if (this.typeDateTo == Tables.Filters.DATETYPE_ABSOLUTE)
			return getAbsDateTo();
		else if (this.typeDateTo == Tables.Filters.DATETYPE_RELATIVE)
			return new RelativeDate(getRelDateTo(), context).getTimeInMillis();
		else 
			return -1;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getAbsDateFrom() {
		return this.absDateFrom;
	}
	public void setAbsDateFrom(long dateFrom) {
		this.absDateFrom = dateFrom;
		setTypeDateFrom(Filters.DATETYPE_ABSOLUTE);
	}
	public long getAbsDateTo() {
		return this.absDateTo;
	}
	public void setAbsDateTo(long dateTo) {
		this.absDateTo = dateTo;
		setTypeDateTo(Filters.DATETYPE_ABSOLUTE);
	}
	
	public boolean isConvOrderDesc()
	{
		return this.convOrder;
	}
	
	public void setConvOrderDesc()
	{
		this.convOrder = true;
	}
	
	public void setConvOrderAsc()
	{
		this.convOrder = false;
	}
	
	public boolean isMsgOrderDesc()
	{
		return this.msgOrder;
	}
	
	public void setMsgOrderDesc()
	{
		this.msgOrder = true;
	}
	
	public void setMsgOrderAsc()
	{
		this.msgOrder = false;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getSortCol() {
		return this.sortCol;
	}

	public void setSortCol(int sortCol) {
		this.sortCol = sortCol;
	}

	public String getRelDateFrom() {
		return this.relDateFrom;
	}

	public void setRelDateFrom(String relDateFrom) {
		this.relDateFrom = relDateFrom;
		setTypeDateFrom(Filters.DATETYPE_RELATIVE);
	}

	public String getRelDateTo() {
		return this.relDateTo;
	}

	public void setRelDateTo(String relDateTo) {
		this.relDateTo = relDateTo;
		setTypeDateTo(Filters.DATETYPE_RELATIVE);
	}

	public int getTypeDateFrom() {
		return this.typeDateFrom;
	}

	public void setTypeDateFrom(int typeDateFrom) {
		this.typeDateFrom = typeDateFrom;
	}

	public int getTypeDateTo() {
		return this.typeDateTo;
	}

	public void setTypeDateTo(int typeDateTo) {
		this.typeDateTo = typeDateTo;
	}

	public int getExportMode() {
		return this.exportMode;
	}

	public void setExportMode(int exportMode) {
		this.exportMode = exportMode;
	}

    public static Filter getDefaultFilter()
    {
        /*
        Sort conversations by LAST MESSAGE DATE
        In Descending Order
        Show Oldest First
        Export messages with Simplified Info
        */

        Filter filter = new Filter();

        filter.setConvOrderDesc();
        filter.setMsgOrderAsc();

        filter.setSortCol(Filters.CONV_SORT_DATE);
        filter.setExportMode(Filters.EXPORT_SIMPLE);

        return filter;
    }

    public static Filter getDaysBackFilter(int daysBack)
    {
        Filter filter = getDefaultFilter();
        if (daysBack > -1)
        {
            filter.setTypeDateFrom(Tables.Filters.DATETYPE_RELATIVE);
            filter.setRelDateFrom("-" + daysBack + "ds");
        }
        return filter;
    }
}
