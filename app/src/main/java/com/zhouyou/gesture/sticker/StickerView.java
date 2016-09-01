package com.zhouyou.gesture.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 作者：ZhouYou
 * 日期：2016/8/31.
 */
public class StickerView extends View {

    private Context context;
    // 被操作的贴纸对象
    private Sticker sticker;
    // 手指按下时图片的矩阵
    private Matrix downMatrix = new Matrix();
    // 手指移动时图片的矩阵
    private Matrix moveMatrix = new Matrix();
    // 多点触屏时的中心点
    private PointF midPoint = new PointF();
    // 图片的中心点坐标
    private PointF imageMidPoint = new PointF();

    // 触控模式
    private int mode;
    private static final int NONE = 0; // 无模式
    private static final int TRANS = 1; // 拖拽模式
    private static final int ROTATE = 2; // 单点旋转模式
    private static final int ZOOM_SINGLE = 3; // 单点缩放模式
    private static final int ZOOM_MULTI = 4; // 多点缩放模式

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sticker != null) {
            sticker.draw(canvas);
        }
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
                downX = event.getX();
                downY = event.getY();
                // 旋转手势验证
                if (sticker.isInActionCheck(event, sticker.getRotateIcon())) {
                    mode = ROTATE;
                    downMatrix.set(sticker.getMatrix());
                    imageMidPoint = sticker.getImageMidPoint(downMatrix);
                    oldRotation = sticker.getSpaceRotation(event, imageMidPoint);
                    Log.d("onTouchEvent", "旋转手势");
                }
                // 单点缩放手势验证
                else if (sticker.isInActionCheck(event, sticker.getZoomIcon())) {
                    mode = ZOOM_SINGLE;
                    downMatrix.set(sticker.getMatrix());
                    imageMidPoint = sticker.getImageMidPoint(downMatrix);
                    oldDistance = sticker.getSingleTouchDistance(event, imageMidPoint);
                    Log.d("onTouchEvent", "单点缩放手势");
                }
                // 平移手势验证
                else if (sticker.isWithinImageCheck(event, moveMatrix)) {
                    mode = TRANS;
                    downMatrix.set(sticker.getMatrix());
                    Log.d("onTouchEvent", "平移手势");
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // 多点触控
                mode = ZOOM_MULTI;
                oldDistance = sticker.getMultiTouchDistance(event);
                midPoint = sticker.getMidPoint(event);
                downMatrix.set(sticker.getMatrix());
                break;
            case MotionEvent.ACTION_MOVE:
                // 单点旋转
                if (mode == ROTATE) {
                    moveMatrix.set(downMatrix);
                    float deltaRotation = sticker.getSpaceRotation(event, imageMidPoint) - oldRotation;
                    moveMatrix.postRotate(deltaRotation, imageMidPoint.x, imageMidPoint.y);
                    sticker.getMatrix().set(moveMatrix);
                    invalidate();
                }
                // 单点缩放
                else if (mode == ZOOM_SINGLE) {
                    moveMatrix.set(downMatrix);
                    float scale = sticker.getSingleTouchDistance(event, imageMidPoint) / oldDistance;
                    moveMatrix.postScale(scale, scale, imageMidPoint.x, imageMidPoint.y);
                    sticker.getMatrix().set(moveMatrix);
                    invalidate();
                }
                // 多点缩放
                else if (mode == ZOOM_MULTI) {
                    moveMatrix.set(downMatrix);
                    float scale = sticker.getMultiTouchDistance(event) / oldDistance;
                    moveMatrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    sticker.getMatrix().set(moveMatrix);
                    invalidate();
                }
                // 平移
                else if (mode == TRANS) {
                    moveMatrix.set(downMatrix);
                    moveMatrix.postTranslate(event.getX() - downX, event.getY() - downY);
                    sticker.getMatrix().set(moveMatrix);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                mode = NONE;
                midPoint = null;
                imageMidPoint = null;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 添加一个贴纸
     *
     * @param resource
     */
    public void addSticker(int resource) {
        addSticker(BitmapFactory.decodeResource(getResources(), resource));
    }

    /**
     * 添加一个贴纸
     *
     * @param drawable
     */
    public void addSticker(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmap = (BitmapDrawable) drawable;
            addSticker(bitmap);
        }
    }

    /**
     * 添加一个贴纸
     *
     * @param bitmap
     */
    public void addSticker(Bitmap bitmap) {
        sticker = new Sticker(context, bitmap);
        sticker.getMatrix().postTranslate((getWidth() - sticker.getStickerWidth()) / 2, (getHeight() - sticker.getStickerHeight()) / 2);
        invalidate();
    }
}
