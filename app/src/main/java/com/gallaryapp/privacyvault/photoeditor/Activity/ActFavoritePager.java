package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterFavouritePager;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;

public class ActFavoritePager extends ActBase {

    TextView headerTxt;
    ViewPager favoriteVP;

    ArrayList<Media> favoriteList = new ArrayList<>();
    int currentPos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_favorite_pager);

        inBinding();
        setOnBackPressed();

        favoriteList = (ArrayList) getIntent().getSerializableExtra("data");
        currentPos = getIntent().getIntExtra("position", 0);
        initUi();
    }

    private void inBinding() {
        headerTxt = (TextView) findViewById(R.id.headerTxt);
        favoriteVP = (ViewPager) findViewById(R.id.favoriteVP);
    }

    private void initUi() {

        headerTxt.setText(FileUtils.getFileNameFromPath(favoriteList.get(currentPos).getPath()));

        favoriteVP.setAdapter(new AdapterFavouritePager(this, favoriteList));
        favoriteVP.setCurrentItem(currentPos);
        favoriteVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                headerTxt.setText(FileUtils.getFileNameFromPath(favoriteList.get(pos).getPath()));
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
                InterstitialAds.ShowInterstitialBack(ActFavoritePager.this, () -> {
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
