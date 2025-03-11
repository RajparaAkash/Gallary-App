package com.gallaryapp.privacyvault.photoeditor.EditViews.StickerViewEdit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.gallaryapp.privacyvault.photoeditor.R;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StickerView extends FrameLayout {

    public static final int FLIP_HORIZONTALLY = 1;
    public static final int FLIP_VERTICALLY = 1 << 1;
    private static final String TAG = "StickerView";
    private static final int DEFAULT_MIN_CLICK_DELAY_TIME = 200;
    private final boolean showIcons;
    private final boolean showBorder;
    private final boolean bringToFrontCurrentSticker;
    private final List<Sticker> GMStickers = new ArrayList<>();
    private final List<BitmapStickersIcon> icons = new ArrayList<>(4);
    private final Paint borderPaint = new Paint();
    private final RectF stickerRect = new RectF();
    private final Matrix sizeMatrix = new Matrix();
    private final Matrix downMatrix = new Matrix();
    private final Matrix moveMatrix = new Matrix();
    // region storing variables
    private final float[] bitmapPoints = new float[8];
    private final float[] bounds = new float[8];
    private final float[] point = new float[2];
    private final PointF currentCenterPoint = new PointF();
    private final float[] tmp = new float[2];
    // endregion
    private final int touchSlop;
    private PointF midPoint = new PointF();
    private BitmapStickersIcon currentIcon;
    //the first point down position
    private float downX;
    private float downY;
    private float oldDistance = 0f;
    private float oldRotation = 0f;
    @ActionMode
    private int currentMode = ActionMode.NONE;
    private Sticker handlingGMSticker;
    private boolean locked;
    private boolean constrained;
    private OnStickerOperationListener onStickerOperationListener;
    private long lastClickTime = 0;
    private int minClickDelayTime = DEFAULT_MIN_CLICK_DELAY_TIME;

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.StickerView);
            showIcons = a.getBoolean(R.styleable.StickerView_showIcons, false);
            showBorder = a.getBoolean(R.styleable.StickerView_showBorder, false);
            bringToFrontCurrentSticker = a.getBoolean(R.styleable.StickerView_bringToFrontCurrentSticker, false);

            borderPaint.setAntiAlias(true);
            borderPaint.setColor(a.getColor(R.styleable.StickerView_borderColor, Color.WHITE));
            configDefaultIcons();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    public Sticker getLastSticker() {
        return GMStickers.get(GMStickers.size() - 1);
    }

    public void configDefaultIcons() {
        BitmapStickersIcon deleteIcon = new BitmapStickersIcon(ContextCompat.getDrawable(getContext(),
                R.drawable.sticker_close_btn),
                BitmapStickersIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvents());

        BitmapStickersIcon zoomIcon = new BitmapStickersIcon(ContextCompat.getDrawable(getContext(),
                R.drawable.sticker_scale_btn),
                BitmapStickersIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvents());

        icons.clear();
        icons.add(deleteIcon);
        icons.add(zoomIcon);
    }

    public int getStickerPosition(Sticker GMSticker) {
        for (int i = 0; i < GMStickers.size(); i++) {
            if (GMStickers.get(i).equals(GMSticker)) {
                return i;
            }
        }
        return 0;
    }


    public void sendToLayer(int oldPos/*, int newPos*/) {
        int newPos = GMStickers.size() - 1;
        if (GMStickers.size() >= oldPos && GMStickers.size() >= newPos) {
            Sticker s = GMStickers.get(oldPos);
            GMStickers.remove(oldPos);
            GMStickers.add(newPos, s);
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            stickerRect.left = left;
            stickerRect.top = top;
            stickerRect.right = right;
            stickerRect.bottom = bottom;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawStickers(canvas);
    }

    protected void drawStickers(Canvas canvas) {
        for (int i = 0; i < GMStickers.size(); i++) {
            Sticker GMSticker = GMStickers.get(i);
            if (GMSticker != null) {
                GMSticker.draw(canvas);
            }
        }

        if (handlingGMSticker != null && !locked && (showBorder || showIcons)) {

            getStickerPoints(handlingGMSticker, bitmapPoints);

            float x1 = bitmapPoints[0];
            float y1 = bitmapPoints[1];
            float x2 = bitmapPoints[2];
            float y2 = bitmapPoints[3];
            float x3 = bitmapPoints[4];
            float y3 = bitmapPoints[5];
            float x4 = bitmapPoints[6];
            float y4 = bitmapPoints[7];

            if (showBorder) {
                canvas.drawLine(x1, y1, x2, y2, borderPaint);
                canvas.drawLine(x1, y1, x3, y3, borderPaint);
                canvas.drawLine(x2, y2, x4, y4, borderPaint);
                canvas.drawLine(x4, y4, x3, y3, borderPaint);
            }

            //draw icons
            if (showIcons) {
                float rotation = calculateRotation(x4, y4, x3, y3);
                for (int i = 0; i < icons.size(); i++) {
                    BitmapStickersIcon icon = icons.get(i);
                    switch (icon.getPosition()) {
                        case BitmapStickersIcon.LEFT_TOP:
                            configIconMatrix(icon, x1, y1, rotation);
                            break;

                        case BitmapStickersIcon.RIGHT_TOP:
                            configIconMatrix(icon, x2, y2, rotation);
                            break;

                        case BitmapStickersIcon.LEFT_BOTTOM:
                            configIconMatrix(icon, x3, y3, rotation);
                            break;

                        case BitmapStickersIcon.RIGHT_BOTOM:
                            configIconMatrix(icon, x4, y4, rotation);
                            break;
                    }
                    icon.draw(canvas, borderPaint);
                }
            }
        }
    }

    protected void configIconMatrix(@NonNull BitmapStickersIcon icon, float x, float y, float rotation) {
        icon.setX(x);
        icon.setY(y);
        icon.getMatrix().reset();

        icon.getMatrix().postRotate(rotation, icon.getWidth() / 2, icon.getHeight() / 2);
        icon.getMatrix().postTranslate(x - icon.getWidth() / 2, y - icon.getHeight() / 2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();

                return findCurrentIconTouched() != null || findHandlingSticker() != null;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!onTouchDown(event)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistance = calculateDistance(event);
                oldRotation = calculateRotation(event);

                midPoint = calculateMidPoint(event);

                if (handlingGMSticker != null && isInStickerArea(handlingGMSticker, event.getX(1),
                        event.getY(1)) && findCurrentIconTouched() == null) {
                    currentMode = ActionMode.ZOOM_WITH_TWO_FINGER;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                handleCurrentMode(event);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (currentMode == ActionMode.ZOOM_WITH_TWO_FINGER && handlingGMSticker != null) {
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerZoomFinished(handlingGMSticker);
                    }
                }
                currentMode = ActionMode.NONE;
                break;
        }

        return true;
    }


    protected boolean onTouchDown(@NonNull MotionEvent event) {
        currentMode = ActionMode.DRAG;

        downX = event.getX();
        downY = event.getY();

        midPoint = calculateMidPoint();
        oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY);
        oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY);

        currentIcon = findCurrentIconTouched();
        if (currentIcon != null) {
            currentMode = ActionMode.ICON;
            currentIcon.onActionDown(this, event);
        } else {
            handlingGMSticker = findHandlingSticker();
        }

        if (handlingGMSticker != null) {
            onStickerOperationListener.onStickerTouchedDown(handlingGMSticker);
            downMatrix.set(handlingGMSticker.getMatrix());
            if (bringToFrontCurrentSticker) {
                GMStickers.remove(handlingGMSticker);
                GMStickers.add(handlingGMSticker);
            }
        }

        if (currentIcon == null && handlingGMSticker == null) {
            return false;
        }
        invalidate();
        return true;
    }

    protected void onTouchUp(@NonNull MotionEvent event) {
        long currentTime = SystemClock.uptimeMillis();

        if (currentMode == ActionMode.ICON && currentIcon != null && handlingGMSticker != null) {
            currentIcon.onActionUp(this, event);
        }

        if (currentMode == ActionMode.DRAG
                && Math.abs(event.getX() - downX) < touchSlop
                && Math.abs(event.getY() - downY) < touchSlop
                && handlingGMSticker != null) {
            currentMode = ActionMode.CLICK;
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerClicked(handlingGMSticker);
            }
            if (currentTime - lastClickTime < minClickDelayTime) {
                if (onStickerOperationListener != null) {
                    onStickerOperationListener.onStickerDoubleTapped(handlingGMSticker);
                }
            }
        }

        if (currentMode == ActionMode.DRAG && handlingGMSticker != null) {
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDragFinished(handlingGMSticker);
            }
        }

        currentMode = ActionMode.NONE;
        lastClickTime = currentTime;
    }

    protected void handleCurrentMode(@NonNull MotionEvent event) {
        switch (currentMode) {
            case ActionMode.NONE:
            case ActionMode.CLICK:
                break;

            case ActionMode.DRAG:
                if (handlingGMSticker != null) {
                    moveMatrix.set(downMatrix);
                    moveMatrix.postTranslate(event.getX() - downX, event.getY() - downY);
                    handlingGMSticker.setMatrix(moveMatrix);
                    if (constrained) {
                        constrainSticker(handlingGMSticker);
                    }
                }
                break;

            case ActionMode.ZOOM_WITH_TWO_FINGER:
                if (handlingGMSticker != null) {
                    float newDistance = calculateDistance(event);
                    float newRotation = calculateRotation(event);

                    moveMatrix.set(downMatrix);
                    moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x, midPoint.y);
                    moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
                    handlingGMSticker.setMatrix(moveMatrix);
                }
                break;

            case ActionMode.ICON:
                if (handlingGMSticker != null && currentIcon != null) {
                    currentIcon.onActionMove(this, event);
                }
                break;
        }
    }

    public void zoomAndRotateCurrentSticker(@NonNull MotionEvent event) {
        zoomAndRotateSticker(handlingGMSticker, event);
    }

    public void zoomAndRotateSticker(@Nullable Sticker GMSticker, @NonNull MotionEvent event) {
        if (GMSticker != null) {
            float newDistance = calculateDistance(midPoint.x, midPoint.y, event.getX(), event.getY());
            float newRotation = calculateRotation(midPoint.x, midPoint.y, event.getX(), event.getY());

            moveMatrix.set(downMatrix);
            moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x, midPoint.y);
            moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
            handlingGMSticker.setMatrix(moveMatrix);
        }
    }

    protected void constrainSticker(@NonNull Sticker GMSticker) {
        float moveX = 0;
        float moveY = 0;
        int width = getWidth();
        int height = getHeight();
        GMSticker.getMappedCenterPoint(currentCenterPoint, point, tmp);
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x;
        }

        if (currentCenterPoint.x > width) {
            moveX = width - currentCenterPoint.x;
        }

        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y;
        }

        if (currentCenterPoint.y > height) {
            moveY = height - currentCenterPoint.y;
        }

        GMSticker.getMatrix().postTranslate(moveX, moveY);
    }

    @Nullable
    protected BitmapStickersIcon findCurrentIconTouched() {
        for (BitmapStickersIcon icon : icons) {
            float x = icon.getX() - downX;
            float y = icon.getY() - downY;
            float distance_pow_2 = x * x + y * y;
            if (distance_pow_2 <= Math.pow(icon.getIconRadius() + icon.getIconRadius(), 2)) {
                return icon;
            }
        }

        return null;
    }

    @Nullable
    protected Sticker findHandlingSticker() {
        for (int i = GMStickers.size() - 1; i >= 0; i--) {
            if (isInStickerArea(GMStickers.get(i), downX, downY)) {
                return GMStickers.get(i);
            }
        }
        return null;
    }

    protected boolean isInStickerArea(@NonNull Sticker GMSticker, float downX, float downY) {
        tmp[0] = downX;
        tmp[1] = downY;
        return GMSticker.contains(tmp);
    }

    @NonNull
    protected PointF calculateMidPoint(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            midPoint.set(0, 0);
            return midPoint;
        }
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        midPoint.set(x, y);
        return midPoint;
    }

    @NonNull
    protected PointF calculateMidPoint() {
        if (handlingGMSticker == null) {
            midPoint.set(0, 0);
            return midPoint;
        }
        handlingGMSticker.getMappedCenterPoint(midPoint, point, tmp);
        return midPoint;
    }

    protected float calculateRotation(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }


    protected float calculateDistance(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        for (int i = 0; i < GMStickers.size(); i++) {
            Sticker GMSticker = GMStickers.get(i);
            if (GMSticker != null) {
                transformSticker(GMSticker);
            }
        }
    }

    protected void transformSticker(@Nullable Sticker GMSticker) {
        if (GMSticker == null) {
            return;
        }

        sizeMatrix.reset();

        float width = getWidth();
        float height = getHeight();
        float stickerWidth = GMSticker.getWidth();
        float stickerHeight = GMSticker.getHeight();
        //step 1
        float offsetX = (width - stickerWidth) / 2;
        float offsetY = (height - stickerHeight) / 2;

        sizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (width < height) {
            scaleFactor = width / stickerWidth;
        } else {
            scaleFactor = height / stickerHeight;
        }

        sizeMatrix.postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);

        GMSticker.getMatrix().reset();
        GMSticker.setMatrix(sizeMatrix);

        invalidate();
    }

    public void flipCurrentSticker(int direction) {
        flip(handlingGMSticker, direction);
    }

    public void flip(@Nullable Sticker GMSticker, @Flip int direction) {
        if (GMSticker != null) {
            GMSticker.getCenterPoint(midPoint);
            if ((direction & FLIP_HORIZONTALLY) > 0) {
                GMSticker.getMatrix().preScale(-1, 1, midPoint.x, midPoint.y);
                GMSticker.setFlippedHorizontally(!GMSticker.isFlippedHorizontally());
            }
            if ((direction & FLIP_VERTICALLY) > 0) {
                GMSticker.getMatrix().preScale(1, -1, midPoint.x, midPoint.y);
                GMSticker.setFlippedVertically(!GMSticker.isFlippedVertically());
            }

            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerFlipped(GMSticker);
            }

            invalidate();
        }
    }

    public boolean replace(@Nullable Sticker GMSticker) {
        return replace(GMSticker, true);
    }

    public boolean replace(@Nullable Sticker GMSticker, boolean needStayState) {
        if (handlingGMSticker != null && GMSticker != null) {
            float width = getWidth();
            float height = getHeight();
            if (needStayState) {
                GMSticker.setMatrix(handlingGMSticker.getMatrix());
                GMSticker.setFlippedVertically(handlingGMSticker.isFlippedVertically());
                GMSticker.setFlippedHorizontally(handlingGMSticker.isFlippedHorizontally());
            } else {
                handlingGMSticker.getMatrix().reset();
                float offsetX = (width - handlingGMSticker.getWidth()) / 2f;
                float offsetY = (height - handlingGMSticker.getHeight()) / 2f;
                GMSticker.getMatrix().postTranslate(offsetX, offsetY);

                float scaleFactor;
                if (width < height) {
                    scaleFactor = width / handlingGMSticker.getDrawable().getIntrinsicWidth();
                } else {
                    scaleFactor = height / handlingGMSticker.getDrawable().getIntrinsicHeight();
                }
                GMSticker.getMatrix().postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);
            }
            int index = GMStickers.indexOf(handlingGMSticker);
            GMStickers.set(index, GMSticker);
            handlingGMSticker = GMSticker;

            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(@Nullable Sticker GMSticker) {
        if (GMStickers.contains(GMSticker)) {
            GMStickers.remove(GMSticker);
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDeleted(GMSticker);
            }
            if (handlingGMSticker == GMSticker) {
                handlingGMSticker = null;
            }
            invalidate();

            return true;
        } else {
            return false;
        }
    }

    public boolean removeCurrentSticker() {
        return remove(handlingGMSticker);
    }

    public void removeAllStickers() {
        GMStickers.clear();
        if (handlingGMSticker != null) {
            handlingGMSticker.release();
            handlingGMSticker = null;
        }
        invalidate();
    }

    @NonNull
    public StickerView addSticker(@NonNull Sticker GMSticker) {
        return addSticker(GMSticker, Sticker.Position.CENTER);
    }

    public StickerView addSticker(@NonNull final Sticker GMSticker, final @Sticker.Position int position) {
        if (ViewCompat.isLaidOut(this)) {
            addStickerImmediately(GMSticker, position);
        } else {
            post(() -> addStickerImmediately(GMSticker, position));
        }
        return this;
    }

    protected void addStickerImmediately(@NonNull Sticker GMSticker, @Sticker.Position int position) {
        setStickerPosition(GMSticker, position);

        float scaleFactor, widthScaleFactor, heightScaleFactor;

        widthScaleFactor = (float) getWidth() / GMSticker.getDrawable().getIntrinsicWidth();
        heightScaleFactor = (float) getHeight() / GMSticker.getDrawable().getIntrinsicHeight();
        scaleFactor = widthScaleFactor > heightScaleFactor ? heightScaleFactor : widthScaleFactor;

        GMSticker.getMatrix().postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);

        handlingGMSticker = GMSticker;
        GMStickers.add(GMSticker);
        if (onStickerOperationListener != null) {
            onStickerOperationListener.onStickerAdded(GMSticker);
        }
        invalidate();
    }

    protected void setStickerPosition(@NonNull Sticker GMSticker, @Sticker.Position int position) {
        float width = getWidth();
        float height = getHeight();
        float offsetX = width - GMSticker.getWidth();
        float offsetY = height - GMSticker.getHeight();
        if ((position & Sticker.Position.TOP) > 0) {
            offsetY /= 4f;
        } else if ((position & Sticker.Position.BOTTOM) > 0) {
            offsetY *= 3f / 4f;
        } else {
            offsetY /= 2f;
        }
        if ((position & Sticker.Position.LEFT) > 0) {
            offsetX /= 4f;
        } else if ((position & Sticker.Position.RIGHT) > 0) {
            offsetX *= 3f / 4f;
        } else {
            offsetX /= 2f;
        }
        GMSticker.getMatrix().postTranslate(offsetX, offsetY);
    }

    @NonNull
    public float[] getStickerPoints(@Nullable Sticker GMSticker) {
        float[] points = new float[8];
        getStickerPoints(GMSticker, points);
        return points;
    }

    public void getStickerPoints(@Nullable Sticker GMSticker, @NonNull float[] dst) {
        if (GMSticker == null) {
            Arrays.fill(dst, 0);
            return;
        }
        GMSticker.getBoundPoints(bounds);
        GMSticker.getMappedPoints(dst, bounds);
    }

    public void save(@NonNull File file) {
        try {
            UseFull.saveImageToGallery(file, createBitmap());
            UseFull.notifySystemGallery(getContext(), file);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
            //
        }
    }

    @NonNull
    public Bitmap createBitmap() throws OutOfMemoryError {
        handlingGMSticker = null;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public int getStickerCount() {
        return GMStickers.size();
    }

    public boolean isNoneSticker() {
        return getStickerCount() == 0;
    }

    public boolean isLocked() {
        return locked;
    }

    @NonNull
    public StickerView setLocked(boolean locked) {
        this.locked = locked;
        invalidate();
        return this;
    }

    public int getMinClickDelayTime() {
        return minClickDelayTime;
    }

    @NonNull
    public StickerView setMinClickDelayTime(int minClickDelayTime) {
        this.minClickDelayTime = minClickDelayTime;
        return this;
    }

    public boolean isConstrained() {
        return constrained;
    }

    @NonNull
    public StickerView setConstrained(boolean constrained) {
        this.constrained = constrained;
        postInvalidate();
        return this;
    }

    @Nullable
    public OnStickerOperationListener getOnStickerOperationListener() {
        return onStickerOperationListener;
    }

    @NonNull
    public StickerView setOnStickerOperationListener(@Nullable OnStickerOperationListener onStickerOperationListener) {
        this.onStickerOperationListener = onStickerOperationListener;
        return this;
    }

    @Nullable
    public Sticker getCurrentSticker() {
        return handlingGMSticker;
    }

    @NonNull
    public List<BitmapStickersIcon> getIcons() {
        return icons;
    }

    public void setIcons(@NonNull List<BitmapStickersIcon> icons) {
        this.icons.clear();
        this.icons.addAll(icons);
        invalidate();
    }

    @IntDef({
            ActionMode.NONE, ActionMode.DRAG, ActionMode.ZOOM_WITH_TWO_FINGER, ActionMode.ICON,
            ActionMode.CLICK
    })
    @Retention(RetentionPolicy.SOURCE)
    protected @interface ActionMode {
        int NONE = 0;
        int DRAG = 1;
        int ZOOM_WITH_TWO_FINGER = 2;
        int ICON = 3;
        int CLICK = 4;
    }

    @IntDef(flag = true, value = {FLIP_HORIZONTALLY, FLIP_VERTICALLY})
    @Retention(RetentionPolicy.SOURCE)
    protected @interface Flip {
    }

    public interface OnStickerOperationListener {
        void onStickerAdded(@NonNull Sticker GMSticker);

        void onStickerClicked(@NonNull Sticker GMSticker);

        void onStickerDeleted(@NonNull Sticker GMSticker);

        void onStickerDragFinished(@NonNull Sticker GMSticker);

        void onStickerTouchedDown(@NonNull Sticker GMSticker);

        void onStickerZoomFinished(@NonNull Sticker GMSticker);

        void onStickerFlipped(@NonNull Sticker GMSticker);

        void onStickerDoubleTapped(@NonNull Sticker GMSticker);
    }
}
