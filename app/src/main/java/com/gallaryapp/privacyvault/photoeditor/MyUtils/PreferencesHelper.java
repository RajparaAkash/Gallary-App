package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.content.SharedPreferences;

import com.gallaryapp.privacyvault.photoeditor.MyApp;

public class PreferencesHelper {

    public static PreferencesHelper instance;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences settings;

    private PreferencesHelper() {
        SharedPreferences sharedPreferences = MyApp.getInstance().getSharedPreferences("MyGalleryApp", 0);
        this.settings = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public static PreferencesHelper getInstance() {
        if (instance == null) {
            instance = new PreferencesHelper();
        }
        return instance;
    }

    public String getString(String key, String defValue) {
        return this.settings.getString(key, defValue);
    }

    public PreferencesHelper setString(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
        return this;
    }

    public int getInt(String key, int defValue) {
        return this.settings.getInt(key, defValue);
    }

    public PreferencesHelper setInt(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.commit();
        return this;
    }

    public boolean getBoolean(String key, boolean defValue) {
        return this.settings.getBoolean(key, defValue);
    }

    public PreferencesHelper setBoolean(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
        return this;
    }
}
