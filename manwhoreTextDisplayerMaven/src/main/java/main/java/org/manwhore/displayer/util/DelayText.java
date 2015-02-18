package org.manwhore.displayer.util;

import android.content.res.Resources;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.manwhore.displayer.R;

/**
 * Class to handle time delays. Produces delays in text form - "1 hr 23 mins later"
 * 
 * @author siglerv
 *
 */
public class DelayText {

    private static long SECOND = 1000;
    private static long MINUTE = 60 * SECOND;
    private static long HOUR = 60 * MINUTE;
    private static long DAY = 24 * HOUR;
    private static long WEEK = 7 * DAY;
    private static long MONTH = 30 * DAY;
    private static long YEAR = 365 * DAY;
    private long time;
        
    boolean reverse = false;
    
    private Resources res;
    private Integer resAsc;
    private Integer resDesc;

    @Inject
    public DelayText(@Assisted("startTime") final Long startTime, @Assisted("endTime") final Long endTime, final Resources res) {
        this(endTime - startTime, res);
    }

    public DelayText(final long time, final Resources res) {
        this.time = time;
        this.res = res;        

        if (this.time < 0) {
            reverse = true;
            this.time = -this.time;
        }
    }
    
    protected Integer getAddResource() {
        return reverse ? resDesc : resAsc;
    }

    public Integer getResAsc() {
        return resAsc;
    }

    public void setResAsc(Integer resAsc) {
        this.resAsc = resAsc;
    }

    public Integer getResDesc() {
        return resDesc;
    }

    public void setResDesc(Integer resDesc) {
        this.resDesc = resDesc;
    }
    
    public String getText() {
        String text = getTextInternal();
        Integer addResource = getAddResource();
        
        if (addResource == null || text == null) {
            return text;
        }
        
        String msg = this.res.getString(addResource);
        
        return String.format(msg, text);
    }

    /**
     * Returns formatted delay string.
     * 
     * @return
     */
    protected String getTextInternal() {
        String output = null;

        output = getUnit(YEAR, R.string.time_year, R.string.time_years);

        if (output != null) {
            return appendNonNull(output, getUnit(MONTH, YEAR, R.string.time_month, R.string.time_months));
        }

        output = getUnit(MONTH, YEAR, R.string.time_month, R.string.time_months);

        if (output != null) {
            return appendNonNull(output, getUnit(WEEK, MONTH, R.string.time_week, R.string.time_weeks));
        }

        output = getUnit(WEEK, MONTH, R.string.time_week, R.string.time_weeks);

        if (output != null) {
            return appendNonNull(output, getUnit(DAY, WEEK, R.string.time_day, R.string.time_days));
        }

        output = getUnit(DAY, WEEK, R.string.time_day, R.string.time_days);

        if (output != null) {
            return appendNonNull(output, getUnit(HOUR, DAY, R.string.time_hour, R.string.time_hours));
        }

        output = getUnit(HOUR, DAY, R.string.time_hour, R.string.time_hours);

        if (output != null) {
            return appendNonNull(output, getUnit(MINUTE, HOUR, R.string.time_minute, R.string.time_minutes));
        }

        return getUnit(MINUTE, HOUR, R.string.time_minute, R.string.time_minutes);

        /*
        if (output != null)		 
        {
        return appendNonNull(output, getUnit(SECOND, MINUTE, R.string.time_second, R.string.time_seconds));
        }
        
        return getUnit(SECOND, MINUTE, R.string.time_second, R.string.time_seconds, true);
         */
    }

    protected String getUnit(long unit, long prevUnit, int resSingle, int resMultiple) {
        return getUnit(unit, prevUnit, resSingle, resMultiple, false);
    }

    protected String getUnit(long unit, int resSingle, int resMultiple) {
        return getUnit(unit, 0, resSingle, resMultiple, false);
    }

    protected String getUnit(long unit, long prevUnit, int resSingle, int resMultiple, boolean allowZero) {
        long unitTime = (prevUnit > 0) ? this.time % prevUnit : this.time;

        long delayValue = unitTime / unit;
        String output = null;

        if ((delayValue > 0) || allowZero) {
            output = "";

            if (delayValue == 1) {
                output += delayValue + " " + this.res.getString(resSingle);
            } else {
                output += delayValue + " " + this.res.getString(resMultiple);
            }
        }

        return output;
    }

    protected String appendNonNull(String str, String toAppend) {
        if (toAppend != null) {
            return str + " " + toAppend;
        } else {
            return str;
        }
    }
}
