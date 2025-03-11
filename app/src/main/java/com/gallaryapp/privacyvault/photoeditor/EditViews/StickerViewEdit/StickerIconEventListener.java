package com.gallaryapp.privacyvault.photoeditor.EditViews.StickerViewEdit;

import android.view.MotionEvent;

public interface StickerIconEventListener {
    void onActionDown(StickerView stickerView, MotionEvent event);

    void onActionMove(StickerView stickerView, MotionEvent event);

    void onActionUp(StickerView stickerView, MotionEvent event);
}
