package com.leedane.cn.uploadfile;

import java.io.Serializable;
import java.util.List;

/**
 * 上传文件的实体bean
 * Created by LeeDane on 2016/1/29.
 */
public class UploadBean implements Serializable{

    private List<UploadItem> itemList;

    public List<UploadItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<UploadItem> itemList) {
        this.itemList = itemList;
    }
}
