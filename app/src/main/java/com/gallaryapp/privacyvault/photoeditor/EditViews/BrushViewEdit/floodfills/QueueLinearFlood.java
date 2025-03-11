package com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit.floodfills;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.LinkedList;
import java.util.Queue;

public class QueueLinearFlood {
    private boolean[] pixelsChecked;
    private Queue<FloodFillRange> ranges;
    private int targetColor;
    private final int[] startColor = {0, 0, 0};
    private Bitmap image = null;
    private int[] tolerance = {0, 0, 0};
    private int width = 0;
    private int height = 0;
    private int[] pixels = null;
    private int fillColor = 0;

    public QueueLinearFlood(Bitmap img, int targetColor, int newColor) {
        useImage(img);
        setFillColor(newColor);
        setTargetColor(targetColor);
    }

    public void setTargetColor(int targetColor) {
        this.targetColor = targetColor;
        this.startColor[0] = Color.red(targetColor);
        this.startColor[1] = Color.green(targetColor);
        this.startColor[2] = Color.blue(targetColor);
    }

    public void setFillColor(int value) {
        this.fillColor = value;
    }

    public void useImage(Bitmap img) {
        this.width = img.getWidth();
        int height = img.getHeight();
        this.height = height;
        this.image = img;
        int i = this.width;
        int[] iArr = new int[i * height];
        this.pixels = iArr;
        img.getPixels(iArr, 0, i, 0, 0, i, height);
    }

    protected void prepare() {
        this.pixelsChecked = new boolean[this.pixels.length];
        this.ranges = new LinkedList();
    }

    public void floodFill(int x, int y) {
        prepare();
        int[] iArr = this.startColor;
        if (iArr[0] == 0) {
            int startPixel = this.pixels[(this.width * y) + x];
            iArr[0] = (startPixel >> 16) & 255;
            iArr[1] = (startPixel >> 8) & 255;
            iArr[2] = startPixel & 255;
        }
        LinearFill(x, y);
        while (this.ranges.size() > 0) {
            FloodFillRange range = this.ranges.remove();
            int i = this.width;
            int i2 = range.Y;
            int i3 = range.startX;
            int downPxIdx = ((i2 + 1) * i) + i3;
            int upPxIdx = (i * (i2 - 1)) + i3;
            int upY = i2 - 1;
            int downY = i2 + 1;
            for (int i4 = range.startX; i4 <= range.endX; i4++) {
                if (range.Y > 0 && !this.pixelsChecked[upPxIdx] && CheckPixel(upPxIdx)) {
                    LinearFill(i4, upY);
                }
                if (range.Y < this.height - 1 && !this.pixelsChecked[downPxIdx] && CheckPixel(downPxIdx)) {
                    LinearFill(i4, downY);
                }
                downPxIdx++;
                upPxIdx++;
            }
        }
        Bitmap bitmap = this.image;
        int[] iArr2 = this.pixels;
        int i5 = this.width;
        bitmap.setPixels(iArr2, 0, i5, 0, 0, i5, this.height);
    }

    protected void LinearFill(int x, int y) {
        int lFillLoc = x;
        int pxIdx = (this.width * y) + x;
        do {
            this.pixels[pxIdx] = this.fillColor;
            boolean[] zArr = this.pixelsChecked;
            zArr[pxIdx] = true;
            lFillLoc--;
            pxIdx--;
            if (lFillLoc < 0 || zArr[pxIdx]) {
                break;
            }
        } while (CheckPixel(pxIdx));
        int lFillLoc2 = lFillLoc + 1;
        int rFillLoc = x;
        int pxIdx2 = (this.width * y) + x;
        do {
            this.pixels[pxIdx2] = this.fillColor;
            boolean[] zArr2 = this.pixelsChecked;
            zArr2[pxIdx2] = true;
            rFillLoc++;
            pxIdx2++;
            if (rFillLoc >= this.width || zArr2[pxIdx2]) {
                break;
            }
        } while (CheckPixel(pxIdx2));
        FloodFillRange r = new FloodFillRange(lFillLoc2, rFillLoc - 1, y);
        this.ranges.offer(r);
    }

    protected boolean CheckPixel(int px) {
        int[] iArr = this.pixels;
        int red = (iArr[px] >>> 16) & 255;
        int green = (iArr[px] >>> 8) & 255;
        int blue = iArr[px] & 255;
        int[] iArr2 = this.startColor;
        int i = iArr2[0];
        int[] iArr3 = this.tolerance;
        return red >= i - iArr3[0] && red <= iArr2[0] + iArr3[0] && green >= iArr2[1] - iArr3[1] && green <= iArr2[1] + iArr3[1] && blue >= iArr2[2] - iArr3[2] && blue <= iArr2[2] + iArr3[2];
    }

    public class FloodFillRange {
        public int Y;
        public int endX;
        public int startX;

        public FloodFillRange(int startX, int endX, int y) {
            this.startX = startX;
            this.endX = endX;
            this.Y = y;
        }
    }
}
