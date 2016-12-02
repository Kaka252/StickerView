package com.zhouyou.sticker;

import android.app.Application;
import android.os.Environment;

import java.io.File;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
public class Lib {

    private static Application app;

    public static void init(Application app) {
        Lib.app = app;
    }

    public static Application getInstance() {
        if (Lib.app == null) {
            throw new IllegalArgumentException("LBase application is null");
        }
        return Lib.app;
    }
}
