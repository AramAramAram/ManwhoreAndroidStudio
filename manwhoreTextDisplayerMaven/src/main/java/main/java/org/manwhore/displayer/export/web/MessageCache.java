package org.manwhore.displayer.export.web;

import org.manwhore.displayer.util.Constants;

import java.util.LinkedList;
import java.util.Map;
import org.manwhore.displayer.webservice.IWSManager;

/**
 * Created by IntelliJ IDEA.
 * User: sigi
 * Date: 5.3.2011
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class MessageCache {

    private IWSManager wsm;

    private String cnvHash;

    private LinkedList<Map> cache = new LinkedList<Map>();

    /**
     * Maximum amount of messages in one batch.
     * When taking 160 chars per message, 100 messages
     * should take about 16kB.
     */
    private static final int BATCHSIZE = 100;

    public MessageCache(IWSManager wsm, String cnvHash)
    {
        this.wsm = wsm;
        this.cnvHash = cnvHash;
    }

    public void add(IMappable object) throws MCException {
        cache.add(object.toMap());

        if (cache.size() >= BATCHSIZE)
        {
            flush();
        }
    }

    public void flush() throws MCException {
        if (cache.size() == 0)
            return;

        int result = wsm.cnvSend(cnvHash, cache);

        if (result != Constants.WSRESULT_OK)
            throw new MCException(result);

        cache.clear();
    }

    public static class MCException extends Exception{

        private int code;

        public MCException(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

}
