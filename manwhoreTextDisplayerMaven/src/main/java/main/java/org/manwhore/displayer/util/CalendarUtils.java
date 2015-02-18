package org.manwhore.displayer.util;

import java.util.Calendar;

/**
 * Calendar utilities.
 * @author siglerv
 *
 */
public class CalendarUtils {
	
	public static void clearCalendar(Calendar cal)
	{
		cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
	}

	
	public static Calendar getCleanCalendar()
	{
		Calendar cal = Calendar.getInstance();
		clearCalendar(cal);
		return cal;
	}

}
