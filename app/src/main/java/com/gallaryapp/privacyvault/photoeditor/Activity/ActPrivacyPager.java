package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyPager;
import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragBase;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ActPrivacyPager extends ActBase implements FragBase.MediaTapListener {

    private TextView headerTxt;
    private ViewPager privacyMediaViewPager;

    private LinearLayout p_bottomShare;
    private LinearLayout p_bottomUnlock;
    private LinearLayout p_bottomDelete;
    private LinearLayout p_bottomInfo;

    int currentPos;
    boolean fromSearch;
    ArrayList<Media> privacyVaultMediaList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_privacy_pager);

        // Adaptive_Banner
        new BannerAds(this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));

        inBinding();
        setOnBackPressed();

        privacyVaultMediaList = (ArrayList) getIntent().getSerializableExtra("data");
        currentPos = getIntent().getIntExtra("position", 0);
        fromSearch = getIntent().getBooleanExtra("fromSearch", false);
        initUi();

        p_bottomShare.setOnClickListener(view -> {
            int currentItem = privacyMediaViewPager.getCurrentItem();
            Media currentItemData = privacyVaultMediaList.get(currentItem);
            String filePath = currentItemData.getPath();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            String mimeType = FileUtils.getMimeType(filePath);
            shareIntent.setType(mimeType);

            File file = new File(filePath);
            Uri uri = FileProvider.getUriForFile(ActPrivacyPager.this, getApplicationContext().getPackageName() + ".provider", file);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share media"));
        });

        p_bottomUnlock.setOnClickListener(view -> {

            int currentItem = privacyMediaViewPager.getCurrentItem();
            Media currentItemData = privacyVaultMediaList.get(currentItem);
            String oldFilePath = currentItemData.getPath();

            UnlockFilesAsyncTask task = new UnlockFilesAsyncTask(currentItem, oldFilePath, UtilApp.privateUnlockPath);
            task.execute();
        });

        p_bottomDelete.setOnClickListener(view -> {
            dialogFileDelete();
        });

        p_bottomInfo.setOnClickListener(view -> {
            dialogFileDetails();
        });
    }

    private void inBinding() {
        headerTxt = (TextView) findViewById(R.id.headerTxt);
        privacyMediaViewPager = (ViewPager) findViewById(R.id.privacyMediaViewPager);

        p_bottomShare = (LinearLayout) findViewById(R.id.p_bottomShare);
        p_bottomUnlock = (LinearLayout) findViewById(R.id.p_bottomUnlock);
        p_bottomDelete = (LinearLayout) findViewById(R.id.p_bottomDelete);
        p_bottomInfo = (LinearLayout) findViewById(R.id.p_bottomInfo);
    }

    private void initUi() {
        headerTxt.setText(privacyVaultMediaList.get(currentPos).getFileName());
        privacyMediaViewPager.setAdapter(new AdapterPrivacyPager(this, privacyVaultMediaList));
        privacyMediaViewPager.setCurrentItem(currentPos);
        privacyMediaViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                headerTxt.setText(privacyVaultMediaList.get(pos).getFileName());
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onViewTapped() {

    }

    private class UnlockFilesAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private ProgressDialog progressDialog;
        private int currentItem;
        private String oldFilePath;
        private String folderPath;

        public UnlockFilesAsyncTask(int currentItem, String oldFilePath, String folderPath) {
            this.currentItem = currentItem;
            this.oldFilePath = oldFilePath;
            this.folderPath = folderPath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActPrivacyPager.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            File file = new File(folderPath);
            if (!file.exists()) {
                file.mkdir();
            }

            try {
                File sourceFile = new File(oldFilePath);
                File destFile = new File(folderPath, sourceFile.getName());

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

                MediaScannerConnection.scanFile(ActPrivacyPager.this,
                        new String[]{new File(folderPath).getAbsolutePath()}, null, null);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result) {
                Toast.makeText(ActPrivacyPager.this, "Unlock Successfully", Toast.LENGTH_SHORT).show();

                privacyVaultMediaList.remove(currentItem);
                privacyMediaViewPager.setAdapter(new AdapterPrivacyPager(ActPrivacyPager.this, privacyVaultMediaList));
                if (currentItem < privacyVaultMediaList.size()) {
                    privacyMediaViewPager.setCurrentItem(currentItem);
                } else if (currentItem > 0) {
                    privacyMediaViewPager.setCurrentItem(currentItem - 1);
                } else {
                    finish();
                }

                UtilApp.isAllMediaFragChange = true;
                UtilApp.isAlbumsFragChange = true;
                UtilApp.isValutActChange = true;
                UtilApp.isValutGridActChange = true;
                if (fromSearch) {
                    UtilApp.isValutSearchActChange = true;
                }
            } else {
                Toast.makeText(ActPrivacyPager.this, "Failed to unlock files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dialogFileDelete() {
        Dialog dialogDelete = UtilDialog.getDialog(this, R.layout.dialog_file_delete);

        TextView deleteTxt = (TextView) dialogDelete.findViewById(R.id.deleteTxt);
        TextView cancelTxt = (TextView) dialogDelete.findViewById(R.id.cancelTxt);

        cancelTxt.setOnClickListener(v -> {
            dialogDelete.dismiss();
        });

        deleteTxt.setOnClickListener(v -> {
            dialogDelete.dismiss();
            int currentItem = privacyMediaViewPager.getCurrentItem();
            Media currentItemData = privacyVaultMediaList.get(currentItem);
            String filePath = currentItemData.getPath();

            File fileToDelete = new File(filePath);
            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    UtilApp.isValutGridActChange = true;
                    if (fromSearch) {
                        UtilApp.isValutSearchActChange = true;
                    }
                    Toast.makeText(ActPrivacyPager.this, "Delete Successfully", Toast.LENGTH_SHORT).show();

                    privacyVaultMediaList.remove(currentItem);
                    privacyMediaViewPager.setAdapter(new AdapterPrivacyPager(ActPrivacyPager.this, privacyVaultMediaList));
                    if (currentItem < privacyVaultMediaList.size()) {
                        privacyMediaViewPager.setCurrentItem(currentItem);
                        headerTxt.setText(privacyVaultMediaList.get(currentItem).getFileName());
                    } else if (currentItem > 0) {
                        privacyMediaViewPager.setCurrentItem(currentItem - 1);
                        headerTxt.setText(privacyVaultMediaList.get(currentItem - 1).getFileName());
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(ActPrivacyPager.this, "Failed to delete files", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ActPrivacyPager.this, "File does not exist", Toast.LENGTH_SHORT).show();
            }

            MediaScannerConnection.scanFile(ActPrivacyPager.this,
                    new String[]{new File(filePath).getAbsolutePath()}, null, null);
        });

        dialogDelete.show();
    }

    private void dialogFileDetails() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_media_details, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        TextView moreD_name_txt = bottomSheetView.findViewById(R.id.moreD_name_txt);
        TextView moreD_type_txt = bottomSheetView.findViewById(R.id.moreD_type_txt);
        TextView moreD_size_txt = bottomSheetView.findViewById(R.id.moreD_size_txt);
        TextView moreD_date_txt = bottomSheetView.findViewById(R.id.moreD_date_txt);
        TextView moreD_resolution_txt = bottomSheetView.findViewById(R.id.moreD_resolution_txt);
        TextView moreD_path_txt = bottomSheetView.findViewById(R.id.moreD_path_txt);
        TextView tvOk = bottomSheetView.findViewById(R.id.tvOk);

        int currentItem = privacyMediaViewPager.getCurrentItem();
        Media currentItemData = privacyVaultMediaList.get(currentItem);
        String imagePath = currentItemData.getPath();

        moreD_name_txt.setText(FileUtils.getFileNameFromPath(imagePath));
        moreD_size_txt.setText(FileUtils.getFileSize(imagePath));
        moreD_date_txt.setText(FileUtils.getFileLastModified(imagePath));
        moreD_path_txt.setText(imagePath);


        if (FileUtils.isImage(imagePath)) {
            moreD_type_txt.setText(FileUtils.getFileMimeType(imagePath));
            moreD_resolution_txt.setText(FileUtils.getFileResolution(imagePath));
        }

        if (FileUtils.isVideo(imagePath)) {
            moreD_type_txt.setText(FileUtils.getVideoMimeType(imagePath));
            moreD_resolution_txt.setText(FileUtils.getVideoResolution(imagePath));
        }

        tvOk.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
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
                InterstitialAds.ShowInterstitialBack(ActPrivacyPager.this, () -> {
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
