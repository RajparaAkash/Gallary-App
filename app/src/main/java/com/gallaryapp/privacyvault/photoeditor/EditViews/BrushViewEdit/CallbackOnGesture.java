package com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit;

import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class CallbackOnGesture implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {

    private final RectF currentViewport = new RectF();
    private final RectF canvasRect = new RectF();
    private final float minZoom = 1.0f;
    private final float maxZoom = 5.0f;
    private float scaleFactor = 1.0f;
    private int canvasWidth = 0;
    private int canvasHeight = 0;

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
        if (motionEvent1.getPointerCount() == 2) {
            float viewportOffsetX = (this.currentViewport.width() * distanceX) / this.canvasRect.width();
            float viewportOffsetY = (this.currentViewport.height() * distanceY) / this.canvasRect.height();
            RectF rectF = this.currentViewport;
            setViewportBottomLeft(rectF.left + viewportOffsetX, rectF.bottom + viewportOffsetY);
            return true;
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    private void setViewportBottomLeft(float x, float y) {
        float viewWidth = this.currentViewport.width();
        float viewHeight = this.currentViewport.height();
        float left = Math.max(0.0f, Math.min(x, this.canvasRect.width() - viewWidth));
        float bottom = Math.max(0.0f + viewHeight, Math.min(y, this.canvasRect.height()));
        float top = bottom - viewHeight;
        float right = left + viewWidth;
        this.currentViewport.set(left, top, right, bottom);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float scaleFactor = this.scaleFactor * scaleGestureDetector.getScaleFactor();
        this.scaleFactor = scaleFactor;
        float max = Math.max(1.0f, Math.min(scaleFactor, 5.0f));
        this.scaleFactor = max;
        RectF rectF = this.canvasRect;
        rectF.right = this.canvasWidth * max;
        rectF.bottom = this.canvasHeight * max;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
    }

    public RectF getCurrentViewport() {
        return this.currentViewport;
    }

    public RectF getCanvasRect() {
        return this.canvasRect;
    }

    public float getScaleFactor() {
        return this.scaleFactor;
    }

    public void setCanvasBounds(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        RectF rectF = this.canvasRect;
        rectF.right = canvasWidth;
        rectF.bottom = canvasHeight;
    }

    public void setViewBounds(int viewWidth, int viewHeight) {
        RectF rectF = this.currentViewport;
        rectF.right = viewWidth;
        rectF.bottom = viewHeight;
    }
}
