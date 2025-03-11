package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.gallaryapp.privacyvault.photoeditor.Ads.MyAdsPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.LocaleManager;

public class ActBase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocaleManager.setLocale(this);
        if (MyAdsPreference.get_IsFullScreen().equalsIgnoreCase("on")) {
            hideNavigation();
        }
    }

    @Override
    public void attachBaseContext(Context context) {
        super.attachBaseContext(LocaleManager.setLocale(context));
    }

    public void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public void hideNavigation() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
