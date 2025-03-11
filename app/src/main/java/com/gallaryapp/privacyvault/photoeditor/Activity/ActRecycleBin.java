package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyAlbumFiles;
import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceActions;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilPrivacyVault;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ActRecycleBin extends ActBase {

    ImageView back_img;
    TextView headerTxt;
    ImageView recycleSelectAllImg;
    ImageView recycleMoreImg;
    RecyclerView recycleRv;
    LinearLayout recycleBottomLay;
    TextView bottomRestoreTxt;
    TextView bottomDeleteTxt;
    TextView noDataFoundTxt;

    AdapterPrivacyAlbumFiles moveToTrashAdapter;
    ArrayList<Media> movieToTrashList = new ArrayList<>();

    boolean selectionMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recycle_bin);

        idBind();
        setOnBackPressed();

        refreshAdapter();

        recycleSelectAllImg.setOnClickListener(v -> {
            if (moveToTrashAdapter.getSelectedCount() == moveToTrashAdapter.getItemCount()) {

                back_img.setImageResource(R.drawable.header_back_img);
                headerTxt.setText(getResources().getString(R.string.str_105));
                recycleSelectAllImg.setVisibility(View.GONE);
                recycleMoreImg.setVisibility(View.VISIBLE);
                recycleBottomLay.setVisibility(View.GONE);

                moveToTrashAdapter.clearSelected();
                recycleSelectAllImg.setImageResource(R.drawable.icon_img_unselected);

            } else {

                moveToTrashAdapter.selectAll();
                recycleSelectAllImg.setImageResource(R.drawable.icon_img_selected);
                headerTxt.setText(moveToTrashAdapter.getSelectedCount() + " of " + moveToTrashAdapter.getItemCount());
            }
        });

        bottomRestoreTxt.setOnClickListener(view -> {
            dialogFileRestore(false);
        });

        bottomDeleteTxt.setOnClickListener(view -> {
            dialogFileDelete(false);
        });

        recycleMoreImg.setOnClickListener(view -> {
            dialogMore();
        });
    }

    private void dialogFileRestore(boolean restoreAll) {
        Dialog dialogRestore = UtilDialog.getDialog(ActRecycleBin.this, R.layout.dialog_file_restore);
        dialogRestore.setCancelable(true);

        TextView restoreTxt = (TextView) dialogRestore.findViewById(R.id.restoreTxt);
        TextView cancelTxt = (TextView) dialogRestore.findViewById(R.id.cancelTxt);

        restoreTxt.setOnClickListener(v -> {
            dialogRestore.dismiss();
            RestoreFilesTask task = new RestoreFilesTask(restoreAll);
            task.execute();
        });

        cancelTxt.setOnClickListener(v -> {
            dialogRestore.dismiss();
        });

        dialogRestore.show();
    }

    private void dialogFileDelete(boolean deleteAll) {
        Dialog dialogDelete = UtilDialog.getDialog(this, R.layout.dialog_file_delete);
        dialogDelete.setCancelable(true);

        TextView deleteTxt = (TextView) dialogDelete.findViewById(R.id.deleteTxt);
        TextView cancelTxt = (TextView) dialogDelete.findViewById(R.id.cancelTxt);

        deleteTxt.setOnClickListener(v -> {
            dialogDelete.dismiss();

            DeleteFilesTask task = new DeleteFilesTask(deleteAll);
            task.execute();
        });

        cancelTxt.setOnClickListener(v -> {
            dialogDelete.dismiss();
        });

        dialogDelete.show();
    }

    private class RestoreFilesTask extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;
        private boolean restoreAll;

        public RestoreFilesTask(boolean restoreAll) {
            this.restoreAll = restoreAll;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActRecycleBin.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            int totalFiles;
            if (restoreAll) {
                totalFiles = movieToTrashList.size();
            } else {
                totalFiles = moveToTrashAdapter.getSelected().size();
            }

            for (int i = 0; i < totalFiles; i++) {

                String oldFilePath;
                if (restoreAll) {
                    oldFilePath = movieToTrashList.get(i).getPath();
                } else {
                    oldFilePath = moveToTrashAdapter.getSelected().get(i).getPath();
                }

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

                    MediaScannerConnection.scanFile(ActRecycleBin.this,
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
                Toast.makeText(ActRecycleBin.this, "Restore Successfully", Toast.LENGTH_SHORT).show();
                refreshWithAdapterRestore();
            } else {
                Toast.makeText(ActRecycleBin.this, "Failed to restore files", Toast.LENGTH_SHORT).show();
                refreshWithAdapterRestore();
            }
        }
    }

    private class DeleteFilesTask extends AsyncTask<Void, Integer, Boolean> {

        private ProgressDialog progressDialog;
        private boolean deleteAll;

        public DeleteFilesTask(boolean deleteAll) {
            this.deleteAll = deleteAll;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActRecycleBin.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            int totalFiles;
            if (deleteAll) {
                totalFiles = movieToTrashList.size();
            } else {
                totalFiles = moveToTrashAdapter.getSelected().size();
            }

            for (int i = 0; i < totalFiles; i++) {

                String delteFilePath;
                if (deleteAll) {
                    delteFilePath = movieToTrashList.get(i).getPath();
                } else {
                    delteFilePath = moveToTrashAdapter.getSelected().get(i).getPath();
                }

                File file = new File(delteFilePath);
                if (file.exists()) {
                    file.delete();
                }

                MediaScannerConnection.scanFile(ActRecycleBin.this,
                        new String[]{new File(delteFilePath).getAbsolutePath()}, null, null);
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
                Toast.makeText(ActRecycleBin.this, "Delete Successfully", Toast.LENGTH_SHORT).show();
                refreshWithAdapterDelete();
            } else {
                Toast.makeText(ActRecycleBin.this, "Failed to delete files", Toast.LENGTH_SHORT).show();
                refreshWithAdapterDelete();
            }
        }
    }

    private void dialogMore() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_more_recycler, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        LinearLayout r_more_select = bottomSheetDialog.findViewById(R.id.r_more_select);
        LinearLayout r_more_empty_recycle = bottomSheetDialog.findViewById(R.id.r_more_empty_recycle);
        LinearLayout r_more_restore_all = bottomSheetDialog.findViewById(R.id.r_more_restore_all);

        r_more_select.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            moveToTrashAdapter.startSelection();

            back_img.setImageResource(R.drawable.header_close_img);
            headerTxt.setText(moveToTrashAdapter.getSelectedCount() + " of " + moveToTrashAdapter.getItemCount());
            recycleMoreImg.setVisibility(View.GONE);
            recycleSelectAllImg.setVisibility(View.VISIBLE);
            recycleBottomLay.setVisibility(View.VISIBLE);
        });

        r_more_empty_recycle.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            dialogFileDelete(true);
        });

        r_more_restore_all.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            dialogFileRestore(true);
        });
    }

    private void refreshAdapter() {

        movieToTrashList = UtilPrivacyVault.getRecycleBinAll();
        moveToTrashAdapter = new AdapterPrivacyAlbumFiles(this, movieToTrashList, new InterfaceActions() {
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
                    headerTxt.setText(moveToTrashAdapter.getSelectedCount() + " of " + moveToTrashAdapter.getItemCount());
                    recycleMoreImg.setVisibility(View.GONE);
                    recycleSelectAllImg.setVisibility(View.VISIBLE);
                    recycleBottomLay.setVisibility(View.VISIBLE);

                } else {
                    back_img.setImageResource(R.drawable.header_back_img);
                    headerTxt.setText(getResources().getString(R.string.str_105));
                    recycleSelectAllImg.setVisibility(View.GONE);
                    recycleMoreImg.setVisibility(View.VISIBLE);
                    recycleBottomLay.setVisibility(View.GONE);

                    moveToTrashAdapter.clearSelected();
                }

                if (selectionCount == totalCount) {
                    recycleSelectAllImg.setImageResource(R.drawable.icon_img_selected);
                } else {
                    recycleSelectAllImg.setImageResource(R.drawable.icon_img_unselected);
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
                    InterstitialAds.ShowInterstitial(ActRecycleBin.this, () -> {
                        Intent i = new Intent(ActRecycleBin.this, ActRecycleBinPager.class);
                        i.putExtra("data", movieToTrashList);
                        i.putExtra("position", pos);
                        startActivity(i);
                    });
                }
            }
        });

        if (movieToTrashList.size() > 0) {

            // Adaptive_Banner
            new BannerAds(this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));

            recycleRv.setLayoutManager(new GridLayoutManager(this, 3));
            recycleRv.setAdapter(moveToTrashAdapter);
            noDataFoundTxt.setVisibility(View.GONE);
            recycleMoreImg.setVisibility(View.VISIBLE);
            recycleRv.setVisibility(View.VISIBLE);
        } else {
            recycleRv.setVisibility(View.GONE);
            recycleMoreImg.setVisibility(View.GONE);
            noDataFoundTxt.setVisibility(View.VISIBLE);
        }
    }

    private void idBind() {
        back_img = findViewById(R.id.back_img);
        headerTxt = findViewById(R.id.headerTxt);
        recycleSelectAllImg = findViewById(R.id.recycleSelectAllImg);
        recycleMoreImg = findViewById(R.id.recycleMoreImg);
        recycleRv = findViewById(R.id.recycleRv);
        recycleBottomLay = findViewById(R.id.recycleBottomLay);
        bottomRestoreTxt = findViewById(R.id.bottomRestoreTxt);
        bottomDeleteTxt = findViewById(R.id.bottomDeleteTxt);
        noDataFoundTxt = findViewById(R.id.noDataFoundTxt);
    }

    public void refreshWithAdapterDelete() {
        refreshWithOutAdapter();
        refreshAdapter();
    }

    public void refreshWithAdapterRestore() {
        refreshWithOutAdapter();
        refreshAdapter();
        UtilApp.isAllMediaFragChange = true;
        UtilApp.isAlbumsFragChange = true;
    }

    public void refreshWithOutAdapter() {
        selectionMode = false;
        back_img.setImageResource(R.drawable.header_back_img);
        headerTxt.setText(getResources().getString(R.string.str_105));
        recycleSelectAllImg.setVisibility(View.GONE);
        recycleMoreImg.setVisibility(View.VISIBLE);
        recycleBottomLay.setVisibility(View.GONE);

        moveToTrashAdapter.clearSelected();
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!selectionMode) {
                    InterstitialAds.ShowInterstitialBack(ActRecycleBin.this, () -> {
                        finish();
                    });
                } else {
                    refreshWithOutAdapter();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        back_img.setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}