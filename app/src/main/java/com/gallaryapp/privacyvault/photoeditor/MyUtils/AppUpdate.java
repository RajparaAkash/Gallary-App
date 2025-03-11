package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import static android.app.Activity.RESULT_CANCELED;

import android.app.Activity;
import android.content.IntentSender;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class AppUpdate {

    private final Activity mActivity;
    private final AppUpdateManager appUpdateManager;

    private final int appUpdateType = AppUpdateType.FLEXIBLE;
    private final int MY_REQUEST_CODE = 500;

    public AppUpdate(Activity activity) {
        this.mActivity = activity;
        appUpdateManager = AppUpdateManagerFactory.create(mActivity);
    }

    InstallStateUpdatedListener stateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackBarForCompleteUpdate();
        }
    };

    public void checkAppUpdate() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(info -> {
            boolean isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE;
            boolean isUpdateAllowed = info.isUpdateTypeAllowed(appUpdateType);

            if (isUpdateAvailable && isUpdateAllowed) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            info,
                            appUpdateType,
                            mActivity,
                            MY_REQUEST_CODE
                    );
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        appUpdateManager.registerListener(stateUpdatedListener);
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(mActivity, "Update canceled", Toast.LENGTH_SHORT).show();
            } else if (resultCode != AppCompatActivity.RESULT_OK) {
                checkAppUpdate();
            }
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(
                mActivity.findViewById(R.id.dashboardLay),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE
        ).setAction(
                "RESTART", view -> {
                    if (appUpdateManager != null) {
                        appUpdateManager.completeUpdate();
                    }
                }
        ).show();
    }

    public void onResume() {
        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(info -> {
                if (info.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackBarForCompleteUpdate();
                }
            });
        }
    }

    public void onDestroy() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(stateUpdatedListener);
        }
    }
}