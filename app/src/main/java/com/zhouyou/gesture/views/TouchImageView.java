package com.zhouyou.gesture.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.zhouyou.gesture.R;

/**
 * 作者：ZhouYou
 * 日期：2016/8/23.
 */
public class TouchImageView extends ImageView {

    private Matrix matrix;
    // 资源图片的位图
    private Bitmap srcImage;
    // 触控模式
    private int mode;
    private static final int NONE = 0; // 无模式
    private static final int DRAG = 1; // 拖拽模式
    private static final int ZOOM = 2; // 缩放模式

    public TouchImageView(Context context) {
        this(context, null);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        srcImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_avatar_1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(srcImage, matrix, null);
    }

    // 手指按下屏幕的X坐标
    private float downX;
    // 手指按下屏幕的Y坐标
    private float downY;
    // 手指之间的初始距离
    private float oldDistance;
    // 手指之间的初始角度
    private float oldRotation;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // 多点触控
                mode = ZOOM;
                oldDistance = getSpaceDistance(event);
                oldRotation = getSpaceRotation(event);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取手指的旋转角度
     *
     * @param event
     * @return
     */
    private float getSpaceRotation(MotionEvent event) {
        int pointerCounts = event.getPointerCount();
        float x = 0;
        float y = 0;
        for (int i = 0; i < pointerCounts; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCounts;
        y = x / pointerCounts;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 获取手指间的距离
     *
     * @param event
     * @return
     */
    private float getSpaceDistance(MotionEvent event) {
        double deltaX = event.getX(0) - event.getX(1);
        double deltaY = event.getY(0) - event.getY(1);
        double radius = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radius);
    }


}
