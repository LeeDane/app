package com.leedane.cn.financial.fragment;

import android.os.Bundle;

import com.leedane.cn.app.R;

/**
 * 列表数据的fragment
 * Created by LeeDane on 2016/8/22.
 */
public class YesterDayListDataFragment extends BaseListDataFragment {
    public YesterDayListDataFragment() {
    }

    public static final YesterDayListDataFragment newInstance(Bundle bundle) {
        YesterDayListDataFragment fragment = new YesterDayListDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_yesterday_list;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialYesterDayList";
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.financial_yesterday_list;
    }
}