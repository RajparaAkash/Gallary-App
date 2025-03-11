package com.gallaryapp.privacyvault.photoeditor.EditViews.colorpick;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class AlphaPatternsDrawable extends Drawable {

    private Bitmap mBitmap;
    private final Paint mPaint = new Paint();
    private final Paint mPaintGray;
    private final Paint mPaintWhite;
    private int mRectangleSize;
    private int numRectanglesHorizontal;
    private int numRectanglesVertical;

    public AlphaPatternsDrawable(int rectangleSize) {
        Paint paint = new Paint();
        this.mPaintWhite = paint;
        Paint paint2 = new Paint();
        this.mPaintGray = paint2;
        this.mRectangleSize = 10;
        this.mRectangleSize = rectangleSize;
        paint.setColor(-1);
        paint2.setColor(-3421237);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.mBitmap, (Rect) null, getBounds(), this.mPaint);
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void setAlpha(int alpha) {
        throw new UnsupportedOperationException("Alpha is not supported by this drawwable.");
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        throw new UnsupportedOperationException("ColorFilter is not supported by this drawwable.");
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int height = bounds.height();
        int width = bounds.width();
        this.numRectanglesHorizontal = (int) Math.ceil(width / this.mRectangleSize);
        this.numRectanglesVertical = (int) Math.ceil(height / this.mRectangleSize);
        generatePatternBitmap();
    }

    private void generatePatternBitmap() {
        boolean z;
        if (getBounds().width() <= 0 || getBounds().height() <= 0) {
            return;
        }
        this.mBitmap = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.mBitmap);
        Rect r = new Rect();
        boolean verticalStartWhite = true;
        for (int i = 0; i <= this.numRectanglesVertical; i++) {
            boolean isWhite = verticalStartWhite;
            int j = 0;
            while (true) {
                z = false;
                if (j > this.numRectanglesHorizontal) {
                    break;
                }
                int i2 = this.mRectangleSize;
                int i3 = i * i2;
                r.top = i3;
                int i4 = j * i2;
                r.left = i4;
                r.bottom = i3 + i2;
                r.right = i4 + i2;
                canvas.drawRect(r, isWhite ? this.mPaintWhite : this.mPaintGray);
                if (!isWhite) {
                    z = true;
                }
                isWhite = z;
                j++;
            }
            if (!verticalStartWhite) {
                z = true;
            }
            verticalStartWhite = z;
        }
    }
}
