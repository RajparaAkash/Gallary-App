package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SwitchCompat;

import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.NativeAds;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ActPrivacySetting extends ActBase {

    LinearLayout privacySettingLay1;
    LinearLayout privacySettingLay3;
    SwitchCompat enbleFingerSwitch;
    SwitchCompat invisibleModeSwitch;
    TextView lockStyleTypeTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_privacy_setting);

        // NativeSmall
        NativeAds.ShowNativeSmall(this, findViewById(R.id.nativeSmallLay), findViewById(R.id.nativeLay));

        idBind();
        setOnBackPressed();

        if (MyPreference.get_IsPinLock()) {
            lockStyleTypeTxt.setText(getString(R.string.str_151));
        } else {
            lockStyleTypeTxt.setText(getString(R.string.str_146));
        }

        int[][] states = new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}};
        int[] thumbColors = new int[]{getResources().getColor(R.color.switch_color_1), getResources().getColor(R.color.switch_color_2)};
        int[] trackColors = new int[]{getResources().getColor(R.color.switch_color_3), getResources().getColor(R.color.switch_color_4)};

        enbleFingerSwitch.setThumbTintList(new ColorStateList(states, thumbColors));
        enbleFingerSwitch.setTrackTintList(new ColorStateList(states, trackColors));

        invisibleModeSwitch.setThumbTintList(new ColorStateList(states, thumbColors));
        invisibleModeSwitch.setTrackTintList(new ColorStateList(states, trackColors));

        enbleFingerSwitch.setChecked(MyPreference.get_IsEnableFingerprint());
        invisibleModeSwitch.setChecked(MyPreference.get_IsInvisibleMode());

        btnClick();
    }

    private void btnClick() {
        privacySettingLay1.setOnClickListener(v -> {
            InterstitialAds.ShowInterstitial(this, () -> {
                startActivity(new Intent(ActPrivacySetting.this, ActPasswordChange.class));
            });
        });

        privacySettingLay3.setOnClickListener(v -> {
            dialogChangeUnlockType();
        });

        enbleFingerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MyPreference.set_IsEnableFingerprint(isChecked);
        });

        invisibleModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MyPreference.set_IsInvisibleMode(isChecked);
        });
    }

    private void idBind() {
        privacySettingLay1 = findViewById(R.id.privacySettingLay1);
        privacySettingLay3 = findViewById(R.id.privacySettingLay3);
        enbleFingerSwitch = findViewById(R.id.enbleFingerSwitch);
        invisibleModeSwitch = findViewById(R.id.invisibleModeSwitch);
        lockStyleTypeTxt = findViewById(R.id.lockStyleTypeTxt);
    }

    public void dialogChangeUnlockType() {
        BottomSheetDialog dgChangeUnlockMode = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_change_unlock_type, null);
        dgChangeUnlockMode.setContentView(bottomSheetView);
        dgChangeUnlockMode.show();

        LinearLayout pinLay = dgChangeUnlockMode.findViewById(R.id.pinLay);
        LinearLayout patternLay = dgChangeUnlockMode.findViewById(R.id.patternLay);

        CheckBox pinCheckBox = dgChangeUnlockMode.findViewById(R.id.pinCheckBox);
        CheckBox patternCheckBox = dgChangeUnlockMode.findViewById(R.id.patternCheckBox);

        TextView cancelTxt = dgChangeUnlockMode.findViewById(R.id.cancelTxt);
        TextView changeTxt = dgChangeUnlockMode.findViewById(R.id.changeTxt);

        changeTxt.setEnabled(false);
        changeTxt.setAlpha(0.5f);

        if (MyPreference.get_IsPinLock()) {
            pinCheckBox.setChecked(true);
            patternCheckBox.setChecked(false);
        } else {
            pinCheckBox.setChecked(false);
            patternCheckBox.setChecked(true);
        }

        pinLay.setOnClickListener(v -> {
            if (MyPreference.get_IsPinLock()) {
                changeTxt.setEnabled(false);
                changeTxt.setAlpha(0.5f);
            } else {
                changeTxt.setEnabled(true);
                changeTxt.setAlpha(1f);
            }
            pinCheckBox.setChecked(true);
            patternCheckBox.setChecked(false);
        });

        patternLay.setOnClickListener(v -> {
            if (MyPreference.get_IsPinLock()) {
                changeTxt.setEnabled(true);
                changeTxt.setAlpha(1f);
            } else {
                changeTxt.setEnabled(false);
                changeTxt.setAlpha(0.5f);
            }
            pinCheckBox.setChecked(false);
            patternCheckBox.setChecked(true);
        });

        cancelTxt.setOnClickListener(v -> {
            dgChangeUnlockMode.dismiss();
        });

        changeTxt.setOnClickListener(v -> {
            dgChangeUnlockMode.dismiss();
            InterstitialAds.ShowInterstitial(this, () -> {
                Intent intent = new Intent(ActPrivacySetting.this, ActPasswordStyleChange.class);
                startActivityForResult(intent, 111);
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == -1) {
                if (MyPreference.get_IsPinLock()) {
                    lockStyleTypeTxt.setText(getString(R.string.str_151));
                } else {
                    lockStyleTypeTxt.setText(getString(R.string.str_146));
                }

                Intent broadCastIntent = new Intent("SHOW_LOCK");
                broadCastIntent.putExtra("COMMAND", "update");
                sendBroadcast(broadCastIntent);
            }
        }
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActPrivacySetting.this, () -> {
                    finish();
                });
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}
