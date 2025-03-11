package com.gallaryapp.privacyvault.photoeditor.EditViews.StickerViewEdit;

import android.view.MotionEvent;

public class DeleteIconEvents implements StickerIconEventListener {

    @Override
    public void onActionDown(StickerView stickerView, MotionEvent event) {
    }

    @Override
    public void onActionMove(StickerView stickerView, MotionEvent event) {
    }

    @Override
    public void onActionUp(StickerView stickerView, MotionEvent event) {
        stickerView.removeCurrentSticker();
    }
}
