package com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit.floodfills;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class ScalingUtils {

    public enum ScalingLogic {
        CROP,
        FIT
    }

    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);
        new Canvas(scaledBitmap).drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(2));
        return scaledBitmap;
    }

    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic != ScalingLogic.CROP) {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
        float dstAspect = ((float) dstWidth) / ((float) dstHeight);
        if (((float) srcWidth) / ((float) srcHeight) > dstAspect) {
            int srcRectWidth = (int) (((float) srcHeight) * dstAspect);
            int srcRectLeft = (srcWidth - srcRectWidth) / 2;
            return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
        }
        int srcRectHeight = (int) (((float) srcWidth) / dstAspect);
        int scrRectTop = (srcHeight - srcRectHeight) / 2;
        return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
    }

    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, ScalingLogic scalingLogic) {
        if (scalingLogic != ScalingLogic.FIT) {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
        float srcAspect = ((float) srcWidth) / ((float) srcHeight);
        if (srcAspect > ((float) dstWidth) / ((float) dstHeight)) {
            return new Rect(0, 0, dstWidth, (int) (((float) dstWidth) / srcAspect));
        }
        return new Rect(0, 0, (int) (((float) dstHeight) * srcAspect), dstHeight);
    }
}
