package com.gallaryapp.privacyvault.photoeditor.EditViews.colorpick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickView extends View {

    private float ALPHA_PANEL_HEIGHT;
    private float HUE_PANEL_WIDTH;
    private float PALETTE_CIRCLE_TRACKER_RADIUS;
    private float PANEL_SPACING;
    private float RECTANGLE_TRACKER_OFFSET;
    private int mAlpha;
    private Paint mAlphaPaint;
    private AlphaPatternsDrawable mAlphaPattern;
    private RectF mAlphaRect;
    private Shader mAlphaShader;
    private String mAlphaSliderText;
    private Paint mAlphaTextPaint;
    private int mBorderColor;
    private Paint mBorderPaint;
    private float mDensity;
    private float mDrawingOffset;
    private RectF mDrawingRect;
    private float mHue;
    private Paint mHuePaint;
    private RectF mHueRect;
    private Shader mHueShader;
    private Paint mHueTrackerPaint;
    private int mLastTouchedPanel;
    private OnColorChangedListener mListener;
    private float mSat;
    private Shader mSatShader;
    private Paint mSatValPaint;
    private RectF mSatValRect;
    private Paint mSatValTrackerPaint;
    private boolean mShowAlphaPanel;
    private int mSliderTrackerColor;
    private Point mStartTouchPoint;
    private float mVal;
    private Shader mValShader;


    public interface OnColorChangedListener {
        void onColorChanged(int i);
    }

    public ColorPickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.HUE_PANEL_WIDTH = 30.0f;
        this.ALPHA_PANEL_HEIGHT = 20.0f;
        this.PANEL_SPACING = 10.0f;
        this.PALETTE_CIRCLE_TRACKER_RADIUS = 5.0f;
        this.RECTANGLE_TRACKER_OFFSET = 2.0f;
        this.mDensity = 1.0f;
        this.mAlpha = 255;
        this.mHue = 360.0f;
        this.mSat = 0.0f;
        this.mVal = 0.0f;
        this.mAlphaSliderText = "";
        this.mSliderTrackerColor = -14935012;
        this.mBorderColor = -9539986;
        this.mShowAlphaPanel = false;
        this.mLastTouchedPanel = 0;
        this.mStartTouchPoint = null;
        init();
    }

    private void init() {
        float f = getContext().getResources().getDisplayMetrics().density;
        this.mDensity = f;
        this.PALETTE_CIRCLE_TRACKER_RADIUS *= f;
        this.RECTANGLE_TRACKER_OFFSET *= f;
        this.HUE_PANEL_WIDTH *= f;
        this.ALPHA_PANEL_HEIGHT *= f;
        this.PANEL_SPACING *= f;
        this.mDrawingOffset = calculateRequiredOffset();
        initPaintTools();
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void initPaintTools() {
        this.mSatValPaint = new Paint();
        this.mSatValTrackerPaint = new Paint();
        this.mHuePaint = new Paint();
        this.mHueTrackerPaint = new Paint();
        this.mAlphaPaint = new Paint();
        this.mAlphaTextPaint = new Paint();
        this.mBorderPaint = new Paint();
        this.mSatValTrackerPaint.setStyle(Paint.Style.STROKE);
        this.mSatValTrackerPaint.setStrokeWidth(this.mDensity * 2.0f);
        this.mSatValTrackerPaint.setAntiAlias(true);
        this.mHueTrackerPaint.setColor(this.mSliderTrackerColor);
        this.mHueTrackerPaint.setStyle(Paint.Style.STROKE);
        this.mHueTrackerPaint.setStrokeWidth(this.mDensity * 2.0f);
        this.mHueTrackerPaint.setAntiAlias(true);
        this.mAlphaTextPaint.setColor(-14935012);
        this.mAlphaTextPaint.setTextSize(this.mDensity * 14.0f);
        this.mAlphaTextPaint.setAntiAlias(true);
        this.mAlphaTextPaint.setTextAlign(Paint.Align.CENTER);
        this.mAlphaTextPaint.setFakeBoldText(true);
    }

    private float calculateRequiredOffset() {
        float offset = Math.max(this.PALETTE_CIRCLE_TRACKER_RADIUS, this.RECTANGLE_TRACKER_OFFSET);
        return 1.5f * Math.max(offset, this.mDensity * 1.0f);
    }

    private int[] buildHueColorArray() {
        int[] hue = new int[361];
        int count = 0;
        int i = hue.length - 1;
        while (i >= 0) {
            hue[count] = Color.HSVToColor(new float[]{i, 1.0f, 1.0f});
            i--;
            count++;
        }
        return hue;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.mDrawingRect.width() <= 0.0f || this.mDrawingRect.height() <= 0.0f) {
            return;
        }
        drawSatValPanel(canvas);
        drawHuePanel(canvas);
        drawAlphaPanel(canvas);
    }

    private void drawSatValPanel(Canvas canvas) {
        RectF rect = this.mSatValRect;
        this.mBorderPaint.setColor(this.mBorderColor);
        RectF rectF = this.mDrawingRect;
        canvas.drawRect(rectF.left, rectF.top, rect.right + 1.0f, rect.bottom + 1.0f, this.mBorderPaint);
        if (this.mValShader == null) {
            float f = rect.left;
            this.mValShader = new LinearGradient(f, rect.top, f, rect.bottom, -1, -16777216, Shader.TileMode.CLAMP);
        }
        int rgb = Color.HSVToColor(new float[]{this.mHue, 1.0f, 1.0f});
        float f2 = rect.left;
        float f3 = rect.top;
        this.mSatShader = new LinearGradient(f2, f3, rect.right, f3, -1, rgb, Shader.TileMode.CLAMP);
        ComposeShader mShader = new ComposeShader(this.mValShader, this.mSatShader, PorterDuff.Mode.MULTIPLY);
        this.mSatValPaint.setShader(mShader);
        canvas.drawRect(rect, this.mSatValPaint);
        Point p = satValToPoint(this.mSat, this.mVal);
        this.mSatValTrackerPaint.setColor(-16777216);
        canvas.drawCircle(p.x, p.y, this.PALETTE_CIRCLE_TRACKER_RADIUS - (this.mDensity * 1.0f), this.mSatValTrackerPaint);
        this.mSatValTrackerPaint.setColor(-2236963);
        canvas.drawCircle(p.x, p.y, this.PALETTE_CIRCLE_TRACKER_RADIUS, this.mSatValTrackerPaint);
    }

    private void drawHuePanel(Canvas canvas) {
        RectF rect = this.mHueRect;
        this.mBorderPaint.setColor(this.mBorderColor);
        canvas.drawRect(rect.left - 1.0f, rect.top - 1.0f, rect.right + 1.0f, rect.bottom + 1.0f, this.mBorderPaint);
        if (this.mHueShader == null) {
            float f = rect.left;
            LinearGradient linearGradient = new LinearGradient(f, rect.top, f, rect.bottom, buildHueColorArray(), (float[]) null, Shader.TileMode.CLAMP);
            this.mHueShader = linearGradient;
            this.mHuePaint.setShader(linearGradient);
        }
        canvas.drawRect(rect, this.mHuePaint);
        float rectHeight = (this.mDensity * 4.0f) / 2.0f;
        Point p = hueToPoint(this.mHue);
        RectF r = new RectF();
        float f2 = rect.left;
        float f3 = this.RECTANGLE_TRACKER_OFFSET;
        r.left = f2 - f3;
        r.right = rect.right + f3;
        int i = p.y;
        r.top = i - rectHeight;
        r.bottom = i + rectHeight;
        canvas.drawRoundRect(r, 2.0f, 2.0f, this.mHueTrackerPaint);
    }

    private void drawAlphaPanel(Canvas canvas) {
        if (!this.mShowAlphaPanel || this.mAlphaRect == null || this.mAlphaPattern == null) {
            return;
        }
        RectF rect = this.mAlphaRect;
        this.mBorderPaint.setColor(this.mBorderColor);
        canvas.drawRect(rect.left - 1.0f, rect.top - 1.0f, rect.right + 1.0f, rect.bottom + 1.0f, this.mBorderPaint);
        this.mAlphaPattern.draw(canvas);
        float[] hsv = {this.mHue, this.mSat, this.mVal};
        int color = Color.HSVToColor(hsv);
        int acolor = Color.HSVToColor(0, hsv);
        float f = rect.left;
        float f2 = rect.top;
        LinearGradient linearGradient = new LinearGradient(f, f2, rect.right, f2, color, acolor, Shader.TileMode.CLAMP);
        this.mAlphaShader = linearGradient;
        this.mAlphaPaint.setShader(linearGradient);
        canvas.drawRect(rect, this.mAlphaPaint);
        String str = this.mAlphaSliderText;
        if (str != null && str != "") {
            canvas.drawText(str, rect.centerX(), rect.centerY() + (this.mDensity * 4.0f), this.mAlphaTextPaint);
        }
        float rectWidth = (this.mDensity * 4.0f) / 2.0f;
        Point p = alphaToPoint(this.mAlpha);
        RectF r = new RectF();
        int i = p.x;
        r.left = i - rectWidth;
        r.right = i + rectWidth;
        float f3 = rect.top;
        float f4 = this.RECTANGLE_TRACKER_OFFSET;
        r.top = f3 - f4;
        r.bottom = rect.bottom + f4;
        canvas.drawRoundRect(r, 2.0f, 2.0f, this.mHueTrackerPaint);
    }

    private Point hueToPoint(float hue) {
        RectF rect = this.mHueRect;
        float height = rect.height();
        Point p = new Point();
        p.y = (int) ((height - ((hue * height) / 360.0f)) + rect.top);
        p.x = (int) rect.left;
        return p;
    }

    private Point satValToPoint(float sat, float val) {
        RectF rect = this.mSatValRect;
        float height = rect.height();
        float width = rect.width();
        Point p = new Point();
        p.x = (int) ((sat * width) + rect.left);
        p.y = (int) (((1.0f - val) * height) + rect.top);
        return p;
    }

    private Point alphaToPoint(int alpha) {
        RectF rect = this.mAlphaRect;
        float width = rect.width();
        Point p = new Point();
        p.x = (int) ((width - ((alpha * width) / 255.0f)) + rect.left);
        p.y = (int) rect.top;
        return p;
    }

    private float[] pointToSatVal(float x, float y) {
        float x2;
        float y2;
        RectF rect = this.mSatValRect;
        float[] result = new float[2];
        float width = rect.width();
        float height = rect.height();
        float f = rect.left;
        if (x < f) {
            x2 = 0.0f;
        } else if (x > rect.right) {
            x2 = width;
        } else {
            x2 = x - f;
        }
        float f2 = rect.top;
        if (y < f2) {
            y2 = 0.0f;
        } else if (y > rect.bottom) {
            y2 = height;
        } else {
            y2 = y - f2;
        }
        result[0] = (1.0f / width) * x2;
        result[1] = 1.0f - ((1.0f / height) * y2);
        return result;
    }

    private float pointToHue(float y) {
        float y2;
        RectF rect = this.mHueRect;
        float height = rect.height();
        float f = rect.top;
        if (y < f) {
            y2 = 0.0f;
        } else if (y > rect.bottom) {
            y2 = height;
        } else {
            y2 = y - f;
        }
        return 360.0f - ((y2 * 360.0f) / height);
    }

    private int pointToAlpha(int x) {
        int x2;
        RectF rect = this.mAlphaRect;
        int width = (int) rect.width();
        float f = rect.left;
        if (x < f) {
            x2 = 0;
        } else if (x > rect.right) {
            x2 = width;
        } else {
            x2 = x - ((int) f);
        }
        return 255 - ((x2 * 255) / width);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean update = false;
        if (event.getAction() == 2) {
            switch (this.mLastTouchedPanel) {
                case 0:
                    float sat = this.mSat + (x / 50.0f);
                    float val = this.mVal - (y / 50.0f);
                    if (sat < 0.0f) {
                        sat = 0.0f;
                    } else if (sat > 1.0f) {
                        sat = 1.0f;
                    }
                    if (val < 0.0f) {
                        val = 0.0f;
                    } else if (val > 1.0f) {
                        val = 1.0f;
                    }
                    this.mSat = sat;
                    this.mVal = val;
                    update = true;
                    break;
                case 1:
                    float hue = this.mHue - (10.0f * y);
                    if (hue < 0.0f) {
                        hue = 0.0f;
                    } else if (hue > 360.0f) {
                        hue = 360.0f;
                    }
                    this.mHue = hue;
                    update = true;
                    break;
                case 2:
                    if (!this.mShowAlphaPanel || this.mAlphaRect == null) {
                        update = false;
                        break;
                    } else {
                        int alpha = (int) (this.mAlpha - (10.0f * x));
                        if (alpha < 0) {
                            alpha = 0;
                        } else if (alpha > 255) {
                            alpha = 255;
                        }
                        this.mAlpha = alpha;
                        update = true;
                        break;
                    }
            }
        }
        if (update) {
            OnColorChangedListener onColorChangedListener = this.mListener;
            if (onColorChangedListener != null) {
                onColorChangedListener.onColorChanged(Color.HSVToColor(this.mAlpha, new float[]{this.mHue, this.mSat, this.mVal}));
            }
            invalidate();
            return true;
        }
        return super.onTrackballEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean update = false;
        switch (event.getAction()) {
            case 0:
                this.mStartTouchPoint = new Point((int) event.getX(), (int) event.getY());
                update = moveTrackersIfNeeded(event);
                break;
            case 1:
                this.mStartTouchPoint = null;
                update = moveTrackersIfNeeded(event);
                break;
            case 2:
                update = moveTrackersIfNeeded(event);
                break;
        }
        if (update) {
            OnColorChangedListener onColorChangedListener = this.mListener;
            if (onColorChangedListener != null) {
                onColorChangedListener.onColorChanged(Color.HSVToColor(this.mAlpha, new float[]{this.mHue, this.mSat, this.mVal}));
            }
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean moveTrackersIfNeeded(MotionEvent event) {
        Point point = this.mStartTouchPoint;
        if (point == null) {
            return false;
        }
        int startX = point.x;
        int startY = point.y;
        if (this.mHueRect.contains(startX, startY)) {
            this.mLastTouchedPanel = 1;
            this.mHue = pointToHue(event.getY());
            return true;
        } else if (this.mSatValRect.contains(startX, startY)) {
            this.mLastTouchedPanel = 0;
            float[] result = pointToSatVal(event.getX(), event.getY());
            this.mSat = result[0];
            this.mVal = result[1];
            return true;
        } else {
            RectF rectF = this.mAlphaRect;
            if (rectF == null || !rectF.contains(startX, startY)) {
                return false;
            }
            this.mLastTouchedPanel = 2;
            this.mAlpha = pointToAlpha((int) event.getX());
            return true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthAllowed = MeasureSpec.getSize(widthMeasureSpec);
        int heightAllowed = MeasureSpec.getSize(heightMeasureSpec);
        int widthAllowed2 = chooseWidth(widthMode, widthAllowed);
        int heightAllowed2 = chooseHeight(heightMode, heightAllowed);
        if (!this.mShowAlphaPanel) {
            height = (int) ((widthAllowed2 - this.PANEL_SPACING) - this.HUE_PANEL_WIDTH);
            if (height > heightAllowed2 || getTag().equals("landscape")) {
                height = heightAllowed2;
                width = (int) (height + this.PANEL_SPACING + this.HUE_PANEL_WIDTH);
            } else {
                width = widthAllowed2;
            }
        } else {
            float f = this.ALPHA_PANEL_HEIGHT;
            float f2 = this.HUE_PANEL_WIDTH;
            width = (int) ((heightAllowed2 - f) + f2);
            if (width > widthAllowed2) {
                width = widthAllowed2;
                height = (int) ((widthAllowed2 - f2) + f);
            } else {
                height = heightAllowed2;
            }
        }
        setMeasuredDimension(width, height);
    }

    private int chooseWidth(int mode, int size) {
        if (mode == Integer.MIN_VALUE || mode == 1073741824) {
            return size;
        }
        return getPrefferedWidth();
    }

    private int chooseHeight(int mode, int size) {
        if (mode == Integer.MIN_VALUE || mode == 1073741824) {
            return size;
        }
        return getPrefferedHeight();
    }

    private int getPrefferedWidth() {
        int width = getPrefferedHeight();
        if (this.mShowAlphaPanel) {
            width = (int) (width - (this.PANEL_SPACING + this.ALPHA_PANEL_HEIGHT));
        }
        return (int) (width + this.HUE_PANEL_WIDTH + this.PANEL_SPACING);
    }

    private int getPrefferedHeight() {
        int height = (int) (this.mDensity * 200.0f);
        if (this.mShowAlphaPanel) {
            return (int) (height + this.PANEL_SPACING + this.ALPHA_PANEL_HEIGHT);
        }
        return height;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        RectF rectF = new RectF();
        this.mDrawingRect = rectF;
        rectF.left = this.mDrawingOffset + getPaddingLeft();
        this.mDrawingRect.right = (w - this.mDrawingOffset) - getPaddingRight();
        this.mDrawingRect.top = this.mDrawingOffset + getPaddingTop();
        this.mDrawingRect.bottom = (h - this.mDrawingOffset) - getPaddingBottom();
        setUpSatValRect();
        setUpHueRect();
        setUpAlphaRect();
    }

    private void setUpSatValRect() {
        RectF dRect = this.mDrawingRect;
        float panelSide = dRect.height() - 2.0f;
        if (this.mShowAlphaPanel) {
            panelSide -= this.PANEL_SPACING + this.ALPHA_PANEL_HEIGHT;
        }
        float left = dRect.left + 1.0f;
        float top = dRect.top + 1.0f;
        float bottom = top + panelSide;
        float right = left + panelSide;
        this.mSatValRect = new RectF(left, top, right, bottom);
    }

    private void setUpHueRect() {
        RectF dRect = this.mDrawingRect;
        float f = dRect.right;
        float left = (f - this.HUE_PANEL_WIDTH) + 1.0f;
        float top = dRect.top + 1.0f;
        float bottom = (dRect.bottom - 1.0f) - (this.mShowAlphaPanel ? this.PANEL_SPACING + this.ALPHA_PANEL_HEIGHT : 0.0f);
        float right = f - 1.0f;
        this.mHueRect = new RectF(left, top, right, bottom);
    }

    private void setUpAlphaRect() {
        if (!this.mShowAlphaPanel) {
            return;
        }
        RectF dRect = this.mDrawingRect;
        float left = dRect.left + 1.0f;
        float f = dRect.bottom;
        float top = (f - this.ALPHA_PANEL_HEIGHT) + 1.0f;
        float bottom = f - 1.0f;
        float right = dRect.right - 1.0f;
        this.mAlphaRect = new RectF(left, top, right, bottom);
        AlphaPatternsDrawable alphaPatternsDrawable = new AlphaPatternsDrawable((int) (this.mDensity * 5.0f));
        this.mAlphaPattern = alphaPatternsDrawable;
        alphaPatternsDrawable.setBounds(Math.round(this.mAlphaRect.left), Math.round(this.mAlphaRect.top), Math.round(this.mAlphaRect.right), Math.round(this.mAlphaRect.bottom));
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.mListener = listener;
    }

    public int getBorderColor() {
        return this.mBorderColor;
    }

    public void setBorderColor(int color) {
        this.mBorderColor = color;
        invalidate();
    }

    public int getColor() {
        return Color.HSVToColor(this.mAlpha, new float[]{this.mHue, this.mSat, this.mVal});
    }

    public void setColor(int color) {
        setColor(color, false);
    }

    public void setColor(int color, boolean callback) {
        OnColorChangedListener onColorChangedListener;
        int alpha = Color.alpha(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        this.mAlpha = alpha;
        float f = hsv[0];
        this.mHue = f;
        float f2 = hsv[1];
        this.mSat = f2;
        float f3 = hsv[2];
        this.mVal = f3;
        if (callback && (onColorChangedListener = this.mListener) != null) {
            onColorChangedListener.onColorChanged(Color.HSVToColor(alpha, new float[]{f, f2, f3}));
        }
        invalidate();
    }

    public float getDrawingOffset() {
        return this.mDrawingOffset;
    }

    public boolean getAlphaSliderVisible() {
        return this.mShowAlphaPanel;
    }

    public void setAlphaSliderVisible(boolean visible) {
        if (this.mShowAlphaPanel != visible) {
            this.mShowAlphaPanel = visible;
            this.mValShader = null;
            this.mSatShader = null;
            this.mHueShader = null;
            this.mAlphaShader = null;
            requestLayout();
        }
    }

    public int getSliderTrackerColor() {
        return this.mSliderTrackerColor;
    }

    public void setSliderTrackerColor(int color) {
        this.mSliderTrackerColor = color;
        this.mHueTrackerPaint.setColor(color);
        invalidate();
    }

    public String getAlphaSliderText() {
        return this.mAlphaSliderText;
    }

    public void setAlphaSliderText(int res) {
        String text = getContext().getString(res);
        setAlphaSliderText(text);
    }

    public void setAlphaSliderText(String text) {
        this.mAlphaSliderText = text;
        invalidate();
    }
}
