package com.zhouyou.gesture;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.zhouyou.gesture.sticker.Sticker;
import com.zhouyou.gesture.sticker.StickerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ZhouYou
 * 日期：2016/8/25.
 */
public class StickerViewActivity extends Activity implements View.OnClickListener {

    private FrameLayout flContent;

    private List<StickerView> stickerViews;

    private FrameLayout.LayoutParams lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_view);
        flContent = (FrameLayout) findViewById(R.id.fl_content);
        findViewById(R.id.btn_add_sticker).setOnClickListener(this);
        stickerViews = new ArrayList<>();
        lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_sticker:
                addSticker();
                reset();
                break;
            default:
                break;
        }
    }

    private void addSticker() {
        final StickerView sv = new StickerView(this);
        sv.setImageResource(R.mipmap.ic_avatar_1);
        sv.setEdit(true);
        sv.setLayoutParams(lp);
        sv.setOnStickerActionListener(new StickerView.OnStickerActionListener() {
            @Override
            public void onDelete() {
                // 处理删除操作
                flContent.removeView(sv);
                stickerViews.remove(sv);
                reset();
            }

            @Override
            public void onEdit(StickerView stickerView) {

                stickerView.setEdit(true);
                stickerView.bringToFront();
//                int position = stickerViews.indexOf(stickerView);
//                if (position == stickerViews.size() - 1) return;
//                if (position < 0 && position >= stickerViews.size()) return;
//                Log.d("Sticker", "onEdit: " + position + " | size = " + stickerViews.size());
//                StickerView removedSticker = stickerViews.remove(position);
//                stickerViews.add(stickerViews.size(), removedSticker);
//                reset();
            }
        });
        flContent.addView(sv);
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
                item.bringToFront();
                item.setEdit(true);
            } else {
                item.setEdit(false);
            }
            stickerViews.set(i, item);
        }
    }
}
