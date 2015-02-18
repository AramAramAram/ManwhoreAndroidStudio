package org.manwhore.displayer.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import org.manwhore.displayer.R;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: sigi
 * Date: 8.5.2011
 * Time: 21:43
 * To change this template use File | Settings | File Templates.
 */
public class ConversationFilterSpinner extends Spinner
{
    public ConversationFilterSpinner(Context context) {
        super(context);
        initSpinner();
    }

    public ConversationFilterSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSpinner();
    }

    public ConversationFilterSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSpinner();
    }

    protected void initSpinner()
    {
        //init "days back" filter
        LinkedList<SpinnerListItem> fields = new LinkedList<SpinnerListItem>();

        //no filter
        String title = getResources().getString(R.string.ui_label_conversation_filter_default);
        fields.add(new SpinnerListItem(title, -1));

        //today
        title = getResources().getString(R.string.ui_label_conversation_filter_today);
        fields.add(new SpinnerListItem(title, 0));

        for (int i = 2; i <= 31; i++)
        {
            title = i + " " + getResources().getString(R.string.ui_label_conversation_filter_back);
            fields.add(new SpinnerListItem(title, i));
        }

        ArrayAdapter<SpinnerListItem> adapter = new ArrayAdapter<SpinnerListItem>(getContext(),
                android.R.layout.simple_spinner_item, fields);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(adapter);

    }

    public static class SpinnerListItem
    {
        private String title;
        private int value;

        private SpinnerListItem(String title, int value) {
            this.title = title;
            this.value = value;


        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
