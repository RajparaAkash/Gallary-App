package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.animation.Animator;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterAlbumCopyMove;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterMainPager;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyMoveAlbums;
import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.DataHelper.DBFavourite;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragBase;
import com.gallaryapp.privacyvault.photoeditor.Fragment.FragFileImage;
import com.gallaryapp.privacyvault.photoeditor.Interface.OnDeleteInterface;
import com.gallaryapp.privacyvault.photoeditor.Model.Album;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FavoriteHelper;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.FileUtils;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilMediaDelete;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilPrivacyVault;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilRename;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ActMainPager extends ActBase implements FragBase.MediaTapListener {

    LinearLayout toolbarLay;
    TextView headerTxt;
    ImageView rotateMediaImg;
    ImageView favoriteMediaImg;
    ViewPager mainMediaViewPager;
    LinearLayout bottomOptionLay;
    LinearLayout bottomEditLay;
    LinearLayout bottomMoreLay;

    EditText renameEditText;
    private boolean fullScreenMode;
    private AdapterMainPager adapter;
    private ArrayList<Media> media;
    private int position;

    Handler handler = new Handler();
    private boolean isSlideShowOn = false;
    private boolean isHideToFolder = false;
    boolean isMoveToTrash = false;
    private String imageOrVideoPath;
    String selectedAlbum;
    Uri uriLocal;

    ArrayList<Media> removedPosList = new ArrayList<>();
    Runnable slideShowRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                try {
                    ViewPager mViewPager = mainMediaViewPager;
                    mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1) % media.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                handler.postDelayed(this, 4000L);
            }
        }
    };

    private OnBackPressedCallback backPressed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main_pager);
        this.removedPosList = new ArrayList<>();

        // Adaptive_Banner
        new BannerAds(this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));

        idBind();
        setOnBackPressed();

        String action = getIntent().getAction();
        if (action != null) {
            loadAlbum(getIntent(), action);
        }

        this.adapter = new AdapterMainPager(getSupportFragmentManager(), this.media);
        initUi();
        prepareSharedElementTransition();

        bottomMoreLay.setOnClickListener(v -> openMoreDialog());

        bottomEditLay.setOnClickListener(v -> {
            clickedOnEdit();
        });

        findViewById(R.id.bottomShareLay).setOnClickListener(v -> {
            clickedOnShare();
        });

        findViewById(R.id.bottomDeleteLay).setOnClickListener(v -> {
            clickedOnDelete();
        });

        findViewById(R.id.bottomLockLay).setOnClickListener(v -> {
            clickedOnMoveInVault();
        });

        rotateMediaImg.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ActMainPager.this, v);
            popup.getMenuInflater().inflate(R.menu.menu_rotate_image, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.e_rotate_180:
                            rotateImage(180);
                            return true;
                        case R.id.e_rotate_left_90:
                            rotateImage(-90);
                            return true;
                        case R.id.e_rotate_right_90:
                            rotateImage(90);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        });

        favoriteMediaImg.setOnClickListener(v -> {
            if (checkFavoriteItem(getCurrentMedia().getPath())) {
                favoriteMediaImg.setImageResource(R.drawable.icon_heart_unfill);
            } else {
                favoriteMediaImg.setImageResource(R.drawable.icon_heart_fill);
            }
            addToFavorite();
        });
    }

    public void addToFavorite() {
        String likeListData = FavoriteHelper.getPreferenceString(ActMainPager.this, "likeList", "");
        List<String> likeList = new ArrayList<String>();
        if (!likeListData.isEmpty()) {
            likeList.addAll(Arrays.asList(likeListData.split(",")));
        }
        if (likeList.contains(getCurrentMedia().getPath())) {
            likeList.remove(getCurrentMedia().getPath());
            DBFavourite dbFavourite = new DBFavourite(ActMainPager.this);
            dbFavourite.deleteData(getCurrentMedia().getPath());
        } else {
            likeList.add(getCurrentMedia().getPath());
            favoriteSet();
        }
        String str = String.join(",", likeList);
        FavoriteHelper.setPreferenceString(ActMainPager.this, "likeList", str);
    }

    public void favoriteSet() {
        String filePath = getCurrentMedia().getPath();
        DBFavourite dbFavourite = new DBFavourite(ActMainPager.this);
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
            Toast.makeText(ActMainPager.this, "Add To Favorite", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ActMainPager.this, "All Ready Added", Toast.LENGTH_LONG).show();
        }
    }

    private void idBind() {
        toolbarLay = findViewById(R.id.toolbarLay);
        headerTxt = findViewById(R.id.headerTxt);
        rotateMediaImg = findViewById(R.id.rotateMediaImg);
        favoriteMediaImg = findViewById(R.id.favoriteMediaImg);
        mainMediaViewPager = findViewById(R.id.mainMediaViewPager);
        bottomOptionLay = findViewById(R.id.bottomOptionLay);
        bottomEditLay = findViewById(R.id.bottomEditLay);
        bottomMoreLay = findViewById(R.id.bottomMoreLay);
    }

    private void prepareSharedElementTransition() {
        TransitionSet transitionSet = new TransitionSet();
        Transition bound = new ChangeBounds();
        transitionSet.addTransition(bound);
        Transition changeImageTransform = new ChangeImageTransform();
        transitionSet.addTransition(changeImageTransform);
        transitionSet.setDuration(375L);
        getWindow().setSharedElementEnterTransition(transitionSet);
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                PagerAdapter adapter = mainMediaViewPager.getAdapter();
                Fragment currentFragment = (Fragment) adapter.instantiateItem(mainMediaViewPager, position);
                View view = currentFragment.getView();
                if (view == null) {
                    return;
                }
                sharedElements.put(names.get(0), view.findViewById(R.id.imageView));
            }
        });
    }

    private void loadAlbum(Intent intent, String action) {
        this.position = intent.getIntExtra("args_position", 0);
        if (action.equals("ALL_ALBUM") || action.equals("SEARCHED")) {
            this.media = intent.getParcelableArrayListExtra("args_media");
        }
        if (action.equals("ALL_MEDIA")) {
            this.media = UtilApp.mediaArrayList;
        }
        if (action.equals("ALL_SEARCH")) {
            this.media = UtilApp.searchArrayList;
        }
    }

    private void initUi() {

        updatePageTitle(this.position);
        mainMediaViewPager.setAdapter(this.adapter);
        mainMediaViewPager.setCurrentItem(this.position);
        mainMediaViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ActMainPager.this.position = position;
                ActMainPager.this.updatePageTitle(position);
                ActMainPager.this.invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation() == Surface.ROTATION_90) {
            Configuration configuration = new Configuration();
            configuration.orientation = Configuration.ORIENTATION_LANDSCAPE;
            onConfigurationChanged(configuration);
        }
    }

    @Override
    public void onViewTapped() {
        toggleSystemUI();
    }

    boolean checkFavoriteItem(String path) {
        String likeListData = FavoriteHelper.getPreferenceString(ActMainPager.this, "likeList", "");
        if (!likeListData.isEmpty()) {
            List<String> likeList = Arrays.asList(likeListData.split(","));
            if (likeList.contains(path)) {
                return true;
            }
        }
        return false;
    }

    public void updatePageTitle(int position) {

        if (this.media.size() == 0) {
            displayAlbums();
            return;
        }

        Media currentMedia = getCurrentMedia();
        if (currentMedia != null && currentMedia.getPath() != null) {
            if (checkFavoriteItem(currentMedia.getPath())) {
                favoriteMediaImg.setImageResource(R.drawable.icon_heart_fill);
            } else {
                favoriteMediaImg.setImageResource(R.drawable.icon_heart_unfill);
            }

            // Ensure that currentMedia.getFile() is not null before accessing getName()
            if (currentMedia.getFile() != null) {
                headerTxt.setText(currentMedia.getFile().getName());
            } else {
                headerTxt.setText("Unnamed File"); // Provide a default text if the file is null
            }
        } else {
            headerTxt.setText("Media not available");
        }

        bottomOptionLay.setVisibility(this.fullScreenMode ? View.GONE : View.VISIBLE);

        if (position >= 0 && this.media.size() > position && FileUtils.isImage(this.media.get(position).getPath())) {
            bottomEditLay.setVisibility(View.VISIBLE);
            rotateMediaImg.setVisibility(View.VISIBLE);
        } else {
            bottomEditLay.setVisibility(View.GONE);
            rotateMediaImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(getApplicationContext()).clearMemory();
        Glide.get(getApplicationContext()).trimMemory(80);
        System.gc();
    }

    private void displayAlbums() {
        backPressed.handleOnBackPressed();
    }

    private void rotateImage(int rotationDegree) {
        Fragment mediaFragment = this.adapter.getRegisteredFragment(this.position);
        if (!(mediaFragment instanceof FragFileImage)) {
            throw new RuntimeException("Trying to rotate a wrong media type!");
        }
        try {
            ((FragFileImage) mediaFragment).rotatePicture(rotationDegree);
        } catch (Exception e) {
        }
    }

    public void clickedOnEdit() {
        if (getCurrentMedia() != null && getCurrentMedia().getFile() != null && getCurrentMedia().getFile().exists()) {
            InterstitialAds.ShowInterstitial(this, () -> {
                Intent i = new Intent(this, ActImageEdit.class);
                i.putExtra("editImagePath", getCurrentMedia().getPath());
                startActivity(i);
            });
        }
    }

    public void clickedOnShare() {
        doShareTask();
    }

    public void clickedOnDelete() {
        dialogFileDelete();
    }

    public void clickedOnMoveInVault() {
        if (!UtilPrivacyVault.isPrivacyVaultSetup(this)) {

        } else {
            if (UtilPrivacyVault.getPrivacyVaultAlbums().size() == 0) {
                isMoveToTrash = false;
                isHideToFolder = false;
                moveInVault();
            } else {
                dialogFileHideToAlbum();
            }
        }
    }

    private void moveInVault() {
        if (getCurrentMedia() != null && getCurrentMedia().getFile() != null && getCurrentMedia().getFile().exists()) {
            ArrayList<Media> mediaArrayList = new ArrayList<>();
            mediaArrayList.add(getCurrentMedia());
            if (Build.VERSION.SDK_INT >= 29) {
                UtilMediaDelete.deleteMoveToPrivacyFile(this, mediaArrayList, new OnDeleteInterface() {
                    @Override
                    public void onDeleteComplete() {
                        adapter.notifyDataSetChanged();
                        updatePageTitle(mainMediaViewPager.getCurrentItem());
                    }

                    @Override
                    public void onMediaDeleteSuccess(boolean isSuccess, Media mediaDeleted) {
                        doSingleDeleteListen(isSuccess, mediaDeleted);
                    }
                });
            }
        }
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

    public void startDeleteFile() {
        if (getCurrentMedia() != null && getCurrentMedia().getFile() != null && getCurrentMedia().getFile().exists()) {
            ArrayList<Media> mediaArrayList = new ArrayList<>();
            mediaArrayList.add(getCurrentMedia());
            UtilMediaDelete.deleteNormalFile(this, mediaArrayList, new OnDeleteInterface() {
                @Override
                public void onDeleteComplete() {
                    adapter.notifyDataSetChanged();
                    updatePageTitle(mainMediaViewPager.getCurrentItem());
                }

                @Override
                public void onMediaDeleteSuccess(boolean isSuccess, Media mediaDeleted) {
                    doSingleDeleteListen(isSuccess, mediaDeleted);
                }
            });
        }
    }

    private void doShareTask() {
        if (getCurrentMedia() != null && getCurrentMedia().getFile() != null && getCurrentMedia().getFile().exists()) {
            try {
                ArrayList<Media> arrList = new ArrayList<>();
                arrList.add(getCurrentMedia());
                UtilMediaDelete.shareMedia(this, arrList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Media getCurrentMedia() {
        int size = this.media.size();
        int i = this.position;
        if (size <= i || i == -1) {
            return null;
        }
        return this.media.get(i);
    }

    private void dialogFileRename() {
        Dialog dialogRename = UtilDialog.getDialog(ActMainPager.this, R.layout.dialog_file_rename);
        dialogRename.setCancelable(true);

        TextView renameTxt = (TextView) dialogRename.findViewById(R.id.renameTxt);
        TextView cancelTxt = (TextView) dialogRename.findViewById(R.id.cancelTxt);
        EditText renameFileEt = (EditText) dialogRename.findViewById(R.id.renameFileEt);

        renameFileEt.setText(UtilApp.getPhotoNameByPath(getCurrentMedia().getPath()));
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
            Media currentMedia = getCurrentMedia();

            String str = editTextNewName.getText().toString();

            String name = new File(getCurrentMedia().getPath()).getName();
            String substring = name.substring(name.lastIndexOf(".") + 1);

            if (!str.isEmpty() && str.trim().length() > 0) {
                String parent = new File(getCurrentMedia().getPath()).getParent();
                File file = new File(parent, name);
                File file2 = new File(parent, str + "." + substring);
                if (Build.VERSION.SDK_INT >= 30) {
                    if (UtilRename.renameFileAboveQ(ActMainPager.this, getCurrentMedia().getPath(), str)) {
                        currentMedia.setPath(file2.getAbsolutePath());
                        Toast.makeText(getApplicationContext(), "Rename Successfully", Toast.LENGTH_SHORT).show();

                        if (getCurrentMedia().getFile() != null) {
                            headerTxt.setText(getCurrentMedia().getFile().getName());
                        }

                        UtilApp.isAllMediaFragChange = true;
                        UtilApp.isAlbumsDataActChange = true;
                        UtilApp.isMediaTypeActChange = true;
                    }
                }
            }
        } else {
            UtilApp.showToast(getApplicationContext(), "Nothing changed");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == 124 && resultCode == -1) {
            if (selectedAlbum != null) {
                moveFileResult(imageOrVideoPath, selectedAlbum);
            }
        }

        if (requestCode == 2 && resultCode == -1) {
            ReName(renameEditText);
        }

        if ((requestCode == 123 || requestCode == 124 || requestCode == 125 || requestCode == 126) && resultCode == -1) {
            UtilApp.isAlbumsFragChange = true;
            UtilApp.isAllMediaFragChange = true;
        }

        if (resultCode == -1) {
            deletePendingMedia(requestCode, resultCode);
        }
    }

    public void deletePendingMedia(int requestCode, int resultCode) {
        if (requestCode == 124) {
            UtilMediaDelete.deletePendingMedia(this, requestCode, resultCode, new OnDeleteInterface() {
                @Override
                public void onDeleteComplete() {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                        updatePageTitle(mainMediaViewPager.getCurrentItem());
                    }
                }

                @Override
                public void onMediaDeleteSuccess(boolean isSuccess, Media mediaDeleted) {
                    doSingleDeleteListen(isSuccess, mediaDeleted);
                }
            });
        } else if (requestCode == 126) {
            ArrayList<Media> mediaArrayList = new ArrayList<>();
            mediaArrayList.add(getCurrentMedia());
            Intent intent2 = new Intent();
            intent2.putExtra("picked_media_list", mediaArrayList);

            if (isMoveToTrash) {
                new MoveInTrashFile(requestCode, resultCode).execute(intent2);
            } else {
                new EncryptFile(requestCode, resultCode).execute(intent2);
            }
        }
    }

    public void toggleSystemUI() {
        if (!this.fullScreenMode) {
            hideSystemUI();
        } else {
            showSystemUI();
        }
    }

    public void hideSystemUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbarLay.animate().translationY(-toolbarLay.getHeight()).setInterpolator(new AccelerateInterpolator())
                        .setDuration(200L).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                toolbarLay.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationStart(Animator animation, boolean isReverse) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        }).start();

                float value = bottomOptionLay.getHeight();

                bottomOptionLay.animate().translationY(value).setInterpolator(new DecelerateInterpolator())
                        .setDuration(400L).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                bottomOptionLay.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {
                            }
                        }).start();
                fullScreenMode = true;
            }
        });
    }

    public void showSystemUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbarLay.animate().translationY(0.0f).setInterpolator(new DecelerateInterpolator()).setDuration(240L).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        toolbarLay.setVisibility(View.VISIBLE);
                        bottomOptionLay.setVisibility(View.VISIBLE);
                        if (position >= 0 && media.size() > position && getCurrentMedia() != null && FileUtils.isImage(getCurrentMedia().getPath())) {
                            bottomEditLay.setVisibility(View.VISIBLE);
                            rotateMediaImg.setVisibility(View.VISIBLE);
                            return;
                        }
                        bottomEditLay.setVisibility(View.GONE);
                        rotateMediaImg.setVisibility(View.GONE);
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

                bottomOptionLay.animate().translationY(0.0f).setInterpolator(new AccelerateInterpolator()).setDuration(200L).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        bottomOptionLay.setVisibility(View.VISIBLE);
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
                fullScreenMode = false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.handler.removeCallbacks(this.slideShowRunnable);
        this.handler = null;
    }

    public void onCompleteEncryption() {
        this.adapter.notifyDataSetChanged();
        updatePageTitle(mainMediaViewPager.getCurrentItem());

        if (isMoveToTrash) {
            Toast.makeText(this, "Delete Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Hide Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void doSingleDeleteListen(boolean isSuccess, Media mediaDeleted) {
        if (isSuccess && this.adapter != null) {
            this.removedPosList.add(mediaDeleted);
            this.media.remove(mediaDeleted);
            if (this.media.size() == 0) {
                displayAlbums();
            }
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
            progressDialog = new ProgressDialog(ActMainPager.this, R.style.ProgressDialogStyle);
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
                            /*Toast.makeText(MediaOpenActivity.this, "Error moveing file to album", Toast.LENGTH_SHORT).show();*/
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

            int i = this.resultCode;
            if (i == -1 && this.requestCode == -1) {
                onCompleteEncryption();
            } else {
                UtilMediaDelete.deletePendingMedia(ActMainPager.this, this.requestCode, i, new OnDeleteInterface() {
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
                    public void onMediaDeleteSuccess(boolean isSuccess, Media mediaDeleted) {
                        doSingleDeleteListen(isSuccess, mediaDeleted);
                    }
                });
            }
        }
    }

    public class MoveInTrashFile extends AsyncTask<Intent, String, String> {
        private ProgressDialog progressDialog;
        int requestCode;
        int resultCode;

        public MoveInTrashFile(int requestCode, int resultCode) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActMainPager.this, R.style.ProgressDialogStyle);
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
                            /*Toast.makeText(MediaOpenActivity.this, "Error moveing file to album", Toast.LENGTH_SHORT).show();*/
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

            int i = this.resultCode;
            if (i == -1 && this.requestCode == -1) {
                onCompleteEncryption();
            } else {
                UtilMediaDelete.deletePendingMedia(ActMainPager.this, this.requestCode, i, new OnDeleteInterface() {
                    @Override
                    public void onDeleteComplete() {
                        MoveInTrashFile encryptFile = MoveInTrashFile.this;
                        if (encryptFile.requestCode != 125) {
                            onCompleteEncryption();
                        } else if (UtilMediaDelete.getPendingMediaList().size() == 0) {
                            onCompleteEncryption();
                        }
                    }

                    @Override
                    public void onMediaDeleteSuccess(boolean isSuccess, Media mediaDeleted) {
                        doSingleDeleteListen(isSuccess, mediaDeleted);
                    }
                });
            }
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
        LinearLayout dialogEditLay = bottomSheetDialog.findViewById(R.id.dialogEditLay);
        LinearLayout dialogWallpaperLay = bottomSheetDialog.findViewById(R.id.dialogWallpaperLay);
        LinearLayout dialogInfoLay = bottomSheetDialog.findViewById(R.id.dialogInfoLay);

        dialogEditLay.setVisibility(View.GONE);

        if (!FileUtils.isImage(getCurrentMedia().getPath())) {
            dialogPrintLay.setVisibility(View.GONE);
            dialogWallpaperLay.setVisibility(View.GONE);
        }

        dialogWallpaperLay.setVisibility(View.GONE);

        dialogRenameLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            dialogFileRename();
        });

        dialogCopyLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            imageOrVideoPath = getCurrentMedia().getPath();
            dialogFileCopyMoveToAlbum(true);
        });

        dialogMoveLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            imageOrVideoPath = getCurrentMedia().getPath();
            dialogFileCopyMoveToAlbum(false);
        });

        dialogSlideShowLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            boolean z = !isSlideShowOn;
            isSlideShowOn = z;
            if (z) {
                handler.postDelayed(slideShowRunnable, 4000L);
                hideSystemUI();
            } else {
                handler.removeCallbacks(slideShowRunnable);
            }
            supportInvalidateOptionsMenu();
        });

        dialogPrintLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            if (getCurrentMedia() != null && getCurrentMedia().getFile() != null && getCurrentMedia().getFile().exists()) {
                PrintHelper photoPrinter = new PrintHelper(ActMainPager.this);
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                try {
                    InputStream in2 = getContentResolver().openInputStream(getCurrentMedia().getUri());
                    Bitmap bitmap = BitmapFactory.decodeStream(in2);
                    photoPrinter.printBitmap(String.format("print_%s", getCurrentMedia().getDisplayPath()), bitmap);
                    if (in2 != null) {
                        in2.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Unable to print the picture", Toast.LENGTH_SHORT).show();
            }
        });

        dialogWallpaperLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            InterstitialAds.ShowInterstitial(this, () -> {
                Intent setWallInt = new Intent(ActMainPager.this, ActSetWallpaper.class);
                setWallInt.putExtra("setWallpaperPath", getCurrentMedia().getFile().getPath());
                startActivity(setWallInt);
            });
        });

        dialogInfoLay.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showInfoDialog();
        });
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

        String imagePath = getCurrentMedia().getPath();

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

        tvOk.setOnClickListener(v -> bottomSheetDialog.dismiss());
    }

    public AdapterAlbumCopyMove adapterAlbumCopyMove;

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

        UtilApp.getMainAllAlbum(ActMainPager.this, MyPreference.get_AlbumsAS_SortBy(), MyPreference.get_AlbumsAS_IsAscending()).subscribeOn(Schedulers.io()).map(new Function() {
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

        allfolderRV.setLayoutManager(new LinearLayoutManager(ActMainPager.this));
        adapterAlbumCopyMove = new AdapterAlbumCopyMove(ActMainPager.this, new AdapterAlbumCopyMove.Click() {
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
            ArrayList<Media> mediaArrayList = new ArrayList<>();
            mediaArrayList.add(getCurrentMedia());

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

    private void moveFileResult(String filePath, String albumPathFull) {

        try {
            File sourceFile = new File(filePath);
            File destFile = new File(albumPathFull, sourceFile.getName());

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
                Uri sourceUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", sourceFile);
                ContentResolver contentResolver = getContentResolver();
                try {
                    contentResolver.delete(sourceUri, null, null);
                    doSingleDeleteListen(true, getCurrentMedia());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (destFile.exists() && destFile.length() == sourceFile.length()) {
                    if (sourceFile.delete()) {
                        doSingleDeleteListen(true, getCurrentMedia());
                    }
                }
            }

            MediaScannerConnection.scanFile(ActMainPager.this, new String[]{new File(albumPathFull).getAbsolutePath()}, null, null);

        } catch (Exception e) {
            e.printStackTrace();
            /*Toast.makeText(this, "Error moveing file to album", Toast.LENGTH_SHORT).show();*/
        }
    }

    private void copyFileToAlbum(String filePath, String albumPathFull) {

        File selectedFile = new File(filePath);
        String selectedFileName = selectedFile.getName();

        if (isFileAlreadyExists(selectedFileName, albumPathFull)) {
            Toast.makeText(this, "File already exists in album", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File sourceFile = new File(filePath);
            File destFile = new File(albumPathFull, sourceFile.getName());

            FileInputStream inputStream = new FileInputStream(sourceFile);
            FileOutputStream outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            MediaScannerConnection.scanFile(this, new String[]{new File(albumPathFull).getAbsolutePath()}, null, null);

            Toast.makeText(this, "File copy successfully", Toast.LENGTH_SHORT).show();
            UtilApp.isAlbumsFragChange = true;
            UtilApp.isAllMediaFragChange = true;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error copying file to album", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFileAlreadyExists(String fileName, String albumPath) {
        File destFile = new File(albumPath, fileName);
        return destFile.exists();
    }

    private void dialogCreateAlbum(BottomSheetDialog bottomSheetDialog, Boolean isCopy) {
        Dialog dialogCreateAlbum = UtilDialog.getDialog(ActMainPager.this, R.layout.dialog_create_album);
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

                    selectedAlbum = trashDirectory.getAbsolutePath();
                    if (isCopy) {
                        copyFileToAlbum(imageOrVideoPath, selectedAlbum);
                    } else {
                        moveFileToAlbum(imageOrVideoPath, selectedAlbum);
                    }
                } else {
                }
            } else {
                Toast.makeText(ActMainPager.this, "Album already exists", Toast.LENGTH_SHORT).show();
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
        Dialog dialogCreateAlbumPrivacy = UtilDialog.getDialog(ActMainPager.this, R.layout.dialog_create_album);
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
        backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (removedPosList.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("deletedPosList", removedPosList);
                    setResult(-1, intent);
                    finish();
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}
