package com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class PathTrakers extends Path {

    private Paint paint;
    private Point startPoint;
    private List<Point> traker;
    private boolean isEraser = false;
    private boolean isBucket = false;

    public PathTrakers(Point startPoint) {
        ArrayList arrayList = new ArrayList();
        this.traker = arrayList;
        arrayList.add(startPoint);
    }

    public Point getStartPoint() {
        return this.startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public void setEraser(boolean eraser) {
        this.isEraser = eraser;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setPaint(Paint paint) {
        this.paint = new Paint(paint);
    }

    public boolean isBucket() {
        return this.isBucket;
    }

    public void setBucket(boolean bucket) {
        this.isBucket = bucket;
    }
}
