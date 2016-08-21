package com.zhouyou.gesture.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by zhouyou on 16/8/21.
 */
public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener {

    private static final String TAG = ZoomImageView.class.getSimpleName();
    // 最大缩放倍数
    private static final float MAX_SCALE = 4.0f;
    // 初始缩放倍数
    private float initScale = 1.0f;
    // 存放矩阵的9个值
    private float[] matrixValues = new float[9];
    // 只初始一次
    private boolean loadOnce = false;
    // 缩放手势的监听
    private ScaleGestureDetector scaleGestureDetector;
    // 缩放矩阵
    private Matrix matrix;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        matrix = new Matrix();
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (getDrawable() == null) return true;
                // 当前的图片的缩放倍数
                float currScale = getCurrentScale();
                // 缩放因子
                float scaleFactor = detector.getScaleFactor();
                if ((currScale > initScale && scaleFactor < 1.0f) || (currScale < MAX_SCALE && scaleFactor > 1.0f)) {
                    // 最小缩放比例
                    if (scaleFactor * currScale < initScale) {
                        Log.d(TAG, "正在缩小");
                        scaleFactor = initScale / currScale;
                    }
                    // 最大缩放比例
                    if (scaleFactor * currScale > MAX_SCALE) {
                        Log.d(TAG, "正在放大");
                        scaleFactor = MAX_SCALE / currScale;
                    }
                    matrix.postScale(scaleFactor, scaleFactor, getWidth() / 2, getHeight() / 2);
                    setImageMatrix(matrix);
                }
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
        setOnTouchListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 初始化图片的缩放倍数，并初始化图片的位置
     */
    @Override
    public void onGlobalLayout() {
        if (!loadOnce) {
            Drawable d = getDrawable();
            if (d == null) return;
            // 获取控件的宽高
            int width = getWidth();
            int height = getHeight();
            // 获取图片的宽高
            int w = d.getIntrinsicWidth();
            int h = d.getIntrinsicHeight();

            float scale = 1.0f;
            // 图片宽大于屏幕
            if (w > width && h <= height) {
                scale = width * 1.0f / w;
            }
            // 图片高大于屏幕
            if (w <= width && h > height) {
                scale = height * 1.0f / h;
            }
            // 图片宽高均大于屏幕
            if (w > width && h > height) {
                scale = Math.min(width * 1.0f / w, height * 1.0f / h);
            }
            initScale = scale;
            // 把图片平移到控件的中心位置
            matrix.postTranslate((width - w) / 2, (height - h) / 2);
            matrix.postScale(scale, scale, width / 2, height / 2);
            setImageMatrix(matrix);
            loadOnce = true;
        }
    }

    /**
     * 获得当前图片的缩放比例
     *
     * @return
     */
    private float getCurrentScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }
}
