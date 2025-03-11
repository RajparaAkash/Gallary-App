package com.gallaryapp.privacyvault.photoeditor.MyViewCustom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.gallaryapp.privacyvault.photoeditor.MyUtils.MyPreference;
import com.gallaryapp.privacyvault.photoeditor.R;

import java.util.ArrayList;
import java.util.List;

public class Lock9Views extends ViewGroup {

    public static Vibrator vibrator;
    private boolean autoLink;
    private Paint brushPaint;
    private CallBack callBack;
    private final Context context;

    public boolean enableVibrate;
    boolean isGlow;
    private int lineColor;
    private float lineWidth;
    PathEffect[] mEffects;

    private float nodeAreaExpand;
    private final List<NodeView> nodeList = new ArrayList();

    public int nodeOnAnim;

    public Drawable nodeOnSrc;
    private float nodeSize;

    public Drawable nodeSrc;
    private float padding;
    private Paint paint;
    private final StringBuilder passwordBuilder = new StringBuilder();

    private float spacing;

    public int vibrateTime;
    private float x;
    private float y;

    public interface CallBack {
        void onFinish(String str);
    }

    public Lock9Views(Context context2, AttributeSet attrs) {
        super(context2, attrs);
        this.context = context2;
        init(context2, attrs, 0, 0);
    }

    public void setCallBack(CallBack callBack2) {
        this.callBack = callBack2;
    }

    public Drawable getNode() {
        return this.nodeSrc;
    }

    public void setNode(Drawable d) {
        this.nodeSrc = d;
        invalidate();
        requestLayout();
    }

    private void init(Context context2, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context2.obtainStyledAttributes(attrs, R.styleable.Lock9View, defStyleAttr, defStyleRes);
        this.nodeSrc = a.getDrawable(R.styleable.Lock9View_lock9_nodeSrc);
        this.nodeOnSrc = a.getDrawable(R.styleable.Lock9View_lock9_nodeOnSrc);
        this.nodeSize = a.getDimension(R.styleable.Lock9View_lock9_nodeSize, 0.0f);
        this.nodeAreaExpand = a.getDimension(R.styleable.Lock9View_lock9_nodeAreaExpand, 0.0f);
        this.nodeOnAnim = a.getResourceId(R.styleable.Lock9View_lock9_nodeOnAnim, 0);
        this.lineColor = a.getColor(R.styleable.Lock9View_lock9_lineColor, Color.argb(0, 0, 0, 0));
        this.lineWidth = a.getDimension(R.styleable.Lock9View_lock9_lineWidth, 0.0f);
        this.padding = a.getDimension(9, 0.0f);
        this.spacing = a.getDimension(R.styleable.Lock9View_lock9_spacing, 0.0f);
        this.autoLink = true;
        this.enableVibrate = a.getBoolean(R.styleable.Lock9View_lock9_enableVibrate, true);
        this.vibrateTime = a.getInt(R.styleable.Lock9View_lock9_vibrateTime, 20);
        a.recycle();
        if (this.enableVibrate && !isInEditMode()) {
            vibrator = (Vibrator) context2.getSystemService(Context.VIBRATOR_SERVICE);
        }
        Paint paint2 = new Paint(4);
        this.paint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(this.lineWidth);

        if (MyPreference.get_IsInvisibleMode()) {
            lineColor = Color.parseColor("#00000000");
            nodeOnSrc = getResources().getDrawable(R.drawable.icon_pattern_dot_default);
        }

        this.paint.setColor(this.lineColor);
        this.paint.setAntiAlias(true);
        if (this.nodeSrc != null) {
            for (int n = 0; n < 9; n++) {
                addView(new NodeView(getContext(), n + 1));
            }
        }
        this.mEffects = new PathEffect[6];
        setWillNotDraw(false);
    }


    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = measureSize(widthMeasureSpec);
        setMeasuredDimension(size, size);
    }

    private int measureSize(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
            case 1073741824:
                return specSize;
            default:
                return 0;
        }
    }


    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }
        if (this.nodeSize > 0.0f) {
            float areaWidth = (float) ((right - left) / 3);
            for (int n = 0; n < 9; n++) {
                float f = this.nodeSize;
                int l = (int) ((((float) (n % 3)) * areaWidth) + ((areaWidth - f) / 2.0f));
                int t = (int) ((((float) (n / 3)) * areaWidth) + ((areaWidth - f) / 2.0f));
                ((NodeView) getChildAt(n)).layout(l, t, (int) (((float) l) + f), (int) (((float) t) + f));
            }
            return;
        }
        float nodeSize2 = ((((float) (right - left)) - (this.padding * 2.0f)) - (this.spacing * 2.0f)) / 3.0f;
        for (int n2 = 0; n2 < 9; n2++) {
            float f2 = this.padding;
            float f3 = this.spacing;
            int l2 = (int) ((((float) (n2 % 3)) * (nodeSize2 + f3)) + f2);
            int t2 = (int) (f2 + (((float) (n2 / 3)) * (f3 + nodeSize2)));
            ((NodeView) getChildAt(n2)).layout(l2, t2, (int) (((float) l2) + nodeSize2), (int) (((float) t2) + nodeSize2));
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
            case 2:
                this.x = event.getX();
                float y2 = event.getY();
                this.y = y2;
                NodeView currentNode = getNodeAt(this.x, y2);
                if (currentNode != null && !currentNode.isHighLighted()) {
                    if (this.nodeList.size() > 0 && this.autoLink) {
                        List<NodeView> list = this.nodeList;
                        NodeView middleNode = getNodeBetween(list.get(list.size() - 1), currentNode);
                        if (middleNode != null && !middleNode.isHighLighted()) {
                            middleNode.setHighLighted(true, true);
                            this.nodeList.add(middleNode);
                        }
                    }
                    currentNode.setHighLighted(true, false);
                    this.nodeList.add(currentNode);
                }
                if (this.nodeList.size() <= 0) {
                    requestDisallowInterceptTouchEvent(false);
                    break;
                } else {
                    requestDisallowInterceptTouchEvent(true);
                    invalidate();
                    break;
                }
            case 1:
                if (this.nodeList.size() > 0) {
                    if (this.callBack != null) {
                        this.passwordBuilder.setLength(0);
                        for (NodeView nodeView : this.nodeList) {
                            this.passwordBuilder.append(nodeView.getNum());
                        }
                        this.callBack.onFinish(this.passwordBuilder.toString());
                    }
                    this.nodeList.clear();
                    for (int n = 0; n < getChildCount(); n++) {
                        ((NodeView) getChildAt(n)).setHighLighted(false, false);
                    }
                    invalidate();
                    break;
                }
                break;
        }
        return true;
    }


    public void onDraw(Canvas canvas) {
        for (int n = 1; n < this.nodeList.size(); n++) {
            NodeView firstNode = this.nodeList.get(n - 1);
            NodeView secondNode = this.nodeList.get(n);
            if (this.isGlow) {
                canvas.drawLine((float) firstNode.getCenterX(), (float) firstNode.getCenterY(), (float) secondNode.getCenterX(), (float) secondNode.getCenterY(), this.brushPaint);
            }
            canvas.drawLine((float) firstNode.getCenterX(), (float) firstNode.getCenterY(), (float) secondNode.getCenterX(), (float) secondNode.getCenterY(), this.paint);
        }
        if (this.nodeList.size() > 0) {
            List<NodeView> list = this.nodeList;
            NodeView lastNode = list.get(list.size() - 1);
            if (this.isGlow) {
                canvas.drawLine((float) lastNode.getCenterX(), (float) lastNode.getCenterY(), this.x, this.y, this.brushPaint);
            }
            canvas.drawLine((float) lastNode.getCenterX(), (float) lastNode.getCenterY(), this.x, this.y, this.paint);
        }
    }

    private NodeView getNodeAt(float x2, float y2) {
        for (int n = 0; n < getChildCount(); n++) {
            NodeView node = (NodeView) getChildAt(n);
            if (x2 >= ((float) node.getLeft()) - this.nodeAreaExpand && x2 < ((float) node.getRight()) + this.nodeAreaExpand && y2 >= ((float) node.getTop()) - this.nodeAreaExpand && y2 < ((float) node.getBottom()) + this.nodeAreaExpand) {
                return node;
            }
        }
        return null;
    }

    private NodeView getNodeBetween(NodeView na, NodeView nb) {
        if (na.getNum() > nb.getNum()) {
            NodeView nc = na;
            na = nb;
            nb = nc;
        }
        if (na.getNum() % 3 == 1 && nb.getNum() - na.getNum() == 2) {
            return (NodeView) getChildAt(na.getNum());
        }
        if (na.getNum() <= 3 && nb.getNum() - na.getNum() == 6) {
            return (NodeView) getChildAt(na.getNum() + 2);
        }
        if ((na.getNum() == 1 && nb.getNum() == 9) || (na.getNum() == 3 && nb.getNum() == 7)) {
            return (NodeView) getChildAt(4);
        }
        return null;
    }

    private class NodeView extends View {
        private boolean highLighted = false;
        private final int num;

        public NodeView(Context context, int num2) {
            super(context);
            this.num = num2;
            setBackgroundDrawable(Lock9Views.this.nodeSrc);
        }

        public boolean isHighLighted() {
            return this.highLighted;
        }

        public void setHighLighted(boolean highLighted2, boolean isMid) {
            if (this.highLighted != highLighted2) {
                this.highLighted = highLighted2;
                if (Lock9Views.this.nodeOnSrc != null) {
                    Lock9Views lock9Views = Lock9Views.this;
                    setBackgroundDrawable(highLighted2 ? lock9Views.nodeOnSrc : lock9Views.nodeSrc);
                }
                if (Lock9Views.this.nodeOnAnim != 0) {
                    if (highLighted2) {
                        startAnimation(AnimationUtils.loadAnimation(getContext(), Lock9Views.this.nodeOnAnim));
                    } else {
                        clearAnimation();
                    }
                }

                if (Lock9Views.this.enableVibrate && !isMid && highLighted2) {
                    Lock9Views.vibrator.vibrate((long) Lock9Views.this.vibrateTime);
                }
            }
        }

        public int getCenterX() {
            return (getLeft() + getRight()) / 2;
        }

        public int getCenterY() {
            return (getTop() + getBottom()) / 2;
        }

        public int getNum() {
            return this.num;
        }
    }
}
