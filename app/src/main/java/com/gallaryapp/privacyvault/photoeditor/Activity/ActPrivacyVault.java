package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyAlbums;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragPrivacyPhotos;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragPrivacyVideos;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceActions;
import com.gallaryapp.privacyvault.photoeditor.Interface.MoreClickListener;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilPrivacyVault;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActPrivacyVault extends ActBase {

    ImageView back_img;
    TextView headerTxt;
    ImageView privacySearchImg;
    ImageView privacyListGridImg;
    ImageView privacyMoreImg1;
    ImageView privacyMoreImg2;
    ImageView privacyAlbumSelectAll;
    LinearLayout privacyLayoutAsList;
    RelativeLayout privacyLayoutAsGrid;
    RecyclerView privacyAlbumAsGridRv;
    TextView noDataFoundTxt;
    ImageView mediaAddDeleteImg;
    TabLayout privacyTabLayout;
    ViewPager privacyViewpager;

    LinearLayout privacyAlbumBottomLy;
    LinearLayout privacyAlbumBottomUnlock;
    LinearLayout privacyAlbumBottomDelete;
    LinearLayout privacyAlbumBottomPin;
    ImageView privacyAlbumBottomPinImg;
    TextView privacyAlbumBottomPinTxt;

    boolean isShowAsGrid = true;
    private AdapterPrivacyAlbums adapter;
    private ArrayList<Album> privacyVaultAlbumList = new ArrayList<>();
    private MoreClickListener buttonClickListener;
    ViewPagerAdapter pagerAdapter;
    boolean selectionMode = false;
    boolean isAlreadyPined = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_privacy_vault);

        UtilApp.isValutActChange = false;

        idBind();
        setOnBackPressed();

        setViewPagerMethod();
        setAdapterAsGrid();

        privacyMoreImg1.setOnClickListener(view -> {
            if (isShowAsGrid) {
                dialogPrivacyMore();
            }
        });

        privacyListGridImg.setOnClickListener(view -> {
            if (!isShowAsGrid) {
                privacyMoreImg2.setVisibility(View.GONE);
                privacyMoreImg1.setVisibility(View.VISIBLE);
                privacyLayoutAsList.setVisibility(View.GONE);
                privacyLayoutAsGrid.setVisibility(View.VISIBLE);
                privacyListGridImg.setImageResource(R.drawable.icon_privacy_list);
                isShowAsGrid = true;

                if (UtilApp.isValutActChange) {
                    setAdapterAsGrid();
                }

            } else {
                privacyMoreImg1.setVisibility(View.GONE);
                privacyMoreImg2.setVisibility(View.VISIBLE);
                privacyLayoutAsGrid.setVisibility(View.GONE);
                privacyLayoutAsList.setVisibility(View.VISIBLE);
                privacyListGridImg.setImageResource(R.drawable.icon_privacy_grid);
                isShowAsGrid = false;
            }
        });

        mediaAddDeleteImg.setOnClickListener(view -> {
            InterstitialAds.ShowInterstitial(this, () -> {
                startActivity(new Intent(ActPrivacyVault.this, ActPrivacySelectFile.class));
            });
        });

        buttonClickListener = new MoreClickListener() {
            @Override
            public void onMoreClicked() {
                int currentFragmentPosition = privacyViewpager.getCurrentItem();
                if (currentFragmentPosition == 0) {
                    ((FragPrivacyPhotos) pagerAdapter.getItem(currentFragmentPosition)).onMoreClicked();
                } else if (currentFragmentPosition == 1) {
                    ((FragPrivacyVideos) pagerAdapter.getItem(currentFragmentPosition)).onMoreClicked();
                }
            }
        };

        privacyMoreImg2.setOnClickListener(v -> {
            buttonClickListener.onMoreClicked();
        });

        privacySearchImg.setOnClickListener(view -> {
            InterstitialAds.ShowInterstitial(this, () -> {
                startActivity(new Intent(ActPrivacyVault.this, ActPrivacySearch.class));
            });
        });

        privacyAlbumSelectAll.setOnClickListener(v -> {
            if (adapter.getSelectedCount() == (adapter.getItemCount() - 1)) {

                adapter.clearSelected();
                privacyAlbumSelectAll.setImageResource(R.drawable.icon_img_unselected);

            } else {
                adapter.selectAll();
                privacyAlbumSelectAll.setImageResource(R.drawable.icon_img_selected);
                headerTxt.setText(adapter.getSelectedCount() + " of " + (adapter.getItemCount() - 1));
            }
        });

        privacyAlbumBottomUnlock.setOnClickListener(view -> {
            ArrayList<Album> selectedAlbums = adapter.getSelected();
            for (Album album : selectedAlbums) {
                new UnlockFilesAsyncTask(album).execute();
            }
        });

        privacyAlbumBottomDelete.setOnClickListener(view -> {
            ArrayList<String> pathsToDelete = new ArrayList<>();
            for (int i = 0; i < adapter.getSelected().size(); i++) {
                pathsToDelete.add(adapter.getSelected().get(i).getPath());
            }
            DeleteFoldersTask deleteTask = new DeleteFoldersTask();
            deleteTask.execute(pathsToDelete);
        });

        privacyAlbumBottomPin.setOnClickListener(view -> {

            if (adapter != null) {
                if (isAlreadyPined) {
                    adapter.unPinSelectedAlbums();
                } else {
                    adapter.pinSelectedAlbums();
                }
            }
            adapter.clearSelected();
            setAdapterAsGrid();
        });
    }

    public class DeleteFoldersTask extends AsyncTask<ArrayList<String>, Void, Boolean> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActPrivacyVault.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(ArrayList<String>... params) {
            ArrayList<String> pathsToDelete = params[0];
            boolean success = true;
            for (String path : pathsToDelete) {
                if (!deleteFolder(path)) {
                    success = false;
                }
            }

            return success;
        }

        private boolean deleteFolder(String path) {
            File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    String[] children = file.list();
                    for (String child : children) {
                        if (!deleteFolder(new File(file, child).getPath())) {
                            return false;
                        }
                    }
                }
                return file.delete();
            }

            MediaScannerConnection.scanFile(ActPrivacyVault.this,
                    new String[]{new File(path).getAbsolutePath()}, null, null);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result) {
                Toast.makeText(ActPrivacyVault.this, "Delete Successfully", Toast.LENGTH_SHORT).show();
                adapter.clearSelected();
                setAdapterAsGrid();
            } else {
                Toast.makeText(ActPrivacyVault.this, "Failed To Delete", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UnlockFilesAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private ProgressDialog progressDialog;
        private Album selectedAlbum;

        public UnlockFilesAsyncTask(Album selectedAlbum) {
            this.selectedAlbum = selectedAlbum;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActPrivacyVault.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            File file = new File(UtilApp.privateUnlockPath);
            if (!file.exists()) {
                file.mkdir();
            }

            ArrayList<String> mediaPaths = selectedAlbum.getMediaPaths();
            int totalFiles = mediaPaths.size();

            for (int i = 0; i < totalFiles; i++) {
                String oldFilePath = mediaPaths.get(i);

                try {
                    File sourceFile = new File(oldFilePath);
                    File destFile = new File(UtilApp.privateUnlockPath, sourceFile.getName());

                    FileInputStream inputStream = new FileInputStream(sourceFile);
                    FileOutputStream outputStream = new FileOutputStream(destFile);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    inputStream.close();
                    outputStream.close();

                    if (sourceFile.exists()) {
                        sourceFile.delete();
                    }

                    MediaScannerConnection.scanFile(ActPrivacyVault.this,
                            new String[]{new File(UtilApp.privateUnlockPath).getAbsolutePath()}, null, null);

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result) {
                Toast.makeText(ActPrivacyVault.this, "Unlock Successfully", Toast.LENGTH_SHORT).show();
                adapter.clearSelected();
                setAdapterAsGrid();
                UtilApp.isAlbumsFragChange = true;
                UtilApp.isAllMediaFragChange = true;
            } else {
                Toast.makeText(ActPrivacyVault.this, "Failed to unlock files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickUpFile(String str) {
        InterstitialAds.ShowInterstitial(this, () -> {
            Intent intent = new Intent(ActPrivacyVault.this, ActPrivacySelectFile.class);
            intent.putExtra("folder_name", str);
            intent.putExtra("isDirectHide", true);
            startActivity(intent);
        });
    }

    private void loadAlbums() {
        SharedPreferences sharedPreferences = getSharedPreferences("AlbumPrefs", Context.MODE_PRIVATE);

        for (Album album : privacyVaultAlbumList) {
            boolean isPinned = sharedPreferences.getBoolean(album.getName(), false);
            album.setPinned(isPinned);
        }
    }

    private void setAdapterAsGrid() {
        privacyVaultAlbumList = UtilPrivacyVault.getPrivacyVaultAlbums();
        loadAlbums();

        if (privacyVaultAlbumList.size() != 0) {

            privacyAlbumAsGridRv.setVisibility(View.VISIBLE);
            noDataFoundTxt.setVisibility(View.GONE);

            privacyAlbumAsGridRv.setLayoutManager(new GridLayoutManager(this, MyPreference.get_Privacy_AlbumGridSize()));
            adapter = new AdapterPrivacyAlbums(this, privacyVaultAlbumList, new AdapterPrivacyAlbums.Click() {
                @Override
                public void ClickToActivity() {
                    if (!selectionMode) {
                        dialogCreateAlbumPrivacy();
                    }
                }
            }, new InterfaceActions() {
                @Override
                public void onItemSelected(int i, ImageView imageView) {

                }

                @Override
                public void onSelectMode(boolean z) {
                    selectionMode = z;
                    if (z) {
                        mediaAddDeleteImg.setVisibility(View.GONE);
                        privacySearchImg.setVisibility(View.GONE);
                        privacyListGridImg.setVisibility(View.GONE);
                        privacyMoreImg1.setVisibility(View.GONE);
                        privacyAlbumBottomLy.setVisibility(View.VISIBLE);
                        headerTxt.setText(adapter.getSelectedCount() + " of " + (adapter.getItemCount() - 1));
                        privacyAlbumSelectAll.setVisibility(View.VISIBLE);
                        back_img.setImageResource(R.drawable.header_close_img);
                    } else {
                        privacyAlbumBottomLy.setVisibility(View.GONE);
                        privacyAlbumSelectAll.setVisibility(View.GONE);
                        privacySearchImg.setVisibility(View.VISIBLE);
                        privacyListGridImg.setVisibility(View.VISIBLE);
                        privacyMoreImg1.setVisibility(View.VISIBLE);
                        mediaAddDeleteImg.setVisibility(View.VISIBLE);
                        headerTxt.setText(getResources().getString(R.string.str_9));
                        back_img.setImageResource(R.drawable.header_back_img);
                    }
                }

                @Override
                public void onSelectionCountChanged(int selectionCount, int totalCount) {

                    isAlreadyPined = alreadyPined();

                    if (isAlreadyPined) {
                        privacyAlbumBottomPinImg.setImageResource(R.drawable.icon_privacy_album_unpin);
                        privacyAlbumBottomPinTxt.setText(getResources().getString(R.string.str_80));
                    } else {
                        privacyAlbumBottomPinImg.setImageResource(R.drawable.icon_privacy_album_pin);
                        privacyAlbumBottomPinTxt.setText(getResources().getString(R.string.str_80));
                    }

                    if (selectionCount == (totalCount - 1)) {
                        privacyAlbumSelectAll.setImageResource(R.drawable.icon_img_selected);
                    } else {
                        privacyAlbumSelectAll.setImageResource(R.drawable.icon_img_unselected);
                    }
                    headerTxt.setText(adapter.getSelectedCount() + " of " + (adapter.getItemCount() - 1));
                }
            }, new AdapterPrivacyAlbums.LongClickInter() {
                @Override
                public void onClick(int pos) {

                }
            }, new AdapterPrivacyAlbums.NormalClickInter() {
                @Override
                public void onClick(int pos, String albumPath) {

                    if (!selectionMode) {
                        InterstitialAds.ShowInterstitial(ActPrivacyVault.this, () -> {
                            Intent intent = new Intent(ActPrivacyVault.this, ActPrivacyAlbumFiles.class);
                            intent.putExtra("albumPath", albumPath);
                            startActivity(intent);
                        });
                    }
                }
            });
            privacyAlbumAsGridRv.setAdapter(adapter);
        } else {
            privacyAlbumAsGridRv.setVisibility(View.GONE);
            noDataFoundTxt.setVisibility(View.VISIBLE);
        }
    }

    public boolean alreadyPined() {
        boolean anySelectedAlbumPinned = false;
        boolean allSelectedAlbumsUnpinned = true;

        for (Album album : adapter.getSelected()) {
            if (album.isPinned()) {
                anySelectedAlbumPinned = true;
            } else {
                allSelectedAlbumsUnpinned = false;
            }

            if (anySelectedAlbumPinned && !allSelectedAlbumsUnpinned) {
                break;
            }
        }

        return anySelectedAlbumPinned && allSelectedAlbumsUnpinned;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilApp.isValutActChange) {
            setAdapterAsGrid();
        }
    }

    public void setViewPagerMethod() {
        setupViewPager(privacyViewpager);
        privacyTabLayout.setupWithViewPager(privacyViewpager);

        privacyTabLayout.getTabAt(0).setCustomView(getHeaderView());
        privacyTabLayout.getTabAt(1).setCustomView(getHeaderView());

        for (int i = 0; i < privacyTabLayout.getTabCount(); i++) {

            TooltipCompat.setTooltipText(Objects.requireNonNull(privacyTabLayout.getTabAt(i)).view, null);

            TabLayout.Tab tab = privacyTabLayout.getTabAt(i);
            AppCompatTextView appCompatTextView = tab.getCustomView().findViewById(R.id.tbTxt);

            if (i == 0) {
                appCompatTextView.setTextColor(getResources().getColor(R.color.tab_selected_vault));
                appCompatTextView.setBackgroundResource(R.drawable.tab_select_bg_vault);
                appCompatTextView.setText(getResources().getString(R.string.str_117));

            } else {
                appCompatTextView.setTextColor(getResources().getColor(R.color.tab_unselected_vault));
                appCompatTextView.setBackgroundResource(R.drawable.tab_unselect_bg_vault);

                if (i == 1) {
                    appCompatTextView.setText(getResources().getString(R.string.str_118));
                }
            }
        }

        privacyTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                AppCompatTextView appCompatTextView = tab.getCustomView().findViewById(R.id.tbTxt);
                appCompatTextView.setTextColor(getResources().getColor(R.color.tab_selected_vault));
                appCompatTextView.setBackgroundResource(R.drawable.tab_select_bg_vault);

                if (tab.getText().equals("1")) {
                    appCompatTextView.setText(getResources().getString(R.string.str_117));
                } else if (tab.getText().equals("2")) {
                    appCompatTextView.setText(getResources().getString(R.string.str_118));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                AppCompatTextView appCompatTextView = tab.getCustomView().findViewById(R.id.tbTxt);
                appCompatTextView.setTextColor(getResources().getColor(R.color.tab_unselected));
                appCompatTextView.setBackgroundResource(R.drawable.tab_unselect_bg_vault);

                if (tab.getText().equals("1")) {
                    appCompatTextView.setText(getResources().getString(R.string.str_117));
                } else if (tab.getText().equals("2")) {
                    appCompatTextView.setText(getResources().getString(R.string.str_118));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        int betweenSpace = -30;

        ViewGroup slidingTabStrip = (ViewGroup) privacyTabLayout.getChildAt(0);

        for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.rightMargin = betweenSpace;
        }


        privacyViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Get the fragment at the selected position
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.privacyViewpager + ":" + position);

                if (fragment instanceof FragPrivacyPhotos) {
                    FragPrivacyPhotos fragPrivacyPhotos = (FragPrivacyPhotos) fragment;
                    if (fragPrivacyPhotos.isSelectionModeEnabledPhoto()) {
                        fragPrivacyPhotos.unselectAllImages();
                    }
                } else if (fragment instanceof FragPrivacyVideos) {
                    FragPrivacyVideos fragPrivacyVideos = (FragPrivacyVideos) fragment;
                    if (fragPrivacyVideos.isSelectionModeEnabledVideo()) {
                        fragPrivacyVideos.unselectAllVideos();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private View getHeaderView() {
        return ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_tab_privacy, null, false);
    }

    private void setupViewPager(ViewPager viewPager) {
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new FragPrivacyPhotos(), "1");
        pagerAdapter.addFragment(new FragPrivacyVideos(), "2");
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

    public void dialogPrivacyMore() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_more_privacy_vault, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        LinearLayout p_more_thumbnail_columns = bottomSheetDialog.findViewById(R.id.p_more_thumbnail_columns);
        LinearLayout p_more_create_album = bottomSheetDialog.findViewById(R.id.p_more_create_album);
        LinearLayout p_more_secret_snap = bottomSheetDialog.findViewById(R.id.p_more_secret_snap);
        LinearLayout p_more_settings = bottomSheetDialog.findViewById(R.id.p_more_settings);

        p_more_thumbnail_columns.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            dialogThumbnailColumns();
        });

        p_more_create_album.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            dialogCreateAlbumPrivacy();
        });

        p_more_secret_snap.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            InterstitialAds.ShowInterstitial(this, () -> {
                startActivity(new Intent(ActPrivacyVault.this, ActPrivacySecretSnap.class));
            });
        });

        p_more_settings.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            InterstitialAds.ShowInterstitial(this, () -> {
                startActivity(new Intent(ActPrivacyVault.this, ActPrivacySetting.class));
            });
        });
    }

    public void dialogThumbnailColumns() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_thumbnail_columns, null);
        sheetDialog.setContentView(bottomSheetView);
        sheetDialog.show();

        LinearLayout columns2Lay = sheetDialog.findViewById(R.id.columns2Lay);
        LinearLayout columns3Lay = sheetDialog.findViewById(R.id.columns3Lay);
        LinearLayout columns4Lay = sheetDialog.findViewById(R.id.columns4Lay);

        CheckBox columns2CheckBox = sheetDialog.findViewById(R.id.columns2CheckBox);
        CheckBox columns3CheckBox = sheetDialog.findViewById(R.id.columns3CheckBox);
        CheckBox columns4CheckBox = sheetDialog.findViewById(R.id.columns4CheckBox);

        TextView cancelTxt = sheetDialog.findViewById(R.id.cancelTxt);
        TextView saveTxt = sheetDialog.findViewById(R.id.saveTxt);

        if (MyPreference.get_Privacy_AlbumGridSize() == 2) {
            columns2CheckBox.setChecked(true);
        } else if (MyPreference.get_Privacy_AlbumGridSize() == 3) {
            columns3CheckBox.setChecked(true);
        } else if (MyPreference.get_Privacy_AlbumGridSize() == 4) {
            columns4CheckBox.setChecked(true);
        }

        columns2Lay.setOnClickListener(v -> {
            columns2CheckBox.setChecked(true);
            columns3CheckBox.setChecked(false);
            columns4CheckBox.setChecked(false);
        });

        columns3Lay.setOnClickListener(v -> {
            columns2CheckBox.setChecked(false);
            columns3CheckBox.setChecked(true);
            columns4CheckBox.setChecked(false);
        });

        columns4Lay.setOnClickListener(v -> {
            columns2CheckBox.setChecked(false);
            columns3CheckBox.setChecked(false);
            columns4CheckBox.setChecked(true);
        });

        cancelTxt.setOnClickListener(v -> {
            sheetDialog.dismiss();
        });

        saveTxt.setOnClickListener(v -> {
            sheetDialog.dismiss();

            if (columns2CheckBox.isChecked()) {
                MyPreference.set_Privacy_AlbumGridSize(2);
                setAdapterAsGrid();
            } else if (columns3CheckBox.isChecked()) {
                MyPreference.set_Privacy_AlbumGridSize(3);
                setAdapterAsGrid();
            } else if (columns4CheckBox.isChecked()) {
                MyPreference.set_Privacy_AlbumGridSize(4);
                setAdapterAsGrid();
            }
        });
    }

    private void dialogCreateAlbumPrivacy() {
        Dialog dialogCreateAlbumPrivacy = UtilDialog.getDialog(this, R.layout.dialog_create_album);
        dialogCreateAlbumPrivacy.show();

        EditText createFolderEt = dialogCreateAlbumPrivacy.findViewById(R.id.createFolderEt);
        TextView cancelTxt = dialogCreateAlbumPrivacy.findViewById(R.id.cancelTxt);
        TextView createTxt = dialogCreateAlbumPrivacy.findViewById(R.id.createTxt);

        cancelTxt.setOnClickListener(view -> {
            dialogCreateAlbumPrivacy.dismiss();
        });

        createTxt.setOnClickListener(view -> {
            dialogCreateAlbumPrivacy.dismiss();

            if (!UtilApp.privateVaultFile.exists()) {
                UtilApp.privateVaultFile.mkdir();
            }

            File newFile = new File(UtilApp.privateVaultFile, createFolderEt.getText().toString());
            if (!newFile.exists()) {
                newFile.mkdir();
            }
            pickUpFile(createFolderEt.getText().toString());
        });
    }

    private void idBind() {
        back_img = findViewById(R.id.back_img);
        headerTxt = findViewById(R.id.headerTxt);
        privacySearchImg = findViewById(R.id.privacySearchImg);
        privacyListGridImg = findViewById(R.id.privacyListGridImg);
        privacyMoreImg1 = findViewById(R.id.privacyMoreImg1);
        privacyMoreImg2 = findViewById(R.id.privacyMoreImg2);
        privacyAlbumSelectAll = findViewById(R.id.privacyAlbumSelectAll);
        privacyLayoutAsList = findViewById(R.id.privacyLayoutAsList);
        privacyLayoutAsGrid = findViewById(R.id.privacyLayoutAsGrid);
        privacyAlbumAsGridRv = findViewById(R.id.privacyAlbumAsGridRv);
        noDataFoundTxt = findViewById(R.id.noDataFoundTxt);
        mediaAddDeleteImg = findViewById(R.id.mediaAddDeleteImg);
        privacyTabLayout = findViewById(R.id.privacyTabLayout);
        privacyViewpager = findViewById(R.id.privacyViewpager);

        privacyAlbumBottomLy = (LinearLayout) findViewById(R.id.privacyAlbumBottomLy);
        privacyAlbumBottomUnlock = (LinearLayout) findViewById(R.id.privacyAlbumBottomUnlock);
        privacyAlbumBottomDelete = (LinearLayout) findViewById(R.id.privacyAlbumBottomDelete);
        privacyAlbumBottomPin = (LinearLayout) findViewById(R.id.privacyAlbumBottomPin);
        privacyAlbumBottomPinImg = (ImageView) findViewById(R.id.privacyAlbumBottomPinImg);
        privacyAlbumBottomPinTxt = (TextView) findViewById(R.id.privacyAlbumBottomPinTxt);
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.privacyViewpager + ":" + privacyViewpager.getCurrentItem());

                if (fragment instanceof FragPrivacyPhotos) {
                    FragPrivacyPhotos fragPrivacyPhotos = (FragPrivacyPhotos) fragment;
                    if (fragPrivacyPhotos.isSelectionModeEnabledPhoto()) {
                        fragPrivacyPhotos.unselectAllImages();
                        return;
                    }
                } else if (fragment instanceof FragPrivacyVideos) {
                    FragPrivacyVideos fragPrivacyVideos = (FragPrivacyVideos) fragment;
                    if (fragPrivacyVideos.isSelectionModeEnabledVideo()) {
                        fragPrivacyVideos.unselectAllVideos();
                        return;
                    }
                }

                if (!selectionMode) {
                    InterstitialAds.ShowInterstitialBack(ActPrivacyVault.this, () -> {
                        finish();
                    });
                } else {
                    adapter.clearSelected();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}
