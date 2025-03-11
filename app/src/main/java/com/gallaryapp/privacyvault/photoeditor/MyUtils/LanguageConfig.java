package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.content.Context;
import android.content.res.TypedArray;

import com.gallaryapp.privacyvault.photoeditor.Model.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageConfig {

    public static boolean dialog = false;

    public static boolean isLanguage(String str) {
        return Locale.getDefault().getLanguage().equals(str);
    }

    public static List<Language> eTool(Context context, int i, int i2) {
        ArrayList arrayList = new ArrayList();
        TypedArray obtainTypedArray = context.getResources().obtainTypedArray(i);
        String[] stringArray = context.getResources().getStringArray(i2);
        for (int i3 = 0; i3 < obtainTypedArray.length(); i3++) {
            Language language = new Language();
            language.icon = obtainTypedArray.getResourceId(i3, -1);
            language.name = stringArray[i3];
            arrayList.add(language);
        }
        return arrayList;
    }
}