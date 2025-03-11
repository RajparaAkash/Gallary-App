package com.gallaryapp.privacyvault.photoeditor.EditViews;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;

public class MultiTouchHandlers implements Parcelable {

    public static final Creator<MultiTouchHandlers> CREATOR = new Creator<MultiTouchHandlers>() {

        @Override
        public MultiTouchHandlers createFromParcel(Parcel in2) {
            return new MultiTouchHandlers(in2);
        }

        
        @Override
        public MultiTouchHandlers[] newArray(int size) {
            return new MultiTouchHandlers[size];
        }
    };
    private PointF mCheckingPosition;
    private float mD;
    private boolean mEnableRotation;
    private boolean mEnableTranslateX;
    private boolean mEnableTranslateY;
    private boolean mEnableZoom;
    private float[] mLastEvent;
    private Matrix mMatrix;
    private float mMaxPositionOffset;
    private PointF mMid;
    private int mMode;
    private float mNewRot;
    private float mOldDist;
    private PointF mOldImagePosition;
    private Matrix mSavedMatrix;
    private float mScale;
    private Matrix mScaleMatrix;
    private Matrix mScaleSavedMatrix;
    private PointF mStart;

    public MultiTouchHandlers() {
        this.mMatrix = new Matrix();
        this.mSavedMatrix = new Matrix();
        this.mMode = 0;
        this.mStart = new PointF();
        this.mMid = new PointF();
        this.mOldDist = 1.0f;
        this.mD = 0.0f;
        this.mNewRot = 0.0f;
        this.mLastEvent = null;
        this.mEnableRotation = false;
        this.mEnableZoom = true;
        this.mEnableTranslateX = true;
        this.mEnableTranslateY = true;
        this.mScale = 1.0f;
        this.mScaleMatrix = new Matrix();
        this.mScaleSavedMatrix = new Matrix();
        this.mMaxPositionOffset = -1.0f;
        this.mOldImagePosition = new PointF(0.0f, 0.0f);
        this.mCheckingPosition = new PointF(0.0f, 0.0f);
    }

    private MultiTouchHandlers(Parcel in2) {
        this.mMatrix = new Matrix();
        this.mSavedMatrix = new Matrix();
        this.mMode = 0;
        this.mStart = new PointF();
        this.mMid = new PointF();
        this.mOldDist = 1.0f;
        this.mD = 0.0f;
        this.mNewRot = 0.0f;
        this.mLastEvent = null;
        this.mEnableRotation = false;
        this.mEnableZoom = true;
        this.mEnableTranslateX = true;
        this.mEnableTranslateY = true;
        this.mScale = 1.0f;
        this.mScaleMatrix = new Matrix();
        this.mScaleSavedMatrix = new Matrix();
        this.mMaxPositionOffset = -1.0f;
        this.mOldImagePosition = new PointF(0.0f, 0.0f);
        this.mCheckingPosition = new PointF(0.0f, 0.0f);
        float[] values = new float[9];
        in2.readFloatArray(values);
        Matrix matrix = new Matrix();
        this.mMatrix = matrix;
        matrix.setValues(values);
        float[] values2 = new float[9];
        in2.readFloatArray(values2);
        Matrix matrix2 = new Matrix();
        this.mSavedMatrix = matrix2;
        matrix2.setValues(values2);
        this.mMode = in2.readInt();
        this.mStart = (PointF) in2.readParcelable(PointF.class.getClassLoader());
        this.mMid = (PointF) in2.readParcelable(PointF.class.getClassLoader());
        this.mOldDist = in2.readFloat();
        this.mD = in2.readFloat();
        this.mNewRot = in2.readFloat();
        boolean[] b = new boolean[4];
        in2.readBooleanArray(b);
        this.mEnableRotation = b[0];
        this.mEnableZoom = b[1];
        this.mEnableTranslateX = b[2];
        this.mEnableTranslateY = b[3];
        this.mScale = in2.readFloat();
        float[] values3 = new float[9];
        in2.readFloatArray(values3);
        Matrix matrix3 = new Matrix();
        this.mScaleMatrix = matrix3;
        matrix3.setValues(values3);
        float[] values4 = new float[9];
        in2.readFloatArray(values4);
        Matrix matrix4 = new Matrix();
        this.mScaleSavedMatrix = matrix4;
        matrix4.setValues(values4);
        this.mMaxPositionOffset = in2.readFloat();
        this.mOldImagePosition = (PointF) in2.readParcelable(PointF.class.getClassLoader());
        this.mCheckingPosition = (PointF) in2.readParcelable(PointF.class.getClassLoader());
    }

    public void setMatrices(Matrix matrix, Matrix scaleMatrix) {
        this.mMatrix.set(matrix);
        this.mSavedMatrix.set(matrix);
        this.mScaleMatrix.set(scaleMatrix);
        this.mScaleSavedMatrix.set(scaleMatrix);
    }

    public void touch(MotionEvent event) {
        switch (event.getAction() & 255) {
            case 0:
                this.mSavedMatrix.set(this.mMatrix);
                this.mScaleSavedMatrix.set(this.mScaleMatrix);
                this.mStart.set(event.getX(), event.getY());
                PointF pointF = this.mOldImagePosition;
                PointF pointF2 = this.mCheckingPosition;
                pointF.set(pointF2.x, pointF2.y);
                this.mMode = 1;
                this.mLastEvent = null;
                return;
            case 1:
            case 6:
                this.mMode = 0;
                this.mLastEvent = null;
                return;
            case 2:
                int i = this.mMode;
                if (i == 1) {
                    this.mMatrix.set(this.mSavedMatrix);
                    this.mScaleMatrix.set(this.mScaleSavedMatrix);
                    PointF pointF3 = this.mCheckingPosition;
                    PointF pointF4 = this.mOldImagePosition;
                    pointF3.set(pointF4.x, pointF4.y);
                    float dx = event.getX() - this.mStart.x;
                    float dy = event.getY() - this.mStart.y;
                    PointF pointF5 = this.mCheckingPosition;
                    float f = pointF5.x + dx;
                    pointF5.x = f;
                    float f2 = pointF5.y + dy;
                    pointF5.y = f2;
                    if (!this.mEnableTranslateX) {
                        dx = 0.0f;
                        float f3 = this.mMaxPositionOffset;
                        if (f2 > f3) {
                            dy -= f2 - f3;
                            pointF5.y = f3;
                        } else if (f2 < (-f3)) {
                            dy -= f2 + f3;
                            pointF5.y = -f3;
                        }
                    }
                    if (!this.mEnableTranslateY) {
                        dy = 0.0f;
                        float f4 = this.mMaxPositionOffset;
                        if (f > f4) {
                            dx -= f - f4;
                            pointF5.x = f4;
                        } else if (f < (-f4)) {
                            dx -= f + f4;
                            pointF5.x = -f4;
                        }
                    }
                    this.mMatrix.postTranslate(dx, dy);
                    Matrix matrix = this.mScaleMatrix;
                    float f5 = this.mScale;
                    matrix.postTranslate(dx * f5, f5 * dy);
                    return;
                } else if (i == 2 && this.mEnableZoom) {
                    float newDist = spacing(event);
                    if (newDist > 10.0f) {
                        this.mMatrix.set(this.mSavedMatrix);
                        this.mScaleMatrix.set(this.mScaleSavedMatrix);
                        float scale = newDist / this.mOldDist;
                        Matrix matrix2 = this.mMatrix;
                        PointF pointF6 = this.mMid;
                        matrix2.postScale(scale, scale, pointF6.x, pointF6.y);
                        Matrix matrix3 = this.mScaleMatrix;
                        PointF pointF7 = this.mMid;
                        float f6 = pointF7.x;
                        float f7 = this.mScale;
                        matrix3.postScale(scale, scale, f6 * f7, pointF7.y * f7);
                    }
                    if (this.mEnableRotation && this.mLastEvent != null && event.getPointerCount() == 2) {
                        this.mNewRot = rotation(event);
                        midPoint(this.mMid, event);
                        float r = this.mNewRot - this.mD;
                        Matrix matrix4 = this.mMatrix;
                        PointF pointF8 = this.mMid;
                        matrix4.postRotate(r, pointF8.x, pointF8.y);
                        Matrix matrix5 = this.mScaleMatrix;
                        PointF pointF9 = this.mMid;
                        float f8 = pointF9.x;
                        float f9 = this.mScale;
                        matrix5.postRotate(r, f8 * f9, pointF9.y * f9);
                        return;
                    }
                    return;
                } else {
                    return;
                }
            case 3:
            case 4:
            default:
                return;
            case 5:
                float spacing = spacing(event);
                this.mOldDist = spacing;
                if (spacing > 10.0f) {
                    this.mSavedMatrix.set(this.mMatrix);
                    this.mScaleSavedMatrix.set(this.mScaleMatrix);
                    midPoint(this.mMid, event);
                    this.mMode = 2;
                }
                float[] fArr = new float[4];
                this.mLastEvent = fArr;
                fArr[0] = event.getX(0);
                this.mLastEvent[1] = event.getX(1);
                this.mLastEvent[2] = event.getY(0);
                this.mLastEvent[3] = event.getY(1);
                this.mD = rotation(event);
                return;
        }
    }

    public Matrix getMatrix() {
        return this.mMatrix;
    }

    public Matrix getScaleMatrix() {
        return this.mScaleMatrix;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public void setEnableRotation(boolean enableRotation) {
        this.mEnableRotation = enableRotation;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt((x * x) + (y * y));
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2.0f, y / 2.0f);
    }

    private float rotation(MotionEvent event) {
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        float[] values = new float[9];
        this.mMatrix.getValues(values);
        dest.writeFloatArray(values);
        float[] values2 = new float[9];
        this.mSavedMatrix.getValues(values2);
        dest.writeFloatArray(values2);
        dest.writeInt(this.mMode);
        dest.writeParcelable(this.mStart, flags);
        dest.writeParcelable(this.mMid, flags);
        dest.writeFloat(this.mOldDist);
        dest.writeFloat(this.mD);
        dest.writeFloat(this.mNewRot);
        boolean[] b = {this.mEnableRotation, this.mEnableZoom, this.mEnableTranslateX, this.mEnableTranslateY};
        dest.writeBooleanArray(b);
        dest.writeFloat(this.mScale);
        float[] values3 = new float[9];
        this.mScaleMatrix.getValues(values3);
        dest.writeFloatArray(values3);
        float[] values4 = new float[9];
        this.mScaleSavedMatrix.getValues(values4);
        dest.writeFloatArray(values4);
        dest.writeFloat(this.mMaxPositionOffset);
        dest.writeParcelable(this.mOldImagePosition, flags);
        dest.writeParcelable(this.mCheckingPosition, flags);
    }
}
