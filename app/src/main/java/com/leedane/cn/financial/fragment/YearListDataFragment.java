package com.leedane.cn.financial.fragment;

import android.os.Bundle;

import com.leedane.cn.app.R;

/**
 * 列表数据的fragment
 * Created by LeeDane on 2016/8/22.
 */
public class YearListDataFragment extends BaseListDataFragment {
    public YearListDataFragment(){
    }

    public static final YearListDataFragment newInstance(Bundle bundle){
        YearListDataFragment fragment = new YearListDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_year_list;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialYearList";
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.financial_year_list;
    }
}
