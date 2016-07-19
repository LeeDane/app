package com.leedane.cn.emoji;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 表情页适配器（FragmentPagerAdapter的好处是fragment常驻内存，对于要求效率而页卡很少的表情控件最合适）
 * Created by LeeDane on 2016/7/5.
 */
public class EmojiPagerAdapter extends FragmentPagerAdapter {
    private OnEmojiClickListener listener;

    public EmojiPagerAdapter(FragmentManager fm, OnEmojiClickListener l) {
        super(fm);
        listener = l;
    }
    @Override
    public EmojiPageFragment getItem(int index) {
        return new EmojiPageFragment(index, index, listener);
    }

    /**
     * 显示模式：如果只有一种Emoji表情，则像QQ表情一样左右滑动分页显示<br>
     * 如果有多种Emoji表情，每页显示一种，Emoji筛选时上下滑动筛选。
     */
    @Override
    public int getCount() {
        return EmojiUtil.EMOJI_TAB_CONTENT;
    }
}
