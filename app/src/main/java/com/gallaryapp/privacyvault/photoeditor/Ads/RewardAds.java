package com.gallaryapp.privacyvault.photoeditor.Ads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.gallaryapp.privacyvault.photoeditor.MyApp;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class RewardAds {

    public static RewardedAd mRewardedAd;
    public static boolean isLoadingRewardedAd;
    public static boolean successfulRewarded = false;
    public static boolean isShowingRewardedAd = false;
    public static ComplectCallback complectCallback;

    public interface ComplectCallback {
        void onComplect();
    }

    public static void LoadRewardedAds(Context context) {
        if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
            if (MyAdsPreference.get_FlagRewarded().equalsIgnoreCase("on")) {
                RewardedAd_Load(MyAdsPreference.get_IdRewardedGoogle(), context);
            }
        }
    }

    private static void RewardedAd_Load(String rewardedAdId, Context context) {

        if (isLoadingRewardedAd || mRewardedAd != null) {
            return;
        }
        isLoadingRewardedAd = true;

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(context, rewardedAdId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                isShowingRewardedAd = false;
                mRewardedAd = null;
                isLoadingRewardedAd = false;

                if (isLoadingRewardedAd || mRewardedAd != null) {
                    return;
                }

                isLoadingRewardedAd = true;

                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(context, rewardedAdId, adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isShowingRewardedAd = false;
                        mRewardedAd = null;
                        isLoadingRewardedAd = false;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        isLoadingRewardedAd = false;
                        mRewardedAd = rewardedAd;
                        rewardedContentCallback();
                    }
                });
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                isLoadingRewardedAd = false;
                mRewardedAd = rewardedAd;
                rewardedContentCallback();
            }

            private void rewardedContentCallback() {
                mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();

                        if (successfulRewarded) {
                            if (complectCallback != null) {
                                complectCallback.onComplect();
                                complectCallback = null;
                            }
                            successfulRewarded = false;
                            mRewardedAd = null;
                            isLoadingRewardedAd = false;
                        }
                        LoadRewardedAds(context);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);

                        if (complectCallback != null) {
                            complectCallback.onComplect();
                            complectCallback = null;
                        }
                        successfulRewarded = false;
                        mRewardedAd = null;
                        isLoadingRewardedAd = false;
                        LoadRewardedAds(context);
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        successfulRewarded = false;
                    }
                });
            }
        });
    }

    public static void ShowRewardedAd(Activity activity, ComplectCallback callback) {

        if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")) {
            if (MyAdsPreference.get_FlagRewarded().equalsIgnoreCase("on")) {

                MyApp.isFullScreenShow = true;

                complectCallback = callback;
                successfulRewarded = false;
                if (mRewardedAd != null && !isLoadingRewardedAd) {
                    isShowingRewardedAd = true;
                    mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            successfulRewarded = true;
                            MyApp.isFullScreenShow = false;
                        }
                    });
                } else {
                    //Fail RewardedAd
                    showInterstitialAd(activity, complectCallback);
                    LoadRewardedAds(activity);
                }

            } else {
                callback.onComplect();
            }
        } else {
            callback.onComplect();
        }
    }

    public static void showInterstitialAd(Activity activity, ComplectCallback callback) {
        complectCallback = callback;

        InterstitialAds.ShowInterstitialDirect(activity, () -> {
            callback.onComplect();
        });
    }
}