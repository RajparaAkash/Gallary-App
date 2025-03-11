package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.NativeAds;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;

public class ActAiBgRemover extends ActBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ai_bg_remover);

        // NativeSmall
        NativeAds.ShowNativeSmall(this, findViewById(R.id.nativeSmallLay), findViewById(R.id.nativeLay));

        setOnBackPressed();

        findViewById(R.id.continueTxt).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction("android.intent.action.GET_CONTENT");
            startActivityForResult(Intent.createChooser(intent, "Select picture using"), 809);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 809 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            startCropActivity(selectedImageUri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK && data != null) {

            Uri croppedImageUri = UCrop.getOutput(data);

            Glide.with(this)
                    .asBitmap()
                    .load(FileUtils.getPath(this, croppedImageUri))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            ActAiBgRemoverAuto.editBitmap = resizeBtm(resource);
                            InterstitialAds.ShowInterstitial(ActAiBgRemover.this, () -> {
                                Intent intent = new Intent(ActAiBgRemover.this, ActAiBgRemoverAuto.class);
                                startActivity(intent);
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
    }

    private void startCropActivity(Uri imageUri) {
        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png")))
                .withOptions(options)
                .start(this);
    }

    public static Bitmap resizeBtm(Bitmap bitmap) {
        if (bitmap != null) {
            try {
                float width = bitmap.getWidth();
                float height = bitmap.getHeight();
                float max = Math.max(width / 2440.0f, height / 2440.0f);
                return max > 1.0f ? Bitmap.createScaledBitmap(bitmap, (int) (width / max), (int) (height / max), false) : bitmap;
            } catch (Exception unused) {
            }
        }
        return null;
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActAiBgRemover.this, () -> {
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