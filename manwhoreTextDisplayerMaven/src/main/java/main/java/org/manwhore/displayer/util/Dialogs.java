package org.manwhore.displayer.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import org.manwhore.displayer.R;


/**
 * Creat
 * ed by IntelliJ IDEA.
 * User: sigi
 * Date: 13.3.2011
 * Time: 09:56
 * To change this template use File | Settings | File Templates.
 */
public class Dialogs {

    public static void alertBox(Context context, int titleRes, int msgRes)
    {
        alertBox(context, titleRes, msgRes, null);
    }

    public static void alertBox(Context context, int titleRes, int msgRes, final Runnable onDismiss)
    {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
        dlgBuilder.setTitle(titleRes);
        dlgBuilder.setMessage(msgRes);
        dlgBuilder.setPositiveButton(R.string.ui_label_ok, new android.content.DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (onDismiss != null)
                    onDismiss.run();
            }
        });


        dlgBuilder.show();
    }
}
