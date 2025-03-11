package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyMoveAlbums;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacySelectAlbums;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacySelectAlbumsData;
import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceActions;
import com.gallaryapp.privacyvault.photoeditor.Interface.OnDeleteInterface;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilMediaDelete;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilPrivacyVault;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActPrivacySelectFile extends ActBase {

    private TextView headerTxt;
    private RelativeLayout layoutAllAlbum;
    private RecyclerView allAlbumListRv;
    private RecyclerView albumDataRv;
    private TextView notFoundTxt;
    private ImageView hideDoneImg;
    private ProgressBar progressBar;

    private AdapterPrivacySelectAlbums adapterPrivacySelectAlbums;
    private AdapterPrivacySelectAlbumsData adapterPrivacySelectAlbumsData;
    ArrayList<Media> vaultAlbumDataList;

    boolean selectionMode = false;
    boolean isDirectHide = false;
    private String folderName = "Default folder";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_privacy_select_file);

        idBind();
        setOnBackPressed();

        setAdapterAllAlbum();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            folderName = bundle.getString("folder_name", "Default folder");
            isDirectHide = bundle.getBoolean("isDirectHide", false);
        }

        hideDoneImg.setEnabled(false);
        hideDoneImg.setAlpha(0.5f);

        hideDoneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapterPrivacySelectAlbumsData.getSelected().size() > 20) {
                    Toast.makeText(ActPrivacySelectFile.this, "At a time maximum 20 images Move to privacy vault", Toast.LENGTH_SHORT).show();
                } else if (adapterPrivacySelectAlbumsData.getSelected().size() > 0) {
                    if (isDirectHide || UtilPrivacyVault.getPrivacyVaultAlbums().size() == 0) {
                        hideImage();
                    } else {
                        dialogFileHideToAlbum();
                    }
                }
            }
        });
    }

    private void setAdapterAllAlbum() {
        try {
            new LoadAlbumsTask(this).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapterDataAlbum(String path) {
        vaultAlbumDataList = getMediaFiles(getContentResolver(), path);

        albumDataRv.setLayoutManager(new GridLayoutManager(this, 3));
        adapterPrivacySelectAlbumsData = new AdapterPrivacySelectAlbumsData(this, vaultAlbumDataList, new InterfaceActions() {
            @Override
            public void onItemSelected(int i, ImageView imageView) {

            }

            @Override
            public void onSelectMode(boolean z) {
                selectionMode = z;
            }

            @Override
            public void onSelectionCountChanged(int selectionCount, int totalCount) {
                if (selectionCount == 0) {
                    hideDoneImg.setEnabled(false);
                    hideDoneImg.setAlpha(0.5f);
                } else {
                    hideDoneImg.setEnabled(true);
                    hideDoneImg.setAlpha(1f);
                }
            }
        }, new AdapterPrivacySelectAlbumsData.NormalClickInter() {
            @Override
            public void onClick(int pos) {

            }
        });
        albumDataRv.setAdapter(adapterPrivacySelectAlbumsData);
    }

    public class LoadAlbumsTask extends AsyncTask<Void, Void, ArrayList<Album>> {
        private Context context;

        public LoadAlbumsTask(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Album> doInBackground(Void... voids) {
            progressBar.setVisibility(View.VISIBLE);

            ArrayList<Album> folderList = new ArrayList<>();

            String[] projection = {MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME, MediaStore.Files.FileColumns.BUCKET_ID, MediaStore.Files.FileColumns.DATA};
            String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";

            Cursor query = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), projection, null, null, sortOrder);

            if (query != null) {
                try {
                    HashMap<String, Long> bucketLastModifiedTimes = new HashMap<>();
                    HashMap<String, Integer> bucketImageCounts = new HashMap<>();

                    while (query.moveToNext()) {
                        String bucketId = query.getString(1);
                        long lastModified = new File(query.getString(2)).lastModified();

                        // Update last modified time for the bucket
                        if (!bucketLastModifiedTimes.containsKey(bucketId) || lastModified > bucketLastModifiedTimes.get(bucketId)) {
                            bucketLastModifiedTimes.put(bucketId, lastModified);
                        }

                        // Update image count for the bucket
                        if (bucketImageCounts.containsKey(bucketId)) {
                            int count = bucketImageCounts.get(bucketId);
                            bucketImageCounts.put(bucketId, count + 1);
                        } else {
                            bucketImageCounts.put(bucketId, 1);
                        }
                    }

                    // Sort the bucketLastModifiedTimes map by value (last modified time)
                    List<Map.Entry<String, Long>> sortedList = new ArrayList<>(bucketLastModifiedTimes.entrySet());
                    sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

                    // Iterate over the sorted list to create VaultGridAlbum objects
                    for (Map.Entry<String, Long> entry : sortedList) {
                        String bucketId = entry.getKey();
                        long lastModifiedTime = entry.getValue();
                        int imageCount = bucketImageCounts.get(bucketId); // Get image count for the folder
                        query.moveToFirst(); // Reset cursor position

                        while (query.moveToNext()) {
                            String currentBucketId = query.getString(1);

                            // Add null check before equals
                            if (bucketId != null && bucketId.equals(currentBucketId)) {
                                String folderName = query.getString(0);
                                String filePath = query.getString(2);
                                folderList.add(new Album(folderName, filePath, filePath, imageCount));
                                break;
                            }
                        }
                    }
                } finally {
                    query.close();
                }
            }

            return folderList;
        }


//        @Override
//        protected ArrayList<Album> doInBackground(Void... voids) {
//            progressBar.setVisibility(View.VISIBLE);
//
//            ArrayList<Album> folderList = new ArrayList<>();
//
//            String[] projection = {MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME, MediaStore.Files.FileColumns.BUCKET_ID, MediaStore.Files.FileColumns.DATA};
//            String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";
//
//            Cursor query = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), projection, null, null, sortOrder);
//
//            if (query != null) {
//                try {
//                    HashMap<String, Long> bucketLastModifiedTimes = new HashMap<>();
//                    HashMap<String, Integer> bucketImageCounts = new HashMap<>();
//
//                    while (query.moveToNext()) {
//                        String bucketId = query.getString(1);
//                        long lastModified = new File(query.getString(2)).lastModified();
//
//                        // Update last modified time for the bucket
//                        if (!bucketLastModifiedTimes.containsKey(bucketId) || lastModified > bucketLastModifiedTimes.get(bucketId)) {
//                            bucketLastModifiedTimes.put(bucketId, lastModified);
//                        }
//
//                        // Update image count for the bucket
//                        if (bucketImageCounts.containsKey(bucketId)) {
//                            int count = bucketImageCounts.get(bucketId);
//                            bucketImageCounts.put(bucketId, count + 1);
//                        } else {
//                            bucketImageCounts.put(bucketId, 1);
//                        }
//                    }
//
//                    // Sort the bucketLastModifiedTimes map by value (last modified time)
//                    List<Map.Entry<String, Long>> sortedList = new ArrayList<>(bucketLastModifiedTimes.entrySet());
//                    sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
//
//                    // Iterate over the sorted list to create VaultGridAlbum objects
//                    for (Map.Entry<String, Long> entry : sortedList) {
//                        String bucketId = entry.getKey();
//                        long lastModifiedTime = entry.getValue();
//                        int imageCount = bucketImageCounts.get(bucketId); // Get image count for the folder
//                        query.moveToFirst(); // Reset cursor position
//                        while (query.moveToNext()) {
//                            if (bucketId.equals(query.getString(1))) {
//                                String folderName = query.getString(0);
//                                String filePath = query.getString(2);
//                                folderList.add(new Album(folderName, filePath, filePath, imageCount));
//                                break;
//                            }
//                        }
//                    }
//                } finally {
//                    query.close();
//                }
//            }
//
//            return folderList;
//        }

        @Override
        protected void onPostExecute(ArrayList<Album> result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            if (result.size() != 0) {

                // Adaptive_Banner
                new BannerAds(ActPrivacySelectFile.this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));

                allAlbumListRv.setVisibility(View.VISIBLE);
                notFoundTxt.setVisibility(View.GONE);

                allAlbumListRv.setLayoutManager(new GridLayoutManager(ActPrivacySelectFile.this, 3));
                adapterPrivacySelectAlbums = new AdapterPrivacySelectAlbums(ActPrivacySelectFile.this, result, new AdapterPrivacySelectAlbums.Click() {
                    @Override
                    public void ClickToActivity(String path, String albumName) {
                        layoutAllAlbum.setVisibility(View.GONE);
                        albumDataRv.setVisibility(View.VISIBLE);
                        setAdapterDataAlbum(path);
                        headerTxt.setText(albumName);

                        hideDoneImg.setEnabled(false);
                        hideDoneImg.setAlpha(0.5f);
                    }
                });
                allAlbumListRv.setAdapter(adapterPrivacySelectAlbums);

            } else {
                allAlbumListRv.setVisibility(View.GONE);
                albumDataRv.setVisibility(View.GONE);
                notFoundTxt.setVisibility(View.VISIBLE);
            }
        }
    }

    private ArrayList<Media> getMediaFiles(ContentResolver contentResolver, String albumPath) {
        ArrayList<Media> mediaFiles = new ArrayList<>();
        String[] projection = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.MIME_TYPE};
        String selection = MediaStore.MediaColumns.DATA + " LIKE ?";
        String[] selectionArgs = {albumPath + "%"};

        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                long fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE));
                long mediaId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));

                if (FileUtils.isImage(fileName) || FileUtils.isVideo(fileName)) {
                    mediaFiles.add(new Media(mediaId, filePath, fileName, fileSize, mimeType));
                }
            }
            cursor.close();
        }
        return mediaFiles;
    }

    private void hideImage() {
        if (Build.VERSION.SDK_INT >= 29) {
            UtilMediaDelete.deleteMoveToPrivacyFile(this, this.adapterPrivacySelectAlbumsData.getSelected(), new OnDeleteInterface() {
                @Override
                public void onDeleteComplete() {
                    adapterPrivacySelectAlbumsData.invalidateSelectedCount();
                }

                @Override
                public void onMediaDeleteSuccess(boolean isSuccess, Media media) {
                    if (isSuccess) {
                        adapterPrivacySelectAlbumsData.removeSelectedMedia(media);
                    }
                }
            });
        }
    }

    public class EncryptFile extends AsyncTask<Intent, String, String> {
        private ProgressDialog progressDialog;
        int requestCode;
        int resultCode;

        public EncryptFile(int requestCode, int resultCode) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActPrivacySelectFile.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        public String doInBackground(Intent... data) {
            if (this.resultCode == -1) {
                ArrayList<Media> imageList = data[0].getParcelableArrayListExtra("picked_media_list");
                if (imageList.size() > 0) {
                    int i = 0;
                    while (i < imageList.size()) {

                        if (!UtilApp.privateVaultFile.exists()) {
                            UtilApp.privateVaultFile.mkdir();
                        }

                        File file = new File(UtilApp.privateVaultFile, folderName);
                        if (!file.exists()) {
                            file.mkdir();
                        }

                        String oldFilePath = imageList.get(i).getPath();
                        String newFolderPath = file.getPath();

                        try {

                            File sourceFile = new File(oldFilePath);
                            File destFile = new File(newFolderPath, sourceFile.getName());

                            FileInputStream inputStream = new FileInputStream(sourceFile);
                            FileOutputStream outputStream = new FileOutputStream(destFile);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }

                            inputStream.close();
                            outputStream.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        i++;
                    }
                    return null;
                }
                return null;
            }
            return null;
        }


        @Override
        public void onPostExecute(String file_url) {
            progressDialog.dismiss();

            UtilMediaDelete.deletePendingMedia(ActPrivacySelectFile.this, this.requestCode, this.resultCode, new OnDeleteInterface() {
                @Override
                public void onDeleteComplete() {
                    EncryptFile encryptFile = EncryptFile.this;
                    if (encryptFile.requestCode != 125) {
                        onCompleteEncryption();
                    } else if (UtilMediaDelete.getPendingMediaList().size() == 0) {
                        onCompleteEncryption();
                    }
                }

                @Override
                public void onMediaDeleteSuccess(boolean isSuccess, Media media) {
                    if (isSuccess && adapterPrivacySelectAlbumsData != null) {
                        adapterPrivacySelectAlbumsData.removeSelectedMedia(media);
                    }
                }
            });
        }
    }

    public void onCompleteEncryption() {
        adapterPrivacySelectAlbumsData.invalidateSelectedCount();
        adapterPrivacySelectAlbumsData.clearSelected();
        finish();
        Toast.makeText(this, "Hide Successfully", Toast.LENGTH_SHORT).show();

        UtilApp.isAllMediaFragChange = true;
        UtilApp.isAlbumsFragChange = true;
        UtilApp.isValutActChange = true;
    }

    private void dialogFileHideToAlbum() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_file_copy_move_hide, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        TextView txtDialog = bottomSheetDialog.findViewById(R.id.txtDialog);
        TextView text_cancel = bottomSheetDialog.findViewById(R.id.text_cancel);
        TextView text_copy_move_album = bottomSheetDialog.findViewById(R.id.text_copy_move_album);
        LinearLayout createNewAlbumLy = bottomSheetDialog.findViewById(R.id.createNewAlbumLy);

        txtDialog.setText(getResources().getString(R.string.str_52));
        text_copy_move_album.setText(getResources().getString(R.string.str_53));
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

                String[] segments = path.split("/");
                folderName = segments[segments.length - 1];
            }
        }));

        text_cancel.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });

        text_copy_move_album.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            hideImage();
        });
    }

    private void dialogCreateAlbumPrivacy(BottomSheetDialog bottomSheetDialog) {
        Dialog dialogCreateAlbumPrivacy = UtilDialog.getDialog(ActPrivacySelectFile.this, R.layout.dialog_create_album);
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
            folderName = createFolderEt.getText().toString();
            hideImage();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 126 && resultCode == -1) {
            deletePendingMedia(requestCode, resultCode);
        }
    }

    public void deletePendingMedia(int requestCode, int resultCode) {
        if (adapterPrivacySelectAlbumsData != null) {
            Intent intent2 = new Intent();
            intent2.putExtra("picked_media_list", adapterPrivacySelectAlbumsData.getSelected());
            new EncryptFile(requestCode, resultCode).execute(intent2);
        }
    }

    private void idBind() {
        headerTxt = findViewById(R.id.headerTxt);
        layoutAllAlbum = findViewById(R.id.layoutAllAlbum);
        allAlbumListRv = findViewById(R.id.allAlbumListRv);
        albumDataRv = findViewById(R.id.albumDataRv);
        notFoundTxt = findViewById(R.id.notFoundTxt);
        hideDoneImg = findViewById(R.id.hideDoneImg);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (selectionMode) {
                    adapterPrivacySelectAlbumsData.clearSelected();
                    selectionMode = false;
                    hideDoneImg.setEnabled(false);
                    hideDoneImg.setAlpha(0.5f);
                } else {
                    if (albumDataRv.getVisibility() == View.VISIBLE) {
                        albumDataRv.setVisibility(View.GONE);
                        layoutAllAlbum.setVisibility(View.VISIBLE);
                        headerTxt.setText(getResources().getString(R.string.str_116));
                    } else {
                        InterstitialAds.ShowInterstitialBack(ActPrivacySelectFile.this, () -> {
                            finish();
                        });
                    }
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}
