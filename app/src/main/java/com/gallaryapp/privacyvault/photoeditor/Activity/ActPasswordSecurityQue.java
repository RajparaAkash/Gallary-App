package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.R;

public class ActPasswordSecurityQue extends ActBase {

    Spinner securityQueSpinner;
    EditText securityQueAnswerEt;

    String[] securityQueArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_password_security_que);

        idBind();
        setOnBackPressed();

        securityQueArray = getResources().getStringArray(R.array.securityQuestion);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item_privacy, securityQueArray);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        securityQueSpinner.setAdapter((SpinnerAdapter) arrayAdapter);

        findViewById(R.id.saveTxt).setOnClickListener(view -> saveClick());
    }

    private void idBind() {
        securityQueSpinner = findViewById(R.id.securityQueSpinner);
        securityQueAnswerEt = findViewById(R.id.securityQueAnswerEt);
    }

    public void saveClick() {
        CharSequence inputAnswer = securityQueAnswerEt.getText().toString().trim();
        if (TextUtils.isEmpty(inputAnswer)) {
            securityQueAnswerEt.setError(getString(R.string.str_192));
            securityQueAnswerEt.requestFocus();
        } else {

            if (MyPreference.get_SecurityQuestion().equalsIgnoreCase(securityQueArray[securityQueSpinner.getSelectedItemPosition()])
                    && inputAnswer.equals(MyPreference.get_SecurityAnswer())) {
                InterstitialAds.ShowInterstitial(this, () -> {
                    startActivityForResult(new Intent(getApplicationContext(), ActPasswordRecover.class), 11);
                });
            } else {
                Toast.makeText(this, "Please select valid Question and Answer", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            setResult(-1, new Intent());
            finish();
        }
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActPasswordSecurityQue.this, () -> {
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
