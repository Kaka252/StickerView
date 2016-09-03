package com.zhouyou.gesture;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.zhouyou.gesture.sticker.Sticker;
import com.zhouyou.gesture.sticker.StickerLayout;
import com.zhouyou.gesture.sticker.StickerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ZhouYou
 * 日期：2016/8/25.
 */
public class StickerViewActivity extends Activity implements View.OnClickListener {

    private StickerLayout stickerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_view);
        findViewById(R.id.btn_add_sticker).setOnClickListener(this);
        stickerLayout = (StickerLayout) findViewById(R.id.sticker_layout);
        stickerLayout.setBackgroundImage(R.mipmap.bg_scene);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_sticker:
                stickerLayout.addSticker(R.mipmap.ic_avatar_2);
                break;
            default:
                break;
        }
    }
}
