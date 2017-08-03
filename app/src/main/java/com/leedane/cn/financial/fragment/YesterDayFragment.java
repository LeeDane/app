package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.util.ToastUtil;

/**
 * 记账昨日的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class YesterDayFragment extends BaseFragment {
    public YesterDayFragment(){
    }
    public static final YesterDayFragment newInstance(Bundle bundle){
        YesterDayFragment fragment = new YesterDayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        super.financialList =  CalculateUtil.yesterDayList;
        Bundle bundle = new Bundle();
        //bundle.putSerializable(getFinancialListKey(), financialList);
        bundle.putInt("model", EnumUtil.FinancialModel.昨日.value);
        getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getListDataFragment(bundle)).commit();
    }

    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_yesterday_list_or_chart;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.yesterday_container;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialYesterDayList";
    }

    @Override
    protected Fragment getListDataFragment(Bundle bundle) {
        return YesterDayListDataFragment.newInstance(bundle);
    }

    @Override
    protected Fragment getChartDataFragment(Bundle bundle) {
        return YesterdayChartDataFragment.newInstance(bundle);
    }

    @Override
    protected int getModel() {
        return EnumUtil.FinancialModel.昨日.value;
    }


    @Override
    public void calculate(int model) {
        super.calculate(model);
        if(model == getModel()){
            super.financialList =  CalculateUtil.yesterDayList;
            Bundle bundle = new Bundle();
            //bundle.putSerializable(getFinancialListKey(), financialList);
            bundle.putInt("model", model);
            getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getListDataFragment(bundle)).commit();
        }
    }
}
