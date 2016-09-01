package com.zhouyou.gesture.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
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

    }

    public void draw(Canvas canvas) {

        // 画边框
//        canvas.drawLine(x1, y1, x2, y2, paintEdge);
//        canvas.drawLine(x2, y2, x4, y4, paintEdge);
//        canvas.drawLine(x4, y4, x3, y3, paintEdge);
//        canvas.drawLine(x3, y3, x1, y1, paintEdge);
        // 画图片
        canvas.drawBitmap(srcImage, matrix, null);

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
        float[] points = StickerUtils.getBitmapPoints(srcImage, matrix);
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

    public RectF getSrcImageBound() {
        RectF dst = new RectF();
        matrix.mapRect(dst, new RectF(0, 0, getStickerWidth(), getStickerHeight()));
        return dst;
    }

    public int getStickerWidth() {
        return srcImage == null ? 0 : srcImage.getWidth();
    }

    public int getStickerHeight() {
        return srcImage == null ? 0 : srcImage.getHeight();
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public Bitmap getSrcImage() {
        return srcImage;
    }
}
