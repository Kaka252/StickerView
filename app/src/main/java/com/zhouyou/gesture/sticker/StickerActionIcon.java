package com.zhouyou.gesture.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 作者：ZhouYou
 * 日期：2016/8/31.
 */
public class StickerActionIcon {

    private Context context;
    // 资源缩放图片的位图
    private Bitmap srcIcon;
    private Rect rect;

    public StickerActionIcon(Context context) {
        this.context = context;
        rect = new Rect();
    }

    public void setSrcIcon(int resource) {
        srcIcon = BitmapFactory.decodeResource(context.getResources(), resource);
    }

    public void draw(Canvas canvas, float x, float y) {
        // 画顶点缩放图片
        rect.left = (int) (x - srcIcon.getWidth() / 2);
        rect.right = (int) (x + srcIcon.getWidth() / 2);
        rect.top = (int) (y - srcIcon.getHeight() / 2);
        rect.bottom = (int) (y + srcIcon.getHeight() / 2);
        canvas.drawBitmap(srcIcon, null, rect, null);
    }

    public Rect getRect() {
        return rect;
    }

}
