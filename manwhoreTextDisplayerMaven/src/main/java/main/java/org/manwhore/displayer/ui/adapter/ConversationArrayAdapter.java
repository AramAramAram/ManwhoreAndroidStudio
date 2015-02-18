/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.inject.Inject;
import org.manwhore.displayer.R;
import org.manwhore.displayer.db.entity.Conversation;
import org.manwhore.displayer.db.dao.IConversationDAO;
import org.manwhore.displayer.ui.ConversationListLoadTask;
import org.manwhore.displayer.util.Constants;
import org.manwhore.displayer.util.IPersonResolver;
import java.util.LinkedList;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sigi
 */
public class ConversationArrayAdapter extends ArrayAdapter<Conversation> {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(ConversationArrayAdapter.class);
    
    public static final Integer MAX_THREADS = Integer.valueOf(3);
    
    public static final Integer INACTIVE_SLEEP = 10;
    
    @Inject
    private IConversationDAO dao;
    
    @Inject
    private IPersonResolver personResolver;
    
    @Inject
    ConversationArrayAdapter(Context context) {
        super(context, 0);
        this.setNotifyOnChange(true);
    }
    
    public void startLoading() {        
        final ConversationResolvePersonTask resolver = new ConversationResolvePersonTask();
        
        resolver.execute();
            new ConversationListLoadTask(dao) {

            @Override
            protected void onProgressUpdate(Conversation... values) {
                for (Conversation value : values) {
                    ConversationArrayAdapter.this.add(value);
                    resolver.enqueue(value);
                }
            }
            
        }.execute(dao.getCursor());        
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.thread_list_row, parent, false);
        }

        TextView textViewPerson = (TextView) view.findViewById(R.id.conversationPerson);
        TextView textViewMsgCount = (TextView) view.findViewById(R.id.conversationMsgCount);

        Conversation conversation = (Conversation) getItem(position);

        textViewMsgCount.setText("(" + conversation.getMsgCount() + ")");
        textViewPerson.setText(conversation.getPerson() == null ? conversation.getAddress() : conversation.getPerson());

        ((ListView) parent).setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                
                Conversation msgThread = (Conversation) parent.getItemAtPosition(position);

                Intent intent = new Intent();
                intent.setClassName(Constants.PACKAGE, Constants.ACTIVITY_CONVERSATION_VIEW);
                intent.putExtra(Constants.INTENT_KEY_MSGTHREAD, msgThread);

                parent.getContext().startActivity(intent);
            }
        });
        return view;
    }
    
    private class ConversationResolvePersonTask extends AsyncTask<Conversation, Object, Object>{
                        
        private Integer runningThreads = 0;
                        
        private Queue<Conversation> queue = new LinkedList<Conversation>();
        
        private boolean finished = false;
        
        public synchronized boolean isFinished() {
            return finished && queue.isEmpty();
        }
        
        public synchronized void finish() {
            finished = true;
        }
        
        public synchronized Conversation poll() {
            return queue.poll();
        }
        
        public synchronized void enqueue(Conversation convo) {
            queue.add(convo);
        }
        
        public synchronized void threadStart() {
            runningThreads++;
        }
        
        public synchronized void threadEnd() {
            runningThreads--;
        }
        
        public synchronized boolean canStartThread() {
            return runningThreads < MAX_THREADS;
        }
       
        @Override
        protected Object doInBackground(Conversation... arg0) {
            while (!isFinished()) {
                
                Conversation convo = poll();
                if (convo == null) {
                    try {
                        Thread.sleep(INACTIVE_SLEEP);
                    } catch (InterruptedException ie) {
                        //nothing to do here
                    }
                    continue;
                }
                
                while (!canStartThread()) {
                    try {
                        Thread.sleep(INACTIVE_SLEEP);
                    } catch (InterruptedException ie) {
                        //nothing to do here
                    }
                }
                
                threadStart();
                new AsyncTask<Conversation, Object, Object>() {

                    @Override
                    protected Object doInBackground(Conversation... arg0) {
                        for (Conversation conversation : arg0) {
                            LOGGER.debug("Resolving contact for conversation {}", conversation);
                            conversation.setPerson(personResolver.getPersonByAddress(conversation.getAddress()));
                        }                        
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        ConversationArrayAdapter.this.notifyDataSetChanged();
                        threadEnd();
                    }                     
                }.execute(convo);
            }
            return null;
        }    
    }
}
