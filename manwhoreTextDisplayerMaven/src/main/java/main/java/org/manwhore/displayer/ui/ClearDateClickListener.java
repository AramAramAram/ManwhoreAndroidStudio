package org.manwhore.displayer.ui;

import java.util.Calendar;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import org.manwhore.displayer.R;
import org.manwhore.displayer.util.CalendarUtils;

/**
 * OnClickListener for clearing date.
 * 
 * @author siglerv
 *
 */
public abstract class ClearDateClickListener implements OnClickListener
{
	private Calendar calendar;
	private TextView valueView;
	
	public ClearDateClickListener(Calendar calendar, TextView valueView)
	{
		this.calendar = calendar;
		this.valueView = valueView;
	}

	@Override
	public void onClick(View v) {			
		CalendarUtils.clearCalendar(ClearDateClickListener.this.calendar);			
		ClearDateClickListener.this.valueView.setText(R.string.ui_label_undefined);
		resetCalFlag();
	}
	
	protected abstract void resetCalFlag();
}