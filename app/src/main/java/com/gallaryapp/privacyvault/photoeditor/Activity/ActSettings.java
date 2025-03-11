package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.browser.customtabs.CustomTabsIntent;

import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.MyAdsPreference;
import com.gallaryapp.privacyvault.photoeditor.Ads.NativeAds;
import com.gallaryapp.privacyvault.photoeditor.BuildConfig;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.LanguageDialog;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.LocaleManager;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilPrivacyVault;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ActSettings extends ActBase implements LanguageDialog.onLanguage {

    private LinearLayout settingLay1;
    private LinearLayout settingLay3;
    private LinearLayout settingLay4;
    private LinearLayout settingLay5;
    private LinearLayout settingLay6;
    private LinearLayout settingLay7;
    private LinearLayout settingLay8;
    private LinearLayout settingLay9;
    private SwitchCompat moveToTrashSwitch;
    private TextView versionTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_settings);

        // NativeSmall
        NativeAds.ShowNativeSmall(this, findViewById(R.id.nativeSmallLay), findViewById(R.id.nativeLay));

        idBind();
        setOnBackPressed();

        versionTxt.setText("Version " + BuildConfig.VERSION_NAME);

        int[][] states = new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}};
        int[] thumbColors = new int[]{getResources().getColor(R.color.switch_color_1), getResources().getColor(R.color.switch_color_2)};
        int[] trackColors = new int[]{getResources().getColor(R.color.switch_color_3), getResources().getColor(R.color.switch_color_4)};

        moveToTrashSwitch.setThumbTintList(new ColorStateList(states, thumbColors));
        moveToTrashSwitch.setTrackTintList(new ColorStateList(states, trackColors));
        moveToTrashSwitch.setChecked(MyPreference.get_IsMoveToTrash());

        btnClick();
    }

    private void btnClick() {
        settingLay1.setOnClickListener(v -> {
            if (UtilPrivacyVault.isPrivacyVaultSetup(ActSettings.this)) {
                InterstitialAds.ShowInterstitial(this, () -> {
                    startActivity(new Intent(ActSettings.this, ActPrivacySetting.class));
                });
            }
        });

        settingLay3.setOnClickListener(v -> {
            new LanguageDialog(this, this).show();
        });

        settingLay4.setOnClickListener(v -> {
            dialogThemeChange();
        });

        settingLay5.setOnClickListener(v -> {
            rateUs();
        });

        settingLay6.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                String shareMessage = "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share Application"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        settingLay7.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sagarbhaisojitra@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No email app installed", Toast.LENGTH_SHORT).show();
            }

            /*Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name));
            intent.putExtra("android.intent.extra.EMAIL", new String[]{"sagarbhaisojitra@gmail.com"});
            intent.setType("text/plain");
            PackageManager packageManager = getPackageManager();
            if (isPackageInstalled("com.google.android.gm", packageManager)) {
                intent.setPackage("com.google.android.gm");
            }
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }*/
        });

        settingLay8.setOnClickListener(v -> {
            openPrivacyPolicy(this, MyAdsPreference.get_PrivacyPolicy());
        });

        settingLay9.setOnClickListener(v -> {
            rateUs();
        });

        moveToTrashSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MyPreference.set_IsMoveToTrash(isChecked);
        });
    }

    private void rateUs() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        } catch (ActivityNotFoundException unused) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        }
    }

    public void dialogThemeChange() {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_theme_change, null);
        bottomDialog.setContentView(bottomSheetView);
        bottomDialog.show();

        LinearLayout lightLay = bottomDialog.findViewById(R.id.lightLay);
        LinearLayout darkLay = bottomDialog.findViewById(R.id.darkLay);
        LinearLayout systemLay = bottomDialog.findViewById(R.id.systemLay);

        CheckBox lightCheckBox = bottomDialog.findViewById(R.id.lightCheckBox);
        CheckBox darkCheckBox = bottomDialog.findViewById(R.id.darkCheckBox);
        CheckBox systemCheckBox = bottomDialog.findViewById(R.id.systemCheckBox);

        TextView cancelTxt = bottomDialog.findViewById(R.id.cancelTxt);
        TextView saveTxt = bottomDialog.findViewById(R.id.saveTxt);

        if (MyPreference.get_Theme() == 1) {
            lightCheckBox.setChecked(true);
            darkCheckBox.setChecked(false);
            systemCheckBox.setChecked(false);
        } else if (MyPreference.get_Theme() == 2) {
            lightCheckBox.setChecked(false);
            darkCheckBox.setChecked(true);
            systemCheckBox.setChecked(false);
        } else if (MyPreference.get_Theme() == -1) {
            lightCheckBox.setChecked(false);
            darkCheckBox.setChecked(false);
            systemCheckBox.setChecked(true);
        }

        saveTxt.setEnabled(false);
        saveTxt.setAlpha(0.5f);

        lightLay.setOnClickListener(v -> {
            if (MyPreference.get_Theme() == 1) {
                saveTxt.setEnabled(false);
                saveTxt.setAlpha(0.5f);
            } else {
                saveTxt.setEnabled(true);
                saveTxt.setAlpha(1f);
            }

            lightCheckBox.setChecked(true);
            darkCheckBox.setChecked(false);
            systemCheckBox.setChecked(false);
        });

        darkLay.setOnClickListener(v -> {
            if (MyPreference.get_Theme() == 2) {
                saveTxt.setEnabled(false);
                saveTxt.setAlpha(0.5f);
            } else {
                saveTxt.setEnabled(true);
                saveTxt.setAlpha(1f);
            }

            lightCheckBox.setChecked(false);
            darkCheckBox.setChecked(true);
            systemCheckBox.setChecked(false);
        });

        systemLay.setOnClickListener(v -> {
            if (MyPreference.get_Theme() == -1) {
                saveTxt.setEnabled(false);
                saveTxt.setAlpha(0.5f);
            } else {
                saveTxt.setEnabled(true);
                saveTxt.setAlpha(1f);
            }

            lightCheckBox.setChecked(false);
            darkCheckBox.setChecked(false);
            systemCheckBox.setChecked(true);
        });

        cancelTxt.setOnClickListener(v -> {
            bottomDialog.dismiss();
        });

        saveTxt.setOnClickListener(v -> {
            bottomDialog.dismiss();
            UtilApp.isThemeChanged = true;

            if (lightCheckBox.isChecked()) {
                MyPreference.set_Theme(1);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            } else if (darkCheckBox.isChecked()) {
                MyPreference.set_Theme(2);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            } else if (systemCheckBox.isChecked()) {
                MyPreference.set_Theme(-1);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });
    }

    public void openPrivacyPolicy(Context context, String link) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(link));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPackageInstalled(String str, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(str, 0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    private void idBind() {
        settingLay1 = findViewById(R.id.settingLay1);
        settingLay3 = findViewById(R.id.settingLay3);
        settingLay4 = findViewById(R.id.settingLay4);
        settingLay5 = findViewById(R.id.settingLay5);
        settingLay6 = findViewById(R.id.settingLay6);
        settingLay7 = findViewById(R.id.settingLay7);
        settingLay8 = findViewById(R.id.settingLay8);
        settingLay9 = findViewById(R.id.settingLay9);
        moveToTrashSwitch = findViewById(R.id.moveToTrashSwitch);
        versionTxt = findViewById(R.id.versionTxt);
    }

    @Override
    public void setLang(int i) {
        if (i >= 0 && i < LocaleManager.LocaleDef.SUPPORTED_LOCALES.length) {
            setNewLocale(LocaleManager.LocaleDef.SUPPORTED_LOCALES[i]);
        }
    }

    private void setNewLocale(String str) {
        LocaleManager.setNewLocale(this, str);
        restartApp();
    }

    private void restartApp() {
        Intent intent = new Intent(this, ActDashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (UtilApp.isThemeChanged) {
                    UtilApp.isThemeChanged = false;
                    Intent intent = new Intent(ActSettings.this, ActDashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}