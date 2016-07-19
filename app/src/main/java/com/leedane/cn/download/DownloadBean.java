package com.leedane.cn.download;

import java.util.List;

/**
 * 下载任务的实体
 * Created by LeeDane on 2016/1/24.
 */
public class DownloadBean {
    private List<DownloadItem> itemList;

    public List<DownloadItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<DownloadItem> itemList) {
        this.itemList = itemList;
    }
}
