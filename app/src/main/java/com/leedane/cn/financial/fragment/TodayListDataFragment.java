package com.leedane.cn.financial.fragment;

import android.os.Bundle;

import com.leedane.cn.app.R;

/**
 * 今日列表数据的fragment
 * Created by LeeDane on 2017/8/1.
 */
public class TodayListDataFragment extends BaseListDataFragment {
    public TodayListDataFragment() {
    }

    public static final TodayListDataFragment newInstance(Bundle bundle) {
        TodayListDataFragment fragment = new TodayListDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_today_list;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialTodayList";
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.financial_today_list;
    }
}