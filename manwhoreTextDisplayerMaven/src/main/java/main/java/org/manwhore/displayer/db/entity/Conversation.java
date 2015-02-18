package org.manwhore.displayer.db.entity;

import java.io.Serializable;
import org.manwhore.displayer.logging.Util;

/**
 * Class representing a conversation.
 * @author siglerv
 *
 */
public class Conversation implements Serializable {

    private static final long serialVersionUID = -4952183348547158089L;
    private Integer msgCount;
    private String address;
    private String person;
    private Long threadId;

    public Integer getMsgCount() {
        return this.msgCount;
    }

    public void setMsgCount(Integer msgCount) {
        this.msgCount = msgCount;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPerson() {
        return this.person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Conversation:");
        builder.append(" address: ").append(Util.obfuscateShort(getAddress()));
        builder.append(" msgCount: ").append(getMsgCount());
        builder.append(" threadId: ").append(getThreadId());
        builder.append("]");
        return builder.toString();
    }
    
}
