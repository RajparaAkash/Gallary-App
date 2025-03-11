package com.gallaryapp.privacyvault.photoeditor.Ads;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.gallaryapp.privacyvault.photoeditor.MyApp;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class AppOpenAdManager implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private static final String LOG_TAG = "AppOpenAdManager";
    private AppOpenAd appOpenAd = null;
    private static boolean isShowingAd = false;
    public static boolean isSplash = false;
    private long loadTime = 0;
    private Activity currentActivity;
    private final MyApp myApplication;

    public AppOpenAdManager(MyApp myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public void fetchAd() {
        if (isAdAvailable()) {
            return;
        }

        AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                super.onAdLoaded(appOpenAd);
                AppOpenAdManager.this.appOpenAd = appOpenAd;
                AppOpenAdManager.this.loadTime = (new Date()).getTime();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        };
        if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
            if (MyAdsPreference.get_FlagAppOpen().equalsIgnoreCase("on")) {

                AdRequest request = new AdRequest.Builder().build();
                AppOpenAd.load(myApplication, MyAdsPreference.get_IdAppOpenGoogle(), request, loadCallback);
            }
        }
    }

    public void showAdIfAvailable() {

        if (MyApp.isFullScreenShow) {
            return;
        }
        if (!isShowingAd && isAdAvailable()) {

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {

                            AppOpenAdManager.this.appOpenAd = null;
                            isShowingAd = false;
                            fetchAd();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };
            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);

        } else {
            fetchAd();
        }
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo();
    }

    private boolean wasLoadTimeLessThanNHoursAgo() {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * (long) 4));
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStop(owner);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        if (isSplash) {
            Log.d(LOG_TAG, "no open ad for splash");
        } else {
            showAdIfAvailable();
        }
    }
}