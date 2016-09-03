package com.zhouyou.gesture;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 作者：ZhouYou
 * 日期：2016/9/3.
 */
public class PreviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.activity_preview);
        ImageView ivPreview = (ImageView) findViewById(R.id.iv_preview);
        ivPreview.setImageURI(Uri.parse(path));
    }
}
