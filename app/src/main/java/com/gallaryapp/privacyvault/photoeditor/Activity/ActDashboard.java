package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.MyAdsPreference;
import com.gallaryapp.privacyvault.photoeditor.Ads.NativeAds;
import com.gallaryapp.privacyvault.photoeditor.BuildConfig;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragMainAlbum;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragMainMedia;
import com.gallaryapp.privacyvault.photoeditor.Interface.MoreClickListener;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.AppUpdate;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.LanguageDialog;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.LocaleManager;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.NetworkUtil;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActDashboard extends ActBase {

    private TextView headerTxt;
    private ImageView dashboardMoreImg;
    private TabLayout dashboardTabLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private LinearLayout drawer_aiRemoverLay;
    private LinearLayout drawer_privacyLay;
    private LinearLayout drawer_statusLay;
    private LinearLayout drawer_recycleLay;
    private LinearLayout drawer_favoriteLay;
    private LinearLayout drawer_languageLay;
    private LinearLayout drawer_settingsLay;
    private TextView drawer_versionTxt;

    private TextView total_photos;
    private TextView total_videos;
    private TextView total_albums;

    private FragMainAlbum albumsFragment;
    private FragMainMedia allMediaFragment;
    private Fragment currentFragment;

    private MoreClickListener moreClickListener;
    int currentTabPosition = 0;
    private boolean isDialogShowing = false;

    private AppUpdate appUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dashboard);

        idBind();
        setOnBackPressed();
        drawerClick();

        // Adaptive_Banner
        /*new BannerAds(this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));*/

        String text = getString(R.string.str_8);
        headerTxt.setText(Html.fromHtml(text));

        albumsFragment = new FragMainAlbum();
        allMediaFragment = new FragMainMedia();

        createTabIcons();
        showFragment(albumsFragment);

        dashboardTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        currentTabPosition = 0;
                        showFragment(albumsFragment);
                        moreClickListener = albumsFragment;
                        break;
                    case 1:
                        currentTabPosition = 1;
                        showFragment(allMediaFragment);
                        moreClickListener = allMediaFragment;
                        break;
                    case 2:
                        InterstitialAds.ShowInterstitial(ActDashboard.this, () -> {
                            startActivity(new Intent(ActDashboard.this, ActWaStatus.class));
                        });
                        break;
                    case 3:
                        InterstitialAds.ShowInterstitial(ActDashboard.this, () -> {
                            startActivity(new Intent(ActDashboard.this, ActPasswordSetup.class));
                        });
                        break;
                }

                // Update tab colors
                updateTabColors(tab, true);
                setDbMoreClickListener(moreClickListener);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabColors(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        moreClickListener = allMediaFragment;
                        break;
                    case 1:
                        moreClickListener = albumsFragment;
                        break;
                }
            }
        });

        findViewById(R.id.dashboardSearchImg).setOnClickListener(v -> {
            InterstitialAds.ShowInterstitial(this, () -> {
                if (currentTabPosition == 0) {
                    startActivity(new Intent(ActDashboard.this, ActSearchAlbum.class));
                } else if (currentTabPosition == 1) {
                    startActivity(new Intent(ActDashboard.this, ActSearchMedia.class));
                }
            });
        });

        findViewById(R.id.dashboardDrawerImg).setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        appUpdate = new AppUpdate(ActDashboard.this);
        appUpdate.checkAppUpdate();
    }

    private void idBind() {
        headerTxt = findViewById(R.id.headerTxt);
        dashboardMoreImg = findViewById(R.id.dashboardMoreImg);
        dashboardTabLayout = findViewById(R.id.dashboardTabLayout);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        drawer_aiRemoverLay = findViewById(R.id.drawer_aiRemoverLay);
        drawer_privacyLay = findViewById(R.id.drawer_privacyLay);
        drawer_statusLay = findViewById(R.id.drawer_statusLay);
        drawer_recycleLay = findViewById(R.id.drawer_recycleLay);
        drawer_favoriteLay = findViewById(R.id.drawer_favoriteLay);
        drawer_languageLay = findViewById(R.id.drawer_languageLay);
        drawer_settingsLay = findViewById(R.id.drawer_settingsLay);
        drawer_versionTxt = findViewById(R.id.drawer_versionTxt);
        total_photos = findViewById(R.id.total_photos);
        total_videos = findViewById(R.id.total_videos);
        total_albums = findViewById(R.id.total_albums);
    }

    public void drawerClick() {

        drawer_aiRemoverLay.setOnClickListener(view -> {
            drawerLayout.closeDrawer(navigationView);
            new Handler().postDelayed(() -> {
                InterstitialAds.ShowInterstitial(this, () -> {
                    startActivity(new Intent(ActDashboard.this, ActAiBgRemover.class));
                    overridePendingTransition(0, 0);
                });
            }, 300);
        });

        drawer_privacyLay.setOnClickListener(view -> {
            drawerLayout.closeDrawer(navigationView);
            new Handler().postDelayed(() -> {
                InterstitialAds.ShowInterstitial(this, () -> {
                    startActivity(new Intent(ActDashboard.this, ActPasswordSetup.class));
                    overridePendingTransition(0, 0);
                });
            }, 300);
        });

        drawer_statusLay.setOnClickListener(view -> {
            drawerLayout.closeDrawer(navigationView);
            new Handler().postDelayed(() -> {
                InterstitialAds.ShowInterstitial(this, () -> {
                    startActivity(new Intent(ActDashboard.this, ActWaStatus.class));
                    overridePendingTransition(0, 0);
                });
            }, 300);
        });

        drawer_recycleLay.setOnClickListener(view -> {
            drawerLayout.closeDrawer(navigationView);
            new Handler().postDelayed(() -> {
                InterstitialAds.ShowInterstitial(this, () -> {
                    startActivity(new Intent(ActDashboard.this, ActRecycleBin.class));
                    overridePendingTransition(0, 0);
                });
            }, 300);
        });

        drawer_favoriteLay.setOnClickListener(view -> {
            drawerLayout.closeDrawer(navigationView);
            new Handler().postDelayed(() -> {
                InterstitialAds.ShowInterstitial(this, () -> {
                    startActivity(new Intent(ActDashboard.this, ActFavorite.class));
                    overridePendingTransition(0, 0);
                });
            }, 300);
        });

        drawer_languageLay.setOnClickListener(view -> {
            drawerLayout.closeDrawer(navigationView);
            new Handler().postDelayed(() -> {
                new LanguageDialog(this, new LanguageDialog.onLanguage() {
                    @Override
                    public void setLang(int i) {
                        if (i >= 0 && i < LocaleManager.LocaleDef.SUPPORTED_LOCALES.length) {
                            setNewLocale(LocaleManager.LocaleDef.SUPPORTED_LOCALES[i]);
                        }
                    }
                }).show();
                /*InterstitialAds.ShowInterstitial(this, () -> {
                    startActivity(new Intent(ActDashboard.this, ActLanguage.class));
                    overridePendingTransition(0, 0);
                });*/
            }, 300);
        });

        drawer_settingsLay.setOnClickListener(view -> {
            drawerLayout.closeDrawer(navigationView);
            new Handler().postDelayed(() -> {
                InterstitialAds.ShowInterstitial(this, () -> {
                    startActivity(new Intent(ActDashboard.this, ActSettings.class));
                    overridePendingTransition(0, 0);
                });
            }, 300);
        });

        drawer_versionTxt.setText("Version : " + BuildConfig.VERSION_NAME);
    }

    private void setNewLocale(String str) {
        LocaleManager.setNewLocale(this, str);
        restartApp();
    }

    private void restartApp() {
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);

        /*Intent intent = new Intent(this, ActDashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();*/
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            transaction.add(R.id.fragment_container, fragment);
        }
        if (currentFragment != null && currentFragment != fragment) {
            transaction.hide(currentFragment);
        }
        transaction.show(fragment);
        transaction.setPrimaryNavigationFragment(fragment);
        transaction.setReorderingAllowed(true);
        transaction.commitNow();

        currentFragment = fragment;
    }

    private void createTabIcons() {

        String[] titleArr = {
                getResources().getString(R.string.str_83),
                getResources().getString(R.string.str_82),
                getResources().getString(R.string.str_154),
                getResources().getString(R.string.str_18)};

        for (int i = 0; i < titleArr.length; i++) {
            View tabView = LayoutInflater.from(this).inflate(R.layout.custom_tab_main, null);
            TextView dbTabTxt = tabView.findViewById(R.id.dbTabTxt);
            dbTabTxt.setText(titleArr[i]);
            ImageView dbTabImg = tabView.findViewById(R.id.dbTabImg);

            switch (i) {
                case 0:
                    dbTabImg.setImageResource(R.drawable.icon_main_tab_1);
                    break;
                case 1:
                    dbTabImg.setImageResource(R.drawable.icon_main_tab_2);
                    break;
                case 2:
                    dbTabImg.setImageResource(R.drawable.icon_main_tab_4);
                    break;
                case 3:
                    dbTabImg.setImageResource(R.drawable.icon_main_tab_3);
                    break;
            }

            if (i == 0) {
                dbTabTxt.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                dbTabImg.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
                dbTabImg.setBackgroundResource(R.drawable.bg_tab_img);
            } else {
                dbTabTxt.setTextColor(ContextCompat.getColor(this, R.color.tab_unselected));
                dbTabImg.setColorFilter(ContextCompat.getColor(this, R.color.tab_unselected));
                dbTabImg.setBackground(null);
            }

            dashboardTabLayout.addTab(dashboardTabLayout.newTab().setCustomView(tabView));
        }
    }

    private void updateTabColors(TabLayout.Tab tab, boolean selected) {
        View tabView = tab.getCustomView();
        TextView dbTabTxt = tabView.findViewById(R.id.dbTabTxt);
        ImageView dbTabImg = tabView.findViewById(R.id.dbTabImg);

        if (selected) {
            dbTabTxt.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            dbTabImg.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
            dbTabImg.setBackgroundResource(R.drawable.bg_tab_img);
        } else {
            dbTabTxt.setTextColor(ContextCompat.getColor(this, R.color.tab_unselected));
            dbTabImg.setColorFilter(ContextCompat.getColor(this, R.color.tab_unselected));
            dbTabImg.setBackground(null);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        appUpdate.onResume();

        total_photos.setText(String.valueOf(countImages()));
        total_videos.setText(String.valueOf(countVideos()));
        total_albums.setText(String.valueOf(countAlbums()));

        if (currentTabPosition == 0) {
            dashboardTabLayout.getTabAt(0).select();
        } else if (currentTabPosition == 1) {
            dashboardTabLayout.getTabAt(1).select();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appUpdate.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        appUpdate.onActivityResult(requestCode, resultCode);
    }

    private int countImages() {
        return getImagePaths().size();
    }

    private int countVideos() {
        return getVideoPaths().size();
    }

    private int countAlbums() {
        Set<String> albumPaths = new HashSet<>();
        List<String> imagePaths = getImagePaths();
        List<String> videoPaths = getVideoPaths();

        for (String path : imagePaths) {
            albumPaths.add(new File(path).getParent());
        }

        for (String path : videoPaths) {
            albumPaths.add(new File(path).getParent());
        }

        return albumPaths.size();
    }

    private List<String> getImagePaths() {
        List<String> imagePaths = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(columnIndex);
                    imagePaths.add(imagePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePaths;
    }

    private List<String> getVideoPaths() {
        List<String> videoPaths = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media.DATA};
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                while (cursor.moveToNext()) {
                    String videoPath = cursor.getString(columnIndex);
                    videoPaths.add(videoPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoPaths;
    }

    public void setDbMoreClickListener(MoreClickListener listener) {
        dashboardMoreImg.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMoreClicked();
            }
        });
    }

    private void exitAppDialogWithAds() {
        if (isDialogShowing) return;
        isDialogShowing = true;

        BottomSheetDialog dialogExit = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_exit_ads, null);
        dialogExit.setContentView(bottomSheetView);

        FrameLayout nativeBigLay = dialogExit.findViewById(R.id.nativeBigLay);
        FrameLayout nativeLay = dialogExit.findViewById(R.id.nativeLay);

        // NativeBig
        NativeAds.ShowNativeBig(this, nativeBigLay, nativeLay);

        dialogExit.findViewById(R.id.exitTxt).setOnClickListener(view -> {
            dialogExit.dismiss();
            finishAffinity();
            System.exit(0);
        });

        dialogExit.setOnDismissListener(dialog -> isDialogShowing = false);

        dialogExit.show();
    }

    private long BackPressedTime;

    private void exitApp() {
        if (BackPressedTime + 2000 > System.currentTimeMillis()) {
            try {
                System.gc();
                finishAffinity();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        BackPressedTime = System.currentTimeMillis();
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    if (NetworkUtil.isNetworkConnected(ActDashboard.this)) {
                        if (isDialogShowing) return;
                        if (MyAdsPreference.get_AdStatus().equalsIgnoreCase("on")
                                && MyAdsPreference.get_FlagNative().equalsIgnoreCase("on")) {
                            exitAppDialogWithAds();
                        } else {
                            exitApp();
                        }
                    } else {
                        exitApp();
                    }
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);
    }
}
