package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gallaryapp.privacyvault.photoeditor.Ads.BannerAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.DebounceClickListener;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.Ads.RewardAds;
import com.gallaryapp.privacyvault.photoeditor.BgRemover.BgRemoverOptionAdapter;
import com.gallaryapp.privacyvault.photoeditor.BgRemover.Config;
import com.gallaryapp.privacyvault.photoeditor.BgRemover.ImageUtils;
import com.gallaryapp.privacyvault.photoeditor.BgRemover.MultiTouchListener;
import com.gallaryapp.privacyvault.photoeditor.BgRemover.PerfectErase;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActAiBgRemoverEdit extends ActBase {

    private ImageView bgRemover_undoImg;
    private ImageView bgRemover_redoImg;
    private ImageView bgRemover_bgImg;
    private RelativeLayout bgRemover_mainLay;
    private TextView bgRemover_valueTxt;
    private LinearLayout erase_mainLay;
    private LinearLayout auto_mainLay;
    private LinearLayout lasso_mainLay;
    private RecyclerView bgRemoverOptionsRv;

    private SeekBar erase_sizeSb;
    private SeekBar erase_offSetSb;
    private SeekBar auto_thresholdSb;
    private SeekBar auto_offSetSb;
    private SeekBar lasso_offSetSb;

    private ImageView lasso_outSideImg;
    private ImageView lasso_inSideImg;
    private ImageView mainImageView;

    public static BitmapShader bShader = null;
    public static Bitmap bitmap = null;
    public static Bitmap gBtm = null;
    public static int iBg = 1;
    public static int height = 0;
    public static int width;
    public static int iH;
    public static int iW;
    public static Bitmap oBtm;
    MLImageSegmentationAnalyzer analyzer;
    public PerfectErase perfectErase;

    private Dialog dialog;
    boolean showDialog = false;
    private boolean checkMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ai_bg_remover_edit);

        // Adaptive_Banner
        new BannerAds(this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));

        idBind();
        setOnBackPressed();
        init();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        int dpToPx = i - ImageUtils.dpToPx(this, 120.0f);
        height = dpToPx;
        iBg = 1;

        bgRemover_bgImg.setImageBitmap(ImageUtils.getTiledBitmap(this, R.drawable.bg_remover_trans_white, width, dpToPx));

        bgRemover_mainLay.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogImageImporting();
            }
        }, 10L);

        bgRemoverOptionsRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        bgRemoverOptionsRv.setHasFixedSize(true);
        bgRemoverOptionsRv.setAdapter(new BgRemoverOptionAdapter(this, Config.eTool(this, R.array.bgRemover_icon, R.array.bgRemover_title), new BgRemoverOptionAdapter.oClick() {
            @Override
            public void onOption(int i2) {
                adapterOptionClick(i2);
            }
        }));

        findViewById(R.id.bgRemover_saveTxt).setOnClickListener(view -> {
            showRewardAdsDialog();
        });
    }

    private void idBind() {
        bgRemover_undoImg = findViewById(R.id.bgRemover_undoImg);
        bgRemover_redoImg = findViewById(R.id.bgRemover_redoImg);
        bgRemover_bgImg = findViewById(R.id.bgRemover_bgImg);
        bgRemover_mainLay = findViewById(R.id.bgRemover_mainLay);
        bgRemover_valueTxt = findViewById(R.id.bgRemover_valueTxt);
        erase_mainLay = findViewById(R.id.erase_mainLay);
        auto_mainLay = findViewById(R.id.auto_mainLay);
        lasso_mainLay = findViewById(R.id.lasso_mainLay);
        bgRemoverOptionsRv = findViewById(R.id.bgRemoverOptionsRv);

        erase_sizeSb = findViewById(R.id.erase_sizeSb);
        erase_offSetSb = findViewById(R.id.erase_offSetSb);
        auto_thresholdSb = findViewById(R.id.auto_thresholdSb);
        auto_offSetSb = findViewById(R.id.auto_offSetSb);
        lasso_offSetSb = findViewById(R.id.lasso_offSetSb);

        lasso_outSideImg = findViewById(R.id.lasso_outSideImg);
        lasso_inSideImg = findViewById(R.id.lasso_inSideImg);
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

                RewardAds.ShowRewardedAd(ActAiBgRemoverEdit.this, () -> {
                    saveFinalImage();
                });
            }
        });
    }

    public void adapterOptionClick(int i) {
        if (i == 0) {
            perfectErase.enableTouchClear(true);
            bgRemover_mainLay.setOnTouchListener(null);
            perfectErase.setMODE(1);
            perfectErase.invalidate();
            erase_offSetSb.setProgress(perfectErase.getOffset() + 150);
            erase_mainLay.setVisibility(View.VISIBLE);
            auto_mainLay.setVisibility(View.GONE);
            lasso_mainLay.setVisibility(View.GONE);
        } else if (i == 1) {
            perfectErase.enableTouchClear(true);
            bgRemover_mainLay.setOnTouchListener(null);
            perfectErase.setMODE(2);
            perfectErase.invalidate();
            auto_offSetSb.setProgress(perfectErase.getOffset() + 150);
            erase_mainLay.setVisibility(View.GONE);
            auto_mainLay.setVisibility(View.VISIBLE);
            lasso_mainLay.setVisibility(View.GONE);
        } else if (i == 2) {
            perfectErase.enableTouchClear(true);
            bgRemover_mainLay.setOnTouchListener(null);
            perfectErase.setMODE(3);
            perfectErase.invalidate();
            lasso_offSetSb.setProgress(perfectErase.getOffset() + 150);
            erase_mainLay.setVisibility(View.GONE);
            auto_mainLay.setVisibility(View.GONE);
            lasso_mainLay.setVisibility(View.VISIBLE);
        } else if (i == 3) {
            perfectErase.enableTouchClear(true);
            bgRemover_mainLay.setOnTouchListener(null);
            perfectErase.setMODE(4);
            perfectErase.invalidate();
            erase_offSetSb.setProgress(perfectErase.getOffset() + 150);
            erase_mainLay.setVisibility(View.VISIBLE);
            auto_mainLay.setVisibility(View.GONE);
            lasso_mainLay.setVisibility(View.GONE);
        } else if (i == 4) {
            perfectErase.enableTouchClear(false);
            bgRemover_mainLay.setOnTouchListener(new MultiTouchListener());
            perfectErase.setMODE(0);
            perfectErase.invalidate();
            erase_mainLay.setVisibility(View.GONE);
            auto_mainLay.setVisibility(View.GONE);
            lasso_mainLay.setVisibility(View.GONE);
        } else {
            changeBG();
        }
    }

    private void set(View view, final Runnable runnable) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                runnable.run();
            }
        });
    }

    private void init() {
        set(bgRemover_undoImg, new Runnable() {
            @Override
            public void run() {
                perfectErase.undoChange();
            }
        });
        set(bgRemover_redoImg, new Runnable() {
            @Override
            public void run() {
                perfectErase.redoChange();
            }
        });
        set(lasso_inSideImg, new Runnable() {
            @Override
            public void run() {
                perfectErase.enableInsideCut(true);
            }
        });
        set(lasso_outSideImg, new Runnable() {
            @Override
            public void run() {
                perfectErase.enableInsideCut(false);
            }
        });

        sbChange(auto_offSetSb);
        sbChange(lasso_offSetSb);
        sbChange(erase_sizeSb);

        erase_offSetSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                bgRemover_valueTxt.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bgRemover_valueTxt.setVisibility(View.GONE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                bgRemover_valueTxt.setText(i + " %");
                if (perfectErase != null) {
                    perfectErase.setOffset(i - 150);
                    perfectErase.invalidate();
                }
            }
        });

        erase_sizeSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                bgRemover_valueTxt.setText(i + " %");
                if (perfectErase != null) {
                    perfectErase.setRadius(i + 2);
                    perfectErase.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                bgRemover_valueTxt.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bgRemover_valueTxt.setVisibility(View.GONE);
            }
        });

        auto_thresholdSb.setOnSeekBarChangeListener(listener());
    }

    private void sbChange(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(change());
    }

    private SeekBar.OnSeekBarChangeListener change() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                bgRemover_valueTxt.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bgRemover_valueTxt.setVisibility(View.GONE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                bgRemover_valueTxt.setText(i + " %");
                if (perfectErase != null) {
                    perfectErase.setOffset(i - 150);
                    perfectErase.invalidate();
                }
            }
        };
    }

    private SeekBar.OnSeekBarChangeListener listener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                bgRemover_valueTxt.setText(i + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                bgRemover_valueTxt.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bgRemover_valueTxt.setVisibility(View.GONE);
                if (perfectErase != null) {
                    perfectErase.setThreshold(seekBar.getProgress() + 10);
                    perfectErase.updateThreshHold();
                }
            }
        };
    }

    private void saveFinalImage() {
        Bitmap finalBitmap = perfectErase.getFinalBitmap();
        bitmap = finalBitmap;
        if (finalBitmap != null) {
            try {
                int dpToPx = ImageUtils.dpToPx(this, 42.0f);
                Bitmap bitmapResize = ImageUtils.getBitmapResize(bitmap, iW + dpToPx + dpToPx, iH + dpToPx + dpToPx);
                bitmap = bitmapResize;
                int i = dpToPx + dpToPx;
                Bitmap createBitmap = Bitmap.createBitmap(bitmapResize, dpToPx, dpToPx, bitmapResize.getWidth() - i, bitmap.getHeight() - i);
                bitmap = createBitmap;
                Bitmap createScaledBitmap = Bitmap.createScaledBitmap(createBitmap, iW, iH, true);
                bitmap = createScaledBitmap;
                Bitmap maskBtm = ImageUtils.maskBtm(gBtm, createScaledBitmap);
                bitmap = maskBtm;
                downloadImage(maskBtm);
                return;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return;
            }
        }
        finish();
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
                    try {
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

                        MediaScannerConnection.scanFile(ActAiBgRemoverEdit.this, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String str, Uri uri) {
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dismissDialog();

                    if (checkMemory) {
                        Intent intent = new Intent(ActAiBgRemoverEdit.this, ActImageShare.class);
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

    private void changeBG() {
        int[] iArr = {R.drawable.bg_remover_trans_white, R.drawable.bg_remover_trans_black};
        iBg = (iBg % 2) + 1;
        bgRemover_bgImg.setImageBitmap(null);
        bgRemover_bgImg.setImageBitmap(ImageUtils.getTiledBitmap(this, iArr[iBg - 1], width, height));
    }

    public void dialogImageImporting() {
        this.showDialog = false;
        showDialog(getResources().getString(R.string.str_221));

        new Thread(new Runnable() {
            @Override
            public void run() {
                importImage();
            }
        }).start();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (showDialog) {
                    Toast.makeText(ActAiBgRemoverEdit.this, "Error while getting image.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                setImageBitmap();
            }
        });
    }

    public void importImage() {
        try {
            Bitmap bitmap2 = oBtm;
            if (bitmap2 == null) {
                showDialog = true;
            } else {
                gBtm = bitmap2.copy(bitmap2.getConfig(), true);
                int dpToPx = ImageUtils.dpToPx(this, 42.0f);
                iW = oBtm.getWidth();
                iH = oBtm.getHeight();
                Bitmap createBitmap = Bitmap.createBitmap(oBtm.getWidth() + dpToPx + dpToPx, oBtm.getHeight() + dpToPx + dpToPx, oBtm.getConfig());
                Canvas canvas = new Canvas(createBitmap);
                canvas.drawColor(0);
                float f = dpToPx;
                canvas.drawBitmap(oBtm, f, f, (Paint) null);
                oBtm = createBitmap;
                if (createBitmap.getWidth() > width || oBtm.getHeight() > height || (oBtm.getWidth() < width && oBtm.getHeight() < height)) {
                    oBtm = ImageUtils.getBitmapResize(oBtm, width, height);
                }
            }
            Thread.sleep(1000L);
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            showDialog = true;
            dismissDialog();
        }
        dismissDialog();
    }

    public void setImageBitmap() {
        perfectErase = new PerfectErase(this);
        mainImageView = new ImageView(this);
        perfectErase.setImageBitmap(oBtm);
        mainImageView.setImageBitmap(getGreenLayerBitmap(oBtm));

        perfectErase.invalidate();
        perfectErase.enableTouchClear(true);
        bgRemover_mainLay.setOnTouchListener(null);
        perfectErase.setMODE(1);
        perfectErase.invalidate();

        erase_offSetSb.setProgress(perfectErase.getOffset() + 150);
        erase_sizeSb.setProgress(18);
        auto_thresholdSb.setProgress(20);
        bgRemover_mainLay.removeAllViews();
        bgRemover_mainLay.setScaleX(1.0f);
        bgRemover_mainLay.setScaleY(1.0f);
        bgRemover_mainLay.addView(mainImageView);
        bgRemover_mainLay.addView(perfectErase);
        perfectErase.invalidate();
        mainImageView.setVisibility(View.GONE);
        oBtm.recycle();
    }

    public Bitmap getGreenLayerBitmap(Bitmap bitmap2) {
        Paint paint = new Paint();
        paint.setColor(-16711936);
        paint.setAlpha(80);
        int dpToPx = ImageUtils.dpToPx(this, 42.0f);
        int i = dpToPx * 2;
        Bitmap createBitmap = Bitmap.createBitmap(iW + i, iH + i, bitmap2.getConfig());
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(0);
        float f = dpToPx;
        canvas.drawBitmap(gBtm, f, f, (Paint) null);
        canvas.drawRect(f, f, iW + f, iH + f, paint);
        Bitmap createBitmap2 = Bitmap.createBitmap(iW + i, iH + i, bitmap2.getConfig());
        Canvas canvas2 = new Canvas(createBitmap2);
        canvas2.drawColor(0);
        canvas2.drawBitmap(gBtm, f, f, (Paint) null);
        bShader = new BitmapShader(ImageUtils.getBitmapResize(createBitmap2, width, height), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        return ImageUtils.getBitmapResize(createBitmap, width, height);
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

    private void dialogDiscardImg() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_discard_changes, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        TextView messageTxt = bottomSheetView.findViewById(R.id.messageTxt);
        TextView discardTxt = bottomSheetView.findViewById(R.id.discardTxt);
        TextView cancelTxt = bottomSheetView.findViewById(R.id.cancelTxt);

        messageTxt.setText(getResources().getString(R.string.str_13));
        discardTxt.setText(getResources().getString(R.string.str_14));

        cancelTxt.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        discardTxt.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            InterstitialAds.ShowInterstitialBack(ActAiBgRemoverEdit.this, () -> {
                finish();
            });
        });
    }

    @Override
    public void onDestroy() {
        Bitmap bitmap2 = oBtm;
        if (bitmap2 != null) {
            bitmap2.recycle();
            oBtm = null;
        }
        try {
            if (!isFinishing() && perfectErase.pd != null && perfectErase.pd.isShowing()) {
                perfectErase.pd.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void setOnBackPressed() {
        OnBackPressedCallback backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dialogDiscardImg();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}