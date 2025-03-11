package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyAlbumFiles;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyMoveAlbums;
import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceActions;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilPrivacyVault;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ActPrivacyAlbumFiles extends ActBase {

    private ImageView back_img;
    private TextView headerTxt;
    private ImageView privacyDataSelectAllImg;
    private RecyclerView privacyAlbumFilesRv;

    private LinearLayout privacyBottomDataLay;
    private LinearLayout privacyBottomDataShare;
    private LinearLayout privacyBottomDataMoveTo;
    private LinearLayout privacyBottomDataUnlock;
    private LinearLayout privacyBottomDataDelete;

    AdapterPrivacyAlbumFiles adapterPrivacyAlbumFiles;
    ArrayList<Media> privacyVaultMediaList;

    boolean selectionMode = false;
    String albumPath;
    String albumName;
    String selectedAlbum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_privacy_album_files);

        // Adaptive_Banner
        new BannerAds(this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));

        idBind();
        setOnBackPressed();

        albumPath = getIntent().getStringExtra("albumPath");
        albumName = new File(albumPath).getName();
        headerTxt.setText(albumName);

        refreshAlbum();

        privacyDataSelectAllImg.setOnClickListener(v -> {
            if (adapterPrivacyAlbumFiles.getSelectedCount() == adapterPrivacyAlbumFiles.getItemCount()) {

                back_img.setImageResource(R.drawable.header_back_img);
                headerTxt.setText(albumName);
                privacyDataSelectAllImg.setVisibility(View.GONE);
                privacyBottomDataLay.setVisibility(View.GONE);

                adapterPrivacyAlbumFiles.clearSelected();
                privacyDataSelectAllImg.setImageResource(R.drawable.icon_img_unselected);

            } else {
                adapterPrivacyAlbumFiles.selectAll();
                privacyDataSelectAllImg.setImageResource(R.drawable.icon_img_selected);

                headerTxt.setText(adapterPrivacyAlbumFiles.getSelectedCount() + " of " + adapterPrivacyAlbumFiles.getItemCount());
            }
        });

        privacyBottomDataShare.setOnClickListener(v -> {
            ArrayList<String> filePaths = new ArrayList<>();
            for (int i = 0; i < adapterPrivacyAlbumFiles.getSelected().size(); i++) {
                filePaths.add(adapterPrivacyAlbumFiles.getSelected().get(i).getPath());
            }
            shareMultipleFiles(filePaths);
        });

        privacyBottomDataMoveTo.setOnClickListener(view -> {
            dialogFileMoveToAlbum();
        });

        privacyBottomDataUnlock.setOnClickListener(view -> {
            unlockMedia();
        });

        privacyBottomDataDelete.setOnClickListener(view -> {
            dialogFileDelete();
        });
    }

    private void refreshAlbum() {
        privacyAlbumFilesRv.setLayoutManager(new GridLayoutManager(this, 3));
        privacyVaultMediaList = getMediaFiles(albumPath);

        if (privacyVaultMediaList.size() == 0) {
            finish();
            return;
        }

        adapterPrivacyAlbumFiles = new AdapterPrivacyAlbumFiles(this, privacyVaultMediaList, new InterfaceActions() {
            @Override
            public void onItemSelected(int i, ImageView imageView) {

            }

            @Override
            public void onSelectMode(boolean z) {
                selectionMode = z;
            }

            @Override
            public void onSelectionCountChanged(int selectionCount, int totalCount) {

                if (selectionCount > 0) {
                    back_img.setImageResource(R.drawable.header_close_img);
                    headerTxt.setText(adapterPrivacyAlbumFiles.getSelectedCount() + " of " + adapterPrivacyAlbumFiles.getItemCount());
                    privacyDataSelectAllImg.setVisibility(View.VISIBLE);
                    privacyBottomDataLay.setVisibility(View.VISIBLE);

                } else {
                    back_img.setImageResource(R.drawable.header_back_img);
                    headerTxt.setText(albumName);
                    privacyDataSelectAllImg.setVisibility(View.GONE);
                    privacyBottomDataLay.setVisibility(View.GONE);

                    adapterPrivacyAlbumFiles.clearSelected();
                }

                if (selectionCount == totalCount) {
                    privacyDataSelectAllImg.setImageResource(R.drawable.icon_img_selected);
                } else {
                    privacyDataSelectAllImg.setImageResource(R.drawable.icon_img_unselected);
                }

            }
        }, new AdapterPrivacyAlbumFiles.LongClickInter() {
            @Override
            public void onClick(int pos) {

            }
        }, new AdapterPrivacyAlbumFiles.NormalClickInter() {
            @Override
            public void onClick(int pos) {
                if (!selectionMode) {
                    InterstitialAds.ShowInterstitial(ActPrivacyAlbumFiles.this, () -> {
                        Intent i = new Intent(ActPrivacyAlbumFiles.this, ActPrivacyPager.class);
                        i.putExtra("data", privacyVaultMediaList);
                        i.putExtra("position", pos);
                        i.putExtra("fromSearch", false);
                        startActivity(i);
                    });
                }
            }
        });
        privacyAlbumFilesRv.setAdapter(adapterPrivacyAlbumFiles);
    }

    private void shareMultipleFiles(ArrayList<String> filePaths) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            uris.add(fileUri);
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("*/*"); // Set the MIME type to */* to allow sharing of mixed types
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Files"));
    }

    private void dialogFileMoveToAlbum() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_file_copy_move_hide, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        TextView txtDialog = bottomSheetDialog.findViewById(R.id.txtDialog);
        TextView text_cancel = bottomSheetDialog.findViewById(R.id.text_cancel);
        TextView text_copy_move_album = bottomSheetDialog.findViewById(R.id.text_copy_move_album);
        LinearLayout createNewAlbumLy = bottomSheetDialog.findViewById(R.id.createNewAlbumLy);

        txtDialog.setText(getResources().getString(R.string.str_140));
        text_copy_move_album.setText(getResources().getString(R.string.str_142));
        text_copy_move_album.setEnabled(false);
        text_copy_move_album.setAlpha(0.5f);

        createNewAlbumLy.setOnClickListener(view -> {
            dialogCreateAlbumPrivacy(bottomSheetDialog);
        });

        RecyclerView vaultFolderRV = bottomSheetDialog.findViewById(R.id.albumCopyMoveRV);

        vaultFolderRV.setLayoutManager(new LinearLayoutManager(this));
        vaultFolderRV.setAdapter(new AdapterPrivacyMoveAlbums(this, UtilPrivacyVault.getPrivacyVaultAlbums(), new AdapterPrivacyMoveAlbums.Click() {
            @Override
            public void ClickToActivity(String path) {
                text_copy_move_album.setEnabled(true);
                text_copy_move_album.setAlpha(1f);

                selectedAlbum = path;
            }
        }));

        text_cancel.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });

        text_copy_move_album.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();

            if (isFileAlreadyExists(adapterPrivacyAlbumFiles.getSelected().get(0).getFileName(), selectedAlbum)) {
                Toast.makeText(ActPrivacyAlbumFiles.this, "File already exists in album", Toast.LENGTH_SHORT).show();
                reSetWithOutAdapter();
                return;
            }

            MoveFilesAsyncTask task = new MoveFilesAsyncTask();
            task.execute();
        });
    }

    private class MoveFilesAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActPrivacyAlbumFiles.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            int totalFiles = adapterPrivacyAlbumFiles.getSelected().size();

            for (int i = 0; i < totalFiles; i++) {
                String oldFilePath = adapterPrivacyAlbumFiles.getSelected().get(i).getPath();

                File file = new File(selectedAlbum);
                if (!file.exists()) {
                    file.mkdir();
                }

                try {
                    File sourceFile = new File(oldFilePath);
                    File destFile = new File(selectedAlbum, sourceFile.getName());

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

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
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
                Toast.makeText(ActPrivacyAlbumFiles.this, "Move Successfully", Toast.LENGTH_SHORT).show();
                reSetWithAdapter();
                UtilApp.isValutActChange = true;
            } else {
                Toast.makeText(ActPrivacyAlbumFiles.this, "Failed to move files", Toast.LENGTH_SHORT).show();
                reSetWithAdapter();
            }
        }
    }

    private boolean isFileAlreadyExists(String fileName, String albumPath) {
        File destFile = new File(albumPath, fileName);
        return destFile.exists();
    }

    private void dialogCreateAlbumPrivacy(BottomSheetDialog bottomSheetDialog) {
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
            bottomSheetDialog.dismiss();

            if (!UtilApp.privateVaultFile.exists()) {
                UtilApp.privateVaultFile.mkdir();
            }

            File newFile = new File(UtilApp.privateVaultFile, createFolderEt.getText().toString());
            if (!newFile.exists()) {
                newFile.mkdir();
            }
            selectedAlbum = newFile.getPath();
            MoveFilesAsyncTask task = new MoveFilesAsyncTask();
            task.execute();
        });
    }

    private void dialogFileDelete() {
        Dialog dialogDelete = UtilDialog.getDialog(this, R.layout.dialog_file_delete);
        dialogDelete.setCancelable(true);

        TextView deleteTxt = (TextView) dialogDelete.findViewById(R.id.deleteTxt);
        TextView cancelTxt = (TextView) dialogDelete.findViewById(R.id.cancelTxt);

        deleteTxt.setOnClickListener(v -> {
            dialogDelete.dismiss();
            startDeleteFile();
            reSetWithAdapter();
            UtilApp.isValutActChange = true;
        });

        cancelTxt.setOnClickListener(v -> {
            dialogDelete.dismiss();
        });

        dialogDelete.show();
    }

    private void startDeleteFile() {
        for (int i = 0; i < adapterPrivacyAlbumFiles.getSelected().size(); i++) {
            File file = new File(adapterPrivacyAlbumFiles.getSelected().get(i).getPath());
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void unlockMedia() {
        UnlockFilesAsyncTask task = new UnlockFilesAsyncTask();
        task.execute();
    }

    private class UnlockFilesAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActPrivacyAlbumFiles.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            int totalFiles = adapterPrivacyAlbumFiles.getSelected().size();

            for (int i = 0; i < totalFiles; i++) {
                String oldFilePath = adapterPrivacyAlbumFiles.getSelected().get(i).getPath();

                File file = new File(UtilApp.privateUnlockPath);
                if (!file.exists()) {
                    file.mkdir();
                }

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

                    MediaScannerConnection.scanFile(ActPrivacyAlbumFiles.this,
                            new String[]{new File(UtilApp.privateUnlockPath).getAbsolutePath()}, null, null);

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
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
                Toast.makeText(ActPrivacyAlbumFiles.this, "Unlock Successfully", Toast.LENGTH_SHORT).show();
                reSetWithAdapter();
                UtilApp.isAllMediaFragChange = true;
                UtilApp.isAlbumsFragChange = true;
                UtilApp.isValutActChange = true;
            } else {
                Toast.makeText(ActPrivacyAlbumFiles.this, "Failed to unlock files", Toast.LENGTH_SHORT).show();
                reSetWithAdapter();
            }
        }
    }

    private void idBind() {
        back_img = findViewById(R.id.back_img);
        headerTxt = findViewById(R.id.headerTxt);
        privacyDataSelectAllImg = findViewById(R.id.privacyDataSelectAllImg);
        privacyAlbumFilesRv = findViewById(R.id.privacyAlbumFilesRv);

        privacyBottomDataLay = (LinearLayout) findViewById(R.id.privacyBottomDataLay);
        privacyBottomDataShare = (LinearLayout) findViewById(R.id.privacyBottomDataShare);
        privacyBottomDataMoveTo = (LinearLayout) findViewById(R.id.privacyBottomDataMoveTo);
        privacyBottomDataUnlock = (LinearLayout) findViewById(R.id.privacyBottomDataUnlock);
        privacyBottomDataDelete = (LinearLayout) findViewById(R.id.privacyBottomDataDelete);
    }

    private ArrayList<Media> getMediaFiles(String albumPath) {
        ArrayList<Media> mediaFiles = new ArrayList<>();
        File directory = new File(albumPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && (FileUtils.isImage(file.getName()) || FileUtils.isVideo(file.getName()))) {
                    mediaFiles.add(new Media(file.getAbsolutePath(), file.getName()));
                }
            }
        }
        return mediaFiles;
    }


    private void reSetWithAdapter() {
        reSetWithOutAdapter();
        refreshAlbum();
    }

    private void reSetWithOutAdapter() {
        selectionMode = false;
        back_img.setImageResource(R.drawable.header_back_img);
        headerTxt.setText(albumName);
        privacyDataSelectAllImg.setVisibility(View.GONE);
        privacyBottomDataLay.setVisibility(View.GONE);

        adapterPrivacyAlbumFiles.clearSelected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilApp.isValutGridActChange) {
            UtilApp.isValutGridActChange = false;
            refreshAlbum();
        }
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!selectionMode) {
                    InterstitialAds.ShowInterstitialBack(ActPrivacyAlbumFiles.this, () -> {
                        finish();
                    });
                } else {
                    reSetWithOutAdapter();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}