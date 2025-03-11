package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyViewCustom.Lock9Views;
import com.gallaryapp.privacyvault.photoeditor.R;

public class ActPasswordStyleChange extends ActBase {

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

    boolean isRepeatScreen;
    private Vibrator vibrator;
    boolean confirmPin = false;
    boolean confirmPattern = false;
    boolean setupFirstTime = true;
    boolean setupPatternLock = false;
    boolean setupPinLock = false;
    String firstTimeInputedPin = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_password);

        idBind();
        setOnBackPressed();

        pinMainLay.setVisibility(View.GONE);
        patternMainLay.setVisibility(View.GONE);

        if (MyPreference.get_IsPinLock()) {
            setupPinToPatternLock();
        } else {
            setupPatternToPinLock();
        }

        patternLockView.setCallBack(new Lock9Views.CallBack() {
            @Override
            public void onFinish(String password) {

                if (password.length() < 4) {
                    patternMessageTxt.setText(getResources().getString(R.string.str_186));
                    return;
                }

                if (confirmPattern) {
                    if (MyPreference.get_Password().equals(password)) {
                        pinMessageTxt.setText(R.string.str_120);
                        setupPinLock();
                        return;
                    }
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                    patternMessageTxt.startAnimation(shake);
                    patternMessageTxt.setText(getResources().getString(R.string.str_136));
                    return;
                }
                boolean z = setupPatternLock;
                if (z && setupFirstTime) {
                    firstTimeInputedPin = password;
                    setupFirstTime = false;
                    patternMessageTxt.setText(getResources().getString(R.string.str_189));
                } else if (z) {
                    if (firstTimeInputedPin.equals(password)) {
                        setResult(-1);
                        MyPreference.set_Password(password);
                        MyPreference.set_IsPinLock(false);
                        finish();
                        return;
                    }
                    Animation shake2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                    patternMessageTxt.startAnimation(shake2);
                    patternMessageTxt.setText(getResources().getString(R.string.str_144));
                } else {
                }
            }
        });

        patternMessageTxt.setText(R.string.str_188);
        pinMessageTxt.setText(R.string.str_121);
        pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        findViewById(R.id.pinTxt_0).setOnClickListener(v -> pinClicked(v));

        findViewById(R.id.pinTxt_1).setOnClickListener(v -> pinClicked(v));

        findViewById(R.id.pinTxt_2).setOnClickListener(v -> pinClicked(v));

        findViewById(R.id.pinTxt_3).setOnClickListener(v -> pinClicked(v));

        findViewById(R.id.pinTxt_4).setOnClickListener(v -> pinClicked(v));

        findViewById(R.id.pinTxt_5).setOnClickListener(v -> pinClicked(v));

        findViewById(R.id.pinTxt_6).setOnClickListener(v -> pinClicked(v));

        findViewById(R.id.pinTxt_7).setOnClickListener(v -> pinClicked(v));

        findViewById(R.id.pinTxt_8).setOnClickListener(v -> pinClicked(v));

        findViewById(R.id.pinTxt_9).setOnClickListener(v -> pinClicked(v));

        findViewById(R.id.pinTxt_Delete).setOnClickListener(v -> pinClicked(v));

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

    private void setupPatternLock() {
        firstTimeInputedPin = "";
        setupPatternLock = true;
        pinMainLay.setVisibility(View.GONE);
        patternMainLay.setVisibility(View.VISIBLE);
    }

    private void setupPinToPatternLock() {
        confirmPin = true;
        pinMainLay.setVisibility(View.VISIBLE);
        patternMainLay.setVisibility(View.GONE);
    }

    public void setupPinLock() {
        firstTimeInputedPin = "";
        setupPinLock = true;
        setupFirstTime = true;
        pinMainLay.setVisibility(View.VISIBLE);
        patternMainLay.setVisibility(View.GONE);
    }

    private void setupPatternToPinLock() {
        confirmPattern = true;
        pinMainLay.setVisibility(View.GONE);
        patternMainLay.setVisibility(View.VISIBLE);
    }

    public void pinClicked(View v) {
        if (extraTxt.getText().toString().length() < 5) {
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
                    if (setupFirstTime && extraTxt.getText().toString().equals("")) {
                        pinMessageTxt.setText(R.string.str_68);
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
                    if (confirmPin) {
                        if (MyPreference.get_Password().equals(extraTxt.getText().toString())) {
                            patternMessageTxt.setText(R.string.str_191);
                            setupPatternLock();
                            return;
                        }
                        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                        pinMessageTxt.startAnimation(shake);
                        pinMessageTxt.setText(getResources().getString(R.string.str_135));
                        shake.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                extraTxt.setText("");
                                pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                                pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                                pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                                pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                    } else if (setupPinLock) {

                        if (setupFirstTime) {
                            firstTimeInputedPin = extraTxt.getText().toString();
                            extraTxt.setText("");
                            pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                            pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                            pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                            pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                            pinMessageTxt.setText(R.string.str_20);
                            setupFirstTime = false;
                        } else if (firstTimeInputedPin.equals(extraTxt.getText().toString())) {
                            setResult(-1);
                            MyPreference.set_Password(firstTimeInputedPin);
                            MyPreference.set_IsPinLock(true);
                            finish();
                        } else {
                            Animation shake2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                            pinMessageTxt.startAnimation(shake2);
                            pinMessageTxt.setText(getResources().getString(R.string.str_145));
                            shake2.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    extraTxt.setText("");
                                    pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                                    pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                                    pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                                    pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                        }
                    }
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

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActPasswordStyleChange.this, () -> {
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
