package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;

public class UtilVaultFile {

    private File dirName;

    public UtilVaultFile(Context context) {
        try {
            File externalFilesDir = context.getExternalFilesDir(null);
            this.dirName = externalFilesDir;
            if (externalFilesDir != null && !externalFilesDir.exists()) {
                this.dirName.mkdirs();
            }
        } catch (NullPointerException e) {
        }
    }

    public static Bitmap decodeFile(String filePath) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        int width_tmp = o.outWidth;
        int height_tmp = o.outHeight;
        int inSampleSize = 1;
        if (width_tmp > 400 || height_tmp > 400) {
            int halfHeight = height_tmp / 2;
            int halfWidth = width_tmp / 2;
            while (halfHeight / inSampleSize >= 400 && halfWidth / inSampleSize >= 400) {
                inSampleSize *= 2;
            }
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = inSampleSize;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, o2);
        return rotatedBitmap(bmp, filePath);
    }

    public static Bitmap rotatedBitmap(Bitmap bmp, String filePath) {
        try {
            ExifInterface ei = new ExifInterface(filePath);
            int orientation = ei.getAttributeInt("Orientation", 1);
            switch (orientation) {
                case 1:
                    bmp = rotateImage(bmp, 0);
                    break;
                case 3:
                    bmp = rotateImage(bmp, 180);
                    break;
                case 6:
                    bmp = rotateImage(bmp, 90);
                    break;
                case 8:
                    bmp = rotateImage(bmp, 270);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public static Bitmap rotateImage(Bitmap bmp, int mRotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(mRotation);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }
}
