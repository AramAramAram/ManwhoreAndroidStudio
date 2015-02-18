package org.manwhore.displayer.export.web;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.manwhore.displayer.IExportingActivity;
import org.manwhore.displayer.base.ExportingActivity;
import org.manwhore.displayer.export.AbstractExportTask;
import org.manwhore.displayer.export.ExportDescriptor;
import org.manwhore.displayer.export.ExportException;
import org.manwhore.displayer.export.ExportResult;
import org.manwhore.displayer.util.Constants;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.manwhore.displayer.util.ObjectProvider;
import org.manwhore.displayer.webservice.IWSManager;
import org.w3c.tidy.servlet.util.HTMLEncode;

/**
 * Created by IntelliJ IDEA.
 * User: sigi
 * Date: 5.3.2011
 * Time: 22:47
 * To change this template use File | Settings | File Templates.
 */
public class WebSubmitTask extends AbstractExportTask {

    public static final int ERR_LOGIN_FAILED = 2;
    public static final int ERR_PROFILE_UNDEFINED = 3;
    public static final int ERR_SERVER_ERROR = 4;
    public static final int ERR_CONNECTION_ERROR = 5;
    public static final int MSG_TYPE_RECREATED = 100;
    public static final int MW_PREPARE_CREATED = 0;
    public static final int MW_PREPARE_EXISTS = 1;
    public static final String MW_PREPARE_STATUS_KEY = "status";
    private String title;
    private String person;
    private String hash = null;
    private boolean isAppend;
    
    @Inject
    private ObjectProvider<IWSManager> wsmProvider;
    
    private IWSManager wsm;
    
    private Integer prepareStatus;
    private MessageCache cache;
    
    private IExportingActivity callerActivity;

    @Inject
    public WebSubmitTask(@Assisted ExportDescriptor descriptor) {
        super(descriptor.getConversation(), descriptor.getFilter());

        this.title = descriptor.getTitle();
        this.person = descriptor.getPersonName();                
        this.hash = descriptor.getHash();
        this.isAppend = hash != null;

    }

    public static String MD5_Hash(String s) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }
        m.update(s.getBytes(), 0, s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }

    protected void throwErrorByWSResult(int resultCode) throws ExportException {
        switch (resultCode) {
            case Constants.WSRESULT_PROFILE_UNDEFINED: {
                throw new ExportException(ERR_PROFILE_UNDEFINED);
            }
            case Constants.WSRESULT_NOT_LOGGED_IN: {
                throw new ExportException(ERR_LOGIN_FAILED);
            }
            case Constants.WSRESULT_CALL_FAIL: {
                throw new ExportException(ERR_CONNECTION_ERROR);
            }
            default: {
                throw new ExportException(ERR_SERVER_ERROR);
            }
        }
    }

    @Override
    public void handleMessage(org.manwhore.displayer.db.entity.Message message) throws ExportException {      
        Message msg = new Message();
        msg.setText(HTMLEncode.encode(message.getBody()));

        if (message.isIncoming()) {
            msg.setSender(1);
        } else {
            msg.setSender(0);
        }

        if (getPreviousDate() > 0) {
            String delayText = getDelayText(getPreviousDate(), message.getDate());

            msg.setDelaytext(delayText == null ? "" : delayText);
        } else {
            msg.setDelaytext("");
        }
        msg.setConvhash(hash);

        try {
            cache.add(msg);
        } catch (MessageCache.MCException e) {
            throwErrorByWSResult(e.getCode());
            return;
        }
    }

    @Override
    public void finalizeExport(ExportResult exportResult) throws ExportException {

        //send remaining messages
        try {
            cache.flush();
        } catch (MessageCache.MCException e) {
            throwErrorByWSResult(e.getCode());
            return;
        }
        
        //finalize the conversation
        int result = wsm.cnvFinalize(hash, HTMLEncode.encode(title),
                HTMLEncode.encode(person));

        if (result != Constants.WSRESULT_OK) {
            throwErrorByWSResult(result);
            return;
        }

        wsm.logout();

        if (isAppend) {
            if (prepareStatus == null) {
                throw new ExportException(ERR_SERVER_ERROR);
            } else {

                getDaoManager().getConversationDAO().saveAsSubmitted(getConversation(), hash, title, person, getLastDate());

                if (prepareStatus == MW_PREPARE_CREATED) {
                    exportResult.setResultCode(MSG_TYPE_RECREATED);
                } else if (prepareStatus == MW_PREPARE_EXISTS) {
                    exportResult.setResultCode(MSG_TYPE_FINISHED);
                } else {
                    throw new ExportException(ERR_SERVER_ERROR);
                }
            }
        } else {
            getDaoManager().getConversationDAO().saveAsSubmitted(getConversation(), hash, title, person, getLastDate());
            exportResult.setResultCode(MSG_TYPE_FINISHED);
        }
    }

    @Override
    public void initExport() throws ExportException {
        try {
            wsm = wsmProvider.get();
        } catch (Exception e) {
            throw new ExportException(Constants.WSRESULT_CALL_FAIL, e);
        }

				if (this.hash == null) {
            StringBuilder toHash = new StringBuilder();
            toHash.append(title);
            toHash.append(wsm.getUser());
            toHash.append(System.currentTimeMillis());
            toHash.append(person);
            toHash.append(Math.random());

            this.hash = MD5_Hash(toHash.toString());
        }
        
        int result = wsm.login();

        if (result != Constants.WSRESULT_OK) {
            throwErrorByWSResult(result);
            return;
        }

        //ok, we got hash, send prepare message
        Map resultMap = new HashMap();
        result = wsm.cnvPrepare(hash, resultMap);
        if (result != Constants.WSRESULT_OK) {
            throwErrorByWSResult(result);
            return;
        }
        prepareStatus = (Integer) resultMap.get(MW_PREPARE_STATUS_KEY);
        
        this.cache = new MessageCache(wsm, this.hash);
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
    
    

    @Override
    public void updateProgress(Integer progress) {
        IExportingActivity ea = getCallerActivity();
        if (ea != null) {
            ea.onExportProgress(progress);
        }
    }

    @Override
    public void onTaskFinished(ExportResult result) {
        IExportingActivity ea = getCallerActivity();
        if (ea != null) {
            ea.onExportFinished(result);
        }
    }

    public synchronized void setCallerActivity(ExportingActivity callerActivity) {
        this.callerActivity = callerActivity;
    }

    public synchronized IExportingActivity getCallerActivity() {
        return callerActivity;
    }

    @Override
    public void onTaskStarted() {
        IExportingActivity ea = getCallerActivity();
        if (ea != null) {
            ea.onExportStarted();
        }
    }
    
}
