package com.leedane.cn.bean;

import com.leedane.cn.bean.base.IdBean;

/**
 * 发现列表的Bean
 * Created by LeeDane on 2016/4/15.
 */
public class MenuBean extends IdBean{

    public MenuBean(){

    }

    public MenuBean(int iconId, String title) {
        this.iconId = iconId;
        this.title = title;
    }

    /**
     * 展示的标题
     */
    private String title;

    /**
     * 有图片的，展示图片资源的ID
     */
    private int iconId;

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
