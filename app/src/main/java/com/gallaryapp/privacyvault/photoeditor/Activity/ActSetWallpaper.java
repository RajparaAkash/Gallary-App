package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;

public class ActSetWallpaper extends ActBase {

    ImageView setWallpaperImage;
    TextView setAsWallpaperTxt;
    String wallpaperPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_set_wallpaper);

        // Adaptive_Banner
        new BannerAds(this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));

        idBind();
        setOnBackPressed();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            wallpaperPath = bundle.getString("setWallpaperPath", " ");
        }

        Glide.with(this).load(wallpaperPath).into(setWallpaperImage);

        setAsWallpaperTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;
                try {

                    File file = new File(wallpaperPath);

                    if (file != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            uri = FileProvider.getUriForFile(ActSetWallpaper.this, getApplicationContext().getPackageName() + ".provider", file);
                        } else {
                            uri = Uri.fromFile(file);
                        }
                        Intent intent = new Intent("android.intent.action.ATTACH_DATA");
                        intent.setDataAndType(uri, FileUtils.getMimeType(wallpaperPath));
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(intent, getString(R.string.str_138)));
                    }
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        });
    }

    private void idBind() {
        setWallpaperImage = findViewById(R.id.setWallpaperImage);
        setAsWallpaperTxt = findViewById(R.id.setAsWallpaperTxt);
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActSetWallpaper.this, () -> {
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