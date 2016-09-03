package com.zhouyou.gesture.sticker;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.widget.Toast;

import com.zhouyou.gesture.base.App;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zhouyou on 16/9/1.
 */
public class StickerUtils {


    /**
     * 将matrix的点映射成坐标点
     *
     * @return
     */
    public static float[] getBitmapPoints(Bitmap bitmap, Matrix matrix) {
        float[] dst = new float[8];
        float[] src = new float[]{
                0, 0,
                bitmap.getWidth(), 0,
                0, bitmap.getHeight(),
                bitmap.getWidth(), bitmap.getHeight()
        };
        matrix.mapPoints(dst, src);
        return dst;
    }

    /**
     * 获取资源的URI
     *
     * @param resId
     * @return
     */
    public static Uri getResouceUri(int resId) {
        return Uri.parse("res://" + App.get().getPackageName() + "/" + resId);
    }

    public static Uri getFileUri(File file) {
        if (file == null || !file.exists()) return null;
        return Uri.parse("file://" + file.getAbsolutePath());
    }

    /**
     * 保存最终合成的bitmap，生成本地路径
     *
     * @param bitmap
     * @return
     */
    public static String saveBitmap(Bitmap bitmap) {
        String imagePath = App.get().getFilesDir().getAbsolutePath() + "/zhouyou.jpg";
        File file = new File(imagePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }
}
