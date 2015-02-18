/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.logging;

/**
 *
 * @author sigi
 */
public class Util {

    public static String obfuscateShort(String what) {
        if (what == null) {
            return null;
        }
        
        int len = what.length();
        if (len > 4) {
            return what.substring(0, len - 3) + "***"; 
        }
        
        return what;
    }
}
