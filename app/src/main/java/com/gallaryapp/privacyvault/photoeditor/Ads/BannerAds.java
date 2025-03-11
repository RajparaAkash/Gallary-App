package com.gallaryapp.privacyvault.photoeditor.Ads;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.NetworkUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;

public class BannerAds {

    public Context context;

    public BannerAds(Context context) {
        this.context = context;
    }

    public void AdaptiveBanner(final ViewGroup viewGroup) {
        if (NetworkUtil.isNetworkConnected(context)) {
            if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
                if (MyAdsPreference.get_FlagAdaptiveBanner().equalsIgnoreCase("on")) {

                    viewGroup.setVisibility(View.VISIBLE);
                    if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google")) {
                        Google_Adaptive_Banner(viewGroup);
                    } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("fb")) {
                        Facebook_Adaptive_Banner(viewGroup);
                    }
                } else {
                    viewGroup.setVisibility(View.GONE);
                }
            } else {
                viewGroup.setVisibility(View.GONE);
            }
        } else {
            viewGroup.setVisibility(View.GONE);
        }
    }

    private void Google_Adaptive_Banner(final ViewGroup viewGroup) {

        if (MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("admob")) {
            try {
                final AdView adView = new AdView(context);
                adView.setAdSize(getAdSize(context));
                adView.setAdUnitId(MyAdsPreference.get_IdBannerGoogleAdmob());
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);

                        // GOOGLE ADMOB FAIL

                        try {
                            final AdManagerAdView adView = new AdManagerAdView(context);
                            adView.setAdUnitId(MyAdsPreference.get_IdBannerGoogleAdx());
                            adView.setAdSize(getAdSize(context));
                            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
                            adView.loadAd(adRequest);
                            adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);

                                    // GOOGLE ADX FAIL

                                    Facebook_Adaptive_Banner_Fail(viewGroup);
                                }

                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();
                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();

                                    try {
                                        viewGroup.removeAllViews();
                                        viewGroup.addView(adView);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            viewGroup.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();

                        try {
                            viewGroup.removeAllViews();
                            viewGroup.addView(adView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                viewGroup.setVisibility(View.GONE);
            }

        } else if (MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("adx")) {
            try {
                final AdManagerAdView adView = new AdManagerAdView(context);
                adView.setAdUnitId(MyAdsPreference.get_IdBannerGoogleAdx());
                adView.setAdSize(getAdSize(context));
                AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
                adView.loadAd(adRequest);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);

                        // GOOGLE ADX FAIL

                        try {
                            final AdView adView = new AdView(context);
                            adView.setAdSize(getAdSize(context));
                            adView.setAdUnitId(MyAdsPreference.get_IdBannerGoogleAdmob());
                            AdRequest adRequest = new AdRequest.Builder().build();
                            adView.loadAd(adRequest);
                            adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);

                                    // GOOGLE ADMOB FAIL

                                    Facebook_Adaptive_Banner_Fail(viewGroup);
                                }

                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();
                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();

                                    try {
                                        viewGroup.removeAllViews();
                                        viewGroup.addView(adView);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            viewGroup.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();

                        try {
                            viewGroup.removeAllViews();
                            viewGroup.addView(adView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                viewGroup.setVisibility(View.GONE);
            }
        }
    }

    private void Facebook_Adaptive_Banner(final ViewGroup viewGroup) {
        try {
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, MyAdsPreference.get_IdBannerFb(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {

                    // FACEBOOK FAIL

                    if (MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("admob")) {
                        try {
                            final AdView adView = new AdView(context);
                            adView.setAdSize(getAdSize(context));
                            adView.setAdUnitId(MyAdsPreference.get_IdBannerGoogleAdmob());
                            AdRequest adRequest = new AdRequest.Builder().build();
                            adView.loadAd(adRequest);
                            adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);

                                    // GOOGLE ADMOB FAIL

                                    try {
                                        final AdManagerAdView adView = new AdManagerAdView(context);
                                        adView.setAdUnitId(MyAdsPreference.get_IdBannerGoogleAdx());
                                        adView.setAdSize(getAdSize(context));
                                        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
                                        adView.loadAd(adRequest);
                                        adView.setAdListener(new AdListener() {
                                            @Override
                                            public void onAdClosed() {
                                                super.onAdClosed();
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                super.onAdFailedToLoad(loadAdError);
                                                viewGroup.setVisibility(View.GONE);

                                                // GOOGLE ADX FAIL
                                            }

                                            @Override
                                            public void onAdOpened() {
                                                super.onAdOpened();
                                            }

                                            @Override
                                            public void onAdLoaded() {
                                                super.onAdLoaded();

                                                try {
                                                    viewGroup.removeAllViews();
                                                    viewGroup.addView(adView);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onAdClicked() {
                                                super.onAdClicked();
                                            }

                                            @Override
                                            public void onAdImpression() {
                                                super.onAdImpression();
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        viewGroup.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();
                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();

                                    try {
                                        viewGroup.removeAllViews();
                                        viewGroup.addView(adView);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            viewGroup.setVisibility(View.GONE);
                        }

                    } else if (MyAdsPreference.get_AdStyleGoogle().equalsIgnoreCase("adx")) {
                        try {
                            final AdManagerAdView adView = new AdManagerAdView(context);
                            adView.setAdUnitId(MyAdsPreference.get_IdBannerGoogleAdx());
                            adView.setAdSize(getAdSize(context));
                            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
                            adView.loadAd(adRequest);
                            adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    super.onAdFailedToLoad(loadAdError);

                                    // GOOGLE ADX FAIL

                                    try {
                                        final AdView adView = new AdView(context);
                                        adView.setAdSize(getAdSize(context));
                                        adView.setAdUnitId(MyAdsPreference.get_IdBannerGoogleAdmob());
                                        AdRequest adRequest = new AdRequest.Builder().build();
                                        adView.loadAd(adRequest);
                                        adView.setAdListener(new AdListener() {
                                            @Override
                                            public void onAdClosed() {
                                                super.onAdClosed();
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                super.onAdFailedToLoad(loadAdError);
                                                viewGroup.setVisibility(View.GONE);

                                                // GOOGLE ADMOB FAIL
                                            }

                                            @Override
                                            public void onAdOpened() {
                                                super.onAdOpened();
                                            }

                                            @Override
                                            public void onAdLoaded() {
                                                super.onAdLoaded();

                                                try {
                                                    viewGroup.removeAllViews();
                                                    viewGroup.addView(adView);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onAdClicked() {
                                                super.onAdClicked();
                                            }

                                            @Override
                                            public void onAdImpression() {
                                                super.onAdImpression();
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        viewGroup.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();
                                }

                                @Override
                                public void onAdLoaded() {
                                    super.onAdLoaded();

                                    try {
                                        viewGroup.removeAllViews();
                                        viewGroup.addView(adView);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            viewGroup.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    try {
                        viewGroup.removeAllViews();
                        viewGroup.addView(adView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            }).build());

        } catch (Exception e) {
            e.printStackTrace();
            viewGroup.setVisibility(View.GONE);
        }
    }

    private void Facebook_Adaptive_Banner_Fail(final ViewGroup viewGroup) {
        try {
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, MyAdsPreference.get_IdBannerFb(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    viewGroup.setVisibility(View.GONE);

                    // FACEBOOK FAIL
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    try {
                        viewGroup.removeAllViews();
                        viewGroup.addView(adView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            }).build());

        } catch (Exception e) {
            e.printStackTrace();
            viewGroup.setVisibility(View.GONE);
        }
    }

    private static AdSize getAdSize(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }
}
