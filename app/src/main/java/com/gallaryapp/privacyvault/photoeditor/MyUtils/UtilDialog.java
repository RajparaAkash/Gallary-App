package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;

import com.gallaryapp.privacyvault.photoeditor.R;

public class UtilDialog {

    public static Dialog getDialog(Context mContext, int layout) {
        try {
            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(layout);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            return dialog;
        } catch (Exception e) {
            return null;
        }
    }


    public static void showPermissionDialog(final Activity activity) {

        Dialog dialogPermission = getDialog(activity, R.layout.dialog_permission_allow);
        dialogPermission.setCancelable(false);

        dialogPermission.findViewById(R.id.cancelTxt).setOnClickListener(view -> {
            dialogPermission.dismiss();
            activity.finishAffinity();
            System.exit(0);
        });

        dialogPermission.findViewById(R.id.settingsTxt).setOnClickListener(view -> {
            dialogPermission.dismiss();
            openAppSettings(activity);
        });

        dialogPermission.show();
    }

    public static void openAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(),  null));
        activity.startActivityForResult(intent, 1001);
    }
}
