package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

import com.gallaryapp.privacyvault.photoeditor.Activity.ActPasswordSetup;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.util.ArrayList;

public class UtilPrivacyVault {

    public static ArrayList<Album> getPrivacyVaultAlbums() {
        ArrayList<Album> privacyAlbumsList = new ArrayList<>();
        File[] files = UtilApp.privateVaultFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.listFiles().length > 0) {
                    File[] mediaFiles = file.listFiles();
                    if (mediaFiles != null && mediaFiles.length > 0) {
                        String thumbnailPath = mediaFiles[0].getPath();

                        ArrayList<String> mediaPaths = new ArrayList<>();
                        for (File mediaFile : mediaFiles) {
                            mediaPaths.add(mediaFile.getPath());
                        }
                        privacyAlbumsList.add(new Album(file.getName(), file.getPath(), thumbnailPath, mediaFiles.length, mediaPaths));
                    }
                }
            }
        }
        return privacyAlbumsList;
    }

    public static ArrayList<Media> getPrivacyVaultAll() {
        ArrayList<Media> privacyMediaAllList = new ArrayList<>();
        if (UtilApp.privateVaultFile.exists() && UtilApp.privateVaultFile.isDirectory()) {
            scanAll(UtilApp.privateVaultFile, privacyMediaAllList);
        }
        return privacyMediaAllList;
    }

    public static ArrayList<Media> getPrivacyVaultImages() {
        ArrayList<Media> privacyMediaImageList = new ArrayList<>();
        if (UtilApp.privateVaultFile.exists() && UtilApp.privateVaultFile.isDirectory()) {
            scanImages(UtilApp.privateVaultFile, privacyMediaImageList);
        }
        return privacyMediaImageList;
    }

    public static ArrayList<Media> getPrivacyVaultVideos() {
        ArrayList<Media> privacyMediaVideoList = new ArrayList<>();
        if (UtilApp.privateVaultFile.exists() && UtilApp.privateVaultFile.isDirectory()) {
            scanVideos(UtilApp.privateVaultFile, privacyMediaVideoList);
        }
        return privacyMediaVideoList;
    }

    public static ArrayList<Media> getRecycleBinAll() {
        ArrayList<Media> recycleBinMediaAllList = new ArrayList<>();
        if (UtilApp.recycleBinFile.exists() && UtilApp.recycleBinFile.isDirectory()) {
            scanAll(UtilApp.recycleBinFile, recycleBinMediaAllList);
        }
        return recycleBinMediaAllList;
    }

    private static void scanAll(File directory, ArrayList<Media> privacyMediaAllList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanAll(file, privacyMediaAllList);
                } else if (file.isFile()) {
                    String filePath = file.getAbsolutePath();
                    String fileName = file.getName();
                    privacyMediaAllList.add(new Media(filePath, fileName));
                }
            }
        }
    }

    private static void scanImages(File directory, ArrayList<Media> privacyMediaImageList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanImages(file, privacyMediaImageList);
                } else if (file.isFile()) {
                    String filePath = file.getAbsolutePath();
                    String fileName = file.getName();
                    if (FileUtils.isImage(fileName)) {
                        privacyMediaImageList.add(new Media(filePath, fileName));
                    }
                }
            }
        }
    }

    private static void scanVideos(File directory, ArrayList<Media> privacyMediaVideoList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanVideos(file, privacyMediaVideoList);
                } else if (file.isFile()) {
                    String filePath = file.getAbsolutePath();
                    String fileName = file.getName();
                    if (FileUtils.isVideo(fileName)) {
                        privacyMediaVideoList.add(new Media(filePath, fileName));
                    }
                }
            }
        }
    }

    public static boolean isPrivacyVaultSetup(Activity mActivity) {
        if (TextUtils.isEmpty(MyPreference.get_Password())) {

            Dialog dialogSetup = UtilDialog.getDialog(mActivity, R.layout.dialog_setup_privacy);
            dialogSetup.setCancelable(true);

            TextView setPinTxt = (TextView) dialogSetup.findViewById(R.id.setPinTxt);
            TextView cancelTxt = (TextView) dialogSetup.findViewById(R.id.cancelTxt);

            setPinTxt.setOnClickListener(v -> {
                dialogSetup.dismiss();
                InterstitialAds.ShowInterstitial(mActivity, () -> {
                    mActivity.startActivity(new Intent(mActivity, ActPasswordSetup.class));
                });
            });

            cancelTxt.setOnClickListener(v -> {
                dialogSetup.dismiss();
            });

            dialogSetup.show();

            return false;
        }
        return true;
    }
}
