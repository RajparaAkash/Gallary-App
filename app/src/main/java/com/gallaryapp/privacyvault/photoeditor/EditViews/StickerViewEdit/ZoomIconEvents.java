package com.gallaryapp.privacyvault.photoeditor.EditViews.StickerViewEdit;

import android.view.MotionEvent;

public class ZoomIconEvents implements StickerIconEventListener {

    @Override
    public void onActionDown(StickerView stickerView, MotionEvent event) {
    }

    @Override
    public void onActionMove(StickerView stickerView, MotionEvent event) {
        stickerView.zoomAndRotateCurrentSticker(event);
    }

    @Override
    public void onActionUp(StickerView stickerView, MotionEvent event) {
        if (stickerView.getOnStickerOperationListener() != null) {
            stickerView.getOnStickerOperationListener()
                    .onStickerZoomFinished(stickerView.getCurrentSticker());
        }
    }
}
