package com.gallaryapp.privacyvault.photoeditor.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Activity.ActPrivacyPager;
import com.gallaryapp.privacyvault.photoeditor.Activity.ActPrivacySecretSnap;
import com.gallaryapp.privacyvault.photoeditor.Activity.ActPrivacySelectFile;
import com.gallaryapp.privacyvault.photoeditor.Activity.ActPrivacySetting;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterPrivacyAlbumFiles;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceActions;
import com.gallaryapp.privacyvault.photoeditor.Interface.MoreClickListener;
import com.gallaryapp.privacyvault.photoeditor.Model.Media;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilPrivacyVault;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class FragPrivacyVideos extends Fragment implements MoreClickListener {

    RecyclerView vaultListVideosRv;
    TextView videoNotFoundTxt;
    ImageView videoAddDelete;

    private AdapterPrivacyAlbumFiles adapter;
    private ArrayList<Media> vaultListData;

    boolean selectionMode = false;
    private Animation rotate_backward;
    private Animation rotate_forward;
    public Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.mActivity = (Activity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_privacy_videos, container, false);

        idBinding(view);
        setAdapterVaultVideo();

        rotate_forward = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_rotate_backward);

        videoAddDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnFab();
            }
        });

        return view;
    }

    private void idBinding(View view) {
        vaultListVideosRv = (RecyclerView) view.findViewById(R.id.vaultListVideosRv);
        videoNotFoundTxt = (TextView) view.findViewById(R.id.videoNotFoundTxt);
        videoAddDelete = (ImageView) view.findViewById(R.id.videoAddDelete);
    }

    private void setAdapterVaultVideo() {

        vaultListData = UtilPrivacyVault.getPrivacyVaultVideos();
        if (vaultListData.size() != 0) {
            vaultListVideosRv.setVisibility(View.VISIBLE);
            videoNotFoundTxt.setVisibility(View.GONE);

            vaultListVideosRv.setLayoutManager(new GridLayoutManager(mActivity, MyPreference.get_Privacy_VideoGridSize()));
            adapter = new AdapterPrivacyAlbumFiles(mActivity, vaultListData, new InterfaceActions() {
                @Override
                public void onItemSelected(int i, ImageView imageView) {

                }

                @Override
                public void onSelectMode(boolean z) {
                    selectionMode = z;
                    if (selectionMode) {
                        videoAddDelete.startAnimation(rotate_forward);
                    } else {
                        videoAddDelete.startAnimation(rotate_backward);
                    }
                }

                @Override
                public void onSelectionCountChanged(int selectionCount, int totalCount) {

                }
            }, new AdapterPrivacyAlbumFiles.LongClickInter() {
                @Override
                public void onClick(int pos) {

                }
            }, new AdapterPrivacyAlbumFiles.NormalClickInter() {
                @Override
                public void onClick(int pos) {

                    if (!selectionMode) {
                        InterstitialAds.ShowInterstitial(mActivity, () -> {
                            Intent i = new Intent(mActivity, ActPrivacyPager.class);
                            i.putExtra("data", vaultListData);
                            i.putExtra("position", pos);
                            i.putExtra("fromSearch", false);
                            startActivity(i);
                        });
                    }
                }
            });
            vaultListVideosRv.setAdapter(adapter);
        } else {
            vaultListVideosRv.setVisibility(View.GONE);
            videoNotFoundTxt.setVisibility(View.VISIBLE);
        }
    }

    public void clickOnFab() {
        if (selectionMode) {
            UnlockFilesAsyncTask task = new UnlockFilesAsyncTask();
            task.execute();
        } else {
            pickUpFile();
        }
    }

    private void pickUpFile() {
        InterstitialAds.ShowInterstitial(requireActivity(), () -> {
            Intent intent = new Intent(requireActivity(), ActPrivacySelectFile.class);
            startActivity(intent);
        });
    }

    private class UnlockFilesAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity, R.style.ProgressDialogStyle);
            progressDialog.setMessage(getResources().getString(R.string.str_166));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            int totalFiles = adapter.getSelected().size();

            for (int i = 0; i < totalFiles; i++) {
                String oldFilePath = adapter.getSelected().get(i).getPath();

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
            videoAddDelete.startAnimation(rotate_backward);
            selectionMode = false;
            if (result) {
                Toast.makeText(mActivity, "Unlock Successfully", Toast.LENGTH_SHORT).show();
                setAdapterVaultVideo();
                UtilApp.isAllMediaFragChange = true;
                UtilApp.isAlbumsFragChange = true;
                UtilApp.isValutActChange = true;
            } else {
                Toast.makeText(mActivity, "Failed to unlock files", Toast.LENGTH_SHORT).show();
                setAdapterVaultVideo();
            }
        }
    }

    public boolean isSelectionModeEnabledVideo() {
        return selectionMode;
    }

    public void unselectAllVideos() {
        adapter.clearSelected();
        videoAddDelete.startAnimation(rotate_backward);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UtilApp.isValutActChange) {
            setAdapterVaultVideo();
        }
    }

    @Override
    public void onMoreClicked() {
        dialogPrivacyMore();
    }

    public void dialogPrivacyMore() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_more_privacy_vault, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        LinearLayout p_more_thumbnail_columns = bottomSheetDialog.findViewById(R.id.p_more_thumbnail_columns);
        LinearLayout p_more_create_album = bottomSheetDialog.findViewById(R.id.p_more_create_album);
        LinearLayout p_more_secret_snap = bottomSheetDialog.findViewById(R.id.p_more_secret_snap);
        LinearLayout p_more_settings = bottomSheetDialog.findViewById(R.id.p_more_settings);

        p_more_create_album.setVisibility(View.GONE);

        p_more_thumbnail_columns.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            dialogThumbnailColumns();
        });

        p_more_secret_snap.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            InterstitialAds.ShowInterstitial(mActivity, () -> {
                startActivity(new Intent(mActivity, ActPrivacySecretSnap.class));
            });
        });

        p_more_settings.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            InterstitialAds.ShowInterstitial(mActivity, () -> {
                startActivity(new Intent(mActivity, ActPrivacySetting.class));
            });
        });
    }

    public void dialogThumbnailColumns() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(mActivity, R.style.CustomBottomSheetDialog);
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

        if (MyPreference.get_Privacy_VideoGridSize() == 2) {
            columns2CheckBox.setChecked(true);
        } else if (MyPreference.get_Privacy_VideoGridSize() == 3) {
            columns3CheckBox.setChecked(true);
        } else if (MyPreference.get_Privacy_VideoGridSize() == 4) {
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
                MyPreference.set_Privacy_VideoGridSize(2);
                setAdapterVaultVideo();
            } else if (columns3CheckBox.isChecked()) {
                MyPreference.set_Privacy_VideoGridSize(3);
                setAdapterVaultVideo();
            } else if (columns4CheckBox.isChecked()) {
                MyPreference.set_Privacy_VideoGridSize(4);
                setAdapterVaultVideo();
            }
        });
    }
}
