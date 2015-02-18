package org.manwhore.displayer;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;

import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.google.inject.Inject;
import org.manwhore.displayer.R;
import org.manwhore.displayer.base.ExportingActivity;
import org.manwhore.displayer.db.entity.Conversation;
import org.manwhore.displayer.db.entity.Message;
import org.manwhore.displayer.db.dao.IMessageDAO;
import org.manwhore.displayer.filter.Filter;
import org.manwhore.displayer.ui.adapter.IConversationListAdapterFactory;
import org.manwhore.displayer.util.Constants;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Activity that displays a conversation received in the Intent object.
 * 
 * @author siglerv
 *
 */
public class ConversationViewActivity extends ExportingActivity {

    private static final int MENU_COPY_CLIPBOARD = 1;
    
    @InjectView(R.id.conversationDaysBack)
    private EditText conversationDaysBack;
    
    @InjectView(R.id.conversationView)
    private ListView conversationView;
    
    @InjectView(R.id.btnApplyDaysBack)
    private Button btnApplyDaysBack;
    
    @InjectExtra(Constants.INTENT_KEY_MSGTHREAD)
    private Conversation conversation;
    
    @Inject
    private IConversationListAdapterFactory factory;
    
    @Inject
    private IMessageDAO msgDao;
            
    String actualDaysBack = "";

    public ConversationViewActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        setTitle(R.string.activity_conversation);

        Cursor cursor = msgDao.getCursor(conversation, Filter.getDefaultFilter());
        
        conversationView.setAdapter(factory.create(cursor));

        conversationDaysBack.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String val = charSequence.toString().trim();

                if ("".equals(val)) {
                    btnApplyDaysBack.setEnabled(true);
                } else {

                    try {
                        Integer.parseInt(val);
                        btnApplyDaysBack.setEnabled(true);
                    } catch (NumberFormatException nfe) {
                        btnApplyDaysBack.setEnabled(false);
                    }
                }
            }

            public void afterTextChanged(Editable editable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        btnApplyDaysBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Filter filter;
                int daysBack;
                
                try {
                    daysBack = Integer.parseInt(conversationDaysBack.getText().toString());
                    filter = Filter.getDaysBackFilter(daysBack);
                } catch (Exception e) {
                    daysBack = -1;
                }

                updateAdapter(Filter.getDaysBackFilter(daysBack));
            }
        });


        registerForContextMenu(conversationView);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = ((AdapterContextMenuInfo) item.getMenuInfo());

        switch (item.getItemId()) {
            case MENU_COPY_CLIPBOARD: {
                Message msg = getListItem(info.position);
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(msg.getBody());

                Toast.makeText(this, R.string.toast_text_copied, Toast.LENGTH_SHORT).show();

                return true;
            }
            default: {
                return super.onContextItemSelected(item);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

        menu.setHeaderTitle(R.string.menu_title_message);
        menu.add(0, MENU_COPY_CLIPBOARD, 0, R.string.menu_item_copy_clipboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_conversation_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.conversationViewMenuSubmit:                
                getExportDescriptor().setConversation(conversation);
                getExportDescriptor().setDaysBack(conversationDaysBack.getText().toString());
                invokeExport();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateAdapter(Filter filter) {
        Cursor cursor = msgDao.getCursor(conversation, filter);
        
        conversationView.setAdapter(factory.create(cursor));
    }
    
    private Message getListItem(int position) {
        ListAdapter adapter = conversationView.getAdapter();
        Cursor cursor = (Cursor) adapter.getItem(position);
        
        return msgDao.convert(cursor);
    }
}
