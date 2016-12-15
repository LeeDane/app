package com.leedane.cn.financial.fragment;

import android.os.Bundle;

import com.leedane.cn.app.R;

/**
 * 搜索列表的fragment类
 * Created by LeeDane on 2016/12/4.
 */
public class SearchListFragment extends BaseListDataFragment{

    public static final String TAG = "SearchFragment";
    public SearchListFragment(){
    }

    public static final SearchListFragment newInstance(Bundle bundle){
        SearchListFragment fragment = new SearchListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getContainerId() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialSearchList";
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.id_recyclerview;
    }
}
