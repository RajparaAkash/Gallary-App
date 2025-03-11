package com.gallaryapp.privacyvault.photoeditor.MyUtils;

public class MyPreference {

    public static String get_AlbumsAS_SortBy() {
        return PreferencesHelper.getInstance().getString("AlbumsAS_SortBy", "date_modified");
    }

    public static void set_AlbumsAS_SortBy(String asSortBy) {
        PreferencesHelper.getInstance().setString("AlbumsAS_SortBy", asSortBy);
    }

    public static boolean get_AlbumsAS_IsAscending() {
        return PreferencesHelper.getInstance().getBoolean("AlbumsAS_IsAscending", false);
    }

    public static void set_AlbumsAS_IsAscending(boolean mBoolean) {
        PreferencesHelper.getInstance().setBoolean("AlbumsAS_IsAscending", mBoolean);
    }

    public static int get_Albums_GridSize() {
        return PreferencesHelper.getInstance().getInt("Albums_GridSize", 3);
    }

    public static void set_Albums_GridSize(int count) {
        PreferencesHelper.getInstance().setInt("Albums_GridSize", count);
    }

    public static boolean get_AlbumsAS_IsGrid() {
        return PreferencesHelper.getInstance().getBoolean("AlbumsAS_IsGrid", true);
    }

    public static void set_AlbumsAS_IsGrid(Boolean aBoolean) {
        PreferencesHelper.getInstance().setBoolean("AlbumsAS_IsGrid", aBoolean);
    }

    public static String get_AllMediaAS_SortBy() {
        return PreferencesHelper.getInstance().getString("AllMediaAS_SortBy", "date_modified");
    }

    public static void set_AllMediaAS_SortBy(String asSortBy) {
        PreferencesHelper.getInstance().setString("AllMediaAS_SortBy", asSortBy);
    }

    public static boolean get_AllMediaAS_IsAscending() {
        return PreferencesHelper.getInstance().getBoolean("AllMediaAS_IsAscending", false);
    }

    public static void set_AllMediaAS_IsAscending(boolean mBoolean) {
        PreferencesHelper.getInstance().setBoolean("AllMediaAS_IsAscending", mBoolean);
    }

    public static int get_AllMedia_GridSize() {
        return PreferencesHelper.getInstance().getInt("AllMedia_GridSize", 3);
    }

    public static void set_AllMedia_GridSize(int count) {
        PreferencesHelper.getInstance().setInt("AllMedia_GridSize", count);
    }

    public static int get_SelectLangPos() {
        return PreferencesHelper.getInstance().getInt("SelectLangPos", 0);
    }

    public static void set_SelectLangPos(int pos) {
        PreferencesHelper.getInstance().setInt("SelectLangPos", pos);
    }

    public static String get_SelectLangCode() {
        return PreferencesHelper.getInstance().getString("SelectLangCode", "");
    }

    public static void set_SelectLangCode(String str) {
        PreferencesHelper.getInstance().setString("SelectLangCode", str);
    }

    public static boolean get_IsEnableFingerprint() {
        return PreferencesHelper.getInstance().getBoolean("IsEnableFingerprint", false);
    }

    public static void set_IsEnableFingerprint(Boolean aBoolean) {
        PreferencesHelper.getInstance().setBoolean("IsEnableFingerprint", aBoolean);
    }

    public static boolean get_IsInvisibleMode() {
        return PreferencesHelper.getInstance().getBoolean("IsInvisibleMode", false);
    }

    public static void set_IsInvisibleMode(Boolean aBoolean) {
        PreferencesHelper.getInstance().setBoolean("IsInvisibleMode", aBoolean);
    }

    public static boolean get_IsMoveToTrash() {
        return PreferencesHelper.getInstance().getBoolean("IsMoveToTrash", true);
    }

    public static void set_IsMoveToTrash(Boolean aBoolean) {
        PreferencesHelper.getInstance().setBoolean("IsMoveToTrash", aBoolean);
    }

    public static int get_Biometric_FailCount() {
        return PreferencesHelper.getInstance().getInt("Biometric_FailCount", 0);
    }

    public static void set_Biometric_FailCount(int str) {
        PreferencesHelper.getInstance().setInt("Biometric_FailCount", str);
    }

    public static int get_Theme() {
        return PreferencesHelper.getInstance().getInt("theme", -1);
    }

    public static void set_Theme(int str) {
        PreferencesHelper.getInstance().setInt("theme", str);
    }

    public static String get_SecurityQuestion() {
        return PreferencesHelper.getInstance().getString("SecurityQuestion", "");
    }

    public static void set_SecurityQuestion(String str) {
        PreferencesHelper.getInstance().setString("SecurityQuestion", str);
    }

    public static String get_SecurityAnswer() {
        return PreferencesHelper.getInstance().getString("SecurityAnswer", "");
    }

    public static void set_SecurityAnswer(String str) {
        PreferencesHelper.getInstance().setString("SecurityAnswer", str);
    }

    public static boolean get_IsEnableSecretSnap() {
        return PreferencesHelper.getInstance().getBoolean("IsEnableSecretSnap", false);
    }

    public static void set_IsEnableSecretSnap(Boolean aBoolean) {
        PreferencesHelper.getInstance().setBoolean("IsEnableSecretSnap", aBoolean);
    }

    public static String get_Password() {
        return PreferencesHelper.getInstance().getString("Password", "");
    }

    public static void set_Password(String str) {
        PreferencesHelper.getInstance().setString("Password", str);
    }

    public static boolean get_IsPinLock() {
        return PreferencesHelper.getInstance().getBoolean("IsPinLock", true);
    }

    public static void set_IsPinLock(boolean mBoolean) {
        PreferencesHelper.getInstance().setBoolean("IsPinLock", mBoolean);
    }

    public static boolean get_IsFirstTime() {
        return PreferencesHelper.getInstance().getBoolean("IsFirstTime", true);
    }

    public static void set_IsFirstTime(boolean mBoolean) {
        PreferencesHelper.getInstance().setBoolean("IsFirstTime", mBoolean);
    }

    public static int get_Privacy_AlbumGridSize() {
        return PreferencesHelper.getInstance().getInt("Privacy_AlbumGridSize", 3);
    }

    public static void set_Privacy_AlbumGridSize(int count) {
        PreferencesHelper.getInstance().setInt("Privacy_AlbumGridSize", count);
    }

    public static int get_Privacy_ImageGridSize() {
        return PreferencesHelper.getInstance().getInt("Privacy_ImageGridSize", 3);
    }

    public static void set_Privacy_ImageGridSize(int count) {
        PreferencesHelper.getInstance().setInt("Privacy_ImageGridSize", count);
    }

    public static int get_Privacy_VideoGridSize() {
        return PreferencesHelper.getInstance().getInt("Privacy_VideoGridSize", 3);
    }

    public static void set_Privacy_VideoGridSize(int count) {
        PreferencesHelper.getInstance().setInt("Privacy_VideoGridSize", count);
    }
}
