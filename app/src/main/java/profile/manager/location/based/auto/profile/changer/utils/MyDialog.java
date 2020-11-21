package profile.manager.location.based.auto.profile.changer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;

public class MyDialog extends AlertDialog {


    Context context;
    AlertDialog dialog;
    AlertDialog.Builder builder;

    public MyDialog(Context context) {
        super(context);
        this.context = context;
    }

    public void createDialog(String title, String message) {
        builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false);
        dialog = builder.create();


    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
