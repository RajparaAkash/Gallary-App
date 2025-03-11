package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterLanguage;
import com.gallaryapp.privacyvault.photoeditor.Ads.NativeAds;
import com.gallaryapp.privacyvault.photoeditor.Interface.LanguageClickListener;
import com.gallaryapp.privacyvault.photoeditor.R;

public class LanguageDialog extends Dialog implements LanguageClickListener {

    Activity activity;
    public onLanguage listener;
    AdapterLanguage adapterLanguage;
    int selectedPos;
    public RecyclerView rvLanguage;

    public interface onLanguage {
        void setLang(int i);
    }

    public LanguageDialog(Activity activity, onLanguage onlanguage) {
        super(activity, R.style.FullScreenDialogTheme);
        this.activity = activity;
        this.listener = onlanguage;
        setContentView(R.layout.dialog_language);

        LocaleManager.setLocale(activity);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        // NativeSmall
        NativeAds.ShowNativeSmall(activity, findViewById(R.id.nativeSmallLay), findViewById(R.id.nativeLay));

        rvLanguage = (RecyclerView) findViewById(R.id.rvLanguage);

        adapterLanguage = new AdapterLanguage(activity, LanguageConfig.eTool(activity, R.array.languageIcon, R.array.languageName), this);
        rvLanguage.setAdapter(adapterLanguage);

        if (LanguageConfig.isLanguage(LocaleManager._ENGLISH)) {
            selectedPos = 0;
        } else if (LanguageConfig.isLanguage(LocaleManager._HINDI)) {
            selectedPos = 1;
        } else if (LanguageConfig.isLanguage(LocaleManager._FRENCH)) {
            selectedPos = 2;
        } else if (LanguageConfig.isLanguage(LocaleManager._SPANISH)) {
            selectedPos = 3;
        } else if (LanguageConfig.isLanguage(LocaleManager._GERMAN)) {
            selectedPos = 4;
        } else if (LanguageConfig.isLanguage(LocaleManager._RUSSIAN)) {
            selectedPos = 5;
        } else if (LanguageConfig.isLanguage(LocaleManager._CHINESE)) {
            selectedPos = 6;
        } else if (LanguageConfig.isLanguage(LocaleManager._JAPANESE)) {
            selectedPos = 7;
        } else if (LanguageConfig.isLanguage(LocaleManager._ITALIAN)) {
            selectedPos = 8;
        } else if (LanguageConfig.isLanguage(LocaleManager._DUTCH)) {
            selectedPos = 9;
        } else if (LanguageConfig.isLanguage(LocaleManager._PORTUGUESE)) {
            selectedPos = 10;
        } else if (LanguageConfig.isLanguage(LocaleManager._KOREAN)) {
            selectedPos = 11;
        } else if (LanguageConfig.isLanguage(LocaleManager._SWEDISH)) {
            selectedPos = 12;
        } else if (LanguageConfig.isLanguage(LocaleManager._INDONESIAN)) {
            selectedPos = 13;
        }

        adapterLanguage.setSelectedPosition(selectedPos);

        findViewById(R.id.saveTxt).setOnClickListener(view -> {
            listener.setLang(selectedPos);
            dismiss();
        });

        findViewById(R.id.back_img).setOnClickListener(view -> {
            dismiss();
        });
    }

    @Override
    public void onLanguageClicked(int i) {
        selectedPos = i;
    }
}