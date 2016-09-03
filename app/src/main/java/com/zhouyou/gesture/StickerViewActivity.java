package com.zhouyou.gesture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zhouyou.gesture.sticker.StickerLayout;
import com.zhouyou.gesture.sticker.StickerUtils;

import java.io.File;

/**
 * 作者：ZhouYou
 * 日期：2016/8/25.
 */
public class StickerViewActivity extends Activity implements View.OnClickListener {

    private StickerLayout stickerLayout;
    private Thread t;
    private CompressTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_view);
        stickerLayout = (StickerLayout) findViewById(R.id.sticker_layout);
        stickerLayout.setBackgroundImage(R.mipmap.bg_scene);
        findViewById(R.id.tv_add_sticker).setOnClickListener(this);
        findViewById(R.id.tv_generate_preview).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_sticker:
                stickerLayout.addSticker(R.mipmap.ic_avatar_2);
                break;
            case R.id.tv_generate_preview:
                Bitmap dstBitmap = stickerLayout.generateCombinedBitmap();
                task = new CompressTask(dstBitmap);
                t = new Thread(task);
                t.start();
                break;
            default:
                break;
        }
    }

    private class CompressTask implements Runnable {

        private Bitmap dstBitmap;

        public CompressTask(Bitmap dstBitmap) {
            this.dstBitmap = dstBitmap;
        }

        @Override
        public void run() {
            if (dstBitmap == null) {
                handler.sendEmptyMessage(0);
                return;
            }
            File successFile = StickerUtils.getCacheFile();
            if (StickerUtils.saveBitmap(dstBitmap, successFile) && successFile != null && successFile.exists()) {
                handler.obtainMessage(1, successFile.toString()).sendToTarget();
            } else {
                handler.sendEmptyMessage(0);
            }
            StickerUtils.recycle(dstBitmap);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String path = (String) msg.obj;
                    Intent intent = new Intent(StickerViewActivity.this, PreviewActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "压缩图片失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(task);
        if (t != null) t = null;
        if (task != null) task = null;
    }
}
