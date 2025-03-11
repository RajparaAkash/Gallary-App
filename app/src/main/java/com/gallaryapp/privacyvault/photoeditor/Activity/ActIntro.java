package com.gallaryapp.privacyvault.photoeditor.Activity;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.viewpager.widget.ViewPager;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterIntro;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.NativeAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Intro;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class ActIntro extends ActBase {

    private ViewPager introViewPager;
    private WormDotsIndicator introIndicator;
    private TextView skipTxt;
    private TextView nextTxt;

    private List<Intro> pageList;
    private boolean checkPermissionsAgain = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_intro);

        // NativeSmall
        NativeAds.ShowNativeSmall(this, findViewById(R.id.nativeSmallLay), findViewById(R.id.nativeLay));

        idBind();
        setOnBackPressed();

        pageList = new ArrayList<>();

        pageList.add(new Intro(R.drawable.intro_img_1, getResources().getString(R.string.str_1), getResources().getString(R.string.str_4)));
        pageList.add(new Intro(R.drawable.intro_img_2, getResources().getString(R.string.str_2), getResources().getString(R.string.str_5)));
        pageList.add(new Intro(R.drawable.intro_img_3, getResources().getString(R.string.str_3), getResources().getString(R.string.str_6)));
        pageList.add(new Intro(R.drawable.intro_img_4, "Background Remover", "Advanced AI tool for removing backgrounds seamlessly."));

        AdapterIntro adapter = new AdapterIntro(this, pageList);
        introViewPager.setAdapter(adapter);

        introIndicator.setViewPager(introViewPager);

        introViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position < 3) {
                    skipTxt.setVisibility(View.VISIBLE);
                    nextTxt.setText(getString(R.string.str_115));
                } else {
                    skipTxt.setVisibility(View.GONE);
                    nextTxt.setText(getString(R.string.str_7));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        findViewById(R.id.skipTxt).setOnClickListener(v -> {
            checkPermission();
        });

        findViewById(R.id.nextTxt).setOnClickListener(v -> {
            int currentItem = introViewPager.getCurrentItem();
            if (currentItem < adapter.getCount() - 1) {
                introViewPager.setCurrentItem(currentItem + 1);
            } else {
                checkPermission();
            }
        });

    }

    private void idBind() {
        introViewPager = findViewById(R.id.introViewPager);
        introIndicator = findViewById(R.id.introIndicator);
        skipTxt = findViewById(R.id.skipTxt);
        nextTxt = findViewById(R.id.nextTxt);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissionsList = {
                    "android.permission.READ_MEDIA_IMAGES",
                    "android.permission.READ_MEDIA_VIDEO",
                    "android.permission.CAMERA"};

            Dexter.withContext(ActIntro.this).withPermissions(permissionsList).withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {

                        nextDashBoard();
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        UtilDialog.showPermissionDialog(ActIntro.this);
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(dexterError -> {
                Toast.makeText(ActIntro.this, "Error occurred! ", LENGTH_SHORT).show();
            }).onSameThread().check();

        } else {
            String[] permissionsList = {
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.CAMERA"};

            if (Build.VERSION.SDK_INT >= 29)
                permissionsList = new String[]{
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.CAMERA"};

            Dexter.withContext(ActIntro.this).withPermissions(permissionsList).withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {

                        nextDashBoard();
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        UtilDialog.showPermissionDialog(ActIntro.this);
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(dexterError -> {
                Toast.makeText(ActIntro.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
            }).onSameThread().check();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            checkPermissionsAgain = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissionsAgain) {
            checkPermissionsAgain = false;
            checkPermission();
        }
    }

    private void nextDashBoard() {
        MyPreference.set_IsFirstTime(false);
        InterstitialAds.ShowInterstitial(this, () -> {
            startActivity(new Intent(ActIntro.this, ActDashboard.class));
            finish();
        });
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
                System.exit(0);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);
    }
}