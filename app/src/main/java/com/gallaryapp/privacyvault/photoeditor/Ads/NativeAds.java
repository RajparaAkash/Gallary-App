package com.gallaryapp.privacyvault.photoeditor.Ads;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.NetworkUtil;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NativeAds {

    public static com.google.android.gms.ads.nativead.NativeAd mNativeAdGoogle = null;
    public static NativeAd mNativeAdFb = null;
    public static boolean nativeAdIsLoading = false;

    public static void LoadNativeAds(Context context) {
        if (NetworkUtil.isNetworkConnected(context)) {
            if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
                if (MyAdsPreference.get_FlagNative().equalsIgnoreCase("on")) {
                    if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("google")) {
                        NativeAd_LoadGoogle(MyAdsPreference.get_IdNativeGoogle(), context);
                    } else if (MyAdsPreference.get_AdStyleMain().equalsIgnoreCase("fb")) {
                        NativeAd_LoadFb(MyAdsPreference.get_IdNativeFb(), context);
                    }
                }
            }
        }
    }

    private static void NativeAd_LoadGoogle(String nativeAdId, Context context) {
        if (nativeAdIsLoading || mNativeAdGoogle != null) {
            return;
        }

        nativeAdIsLoading = true;

        AdLoader.Builder builder = new AdLoader.Builder(context, nativeAdId);
        builder.forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                nativeAdIsLoading = false;
                mNativeAdGoogle = nativeAd;
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                mNativeAdGoogle = null;
                nativeAdIsLoading = false;

                // Fail
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private static void NativeAd_LoadFb(String nativeAdId, Context context) {
        if (nativeAdIsLoading || mNativeAdFb != null) {
            return;
        }

        nativeAdIsLoading = true;

        mNativeAdFb = new NativeAd(context, nativeAdId);
        mNativeAdFb.loadAd(
                mNativeAdFb.buildLoadAdConfig()
                        .withAdListener(new com.facebook.ads.NativeAdListener() {
                            @Override
                            public void onMediaDownloaded(Ad ad) {
                                // Do nothing
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                mNativeAdFb = null;
                                nativeAdIsLoading = false;

                                // Fail
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                if (mNativeAdFb != null) {
                                    nativeAdIsLoading = false;
                                }
                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                // Do nothing
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                // Do nothing
                            }
                        })
                        .build());
    }

    public static void ShowNativeBig(Activity activity, ViewGroup viewGroup, ViewGroup nativeLay) {
        if (mNativeAdGoogle != null && !nativeAdIsLoading) {
            nativeLay.setVisibility(View.VISIBLE);
            viewGroup.setVisibility(View.VISIBLE);
            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_layout_google_native_big, viewGroup, false);
            populateGoogleNativeAdView(activity, mNativeAdGoogle, adView);
            viewGroup.removeAllViews();
            viewGroup.addView(adView);
            mNativeAdGoogle = null;
            nativeAdIsLoading = false;
            LoadNativeAds(activity);

        } else if (mNativeAdFb != null && !nativeAdIsLoading) {
            nativeLay.setVisibility(View.VISIBLE);
            viewGroup.setVisibility(View.VISIBLE);
            NativeAdLayout nativeAdLayout = (NativeAdLayout) activity.getLayoutInflater().inflate(R.layout.ad_layout_facebook_native_big, viewGroup, false);
            populateFbNativeAdView(activity, mNativeAdFb, nativeAdLayout);
            viewGroup.removeAllViews();
            viewGroup.addView(nativeAdLayout);
            mNativeAdFb = null;
            nativeAdIsLoading = false;
            LoadNativeAds(activity);

        } else {
            LoadNativeAds(activity);
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
            nativeLay.setVisibility(View.GONE);
        }
    }

    public static void ShowNativeSmall(Activity activity, ViewGroup viewGroup, ViewGroup nativeLay) {
        if (mNativeAdGoogle != null && !nativeAdIsLoading) {
            nativeLay.setVisibility(View.VISIBLE);
            viewGroup.setVisibility(View.VISIBLE);
            NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_layout_google_native_small, viewGroup, false);
            populateGoogleNativeAdView(activity, mNativeAdGoogle, adView);
            viewGroup.removeAllViews();
            viewGroup.addView(adView);
            mNativeAdGoogle = null;
            nativeAdIsLoading = false;
            LoadNativeAds(activity);

        } else if (mNativeAdFb != null && !nativeAdIsLoading) {
            nativeLay.setVisibility(View.VISIBLE);
            viewGroup.setVisibility(View.VISIBLE);
            NativeAdLayout nativeAdLayout = (NativeAdLayout) activity.getLayoutInflater().inflate(R.layout.ad_layout_facebook_native_small, viewGroup, false);
            populateFbNativeAdView(activity, mNativeAdFb, nativeAdLayout);
            viewGroup.removeAllViews();
            viewGroup.addView(nativeAdLayout);
            mNativeAdFb = null;
            nativeAdIsLoading = false;
            LoadNativeAds(activity);

        } else {
            LoadNativeAds(activity);
            viewGroup.removeAllViews();
            viewGroup.setVisibility(View.GONE);
            nativeLay.setVisibility(View.GONE);
        }
    }

    public static void populateGoogleNativeAdView(Activity activity, com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((com.google.android.gms.ads.nativead.MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        Objects.requireNonNull(adView.getMediaView()).setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.place_holder_img));
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.GONE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

        VideoController vc = Objects.requireNonNull(nativeAd.getMediaContent()).getVideoController();

        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }
    }

    public static void populateFbNativeAdView(Activity activity, NativeAd nativeAd, NativeAdLayout nativeAdLayout) {
        nativeAd.unregisterView();

        LinearLayout adChoicesContainer = nativeAdLayout.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = nativeAdLayout.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = nativeAdLayout.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = nativeAdLayout.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = nativeAdLayout.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = nativeAdLayout.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = nativeAdLayout.findViewById(R.id.native_ad_sponsored_label);
        TextView nativeAdCallToAction = nativeAdLayout.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        nativeAd.registerViewForInteraction(
                nativeAdLayout, nativeAdMedia, nativeAdIcon, clickableViews);

    }
}