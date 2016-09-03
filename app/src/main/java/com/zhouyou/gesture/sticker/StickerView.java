package com.zhouyou.gesture.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.zhouyou.gesture.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ZhouYou
 * 日期：2016/8/31.
 */
public class StickerView extends ImageView {

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
    // 旋转操作图片
    private StickerActionIcon rotateIcon;
    // 缩放操作图片
    private StickerActionIcon zoomIcon;
    // 缩放操作图片
    private StickerActionIcon deleteIcon;
    // 绘制图片的边框
    private Paint paintEdge;

    // 触控模式
    private int mode;
    private static final int NONE = 0; // 无模式
    private static final int TRANS = 1; // 拖拽模式
    private static final int ROTATE = 2; // 单点旋转模式
    private static final int ZOOM_SINGLE = 3; // 单点缩放模式
    private static final int ZOOM_MULTI = 4; // 多点缩放模式

    // 是否正在处于编辑
    private boolean isEdit = true;

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setScaleType(ScaleType.MATRIX);
        rotateIcon = new StickerActionIcon(context);
        rotateIcon.setSrcIcon(R.mipmap.ic_rotate);
        zoomIcon = new StickerActionIcon(context);
        zoomIcon.setSrcIcon(R.mipmap.ic_resize);
        deleteIcon = new StickerActionIcon(context);
        deleteIcon.setSrcIcon(R.mipmap.ic_delete);
        paintEdge = new Paint();
        paintEdge.setColor(Color.BLACK);
        paintEdge.setAlpha(170);
        paintEdge.setAntiAlias(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            sticker.getMatrix().postTranslate((getWidth() - sticker.getStickerWidth()) / 2, (getHeight() - sticker.getStickerHeight()) / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (sticker == null) return;
        sticker.draw(canvas);
        float[] points = StickerUtils.getBitmapPoints(sticker.getSrcImage(), sticker.getMatrix());
        float x1 = points[0];
        float y1 = points[1];
        float x2 = points[2];
        float y2 = points[3];
        float x3 = points[4];
        float y3 = points[5];
        float x4 = points[6];
        float y4 = points[7];
        if (isEdit) {
            // 画边框
            canvas.drawLine(x1, y1, x2, y2, paintEdge);
            canvas.drawLine(x2, y2, x4, y4, paintEdge);
            canvas.drawLine(x4, y4, x3, y3, paintEdge);
            canvas.drawLine(x3, y3, x1, y1, paintEdge);
            // 画操作按钮图片
            rotateIcon.draw(canvas, x2, y2);
            zoomIcon.draw(canvas, x3, y3);
            deleteIcon.draw(canvas, x1, y1);
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
        boolean isStickerOnEdit = true;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                if (sticker == null) return false;
                // 删除操作
                if (deleteIcon.isInActionCheck(event)) {
                    if (listener != null) listener.onDelete();
                }
                // 旋转手势验证
                else if (rotateIcon.isInActionCheck(event)) {
                    mode = ROTATE;
                    downMatrix.set(sticker.getMatrix());
                    imageMidPoint = sticker.getImageMidPoint(downMatrix);
                    oldRotation = sticker.getSpaceRotation(event, imageMidPoint);
                    Log.d("onTouchEvent", "旋转手势");
                }
                // 单点缩放手势验证
                else if (zoomIcon.isInActionCheck(event)) {
                    mode = ZOOM_SINGLE;
                    downMatrix.set(sticker.getMatrix());
                    imageMidPoint = sticker.getImageMidPoint(downMatrix);
                    oldDistance = sticker.getSingleTouchDistance(event, imageMidPoint);
                    Log.d("onTouchEvent", "单点缩放手势");
                }
                // 平移手势验证
                else if (isInStickerArea(sticker, event)) {
                    mode = TRANS;
                    downMatrix.set(sticker.getMatrix());
                    Log.d("onTouchEvent", "平移手势");
                } else {
                    isStickerOnEdit = false;
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
        if (isStickerOnEdit && listener != null) {
            listener.onEdit(this);
        }
        return isStickerOnEdit;
    }

    /**
     * 判断手指是否在操作区域内
     *
     * @param sticker
     * @param event
     * @return
     */
    private boolean isInStickerArea(Sticker sticker, MotionEvent event) {
        RectF dst = sticker.getSrcImageBound();
        return dst.contains(event.getX(), event.getY());
    }

    /**
     * 添加贴纸
     *
     * @param resId
     */
    @Override
    public void setImageResource(int resId) {
        sticker = new Sticker(context, resId);
    }

    /**
     * 设置是否贴纸正在处于编辑状态
     *
     * @param edit
     */
    public void setEdit(boolean edit) {
        isEdit = edit;
        postInvalidate();
    }

    public boolean isEdit() {
        return isEdit;
    }


    private OnStickerActionListener listener;

    public void setOnStickerActionListener(OnStickerActionListener listener) {
        this.listener = listener;
    }

    public interface OnStickerActionListener {
        /*删除贴纸*/
        void onDelete();

        /*编辑贴纸*/
        void onEdit(StickerView stickerView);
    }
}
