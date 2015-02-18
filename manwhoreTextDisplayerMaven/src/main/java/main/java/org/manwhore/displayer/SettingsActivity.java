package org.manwhore.displayer;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import com.crittercism.NewFeedbackSpringboardActivity;
import com.google.inject.Inject;
import org.manwhore.displayer.base.BasePreferenceActivity;
import org.manwhore.displayer.util.Constants;
import org.manwhore.displayer.util.Dialogs;
import org.manwhore.displayer.util.ObjectProvider;
import org.manwhore.displayer.webservice.IWSManager;

/**
 * Created by IntelliJ IDEA.
 * User: sigi
 * Date: 13.3.2011
 * Time: 07:53
 * To change this template use File | Settings | File Templates.
 */
public class SettingsActivity extends BasePreferenceActivity {

    private static final int DLG_PROGRESS = 1;
    
    private CheckTask task;
    
    @Inject
    private ObjectProvider<IWSManager> wsmProvider;
    
//    @InjectPreference("mwAdjustValue")
//    private ListPreference listPref;

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DLG_PROGRESS: {
                final ProgressDialog progressDlg = new ProgressDialog(SettingsActivity.this);
                progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDlg.setMessage(getResources().getString(R.string.ui_label_communicating));
                progressDlg.setCancelable(false);
                progressDlg.setIndeterminate(true);
                return progressDlg;
            }
            default: {
                return super.onCreateDialog(id);
            }
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference checkPref = (Preference) findPreference("mwCheckProfilePref");
        checkPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                task = new CheckTask();
                task.setCallerActivity(SettingsActivity.this);
                task.execute();
                
                return true;
            }
        });
        
        Preference forumPref = (Preference) findPreference("mwSupportForumPref");
        forumPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, NewFeedbackSpringboardActivity.class);
                startActivity(i);
                
                return true;
            }
        });
        
        SettingsActivity sa = (SettingsActivity) getLastNonConfigurationInstance();
        if (sa != null) {
            task = sa.task;
            if (task != null) {
                task.setCallerActivity(this);
            }
        }
        
        /*
        ArrayList<String> tzNames = new ArrayList<String>();
        Collections.sort(tzNames);        
        String[] ids = TimeZone.getAvailableIDs();
        for (String tzId : ids) {
            TimeZone timezone = TimeZone.getTimeZone(tzId);            
            tzNames.add(timezone.getID());            
        }
        
        String[] names = tzNames.toArray(new String[] {});
        
        listPref.setEntries(names);
        listPref.setEntryValues(names);
         * 
         */
    }

    private class CheckTask extends AsyncTask<Object, Object, Integer> {

        private Activity callerActivity;
        
        private IWSManager wsm;


        public synchronized void setCallerActivity(Activity callerActivity) {
            this.callerActivity = callerActivity;
        }

        public synchronized Activity getCallerActivity() {
            return callerActivity;
        }
        
        @Override
        protected Integer doInBackground(Object... arg0) {            
            try {
                this.wsm = wsmProvider.get();                
            } catch (Exception ex) {
                return Constants.WSRESULT_CALL_FAIL;
            }
            
            Integer retVal = Integer.valueOf(wsm.login());
            wsm.logout();
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            Activity ac = getCallerActivity();
            if (ac != null) {
                ac.dismissDialog(DLG_PROGRESS);
                if (result.equals(Constants.WSRESULT_OK)) {
                    Dialogs.alertBox(callerActivity, R.string.ui_dialog_success_title, R.string.ui_label_login_ok);
                } else if (result.equals(Constants.WSRESULT_NOT_LOGGED_IN)) {
                    Dialogs.alertBox(callerActivity, R.string.ui_dialog_error_title, R.string.err_login_failed);
                } else {
                    Dialogs.alertBox(callerActivity, R.string.ui_dialog_error_title, R.string.err_webservice_failed);
                }
            }            
        }

        @Override
        protected void onPreExecute() {
            Activity ac = getCallerActivity();
            if (ac != null) {
                ac.showDialog(DLG_PROGRESS);
            }
        }        
        
    }
}
