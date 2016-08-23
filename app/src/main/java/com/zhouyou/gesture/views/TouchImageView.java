package com.zhouyou.gesture.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 作者：ZhouYou
 * 日期：2016/8/23.
 */
public class TouchImageView extends ImageView {

    private Matrix matrix;
    private Matrix savedMatrix = new Matrix();
    private Matrix moveMatrix = new Matrix();
    // 资源图片的位图
    private Bitmap srcImage;
    // 多点触屏时的中心点
    private PointF midPoint = new PointF();
    // 触控模式
    private int mode;
    private static final int NONE = 0; // 无模式
    private static final int TRANS = 1; // 拖拽模式
    private static final int ZOOM = 2; // 缩放模式
    // 是否超过边界
    private boolean isOverBorder;

    private int widthScreen;
    private int heightScreen;

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
        setScaleType(ScaleType.MATRIX);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        widthScreen = dm.widthPixels;
        heightScreen = dm.heightPixels;
        matrix = new Matrix();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        srcImage = transDrawable2Bitmap(getDrawable());
    }

    private Bitmap transDrawable2Bitmap(Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }

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
                mode = TRANS;
                downX = event.getX();
                downY = event.getY();
                savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // 多点触控
                mode = ZOOM;
                oldDistance = getSpaceDistance(event);
                oldRotation = getSpaceRotation(event);
                savedMatrix.set(matrix);
                getMiddlePoint(midPoint, event);
                break;
            case MotionEvent.ACTION_MOVE:
                // 缩放
                if (mode == ZOOM) {
                    moveMatrix.set(savedMatrix);
                    float deltaRotation = getSpaceRotation(event) - oldRotation;
                    float scale = getSpaceDistance(event) / oldDistance;
                    moveMatrix.postScale(scale, scale, midPoint.x, midPoint.y);
                    moveMatrix.postRotate(deltaRotation, midPoint.x, midPoint.y);
                    isOverBorder = getMatrixBorderCheck();
                    if (!isOverBorder) {
                        matrix.set(moveMatrix);
                        invalidate();
                    }
                }
                // 平移
                else if (mode == TRANS) {
                    moveMatrix.set(savedMatrix);
                    moveMatrix.postTranslate(event.getX() - downX, event.getY() - downY);
                    isOverBorder = getMatrixBorderCheck();
                    if (!isOverBorder) {
                        matrix.set(moveMatrix);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                mode = NONE;
                break;
            default:
                break;
        }
        return true;
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
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 获取手势中心点
     *
     * @param point
     * @param event
     */
    private void getMiddlePoint(PointF point, MotionEvent event) {
        int pointerCounts = event.getPointerCount();
        float x = 0;
        float y = 0;
        for (int i = 0; i < pointerCounts; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCounts;
        y = x / pointerCounts;
        point.set(x, y);
    }

    private boolean getMatrixBorderCheck() {
        if (srcImage == null) return false;
        float[] f = new float[9];
        moveMatrix.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * srcImage.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * srcImage.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * srcImage.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * srcImage.getHeight() + f[5];
        float x4 = f[0] * srcImage.getWidth() + f[1] * srcImage.getHeight() + f[2];
        float y4 = f[3] * srcImage.getWidth() + f[4] * srcImage.getHeight() + f[5];
        // 图片现宽度
        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        // 缩放比率判断
        if (width < widthScreen / 3 || width > widthScreen * 3) {
            return true;
        }
        // 出界判断
        if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
                && x3 < widthScreen / 3 && x4 < widthScreen / 3)
                || (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
                && x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
                || (y1 < heightScreen / 3 && y2 < heightScreen / 3
                && y3 < heightScreen / 3 && y4 < heightScreen / 3)
                || (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3
                && y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
            return true;
        }
        return false;
    }


}
