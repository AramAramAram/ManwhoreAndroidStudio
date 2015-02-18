package org.manwhore.displayer.ui;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;

/**
 * OnClickListener that invokes DatePicker dialog upon clicking.
 * 
 * @author siglerv
 *
 */
public abstract class SetAbsDateClickListener implements OnClickListener
{
	private Calendar calendar;
	private TextView valueView;
	
	public SetAbsDateClickListener(Calendar calendar, TextView valueView)
	{
		this.calendar = calendar;
		this.valueView = valueView;
	}

	@Override
	public void onClick(View v) {
		
		DatePickerDialog dpd = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
			
				SetAbsDateClickListener.this.calendar.set(Calendar.YEAR, year);
				SetAbsDateClickListener.this.calendar.set(Calendar.MONTH, monthOfYear);
				SetAbsDateClickListener.this.calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
											
				SetAbsDateClickListener.this.valueView.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(SetAbsDateClickListener.this.calendar.getTime()));
				setCalFlag();
				
			}
		}, this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH), this.calendar.get(Calendar.DAY_OF_MONTH));
		
		dpd.show();			
	}    	
	
	protected abstract void setCalFlag();
}