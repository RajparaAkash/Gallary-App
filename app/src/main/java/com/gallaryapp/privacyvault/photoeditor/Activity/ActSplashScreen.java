package com.gallaryapp.privacyvault.photoeditor.Activity;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatDelegate;

import com.gallaryapp.privacyvault.photoeditor.Ads.MyAdsPreference;
import com.gallaryapp.privacyvault.photoeditor.Ads.SplashAds;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.NetworkUtil;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class ActSplashScreen extends ActBase {

    private boolean checkPermissionsAgain = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(MyPreference.get_Theme());
        setContentView(R.layout.act_splash_screen);

        setOnBackPressed();

        if (NetworkUtil.isNetworkConnected(this)) {
            getFireBaseData();
        } else {
            directDashBoard();
        }
    }

    private void getFireBaseData() {

        MyAdsPreference.set_AdStatus("on");
        MyAdsPreference.set_AdShowTime("60000");
        MyAdsPreference.set_AdStyleMain("google");
        MyAdsPreference.set_AdStyleSplash("appopen");
        MyAdsPreference.set_AdStyleGoogle("admob");

        MyAdsPreference.set_FlagBackAd("on");
        MyAdsPreference.set_FlagAppOpen("on");
        MyAdsPreference.set_FlagInterstitial("on");
        MyAdsPreference.set_FlagAdaptiveBanner("on");
        MyAdsPreference.set_FlagNative("on");
        MyAdsPreference.set_FlagRewarded("on");

        MyAdsPreference.set_IdAppOpenGoogle("ca-app-pub-3940256099942544/9257395921");
        MyAdsPreference.set_IdInterstitialGoogle("ca-app-pub-3940256099942544/1033173712");
        MyAdsPreference.set_IdBannerGoogleAdmob("ca-app-pub-3940256099942544/9214589741");
        MyAdsPreference.set_IdBannerGoogleAdx("/21775744923/example/adaptive-banner");
        MyAdsPreference.set_IdNativeGoogle("ca-app-pub-3940256099942544/2247696110");
        MyAdsPreference.set_IdRewardedGoogle("ca-app-pub-3940256099942544/5224354917");

        MyAdsPreference.set_IdInterstitialFb("VID_HD_9_16_39S_APP_INSTALL#YOUR_PLACEMENT_ID");
        MyAdsPreference.set_IdBannerFb("IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");

        MyAdsPreference.set_PrivacyPolicy("https://www.google.co.in/");
        MyAdsPreference.set_IsFullScreen("off");

        nextScreen();

        /*FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Set default Remote Config parameter values.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                String jsonString = firebaseRemoteConfig.getString("Gallery_2_1");
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    MyAdsPreference.set_AdStatus(jsonObject.getString("adStatus"));
                    MyAdsPreference.set_AdShowTime(jsonObject.getString("adShowTime"));
                    MyAdsPreference.set_AdStyleMain(jsonObject.getString("adStyleMain"));
                    MyAdsPreference.set_AdStyleSplash(jsonObject.getString("adStyleSplash"));
                    MyAdsPreference.set_AdStyleGoogle(jsonObject.getString("adStyleGoogle"));

                    MyAdsPreference.set_FlagBackAd(jsonObject.getString("flagBackAd"));
                    MyAdsPreference.set_FlagAppOpen(jsonObject.getString("flagAppOpen"));
                    MyAdsPreference.set_FlagInterstitial(jsonObject.getString("flagInterstitial"));
                    MyAdsPreference.set_FlagAdaptiveBanner(jsonObject.getString("flagAdaptiveBanner"));
                    MyAdsPreference.set_FlagNative(jsonObject.getString("flagNative"));
                    MyAdsPreference.set_FlagRewarded(jsonObject.getString("flagRewarded"));

                    MyAdsPreference.set_IdAppOpenGoogle(jsonObject.getString("idAppOpenGoogle"));
                    MyAdsPreference.set_IdInterstitialGoogle(jsonObject.getString("idInterstitialGoogle"));
                    MyAdsPreference.set_IdBannerGoogleAdmob(jsonObject.getString("idBannerGoogleAdmob"));
                    MyAdsPreference.set_IdBannerGoogleAdx(jsonObject.getString("idBannerGoogleAdx"));
                    MyAdsPreference.set_IdNativeGoogle(jsonObject.getString("idNativeGoogle"));
                    MyAdsPreference.set_IdRewardedGoogle(jsonObject.getString("idRewardedGoogle"));

                    MyAdsPreference.set_IdInterstitialFb(jsonObject.getString("idInterstitialFb"));
                    MyAdsPreference.set_IdBannerFb(jsonObject.getString("idBannerFb"));
                    MyAdsPreference.set_IdNativeFb(jsonObject.getString("idNativeFb"));

                    MyAdsPreference.set_PrivacyPolicy(jsonObject.getString("privacyPolicy"));
                    MyAdsPreference.set_IsFullScreen(jsonObject.getString("isFullScreen"));

                    nextScreen();

                } catch (JSONException e) {
                    e.printStackTrace();
                    nextScreen();
                }

            } else {
                Exception exception = task.getException();
                if (exception instanceof FirebaseRemoteConfigFetchThrottledException) {
                    exception.printStackTrace();
                } else if (exception instanceof FirebaseRemoteConfigException) {
                    exception.printStackTrace();
                }
                nextScreen();
            }
        });*/
    }

    private void nextScreen() {
        if (MyPreference.get_IsFirstTime()) {
            SplashAds.ShowSplashAd(ActSplashScreen.this, sendIntentIntro());
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkPermission();
                }
            }, 500);
        }
    }

    private Intent sendIntentIntro() {
        return new Intent(ActSplashScreen.this, ActIntro.class);
    }

    private Intent sendIntentDashBoard() {
        return new Intent(ActSplashScreen.this, ActDashboard.class);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissionsList = {
                    "android.permission.READ_MEDIA_IMAGES",
                    "android.permission.READ_MEDIA_VIDEO",
                    "android.permission.CAMERA"};

            Dexter.withContext(ActSplashScreen.this).withPermissions(permissionsList).withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {

                        nextDashBoard();
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        UtilDialog.showPermissionDialog(ActSplashScreen.this);
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(dexterError -> {
                Toast.makeText(ActSplashScreen.this, "Error occurred! ", LENGTH_SHORT).show();
            }).onSameThread().check();

        } else {
            String[] permissionsList = {
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.CAMERA"};

            if (Build.VERSION.SDK_INT >= 29)
                permissionsList = new String[]{
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.CAMERA"};

            Dexter.withContext(ActSplashScreen.this).withPermissions(permissionsList).withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {

                        nextDashBoard();
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        UtilDialog.showPermissionDialog(ActSplashScreen.this);
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(dexterError -> {
                Toast.makeText(ActSplashScreen.this, "Error occurred!", Toast.LENGTH_SHORT).show();
            }).onSameThread().check();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            checkPermissionsAgain = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissionsAgain) {
            checkPermissionsAgain = false;
            checkPermission();
        }
    }

    public void nextDashBoard() {
        if (NetworkUtil.isNetworkConnected(this)) {
            SplashAds.ShowSplashAd(ActSplashScreen.this, sendIntentDashBoard());
        } else {
            startActivity(new Intent(ActSplashScreen.this, ActDashboard.class));
            finish();
        }
    }

    public void directDashBoard() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MyPreference.get_IsFirstTime()) {
                    startActivity(new Intent(ActSplashScreen.this, ActIntro.class));
                    finish();
                } else {
                    checkPermission();
                }
            }
        }, 500);
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);
    }
}