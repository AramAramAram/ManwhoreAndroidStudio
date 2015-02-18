package org.manwhore.displayer.ui;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.*;
import org.manwhore.displayer.R;
import org.manwhore.displayer.filter.*;
import org.manwhore.displayer.filter.Filter;

import java.util.LinkedList;


/**
 * Created by IntelliJ IDEA.
 * User: sigi
 * Date: 13.3.2011
 * Time: 21:08
 * To change this template use File | Settings | File Templates.
 */
public abstract class ConversationSubmitDialog extends Dialog {

    private EditText conversationDaysBack;
    private EditText conversationTitle;
    private EditText conversationPersonName;

    private Button btnSubmit;

    public ConversationSubmitDialog(Context context) {
		super(context);

        //requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.conversation_submit_dialog);
        //setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.btn_dropdown);

		setTitle(R.string.ui_dialog_conversation_submit_title);

		conversationTitle = (EditText) findViewById(R.id.conversationTitle);
        conversationPersonName = (EditText) findViewById(R.id.conversationPersonName);
		final android.widget.Button btnCancel = (android.widget.Button) findViewById(R.id.buttonCancelDialog);
		btnSubmit = (android.widget.Button) findViewById(R.id.buttonSubmitDialog);

        btnCancel.setOnClickListener(new android.view.View.OnClickListener() {


			public void onClick(View v) {
				cancel();

			}
		});

        TextWatcher watcher = new TextWatcher() {


			public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateDialog();
			}


			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}


			public void afterTextChanged(Editable s) {}
		};


		conversationTitle.addTextChangedListener(watcher);
        conversationPersonName.addTextChangedListener(watcher);

		btnSubmit.setOnClickListener(new android.view.View.OnClickListener() {

			public void onClick(View v) {

                int daysBack = -1;

                try
                {
                    daysBack = Integer.parseInt(conversationDaysBack.getText().toString());
                }
                catch (Exception e)
                {}

                Filter filter;
                if (daysBack < 0)
                    filter = Filter.getDefaultFilter();
                else
                    filter = Filter.getDaysBackFilter(daysBack);

				onSubmitOptionsSet(conversationTitle.getText().toString(), conversationPersonName.getText().toString(), filter);
				dismiss();
			}
		});

        conversationDaysBack = (EditText) findViewById(R.id.conversationDaysBack);

        conversationDaysBack.addTextChangedListener(new TextWatcher(){

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String val = charSequence.toString().trim();

                if ("".equals(val))
                    btnSubmit.setEnabled(true);
                else
                {

                    try
                    {
                        Integer.parseInt(val);
                        btnSubmit.setEnabled(true);
                    }
                    catch (NumberFormatException nfe)
                    {
                        btnSubmit.setEnabled(false);
                    }
                }

            }

            public void afterTextChanged(Editable editable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

	}

    protected void validateDialog()
    {
        String convTitle = conversationTitle.getText().toString().trim();
        String personName = conversationPersonName.getText().toString().trim();
        String daysBack = conversationDaysBack.getText().toString().trim();

        if ("".equals(convTitle) || "".equals(personName) || (!"".equals(daysBack) && !daysBack.matches("[0-9]+")) )
        {
            btnSubmit.setEnabled(false);
        }
        else
        {
            btnSubmit.setEnabled(true);
        }
    }

    public void setConversationDaysBack(String daysBackValue)
    {
        if (daysBackValue != null)
            conversationDaysBack.setText(daysBackValue);
    }

    public void setConversationTitle(String conversationTitle) {
        if (conversationTitle != null)
            this.conversationTitle.setText(conversationTitle);
    }

    public void setConversationPersonName(String conversationPersonName) {
        if (conversationPersonName != null)
            this.conversationPersonName.setText(conversationPersonName);
    }

    public abstract void onSubmitOptionsSet(String title, String person, Filter filter);
}
