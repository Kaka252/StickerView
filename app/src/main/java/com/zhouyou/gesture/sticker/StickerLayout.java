package com.zhouyou.gesture.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zhouyou.gesture.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ZhouYou
 * 日期：2016/9/3.
 */
public class StickerLayout extends FrameLayout {

    private Context context;
    // 贴纸的集合
    private List<StickerView> stickerViews;
    // 贴纸的View参数
    private FrameLayout.LayoutParams lp;

    public StickerLayout(Context context) {
        this(context, null);
    }

    public StickerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        stickerViews = new ArrayList<>();
        lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    /**
     * 新增贴纸
     */
    public void addSticker(int resource) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        addSticker(bitmap);
    }

    /**
     * 新增贴纸
     */
    public void addSticker(Bitmap bitmap) {
        final StickerView sv = new StickerView(context);
        sv.setImageBitmap(bitmap);
        sv.setEdit(true);
        sv.setLayoutParams(lp);
        sv.setOnStickerActionListener(new StickerView.OnStickerActionListener() {
            @Override
            public void onDelete() {
                // 处理删除操作
                removeView(sv);
                stickerViews.remove(sv);
                reset();
            }

            @Override
            public void onEdit(StickerView stickerView) {
                int position = stickerViews.indexOf(stickerView);
                stickerView.setEdit(true);
                stickerView.bringToFront();

                int size = stickerViews.size();
                for (int i = 0; i < size; i++) {
                    StickerView item = stickerViews.get(i);
                    if (item == null) continue;
                    if (position != i) {
                        item.setEdit(false);
                    }
                }
            }
        });
        addView(sv);
        stickerViews.add(sv);
    }

    /**
     * 重置贴纸的操作列表
     */
    private void reset() {
        int size = stickerViews.size();
        if (size <= 0) return;
        for (int i = size - 1; i >= 0; i--) {
            StickerView item = stickerViews.get(i);
            if (item == null) continue;
            if (i == size - 1) {
                item.setEdit(true);
            } else {
                item.setEdit(false);
            }
            stickerViews.set(i, item);
        }
    }
}
