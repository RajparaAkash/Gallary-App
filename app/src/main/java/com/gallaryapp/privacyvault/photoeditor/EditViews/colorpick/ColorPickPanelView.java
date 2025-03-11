package com.gallaryapp.privacyvault.photoeditor.EditViews.colorpick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ColorPickPanelView extends View {

    private AlphaPatternsDrawable mAlphaPattern;
    private int mBorderColor;
    private Paint mBorderPaint;
    private int mColor;
    private Paint mColorPaint;
    private RectF mColorRect;
    private float mDensity;
    private RectF mDrawingRect;

    public ColorPickPanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDensity = 1.0f;
        this.mBorderColor = -9539986;
        this.mColor = -16777216;
        init();
    }

    private void init() {
        this.mBorderPaint = new Paint();
        this.mColorPaint = new Paint();
        this.mDensity = getContext().getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF rect = this.mColorRect;
        this.mBorderPaint.setColor(this.mBorderColor);
        canvas.drawRect(this.mDrawingRect, this.mBorderPaint);
        AlphaPatternsDrawable alphaPatternsDrawable = this.mAlphaPattern;
        if (alphaPatternsDrawable != null) {
            alphaPatternsDrawable.draw(canvas);
        }
        this.mColorPaint.setColor(this.mColor);
        canvas.drawRect(rect, this.mColorPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        RectF rectF = new RectF();
        this.mDrawingRect = rectF;
        rectF.left = getPaddingLeft();
        this.mDrawingRect.right = w - getPaddingRight();
        this.mDrawingRect.top = getPaddingTop();
        this.mDrawingRect.bottom = h - getPaddingBottom();
        setUpColorRect();
    }

    private void setUpColorRect() {
        RectF dRect = this.mDrawingRect;
        float left = dRect.left + 1.0f;
        float top = dRect.top + 1.0f;
        float bottom = dRect.bottom - 1.0f;
        float right = dRect.right - 1.0f;
        this.mColorRect = new RectF(left, top, right, bottom);
        AlphaPatternsDrawable alphaPatternsDrawable = new AlphaPatternsDrawable((int) (this.mDensity * 5.0f));
        this.mAlphaPattern = alphaPatternsDrawable;
        alphaPatternsDrawable.setBounds(Math.round(this.mColorRect.left), Math.round(this.mColorRect.top), Math.round(this.mColorRect.right), Math.round(this.mColorRect.bottom));
    }

    public int getColor() {
        return this.mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    public int getBorderColor() {
        return this.mBorderColor;
    }

    public void setBorderColor(int color) {
        this.mBorderColor = color;
        invalidate();
    }
}
