package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterWaStatusPager;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragBase;
import com.gallaryapp.privacyvault.photoeditor.Interface.OnDeleteInterface;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilMediaDelete;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;

public class ActWaStatusPager extends ActBase implements FragBase.MediaTapListener {

    private TextView headerTxt;
    private ViewPager waStatusVP;

    private int position;
    ArrayList<Media> waStatusPagerList = new ArrayList<>();
    ArrayList<Media> deletedList = new ArrayList<>();
    AdapterWaStatusPager adapterWaStatusPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wa_status_pager);

        idBind();
        setOnBackPressed();

        waStatusPagerList = getIntent().getParcelableArrayListExtra("args_media");
        position = getIntent().getIntExtra("args_position", 0);

        setViewPager();

        findViewById(R.id.waStatusBottomShare).setOnClickListener(view -> {

            int currentItem = waStatusVP.getCurrentItem();
            Media currentItemData = waStatusPagerList.get(currentItem);
            String filePath = currentItemData.getPath();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            String mimeType = FileUtils.getMimeType(filePath);
            shareIntent.setType(mimeType);

            File file = new File(filePath);
            Uri uri = FileProvider.getUriForFile(ActWaStatusPager.this, getApplicationContext().getPackageName() + ".provider", file);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share media"));
        });

        findViewById(R.id.waStatusBottomDelete).setOnClickListener(view -> {
            dialogFileDelete();
        });

        findViewById(R.id.waStatusBottomInfo).setOnClickListener(view -> {
            dialogFileDetails();
        });
    }

    private void setViewPager() {
        updateTitle();
        adapterWaStatusPager = new AdapterWaStatusPager(getSupportFragmentManager(), waStatusPagerList);
        waStatusVP.setAdapter(adapterWaStatusPager);
        waStatusVP.setCurrentItem(position);
        waStatusVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                position = pos;
                updateTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void dialogFileDelete() {
        if (getCurrentMedia() != null && getCurrentMedia().getFile() != null && getCurrentMedia().getFile().exists()) {
            ArrayList<Media> creationList = new ArrayList<>();
            creationList.add(getCurrentMedia());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                UtilMediaDelete.deleteWaStatusFile(this, creationList);
            }
        }
    }

    public void updateTitle() {
        if (waStatusPagerList.size() == 0) {
            Intent intent = new Intent();
            setResult(-2, intent);
            finish();
            return;
        }
        headerTxt.setText(getCurrentMedia().getFile().getName());
    }

    public Media getCurrentMedia() {
        int size = waStatusPagerList.size();
        int i = this.position;
        if (size <= i || i == -1) {
            return null;
        }
        return waStatusPagerList.get(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 124 && resultCode == RESULT_OK) {
            deletePendingMedia(requestCode, resultCode);
        }
    }

    public void deletePendingMedia(int requestCode, int resultCode) {
        UtilMediaDelete.deletePendingMedia(this, requestCode, resultCode, new OnDeleteInterface() {
            @Override
            public void onDeleteComplete() {
                if (adapterWaStatusPager != null) {
                    adapterWaStatusPager.notifyDataSetChanged();
                    updateTitle();
                }
            }

            @Override
            public void onMediaDeleteSuccess(boolean isSuccess, Media media) {
                if (isSuccess && adapterWaStatusPager != null) {
                    deletedList.add(media);
                    waStatusPagerList.remove(media);

                    UtilApp.isAlbumsFragChange = true;
                    UtilApp.isAllMediaFragChange = true;
                }
            }
        });
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

        int currentItem = waStatusVP.getCurrentItem();
        Media currentItemData = waStatusPagerList.get(currentItem);
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

    private void idBind() {
        headerTxt = findViewById(R.id.headerTxt);
        waStatusVP = findViewById(R.id.waStatusVP);
    }

    @Override
    public void onViewTapped() {

    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (deletedList.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("deletedPosList", deletedList);
                    setResult(-1, intent);
                    finish();
                } else {
                    InterstitialAds.ShowInterstitialBack(ActWaStatusPager.this, () -> {
                        finish();
                    });
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}
