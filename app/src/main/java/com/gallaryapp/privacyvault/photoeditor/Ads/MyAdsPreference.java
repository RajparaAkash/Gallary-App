package com.gallaryapp.privacyvault.photoeditor.Ads;

import com.gallaryapp.privacyvault.photoeditor.MyUtils.PreferencesHelper;

public class MyAdsPreference {

    public static String get_AdStatus() {
        return PreferencesHelper.getInstance().getString("adStatus", "on");
    }

    public static void set_AdStatus(String str) {
        PreferencesHelper.getInstance().setString("adStatus", str);
    }

    public static String get_AdShowTime() {
        return PreferencesHelper.getInstance().getString("adShowTime", "60000");
    }

    public static void set_AdShowTime(String str) {
        PreferencesHelper.getInstance().setString("adShowTime", str);
    }

    public static String get_AdStyleMain() {
        return PreferencesHelper.getInstance().getString("adStyleMain", "google");
    }

    public static void set_AdStyleMain(String str) {
        PreferencesHelper.getInstance().setString("adStyleMain", str);
    }

    public static String get_AdStyleSplash() {
        return PreferencesHelper.getInstance().getString("adStyleSplash", "appopen");
    }

    public static void set_AdStyleSplash(String str) {
        PreferencesHelper.getInstance().setString("adStyleSplash", str);
    }

    public static String get_AdStyleGoogle() {
        return PreferencesHelper.getInstance().getString("adStyleGoogle", "admob");
    }

    public static void set_AdStyleGoogle(String str) {
        PreferencesHelper.getInstance().setString("adStyleGoogle", str);
    }

    public static String get_FlagBackAd() {
        return PreferencesHelper.getInstance().getString("flagBackAd", "on");
    }

    public static void set_FlagBackAd(String str) {
        PreferencesHelper.getInstance().setString("flagBackAd", str);
    }

    public static String get_FlagAppOpen() {
        return PreferencesHelper.getInstance().getString("flagAppOpen", "on");
    }

    public static void set_FlagAppOpen(String str) {
        PreferencesHelper.getInstance().setString("flagAppOpen", str);
    }

    public static String get_FlagInterstitial() {
        return PreferencesHelper.getInstance().getString("flagInterstitial", "on");
    }

    public static void set_FlagInterstitial(String str) {
        PreferencesHelper.getInstance().setString("flagInterstitial", str);
    }

    public static String get_FlagAdaptiveBanner() {
        return PreferencesHelper.getInstance().getString("flagAdaptiveBanner", "on");
    }

    public static void set_FlagAdaptiveBanner(String str) {
        PreferencesHelper.getInstance().setString("flagAdaptiveBanner", str);
    }

    public static String get_FlagNative() {
        return PreferencesHelper.getInstance().getString("flagNative", "on");
    }

    public static void set_FlagNative(String str) {
        PreferencesHelper.getInstance().setString("flagNative", str);
    }

    public static String get_FlagRewarded() {
        return PreferencesHelper.getInstance().getString("flagRewarded", "on");
    }

    public static void set_FlagRewarded(String str) {
        PreferencesHelper.getInstance().setString("flagRewarded", str);
    }



    public static String get_IdAppOpenGoogle() {
        return PreferencesHelper.getInstance().getString("idAppOpenGoogle", "");
    }

    public static void set_IdAppOpenGoogle(String str) {
        PreferencesHelper.getInstance().setString("idAppOpenGoogle", str);
    }

    public static String get_IdInterstitialGoogle() {
        return PreferencesHelper.getInstance().getString("idInterstitialGoogle", "");
    }

    public static void set_IdInterstitialGoogle(String str) {
        PreferencesHelper.getInstance().setString("idInterstitialGoogle", str);
    }

    public static String get_IdBannerGoogleAdmob() {
        return PreferencesHelper.getInstance().getString("idBannerGoogleAdmob", "");
    }

    public static void set_IdBannerGoogleAdmob(String str) {
        PreferencesHelper.getInstance().setString("idBannerGoogleAdmob", str);
    }

    public static String get_IdBannerGoogleAdx() {
        return PreferencesHelper.getInstance().getString("idBannerGoogleAdx", "");
    }

    public static void set_IdBannerGoogleAdx(String str) {
        PreferencesHelper.getInstance().setString("idBannerGoogleAdx", str);
    }

    public static String get_IdNativeGoogle() {
        return PreferencesHelper.getInstance().getString("idNativeGoogle", "");
    }

    public static void set_IdNativeGoogle(String str) {
        PreferencesHelper.getInstance().setString("idNativeGoogle", str);
    }

    public static String get_IdRewardedGoogle() {
        return PreferencesHelper.getInstance().getString("idRewardedGoogle", "");
    }

    public static void set_IdRewardedGoogle(String str) {
        PreferencesHelper.getInstance().setString("idRewardedGoogle", str);
    }



    public static String get_IdInterstitialFb() {
        return PreferencesHelper.getInstance().getString("idInterstitialFb", "");
    }

    public static void set_IdInterstitialFb(String str) {
        PreferencesHelper.getInstance().setString("idInterstitialFb", str);
    }

    public static String get_IdBannerFb() {
        return PreferencesHelper.getInstance().getString("idBannerFb", "");
    }

    public static void set_IdBannerFb(String str) {
        PreferencesHelper.getInstance().setString("idBannerFb", str);
    }

    public static String get_IdNativeFb() {
        return PreferencesHelper.getInstance().getString("idNativeFb", "");
    }

    public static void set_IdNativeFb(String str) {
        PreferencesHelper.getInstance().setString("idNativeFb", str);
    }



    public static String get_PrivacyPolicy() {
        return PreferencesHelper.getInstance().getString("privacyPolicy", "");
    }

    public static void set_PrivacyPolicy(String str) {
        PreferencesHelper.getInstance().setString("privacyPolicy", str);
    }

    public static String get_IsFullScreen() {
        return PreferencesHelper.getInstance().getString("isFullScreen", "on");
    }

    public static void set_IsFullScreen(String str) {
        PreferencesHelper.getInstance().setString("isFullScreen", str);
    }

}
