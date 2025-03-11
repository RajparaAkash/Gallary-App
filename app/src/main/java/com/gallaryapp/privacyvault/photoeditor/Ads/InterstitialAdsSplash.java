package com.gallaryapp.privacyvault.photoeditor.Ads;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.gallaryapp.privacyvault.photoeditor.MyApp;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class InterstitialAdsSplash {

    private static InterstitialAd admob_interstitialAdSplash;
    private static AdManagerInterstitialAd adx_interstitialAdSplash;

    public void ShowInterstitial(Activity mActivity, OnCloseListener onCloseListener) {
        if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google") && MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("admob")) {
            InterstitialAds_Admob(mActivity, onCloseListener);
        } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google") && MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("adx")) {
            InterstitialAds_Adx(mActivity, onCloseListener);
        } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("fb")) {
            InterstitialAds_Fb(mActivity, onCloseListener);
        }
    }

    public void InterstitialAds_Admob(Activity mActivity, OnCloseListener onCloseListener) {
        if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(mActivity, MyAdsPreference.get_IdInterstitialGoogle(), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    admob_interstitialAdSplash = interstitialAd;
                    admob_interstitialAdSplash.show(mActivity);
                    admob_interstitialAdSplash.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            admob_interstitialAdSplash = null;
                            MyApp.isFullScreenShow = false;

                            if (onCloseListener != null) {
                                onCloseListener.onClosed();
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            admob_interstitialAdSplash = null;
                            MyApp.isFullScreenShow = true;
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            MyApp.isFullScreenShow = false;

                            if (onCloseListener != null) {
                                onCloseListener.onClosed();
                            }
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    admob_interstitialAdSplash = null;
                    MyApp.isFullScreenShow = false;

                    if (onCloseListener != null) {
                        onCloseListener.onClosed();
                    }
                }
            });
        } else {
            if (onCloseListener != null) {
                onCloseListener.onClosed();
            }
        }
    }

    public void InterstitialAds_Adx(Activity mActivity, OnCloseListener onCloseListener) {
        if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
            AdManagerInterstitialAd.load(mActivity, MyAdsPreference.get_IdInterstitialGoogle(), adRequest, new AdManagerInterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                    adx_interstitialAdSplash = interstitialAd;
                    adx_interstitialAdSplash.show(mActivity);
                    adx_interstitialAdSplash.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            adx_interstitialAdSplash = null;
                            MyApp.isFullScreenShow = false;

                            if (onCloseListener != null) {
                                onCloseListener.onClosed();
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                            adx_interstitialAdSplash = null;
                            MyApp.isFullScreenShow = true;
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            MyApp.isFullScreenShow = false;

                            if (onCloseListener != null) {
                                onCloseListener.onClosed();
                            }
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                        }
                    });
                }


                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    adx_interstitialAdSplash = null;
                    MyApp.isFullScreenShow = false;

                    if (onCloseListener != null) {
                        onCloseListener.onClosed();
                    }
                }
            });
        } else {
            if (onCloseListener != null) {
                onCloseListener.onClosed();
            }
        }
    }

    public void InterstitialAds_Fb(Activity mActivity, OnCloseListener onCloseListener) {
        if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
            com.facebook.ads.InterstitialAd fb_interstitial = new com.facebook.ads.InterstitialAd(mActivity, MyAdsPreference.get_IdInterstitialFb());
            fb_interstitial.loadAd(
                    fb_interstitial.buildLoadAdConfig()
                            .withAdListener(new InterstitialAdListener() {
                                @Override
                                public void onInterstitialDisplayed(Ad ad) {
                                }

                                @Override
                                public void onInterstitialDismissed(Ad ad) {
                                    if (onCloseListener != null) {
                                        onCloseListener.onClosed();
                                    }
                                }

                                @Override
                                public void onError(Ad ad, com.facebook.ads.AdError adError) {

                                    // Fb Fail

                                    if (MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("admob")) {
                                        AdRequest adRequest = new AdRequest.Builder().build();
                                        InterstitialAd.load(mActivity, MyAdsPreference.get_IdInterstitialGoogle(), adRequest, new InterstitialAdLoadCallback() {
                                            @Override
                                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                                admob_interstitialAdSplash = interstitialAd;
                                                admob_interstitialAdSplash.show(mActivity);
                                                admob_interstitialAdSplash.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                        admob_interstitialAdSplash = null;
                                                        MyApp.isFullScreenShow = false;

                                                        if (onCloseListener != null) {
                                                            onCloseListener.onClosed();
                                                        }
                                                    }

                                                    @Override
                                                    public void onAdShowedFullScreenContent() {
                                                        super.onAdShowedFullScreenContent();
                                                        admob_interstitialAdSplash = null;
                                                        MyApp.isFullScreenShow = true;
                                                    }

                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        MyApp.isFullScreenShow = false;

                                                        if (onCloseListener != null) {
                                                            onCloseListener.onClosed();
                                                        }
                                                    }

                                                    @Override
                                                    public void onAdImpression() {
                                                        super.onAdImpression();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                admob_interstitialAdSplash = null;
                                                MyApp.isFullScreenShow = false;

                                                if (onCloseListener != null) {
                                                    onCloseListener.onClosed();
                                                }
                                            }
                                        });
                                    } else if (MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("adx")) {
                                        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
                                        AdManagerInterstitialAd.load(mActivity, MyAdsPreference.get_IdInterstitialGoogle(), adRequest, new AdManagerInterstitialAdLoadCallback() {
                                            @Override
                                            public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                                                adx_interstitialAdSplash = interstitialAd;
                                                adx_interstitialAdSplash.show(mActivity);
                                                adx_interstitialAdSplash.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                        adx_interstitialAdSplash = null;
                                                        MyApp.isFullScreenShow = false;

                                                        if (onCloseListener != null) {
                                                            onCloseListener.onClosed();
                                                        }
                                                    }

                                                    @Override
                                                    public void onAdShowedFullScreenContent() {
                                                        super.onAdShowedFullScreenContent();
                                                        adx_interstitialAdSplash = null;
                                                        MyApp.isFullScreenShow = true;
                                                    }

                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        MyApp.isFullScreenShow = false;

                                                        if (onCloseListener != null) {
                                                            onCloseListener.onClosed();
                                                        }
                                                    }

                                                    @Override
                                                    public void onAdImpression() {
                                                        super.onAdImpression();
                                                    }
                                                });
                                            }


                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                adx_interstitialAdSplash = null;
                                                MyApp.isFullScreenShow = false;

                                                if (onCloseListener != null) {
                                                    onCloseListener.onClosed();
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onAdLoaded(Ad ad) {
                                    if (!fb_interstitial.isAdLoaded()) {
                                        return;
                                    }
                                    if (fb_interstitial.isAdInvalidated()) {
                                        return;
                                    }
                                    fb_interstitial.show();
                                }

                                @Override
                                public void onAdClicked(Ad ad) {

                                }

                                @Override
                                public void onLoggingImpression(Ad ad) {

                                }
                            })
                            .build());
        } else {
            if (onCloseListener != null) {
                onCloseListener.onClosed();
            }
        }
    }
}
