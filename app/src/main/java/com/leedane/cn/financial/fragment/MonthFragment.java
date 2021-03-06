package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.util.ToastUtil;

/**
 * 记账本月的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class MonthFragment extends BaseFragment {
    public MonthFragment(){
    }

    public static final MonthFragment newInstance(Bundle bundle) {
        MonthFragment fragment = new MonthFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        super.financialList =  CalculateUtil.monthList;
        Bundle bundle = new Bundle();
        //bundle.putSerializable(getFinancialListKey(), financialList);
        bundle.putInt("model", EnumUtil.FinancialModel.本月.value);
        getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getListDataFragment(bundle)).commit();
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
    protected int getModel() {
        return EnumUtil.FinancialModel.本月.value;
    }

    @Override
    public void calculate(int model) {
        super.calculate(model);
        if(model == getModel()){
            super.financialList =  CalculateUtil.monthList;
            Bundle bundle = new Bundle();
            //bundle.putSerializable(getFinancialListKey(), financialList);
            bundle.putInt("model", model);
            getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getListDataFragment(bundle)).commit();
        }
    }
}
