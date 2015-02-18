package org.manwhore.displayer.export.web;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: sigi
 * Date: 5.3.2011
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
public class Message implements IMappable
{
    private int sender;
    private String text;
    private String delaytext;
    private String convhash;

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDelaytext() {
        return delaytext;
    }

    public void setDelaytext(String delaytext) {
        this.delaytext = delaytext;
    }

    public String getConvhash() {
        return convhash;
    }

    public void setConvhash(String convhash) {
        this.convhash = convhash;
    }

    public Map<String, Object> toMap()
    {
        Field[] fields = this.getClass().getDeclaredFields();

        HashMap<String,  Object> result = new HashMap<String,  Object>();

        for (Field field : fields)
        {
            try {
                result.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
