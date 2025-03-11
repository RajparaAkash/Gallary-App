package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public void onDoubleTouch() {
    }

    public void onSingleTouch() {
    }

    public OnSwipeTouchListener(Context context) {
        this.gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.gestureDetector.onTouchEvent(motionEvent);
    }

    
    public final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        public GestureListener() {
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return super.onFling(motionEvent, motionEvent2, f, f2);
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            OnSwipeTouchListener.this.onDoubleTouch();
            return super.onDoubleTap(motionEvent);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            OnSwipeTouchListener.this.onSingleTouch();
            return super.onSingleTapConfirmed(motionEvent);
        }
    }
}
