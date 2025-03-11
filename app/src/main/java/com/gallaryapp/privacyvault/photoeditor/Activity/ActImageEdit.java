package com.gallaryapp.privacyvault.photoeditor.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterColors;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterEditOptions;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterEditSubOptions;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterFonts;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterStickersCategory;
import com.gallaryapp.privacyvault.photoeditor.Adapter.AdapterStickersFile;
import com.gallaryapp.privacyvault.photoeditor.Ads.InterstitialAds;
import com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit.CallbackStartDrawing;
import com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit.DrawingViews;
import com.gallaryapp.privacyvault.photoeditor.EditViews.StickerViewEdit.BubbleStickers;
import com.gallaryapp.privacyvault.photoeditor.EditViews.StickerViewEdit.DrawableStickers;
import com.gallaryapp.privacyvault.photoeditor.EditViews.StickerViewEdit.Sticker;
import com.gallaryapp.privacyvault.photoeditor.EditViews.StickerViewEdit.StickerView;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceItemClick;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceItemClickGallery;
import com.gallaryapp.privacyvault.photoeditor.Interface.InterfaceOptionItemClick;
import com.gallaryapp.privacyvault.photoeditor.Model.OptionDatasModel;
import com.gallaryapp.privacyvault.photoeditor.Model.StickerItemParentModelMan;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilApp;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilDialog;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilUnzipFile;
import com.gallaryapp.privacyvault.photoeditor.MyUtils.UtilVaultFile;
import com.gallaryapp.privacyvault.photoeditor.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActImageEdit extends ActBase implements View.OnClickListener {

    ArrayList<String> stickerArrayList = new ArrayList<>();

    String[] arrMainOption;
    Bitmap bm;
    AdapterColors adapterColors;

    Sticker currentSticker;
    DrawingViews drawingView;
    AdapterFonts adapterFonts;
    String[] fontNameArr;
    AppCompatImageView ivCheckMark;
    AppCompatImageView ivClose;
    int lastTimeInertedSticker;
    LinearLayout linImgStickerView;
    LinearLayout linSeekBarView;
    LinearLayout linThirdDivisionOption;
    AdapterEditOptions mAdapter;

    AppCompatImageView mainUserImage;
    String oldSavedFileName;
    int oldSelectedOption;
    Bitmap originalbitmap;
    ProgressDialog progressDialog;
    RecyclerView recyclerAdjust;
    RecyclerView recyclerBrushOption;
    RecyclerView recyclerBubble;
    RecyclerView recyclerColor;
    RecyclerView recyclerFilter;
    RecyclerView recyclerFont;
    RecyclerView recyclerMainAppOption;
    RecyclerView recyclerSticker;
    RecyclerView recyclerStickerCategory;
    RecyclerView recyclerStickerImgOption;
    RecyclerView recyclerStickerTextOption;
    RelativeLayout rel_image;
    View view_;
    int rotateImage;
    String finalSavedImagePath;
    SeekBar seekBar;
    Animation slideDownAnimation;
    Animation slideUpAnimation;
    AdapterStickersFile adapterStickersFile;
    ArrayList<StickerItemParentModelMan> stickerParentList;
    StickerView stickerView;

    Typeface[] typeface;
    boolean isBrushOpacity = false;
    boolean isBrushColorSelection = false;
    boolean isCurrentOptionEdited = false;
    boolean isEditMode = false;

    int screenImgEditorWidth = 0;
    int screenImgEditorHeight = 0;
    int POS_DEFAULT = 2;
    int POS_CROP_ROTATE = 0;
    int POS_TEXT = 1;
    int POS_STICKER = 2;
    int POS_BRUSH = 3;

    String[] arrBrushOptionText = {"Brush", "Eraser", "Size", "Opacity", "Undo", "Redo"};
    int[] arrBrushOptionIcon = {R.drawable.icon_edit_brush_brush, R.drawable.icon_edit_brush_eraser, R.drawable.icon_edit_brush_size, R.drawable.icon_edit_brush_opacity, R.drawable.icon_edit_brush_undo, R.drawable.icon_edit_brush_redo};
    Integer[] arrBrushOptionSelectablePos = {0, 1};
    Integer[] arrBrushOptionDeviderPos = {1, 3};

    String[] arrStickerTextOptionText = {"Edit", "Font", "Flip H", "Flip V", "Delete"};
    int[] arrStickerTextOptionIcon = {R.drawable.icon_edit_text_edit, R.drawable.icon_edit_text_font, R.drawable.icon_edit_text_flip_horizontal, R.drawable.icon_edit_text_flip_vertical, R.drawable.icon_edit_text_delete};
    Integer[] arrStickerTextOptionDeviderPos = {0, 2, 4};

    String[] arrStickerImgOptionText = {"Flip H", "Flip V", "Delete"};
    int[] arrStickerImgOptionIcon = {R.drawable.icon_edit_sticker_flip_horizontal, R.drawable.icon_edit_sticker_flip_vertical, R.drawable.icon_edit_sticker_delete};
    Integer[] arrStickerImgOptionDeviderPos = {1};
    boolean isSetProgrammmatically = false;
    String[] stickerListTitle = new String[0];
    Integer[] stickerListIcon = new Integer[0];

    private OnBackPressedCallback backPressed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_image_edit);

        initUi();
        setOnBackPressed();
        init();

        // Adaptive_Banner
        /*new BannerAds(this).AdaptiveBanner(findViewById(R.id.adaptiveBanner));*/

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
        }
        progressDialog.setMessage(getResources().getString(R.string.str_166));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Intent intent = getIntent();
        if (intent != null) {
            String editImagePath = intent.getStringExtra("editImagePath");
            rotateImage = UtilApp.getCameraPhotoOrientation(ActImageEdit.this, Uri.parse(editImagePath));
            checkBitmap(editImagePath);
            initMainOption();
            initBrushOption();
            initStickerTextOption();
            initStickerImgOption();
            initFontOption();
            initColorOption();
            initStickerOption();
        }
    }

    public void init() {
        DrawingViews drawingViews = (DrawingViews) findViewById(R.id.wachi_drawing_view);
        this.drawingView = drawingViews;
        drawingViews.setUserTouchListener(new CallbackStartDrawing() {
            @Override
            public void onDrawStart() {
                isCurrentOptionEdited = true;
            }
        });

        this.fontNameArr = getResources().getStringArray(R.array.editFontName);
        this.arrMainOption = getResources().getStringArray(R.array.editMainOption);

        AppCompatImageView appCompatImageView = (AppCompatImageView) findViewById(R.id.mainUserImage);
        this.mainUserImage = appCompatImageView;
        appCompatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerStickerImgOption.setVisibility(View.GONE);
                int currentSelectedOption = mAdapter.getCurrentSelectedOption();

                if (currentSelectedOption == POS_STICKER) {
                    recyclerStickerCategory.setVisibility(View.VISIBLE);
                } else {
                    int currentSelectedOption2 = mAdapter.getCurrentSelectedOption();

                    if (currentSelectedOption2 == POS_TEXT) {
                        recyclerStickerTextOption.setVisibility(View.VISIBLE);
                    }
                }
                stickerView.setLocked(true);
            }
        });

        stickerView = (StickerView) findViewById(R.id.sticker_view);

        initMainStickerView();

        ivClose = findViewById(R.id.ivClose);
        ivCheckMark = findViewById(R.id.ivCheckMark);

        ivClose.setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });

        ivCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linThirdDivisionOption.getVisibility() == View.VISIBLE) {
                    if (currentSticker == null) {
                        currentSticker = stickerView.getCurrentSticker();
                    }
                    if (currentSticker != null && recyclerFont.getVisibility() == View.VISIBLE) {
                        itemSelectFromList(linThirdDivisionOption, recyclerFont, false);
                    } else if (currentSticker != null && recyclerBubble.getVisibility() == View.VISIBLE) {
                        itemSelectFromList(linThirdDivisionOption, recyclerBubble, false);
                    } else if (recyclerSticker.getVisibility() == View.VISIBLE) {
                        itemSelectFromList(linThirdDivisionOption, recyclerSticker, false);
                    }
                }
            }
        });

        linSeekBarView = (LinearLayout) findViewById(R.id.linSeekBarView);
        linSeekBarView.setVisibility(View.GONE);
        linThirdDivisionOption = (LinearLayout) findViewById(R.id.linThirdDivisionOption);
        linThirdDivisionOption.setVisibility(View.GONE);

        linImgStickerView = (LinearLayout) findViewById(R.id.linImgStickerView);
        rel_image = (RelativeLayout) findViewById(R.id.rel_image);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar2, int progress, boolean fromUser) {
                if (recyclerStickerTextOption.getVisibility() == View.VISIBLE) {
                    setBubbleTextSize(progress);
                } else if (recyclerBrushOption.getVisibility() == View.VISIBLE) {

                    if (isBrushOpacity) {
                        drawingView.setOpacity(progress);
                    } else {
                        drawingView.setStrokeWidth(progress);
                    }
                } else {
                    if (isSetProgrammmatically) {
                        isSetProgrammmatically = false;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar2) {
                if (recyclerBrushOption.getVisibility() == View.VISIBLE) {
                    seekbarViewAnimation(false, "");
                }
            }
        });
        this.recyclerMainAppOption = (RecyclerView) findViewById(R.id.recyclerMainAppOption);
        this.recyclerAdjust = (RecyclerView) findViewById(R.id.recyclerAdjust);
        this.recyclerBrushOption = (RecyclerView) findViewById(R.id.recyclerBrushOption);
        this.recyclerStickerTextOption = (RecyclerView) findViewById(R.id.recyclerStickerTextOption);
        this.recyclerStickerImgOption = (RecyclerView) findViewById(R.id.recyclerStickerImgOption);
        this.recyclerFilter = (RecyclerView) findViewById(R.id.recyclerFilter);
        this.recyclerStickerCategory = (RecyclerView) findViewById(R.id.recyclerStickerCategory);
        this.recyclerSticker = (RecyclerView) findViewById(R.id.recyclerSticker);
        this.recyclerFont = (RecyclerView) findViewById(R.id.recyclerFont);
        this.recyclerColor = (RecyclerView) findViewById(R.id.recyclerColor);
        this.recyclerBubble = (RecyclerView) findViewById(R.id.recyclerBubble);

        this.recyclerMainAppOption.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerAdjust.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerBrushOption.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerStickerTextOption.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerStickerImgOption.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerFilter.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerStickerCategory.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerSticker.setLayoutManager(new GridLayoutManager(this, 4));
        this.recyclerFont.setLayoutManager(new GridLayoutManager(this, 4));
        this.recyclerColor.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.recyclerBubble.setLayoutManager(new GridLayoutManager(this, 4));
    }

    private void initMainStickerView() {
        this.stickerView.setLocked(false);
        this.stickerView.setConstrained(true);
        this.stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                removeStickerWithDeleteIcon();
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerTouchedDown(@NonNull final Sticker sticker) {
                if (recyclerBrushOption.getVisibility() == View.VISIBLE) {
                    if (isCurrentOptionEdited) {
                        dialogDiscardBrush2(sticker);
                        return;
                    }
                }
                stickerOptionTaskPerform(sticker);
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
            }
        });
    }


    public void initMainOption() {
        ArrayList<OptionDatasModel> mainOptionDataList = new ArrayList<>();
        int p = 0;
        while (true) {
            String[] strArr = this.arrMainOption;
            if (p < strArr.length) {
                mainOptionDataList.add(new OptionDatasModel(strArr[p], UtilApp.arrMainOptionIcon[p], UtilApp.arrMainOptionIcon[p], true, false, false));
                p++;
            } else {
                AdapterEditOptions adapterEditOptions = new AdapterEditOptions(this, mainOptionDataList, new InterfaceOptionItemClick() {
                    @Override
                    public void onItemClick(final int newPosition) {

                        if (newPosition == 2 || newPosition == 3) {
                            view_.setVisibility(View.VISIBLE);
                        }

                        if (recyclerBrushOption.getVisibility() == View.VISIBLE) {
                            if (isCurrentOptionEdited) {
                                dialogDiscardBrush1(newPosition);
                                return;
                            }
                        }
                        if (recyclerFilter.getVisibility() == View.VISIBLE) {
                            if (isCurrentOptionEdited) {
                                dialogDiscardFilter(newPosition);
                                return;
                            }
                        }
                        if (recyclerStickerTextOption.getVisibility() == View.VISIBLE) {
                            stickerView.setLocked(true);
                            setMainOptionSelection(newPosition);
                        } else if (recyclerStickerImgOption.getVisibility() == View.VISIBLE) {
                            stickerView.setLocked(true);
                            setMainOptionSelection(newPosition);
                        } else {
                            setMainOptionSelection(newPosition);
                        }
                    }
                });
                this.mAdapter = adapterEditOptions;
                this.recyclerMainAppOption.setAdapter(adapterEditOptions);
                return;
            }
        }
    }


    public void initStickerOption() {
        stickerParentList = createStickerParentList();
        AdapterStickersCategory adapterStickersCategory = new AdapterStickersCategory(ActImageEdit.this, stickerParentList, new InterfaceItemClickGallery() {
            @Override
            public void onItemClick(View v, int pos) {
                lastTimeInertedSticker = 0;
                hideAllThirdLevelSubOption();
                String zipName = getZipName(pos);
                loadStaticSticker(zipName);
                stickerArrayList.clear();
                stickerArrayList = loadStickerStorage();

                itemSelectFromList(linThirdDivisionOption, recyclerSticker, true);
                adapterStickersFile = new AdapterStickersFile(ActImageEdit.this, stickerArrayList);
                recyclerSticker.setAdapter(adapterStickersFile);

                adapterStickersFile.setClickListener((path, position) -> {

                    itemSelectFromList(linThirdDivisionOption, recyclerSticker, false);
                    lastTimeInertedSticker = lastTimeInertedSticker + 1;

                    Bitmap decodeFile = BitmapFactory.decodeFile(new File(path).getAbsolutePath());
                    Drawable drawable = new BitmapDrawable(getResources(), decodeFile);
                    stickerView.addSticker(new DrawableStickers(drawable));

                });

            }
        });

        recyclerStickerCategory.setAdapter(adapterStickersCategory);
    }

    public ArrayList<String> loadStickerStorage() {
        ArrayList<String> stickerArrayList = new ArrayList<>();
        stickerArrayList.clear();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UtilUnzipFile.getStickerPath(this));
        File file = new File(stringBuilder.toString());
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                if (!file2.getAbsolutePath().contains("thumb")) {
                    stickerArrayList.add(file2.getAbsolutePath());
                }
            }
        }
        return stickerArrayList;
    }

    private String getZipName(int pos) {
        String zipName = null;
        if (pos == 0) {
            zipName = "sticker_baby.zip";
        } else if (pos == 1) {
            zipName = "sticker_birthday.zip";
        } else if (pos == 2) {
            zipName = "sticker_emoj.zip";
        } else if (pos == 3) {
            zipName = "sticker_fastfood.zip";
        } else if (pos == 4) {
            zipName = "sticker_halloween.zip";
        } else if (pos == 5) {
            zipName = "sticker_love.zip";
        } else if (pos == 6) {
            zipName = "sticker_music.zip";
        } else if (pos == 7) {
            zipName = "sticker_sale.zip";
        } else if (pos == 8) {
            zipName = "sticker_social.zip";
        } else if (pos == 9) {
            zipName = "sticker_transport.zip";
        } else if (pos == 10) {
            zipName = "sticker_travel.zip";
        }
        return zipName;
    }

    private void loadStaticSticker(String zipName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UtilUnzipFile.getStickerPath(this));
        if (new File(stringBuilder.toString()).exists()) {
            for (File file2 : new File(stringBuilder.toString()).listFiles()) {
                if (!file2.getAbsolutePath().contains("thumb")) {
                    file2.delete();
                }
            }
        }
        if (new File(stringBuilder.toString()).exists()) {
            try {
                InputStream open = getAssets().open(zipName);
                stringBuilder = new StringBuilder();
                stringBuilder.append(UtilUnzipFile.getStickerPath(this));
                UtilUnzipFile.unzip(open, stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initStickerTextOption() {
        ArrayList<OptionDatasModel> stickerTextOptionDataList = new ArrayList<>();
        int p = 0;
        while (true) {
            String[] strArr = this.arrStickerTextOptionText;
            if (p < strArr.length) {
                stickerTextOptionDataList.add(new OptionDatasModel(strArr[p], this.arrStickerTextOptionIcon[p], false, false, isOptionContainPosition(p, this.arrStickerTextOptionDeviderPos)));
                p++;
            } else {
                AdapterEditSubOptions stickerTextOptionAdapter = new AdapterEditSubOptions(this, stickerTextOptionDataList, new InterfaceItemClick() {
                    @Override
                    public void onItemClick(View v, int position) {

                        if (currentSticker == null) {
                            currentSticker = stickerView.getCurrentSticker();
                        }
                        Sticker sticker = currentSticker;
                        if (sticker == null) {
                            Toast.makeText(ActImageEdit.this, "Please Click on Sticker", Toast.LENGTH_SHORT).show();
                        } else if (position == 0 && (sticker instanceof BubbleStickers)) {
                            isEditMode = true;
                            dialogAddText(((BubbleStickers) sticker).getText().trim());
                        } else if (position == 1) {
                            hideAllThirdLevelSubOption();
                            recyclerViewMoveFirstPosition(recyclerFont, 0);
                            itemSelectFromList(linThirdDivisionOption, recyclerFont, true);
                        } else if (position == 2) {
                            stickerView.flip(sticker, StickerView.FLIP_HORIZONTALLY);
                        } else if (position == 3) {
                            stickerView.flip(sticker, StickerView.FLIP_VERTICALLY);
                        } else if (position == 4) {
                            removeStickerWithDeleteIcon();
                        }
                    }
                });
                this.recyclerStickerTextOption.setAdapter(stickerTextOptionAdapter);
                return;
            }
        }
    }

    public void initStickerImgOption() {
        ArrayList<OptionDatasModel> stickerImgOptionDataList = new ArrayList<>();
        int p = 0;
        while (true) {
            String[] strArr = this.arrStickerImgOptionText;
            if (p < strArr.length) {
                stickerImgOptionDataList.add(new OptionDatasModel(strArr[p], this.arrStickerImgOptionIcon[p], false, false, isOptionContainPosition(p, this.arrStickerImgOptionDeviderPos)));
                p++;
            } else {
                AdapterEditSubOptions stickerImgOptionAdapter = new AdapterEditSubOptions(this, stickerImgOptionDataList, new InterfaceItemClick() {
                    @Override
                    public void onItemClick(View v, int position) {

                        if (currentSticker == null) {
                            currentSticker = stickerView.getCurrentSticker();
                        }

                        Sticker sticker = currentSticker;
                        if (sticker == null) {
                            Toast.makeText(ActImageEdit.this, "Please Click on Sticker", Toast.LENGTH_SHORT).show();
                        } else if (position == 0) {
                            stickerView.flip(sticker, StickerView.FLIP_HORIZONTALLY);
                        } else if (position == 1) {
                            stickerView.flip(sticker, StickerView.FLIP_VERTICALLY);
                        } else if (position == 2) {
                            removeStickerWithDeleteIcon();
                        }
                    }
                });
                this.recyclerStickerImgOption.setAdapter(stickerImgOptionAdapter);
                return;
            }
        }
    }

    public void initBrushOption() {
        ArrayList<OptionDatasModel> brushOptionDataList = new ArrayList<>();
        int p = 0;
        while (true) {
            String[] strArr = this.arrBrushOptionText;
            if (p < strArr.length) {
                brushOptionDataList.add(new OptionDatasModel(strArr[p], this.arrBrushOptionIcon[p], isOptionContainPosition(p, this.arrBrushOptionSelectablePos), false, isOptionContainPosition(p, this.arrBrushOptionDeviderPos)));
                p++;
            } else {
                brushOptionDataList.get(0).setSelected(true);
                AdapterEditSubOptions brushOptionAdapter = new AdapterEditSubOptions(this, brushOptionDataList, new InterfaceItemClick() {
                    @Override
                    public void onItemClick(View v, int position) {
                        if (position == 0) {
                            drawingView.change2Brush();
                        } else if (position == 1) {
                            drawingView.change2Eraser();
                        } else if (position == 2) {
                            isBrushOpacity = false;
                            seekbarViewAnimation(true, getString(R.string.str_180));
                        } else if (position == 3) {
                            isBrushOpacity = true;
                            seekbarViewAnimation(true, getString(R.string.str_179));
                        } else if (position == 4) {
                            drawingView.undo();
                        } else if (position == 5) {
                            drawingView.redo();
                        }
                    }
                });
                this.recyclerBrushOption.setAdapter(brushOptionAdapter);
                return;
            }
        }
    }

    public void initColorOption() {
        AdapterColors adapterColors = new AdapterColors(this);
        this.adapterColors = adapterColors;
        adapterColors.setClickListener(new AdapterColors.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                if (isBrushColorSelection) {
                    drawingView.setColor(Color.parseColor(UtilApp.myColorList[position]));
                    return;
                }
                if (currentSticker == null) {
                    currentSticker = stickerView.getCurrentSticker();
                }
                Sticker sticker = currentSticker;
                if (sticker instanceof BubbleStickers) {
                    ((BubbleStickers) sticker).setCurrentFontColorPosition(position);

                    Bitmap bmpSticker = generateDynamicStickerBitmap(((BubbleStickers) currentSticker).getText(), ((BubbleStickers) currentSticker).getTextSize(), ((BubbleStickers) currentSticker).getCurrBubblePosition(), position, ((BubbleStickers) currentSticker).getCurrentFontPosition());
                    Drawable d = new BitmapDrawable(getResources(), bmpSticker);
                    ((BubbleStickers) currentSticker).setDrawable(d);
                    stickerView.replace(currentSticker);
                    stickerView.invalidate();
                }
            }
        });
        this.recyclerColor.setAdapter(this.adapterColors);
    }

    public void initFontOption() {
        this.typeface = new Typeface[this.fontNameArr.length];
        int tf = 0;
        while (true) {
            String[] strArr = this.fontNameArr;
            if (tf < strArr.length) {
                this.typeface[tf] = UtilApp.getTypefaceFromAsset(this, strArr[tf]);
                tf++;
            } else {
                AdapterFonts adapterFonts = new AdapterFonts(this, this.typeface);
                this.adapterFonts = adapterFonts;
                adapterFonts.setClickListener(new AdapterFonts.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if (currentSticker == null) {
                            currentSticker = stickerView.getCurrentSticker();
                        }
                        Sticker sticker = currentSticker;
                        if (sticker instanceof BubbleStickers) {
                            ((BubbleStickers) sticker).setCurrentFontPosition(position);

                            Bitmap bmpSticker = generateDynamicStickerBitmap(((BubbleStickers) currentSticker).getText(), ((BubbleStickers) currentSticker).getTextSize(), ((BubbleStickers) currentSticker).getCurrBubblePosition(), ((BubbleStickers) currentSticker).getCurrentFontColorPosition(), position);
                            Drawable d = new BitmapDrawable(getResources(), bmpSticker);
                            ((BubbleStickers) currentSticker).setDrawable(d);

                            stickerView.replace(currentSticker);
                            stickerView.invalidate();

                            itemSelectFromList(linThirdDivisionOption, recyclerFont, false);
                        }
                    }
                });
                this.recyclerFont.setAdapter(this.adapterFonts);
                return;
            }
        }
    }

    public void stickerOptionTaskPerform(Sticker sticker) {
        this.lastTimeInertedSticker = 0;
        this.stickerView.setLocked(false);
        this.currentSticker = sticker;
        StickerView stickerView = this.stickerView;
        stickerView.sendToLayer(stickerView.getStickerPosition(sticker));
        Sticker sticker2 = this.currentSticker;
        if (sticker2 instanceof DrawableStickers) {
            hideAllOptionRecycler(true, true);
            recyclerViewMoveFirstPosition(this.recyclerMainAppOption, this.POS_STICKER);
            this.recyclerStickerImgOption.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tvText)).setText(getString(R.string.str_170));
            this.mAdapter.notifySelection(this.POS_STICKER);
        } else if (sticker2 instanceof BubbleStickers) {
            hideAllOptionRecycler(false, false);
            recyclerViewMoveFirstPosition(this.recyclerColor, 0);
            if (this.linSeekBarView.getVisibility() != View.VISIBLE) {
                seekbarViewAnimation(true, getString(R.string.str_171));
            }
            if (this.recyclerColor.getVisibility() != View.VISIBLE) {
                colorSelectionAnimation(true);
            }
            recyclerViewMoveFirstPosition(this.recyclerMainAppOption, this.POS_TEXT);
            this.recyclerStickerTextOption.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tvText)).setText(getString(R.string.str_171));
            this.mAdapter.notifySelection(this.POS_TEXT);
        }
    }

    public void recyclerViewMoveFirstPosition(RecyclerView recyclerView, int position) {
        recyclerView.scrollToPosition(position);
    }

    public void setBubbleTextSize(int textSize) {
        Sticker sticker = this.currentSticker;
        if (sticker instanceof BubbleStickers) {
            ((BubbleStickers) sticker).setTextSize(textSize * 5);
            Bitmap bmpSticker = generateDynamicStickerBitmap(((BubbleStickers) this.currentSticker).getText(), ((BubbleStickers) this.currentSticker).getTextSize(), ((BubbleStickers) this.currentSticker).getCurrBubblePosition(), ((BubbleStickers) this.currentSticker).getCurrentFontColorPosition(), ((BubbleStickers) this.currentSticker).getCurrentFontPosition());
            Drawable d = new BitmapDrawable(getResources(), bmpSticker);
            ((BubbleStickers) this.currentSticker).setDrawable(d);
            this.stickerView.replace(this.currentSticker);
            this.stickerView.invalidate();
        }
    }

    public void hideAllOptionRecycler(boolean hideAllWithSeekbar, boolean hideAllWithColorBar) {
        this.isBrushColorSelection = false;
        if (hideAllWithSeekbar) {
            this.linSeekBarView.setVisibility(View.GONE);
        }
        this.drawingView.setVisibility(View.GONE);
        this.drawingView.clearDrawingBoard();
        this.recyclerAdjust.setVisibility(View.GONE);
        this.recyclerStickerTextOption.setVisibility(View.GONE);
        this.recyclerStickerImgOption.setVisibility(View.GONE);
        this.recyclerBrushOption.setVisibility(View.GONE);
        this.recyclerFilter.setVisibility(View.GONE);
        if (hideAllWithColorBar) {
            this.recyclerColor.setVisibility(View.GONE);
        }
        this.recyclerStickerCategory.setVisibility(View.GONE);
    }

    public void hideAllThirdLevelSubOption() {
        this.recyclerFont.setVisibility(View.GONE);
        this.recyclerBubble.setVisibility(View.GONE);
        this.recyclerSticker.setVisibility(View.GONE);
    }

    public void setMainOptionSelection(int newPosition) {
        this.isCurrentOptionEdited = false;
        if (newPosition != this.POS_CROP_ROTATE) {
            this.mAdapter.notifySelection(newPosition);
        }
        getImage();
        Bitmap bitmap = this.originalbitmap;
        if (bitmap != null) {
            if (newPosition == this.POS_CROP_ROTATE) {
                Uri crpUri = getImageUri(bitmap);
                if (crpUri == null) {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                } else {
                    startCropActivity(crpUri);
                }
            } else if (newPosition == this.POS_BRUSH) {
                hideAllOptionRecycler(true, true);
                recyclerViewMoveFirstPosition(this.recyclerBrushOption, 0);
                this.isBrushColorSelection = true;
                if (this.recyclerColor.getVisibility() != View.VISIBLE) {
                    colorSelectionAnimation(true);
                }
                this.recyclerBrushOption.setVisibility(View.VISIBLE);
                this.drawingView.setVisibility(View.VISIBLE);
                this.drawingView.change2Brush();
            } else if (newPosition == this.POS_TEXT) {
                this.oldSelectedOption = this.mAdapter.getCurrentSelectedOption();
                dialogAddText("");
            } else if (newPosition == this.POS_STICKER) {
                hideAllOptionRecycler(true, true);
                recyclerViewMoveFirstPosition(this.recyclerStickerCategory, 0);
                this.recyclerStickerCategory.setVisibility(View.VISIBLE);
            }
        }
    }

    private void startCropActivity(Uri imageUri) {
        UCrop.Options options = new UCrop.Options();
        UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), "cropImage_" + System.currentTimeMillis() + ".png")))
                .withOptions(options)
                .start(this);
    }

    public void removeStickerWithDeleteIcon() {
        this.stickerView.remove(this.currentSticker);
        this.currentSticker = null;
        if (this.stickerView.getStickerCount() == 0) {
            hideAllOptionRecycler(true, true);
            recyclerViewMoveFirstPosition(this.recyclerMainAppOption, this.POS_DEFAULT);
            setMainOptionSelection(this.POS_DEFAULT);
            return;
        }
        this.currentSticker = this.stickerView.getLastSticker();
    }

    private boolean isOptionContainPosition(int position, Integer[] arrOptionDeviderPos) {
        List<Integer> intList = Arrays.asList(arrOptionDeviderPos);
        return intList.contains(Integer.valueOf(position));
    }

    private ArrayList<StickerItemParentModelMan> createStickerParentList() {
        stickerListTitle = new String[]{"Baby", "Birthday", "Emoji", "Food", "Halloween", "Love", "Music", "Sale", "Social", "Transport", "Travel"};
        stickerListIcon = new Integer[]{R.drawable.icon_sticker_category_1, R.drawable.icon_sticker_category_2, R.drawable.icon_sticker_category_3, R.drawable.icon_sticker_category_4, R.drawable.icon_sticker_category_5, R.drawable.icon_sticker_category_6, R.drawable.icon_sticker_category_7, R.drawable.icon_sticker_category_8, R.drawable.icon_sticker_category_9, R.drawable.icon_sticker_category_10, R.drawable.icon_sticker_category_11};

        ArrayList<StickerItemParentModelMan> stickerParentList = new ArrayList<>();

        for (int k = 0; k < stickerListIcon.length; k++) {
            StickerItemParentModelMan stickerParentMode3 = new StickerItemParentModelMan();
            stickerParentMode3.setParentIcon(getResources().getDrawable(stickerListIcon[k]));
            stickerParentMode3.setParentText(stickerListTitle[k]);
            stickerParentList.add(stickerParentMode3);
        }

        return stickerParentList;
    }

    private int getDetermineMaxTextSize(String strText, float maxWidth) {
        int size = 0;
        Paint paint = new Paint();
        String[] strMsgLine = strText.split("\n");
        int maximumCharLinePos = 0;
        for (int i = 0; i < strMsgLine.length; i++) {
            if (strMsgLine[i].length() > strMsgLine[maximumCharLinePos].length()) {
                maximumCharLinePos = i;
            }
        }
        do {
            size++;
            paint.setTextSize(size);
        } while (paint.measureText(strMsgLine[maximumCharLinePos]) < maxWidth);
        return size;
    }

    public Bitmap generateDynamicStickerBitmap(String strText, int textSize, int bubblePosition, int colorPosition, int fontPosition) {

        Bitmap bitmap;
        String selecteFont;
        bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Rect rectangle = new Rect(58, 27, 192, 263);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        if (colorPosition != -1) {
            paint.setColor(Color.parseColor(UtilApp.myColorList[colorPosition]));
        } else {
            paint.setColor(Color.parseColor("#FFFFFF"));
        }

        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        if (fontPosition != -1) {
            selecteFont = this.fontNameArr[fontPosition];
        } else {
            selecteFont = "";
        }
        if (!selecteFont.isEmpty()) {
            Typeface typeface = UtilApp.getTypefaceFromAsset(this, selecteFont);
            paint.setTypeface(typeface);
        }
        String[] strMsgLine = strText.split("\n");
        float textHeight = getTextHeight(strText, paint);

        if (strMsgLine.length == 1) {
            canvas.drawText(strText, rectangle.exactCenterX() * 2.0f, rectangle.exactCenterY() * 2.0f, paint);
        } else if (strMsgLine.length == 2) {
            float p = (textHeight / 3.0f) * 2.0f;
            float line1 = (rectangle.exactCenterY() * 2.0f) - p;
            float line2 = (rectangle.exactCenterY() * 2.0f) + p;
            canvas.drawText(strMsgLine[0], rectangle.exactCenterX() * 2.0f, line1, paint);
            canvas.drawText(strMsgLine[1], rectangle.exactCenterX() * 2.0f, line2, paint);
        } else {
            float p2 = (textHeight / 3.0f) * 4.0f;
            float line12 = (rectangle.exactCenterY() * 2.0f) - p2;
            float line22 = rectangle.exactCenterY() * 2.0f;
            float line3 = (rectangle.exactCenterY() * 2.0f) + p2;
            canvas.drawText(strMsgLine[0], rectangle.exactCenterX() * 2.0f, line12, paint);
            canvas.drawText(strMsgLine[1], rectangle.exactCenterX() * 2.0f, line22, paint);
            canvas.drawText(strMsgLine[2], rectangle.exactCenterX() * 2.0f, line3, paint);
        }
        return bitmap;
    }

    private float getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    public void checkBitmap(String selectedImagePath) {
        try {
            this.bm = UtilVaultFile.decodeFile(selectedImagePath);
            ViewTreeObserver vto = this.linImgStickerView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    linImgStickerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    screenImgEditorWidth = linImgStickerView.getMeasuredWidth();
                    screenImgEditorHeight = linImgStickerView.getMeasuredHeight();
                    Bitmap bitmap = bm;
                    if (bitmap != null) {
                        manageBitmapWiseView(bitmap);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getImage() {
        BitmapDrawable originaldraw = (BitmapDrawable) this.mainUserImage.getDrawable();
        if (originaldraw != null) {
            this.originalbitmap = originaldraw.getBitmap();
        }
    }

    public void manageBitmapWiseView(Bitmap bitmap) {
        int sWidth;
        int sHeight;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (bitmapWidth < bitmapHeight) {
            sWidth = this.screenImgEditorWidth;
            sHeight = (this.screenImgEditorWidth * bitmapHeight) / bitmapWidth;
            int i = this.screenImgEditorHeight;
            if (sHeight > i) {
                sHeight = this.screenImgEditorHeight;
                sWidth = (i * sWidth) / sHeight;
            }
        } else {
            sWidth = this.screenImgEditorWidth;
            sHeight = (this.screenImgEditorWidth * bitmapHeight) / bitmapWidth;
        }
        this.rel_image.getLayoutParams().width = sWidth;
        this.rel_image.getLayoutParams().height = sHeight;
        this.rel_image.requestLayout();
        this.rel_image.setDrawingCacheEnabled(true);
        this.mainUserImage.setImageBitmap(bitmap);
        this.drawingView.setConfig(sWidth, sHeight);

        if (progressDialog != null && progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    public Uri getImageUri(Bitmap inImage) {
        try {
            // Create a temporary file in the app's cache directory
            File tempFile = new File(getCacheDir(), "IMG_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(tempFile);

            // Compress the bitmap to the file
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            // Return the Uri of the temporary file
            return FileProvider.getUriForFile(this, getPackageName() + ".provider", tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getBubbleStickerTextSize() {
        if (this.currentSticker == null) {
            this.currentSticker = this.stickerView.getCurrentSticker();
        }
        ((BubbleStickers) this.currentSticker).getTextSize();
        return ((BubbleStickers) this.currentSticker).getTextSize() / 5;
    }

    public int getDefaultProgress(String selectedOption) {

        if (selectedOption.equalsIgnoreCase(getString(R.string.str_177)) || selectedOption.equalsIgnoreCase(getString(R.string.str_181)) || selectedOption.equalsIgnoreCase(getString(R.string.str_169)) || selectedOption.equalsIgnoreCase(getString(R.string.str_167)) || selectedOption.equalsIgnoreCase(getString(R.string.str_165)) || selectedOption.equalsIgnoreCase(getString(R.string.str_173))) {
            setSeekBarProgressProgrammatically(0);
            return 50;
        } else if (selectedOption.equalsIgnoreCase(getString(R.string.str_168)) || selectedOption.equalsIgnoreCase(getString(R.string.str_172))) {
            setSeekBarProgressProgrammatically(0);
            return 0;
        } else if (selectedOption.equalsIgnoreCase(getString(R.string.str_176))) {
            setSeekBarProgressProgrammatically(0);
            return 0;
        } else if (selectedOption.equalsIgnoreCase(getString(R.string.str_181))) {
            int progress = this.drawingView.getStrokeWidth();
            return progress;
        } else if (!selectedOption.equalsIgnoreCase(getString(R.string.str_179))) {
            return 0;
        } else {
            int progress2 = this.drawingView.getOpacity();
            return progress2;
        }
    }

    public void setSeekBarProgressProgrammatically(int progress) {
        this.isSetProgrammmatically = true;
        this.seekBar.setProgress(progress);
    }

    public void seekbarViewAnimation(boolean upAnimation, String selectedOption) {
        if (upAnimation) {
            this.linSeekBarView.setVisibility(View.VISIBLE);
            setSeekBarProgressProgrammatically(getDefaultProgress(selectedOption));
            Animation loadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_up);
            this.slideUpAnimation = loadAnimation;
            this.linSeekBarView.startAnimation(loadAnimation);
            ((TextView) findViewById(R.id.tvText)).setText(selectedOption);
            return;
        }
        Animation loadAnimation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_down_edit);
        this.slideDownAnimation = loadAnimation2;
        this.linSeekBarView.startAnimation(loadAnimation2);
        this.slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linSeekBarView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void saveImage() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
        }
        progressDialog.setMessage(getResources().getString(R.string.str_166));
        progressDialog.setCancelable(false);
        new saveImageTask().execute(new String[0]);
    }

    public void dialogAddText(final String strText) {
        Dialog dialogDelete = UtilDialog.getDialog(this, R.layout.dialog_add_text_sticker);
        dialogDelete.setCancelable(true);

        TextView addTxt = (TextView) dialogDelete.findViewById(R.id.addTxt);
        TextView cancelTxt = (TextView) dialogDelete.findViewById(R.id.cancelTxt);
        EditText enterNameEt = (EditText) dialogDelete.findViewById(R.id.enterNameEt);

        enterNameEt.setText(strText);

        cancelTxt.setOnClickListener(v -> {
            dialogDelete.dismiss();
        });

        addTxt.setOnClickListener(v -> {
            hideKeyboard(v);

            if (!enterNameEt.getText().toString().trim().isEmpty()) {
                if (isEditMode) {
                    isEditMode = false;
                    int textSize = getDetermineMaxTextSize(enterNameEt.getText().toString().trim(), 450.0f);
                    Bitmap bmpSticker = generateDynamicStickerBitmap(enterNameEt.getText().toString().trim(), textSize, ((BubbleStickers) currentSticker).getCurrBubblePosition(), ((BubbleStickers) currentSticker).getCurrentFontColorPosition(), ((BubbleStickers) currentSticker).getCurrentFontPosition());
                    Drawable d = new BitmapDrawable(getResources(), bmpSticker);
                    ((BubbleStickers) currentSticker).setTextSize(textSize);
                    ((BubbleStickers) currentSticker).setText(enterNameEt.getText().toString().trim());
                    ((BubbleStickers) currentSticker).setDrawable(d);
                    setSeekBarProgressProgrammatically(getBubbleStickerTextSize());
                    stickerView.replace(currentSticker);
                    stickerView.invalidate();

                    dialogDelete.dismiss();
                    return;
                }
                int textSize2 = getDetermineMaxTextSize(enterNameEt.getText().toString().trim(), 450.0f);
                Bitmap bmpSticker2 = generateDynamicStickerBitmap(enterNameEt.getText().toString().trim(), textSize2, 0, -1, -1);
                Drawable d2 = new BitmapDrawable(getResources(), bmpSticker2);

                BubbleStickers bubbleSticker = new BubbleStickers(d2);
                bubbleSticker.setText(enterNameEt.getText().toString().trim());
                bubbleSticker.setTextSize(textSize2);
                stickerView.addSticker(bubbleSticker);
                hideAllOptionRecycler(true, false);
                if (recyclerColor.getVisibility() != View.VISIBLE) {
                    colorSelectionAnimation(true);
                }
                seekbarViewAnimation(true, getString(R.string.str_171));
                recyclerViewMoveFirstPosition(recyclerStickerTextOption, 0);
                recyclerStickerTextOption.setVisibility(View.VISIBLE);

                dialogDelete.dismiss();
            } else {
                Toast.makeText(this, "Please Enter Value", Toast.LENGTH_SHORT).show();
            }
        });

        dialogDelete.show();
    }

    public void itemSelectFromList(final LinearLayout linLayout, final RecyclerView recyclerView, boolean upAnimation) {
        recyclerView.setVisibility(View.VISIBLE);
        if (upAnimation) {
            linLayout.setVisibility(View.VISIBLE);
            Animation loadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_up);
            this.slideUpAnimation = loadAnimation;
            linLayout.startAnimation(loadAnimation);
            return;
        }
        Animation loadAnimation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_down_edit);
        this.slideDownAnimation = loadAnimation2;
        linLayout.startAnimation(loadAnimation2);
        this.slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivColorPreview:
            case R.id.ivDummy:
            default:
                return;
        }
    }

    public void applyBrushAndSave(Bitmap brushBitmap) {
        getImage();
        Bitmap mutableBitmap = brushBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap resizedMutableBitmap = Bitmap.createScaledBitmap(mutableBitmap, this.originalbitmap.getWidth(), this.originalbitmap.getHeight(), false);
        Bitmap bitmapResult = getBitmapOverlay(this.originalbitmap, resizedMutableBitmap);
        this.mainUserImage.setImageBitmap(bitmapResult);
    }

    private Bitmap getBitmapOverlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(bmp2, 0.0f, 0.0f, (Paint) null);
        return bmOverlay;
    }

    public void colorSelectionAnimation(boolean upAnimation) {
        if (upAnimation) {
            recyclerViewMoveFirstPosition(this.recyclerColor, 0);
            this.recyclerColor.setVisibility(View.VISIBLE);
            return;
        }
        recyclerColor.setVisibility(View.GONE);
    }

    private void initUi() {
        view_ = findViewById(R.id.view_);

        findViewById(R.id.saveTxt).setOnClickListener(v -> {
            saveImage();
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(getApplicationContext()).clearMemory();
        Glide.get(getApplicationContext()).trimMemory(80);
        System.gc();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK && resultData != null) {
            Uri croppedImageUri = UCrop.getOutput(resultData);
            if (croppedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedImageUri);
                    this.originalbitmap = bitmap;
                    if (bitmap != null) {
                        manageBitmapWiseView(bitmap);
                    } else {
                        Toast.makeText(this, "Sorry for inconvenience, Try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class saveImageTask extends AsyncTask<String, String, Exception> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            stickerView.setLocked(true);

            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        public Exception doInBackground(String... strings) {
            getImage();
            if (originalbitmap != null) {
                rel_image.setDrawingCacheEnabled(true);
                rel_image.buildDrawingCache();
                Bitmap bitmap = rel_image.getDrawingCache();

                String fileName = "GalleryEdit_" + System.currentTimeMillis() + ".png";

                File editForlder = new File(UtilApp.editFilePath);
                if (!editForlder.exists()) {
                    editForlder.mkdirs();
                }

                File file = new File(editForlder, fileName);
                oldSavedFileName = fileName;
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();

                    finalSavedImagePath = file.getPath();

                    MediaScannerConnection.scanFile(ActImageEdit.this, new String[]{new File(finalSavedImagePath).getAbsolutePath()}, null, null);

                    return null;
                } catch (Exception e) {
                    return e;
                }
            }
            return new Exception("Image not Detacted");
        }


        @Override
        public void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (isFinishing() || isDestroyed()) {
                return;
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (e != null) {
                Toast.makeText(ActImageEdit.this, e.toString(), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(ActImageEdit.this, getString(R.string.str_190), Toast.LENGTH_SHORT).show();

            InterstitialAds.ShowInterstitial(ActImageEdit.this, () -> {
                Intent intent = new Intent(ActImageEdit.this, ActImageShare.class);
                intent.putExtra("finalSavedImagePath", finalSavedImagePath);
                startActivity(intent);
                finish();
            });
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
            InterstitialAds.ShowInterstitialBack(ActImageEdit.this, () -> {
                finish();
            });
        });
    }

    private void dialogDiscardFilter(final int newPosition) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_discard_changes, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        TextView messageTxt = bottomSheetView.findViewById(R.id.messageTxt);
        TextView discardTxt = bottomSheetView.findViewById(R.id.discardTxt);
        TextView cancelTxt = bottomSheetView.findViewById(R.id.cancelTxt);

        messageTxt.setText(getString(R.string.str_193) + " " + getResources().getString(R.string.str_164) + " " + getString(R.string.str_194));
        discardTxt.setText(getResources().getString(R.string.str_174));

        cancelTxt.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            mainUserImage.setImageBitmap(originalbitmap);
            setMainOptionSelection(newPosition);
        });

        discardTxt.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            setMainOptionSelection(newPosition);
        });
    }

    private void dialogDiscardBrush1(int newPosition) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_discard_changes, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        TextView messageTxt = bottomSheetView.findViewById(R.id.messageTxt);
        TextView discardTxt = bottomSheetView.findViewById(R.id.discardTxt);
        TextView cancelTxt = bottomSheetView.findViewById(R.id.cancelTxt);

        messageTxt.setText(getString(R.string.str_193) + " " + getResources().getString(R.string.str_178) + " " + getString(R.string.str_194));
        discardTxt.setText(getResources().getString(R.string.str_174));

        cancelTxt.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            setMainOptionSelection(newPosition);
        });

        discardTxt.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            applyBrushAndSave(drawingView.getViewBitmap());
            setMainOptionSelection(newPosition);
        });
    }

    private void dialogDiscardBrush2(Sticker sticker) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_b_discard_changes, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        TextView messageTxt = bottomSheetView.findViewById(R.id.messageTxt);
        TextView discardTxt = bottomSheetView.findViewById(R.id.discardTxt);
        TextView cancelTxt = bottomSheetView.findViewById(R.id.cancelTxt);

        messageTxt.setText(getString(R.string.str_193) + " " + getResources().getString(R.string.str_178) + " " + getString(R.string.str_194));
        discardTxt.setText(getResources().getString(R.string.str_174));

        cancelTxt.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            stickerOptionTaskPerform(sticker);
        });

        discardTxt.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            applyBrushAndSave(drawingView.getViewBitmap());
            stickerOptionTaskPerform(sticker);
        });
    }

    private void setOnBackPressed() {
        backPressed = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (linThirdDivisionOption.getVisibility() == View.VISIBLE) {
                    if (currentSticker == null) {
                        currentSticker = stickerView.getCurrentSticker();
                    }
                    if (currentSticker != null && recyclerFont.getVisibility() == View.VISIBLE) {
                        int previousFontPosition = ((BubbleStickers) currentSticker).getCurrentFontPosition();
                        Sticker sticker = currentSticker;
                        if (sticker instanceof BubbleStickers) {
                            Bitmap bmpSticker = generateDynamicStickerBitmap(((BubbleStickers) sticker).getText(), ((BubbleStickers) currentSticker).getTextSize(), ((BubbleStickers) currentSticker).getCurrBubblePosition(), ((BubbleStickers) currentSticker).getCurrentFontColorPosition(), previousFontPosition);
                            Drawable d = new BitmapDrawable(getResources(), bmpSticker);
                            ((BubbleStickers) currentSticker).setDrawable(d);
                            stickerView.replace(currentSticker);
                            stickerView.invalidate();
                        }
                        itemSelectFromList(linThirdDivisionOption, recyclerFont, false);
                    } else if (currentSticker != null && recyclerBubble.getVisibility() == View.VISIBLE) {
                        int previousBubbleWiseTxtSize = ((BubbleStickers) currentSticker).getTextSize();
                        int previousBubblePosition = ((BubbleStickers) currentSticker).getCurrBubblePosition();
                        Sticker sticker2 = currentSticker;
                        if (sticker2 instanceof BubbleStickers) {
                            Bitmap bmpSticker2 = generateDynamicStickerBitmap(((BubbleStickers) sticker2).getText(), previousBubbleWiseTxtSize, previousBubblePosition, ((BubbleStickers) currentSticker).getCurrentFontColorPosition(), ((BubbleStickers) currentSticker).getCurrentFontPosition());
                            Drawable d2 = new BitmapDrawable(getResources(), bmpSticker2);
                            ((BubbleStickers) currentSticker).setDrawable(d2);
                            stickerView.replace(currentSticker);
                            stickerView.invalidate();
                        }
                        itemSelectFromList(linThirdDivisionOption, recyclerBubble, false);
                    } else if (recyclerSticker.getVisibility() == View.VISIBLE) {
                        for (int i = 0; i < lastTimeInertedSticker; i++) {
                            Sticker sticker3 = stickerView.getLastSticker();
                            stickerView.remove(sticker3);
                        }
                        itemSelectFromList(linThirdDivisionOption, recyclerSticker, false);
                    }
                } else {
                    dialogDiscardImg();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressed);

        findViewById(R.id.back_img).setOnClickListener(v -> {
            backPressed.handleOnBackPressed();
        });
    }
}
