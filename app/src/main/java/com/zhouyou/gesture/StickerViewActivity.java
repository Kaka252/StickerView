package com.zhouyou.gesture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.zhouyou.gesture.sticker.StickerLayout;
import com.zhouyou.gesture.sticker.StickerUtils;

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
        findViewById(R.id.btn_generate_preview).setOnClickListener(this);
        stickerLayout = (StickerLayout) findViewById(R.id.sticker_layout);
        stickerLayout.setBackgroundImage(R.mipmap.bg_scene);
        stickerLayout.addSticker(R.mipmap.ic_avatar_2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_generate_preview:
                Bitmap bitmap = stickerLayout.generateCombinedBitmap();
                String path = StickerUtils.saveBitmap(bitmap);
                Intent intent = new Intent(StickerViewActivity.this, PreviewActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
