package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;

/**
 * 记账本月的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class MonthFragment extends BaseFragment {
    public MonthFragment(){
    }

    public static final MonthFragment newInstance(Bundle bundle){
        MonthFragment fragment = new MonthFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_month_list_or_chart;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.month_container;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialMonthList";
    }

    @Override
    protected Fragment getListDataFragment(Bundle bundle) {
        return MonthListDataFragment.newInstance(bundle);
    }

    @Override
    protected Fragment getChartDataFragment(Bundle bundle) {
        return MonthChartDataFragment.newInstance(bundle);
    }

    @Override
    public void calculate(FinancialList financialList, int model) {
        super.calculate(financialList, model);
        if(model == EnumUtil.FinancialModel.本月.value){
            super.financialList =  CalculateUtil.monthList;
        }
    }
}
