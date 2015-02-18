/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer;

import org.manwhore.displayer.export.ExportResult;

/**
 *
 * @author sigi
 */
public interface IExportingActivity {
    
    public void invokeExport();
    
    public void onExportStarted();
        
    public void onExportProgress(int progress);
    
    public void onExportFinished(ExportResult result);
}
