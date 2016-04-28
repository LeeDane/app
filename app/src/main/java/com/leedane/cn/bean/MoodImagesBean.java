package com.leedane.cn.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 心情图片列表的实体bean
 * @author LeeDane
 * @version 1.0
 *
 */

public class MoodImagesBean implements Serializable{

    private static final long serialVersionUID = 1L;
    /**
     * 路径
     */
    @SerializedName("qiniu_path")
    private String path;

    /**
     * 必须，图像的规格,约定的值为：source, default, 30x30, 60x60...
     */
    @SerializedName("pic_size")
    private String size;

    /**
     * 必须，图像的位置.
     */
    @SerializedName("pic_order")
    private int order;

    /**
     * 图片的宽度
     */
    private int width;

    /**
     * 图片的高度
     */
    private int height;

    /**
     * 文件的长度
     */
    private long lenght;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getLenght() {
        return lenght;
    }

    public void setLenght(long lenght) {
        this.lenght = lenght;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}