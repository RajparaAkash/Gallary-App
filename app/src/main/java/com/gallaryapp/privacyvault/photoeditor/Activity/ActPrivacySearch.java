package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyAlbumFiles;
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
import java.util.Locale;

public class ActPrivacySearch extends ActBase {

    private ImageView back_img;
    private TextView headerTxt;
    private EditText privacySearchEt;
    private ImageView privacySelectAllImg;
    private RecyclerView privacyAlbumsRv;

    private LinearLayout privacyBottomLay;
    private LinearLayout privacyBottomShare;
    private LinearLayout privacyBottomUnlock;
    private LinearLayout privacyBottomDelete;
    private LinearLayout privacyBottomInfo;

    AdapterPrivacyAlbumFiles adapterPrivacyAlbumFiles;
    ArrayList<Media> privacyVaultMediaList;
    ArrayList<Media> filteredList;

    boolean selectionMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_privacy_search);

        idBind();
        setOnBackPressed();

        refreshAlbum();

        privacySearchEt.requestFocus();
        showKeyBoard();

        privacySelectAllImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterPrivacyAlbumFiles.getSelectedCount() == adapterPrivacyAlbumFiles.getItemCount()) {

                    back_img.setImageResource(R.drawable.header_back_img);
                    headerTxt.setVisibility(View.GONE);
                    showEditText();
                    privacySelectAllImg.setVisibility(View.GONE);
                    privacyBottomLay.setVisibility(View.GONE);

                    adapterPrivacyAlbumFiles.clearSelected();
                    privacySelectAllImg.setImageResource(R.drawable.icon_img_unselected);

                } else {

                    adapterPrivacyAlbumFiles.selectAll();
                    privacySelectAllImg.setImageResource(R.drawable.icon_img_selected);

                    headerTxt.setText(adapterPrivacyAlbumFiles.getSelectedCount() + " of " + adapterPrivacyAlbumFiles.getItemCount());
                }
            }
        });

        privacySearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                privacyVaultMediaList = UtilPrivacyVault.getPrivacyVaultAll();
                String searchText = s.toString().toLowerCase(Locale.getDefault());
                filteredList = new ArrayList<>();
                for (Media searchData : privacyVaultMediaList) {
                    if (searchData.getFileName().toLowerCase(Locale.getDefault()).startsWith(searchText)) {
                        filteredList.add(searchData);
                    }
                }
                adapterPrivacyAlbumFiles.filterList(filteredList);
            }
        });

        privacyBottomShare.setOnClickListener(v -> {
            ArrayList<String> filePaths = new ArrayList<>();
            for (int i = 0; i < adapterPrivacyAlbumFiles.getSelected().size(); i++) {
                filePaths.add(adapterPrivacyAlbumFiles.getSelected().get(i).getPath());
            }
            shareMultipleFiles(filePaths);
        });

        privacyBottomUnlock.setOnClickListener(view -> {
            unlockMedia();
        });

        privacyBottomDelete.setOnClickListener(view -> {
            dialogFileDelete();
        });

        privacyBottomInfo.setOnClickListener(view -> {
            dialogFileDetails();
        });
    }

    private void refreshAlbum() {
        privacyAlbumsRv.setLayoutManager(new GridLayoutManager(this, 3));
        privacyVaultMediaList = UtilPrivacyVault.getPrivacyVaultAll();

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

                if (selectionCount > 1) {
                    privacyBottomInfo.setVisibility(View.GONE);
                } else {
                    privacyBottomInfo.setVisibility(View.VISIBLE);
                }

                if (selectionCount > 0) {
                    back_img.setImageResource(R.drawable.header_close_img);
                    headerTxt.setText(adapterPrivacyAlbumFiles.getSelectedCount() + " of " + adapterPrivacyAlbumFiles.getItemCount());
                    privacySearchEt.setVisibility(View.GONE);
                    privacySelectAllImg.setVisibility(View.VISIBLE);
                    privacyBottomLay.setVisibility(View.VISIBLE);

                } else {
                    back_img.setImageResource(R.drawable.header_back_img);
                    headerTxt.setVisibility(View.GONE);
                    showEditText();
                    privacySelectAllImg.setVisibility(View.GONE);
                    privacyBottomLay.setVisibility(View.GONE);

                    adapterPrivacyAlbumFiles.clearSelected();
                }

                if (selectionCount == totalCount) {
                    privacySelectAllImg.setImageResource(R.drawable.icon_img_selected);
                } else {
                    privacySelectAllImg.setImageResource(R.drawable.icon_img_unselected);
                }

            }
        }, new AdapterPrivacyAlbumFiles.LongClickInter() {
            @Override
            public void onClick(int pos) {

                if (filteredList != null) {
                    privacyVaultMediaList = filteredList;
                }

                headerTxt.setVisibility(View.VISIBLE);
                hideKeyBoard();
            }
        }, new AdapterPrivacyAlbumFiles.NormalClickInter() {
            @Override
            public void onClick(int pos) {

                if (filteredList != null) {
                    privacyVaultMediaList = filteredList;
                }

                if (!selectionMode) {
                    hideKeyBoard();

                    InterstitialAds.ShowInterstitial(ActPrivacySearch.this, () -> {
                        Intent i = new Intent(ActPrivacySearch.this, ActPrivacyPager.class);
                        i.putExtra("data", privacyVaultMediaList);
                        i.putExtra("position", pos);
                        i.putExtra("fromSearch", true);
                        startActivity(i);
                    });
                }
            }
        });
        privacyAlbumsRv.setAdapter(adapterPrivacyAlbumFiles);
    }

    private void shareMultipleFiles(ArrayList<String> filePaths) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            uris.add(fileUri);
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("*/*");

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Files"));
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

        String imagePath = adapterPrivacyAlbumFiles.getSelected().get(0).getPath();

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
            progressDialog = new ProgressDialog(ActPrivacySearch.this, R.style.ProgressDialogStyle);
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
                Toast.makeText(ActPrivacySearch.this, "Unlock Successfully", Toast.LENGTH_SHORT).show();
                reSetWithAdapter();
                UtilApp.isAllMediaFragChange = true;
                UtilApp.isAlbumsFragChange = true;
                UtilApp.isValutActChange = true;
            } else {
                Toast.makeText(ActPrivacySearch.this, "Failed to unlock files", Toast.LENGTH_SHORT).show();
                reSetWithAdapter();
            }
        }
    }

    private void idBind() {
        back_img = findViewById(R.id.back_img);
        headerTxt = findViewById(R.id.headerTxt);
        privacySearchEt = findViewById(R.id.privacySearchEt);
        privacySelectAllImg = findViewById(R.id.privacySelectAllImg);
        privacyAlbumsRv = findViewById(R.id.privacyAlbumsRv);

        privacyBottomLay = findViewById(R.id.privacyBottomLay);
        privacyBottomShare = findViewById(R.id.privacyBottomShare);
        privacyBottomUnlock = findViewById(R.id.privacyBottomUnlock);
        privacyBottomDelete = findViewById(R.id.privacyBottomDelete);
        privacyBottomInfo = findViewById(R.id.privacyBottomInfo);
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(privacySearchEt, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(privacySearchEt.getWindowToken(), 0);
    }

    private void reSetWithAdapter() {
        reSetWithOutAdapter();
        refreshAlbum();
        privacySearchEt.setText("");
    }

    private void reSetWithOutAdapter() {
        selectionMode = false;
        back_img.setImageResource(R.drawable.header_back_img);
        headerTxt.setVisibility(View.GONE);
        showEditText();
        privacySelectAllImg.setVisibility(View.GONE);
        privacyBottomLay.setVisibility(View.GONE);

        adapterPrivacyAlbumFiles.clearSelected();
    }

    public void showEditText() {
        privacySearchEt.setVisibility(View.VISIBLE);
        privacySearchEt.requestFocus();
        showKeyBoard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilApp.isValutSearchActChange) {
            UtilApp.isValutSearchActChange = false;
            reSetWithAdapter();
        }
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (privacySearchEt.getVisibility() == View.GONE) {
                    reSetWithOutAdapter();
                } else {
                    if (!selectionMode) {
                        InterstitialAds.ShowInterstitialBack(ActPrivacySearch.this, () -> {
                            finish();
                        });
                    } else {
                        reSetWithOutAdapter();
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