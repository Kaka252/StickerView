package com.zhouyou.sticker;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 * 贴纸操作模式
 */
public interface ActionMode {

    int NONE = 0; // 无模式
    int TRANS = 1; // 拖拽模式
    int ROTATE = 2; // 单点旋转模式
    int ZOOM_SINGLE = 3; // 单点缩放模式
    int ZOOM_MULTI = 4; // 多点缩放模式
}
