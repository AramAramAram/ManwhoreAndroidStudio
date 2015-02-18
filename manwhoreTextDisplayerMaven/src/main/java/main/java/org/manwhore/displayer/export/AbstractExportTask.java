package org.manwhore.displayer.export;

import android.app.Application;
import android.database.Cursor;
import android.os.AsyncTask;
import com.google.inject.Inject;
import org.manwhore.displayer.R;
import org.manwhore.displayer.db.dao.IDAOManager;
import org.manwhore.displayer.db.entity.Conversation;
import org.manwhore.displayer.db.entity.Message;
import org.manwhore.displayer.filter.Filter;
import org.manwhore.displayer.util.DelayText;
import org.manwhore.displayer.util.IDelayTextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: sigi
 * Date: 6.3.2011
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractExportTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExportTask.class);
    
    public static final int ERR_STORAGE_INACCESSIBLE = 0;
    public static final int ERR_EXPORT_FAILED = 1;
    public static final int ERR_CONVERSATION_EMPTY = 99;
    public static final String KEY_ERR = "err";
    public static final String KEY_PROGRESS = "processed";
    public static final String KEZ_TYPE = "type";
    public static final String KEY_COUNT = "count";
    public static final int MSG_TYPE_PROGRESS = 0;
    public static final int MSG_TYPE_START = 1;
    public static final int MSG_TYPE_FINISHED = 2;
    public static final int MSG_TYPE_ERR = 3;
    
    @Inject
    private Application context;
    
    @Inject
    private IDelayTextFactory dtFactory;
    
    @Inject
    private IDAOManager daoManager;
    
    private Conversation conversation;    
    private Filter filter;
    private long lastDate = 0;
    private long previousDate = -1;
    
    public AbstractExportTask(Conversation conversation, final Filter filter) {
        this.conversation = conversation;
        this.filter = filter;        
    }

    protected Conversation getConversation() {
        return this.conversation;
    }

    /**
     * Descendants implement exporting functionalities here.
     *
     */
    public void exportConversation() {        
        new AsyncTask<Conversation, Integer, ExportResult>() {

            @Override
            protected ExportResult doInBackground(Conversation... arg0) {
                ExportResult result = new ExportResult();
                Integer counter = 0;
                try {                    
                    for (Conversation convo : arg0) {
                        Cursor cursor = daoManager.getMessageDAO().getCursor(convo, filter);

                        try {
                            if (cursor == null || cursor.getCount() == 0) {
                                result.setErrCode(ERR_CONVERSATION_EMPTY);
                                return result;
                            }
                            initExport();                        

                            while (cursor.moveToNext()) {
                                Message msg = daoManager.getMessageDAO().convert(cursor);    
                                LOGGER.debug("Processing message: {}", msg);
                                handleMessage(msg);
                                long date = msg.getDate();
                                previousDate = date;

                                if (lastDate < date) {
                                    lastDate = date;
                                }
                                counter++;
                                publishProgress(counter);
                            }
                            finalizeExport(result);
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }                    
                    result.setExportedCount(counter);
                } catch (ExportException ee) {
                    result.setErrCode(ee.getCode());
                }
                return result;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                updateProgress(values[0]);
            }

            @Override
            protected void onPostExecute(ExportResult result) {
                onTaskFinished(result);
            }

            @Override
            protected void onPreExecute() {
                onTaskStarted();
            }
            
        }.execute(conversation);
    }

    public Filter getFilter() {
        return filter;
    }

    public long getLastDate() {
        return lastDate;
    }

    public long getPreviousDate() {
        return previousDate;
    }

    public Application getAppContext() {
        return context;
    }

    public IDAOManager getDaoManager() {
        return daoManager;
    }
    
    public String getDelayText(final long start, final long end) {        
        DelayText dt = dtFactory.create(start, end);
        dt.setResAsc(R.string.dt_later);
        dt.setResDesc(R.string.dt_earlier);
        return dt.getText();
    }
        
    public void initExport() throws ExportException {
    }

    public void finalizeExport(ExportResult result) throws ExportException {
    }

    public abstract void handleMessage(Message msg) throws ExportException;

    public abstract void updateProgress(Integer progress);    
    
    public abstract void onTaskFinished(ExportResult result);
    
    public abstract void onTaskStarted();
}
