package com.gallaryapp.privacyvault.photoeditor.BgRemover;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

import com.gallaryapp.privacyvault.photoeditor.R;

public class DialogUtil {

    public static Dialog getDialog(Activity mContext, int layout) {
        try {
            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(layout);
            dialog.getWindow().setLayout(-1, -2);
            dialog.getWindow().setGravity(17);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            return dialog;
        } catch (Exception e) {
            return null;
        }
    }
}
