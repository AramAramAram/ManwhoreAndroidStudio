/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.export;

/**
 *
 * @author sigi
 */
public class ExportResult {
    Integer exportedCount = 0;
    Integer errCode = null;
    Integer resultCode = 0;

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public Integer getExportedCount() {
        return exportedCount;
    }

    public void setExportedCount(Integer exportedCount) {
        this.exportedCount = exportedCount;
    }
    
    public boolean isError() {
        return errCode != null;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }        
}
