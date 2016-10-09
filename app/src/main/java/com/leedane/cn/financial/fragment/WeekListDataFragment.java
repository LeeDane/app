package com.leedane.cn.financial.fragment;

import android.os.Bundle;

import com.leedane.cn.app.R;

/**
 * 列表数据的fragment
 * Created by LeeDane on 2016/8/22.
 */
public class WeekListDataFragment extends BaseListDataFragment {

    public WeekListDataFragment(){
    }

    public static final WeekListDataFragment newInstance(Bundle bundle){
        WeekListDataFragment fragment = new WeekListDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_week_list;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialWeekList";
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.financial_week_list;
    }
}
