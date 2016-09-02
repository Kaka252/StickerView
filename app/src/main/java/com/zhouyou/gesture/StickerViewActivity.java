package com.zhouyou.gesture;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.zhouyou.gesture.sticker.Sticker;
import com.zhouyou.gesture.sticker.StickerView;

/**
 * 作者：ZhouYou
 * 日期：2016/8/25.
 */
public class StickerViewActivity extends Activity implements View.OnClickListener {

    private FrameLayout flContent;

    private StickerView stickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_view);
        flContent = (FrameLayout) findViewById(R.id.fl_content);
        findViewById(R.id.btn_add_sticker).setOnClickListener(this);
        findViewById(R.id.btn_delete_sticker).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_sticker:
                addSticker();
                break;
            case R.id.btn_delete_sticker:
                removeSticker();
                break;
            default:
                break;
        }
    }

    private void addSticker() {
        stickerView = new StickerView(this);
        stickerView.setImageResource(R.mipmap.ic_avatar_1);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        stickerView.setLayoutParams(lp);
        flContent.addView(stickerView);
    }

    private void removeSticker() {
        flContent.removeView(stickerView);
    }
}
