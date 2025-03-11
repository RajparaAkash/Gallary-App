package com.gallaryapp.privacyvault.photoeditor.EditViews.StickerViewEdit;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BubbleStickers extends Sticker {

    private final Rect realBounds;
    private Drawable drawable;
    private String text;
    private int textSize = 50;
    private int currentFontPosition = -1, currentFontColorPosition = -1, currBubblePosition = 0;

    public BubbleStickers(Drawable drawable) {
        this.drawable = drawable;
        realBounds = new Rect(0, 0, getWidth(), getHeight());
    }

    public int getCurrentFontColorPosition() {
        return currentFontColorPosition;
    }

    public void setCurrentFontColorPosition(int currentFontColorPosition) {
        this.currentFontColorPosition = currentFontColorPosition;
    }

    public int getCurrentFontPosition() {
        return currentFontPosition;
    }

    public void setCurrentFontPosition(int currentFontPosition) {
        this.currentFontPosition = currentFontPosition;
    }

    @Nullable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Nullable
    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    @Nullable
    public int getCurrBubblePosition() {
        return currBubblePosition;
    }

    public void setCurrBubblePosition(int currBubblePosition) {
        this.currBubblePosition = currBubblePosition;
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public BubbleStickers setDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.concat(getMatrix());
        drawable.setBounds(realBounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    @NonNull
    @Override
    public BubbleStickers setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        drawable.setAlpha(alpha);
        return this;
    }

    @Override
    public int getWidth() {
        return drawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return drawable.getIntrinsicHeight();
    }

    @Override
    public void release() {
        super.release();
        if (drawable != null) {
            drawable = null;
        }
    }
}
