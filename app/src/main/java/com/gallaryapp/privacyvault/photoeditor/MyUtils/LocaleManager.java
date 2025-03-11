package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Locale;

public class LocaleManager {

    private static final String LANGUAGE_KEY = "language_key";

    public static final String _ENGLISH = "en";
    public static final String _HINDI = "hi";
    public static final String _FRENCH = "fr";
    public static final String _SPANISH = "es";
    public static final String _GERMAN = "de";
    public static final String _RUSSIAN = "ru";
    public static final String _CHINESE = "zh";
    public static final String _JAPANESE = "ja";
    public static final String _ITALIAN = "it";
    public static final String _DUTCH = "nl";
    public static final String _PORTUGUESE = "pt";
    public static final String _KOREAN = "ko";
    public static final String _SWEDISH = "sv";
    public static final String _INDONESIAN = "in";

    @Retention(RetentionPolicy.SOURCE)
    public @interface LocaleDef {
        public static final String[] SUPPORTED_LOCALES = {
                LocaleManager._ENGLISH, LocaleManager._HINDI, LocaleManager._FRENCH, LocaleManager._SPANISH, LocaleManager._GERMAN,
                LocaleManager._RUSSIAN, LocaleManager._CHINESE, LocaleManager._JAPANESE, LocaleManager._ITALIAN, LocaleManager._DUTCH,
                LocaleManager._PORTUGUESE, LocaleManager._KOREAN, LocaleManager._SWEDISH, LocaleManager._INDONESIAN};
    }

    public static Context setLocale(Context context) {
        return updateResources(context, getLanguagePref(context));
    }

    public static void setNewLocale(Context context, String str) {
        setLanguagePref(context, str);
        updateResources(context, str);
    }

    public static String getLanguagePref(Context context) {
        try {
            String string = PreferenceManager.getDefaultSharedPreferences(context).getString(LANGUAGE_KEY, "");
            return Arrays.asList(LocaleDef.SUPPORTED_LOCALES).contains(string) ? string : _ENGLISH;
        } catch (Exception e) {
            e.printStackTrace();
            return _ENGLISH;
        }
    }

    private static void setLanguagePref(Context context, String str) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(LANGUAGE_KEY, str).apply();
    }

    public static Context updateResources(Context context, String str) {
        Locale locale = new Locale(str);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }
}
