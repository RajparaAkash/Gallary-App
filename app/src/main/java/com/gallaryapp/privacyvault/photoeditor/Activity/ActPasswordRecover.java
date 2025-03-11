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

public class ActPasswordRecover extends ActBase {

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

    boolean isFirstTime;
    boolean isRepeatScreen;
    private Vibrator vibrator;
    String oldPatternValue = "";
    String pinCode = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_password);

        idBind();
        setOnBackPressed();

        isFirstTime = true;
        isRepeatScreen = false;

        if (MyPreference.get_IsPinLock()) {
            pinMessageTxt.setText(R.string.str_120);
            initPinLockData();
        } else {
            initPatternData();
            patternMessageTxt.setText(getString(R.string.str_191));
            patternLockView.setCallBack(new Lock9Views.CallBack() {
                @Override
                public void onFinish(String password) {
                    if (password.length() < 4) {
                        patternMessageTxt.setText(getResources().getString(R.string.str_186));
                        return;
                    }

                    if (isFirstTime) {
                        isFirstTime = false;
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
                        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                        patternMessageTxt.startAnimation(shake);
                        patternMessageTxt.setText(getString(R.string.str_144));
                    }
                }
            });
        }

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
                    pinMessageTxt.setText(R.string.str_120);
                    isRepeatScreen = false;
                } else if (isFirstTime && isRepeatScreen) {
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
                    performSecondTime();
                } else if (isRepeatScreen) {
                    if (pinCode.equals(extraTxt.getText().toString())) {
                        MyPreference.set_Password(pinCode);
                        Toast.makeText(getApplicationContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        setResult(-1);
                        finish();
                        return;
                    }
                    extraTxt.setText("");
                    pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                    pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                    pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                    pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);

                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                    pinMessageTxt.startAnimation(shake);
                    pinMessageTxt.setText(getString(R.string.str_145));

                } else {
                    extraTxt.setText("");
                    pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                    pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                    pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                    pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
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

    private void performSecondTime() {
        pinCode = extraTxt.getText().toString();
        extraTxt.setText("");

        pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);

        pinMessageTxt.setText(R.string.str_30);
        isFirstTime = false;
        isRepeatScreen = true;
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActPasswordRecover.this, () -> {
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
