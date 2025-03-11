package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.gallaryapp.privacyvault.photoeditor.Ads.DebounceClickListener;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.NativeAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.RewardAds;
import com.gallaryapp.privacyvault.photoeditor.BgRemover.Config;
import com.gallaryapp.privacyvault.photoeditor.BgRemover.OnRemoved;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActAiBgRemoverAuto extends ActBase {

    private ImageView bgRemover_mainImg;
    private TextView removeBackgroundTxt;
    private LinearLayout optionLay;

    public static Bitmap editBitmap;
    private Bitmap orignalBitmap;
    private Bitmap backgroundBitmap;
    MLImageSegmentationAnalyzer analyzer;

    private Dialog dialog;
    private boolean checkMemory;
    private boolean isSave = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ai_bg_remover_auto);

        // NativeSmall
        NativeAds.ShowNativeSmall(this, findViewById(R.id.nativeSmallLay), findViewById(R.id.nativeLay));

        idBind();
        setOnBackPressed();

        orignalBitmap = editBitmap;
        bgRemover_mainImg.setImageBitmap(orignalBitmap);

        /*int bitmapWidth = orignalBitmap.getWidth();
        int bitmapHeight = orignalBitmap.getHeight();

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            backgroundBitmap = ImageUtils.getTiledBitmap(this, R.drawable.bg_remover_trans_black, bitmapWidth, bitmapHeight);
        } else {
            backgroundBitmap = ImageUtils.getTiledBitmap(this, R.drawable.bg_remover_trans_white, bitmapWidth, bitmapHeight);
        }
        bgRemover_bgImg.setImageBitmap(backgroundBitmap);*/

        removeBackgroundTxt.setOnClickListener(new DebounceClickListener() {
            @Override
            public void performClick(View v) {
                if (isSave) {
                    showRewardAdsDialog();
                } else {
                    showDialog(getResources().getString(R.string.str_220));
                    new Thread(() -> {
                        removeBgFromBitmap();
                    }).start();
                }
            }
        });

        findViewById(R.id.resetTxt).setOnClickListener(v -> {
            isSave = false;
            removeBackgroundTxt.setText(R.string.str_216);
            optionLay.setVisibility(View.INVISIBLE);

            bgRemover_mainImg.setImageBitmap(orignalBitmap);
        });

        findViewById(R.id.refineTxt).setOnClickListener(v -> {
            ActAiBgRemoverEdit.oBtm = editBitmap;
            InterstitialAds.ShowInterstitial(this, () -> {
                Intent intent = new Intent(ActAiBgRemoverAuto.this, ActAiBgRemoverEdit.class);
                startActivity(intent);
            });
        });
    }

    private void idBind() {
        bgRemover_mainImg = findViewById(R.id.bgRemover_mainImg);
        removeBackgroundTxt = findViewById(R.id.removeBackgroundTxt);
        optionLay = findViewById(R.id.optionLay);
    }

    @Override
    public void onDestroy() {
        if (editBitmap != null) {
            editBitmap.recycle();
            editBitmap = null;
        }
        super.onDestroy();
        MLImageSegmentationAnalyzer mLImageSegmentationAnalyzer = this.analyzer;
        if (mLImageSegmentationAnalyzer != null) {
            try {
                mLImageSegmentationAnalyzer.stop();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void removeBgFromBitmap() {
        removeBackgroundTxt.setText(R.string.str_220);
        Config.removeBg(this, orignalBitmap, new OnRemoved() {
            @Override
            public void onRemoved(MLImageSegmentation mLImageSegmentation) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editBitmap = mLImageSegmentation.getForeground();
                        bgRemover_mainImg.setImageBitmap(editBitmap);

                        isSave = true;
                        removeBackgroundTxt.setText(R.string.str_217);
                        optionLay.setVisibility(View.VISIBLE);

                        dismissDialog();
                    }
                }, Config.DURATION);
            }
        });
    }

    private void showRewardAdsDialog() {
        Dialog rewardDialog = UtilDialog.getDialog(this, R.layout.dialog_watch_reward);
        rewardDialog.setCancelable(true);
        rewardDialog.show();

        rewardDialog.findViewById(R.id.cancelTxt).setOnClickListener(v -> {
            rewardDialog.dismiss();
        });

        rewardDialog.findViewById(R.id.watchADTxt).setOnClickListener(new DebounceClickListener() {
            @Override
            public void performClick(View v) {
                rewardDialog.dismiss();

                RewardAds.ShowRewardedAd(ActAiBgRemoverAuto.this, () -> {
                    saveFinalImage();
                });
            }
        });
    }

    private void saveFinalImage() {
        if (editBitmap != null) {
            downloadImage(editBitmap);
        } else {
            finish();
        }
    }

    private void downloadImage(Bitmap finalBitmap) {

        showDialog(getResources().getString(R.string.str_166));

        new Thread(new Runnable() {
            public void run() {
                try {
                    File file = new File(UtilApp.AiBackgroundRemoverPath);
                    if (!file.exists()) {
                        if (!file.mkdirs()) {
                            Toast.makeText(getApplicationContext(), "Can not create directory to save image.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    String fileName = "AiBgRemover_" + System.currentTimeMillis() + ".png";
                    String filePath = file.getPath() + File.separator + fileName;

                    file = new File(filePath);

                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    Bitmap createBitmap = Bitmap.createBitmap(finalBitmap.getWidth(), finalBitmap.getHeight(), finalBitmap.getConfig());

                    Canvas canvas = new Canvas(createBitmap);
                    canvas.drawBitmap(finalBitmap, 0.0f, 0.0f, null);
                    checkMemory = createBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    createBitmap.recycle();
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    MediaScannerConnection.scanFile(ActAiBgRemoverAuto.this, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String str, Uri uri) {
                        }
                    });

                    dismissDialog();

                    if (checkMemory) {
                        Intent intent = new Intent(ActAiBgRemoverAuto.this, ActImageShare.class);
                        intent.putExtra("finalSavedImagePath", file.getAbsolutePath());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissDialog();
                }
            }
        }).start();
    }

    private void showDialog(String msg) {
        dialog = UtilDialog.getDialog(this, R.layout.dialog_ai_bg_remover);
        dialog.setCancelable(false);
        dialog.show();

        ((TextView) dialog.findViewById(R.id.dialogMsgTxt)).setText(msg);
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                InterstitialAds.ShowInterstitialBack(ActAiBgRemoverAuto.this, () -> {
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