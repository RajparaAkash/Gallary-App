package com.gallaryapp.privacyvault.photoeditor;

import android.app.Application;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.AppOpenAdManager;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.MyAdsPreference;
import com.gallaryapp.privacyvault.photoeditor.Ads.NativeAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.RewardAds;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.LocaleManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class MyApp extends Application {

    public static boolean isFullScreenShow = false;

    private static MyApp mInstance;

    public static MyApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocaleManager.setLocale(this);
        mInstance = this;

        AudienceNetworkAds.initialize(this);
        if (BuildConfig.DEBUG) {
            AdSettings.setTestMode(true);
        }

        FirebaseApp.initializeApp(getApplicationContext());

        FirebaseAnalytics.getInstance(getApplicationContext()).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);
    }

    public static void AdsLoad(OnInitializationCompleteListener listener) {
        MobileAds.initialize(
                mInstance,
                initializationStatus -> {
                    listener.onInitializationComplete(initializationStatus);

                    if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
                        if (MyAdsPreference.get_FlagAppOpen().equalsIgnoreCase("on")) {
                            new AppOpenAdManager(mInstance);
                        }
                        if (MyAdsPreference.get_FlagInterstitial().equalsIgnoreCase("on")) {
                            InterstitialAds.LoadInterstitialAds(mInstance);
                        }
                        if (MyAdsPreference.get_FlagRewarded().equalsIgnoreCase("on")) {
                            RewardAds.LoadRewardedAds(mInstance);
                        }
                        if (MyAdsPreference.get_FlagNative().equalsIgnoreCase("on")) {
                            NativeAds.LoadNativeAds(mInstance);
                        }
                    }
                });
    }
}
