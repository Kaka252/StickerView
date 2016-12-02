package com.zhouyou.sticker;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
public interface OnStickerActionListener {

    /*删除贴纸*/
    void onDelete();

    /*编辑贴纸*/
    void onEdit(StickerView stickerView);
}
