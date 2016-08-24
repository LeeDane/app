package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;

/**
 * 记账本周的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class WeekFragment extends BaseFragment {
    public WeekFragment(){
    }

    public static final WeekFragment newInstance(Bundle bundle){
        WeekFragment fragment = new WeekFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_week_list_or_chart;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.week_container;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialWeekList";
    }

    @Override
    protected Fragment getListDataFragment(Bundle bundle) {
        return WeekListDataFragment.newInstance(bundle);
    }

    @Override
    protected Fragment getChartDataFragment(Bundle bundle) {
        return WeekChartDataFragment.newInstance(bundle);
    }

    @Override
    public void calculate(FinancialList financialList, int model) {
        super.calculate(financialList, model);
        if(model == EnumUtil.FinancialModel.本周.value){
            super.financialList =  CalculateUtil.weekList;
        }
    }
}
