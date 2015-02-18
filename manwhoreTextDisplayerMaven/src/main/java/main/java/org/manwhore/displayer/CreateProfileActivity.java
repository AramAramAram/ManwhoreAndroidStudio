package org.manwhore.displayer;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import com.google.inject.Inject;
import org.manwhore.displayer.base.BaseActivity;
import org.manwhore.displayer.util.Constants;
import org.manwhore.displayer.util.Dialogs;
import org.manwhore.displayer.util.ObjectProvider;
import org.manwhore.displayer.webservice.IWSManager;

/**
 * Created by IntelliJ IDEA.
 * User: sigi
 * Date: 26.3.2011
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
public class CreateProfileActivity extends BaseActivity {

    private EditText userName, password, rePassword, email, reEmail;
    @Inject
    private ObjectProvider<IWSManager> wsmProvider;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_profile);

        userName = (EditText) findViewById(R.id.createProfileUsername);
        password = (EditText) findViewById(R.id.createProfilePassword);
        rePassword = (EditText) findViewById(R.id.createProfileRepeatPassword);
        email = (EditText) findViewById(R.id.createProfileEmail);
        reEmail = (EditText) findViewById(R.id.createProfileRepeatEmail);

        Button btnSubmit = (Button) findViewById(R.id.btnCreateProfileSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                submit();
            }
        });

    }

    protected void submit() {

        final String user = userName.getText().toString();
        final String pass = password.getText().toString();
        final String mail = email.getText().toString();

        if (user.trim().equals("")) {
            Dialogs.alertBox(this, R.string.ui_dialog_error_title, R.string.err_username_empty);
            return;
        }

        if (pass.trim().equals("")) {
            Dialogs.alertBox(this, R.string.ui_dialog_error_title, R.string.err_password_empty);
            return;
        }

        if (!pass.equals(rePassword.getText().toString())) {
            Dialogs.alertBox(this, R.string.ui_dialog_error_title, R.string.err_password_mismatch);
            return;
        }

        if (!mail.trim().matches("^\\w+(\\.\\w+)*@\\w+(\\.\\w+)*\\.\\w+$")) {
            Dialogs.alertBox(this, R.string.ui_dialog_error_title, R.string.err_email_invalid);
            return;
        }

        if (!mail.equals(reEmail.getText().toString())) {
            Dialogs.alertBox(this, R.string.ui_dialog_error_title, R.string.err_email_mismatch);
            return;
        }

        try {
            final IWSManager wsm = wsmProvider.get();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Failed creating WSManager instance", e);
            Dialogs.alertBox(this, R.string.ui_dialog_error_title, R.string.err_webservice_failed);
            return;
        }

        final ProgressDialog progressDlg = new ProgressDialog(this);
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDlg.setMessage(getResources().getString(R.string.ui_label_communicating));
        progressDlg.setCancelable(false);
        progressDlg.setIndeterminate(true);

        Runnable worker = new Runnable() {

            private Handler handler = new Handler() {

                @Override
                public void handleMessage(Message msg) {

                    if (msg.what == 0) {
                        progressDlg.show();
                    } else {
                        progressDlg.dismiss();

                        int result = msg.arg1;

                        if (result == Constants.WSRESULT_OK) {
                            Dialogs.alertBox(CreateProfileActivity.this, R.string.ui_dialog_success_title, R.string.ui_label_create_profile_success, new Runnable() {

                                public void run() {

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CreateProfileActivity.this);

                                    SharedPreferences.Editor editor = prefs.edit();

                                    editor.putString(Constants.SETTINGS_USER, user);
                                    editor.putString(Constants.SETTINGS_PASS, pass);

                                    editor.commit();

                                    CreateProfileActivity.this.finish();
                                }
                            });

                        } else if (result == Constants.WSRESULT_SERVER_ERROR) {
                            Dialogs.alertBox(CreateProfileActivity.this, R.string.ui_dialog_error_title, R.string.err_webservice_server_error);
                        } else {
                            Dialogs.alertBox(CreateProfileActivity.this, R.string.ui_dialog_error_title, R.string.err_webservice_failed);
                        }
                    }
                }
            };

            public void run() {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);

                msg = new Message();
                msg.what = 1;
                
                try {
                    IWSManager wsm = wsmProvider.get();
                    msg.arg1 = wsm.createUser(user, pass, mail);                    
                
                } catch (Exception ex) {
                    msg.arg1 = Constants.WSRESULT_CALL_FAIL;
                }
                handler.sendMessage(msg);
            }
        };
        new Thread(worker).start();
    }
}