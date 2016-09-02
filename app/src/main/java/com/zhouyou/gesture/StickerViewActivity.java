package com.zhouyou.gesture;

import android.app.Activity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_view);
        flContent = (FrameLayout) findViewById(R.id.fl_content);
        findViewById(R.id.btn_add_sticker).setOnClickListener(this);
        findViewById(R.id.btn_delete_sticker).setOnClickListener(this);
        stickerViews = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_sticker:
                addSticker();
                break;
            case R.id.btn_delete_sticker:
                removeStickers();
                break;
            default:
                break;
        }
    }

    private void addSticker() {
        for (int i = 0; i < stickerViews.size(); i++) {
            StickerView item = stickerViews.get(i);
            if (item == null) continue;
            if (item.isEdit()) {
                item.setEdit(false);
                item.postInvalidate();
                stickerViews.set(i, item);
            }
        }
        StickerView sv = new StickerView(this);
        sv.setImageResource(R.mipmap.ic_avatar_1);
        sv.setEdit(true);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(lp);
        flContent.addView(sv);
        stickerViews.add(sv);
    }

    private void removeStickers() {
        stickerViews.clear();
        flContent.removeAllViews();
    }
}
