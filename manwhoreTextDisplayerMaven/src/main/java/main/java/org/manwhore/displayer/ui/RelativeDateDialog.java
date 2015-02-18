package org.manwhore.displayer.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.manwhore.displayer.R;

/**
 * Dialog for managing relative date.
 * @author siglerv
 *
 */
public abstract class RelativeDateDialog extends Dialog {

	public RelativeDateDialog(Context context) {
		super(context);
		
		setContentView(R.layout.relative_date_dialog);
		
		setTitle(R.string.ui_dialog_reldate_title);
		
		final TextView checkStatus = (TextView) findViewById(R.id.checkRDStatus); 
		final EditText textRelDate = (EditText) findViewById(R.id.relDateEditBox);
		final Button btnCancel = (Button) findViewById(R.id.buttonCancelDialog);
		final Button btnSet = (Button) findViewById(R.id.buttonSetDialog);
		
		final Pattern rdPattern = Pattern.compile(getContext().getResources().getString(R.string.rd_regexp));
						
		textRelDate.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String str = s.toString().trim();
				if ("".equals(str))
				{
					checkStatus.setText(R.string.ui_label_undefined);
					checkStatus.setTextAppearance(getContext(), R.style.checkNone);
					btnSet.setEnabled(false);
				}
				else
				{
					Matcher matcher = rdPattern.matcher(str);
					if (matcher.matches())
					{
						checkStatus.setText(R.string.ui_label_rd_ok);
						checkStatus.setTextAppearance(getContext(), R.style.checkOk);
						btnSet.setEnabled(true);
					}
					else
					{
						checkStatus.setText(R.string.ui_label_rd_err);
						checkStatus.setTextAppearance(getContext(), R.style.checkErr);
						btnSet.setEnabled(false);
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		btnCancel.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancel();
				
			}
		});
		
		btnSet.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onRelativeDateSet(textRelDate.getText().toString());
				dismiss();
			}
		});

	}	
	
	public abstract void onRelativeDateSet(String relDate);
}
