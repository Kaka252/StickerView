package com.zhouyou.gesture.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.zhouyou.gesture.R;

/**
 * 作者：ZhouYou
 * 日期：2016/8/31.
 */
public class Sticker {
    private Context context;
    // 绘制图片的边框
    private Paint paintEdge;
    // 绘制图片的矩阵
    private Matrix matrix;
    // 原图片
    private Bitmap srcImage;
    // 旋转操作图片
    private StickerActionIcon rotateIcon;
    // 缩放操作图片
    private StickerActionIcon zoomIcon;

    public Sticker(Context context, Bitmap bitmap) {
        this.context = context;
        this.srcImage = bitmap;
        init();
    }

    private void init() {
        matrix = new Matrix();
        paintEdge = new Paint();
        paintEdge.setColor(Color.BLACK);
        paintEdge.setAlpha(170);
        paintEdge.setAntiAlias(true);

        rotateIcon = new StickerActionIcon(context);
        rotateIcon.setSrcIcon(R.mipmap.ic_rotate);
        zoomIcon = new StickerActionIcon(context);
        zoomIcon.setSrcIcon(R.mipmap.ic_resize);
    }

    public void draw(Canvas canvas) {
        float[] points = getBitmapPoints(srcImage, matrix);
        float x1 = points[0];
        float y1 = points[1];
        float x2 = points[2];
        float y2 = points[3];
        float x3 = points[4];
        float y3 = points[5];
        float x4 = points[6];
        float y4 = points[7];
        // 画边框
        canvas.drawLine(x1, y1, x2, y2, paintEdge);
        canvas.drawLine(x2, y2, x4, y4, paintEdge);
        canvas.drawLine(x4, y4, x3, y3, paintEdge);
        canvas.drawLine(x3, y3, x1, y1, paintEdge);
        // 画图片
        canvas.drawBitmap(srcImage, matrix, null);
        // 画操作按钮图片
        rotateIcon.draw(canvas, x2, y2);
        zoomIcon.draw(canvas, x3, y3);
    }

    /**
     * 检查边界
     *
     * @return true - 在边界内 ｜ false － 超出边界
     */
    public boolean isWithinImageCheck(MotionEvent event, Matrix moveMatrix) {
        if (srcImage == null) return false;
        float x = event.getX();
        float y = event.getY();
        float[] points = getBitmapPoints(srcImage, moveMatrix);
        float x1 = points[0];
        float y1 = points[1];
        float x2 = points[2];
        float y2 = points[3];
        float x3 = points[4];
        float y3 = points[5];
        float x4 = points[6];
        float y4 = points[7];
        float edge = (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return (2 + Math.sqrt(2)) * edge >= Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2))
                + Math.sqrt(Math.pow(x - x2, 2) + Math.pow(y - y2, 2))
                + Math.sqrt(Math.pow(x - x3, 2) + Math.pow(y - y3, 2))
                + Math.sqrt(Math.pow(x - x4, 2) + Math.pow(y - y4, 2));
    }

    /**
     * 将matrix的点映射成坐标点
     *
     * @return
     */
    public float[] getBitmapPoints(Bitmap bitmap, Matrix matrix) {
        float[] dst = new float[8];
        float[] src = new float[]{
                0, 0,
                bitmap.getWidth(), 0,
                0, bitmap.getHeight(),
                bitmap.getWidth(), bitmap.getHeight()
        };
        matrix.mapPoints(dst, src);
        return dst;
    }

    /**
     * 获取手势中心点
     *
     * @param event
     */
    public PointF getMidPoint(MotionEvent event) {
        PointF point = new PointF();
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
        return point;
    }

    /**
     * 获取图片中心点
     */
    public PointF getImageMidPoint(Matrix matrix) {
        PointF point = new PointF();
        float[] points = getBitmapPoints(srcImage, matrix);
        float x1 = points[0];
        float x2 = points[2];
        float y2 = points[3];
        float y4 = points[7];
        point.set((x1 + x2) / 2, (y2 + y4) / 2);
        return point;
    }

    /**
     * 获取手指的旋转角度
     *
     * @param event
     * @return
     */
    public float getSpaceRotation(MotionEvent event, PointF imageMidPoint) {
        double deltaX = event.getX(0) - imageMidPoint.x;
        double deltaY = event.getY(0) - imageMidPoint.y;
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 【多点缩放】获取手指间的距离
     *
     * @param event
     * @return
     */
    public float getMultiTouchDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 【单点缩放】获取手指和图片中心点的距离
     *
     * @param event
     * @return
     */
    public float getSingleTouchDistance(MotionEvent event, PointF imageMidPoint) {
        float x = event.getX(0) - imageMidPoint.x;
        float y = event.getY(0) - imageMidPoint.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 判断手指触摸的区域是否在顶点的操作按钮内
     *
     * @param event
     * @param actionIcon
     * @return
     */
    public boolean isInActionCheck(MotionEvent event, StickerActionIcon actionIcon) {
        if (actionIcon == null) return false;
        if (actionIcon.getRect() == null) return false;
        Rect rect = actionIcon.getRect();
        int left = rect.left;
        int right = rect.right;
        int top = rect.top;
        int bottom = rect.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public StickerActionIcon getRotateIcon() {
        return rotateIcon;
    }

    public StickerActionIcon getZoomIcon() {
        return zoomIcon;
    }
}
