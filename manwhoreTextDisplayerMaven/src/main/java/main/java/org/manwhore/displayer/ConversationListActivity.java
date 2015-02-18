package org.manwhore.displayer;


import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.google.inject.Inject;
import org.manwhore.displayer.base.ExportingActivity;
import org.manwhore.displayer.db.entity.Conversation;
import org.manwhore.displayer.export.web.IWebSubmitTaskFactory;
import org.manwhore.displayer.ui.adapter.ConversationArrayAdapter;
import roboguice.inject.InjectView;

/**
 * Activity that lists and sorts conversations based on a filter passed by the intent object.
 * 
 * @author siglerv 
 */
public class ConversationListActivity extends ExportingActivity {

    @InjectView(R.id.threadListView)
    private ListView conversationListView;
    
    private static final int MENU_SUBMIT = 1;
    
    @Inject
    private ConversationArrayAdapter listAdapter;
    
    @Inject
    private IWebSubmitTaskFactory exportFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_list);
        setTitle(R.string.activity_conversationList);
       
        listAdapter.startLoading();
        conversationListView.setAdapter(this.listAdapter);

        registerForContextMenu(conversationListView);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = ((AdapterContextMenuInfo) item.getMenuInfo());

        switch (item.getItemId()) {            
            case MENU_SUBMIT: {
                final Conversation conversation = (Conversation) ConversationListActivity.this.listAdapter.getItem(info.position);
                getExportDescriptor().setConversation(conversation);
                invokeExport();
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

        menu.setHeaderTitle(R.string.menu_title_conversation);        
        menu.add(0, MENU_SUBMIT, 0, R.string.menu_item_submit);
    }
}
