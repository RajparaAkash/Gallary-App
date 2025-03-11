package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragStatusWA;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragStatusWB;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActWaStatus extends ActBase {

    private TabLayout status_tabLayout;
    private ViewPager status_viewPager;

    ViewPagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wa_status);

        idBind();
        setOnBackPressed();

        setViewPagerMethod();
    }

    public void setViewPagerMethod() {
        setupViewPager(status_viewPager);
        status_tabLayout.setupWithViewPager(status_viewPager);

        status_tabLayout.getTabAt(0).setCustomView(getHeaderView());
        status_tabLayout.getTabAt(1).setCustomView(getHeaderView());
        /*status_tabLayout.getTabAt(2).setCustomView(getHeaderView());*/

        for (int i = 0; i < status_tabLayout.getTabCount(); i++) {

            TooltipCompat.setTooltipText(Objects.requireNonNull(status_tabLayout.getTabAt(i)).view, null);

            TabLayout.Tab tab = status_tabLayout.getTabAt(i);
            AppCompatTextView appCompatTextView = tab.getCustomView().findViewById(R.id.tbTxt);

            if (i == 0) {
                appCompatTextView.setTextColor(getResources().getColor(R.color.tab_selected_vault));
                appCompatTextView.setBackgroundResource(R.drawable.tab_select_bg_vault);
                appCompatTextView.setText(getResources().getString(R.string.str_195));

            } else {
                appCompatTextView.setTextColor(getResources().getColor(R.color.tab_unselected_vault));
                appCompatTextView.setBackgroundResource(R.drawable.tab_unselect_bg_vault);

                if (i == 1) {
                    appCompatTextView.setText(getResources().getString(R.string.str_196));
                } /*else {
                    appCompatTextView.setText(getResources().getString(R.string.str_197));
                }*/
            }
        }

        status_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                AppCompatTextView appCompatTextView = tab.getCustomView().findViewById(R.id.tbTxt);
                appCompatTextView.setTextColor(getResources().getColor(R.color.tab_selected_vault));
                appCompatTextView.setBackgroundResource(R.drawable.tab_select_bg_vault);

                if (tab.getText().equals("1")) {
                    appCompatTextView.setText(getResources().getString(R.string.str_195));
                } else if (tab.getText().equals("2")) {
                    appCompatTextView.setText(getResources().getString(R.string.str_196));
                }/* else {
                    appCompatTextView.setText(getResources().getString(R.string.str_197));
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                AppCompatTextView appCompatTextView = tab.getCustomView().findViewById(R.id.tbTxt);
                appCompatTextView.setTextColor(getResources().getColor(R.color.tab_unselected));
                appCompatTextView.setBackgroundResource(R.drawable.tab_unselect_bg_vault);

                if (tab.getText().equals("1")) {
                    appCompatTextView.setText(getResources().getString(R.string.str_195));
                } else if (tab.getText().equals("2")) {
                    appCompatTextView.setText(getResources().getString(R.string.str_196));
                } /*else {
                    appCompatTextView.setText(getResources().getString(R.string.str_197));
                }*/
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        int betweenSpace = -30;

        ViewGroup slidingTabStrip = (ViewGroup) status_tabLayout.getChildAt(0);

        for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.rightMargin = betweenSpace;
        }

        /*status_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Get the fragment at the selected position
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.status_viewPager + ":" + position);

                if (fragment instanceof FragStatusDownlaod) {
                    FragStatusDownlaod fragStatusDownlaod = (FragStatusDownlaod) fragment;
                    fragStatusDownlaod.updateDowloadData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/
    }

    private View getHeaderView() {
        return ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_tab_privacy, null, false);
    }

    private void setupViewPager(ViewPager viewPager) {
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new FragStatusWA(), "1");
        pagerAdapter.addFragment(new FragStatusWB(), "2");
        /*pagerAdapter.addFragment(new FragStatusDownlaod(), "3");*/
        viewPager.setAdapter(pagerAdapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void idBind() {
        status_tabLayout = findViewById(R.id.status_tabLayout);
        status_viewPager = findViewById(R.id.status_viewPager);
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActWaStatus.this, () -> {
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