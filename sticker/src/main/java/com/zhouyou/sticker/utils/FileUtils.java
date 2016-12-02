package com.zhouyou.sticker.utils;

import android.os.Environment;

import com.zhouyou.sticker.Lib;

import java.io.File;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
public class FileUtils {

    private static File mCacheFile;

    /**
     * 获取临时图片的缓存路径
     *
     * @return
     */
    public static File getCacheFile() {
        File file = new File(getAppCacheDir(), "image");
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = "temp_" + System.currentTimeMillis() + ".jpg";
        return new File(file, fileName);
    }

    private static File getAppCacheDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            mCacheFile = Lib.getInstance().getExternalCacheDir();
        }
        if (mCacheFile == null) {
            mCacheFile = Lib.getInstance().getCacheDir();
        }
        return mCacheFile;
    }
}
