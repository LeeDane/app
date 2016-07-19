package com.leedane.cn.emoji;

/**
 * emoji对象
 * Created by LeeDane on 2016/7/5.
 */
public class EmojiBean {
    private final int resId; // 图片资源地址
    private final int value; // 一个emoji对应唯一一个value
    private final String emojiStr; // emoji在互联网传递的字符串

    public EmojiBean(int id, int value, String name) {
        this.resId = id;
        this.value = value;
        this.emojiStr = name;
    }
    public int getResId() {
        return resId;
    }

    public int getValue() {
        return value;
    }

    public String getEmojiStr() {
        return emojiStr;
    }
}
