package com.gallaryapp.privacyvault.photoeditor.Ads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.gallaryapp.privacyvault.photoeditor.MyApp;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class InterstitialAds {

    public static long lastAdShowTime = 0;

    public static InterstitialAd admob_interstitialAd;
    public static AdManagerInterstitialAd adx_interstitialAd;
    public static com.facebook.ads.InterstitialAd fb_interstitialAd;

    public static void LoadInterstitialAds(Context context) {
        Interstitial_Load(context);
    }

    private static void Interstitial_Load(Context context) {
        if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google") && MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("admob")) {
            LoadInterstitial_Admob(context);
        } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google") && MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("adx")) {
            LoadInterstitial_Adx(context);
        } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("fb")) {
            LoadInterstitial_Fb(context);
        }
    }

    private static void LoadInterstitial_Admob(Context context) {
        if (admob_interstitialAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context, MyAdsPreference.get_IdInterstitialGoogle(), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAds) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            admob_interstitialAd = interstitialAds;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            admob_interstitialAd = null;
                        }
                    });
        }
    }

    private static void LoadInterstitial_Adx(Context context) {
        if (adx_interstitialAd == null) {
            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
            AdManagerInterstitialAd.load(context, MyAdsPreference.get_IdInterstitialGoogle(), adRequest,
                    new AdManagerInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAds) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            adx_interstitialAd = interstitialAds;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            adx_interstitialAd = null;
                        }
                    });
        }
    }

    private static void LoadInterstitial_Fb(Context context) {
        if (fb_interstitialAd == null) {
            fb_interstitialAd = new com.facebook.ads.InterstitialAd(context, MyAdsPreference.get_IdInterstitialFb());
            // Create listeners for the Interstitial Ad
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    // Interstitial ad displayed callback
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Ad error callback
                    fb_interstitialAd = null;
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Interstitial ad is loaded and ready to be displayed

                    // Show the ad
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback

                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback

                }
            };

            // For auto play video ads, it's recommended to load the ad
            // at least 30 seconds before it is shown
            fb_interstitialAd.loadAd(
                    fb_interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        }
    }


    public static void ShowInterstitial(Activity activity, OnCloseListener onCloseListener) {
        if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
            if (MyAdsPreference.get_FlagInterstitial().equalsIgnoreCase("on")) {

                long currentTime = System.currentTimeMillis();
                long timeSinceLastAd = currentTime - lastAdShowTime;

                if (timeSinceLastAd >= Long.valueOf(MyAdsPreference.get_AdShowTime())) {
                    MyApp.isFullScreenShow = true;

                    if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google") && MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("admob")) {
                        ShowInterstitial_Admob(activity, onCloseListener);
                    } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google") && MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("adx")) {
                        ShowInterstitial_Adx(activity, onCloseListener);
                    } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("fb")) {
                        ShowInterstitial_Fb(activity, onCloseListener);
                    } else {
                        onCloseListener.onClosed();
                    }

                } else {
                    Interstitial_Load(activity);
                    onCloseListener.onClosed();
                }
            } else {
                onCloseListener.onClosed();
            }
        } else {
            onCloseListener.onClosed();
        }
    }

    public static void ShowInterstitialBack(Activity activity, OnCloseListener onCloseListener) {
        if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
            if (MyAdsPreference.get_FlagInterstitial().equalsIgnoreCase("on")) {
                if (MyAdsPreference.get_FlagBackAd().equalsIgnoreCase("on")) {

                    long currentTime = System.currentTimeMillis();
                    long timeSinceLastAd = currentTime - lastAdShowTime;

                    if (timeSinceLastAd >= Long.valueOf(MyAdsPreference.get_AdShowTime())) {
                        MyApp.isFullScreenShow = true;

                        if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google") && MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("admob")) {
                            ShowInterstitial_Admob(activity, onCloseListener);
                        } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google") && MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("adx")) {
                            ShowInterstitial_Adx(activity, onCloseListener);
                        } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("fb")) {
                            ShowInterstitial_Fb(activity, onCloseListener);
                        } else {
                            onCloseListener.onClosed();
                        }

                    } else {
                        Interstitial_Load(activity);
                        onCloseListener.onClosed();
                    }
                } else {
                    onCloseListener.onClosed();
                }
            } else {
                onCloseListener.onClosed();
            }
        } else {
            onCloseListener.onClosed();
        }
    }

    public static void ShowInterstitialDirect(Activity activity, OnCloseListener onCloseListener) {
        if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
            if (MyAdsPreference.get_FlagInterstitial().equalsIgnoreCase("on")) {

                MyApp.isFullScreenShow = true;

                if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google") && MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("admob")) {
                    ShowInterstitial_Admob(activity, onCloseListener);
                } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google") && MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("adx")) {
                    ShowInterstitial_Adx(activity, onCloseListener);
                } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("fb")) {
                    ShowInterstitial_Fb(activity, onCloseListener);
                } else {
                    onCloseListener.onClosed();
                }

            } else {
                onCloseListener.onClosed();
            }
        } else {
            onCloseListener.onClosed();
        }
    }

    private static void ShowInterstitial_Admob(Activity activity, OnCloseListener onCloseListener) {
        if (admob_interstitialAd != null) {
            admob_interstitialAd.show(activity);
            admob_interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    if (onCloseListener != null) {
                        onCloseListener.onClosed();
                    }

                    admob_interstitialAd = null;

                    long currentTime = System.currentTimeMillis();
                    lastAdShowTime = currentTime;
                    MyApp.isFullScreenShow = false;

                    LoadInterstitial_Admob(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);

                    if (onCloseListener != null) {
                        onCloseListener.onClosed();
                    }

                    long currentTime = System.currentTimeMillis();
                    lastAdShowTime = currentTime;
                    MyApp.isFullScreenShow = false;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }
            });
        } else {
            LoadInterstitial_Admob(activity);
            if (onCloseListener != null) {
                onCloseListener.onClosed();
            }
        }
    }

    private static void ShowInterstitial_Adx(Activity activity, OnCloseListener onCloseListener) {
        if (adx_interstitialAd != null) {
            adx_interstitialAd.show(activity);
            adx_interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    if (onCloseListener != null) {
                        onCloseListener.onClosed();
                    }

                    adx_interstitialAd = null;

                    long currentTime = System.currentTimeMillis();
                    lastAdShowTime = currentTime;
                    MyApp.isFullScreenShow = false;

                    LoadInterstitial_Adx(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);

                    if (onCloseListener != null) {
                        onCloseListener.onClosed();
                    }

                    long currentTime = System.currentTimeMillis();
                    lastAdShowTime = currentTime;
                    MyApp.isFullScreenShow = false;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });
        } else {
            LoadInterstitial_Adx(activity);
            if (onCloseListener != null) {
                onCloseListener.onClosed();
            }
        }
    }

    private static void ShowInterstitial_Fb(Activity activity, OnCloseListener onCloseListener) {
        if (fb_interstitialAd != null && fb_interstitialAd.isAdLoaded()) {
            fb_interstitialAd.show();
            fb_interstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    if (onCloseListener != null) {
                        onCloseListener.onClosed();
                    }

                    fb_interstitialAd = null;

                    long currentTime = System.currentTimeMillis();
                    lastAdShowTime = currentTime;
                    MyApp.isFullScreenShow = false;

                    LoadInterstitial_Fb(activity);
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    if (onCloseListener != null) {
                        onCloseListener.onClosed();
                    }

                    long currentTime = System.currentTimeMillis();
                    lastAdShowTime = currentTime;
                    MyApp.isFullScreenShow = false;
                }

                @Override
                public void onAdLoaded(Ad ad) {
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });
        } else {
            LoadInterstitial_Fb(activity);
            if (onCloseListener != null) {
                onCloseListener.onClosed();
            }
        }
    }
}
