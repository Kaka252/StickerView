package com.zhouyou.gesture;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.zhouyou.gesture.sticker.StickerView;

/**
 * 作者：ZhouYou
 * 日期：2016/8/25.
 */
public class StickerViewActivity extends Activity implements View.OnClickListener {

    private StickerView stickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_view);
        stickerView = (StickerView) findViewById(R.id.sticker_view);
        findViewById(R.id.btn_add_sticker).setOnClickListener(this);
        findViewById(R.id.btn_delete_sticker).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_sticker:
                stickerView.addSticker(R.mipmap.ic_avatar_1);
                break;
            case R.id.btn_delete_sticker:
                break;
            default:
                break;
        }
    }
}
