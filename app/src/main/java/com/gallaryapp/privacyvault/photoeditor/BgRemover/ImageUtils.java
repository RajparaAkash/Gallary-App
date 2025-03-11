package com.gallaryapp.privacyvault.photoeditor.BgRemover;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class ImageUtils {

    public static Bitmap getBitmapResize(Bitmap bitmap, int i, int i2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width >= height) {
            int i3 = (height * i) / width;
            if (i3 > i2) {
                i = (i * i2) / i3;
            } else {
                i2 = i3;
            }
        } else {
            int i4 = (width * i2) / height;
            if (i4 > i) {
                i2 = (i2 * i) / i4;
            } else {
                i = i4;
            }
        }
        return Bitmap.createScaledBitmap(bitmap, i, i2, true);
    }

    public static int dpToPx(Context context, float f) {
        context.getResources();
        return (int) (f * Resources.getSystem().getDisplayMetrics().density);
    }

    public static Bitmap maskBtm(Bitmap bitmap, Bitmap bitmap2) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
        paint.setXfermode(null);
        return createBitmap;
    }

    public static Bitmap getTiledBitmap(Context context, int drawableResId, int width, int height) {
        Rect rect = new Rect(0, 0, width, height);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(BitmapFactory.decodeResource(context.getResources(), drawableResId, new BitmapFactory.Options()), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        new Canvas(createBitmap).drawRect(rect, paint);
        return createBitmap;
    }
}