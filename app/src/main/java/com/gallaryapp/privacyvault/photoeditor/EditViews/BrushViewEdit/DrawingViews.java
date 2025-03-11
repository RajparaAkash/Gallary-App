package com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit.floodfills.QueueLinearFlood;
import com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit.floodfills.ScalingUtils;
import com.gallaryapp.privacyvault.photoeditor.EditViews.BrushViewEdit.manag.ThreadManag;

import java.util.ArrayList;
import java.util.List;

public class DrawingViews extends View {

    private int CURRENT_STROKE_OPACITY;
    private int CURRENT_STROKE_WIDTH;
    private final String TAG;
    private boolean actionUp;
    private Bitmap bufferBitmap;
    private Canvas bufferCanvas;
    private PorterDuffXfermode clearMode;
    private PathTrakers curPath;
    private GestureDetector gestureDetector;
    private boolean isBucket;
    private boolean isDrawing;
    private boolean isEraser;
    private Point lastPosition;
    private final int limit;
    private Bitmap loadedBitmap;
    private Canvas loadedCanvas;
    private Bitmap newStartBitmap;
    private Canvas newStartCanvas;
    private CallbackOnGesture onGestureListener;
    private int originalHeight;
    private int originalWidth;
    private Paint paint;
    private List<PathTrakers> pathTrakerList;
    private QueueLinearFlood queueLinearFloodFiller;
    private List<PathTrakers> redoPathList;
    CallbackStartDrawing startDrawingListener;
    private int targetColor;

    public DrawingViews(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAG = DrawingViews.class.getSimpleName();
        this.limit = 50;
        this.CURRENT_STROKE_WIDTH = 10;
        this.CURRENT_STROKE_OPACITY = 100;
        this.actionUp = false;
        this.isDrawing = false;
        this.isEraser = false;
        this.originalWidth = 0;
        this.originalHeight = 0;
        this.targetColor = -1;
        this.isBucket = false;
        init();
    }

    private void init() {
        initPaint();
        this.pathTrakerList = new ArrayList();
        this.redoPathList = new ArrayList();
        this.lastPosition = new Point(0, 0);
        this.onGestureListener = new CallbackOnGesture();
        this.gestureDetector = new GestureDetector(getContext(), this.onGestureListener);
    }

    private void initPaint() {
        Paint paint = new Paint(7);
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(this.CURRENT_STROKE_WIDTH);
        this.paint.setColor(-16777216);
        this.paint.setAlpha((int) (this.CURRENT_STROKE_OPACITY * 2.55d));
        this.clearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    public void setConfig(int width, int height) {
        this.onGestureListener.setCanvasBounds(width, height);
        this.bufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.bufferCanvas = new Canvas(this.bufferBitmap);
        this.newStartBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.newStartCanvas = new Canvas(this.newStartBitmap);
        this.originalWidth = width;
        this.originalHeight = height;
        String str = this.TAG;
    }

    public void clearDrawingBoard() {
        init();
        setConfig(this.originalWidth, this.originalHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(this.originalWidth, this.originalHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean z;
        super.onDraw(canvas);
        adjustCanvas(canvas);
        adjustCanvas(this.loadedCanvas);
        canvas.drawBitmap(this.bufferBitmap, 0.0f, 0.0f, (Paint) null);
        PathTrakers pathTrakers = this.curPath;
        if (pathTrakers != null && (z = this.isDrawing) && !this.actionUp && z && !this.isEraser) {
            canvas.drawPath(pathTrakers, this.paint);
        }
        Bitmap bitmap = this.loadedBitmap;
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        CallbackStartDrawing callbackStartDrawing = this.startDrawingListener;
        if (callbackStartDrawing != null) {
            callbackStartDrawing.onDrawStart();
        }
        float scaleFactor = this.onGestureListener.getScaleFactor();
        RectF viewRect = this.onGestureListener.getCurrentViewport();
        float touchX = (event.getX(0) + viewRect.left) / scaleFactor;
        float touchY = (event.getY(0) + viewRect.top) / scaleFactor;
        switch (event.getAction() & 255) {
            case 0:
                actionDown(touchX, touchY);
                break;
            case 1:
                actionUp();
                break;
            case 2:
                actionMove(touchX, touchY);
                break;
            case 5:
                actionPointDown();
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.onGestureListener.setViewBounds(w, h);
    }

    private void adjustCanvas(Canvas canvas) {
        if (canvas != null) {
            float scaleFactor = this.onGestureListener.getScaleFactor();
            RectF viewRect = this.onGestureListener.getCurrentViewport();
            canvas.translate(-viewRect.left, -viewRect.top);
            canvas.scale(scaleFactor, scaleFactor);
        }
    }

    private void actionDown(float x, float y) {
        if (isInCanvas(x, y)) {
            this.isDrawing = true;
            this.actionUp = false;
            this.lastPosition.set((int) x, (int) y);
            PathTrakers pathTrakers = new PathTrakers(this.lastPosition);
            this.curPath = pathTrakers;
            pathTrakers.setEraser(this.isEraser);
            this.curPath.setPaint(this.paint);
            this.curPath.moveTo(x, y);
            this.curPath.setStartPoint(new Point((int) x, (int) y));
            this.curPath.setBucket(this.isBucket);
            this.redoPathList.clear();
            this.targetColor = this.bufferBitmap.getPixel((int) x, (int) y);
        }
    }

    private void actionMove(float x, float y) {
        if (isInCanvas(x, y) && this.isDrawing && !this.curPath.isBucket()) {
            PathTrakers pathTrakers = this.curPath;
            Point point = this.lastPosition;
            int i = point.x;
            int i2 = point.y;
            pathTrakers.quadTo(i, i2, (i + x) / 2.0f, (i2 + y) / 2.0f);
            this.lastPosition.set((int) x, (int) y);
            if (this.isEraser) {
                this.bufferCanvas.drawPath(this.curPath, this.paint);
            }
        }
    }

    private void actionUp() {
        if (this.isDrawing) {
            if (!this.isEraser) {
                if (!this.curPath.isBucket()) {
                    this.bufferCanvas.drawPath(this.curPath, this.paint);
                } else {
                    QueueLinearFlood queueLinearFlood = new QueueLinearFlood(this.bufferBitmap, this.targetColor, this.paint.getColor());
                    this.queueLinearFloodFiller = queueLinearFlood;
                    queueLinearFlood.floodFill(this.curPath.getStartPoint().x, this.curPath.getStartPoint().y);
                }
            }
            this.pathTrakerList.add(this.curPath);
            if (this.pathTrakerList.size() > 50) {
                PathTrakers pathTraker = this.pathTrakerList.remove(0);
                this.newStartCanvas.drawPath(pathTraker, pathTraker.getPaint());
            }
            this.isDrawing = false;
        }
        this.actionUp = true;
    }

    private void actionPointDown() {
        this.isDrawing = false;
        this.curPath = null;
    }

    private boolean isInCanvas(float x, float y) {
        RectF canvasRect = this.onGestureListener.getCanvasRect();
        return canvasRect.contains(x, y);
    }

    public void change2Eraser() {
        this.isEraser = true;
        this.isBucket = false;
        this.paint.setXfermode(this.clearMode);
    }

    public void change2Brush() {
        this.isEraser = false;
        this.isBucket = false;
        this.paint.setXfermode(null);
    }

    public void undo() {
        if (this.pathTrakerList.size() > 0) {
            List<PathTrakers> list = this.redoPathList;
            List<PathTrakers> list2 = this.pathTrakerList;
            list.add(list2.remove(list2.size() - 1));
            this.bufferBitmap.eraseColor(0);
            this.bufferCanvas.drawBitmap(this.newStartBitmap, 0.0f, 0.0f, (Paint) null);
            for (PathTrakers pathTraker : this.pathTrakerList) {
                this.bufferCanvas.drawPath(pathTraker, pathTraker.getPaint());
            }
            invalidate();
        }
    }

    public void redo() {
        if (this.redoPathList.size() > 0 && this.pathTrakerList.size() < 50) {
            List<PathTrakers> list = this.redoPathList;
            PathTrakers pathTraker = list.remove(list.size() - 1);
            this.pathTrakerList.add(pathTraker);
            this.bufferCanvas.drawPath(pathTraker, pathTraker.getPaint());
        }
        invalidate();
    }

    public void setColor(int color) {
        int alpha = this.paint.getAlpha();
        this.paint.setColor(color);
        this.paint.setAlpha(alpha);
    }

    public int getStrokeWidth() {
        return this.CURRENT_STROKE_WIDTH;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.CURRENT_STROKE_WIDTH = strokeWidth;
        this.paint.setStrokeWidth(strokeWidth);
    }

    public int getOpacity() {
        return this.CURRENT_STROKE_OPACITY;
    }

    public void setOpacity(int alpha) {
        this.CURRENT_STROKE_OPACITY = alpha;
        this.paint.setAlpha((int) (alpha * 2.55d));
    }

    public void setLoadedBitmap(Bitmap bitmap) {
        Bitmap createScaledBitmap = ScalingUtils.createScaledBitmap(bitmap, this.originalWidth, this.originalHeight, ScalingUtils.ScalingLogic.FIT);
        this.loadedBitmap = createScaledBitmap;
        Canvas canvas = this.loadedCanvas;
        if (canvas == null) {
            this.loadedCanvas = new Canvas(this.loadedBitmap);
        } else {
            canvas.setBitmap(createScaledBitmap);
        }
        ThreadManag.getInstance().postToUIThread(new Runnable() {
            @Override
            public void run() {
                DrawingViews.this.invalidate();
            }
        });
    }

    public Bitmap getViewBitmap() {
        return getViewBitmap(this.originalWidth, this.originalHeight);
    }

    public Bitmap getViewBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Bitmap bitmap2 = this.bufferBitmap;
        if (bitmap2 != null) {
            canvas.drawBitmap(bitmap2, 0.0f, 0.0f, (Paint) null);
        }
        Bitmap bitmap3 = this.loadedBitmap;
        if (bitmap3 != null) {
            canvas.drawBitmap(bitmap3, 0.0f, 0.0f, (Paint) null);
        }
        return ScalingUtils.createScaledBitmap(bitmap, width, height, ScalingUtils.ScalingLogic.FIT);
    }

    public void setUserTouchListener(CallbackStartDrawing startDrawingListener) {
        this.startDrawingListener = startDrawingListener;
    }
}
