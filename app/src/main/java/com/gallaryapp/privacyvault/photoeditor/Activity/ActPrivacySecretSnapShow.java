package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ActPrivacySecretSnapShow extends ActBase {

    private TextView secretSnapTimeTxt;
    private ImageView secretSnapFullImg;

    private String imgCapMilli;
    private String imgPath;

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_privacy_secret_snap_show);

        idBind();
        setOnBackPressed();

        imgPath = getIntent().getStringExtra("imgPath");
        imgCapMilli = getIntent().getStringExtra("imgCapMilli");

        findViewById(R.id.secretSnapDeleteImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(ActPrivacySecretSnapShow.this.imgPath);
                if (file.exists()) {
                    file.delete();
                }
                ActPrivacySecretSnapShow.this.setResult(-1);
                ActPrivacySecretSnapShow.this.finish();
                Toast.makeText(ActPrivacySecretSnapShow.this, "Delete Photo Successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        String imgDate = getDate(Long.parseLong(this.imgCapMilli), "dd MMM yyyy hh:mm:ss aa");
        secretSnapTimeTxt.setText(getResources().getString(R.string.str_15) + " " + imgDate);

        File file = new File(this.imgPath);
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            secretSnapFullImg.setImageBitmap(bmp);
        }
    }

    private void idBind() {
        secretSnapTimeTxt = findViewById(R.id.secretSnapTimeTxt);
        secretSnapFullImg = findViewById(R.id.secretSnapFullImg);
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setResult(0);
                supportFinishAfterTransition();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}
