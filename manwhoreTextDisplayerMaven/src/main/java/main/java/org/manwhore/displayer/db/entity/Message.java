package org.manwhore.displayer.db.entity;

import java.util.Calendar;
import java.util.Date;

/**
 * POJO representing a single message in the conversation.
 * 
 * @author siglerv
 *
 */
public class Message {

    private int id;
    private String person;
    private int personId;
    private String address;
    private String body;
    private long date;
    private boolean incoming;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPerson() {
        return this.person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public int getPersonId() {
        return this.personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isIncoming() {
        return this.incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[Message: ");
        builder.append("Date: ").append(new Date(getDate()));
        builder.append(", Incoming: ").append(isIncoming());
        builder.append(", Body: ").append(getBody());
        builder.append("]");
        
        return builder.toString();
    }    
}
