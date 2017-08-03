package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.util.ToastUtil;

/**
 * 记账本年的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class YearFragment extends BaseFragment {
    public YearFragment(){
    }

    public static final YearFragment newInstance(Bundle bundle){
        YearFragment fragment = new YearFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        super.financialList =  CalculateUtil.yearList;
        Bundle bundle = new Bundle();
        //bundle.putSerializable(getFinancialListKey(), financialList);
        bundle.putInt("model", EnumUtil.FinancialModel.本年.value);
        getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getListDataFragment(bundle)).commit();
    }

    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_year_list_or_chart;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.year_container;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialYearList";
    }

    @Override
    protected Fragment getListDataFragment(Bundle bundle) {
        return YearListDataFragment.newInstance(bundle);
    }

    @Override
    protected Fragment getChartDataFragment(Bundle bundle) {
        return YearChartDataFragment.newInstance(bundle);
    }

    @Override
    protected int getModel() {
        return EnumUtil.FinancialModel.本年.value;
    }

    @Override
    public void calculate(int model) {
        super.calculate(model);
        if(model == getModel()){
            super.financialList =  CalculateUtil.yearList;
            Bundle bundle = new Bundle();
            //bundle.putSerializable(getFinancialListKey(), financialList);
            bundle.putInt("model", model);
            getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getListDataFragment(bundle)).commit();
        }
    }
}
