package com.leedane.cn.financial.fragment;

import android.os.Bundle;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图表数据的fragment
 * Created by LeeDane on 2016/8/7.
 */
public class WeekChartDataFragment extends BaseChartDataFragment {
    public WeekChartDataFragment(){
    }

    public static final WeekChartDataFragment newInstance(Bundle bundle){
        WeekChartDataFragment fragment = new WeekChartDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_chart_week_list;
    }

    @Override
    protected int getPieId() {
        return R.id.week_pie_chart;
    }

    @Override
    protected int getBarId() {
        return R.id.week_bar_chart;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialWeekList";
    }

    @Override
    protected int additionTimeSubstringStart() {
        return 8;
    }

    @Override
    protected int additionTimeSubstringEnd() {
        return 10;
    }

    @Override
    protected ArrayList<String> getMultipleBarXValues(FinancialList financialList){
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        List<FinancialBean> financialBeanList = financialList.getFinancialBeans();
        if(CommonUtil.isEmpty(financialBeanList)){
            return xValues;
        }
        //根据最新和最旧的时间获取时间差
        List<Date> dates = DateUtil.findDates(DateUtil.stringToDate(financialBeanList.get(financialBeanList.size() - 1).getAdditionTime(), "yyyy-MM-dd"),
                DateUtil.stringToDate(financialBeanList.get(0).getAdditionTime(), "yyyy-MM-dd"));

        String date;
        for(Date d: dates){
            date = DateUtil.DateToString(d, "yyyyMMdd");
            xValues.add(date.substring(6, 8));
        }
        return xValues;
    }
    @Override
    protected ArrayList<String> getMultipleBarXLabels(FinancialList financialList){
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        List<FinancialBean> financialBeanList = financialList.getFinancialBeans();
        if(CommonUtil.isEmpty(financialBeanList)){
            return xValues;
        }
        //根据最新和最旧的时间获取时间差
        List<Date> dates = DateUtil.findDates(DateUtil.stringToDate(financialBeanList.get(financialBeanList.size() - 1).getAdditionTime(), "yyyy-MM-dd"),
                DateUtil.stringToDate(financialBeanList.get(0).getAdditionTime(), "yyyy-MM-dd"));

        String date;
        for(Date d: dates){
            date = DateUtil.DateToString(d, "yyyyMMdd");
                xValues.add(date.substring(4, 6) + "月"+ date.substring(6, 8) +"日");
        }
        return xValues;
    }
}
