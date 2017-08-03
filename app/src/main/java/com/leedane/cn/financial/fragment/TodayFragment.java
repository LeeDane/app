package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.util.ToastUtil;

/**
 * 记账今日的fragment
 * Created by LeeDane on 2017/8/1.
 */
public class TodayFragment extends BaseFragment {
    public TodayFragment(){
    }
    public static final TodayFragment newInstance(Bundle bundle){
        TodayFragment fragment = new TodayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        super.financialList =  CalculateUtil.todayList;
        Bundle bundle = new Bundle();
        //bundle.putSerializable(getFinancialListKey(), financialList);
        bundle.putInt("model", EnumUtil.FinancialModel.今日.value);
        getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getListDataFragment(bundle)).commit();
    }

    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_today_list_or_chart;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.today_container;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialTodayList";
    }

    @Override
    protected Fragment getListDataFragment(Bundle bundle) {
        return TodayListDataFragment.newInstance(bundle);
    }

    @Override
    protected Fragment getChartDataFragment(Bundle bundle) {
        return TodayChartDataFragment.newInstance(bundle);
    }

    @Override
    protected int getModel() {
        return EnumUtil.FinancialModel.今日.value;
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
