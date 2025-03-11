package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterAlbumCopyMove;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterAlbumsFile;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyMoveAlbums;
import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.DataHelper.DBFavourite;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragMainAlbum;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceActions;
import com.gallaryapp.privacyvault.photoeditor.Interface.OnDeleteInterface;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FavoriteHelper;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilRename;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilMediaDelete;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilPrivacyVault;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class ActAlbumFiles extends ActBase implements InterfaceActions {

    private ImageView back_img;
    private TextView headerTxt;
    private ImageView albumsMediaSelectAllImg;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mainAlbumsMediaRv;
    private ProgressBar progressBar;
    private LinearLayout albumsMediaBottomLay;

    boolean isMoveToTrash = false;
    public static AdapterAlbumsFile adapterAlbumsFile;
    private Album album;
    int albumPos;
    private boolean pickMode = false;
    int albumphotoSelectedListCount;

    String selectedAlbum;
    Uri uriLocal;
    private String imageOrVideoPath;
    boolean whichButtonClick = true;
    String whereFromCome;
    EditText renameEditText;
    private boolean isHideToFolder = false;
    public AdapterAlbumCopyMove adapterAlbumCopyMove;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_album_files);

        // Adaptive_Banner
        new BannerAds(this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));

        inBinding();
        setOnBackPressed();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (intent != null) {
            if (bundle != null) {
                albumPos = bundle.getInt("albumPos");
                whereFromCome = bundle.getString("keyy");

                try {
                    if (whereFromCome.equalsIgnoreCase("MainAlbum")) {
                        album = FragMainAlbum.mainAllAlbumsAdapter.get(albumPos);
                    } else if (whereFromCome.equalsIgnoreCase("SearchAlbum")) {
                        album = ActSearchAlbum.searchAllAlbumsAdapter.get(albumPos);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pickMode = getIntent().getBooleanExtra("pick_mode", false);


            if (album != null) {
                try {
                    if (!album.getName().isEmpty()) {
                        headerTxt.setText(album.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    headerTxt.setText("Album");
                }
            } else {
                headerTxt.setText("Album");
            }
        }

        mainAlbumsMediaRv.setLayoutManager(new GridLayoutManager(this, 3));
        adapterAlbumsFile = new AdapterAlbumsFile(this, this);
        mainAlbumsMediaRv.setAdapter(adapterAlbumsFile);
        UtilApp.isAlbumsDataActChange = false;
        setAlbumData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            setAlbumData();
        });

        findViewById(R.id.albumsMediaDelete).setOnClickListener(v -> {
            whichButtonClick = false;
            dialogFileDelete();
        });

        findViewById(R.id.albumsMediaLock).setOnClickListener(v -> {
            if (!UtilPrivacyVault.isPrivacyVaultSetup(ActAlbumFiles.this)) {
                isMediaSelected();
            } else if (adapterAlbumsFile.getSelected().size() > 20) {
                Toast.makeText(ActAlbumFiles.this, "At a time maximum 20 images Move to secure vault", Toast.LENGTH_SHORT).show();
            } else if (adapterAlbumsFile.getSelected().size() > 0) {
                if (UtilPrivacyVault.getPrivacyVaultAlbums().size() == 0) {
                    isMoveToTrash = false;
                    isHideToFolder = false;
                    moveInVault();
                } else {
                    dialogFileHideToAlbum();
                }
            } else {
                Toast.makeText(ActAlbumFiles.this, "Please select at least one item to Move to secure vault", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.albumsMediaMore).setOnClickListener(v -> {
            openMoreDialog();
        });

        albumsMediaSelectAllImg.setOnClickListener(v -> {
            if (adapterAlbumsFile.getSelectedCount() == adapterAlbumsFile.getItemCount()) {
                hideBottomBar();
                albumsMediaSelectAllImg.setImageResource(R.drawable.icon_img_unselected);
            } else {
                adapterAlbumsFile.selectAll();
                albumsMediaSelectAllImg.setImageResource(R.drawable.icon_img_selected);
                headerTxt.setText(adapterAlbumsFile.getSelectedCount() + " of " + adapterAlbumsFile.getItemCount());
            }
        });

        findViewById(R.id.albumsMediaShare).setOnClickListener(v -> {
            try {
                UtilMediaDelete.shareMedia(ActAlbumFiles.this, adapterAlbumsFile.getSelected());
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        });

        findViewById(R.id.albumsMediaMoveTo).setOnClickListener(v -> {
            whichButtonClick = true;
            dialogFileCopyMoveToAlbum(false);
        });
    }

    public void addFavoriteItem() {
        for (int i = 0; i < adapterAlbumsFile.getSelected().size(); i++) {
            String filePath = adapterAlbumsFile.getSelected().get(i).getPath();

            String likeListData = FavoriteHelper.getPreferenceString(ActAlbumFiles.this, "likeList", "");
            List<String> likeList = new ArrayList<String>();
            if (!likeListData.isEmpty()) {
                likeList.addAll(Arrays.asList(likeListData.split(",")));
            }
            if (likeList.contains(filePath)) {
                /*Toast.makeText(this, "All Ready Added", Toast.LENGTH_SHORT).show();*/
            } else {
                likeList.add(filePath);
                favoriteData(filePath);
            }
            String str = String.join(",", likeList);
            FavoriteHelper.setPreferenceString(ActAlbumFiles.this, "likeList", str);
        }
        Toast.makeText(ActAlbumFiles.this, "Added Successfully", Toast.LENGTH_SHORT).show();
        hideBottomBar();
    }

    public void favoriteData(String filePath) {

        DBFavourite dbFavourite = new DBFavourite(ActAlbumFiles.this);

        boolean isContains = false;

        Cursor cursor = dbFavourite.readdata();
        while (cursor.moveToNext()) {
            String id = cursor.getString(1);
            if (id.equals(filePath)) {
                isContains = true;
                break;
            }
        }
        if (!isContains) {
            dbFavourite.insertData(filePath);
        } else {
            /*Toast.makeText(ActAlbumFiles.this, "All Ready Added", Toast.LENGTH_LONG).show();*/
        }
    }

    private void openMoreDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_file_more, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        LinearLayout dialogRenameLay = bottomSheetDialog.findViewById(R.id.dialogRenameLay);
        LinearLayout dialogCopyLay = bottomSheetDialog.findViewById(R.id.dialogCopyLay);
        LinearLayout dialogMoveLay = bottomSheetDialog.findViewById(R.id.dialogMoveLay);
        LinearLayout dialogSlideShowLay = bottomSheetDialog.findViewById(R.id.dialogSlideShowLay);
        LinearLayout dialogPrintLay = bottomSheetDialog.findViewById(R.id.dialogPrintLay);
        LinearLayout dialogFavoriteLay = bottomSheetDialog.findViewById(R.id.dialogFavoriteLay);
        LinearLayout dialogEditLay = bottomSheetDialog.findViewById(R.id.dialogEditLay);
        LinearLayout dialogWallpaperLay = bottomSheetDialog.findViewById(R.id.dialogWallpaperLay);
        LinearLayout dialogInfoLay = bottomSheetDialog.findViewById(R.id.dialogInfoLay);

        dialogMoveLay.setVisibility(View.GONE);
        dialogSlideShowLay.setVisibility(View.GONE);
        dialogPrintLay.setVisibility(View.GONE);

        dialogFavoriteLay.setVisibility(View.VISIBLE);
        dialogEditLay.setVisibility(View.VISIBLE);

        if (adapterAlbumsFile.getSelected().size() > 1) {
            dialogEditLay.setVisibility(View.GONE);
            dialogWallpaperLay.setVisibility(View.GONE);
            dialogRenameLay.setVisibility(View.GONE);
            dialogInfoLay.setVisibility(View.GONE);
        } else {

            if (FileUtils.isVideo(adapterAlbumsFile.getFirstSelected().getPath())) {
                dialogEditLay.setVisibility(View.GONE);
                dialogWallpaperLay.setVisibility(View.GONE);
            }
        }

        dialogWallpaperLay.setVisibility(View.GONE);

        dialogRenameLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            dialogFileRename();
        });

        dialogCopyLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            dialogFileCopyMoveToAlbum(true);
        });

        dialogFavoriteLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            addFavoriteItem();
        });

        dialogEditLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            clickedOnEdit();
        });

        dialogWallpaperLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            InterstitialAds.ShowInterstitial(this, () -> {
                Intent setWallInt = new Intent(ActAlbumFiles.this, ActSetWallpaper.class);
                setWallInt.putExtra("setWallpaperPath", adapterAlbumsFile.getFirstSelected().getPath());
                startActivity(setWallInt);
            });
        });

        dialogInfoLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showInfoDialog();
        });
    }

    private void dialogFileRename() {
        Dialog dialogRename = UtilDialog.getDialog(ActAlbumFiles.this, R.layout.dialog_file_rename);
        dialogRename.setCancelable(true);

        TextView renameTxt = (TextView) dialogRename.findViewById(R.id.renameTxt);
        TextView cancelTxt = (TextView) dialogRename.findViewById(R.id.cancelTxt);
        EditText renameFileEt = (EditText) dialogRename.findViewById(R.id.renameFileEt);

        renameFileEt.setText(UtilApp.getPhotoNameByPath(adapterAlbumsFile.getFirstSelected().getPath()));
        renameFileEt.setFocusable(true);

        renameTxt.setOnClickListener(v -> {
            dialogRename.dismiss();
            renameEditText = renameFileEt;
            ReName(renameFileEt);
        });

        cancelTxt.setOnClickListener(v -> {
            dialogRename.dismiss();
        });

        dialogRename.show();
    }

    public void ReName(EditText editTextNewName) {
        if (editTextNewName.length() != 0) {

            String str = editTextNewName.getText().toString();
            if (!str.isEmpty() && str.trim().length() > 0) {
                if (Build.VERSION.SDK_INT >= 30) {
                    if (UtilRename.renameFileAboveQ(ActAlbumFiles.this, adapterAlbumsFile.getFirstSelected().getPath(), str)) {
                        setAlbumData();
                        Toast.makeText(getApplicationContext(), "Rename Successfully", Toast.LENGTH_SHORT).show();
                        hideBottomBar();
                        UtilApp.isAllMediaFragChange = true;
                    }
                }
            }
        } else {
            UtilApp.showToast(getApplicationContext(), "Nothing changed");
        }
    }

    private void showInfoDialog() {

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

        String imagePath = adapterAlbumsFile.getFirstSelected().getPath();

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

    public boolean isMediaSelected() {
        if (adapterAlbumsFile != null && adapterAlbumsFile.getSelectedCount() > 0) {
            hideBottomBar();
            return true;
        }
        return false;
    }

    public void clickedOnEdit() {
        InterstitialAds.ShowInterstitial(this, () -> {
            Intent i = new Intent(this, ActImageEdit.class);
            i.putExtra("editImagePath", adapterAlbumsFile.getFirstSelected().getPath());
            startActivity(i);

            hideBottomBar();
        });
    }

    private void dialogFileDelete() {
        Dialog dialogDelete = UtilDialog.getDialog(this, R.layout.dialog_file_delete);
        dialogDelete.setCancelable(true);

        TextView messageTxt = (TextView) dialogDelete.findViewById(R.id.messageTxt);
        TextView deleteTxt = (TextView) dialogDelete.findViewById(R.id.deleteTxt);
        TextView cancelTxt = (TextView) dialogDelete.findViewById(R.id.cancelTxt);

        if (MyPreference.get_IsMoveToTrash()) {
            messageTxt.setText(getResources().getString(R.string.str_130));
            deleteTxt.setText(getResources().getString(R.string.str_129));
            cancelTxt.setText(getResources().getString(R.string.str_104));
        }

        deleteTxt.setOnClickListener(v -> {
            dialogDelete.dismiss();
            startDeleteFile();
        });

        cancelTxt.setOnClickListener(v -> {
            if (MyPreference.get_IsMoveToTrash()) {
                dialogDelete.dismiss();
                isMoveToTrash = true;
                moveInVault();
            } else {
                dialogDelete.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void inBinding() {
        back_img = (ImageView) findViewById(R.id.back_img);
        headerTxt = (TextView) findViewById(R.id.headerTxt);
        albumsMediaSelectAllImg = (ImageView) findViewById(R.id.albumsMediaSelectAllImg);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mainAlbumsMediaRv = (RecyclerView) findViewById(R.id.mainAlbumsMediaRv);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        albumsMediaBottomLay = (LinearLayout) findViewById(R.id.albumsMediaBottomLay);
    }

    public void setAlbumData() {
        loadAlbumData(this.album);
    }

    private void loadAlbumData(final Album album) {
        Activity activity = ActAlbumFiles.this;
        if (activity != null && !activity.isFinishing() && !isDestroyed()) {
            progressBar.setVisibility(View.VISIBLE);
            this.album = album;
            adapterAlbumsFile.setupFor();

            UtilApp.getMedia(activity, album).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(new Predicate() {
                @Override
                public boolean test(Object obj) {
                    return loadAlbum((Media) obj);
                }
            }).subscribe(new Consumer() {
                @Override
                public void accept(Object obj) {
                    loadAlbum1((Media) obj);
                }
            }, new Consumer() {
                @Override
                public void accept(Object obj) {

                }
            }, new Action() {
                @Override
                public void run() {
                    loadAlbum2(album);
                }
            });
        }
    }

    public boolean loadAlbum(Media media) {
        return UtilApp.getFilter().accept(media);
    }

    public void loadAlbum1(Media media) {
        progressBar.setVisibility(View.GONE);
        this.adapterAlbumsFile.add(media);
    }

    public void loadAlbum2(Album album) {
        album.setCount(getCount());
        swipeRefreshLayout.setRefreshing(false);
    }

    public int getCount() {
        return this.adapterAlbumsFile.getItemCount();
    }

    public void onMediaClick(ArrayList<Media> media, int position, ImageView imageView) {
        if (!this.pickMode) {
            viewImage(media, position, imageView);
            return;
        }
        try {
            Media m = media.get(position);
            Uri uris = ContentUris.withAppendedId(FileUtils.isImage(m.getPath()) ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI, m.getId());
            Intent res = new Intent();
            res.setData(uris);
            res.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            setResult(-1, res);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewImage(ArrayList<Media> media, int position, ImageView imageView) {
        InterstitialAds.ShowInterstitial(this, () -> {
            Intent intent = new Intent(getApplicationContext(), ActMainPager.class);
            intent.setAction("ALL_ALBUM");
            intent.putExtra("args_media", media);
            intent.putExtra("args_position", position);
            startActivityForResult(intent, 140);
        });
    }

    public void startDeleteFile() {
        UtilMediaDelete.deleteNormalFile(ActAlbumFiles.this, adapterAlbumsFile.getSelected(), new OnDeleteInterface() {
            @Override
            public void onDeleteComplete() {
                adapterAlbumsFile.invalidateSelectedCount();
            }

            @Override
            public void onMediaDeleteSuccess(boolean isSuccess, Media media) {
                if (isSuccess) {
                    adapterAlbumsFile.removeSelectedMedia(media);
                }
            }
        });
    }

    private void moveInVault() {
        if (Build.VERSION.SDK_INT >= 29) {
            UtilMediaDelete.deleteMoveToPrivacyFile(this, adapterAlbumsFile.getSelected(), new OnDeleteInterface() {
                @Override
                public void onDeleteComplete() {
                    adapterAlbumsFile.invalidateSelectedCount();
                }

                @Override
                public void onMediaDeleteSuccess(boolean isSuccess, Media media) {
                    if (isSuccess) {
                        adapterAlbumsFile.removeSelectedMedia(media);
                    }
                }
            });
        }
    }

    @Override
    public void onItemSelected(int position, ImageView imageView) {
        onMediaClick(adapterAlbumsFile.getMedia(), position, imageView);
    }

    @Override
    public void onSelectMode(boolean selectMode) {
        swipeRefreshLayout.setEnabled(!selectMode);
    }

    @Override
    public void onSelectionCountChanged(int selectionCount, int totalCount) {

        albumphotoSelectedListCount = selectionCount;

        if (selectionCount > 0) {
            showBottomBar();
        } else {
            hideBottomBar();
        }

        if (selectionCount == totalCount) {
            albumsMediaSelectAllImg.setImageResource(R.drawable.icon_img_selected);
        } else {
            albumsMediaSelectAllImg.setImageResource(R.drawable.icon_img_unselected);
        }
    }

    public class LockFileAsyncTask extends AsyncTask<Intent, String, String> {
        private ProgressDialog progressDialog;
        int requestCode;
        int resultCode;


        public LockFileAsyncTask(int requestCode, int resultCode) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActAlbumFiles.this, R.style.ProgressDialogStyle);
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

                        File file = new File(UtilApp.privateVaultFile, "Default folder");
                        if (!file.exists()) {
                            file.mkdir();
                        }

                        String oldFilePath = imageList.get(i).getPath();
                        String newFolderPath;

                        if (isHideToFolder) {
                            newFolderPath = selectedAlbum;
                        } else {
                            newFolderPath = file.getPath();
                        }

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
            }
            return null;
        }


        @Override
        public void onPostExecute(String file_url) {
            progressDialog.dismiss();

            if (this.resultCode == -1 && this.requestCode == -1) {
                onCompleteEncryption();
            } else {
                UtilMediaDelete.deletePendingMedia(ActAlbumFiles.this, this.requestCode, this.resultCode, new OnDeleteInterface() {
                    @Override
                    public void onDeleteComplete() {
                        LockFileAsyncTask lockFileAsyncTask = LockFileAsyncTask.this;
                        if (lockFileAsyncTask.requestCode != 125) {
                            onCompleteEncryption();
                        } else if (UtilMediaDelete.getPendingMediaList().size() == 0) {
                            onCompleteEncryption();
                        }
                    }

                    @Override
                    public void onMediaDeleteSuccess(boolean isSuccess, Media media) {
                        if (isSuccess && adapterAlbumsFile != null) {
                            adapterAlbumsFile.removeSelectedMedia(media);
                        }
                    }
                });
            }
        }
    }

    public class RecyclerBinFileAsyncTask extends AsyncTask<Intent, String, String> {
        private ProgressDialog progressDialog;
        int requestCode;
        int resultCode;

        public RecyclerBinFileAsyncTask(int requestCode, int resultCode) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActAlbumFiles.this, R.style.ProgressDialogStyle);
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

                        if (!UtilApp.recycleBinFile.exists()) {
                            UtilApp.recycleBinFile.mkdir();
                        }

                        String oldFilePath = imageList.get(i).getPath();
                        String newFolderPath = UtilApp.recycleBinFile.getPath();

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

            if (this.resultCode == -1 && this.requestCode == -1) {
                onCompleteEncryption();
            } else {
                UtilMediaDelete.deletePendingMedia(ActAlbumFiles.this, this.requestCode, this.resultCode, new OnDeleteInterface() {
                    @Override
                    public void onDeleteComplete() {
                        RecyclerBinFileAsyncTask recyclerBinFileAsyncTask = RecyclerBinFileAsyncTask.this;
                        if (recyclerBinFileAsyncTask.requestCode != 125) {
                            onCompleteEncryption();
                        } else if (UtilMediaDelete.getPendingMediaList().size() == 0) {
                            onCompleteEncryption();
                        }
                    }

                    @Override
                    public void onMediaDeleteSuccess(boolean isSuccess, Media media) {
                        if (isSuccess && adapterAlbumsFile != null) {
                            adapterAlbumsFile.removeSelectedMedia(media);
                        }
                    }
                });
            }
        }
    }


    private class MoveFileAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActAlbumFiles.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            for (int i = 0; i < adapterAlbumsFile.getSelected().size(); i++) {
                imageOrVideoPath = adapterAlbumsFile.getSelected().get(i).getPath();

                try {
                    File sourceFile = new File(imageOrVideoPath);
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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Uri sourceUri = FileProvider.getUriForFile(ActAlbumFiles.this, getPackageName() + ".provider", sourceFile);
                        ContentResolver contentResolver = getContentResolver();
                        try {
                            contentResolver.delete(sourceUri, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (destFile.exists() && destFile.length() == sourceFile.length()) {
                            if (sourceFile.delete()) {

                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            ArrayList<Media> mediaArrayList = adapterAlbumsFile.getSelected();

            for (int i = 0; i < mediaArrayList.size(); i++) {
                adapterAlbumsFile.removeSelectedMedia(mediaArrayList.get(i));
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

            MediaScannerConnection.scanFile(ActAlbumFiles.this, new String[]{new File(selectedAlbum).getAbsolutePath()}, null, null);

            if (result) {
                Toast.makeText(ActAlbumFiles.this, "Move Successfully", Toast.LENGTH_SHORT).show();
                hideBottomBar();

                UtilApp.isAlbumsFragChange = true;
                UtilApp.isAllMediaFragChange = true;

                if (adapterAlbumsFile.getItemCount() == 0) {
                    finish();
                }

            } else {
                Toast.makeText(ActAlbumFiles.this, "Failed to move files", Toast.LENGTH_SHORT).show();
                hideBottomBar();
            }
        }
    }

    private class CopyFileAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActAlbumFiles.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                for (int i = 0; i < adapterAlbumsFile.getSelected().size(); i++) {
                    imageOrVideoPath = adapterAlbumsFile.getSelected().get(i).getPath();

                    try {
                        File sourceFile = new File(imageOrVideoPath);
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

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
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

            MediaScannerConnection.scanFile(ActAlbumFiles.this, new String[]{new File(selectedAlbum).getAbsolutePath()}, null, null);

            if (result) {
                Toast.makeText(ActAlbumFiles.this, "File copy successfully", Toast.LENGTH_SHORT).show();
                hideBottomBar();

                UtilApp.isAlbumsFragChange = true;
                UtilApp.isAllMediaFragChange = true;

            } else {
                Toast.makeText(ActAlbumFiles.this, "Error copying file to album", Toast.LENGTH_SHORT).show();
                hideBottomBar();
            }
        }
    }

    public void onCompleteEncryption() {
        adapterAlbumsFile.invalidateSelectedCount();
        if (isMoveToTrash) {
            Toast.makeText(this, "Delete Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Hide Successfully", Toast.LENGTH_SHORT).show();
        }
        if (adapterAlbumsFile.getItemCount() == 0) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilApp.isAlbumsDataActChange) {
            UtilApp.isAlbumsDataActChange = false;
            setAlbumData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 140 && resultCode == -1) {
            if (adapterAlbumsFile != null) {
                ArrayList<Media> deletedPosList = data.getParcelableArrayListExtra("deletedPosList");
                for (int i = 0; i < deletedPosList.size(); i++) {
                    if (FileUtils.isImage(deletedPosList.get(i).getPath())) {
                        adapterAlbumsFile.removeSelectedMedia(deletedPosList.get(i));
                    }
                    if (FileUtils.isVideo(deletedPosList.get(i).getPath())) {
                        adapterAlbumsFile.removeSelectedMedia(deletedPosList.get(i));
                    }
                }

                new Handler().post(() -> {
                    if (adapterAlbumsFile.getItemCount() == 0) {
                        ActAlbumFiles.this.finish();
                    }
                });
            }
        }

        if (requestCode == 2 && resultCode == -1) {
            ReName(renameEditText);
        }

        if (requestCode == 124 && resultCode == -1) {
            if (whichButtonClick) {
                MoveFileAsyncTask task = new MoveFileAsyncTask();
                task.execute();
            }
        }

        if (resultCode == -1) {
            deletePendingMedia(requestCode, resultCode);
        }
    }

    public void deletePendingMedia(int requestCode, int resultCode) {
        if (this.adapterAlbumsFile != null) {
            if (requestCode == 124) {

                UtilMediaDelete.deletePendingMedia(this, requestCode, resultCode, new OnDeleteInterface() {
                    @Override
                    public void onDeleteComplete() {
                        if (adapterAlbumsFile != null) {
                            adapterAlbumsFile.invalidateSelectedCount();
                        }
                    }

                    @Override
                    public void onMediaDeleteSuccess(boolean isSuccess, Media media) {
                        if (isSuccess && adapterAlbumsFile != null) {
                            hideBottomBar();
                            adapterAlbumsFile.removeSelectedMedia(media);
                            if (adapterAlbumsFile.getItemCount() == 0) {
                                finish();
                            }

                            UtilApp.isAlbumsFragChange = true;
                            UtilApp.isAllMediaFragChange = true;
                        }
                    }
                });
            } else if (requestCode == 126) {
                Intent intent2 = new Intent();
                intent2.putExtra("picked_media_list", this.adapterAlbumsFile.getSelected());

                if (isMoveToTrash) {
                    new RecyclerBinFileAsyncTask(requestCode, resultCode).execute(intent2);
                } else {
                    new LockFileAsyncTask(requestCode, resultCode).execute(intent2);
                }

                hideBottomBar();

                UtilApp.isAlbumsFragChange = true;
                UtilApp.isAllMediaFragChange = true;
            }
        }
    }

    public void hideBottomBar() {
        back_img.setImageResource(R.drawable.header_back_img);
        headerTxt.setText(album.getName());
        albumsMediaSelectAllImg.setVisibility(View.GONE);

        albumphotoSelectedListCount = 0;
        adapterAlbumsFile.clearSelected();

        float value = albumsMediaBottomLay.getHeight();
        albumsMediaBottomLay.animate().translationY(value).setInterpolator(new DecelerateInterpolator()).setDuration(400L).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                albumsMediaBottomLay.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        }).start();
    }

    public void showBottomBar() {
        back_img.setImageResource(R.drawable.header_close_img);
        headerTxt.setText(adapterAlbumsFile.getSelectedCount() + " of " + adapterAlbumsFile.getItemCount());
        albumsMediaSelectAllImg.setVisibility(View.VISIBLE);

        albumsMediaBottomLay.animate().translationY(0.0f).setInterpolator(new AccelerateInterpolator()).setDuration(200L).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                albumsMediaBottomLay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        }).start();
    }

    private void dialogFileCopyMoveToAlbum(boolean isCopy) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_file_copy_move_hide, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        TextView txtDialog = bottomSheetDialog.findViewById(R.id.txtDialog);
        TextView text_cancel = bottomSheetDialog.findViewById(R.id.text_cancel);
        TextView text_copy_move_album = bottomSheetDialog.findViewById(R.id.text_copy_move_album);
        LinearLayout createNewAlbumLy = bottomSheetDialog.findViewById(R.id.createNewAlbumLy);
        ProgressBar d_ProgressBar = bottomSheetDialog.findViewById(R.id.progressBar);

        d_ProgressBar.setVisibility(View.VISIBLE);
        if (isCopy) {
            txtDialog.setText(getResources().getString(R.string.str_139));
            text_copy_move_album.setText(getResources().getString(R.string.str_141));
        } else {
            txtDialog.setText(getResources().getString(R.string.str_140));
            text_copy_move_album.setText(getResources().getString(R.string.str_142));
        }

        text_copy_move_album.setEnabled(false);
        text_copy_move_album.setAlpha(0.5f);

        createNewAlbumLy.setOnClickListener(view -> {
            dialogCreateAlbum(bottomSheetDialog, isCopy);
        });

        RecyclerView allfolderRV = bottomSheetDialog.findViewById(R.id.albumCopyMoveRV);

        UtilApp.getMainAllAlbum(ActAlbumFiles.this, MyPreference.get_AlbumsAS_SortBy(), MyPreference.get_AlbumsAS_IsAscending()).subscribeOn(Schedulers.io()).map(new Function() {
            @Override
            public Object apply(Object obj) {
                return (Album) obj;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() {
            @Override
            public void accept(Object obj) {
                adapterAlbumCopyMove.add((Album) obj);
            }
        }, new Consumer() {
            @Override
            public void accept(Object obj) {

            }
        }, new Action() {
            @Override
            public void run() {
                d_ProgressBar.setVisibility(View.GONE);
            }
        });

        allfolderRV.setLayoutManager(new LinearLayoutManager(ActAlbumFiles.this));
        adapterAlbumCopyMove = new AdapterAlbumCopyMove(ActAlbumFiles.this, new AdapterAlbumCopyMove.Click() {
            @Override
            public void ClickToActivity(String selectedPath) {

                text_copy_move_album.setEnabled(true);
                text_copy_move_album.setAlpha(1f);

                selectedAlbum = selectedPath;
            }
        });

        allfolderRV.setAdapter(adapterAlbumCopyMove);

        text_cancel.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });

        text_copy_move_album.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            imageOrVideoPath = adapterAlbumsFile.getFirstSelected().getPath();

            if (isCopy) {
                copyFileToAlbum(imageOrVideoPath, selectedAlbum);
            } else {
                moveFileToAlbum(imageOrVideoPath, selectedAlbum);
            }
        });
    }

    private void moveFileToAlbum(String filePath, String albumPathFull) {

        File selectedFile = new File(filePath);
        String selectedFileName = selectedFile.getName();

        if (isFileAlreadyExists(selectedFileName, albumPathFull)) {
            Toast.makeText(this, "File already exists in album", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ArrayList<Media> mediaArrayList = adapterAlbumsFile.getSelected();

            ArrayList urisToModify = new ArrayList();
            Iterator<Media> it = mediaArrayList.iterator();
            while (it.hasNext()) {
                Media media = it.next();
                Uri uri = null;
                if (media.getId() > 0) {
                    uri = ContentUris.withAppendedId(FileUtils.isImage(media.getPath()) ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI, media.getId());
                } else {
                    if (Build.VERSION.SDK_INT >= 24) {
                        uriLocal = FileProvider.getUriForFile(this, getPackageName() + ".provider", media.getFile());
                    } else {
                        uriLocal = Uri.fromFile(media.getFile());
                    }
                    if (uriLocal != null) {
                        uri = uriLocal;
                    }
                }
                if (uri != null) {
                    urisToModify.add(uri);
                }
            }

            PendingIntent editPendingIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                editPendingIntent = MediaStore.createWriteRequest(getContentResolver(), urisToModify);
            }
            try {
                startIntentSenderForResult(editPendingIntent.getIntentSender(), androidx.appcompat.R.styleable.AppCompatTheme_windowMinWidthMajor, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyFileToAlbum(String filePath, String albumPathFull) {

        File selectedFile = new File(filePath);
        String selectedFileName = selectedFile.getName();

        if (isFileAlreadyExists(selectedFileName, albumPathFull)) {
            Toast.makeText(this, "File already exists in album", Toast.LENGTH_SHORT).show();
            hideBottomBar();
            return;
        }

        CopyFileAsyncTask task = new CopyFileAsyncTask();
        task.execute();
    }

    private boolean isFileAlreadyExists(String fileName, String albumPath) {
        File destFile = new File(albumPath, fileName);
        return destFile.exists();
    }

    private void dialogCreateAlbum(BottomSheetDialog bottomSheetDialog, Boolean isCopy) {
        Dialog dialogCreateAlbum = UtilDialog.getDialog(ActAlbumFiles.this, R.layout.dialog_create_album);
        dialogCreateAlbum.show();

        EditText createFolderEt = dialogCreateAlbum.findViewById(R.id.createFolderEt);
        TextView cancelTxt = dialogCreateAlbum.findViewById(R.id.cancelTxt);
        TextView createTxt = dialogCreateAlbum.findViewById(R.id.createTxt);

        cancelTxt.setOnClickListener(view -> {
            dialogCreateAlbum.dismiss();
        });

        createTxt.setOnClickListener(view -> {
            dialogCreateAlbum.dismiss();
            bottomSheetDialog.dismiss();

            File trashDirectory = new File(UtilApp.createNewAlbumFile, createFolderEt.getText().toString());

            if (!trashDirectory.exists()) {
                if (trashDirectory.mkdir()) {

                    imageOrVideoPath = adapterAlbumsFile.getFirstSelected().getPath();
                    selectedAlbum = trashDirectory.getAbsolutePath();
                    if (isCopy) {
                        copyFileToAlbum(imageOrVideoPath, selectedAlbum);
                    } else {
                        moveFileToAlbum(imageOrVideoPath, selectedAlbum);
                    }
                }
            } else {
                Toast.makeText(ActAlbumFiles.this, "Album already exists", Toast.LENGTH_SHORT).show();
            }
        });
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

                selectedAlbum = path;
            }
        }));

        text_cancel.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });

        text_copy_move_album.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();

            isMoveToTrash = false;
            isHideToFolder = true;
            moveInVault();
        });
    }

    private void dialogCreateAlbumPrivacy(BottomSheetDialog bottomSheetDialog) {
        Dialog dialogCreateAlbumPrivacy = UtilDialog.getDialog(ActAlbumFiles.this, R.layout.dialog_create_album);
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
            isMoveToTrash = false;
            isHideToFolder = true;
            moveInVault();
        });
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (albumphotoSelectedListCount == 0) {
                    InterstitialAds.ShowInterstitialBack(ActAlbumFiles.this, () -> {
                        finish();
                    });
                } else {
                    hideBottomBar();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}