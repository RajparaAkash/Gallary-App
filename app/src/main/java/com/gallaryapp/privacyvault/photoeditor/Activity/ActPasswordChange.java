package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyViewCustom.Lock9Views;
import com.gallaryapp.privacyvault.photoeditor.R;

public class ActPasswordChange extends ActBase {

    LinearLayout pinMainLay;
    LinearLayout patternMainLay;
    TextView extraTxt;
    TextView pinMessageTxt;
    TextView pin1Txt;
    TextView pin2Txt;
    TextView pin3Txt;
    TextView pin4Txt;

    TextView patternMessageTxt;
    Lock9Views patternLockView;

    private Vibrator vibrator;
    String oldPatternValue = "";
    String pinCode = "";
    boolean isSecondTime = false;
    boolean isFirstTime;
    boolean isRepeatScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_password);

        idBind();
        setOnBackPressed();

        if (MyPreference.get_IsPinLock()) {
            initPinLockData();
        } else {
            initPatternData();
            patternMessageTxt.setText(getString(R.string.str_188));
        }

        patternLockView.setCallBack(new Lock9Views.CallBack() {
            @Override
            public void onFinish(String password) {
                if (password.length() < 4) {
                    patternMessageTxt.setText(getResources().getString(R.string.str_186));
                    return;
                }

                if (isFirstTime) {
                    if (MyPreference.get_Password().equals(password)) {
                        isFirstTime = false;
                        isSecondTime = true;
                        patternMessageTxt.setText(getString(R.string.str_191));
                        return;
                    }
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                    patternMessageTxt.startAnimation(shake);
                    patternMessageTxt.setText(getString(R.string.str_136));
                } else if (isSecondTime) {
                    isSecondTime = false;
                    isRepeatScreen = true;
                    oldPatternValue = password;
                    patternMessageTxt.setText(getString(R.string.str_187));
                } else if (isRepeatScreen) {
                    if (oldPatternValue.equals(password)) {
                        MyPreference.set_Password(password);
                        Toast.makeText(getApplicationContext(), "Pattern Changed Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    Animation shake2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                    patternMessageTxt.startAnimation(shake2);
                    patternMessageTxt.setText(getString(R.string.str_144));
                }
            }
        });
        pinMessageTxt.setText(R.string.str_121);
        pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        isFirstTime = true;
        isSecondTime = false;
        isRepeatScreen = false;

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        findViewById(R.id.pinTxt_0).setOnClickListener(view -> pinClicked(view));

        findViewById(R.id.pinTxt_1).setOnClickListener(view -> pinClicked(view));

        findViewById(R.id.pinTxt_2).setOnClickListener(view -> pinClicked(view));

        findViewById(R.id.pinTxt_3).setOnClickListener(view -> pinClicked(view));

        findViewById(R.id.pinTxt_4).setOnClickListener(view -> pinClicked(view));

        findViewById(R.id.pinTxt_5).setOnClickListener(view -> pinClicked(view));

        findViewById(R.id.pinTxt_6).setOnClickListener(view -> pinClicked(view));

        findViewById(R.id.pinTxt_7).setOnClickListener(view -> pinClicked(view));

        findViewById(R.id.pinTxt_8).setOnClickListener(view -> pinClicked(view));

        findViewById(R.id.pinTxt_9).setOnClickListener(view -> pinClicked(view));

        findViewById(R.id.pinTxt_Delete).setOnClickListener(view -> pinClicked(view));

    }

    private void idBind() {
        pinMainLay = findViewById(R.id.pinMainLay);
        patternMainLay = findViewById(R.id.patternMainLay);
        extraTxt = findViewById(R.id.extraTxt);
        pinMessageTxt = findViewById(R.id.pinMessageTxt);
        pin1Txt = findViewById(R.id.pin1Txt);
        pin2Txt = findViewById(R.id.pin2Txt);
        pin3Txt = findViewById(R.id.pin3Txt);
        pin4Txt = findViewById(R.id.pin4Txt);

        patternMessageTxt = findViewById(R.id.patternMessageTxt);
        patternLockView = findViewById(R.id.patternLockView);
    }

    private void initPatternData() {
        pinMainLay.setVisibility(View.GONE);
        patternMainLay.setVisibility(View.VISIBLE);
    }

    private void initPinLockData() {
        pinMainLay.setVisibility(View.VISIBLE);
        patternMainLay.setVisibility(View.GONE);
        pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
    }

    public void pinClicked(View v) {
        vibrator.vibrate(80L);

        String value = "";
        switch (v.getId()) {
            case R.id.pinTxt_0:
                value = "0";
                break;
            case R.id.pinTxt_1:
                value = "1";
                break;
            case R.id.pinTxt_2:
                value = "2";
                break;
            case R.id.pinTxt_3:
                value = "3";
                break;
            case R.id.pinTxt_4:
                value = "4";
                break;
            case R.id.pinTxt_5:
                value = "5";
                break;
            case R.id.pinTxt_6:
                value = "6";
                break;
            case R.id.pinTxt_7:
                value = "7";
                break;
            case R.id.pinTxt_8:
                value = "8";
                break;
            case R.id.pinTxt_9:
                value = "9";
                break;
            case R.id.pinTxt_Delete:
                String text = extraTxt.getText().toString();
                if (text.length() >= 1) {
                    extraTxt.setText(text.substring(0, text.length() - 1));
                    changePasscodeImage();
                }
                if (isFirstTime && extraTxt.getText().toString().equals("")) {
                    pinMessageTxt.setText(R.string.str_121);
                    isRepeatScreen = false;
                    isSecondTime = false;
                } else if (isSecondTime && extraTxt.getText().toString().equals("")) {
                    pinMessageTxt.setText(R.string.str_120);
                    isRepeatScreen = false;
                    isFirstTime = false;
                } else if (isSecondTime && isRepeatScreen) {
                    pinMessageTxt.setText(R.string.str_120);
                    isRepeatScreen = false;
                }
                value = "";
                break;
        }
        if (!value.isEmpty()) {
            v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_alphas));
            String text2 = extraTxt.getText().toString();
            TextView textView = extraTxt;
            textView.setText(text2 + value);
            changePasscodeImage();
            if (extraTxt.getText().toString().length() == 4) {
                if (isFirstTime) {
                    performFirstTime();
                } else if (isSecondTime) {
                    performSecondTime();
                } else if (isRepeatScreen) {
                    if (pinCode.equals(extraTxt.getText().toString())) {
                        MyPreference.set_Password(pinCode);
                        Toast.makeText(getApplicationContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    clearPinLock();

                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                    pinMessageTxt.startAnimation(shake);
                    pinMessageTxt.setText(getString(R.string.str_145));
                } else {
                    Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void changePasscodeImage() {
        switch (extraTxt.getText().toString().length()) {
            case 0:
                pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                return;
            case 1:
                pin1Txt.setBackgroundResource(R.drawable.bg_pin_fill);
                pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                return;
            case 2:
                pin2Txt.setBackgroundResource(R.drawable.bg_pin_fill);
                pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                return;
            case 3:
                pin3Txt.setBackgroundResource(R.drawable.bg_pin_fill);
                pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                return;
            case 4:
                pin4Txt.setBackgroundResource(R.drawable.bg_pin_fill);
                return;
            default:
                return;
        }
    }

    private void performFirstTime() {
        String inputedPass = extraTxt.getText().toString();
        if (MyPreference.get_Password().equals(inputedPass)) {
            pinMessageTxt.setText(R.string.str_120);
            clearPinLock();
            isFirstTime = false;
            isSecondTime = true;
            return;
        }
        clearPinLock();

        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
        pinMessageTxt.startAnimation(shake);
        pinMessageTxt.setText(getString(R.string.str_135));
    }

    public void clearPinLock() {
        pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        extraTxt.setText("");
    }

    private void performSecondTime() {
        pinCode = extraTxt.getText().toString();
        clearPinLock();
        pinMessageTxt.setText(R.string.str_30);
        isSecondTime = false;
        isRepeatScreen = true;
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActPasswordChange.this, () -> {
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
