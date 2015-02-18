/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.export.web;

import org.manwhore.displayer.export.ExportDescriptor;

/**
 *
 * @author sigi
 */
public interface IWebSubmitTaskFactory {
    public WebSubmitTask create(ExportDescriptor descriptor);        
}
