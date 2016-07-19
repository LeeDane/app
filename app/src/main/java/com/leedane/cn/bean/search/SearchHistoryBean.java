package com.leedane.cn.bean.search;

import com.leedane.cn.bean.base.IdBean;

/**
 * 搜索历史
 * Created by leedane on 2016/5/22.
 */
public class SearchHistoryBean extends IdBean{

    private String searchType;//搜索的类别(心情、博客、用户等)

    private String searchKey;  //搜索的内容

    private String createTime; //创建时间

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }
}
