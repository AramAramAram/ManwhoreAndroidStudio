/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.export;

/**
 *
 * @author sigi
 */
public class ExportException extends Exception {
    private int code;

    public ExportException(int code, Throwable t) {
        super(t);
        this.code = code;
    }
    
    public ExportException(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
    
}
