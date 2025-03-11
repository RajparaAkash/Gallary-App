package com.gallaryapp.privacyvault.photoeditor.Ads;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.gallaryapp.privacyvault.photoeditor.MyApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.NetworkUtil;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

public class SplashAds {

    public static void ShowSplashAd(Activity mActivity, Intent intent) {
        if (NetworkUtil.isNetworkConnected(mActivity)) {
            if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
                MyApp.AdsLoad(initializationStatus -> {
                    if (MyAdsPreference.get_AdStyleSplash().equalsIgnoreCase("appopen")) {
                        if (MyAdsPreference.get_FlagAppOpen().equalsIgnoreCase("on")) {
                            Splash_AppOpen(mActivity, intent);
                        } else {
                            mActivity.startActivity(intent);
                            mActivity.finish();
                        }
                    } else {
                        Splash_Interstitial(mActivity, intent);
                    }
                });
            } else {
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        } else {
            mActivity.startActivity(intent);
            mActivity.finish();
        }
    }

    public static void Splash_AppOpen(Activity mActivity, Intent intent) {
        if (MyApp.isFullScreenShow) {
            return;
        }
        try {
            AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                    super.onAdLoaded(appOpenAd);
                    FullScreenContentCallback r0 = new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mActivity.startActivity(intent);
                            mActivity.finish();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            mActivity.startActivity(intent);
                            mActivity.finish();
                        }
                    };
                    appOpenAd.show(mActivity);
                    appOpenAd.setFullScreenContentCallback(r0);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);

                    // AppOpen Fail
                    Splash_Interstitial(mActivity, intent);
                }
            };

            AppOpenAd.load(mActivity, MyAdsPreference.get_IdAppOpenGoogle(), new AdRequest.Builder().build(), loadCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Splash_Interstitial(Activity mActivity, Intent intent) {
        if (MyAdsPreference.get_FlagInterstitial().equalsIgnoreCase("on")) {
            new InterstitialAdsSplash().ShowInterstitial(mActivity, () -> {
                mActivity.startActivity(intent);
                mActivity.finish();
            });
        } else {
            mActivity.startActivity(intent);
            mActivity.finish();
        }
    }
}
