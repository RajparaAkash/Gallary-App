package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyPager;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;

public class ActRecycleBinPager extends ActBase {

    TextView headerTxt;
    ViewPager recycleBinViewPager;

    ArrayList<Media> recycleBinList = new ArrayList<>();
    int currentPos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recycle_bin_pager);

        inBinding();
        setOnBackPressed();

        recycleBinList = (ArrayList) getIntent().getSerializableExtra("data");
        currentPos = getIntent().getIntExtra("position", 0);
        initUi();
    }

    private void inBinding() {
        headerTxt = (TextView) findViewById(R.id.headerTxt);
        recycleBinViewPager = (ViewPager) findViewById(R.id.recycleBinViewPager);
    }

    private void initUi() {

        headerTxt.setText(recycleBinList.get(currentPos).getFileName());

        recycleBinViewPager.setAdapter(new AdapterPrivacyPager(this, recycleBinList));
        recycleBinViewPager.setCurrentItem(currentPos);
        recycleBinViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                headerTxt.setText(recycleBinList.get(pos).getFileName());
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(getApplicationContext()).clearMemory();
        Glide.get(getApplicationContext()).trimMemory(80);
        System.gc();
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActRecycleBinPager.this, () -> {
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