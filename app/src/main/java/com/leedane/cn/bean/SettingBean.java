package com.leedane.cn.bean;

import java.io.Serializable;

/**
 * 设置选项饿实体bean
 * Created by LeeDane on 2015/11/6.
 */
public class SettingBean implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 每一项的唯一标记
     */
    private String uuid;

    /**
     * 提示的标题
     */
    private String title;

    /**
     * EditText的显示内容
     */
    private String content;

    /**
     * EditText为空显示的提示信息
     */
    private String hint;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
