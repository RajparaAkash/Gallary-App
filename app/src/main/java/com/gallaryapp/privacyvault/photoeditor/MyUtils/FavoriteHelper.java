package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class FavoriteHelper {

    public static void setPreferenceString(Context c, String pref, String val) {
        Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
        e.putString(pref, val);
        e.commit();
    }

    public static String getPreferenceString(Context c, String pref, String val) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(pref,
                val);
    }
}