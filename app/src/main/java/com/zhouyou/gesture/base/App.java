package com.zhouyou.gesture.base;

import android.app.Application;
import android.os.Environment;

import com.zhouyou.sticker.Lib;

import java.io.File;

/**
 * 作者：ZhouYou
 * 日期：2016/9/3.
 */
public class App extends Application {

    private static App instance;

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Lib.init(this);
    }
}
