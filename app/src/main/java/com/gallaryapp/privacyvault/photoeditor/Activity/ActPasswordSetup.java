package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SwitchCompat;
import androidx.biometric.BiometricPrompt;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilCamera;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.MyViewCustom.Lock9Views;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.concurrent.Executor;

public class ActPasswordSetup extends ActBase {

    PreviewView cameraPreviewView;
    TextView headerTxt;
    TextView changeLockTypeTxt;
    LinearLayout pinMainLay;
    LinearLayout patternMainLay;
    LinearLayout securityQueMainLay;
    TextView extraTxt;
    TextView pinMessageTxt;
    LinearLayout pinEnterLay;
    TextView pin1Txt;
    TextView pin2Txt;
    TextView pin3Txt;
    TextView pin4Txt;
    ImageView fingerPrintPinImg;
    TextView patternMessageTxt;
    Lock9Views patternLockView;
    ImageView fingerPrintPatternImg;
    Spinner securityQueSpinner;
    EditText securityQueAnswerEt;
    SwitchCompat secretSnapSwitch;
    ConstraintLayout forgotPasswordLay;

    boolean isPinSet = true;
    boolean setupFirstTimePattern = true;
    String[] securityQueArray;
    private BiometricPrompt biometricPrompt;
    boolean isFirstTime;
    boolean isRepeatScreen;
    private Vibrator vibrator;
    public int tryCount = 0;
    String userAnswer = "";
    String pinCode = "";
    String firstTimeInputedPin = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_password);

        idBind();
        setOnBackPressed();

        int[][] states = new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}};
        int[] thumbColors = new int[]{getResources().getColor(R.color.switch_color_1), getResources().getColor(R.color.switch_color_2)};
        int[] trackColors = new int[]{getResources().getColor(R.color.switch_color_3), getResources().getColor(R.color.switch_color_4)};

        secretSnapSwitch.setThumbTintList(new ColorStateList(states, thumbColors));
        secretSnapSwitch.setTrackTintList(new ColorStateList(states, trackColors));

        patternMainLay.setVisibility(View.GONE);
        pinMainLay.setVisibility(View.VISIBLE);
        securityQueMainLay.setVisibility(View.GONE);

        securityQueArray = getResources().getStringArray(R.array.securityQuestion);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item_privacy, securityQueArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        securityQueSpinner.setAdapter((SpinnerAdapter) arrayAdapter);

        if (MyPreference.get_SecurityQuestion().isEmpty() && MyPreference.get_SecurityAnswer().isEmpty()) {
            forgotPasswordLay.setVisibility(View.GONE);
        } else {
            forgotPasswordLay.setVisibility(View.VISIBLE);
        }

        pinCode = MyPreference.get_Password();

        if (pinCode.isEmpty()) {
            changeLockTypeTxt.setVisibility(View.VISIBLE);
            fingerPrintPinImg.setVisibility(View.INVISIBLE);
            fingerPrintPatternImg.setVisibility(View.INVISIBLE);
            isFirstTime = true;
            isRepeatScreen = false;
            pinMessageTxt.setText(getString(R.string.str_19));
            initPinLockData();
        } else {
            changeLockTypeTxt.setVisibility(View.GONE);
            pinMessageTxt.setText(getString(R.string.str_68));
            fingerPrintPinVisible();
            fingerPrintPatternVisible();
            isFirstTime = false;
            setupLockData();

            if (MyPreference.get_IsEnableFingerprint()) {
                FigureAuthenticity();
            }
        }

        forgotPasswordLay.setOnClickListener(view -> dialogRecoverPassword());

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        changeLockTypeTxt.setOnClickListener(view -> {
            setupFirstTimePattern = true;

            if (isPinSet) {
                changeLockTypeTxt.setText(getText(R.string.str_74));
                isPinSet = false;
                setupPatternData();

            } else {
                changeLockTypeTxt.setText(getText(R.string.str_75));
                isPinSet = true;

                fingerPrintPinImg.setVisibility(View.INVISIBLE);
                fingerPrintPatternImg.setVisibility(View.INVISIBLE);
                isFirstTime = true;
                isRepeatScreen = false;
                pinMessageTxt.setText(getString(R.string.str_19));
                initPinLockData();
            }
        });

        fingerPrintPinImg.setOnClickListener(view -> FigureAuthenticity());

        fingerPrintPatternImg.setOnClickListener(view -> FigureAuthenticity());

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

        findViewById(R.id.saveTxt).setOnClickListener(view -> saveClick());

    }

    public void fingerPrintPinVisible() {
        if (MyPreference.get_IsEnableFingerprint()) {
            fingerPrintPinImg.setVisibility(View.VISIBLE);
        } else {
            fingerPrintPinImg.setVisibility(View.INVISIBLE);
        }
    }

    public void fingerPrintPatternVisible() {
        if (MyPreference.get_IsEnableFingerprint()) {
            fingerPrintPatternImg.setVisibility(View.VISIBLE);
        } else {
            fingerPrintPatternImg.setVisibility(View.INVISIBLE);
        }
    }

    public void saveClick() {
        userAnswer = securityQueAnswerEt.getText().toString().trim();
        if (TextUtils.isEmpty(userAnswer)) {
            securityQueAnswerEt.setError(getString(R.string.str_192));
            securityQueAnswerEt.requestFocus();
        } else {
            setSecurityQueAns();
        }
    }

    private void setSecurityQueAns() {
        MyPreference.set_SecurityQuestion(securityQueArray[securityQueSpinner.getSelectedItemPosition()]);
        MyPreference.set_SecurityAnswer(userAnswer);
        MyPreference.set_IsEnableSecretSnap(secretSnapSwitch.isChecked());
        nextGoActivity();
    }

    public void nextGoActivity() {
        InterstitialAds.ShowInterstitial(this, () -> {
            Intent intent = new Intent(getApplicationContext(), ActPrivacyVault.class);
            startActivity(intent);
            finish();
        });
    }

    private void idBind() {
        cameraPreviewView = findViewById(R.id.cameraPreviewView);
        headerTxt = findViewById(R.id.headerTxt);
        changeLockTypeTxt = findViewById(R.id.changeLockTypeTxt);
        pinMainLay = findViewById(R.id.pinMainLay);
        patternMainLay = findViewById(R.id.patternMainLay);
        securityQueMainLay = findViewById(R.id.securityQueMainLay);
        extraTxt = findViewById(R.id.extraTxt);
        pinMessageTxt = findViewById(R.id.pinMessageTxt);
        pinEnterLay = findViewById(R.id.pinEnterLay);
        pin1Txt = findViewById(R.id.pin1Txt);
        pin2Txt = findViewById(R.id.pin2Txt);
        pin3Txt = findViewById(R.id.pin3Txt);
        pin4Txt = findViewById(R.id.pin4Txt);
        fingerPrintPinImg = findViewById(R.id.fingerPrintPinImg);
        patternMessageTxt = findViewById(R.id.patternMessageTxt);
        patternLockView = findViewById(R.id.patternLockView);
        fingerPrintPatternImg = findViewById(R.id.fingerPrintPatternImg);
        securityQueSpinner = findViewById(R.id.securityQueSpinner);
        securityQueAnswerEt = findViewById(R.id.securityQueAnswerEt);
        secretSnapSwitch = findViewById(R.id.secretSnapSwitch);
        forgotPasswordLay = findViewById(R.id.forgotPasswordLay);
    }

    private Executor getMainThreadExecutor() {
        return new MainThreadExecutor();
    }

    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == 7) {
                    Toast.makeText(ActPasswordSetup.this, "You have tried maximum attempts with invalid Biometrics Details. Please try after 30 sec.", Toast.LENGTH_SHORT).show();
                } else if (errorCode != 5 && errorCode != 13) {
                    Toast.makeText(ActPasswordSetup.this, errString, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                nextGoActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                int fail = MyPreference.get_Biometric_FailCount();
                MyPreference.set_Biometric_FailCount(fail + 1);
                if (MyPreference.get_Biometric_FailCount() == 4) {
                    Toast.makeText(ActPasswordSetup.this, "Invalid Biometrics", Toast.LENGTH_SHORT).show();
                    biometricPrompt.cancelAuthentication();
                }
            }
        };
    }

    private void FigureAuthenticity() {
        try {
            BiometricPrompt.AuthenticationCallback authenticationCallback = getAuthenticationCallback();
            biometricPrompt = new BiometricPrompt(this, getMainThreadExecutor(), authenticationCallback);
            BiometricPrompt.PromptInfo build = new BiometricPrompt.PromptInfo.Builder().setTitle(getString(R.string.str_123))
                    .setNegativeButtonText(getString(R.string.str_124)).build();
            biometricPrompt.authenticate(build);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupLockData() {
        if (MyPreference.get_IsPinLock()) {
            initPinLockData();
            return;
        }
        initPatternData();
        patternLockView.setCallBack(new Lock9Views.CallBack() {
            @Override
            public void onFinish(String password) {
                if (password.length() < 4) {
                    patternMessageTxt.setText(getResources().getString(R.string.str_186));
                    return;
                }

                if (MyPreference.get_Password().equals(password)) {

                    tryCount = 0;
                    if (MyPreference.get_SecurityQuestion().equals("") && MyPreference.get_SecurityAnswer().equals("")) {
                        if (userAnswer.equals("")) {
                            patternMainLay.setVisibility(View.GONE);
                            pinMainLay.setVisibility(View.GONE);
                            securityQueAnswerEt.setVisibility(View.VISIBLE);
                            changeLockTypeTxt.setVisibility(View.INVISIBLE);
                            securityQueMainLay.setVisibility(View.VISIBLE);
                            headerTxt.setText(getResources().getString(R.string.str_28));
                            return;
                        }
                        patternMainLay.setVisibility(View.GONE);
                        pinMainLay.setVisibility(View.GONE);
                        changeLockTypeTxt.setVisibility(View.INVISIBLE);
                        securityQueMainLay.setVisibility(View.VISIBLE);
                        headerTxt.setText(getResources().getString(R.string.str_28));
                        return;
                    }
                    nextGoActivity();
                    return;
                }
                Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                patternMessageTxt.startAnimation(shake);
                patternMessageTxt.setText(getString(R.string.str_136));
                tryCount++;

                if (tryCount % 3 == 0 && MyPreference.get_IsEnableSecretSnap()) {
                    UtilCamera.takeSnapShot(ActPasswordSetup.this, cameraPreviewView);
                }
            }
        });
    }


    private void setupPatternData() {

        initPatternData();
        patternMessageTxt.setText(getResources().getString(R.string.str_143));

        patternLockView.setCallBack(new Lock9Views.CallBack() {
            @Override
            public void onFinish(String password) {

                if (password.length() < 4) {
                    patternMessageTxt.setText(getResources().getString(R.string.str_186));
                    return;
                }

                if (setupFirstTimePattern) {
                    firstTimeInputedPin = password;
                    patternMessageTxt.setText(getResources().getString(R.string.str_189));
                    setupFirstTimePattern = false;
                    return;
                }

                if (firstTimeInputedPin.equals(password)) {
                    MyPreference.set_Password(password);
                    patternMainLay.setVisibility(View.GONE);
                    pinMainLay.setVisibility(View.GONE);
                    changeLockTypeTxt.setVisibility(View.INVISIBLE);
                    securityQueMainLay.setVisibility(View.VISIBLE);
                    headerTxt.setText(getResources().getString(R.string.str_28));
                    MyPreference.set_IsPinLock(false);
                } else {
                    Animation shake2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                    patternMessageTxt.startAnimation(shake2);
                    patternMessageTxt.setText(getResources().getString(R.string.str_144));
                }
            }
        });
    }

    private void initPatternData() {
        pinMainLay.setVisibility(View.GONE);
        patternMainLay.setVisibility(View.VISIBLE);
    }

    private void initPinLockData() {
        pinMainLay.setVisibility(View.VISIBLE);
        patternMainLay.setVisibility(View.GONE);
        setDefault();
    }

    public void dialogRecoverPassword() {
        Dialog dialogRecover = UtilDialog.getDialog(this, R.layout.dialog_recover_password);
        dialogRecover.setCancelable(true);

        TextView recoverTxt = (TextView) dialogRecover.findViewById(R.id.recoverTxt);
        TextView cancelTxt = (TextView) dialogRecover.findViewById(R.id.cancelTxt);

        recoverTxt.setOnClickListener(v -> {
            dialogRecover.dismiss();
            InterstitialAds.ShowInterstitial(this, () -> {
                Intent intent = new Intent(ActPasswordSetup.this, ActPasswordSecurityQue.class);
                startActivityForResult(intent, 1);
            });
        });

        cancelTxt.setOnClickListener(v -> {
            dialogRecover.dismiss();
        });

        dialogRecover.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            try {
                setDefault();
            } catch (Exception e) {
            }
            pinCode = MyPreference.get_Password();
        }
        if (requestCode == 2) {
            try {
                setupLockData();
            } catch (Exception e2) {
            }
            pinCode = MyPreference.get_Password();
        }
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
                    if (isFirstTime && extraTxt.getText().toString().equals("")) {
                        pinMessageTxt.setText(R.string.str_68);
                        isRepeatScreen = false;
                    }
                    value = "";
                    break;
            }
            if (!value.isEmpty()) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_alphas));
                String text2 = extraTxt.getText().toString();
                extraTxt.setText(text2 + value);
                changePasscodeImage();
                if (extraTxt.getText().toString().length() == 4) {
                    if (isFirstTime) {
                        fingerPrintPinImg.setVisibility(View.INVISIBLE);
                        fingerPrintPatternImg.setVisibility(View.INVISIBLE);
                        performFirstTime();
                        return;
                    }
                    fingerPrintPinVisible();
                    fingerPrintPatternVisible();
                    if (pinCode.equals(extraTxt.getText().toString())) {
                        tryCount = 0;
                        if (MyPreference.get_SecurityQuestion().equals("") && MyPreference.get_SecurityAnswer().equals("")) {
                            if (userAnswer.equals("")) {
                                patternMainLay.setVisibility(View.GONE);
                                pinMainLay.setVisibility(View.GONE);
                                securityQueAnswerEt.setVisibility(View.VISIBLE);
                                changeLockTypeTxt.setVisibility(View.INVISIBLE);
                                securityQueMainLay.setVisibility(View.VISIBLE);
                                headerTxt.setText(getResources().getString(R.string.str_28));
                                return;
                            }
                            patternMainLay.setVisibility(View.GONE);
                            pinMainLay.setVisibility(View.GONE);
                            changeLockTypeTxt.setVisibility(View.INVISIBLE);
                            securityQueMainLay.setVisibility(View.VISIBLE);
                            headerTxt.setText(getResources().getString(R.string.str_28));
                            return;
                        }
                        nextGoActivity();
                        return;
                    }
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
                    pinEnterLay.startAnimation(shake);
                    pinMessageTxt.setText(getString(R.string.str_135));
                    setDefault();
                    tryCount++;

                    if (tryCount % 3 == 0 && MyPreference.get_IsEnableSecretSnap()) {
                        UtilCamera.takeSnapShot(this, cameraPreviewView);
                    }
                }
            }
        }
    }

    public void setDefault() {
        extraTxt.setText("");

        pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
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
        if (isRepeatScreen) {
            if (pinCode.equals(extraTxt.getText().toString())) {
                MyPreference.set_Password(pinCode);
                patternMainLay.setVisibility(View.GONE);
                pinMainLay.setVisibility(View.GONE);
                changeLockTypeTxt.setVisibility(View.INVISIBLE);
                securityQueMainLay.setVisibility(View.VISIBLE);
                headerTxt.setText(getResources().getString(R.string.str_28));
                if (userAnswer.equals("")) {
                    securityQueAnswerEt.setVisibility(View.VISIBLE);
                }
                return;
            }
            Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
            pinMessageTxt.startAnimation(shake);
            pinMessageTxt.setText(getString(R.string.str_145));
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
            return;
        }

        pinCode = extraTxt.getText().toString();
        extraTxt.setText("");

        pin1Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin2Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin3Txt.setBackgroundResource(R.drawable.bg_pin_unfill);
        pin4Txt.setBackgroundResource(R.drawable.bg_pin_unfill);

        pinMessageTxt.setText(R.string.str_20);
        isRepeatScreen = true;
    }


    public static class MainThreadExecutor implements Executor {
        private final Handler handler;

        private MainThreadExecutor() {
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void execute(Runnable r) {
            handler.post(r);
        }
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActPasswordSetup.this, () -> {
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
