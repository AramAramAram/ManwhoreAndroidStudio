/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.util;

import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;

/**
 *
 * @author sigi
 */
public class LoggerUtils {
    public static void logObject(Logger logger, String name, Object obj) {
        logger.debug("{} - {}", obj.getClass().getSimpleName(), name);
        if (obj instanceof Map) {
            Map map = (Map) obj;            
            for (Object o : map.entrySet()) {
                Entry e = (Entry) o;
                StringBuilder nextName = new StringBuilder(name);
                nextName.append("[").append(e.getKey()).append("]");
                logObject(logger, nextName.toString(), e.getValue());
            }
        } else {
            logger.debug("{} = {}", name, obj);
        }        
    }
}
