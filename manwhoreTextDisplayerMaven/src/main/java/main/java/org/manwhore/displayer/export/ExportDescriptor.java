/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.export;

import org.manwhore.displayer.db.entity.Conversation;
import org.manwhore.displayer.filter.Filter;
import java.util.Date;

/**
 *
 * @author sigi
 */
public class ExportDescriptor {
    private Conversation conversation;
    private Filter filter = Filter.getDefaultFilter();
    private String daysBack;
    private String personName;
    private String title;
    private String hash;
    private Long lastExportDate;

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getDaysBack() {
        return daysBack;
    }

    public void setDaysBack(String daysBack) {
        this.daysBack = daysBack;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getLastExportDate() {
        return lastExportDate;
    }

    public void setLastExportDate(Long lastExportDate) {
        this.lastExportDate = lastExportDate;
    }
    
}
