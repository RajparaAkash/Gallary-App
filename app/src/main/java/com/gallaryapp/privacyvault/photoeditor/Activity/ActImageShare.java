package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Ads.NativeAds;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import java.io.File;

public class ActImageShare extends ActBase {

    private TextView headerTxt;
    private PhotoView sharePhotoview;

    private String filePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_image_share);

        // NativeSmall
        NativeAds.ShowNativeSmall(this, findViewById(R.id.nativeSmallLay), findViewById(R.id.nativeLay));

        idBind();
        setOnBackPressed();

        btnClick();

        Intent intent = getIntent();
        if (intent != null) {
            filePath = intent.getStringExtra("finalSavedImagePath");
        }

        Glide.with(this)
                .load(filePath)
                .error(R.drawable.place_holder_img)
                .into(sharePhotoview);

        headerTxt.setText(FileUtils.getFileNameFromPath(filePath));

        new Handler().postDelayed((Runnable) () -> {
            appReviewShow();
        }, 200);
    }

    private void idBind() {
        headerTxt = findViewById(R.id.headerTxt);
        sharePhotoview = findViewById(R.id.sharePhotoview);
    }

    private void btnClick() {
        findViewById(R.id.homeImg).setOnClickListener(v -> {
            Intent intent = new Intent(ActImageShare.this, ActDashboard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.shareWhatsappLay).setOnClickListener(view -> {
            if (isAppInstalled("com.whatsapp")) {
                shareFileTo("com.whatsapp");
            } else {
                Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.shareFacebookLay).setOnClickListener(view -> {
            if (isAppInstalled("com.facebook.katana")) {
                shareFileTo("com.facebook.katana");
            } else {
                Toast.makeText(this, "Facebook is not installed", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.shareInstaLay).setOnClickListener(view -> {
            if (isAppInstalled("com.instagram.android")) {
                shareFileTo("com.instagram.android");
            } else {
                Toast.makeText(this, "Instagram is not installed", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.shareTwitterLay).setOnClickListener(view -> {
            if (isAppInstalled("com.twitter.android")) {
                shareFileTo("com.twitter.android");
            } else {
                Toast.makeText(this, "Twitter is not installed", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.shareMoreLay).setOnClickListener(view -> {
            shareFileToMore();
        });
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void shareFileTo(String packageName) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setPackage(packageName); // Set the package name for the target app

        String mimeType = FileUtils.getMimeType(filePath);
        shareIntent.setType(mimeType);

        // Set the file URI as the intent data
        File file = new File(filePath);
        Uri uri = FileProvider.getUriForFile(ActImageShare.this, getApplicationContext().getPackageName() + ".provider", file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (isAppInstalled(packageName)) {
            startActivity(shareIntent); // Open the app if it's installed
        } else {
            Toast.makeText(this, "The app is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareFileToMore() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String mimeType = FileUtils.getMimeType(filePath);
        shareIntent.setType(mimeType);

        // Set the file URI as the intent data
        File file = new File(filePath);
        Uri uri = FileProvider.getUriForFile(ActImageShare.this, getApplicationContext().getPackageName() + ".provider", file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share media"));
    }

    private void appReviewShow() {
        try {
            ReviewManager reviewManager = ReviewManagerFactory.create(ActImageShare.this);
            Task<ReviewInfo> request = reviewManager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    ReviewInfo reviewInfo = task.getResult();
                    launchReviewFlow(reviewManager, reviewInfo);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchReviewFlow(ReviewManager reviewManager, ReviewInfo reviewInfo) {
        Task<Void> reviewFlow = reviewManager.launchReviewFlow(ActImageShare.this, reviewInfo);
        reviewFlow.addOnCompleteListener(task -> {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        });
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                UtilApp.isAlbumsFragChange = true;
                UtilApp.isAllMediaFragChange = true;
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}
