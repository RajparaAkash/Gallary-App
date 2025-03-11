package com.gallaryapp.privacyvault.photoeditor.EditViews.StickerViewEdit;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BitmapStickersIcon extends DrawableStickers implements StickerIconEventListener {

    public static final float DEFAULT_ICON_RADIUS = 30f;
    public static final float DEFAULT_ICON_EXTRA_RADIUS = 10f;
    public static final int LEFT_TOP = 0;
    public static final int RIGHT_TOP = 1;
    public static final int LEFT_BOTTOM = 2;
    public static final int RIGHT_BOTOM = 3;
    private float iconRadius = DEFAULT_ICON_RADIUS;
    private float iconExtraRadius = DEFAULT_ICON_EXTRA_RADIUS;
    private float x;
    private float y;
    @Gravity
    private int position = LEFT_TOP;
    private StickerIconEventListener iconEvent;

    public BitmapStickersIcon(Drawable drawable, @Gravity int gravity) {
        super(drawable);
        this.position = gravity;
    }

    public void draw(Canvas canvas, Paint paint) {
        super.draw(canvas);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getIconRadius() {
        return iconRadius;
    }

    public void setIconRadius(float iconRadius) {
        this.iconRadius = iconRadius;
    }

    public float getIconExtraRadius() {
        return iconExtraRadius;
    }

    public void setIconExtraRadius(float iconExtraRadius) {
        this.iconExtraRadius = iconExtraRadius;
    }

    @Override
    public void onActionDown(StickerView stickerView, MotionEvent event) {
        if (iconEvent != null) {
            iconEvent.onActionDown(stickerView, event);
        }
    }

    @Override
    public void onActionMove(StickerView stickerView, MotionEvent event) {
        if (iconEvent != null) {
            iconEvent.onActionMove(stickerView, event);
        }
    }

    @Override
    public void onActionUp(StickerView stickerView, MotionEvent event) {
        if (iconEvent != null) {
            iconEvent.onActionUp(stickerView, event);
        }
    }

    public StickerIconEventListener getIconEvent() {
        return iconEvent;
    }

    public void setIconEvent(StickerIconEventListener iconEvent) {
        this.iconEvent = iconEvent;
    }

    @Gravity
    public int getPosition() {
        return position;
    }

    public void setPosition(@Gravity int position) {
        this.position = position;
    }

    @IntDef({LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Gravity {

    }
}
