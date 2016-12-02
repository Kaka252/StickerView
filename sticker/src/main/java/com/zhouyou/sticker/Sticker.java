package com.zhouyou.sticker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.zhouyou.sticker.utils.PointUtils;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
public class Sticker {
    // 绘制图片的矩阵
    private Matrix matrix;
    // 原图片
    private Bitmap srcImage;

    Sticker(Bitmap bitmap) {
        this.srcImage = bitmap;
        matrix = new Matrix();
    }

    /**
     * 绘制图片
     *
     * @param canvas
     */
    void draw(Canvas canvas) {
        canvas.drawBitmap(srcImage, matrix, null);
    }

    /**
     * 获取手势中心点
     *
     * @param event
     */
    PointF getMidPoint(MotionEvent event) {
        PointF point = new PointF();
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
        return point;
    }

    /**
     * 获取图片中心点
     */
    PointF getImageMidPoint(Matrix matrix) {
        PointF point = new PointF();
        float[] points = PointUtils.getBitmapPoints(srcImage, matrix);
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
    float getSpaceRotation(MotionEvent event, PointF imageMidPoint) {
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
    float getMultiTouchDistance(MotionEvent event) {
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
    float getSingleTouchDistance(MotionEvent event, PointF imageMidPoint) {
        float x = event.getX(0) - imageMidPoint.x;
        float y = event.getY(0) - imageMidPoint.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    RectF getSrcImageBound() {
        RectF dst = new RectF();
        matrix.mapRect(dst, new RectF(0, 0, getStickerWidth(), getStickerHeight()));
        return dst;
    }

    int getStickerWidth() {
        return srcImage == null ? 0 : srcImage.getWidth();
    }

    int getStickerHeight() {
        return srcImage == null ? 0 : srcImage.getHeight();
    }

    Matrix getMatrix() {
        return matrix;
    }

    Bitmap getSrcImage() {
        return srcImage;
    }
}
