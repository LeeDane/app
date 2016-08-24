package com.leedane.cn.financial.fragment;

import android.os.Bundle;

import com.leedane.cn.app.R;

/**
 * 列表数据的fragment
 * Created by LeeDane on 2016/8/22.
 */
public class MonthListDataFragment extends BaseListDataFragment {
    public MonthListDataFragment(){
    }

    public static final MonthListDataFragment newInstance(Bundle bundle){
        MonthListDataFragment fragment = new MonthListDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_month_list;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialMonthList";
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.financial_month_list;
    }
}
