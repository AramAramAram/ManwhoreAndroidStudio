/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.manwhore.displayer.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;
import com.google.inject.Inject;
import org.manwhore.displayer.IExportingActivity;
import org.manwhore.displayer.R;
import org.manwhore.displayer.db.Tables;
import org.manwhore.displayer.db.dao.IDAOManager;
import org.manwhore.displayer.export.AbstractExportTask;
import org.manwhore.displayer.export.ExportDescriptor;
import org.manwhore.displayer.export.ExportResult;
import org.manwhore.displayer.export.web.IWebSubmitTaskFactory;
import org.manwhore.displayer.export.web.WebSubmitTask;
import org.manwhore.displayer.filter.Filter;
import org.manwhore.displayer.ui.ConversationSubmitDialog;

/**
 *
 * @author sigi
 */
public abstract class ExportingActivity extends BaseActivity implements IExportingActivity {

    public static final int DLG_SUBMIT = 900;
    public static final int DLG_ALREADY_EXISTS = 901;
    public static final int DLG_EXPORT_PROGRESS = 902;
    public static final int DLG_RESULT = 903;
    @Inject
    private IWebSubmitTaskFactory taskFactory;
    @Inject
    private IDAOManager daoManager;
    @Inject
    private PowerManager powerManager;
    private WebSubmitTask submitTask;
    private ExportDescriptor descriptor = new ExportDescriptor();
    private ExportResult result = null;
    private ProgressDialog progressDlg;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
            case DLG_SUBMIT: {
                dialog = getSubmitDialog();
                break;
            }
            case DLG_ALREADY_EXISTS: {
                dialog = getAlreadySubmittedDialog();
                break;
            }
            case DLG_EXPORT_PROGRESS: {
                ProgressDialog progressDlg = new ProgressDialog(this);
                progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDlg.setMessage(getResources().getString(R.string.ui_label_submitting));
                progressDlg.setCancelable(false);

                dialog = progressDlg;
                break;
            }
            case DLG_RESULT: {
                dialog = getResultDialog();
                break;
            }
            default: {
                dialog = super.onCreateDialog(id);
            }
        }

        if (dialog != null) {
            onPrepareDialog(id, dialog);
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DLG_SUBMIT: {
                ConversationSubmitDialog conversationSubmitDialog = (ConversationSubmitDialog) dialog;
                if (descriptor.getConversation() != null) {                    
                    getDaoManager().getConversationDAO().loadSubmittedData(descriptor);
                }

                conversationSubmitDialog.setConversationDaysBack(descriptor.getDaysBack());
                conversationSubmitDialog.setConversationTitle(descriptor.getTitle());
                conversationSubmitDialog.setConversationPersonName(descriptor.getPersonName());
                break;
            }
            case DLG_EXPORT_PROGRESS: {
                Cursor cursor = null;
                
                if (getExportDescriptor().getConversation() != null) {
                    cursor = getDaoManager().getMessageDAO().getCursor(getExportDescriptor().getConversation(),
                        getExportDescriptor().getFilter());
                }

                int max = 0;
                if (cursor != null) {
                    max = cursor.getCount();
                    cursor.close();
                }
                //progress dialog
                ProgressDialog progressDlg = (ProgressDialog) dialog;
                progressDlg.setMax(max);
                progressDlg.setProgress(0);                                
                this.progressDlg = progressDlg;
                break;
            }
            case DLG_RESULT: {
                AlertDialog alert = (AlertDialog) dialog;
                if (result.isError()) {
                    alert.setTitle(R.string.ui_dialog_error_title);
                    alert.setMessage(getResources().getString(error2resource(result.getErrCode())));
                } else {
                    alert.setTitle(R.string.ui_dialog_conversation_submitted_as_new_title);
                    alert.setMessage(getResources().getString(R.string.err_conversation_not_found_on_server));
                }
                break;
            }
        }
    }

    public void onExportStarted() {

        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Exporting");
        wakeLock.acquire();
        showDialog(DLG_EXPORT_PROGRESS);
    }

    public void onExportProgress(int progress) {
        if (progressDlg != null) {
            progressDlg.setProgress(progress);
        }
    }

    public void onExportFinished(ExportResult result) {
        dismissDialog(DLG_EXPORT_PROGRESS);
        this.result = result;

        if (!result.isError() && Integer.valueOf(WebSubmitTask.MSG_TYPE_FINISHED).equals(result.getResultCode())) {
            Toast.makeText(this, R.string.toast_conversation_submitted, Toast.LENGTH_SHORT).show();
        } else {
            showDialog(DLG_RESULT);
        }
        getExportDescriptor().setDaysBack(null);
        getExportDescriptor().setHash(null);
        getExportDescriptor().setLastExportDate(null);
        getExportDescriptor().setPersonName(null);
        getExportDescriptor().setTitle(null);

        this.submitTask = null;
        this.progressDlg = null;

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        wakeLock = null;
    }

    protected AlertDialog getResultDialog() {
        //possible error dialog
        final AlertDialog.Builder alertDlgBuilder = new AlertDialog.Builder(this);
        alertDlgBuilder.setPositiveButton(R.string.ui_btn_ok, null);

        return alertDlgBuilder.create();
    }

    protected ConversationSubmitDialog getSubmitDialog() {
        final ConversationSubmitDialog conversationSubmitDialog = new ConversationSubmitDialog(this) {

            @Override
            public void onSubmitOptionsSet(String title, String person, Filter filter) {
                getExportDescriptor().setTitle(title);
                getExportDescriptor().setPersonName(person);
                getExportDescriptor().setFilter(filter);
                submitTask = taskFactory.create(getExportDescriptor());
                submitTask.setCallerActivity(ExportingActivity.this);
                submitTask.exportConversation();
            }
        };
        return conversationSubmitDialog;
    }

    private AlertDialog getAlreadySubmittedDialog() {

        AlertDialog.Builder alreadySubmittedDialog = new AlertDialog.Builder(this);
        alreadySubmittedDialog.setTitle(R.string.ui_dialog_already_submitted_title);
        alreadySubmittedDialog.setMessage(R.string.ui_label_conversation_already_submitted);
        alreadySubmittedDialog.setPositiveButton(R.string.ui_btn_conversation_submit_add_new,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {

                        Filter filter = Filter.getDefaultFilter();
                        filter.setTypeDateFrom(Tables.Filters.DATETYPE_ABSOLUTE);

                        //add a millisecond, because of <= relation in Conversation processing
                        filter.setAbsDateFrom(getExportDescriptor().getLastExportDate() + 1);
                        getExportDescriptor().setFilter(filter);
                        submitTask = taskFactory.create(getExportDescriptor());
                        submitTask.setCallerActivity(ExportingActivity.this);
                        submitTask.exportConversation();
                    }
                });
        alreadySubmittedDialog.setNeutralButton(R.string.ui_btn_conversation_submit_add,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        showDialog(DLG_SUBMIT);
                    }
                });
        alreadySubmittedDialog.setNegativeButton(R.string.ui_btn_conversation_submit_new,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        getExportDescriptor().setHash(null);
                        showDialog(DLG_SUBMIT);
                    }
                });

        return alreadySubmittedDialog.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExportingActivity retained = (ExportingActivity) getLastNonConfigurationInstance();

        if (retained != null) {
            this.submitTask = retained.submitTask;
            this.descriptor = retained.descriptor;
            this.result = retained.result;

            if (submitTask != null) {
                submitTask.setCallerActivity(this);
                if (retained.wakeLock != null) {
                    wakeLock = retained.wakeLock;                    
                    if (!wakeLock.isHeld()) {
                        wakeLock.acquire();
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public IDAOManager getDaoManager() {
        return daoManager;
    }

    public ExportDescriptor getExportDescriptor() {
        return descriptor;
    }

    private static int error2resource(int code) {
        int res;

        switch (code) {
            case WebSubmitTask.ERR_CONNECTION_ERROR: {
                res = R.string.err_submit_failed_connection;
                break;
            }
            case WebSubmitTask.ERR_LOGIN_FAILED: {
                res = R.string.err_login_failed;
                break;
            }
            case WebSubmitTask.ERR_PROFILE_UNDEFINED: {
                res = R.string.ui_label_profile_empty;
                break;
            }
            case AbstractExportTask.ERR_CONVERSATION_EMPTY: {
                res = R.string.err_conversation_empty;
                break;
            }
            default: {
                res = R.string.err_submit_failed_generic;
            }
        }
        return res;
    }

    @Override
    public void invokeExport() {
        getDaoManager().getConversationDAO().loadSubmittedData(getExportDescriptor());
        if (getExportDescriptor().getLastExportDate() != null) {
            showDialog(DLG_ALREADY_EXISTS);
        } else {
            showDialog(DLG_SUBMIT);
        }
    }
}
