package com.leedane.cn.financial.fragment;

import android.os.Bundle;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 图表数据的fragment
 * Created by LeeDane on 2016/8/7.
 */
public class YearChartDataFragment extends BaseChartDataFragment {

    public YearChartDataFragment(){
    }

    public static final YearChartDataFragment newInstance(Bundle bundle){
        YearChartDataFragment fragment = new YearChartDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContainerId() {
        return R.layout.fragment_financial_chart_year_list;
    }

    @Override
    protected int getPieId() {
        return R.id.year_pie_chart;
    }

    @Override
    protected int getBarId() {
        return R.id.year_bar_chart;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialYearList";
    }

    @Override
    protected int additionTimeSubstringStart() {
        return 5;
    }

    @Override
    protected int additionTimeSubstringEnd() {
        return 7;
    }

    @Override
    protected int getBarXType() {
        return 1;
    }

    @Override
    protected ArrayList<String> getMultipleBarXValues(FinancialList financialList){
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        Set<String> days = new HashSet<>();
        for(FinancialBean financialBean: financialList.getFinancialBeans()){
            if(StringUtil.isNotNull(financialBean.getAdditionTime())){
                days.add(financialBean.getAdditionTime().substring(5, 7));
            }
        }
        int max = 0;
        int min = 0;
        if(days.size() > 0){
            int i = 0;
            for(String s: days){
                if(i == 0){
                    max = Integer.parseInt(s);
                    min = Integer.parseInt(s);
                }else{
                    max = Math.max(max, Integer.parseInt(s));
                    min = Math.min(min, Integer.parseInt(s));
                }
                i ++;
            }
        }
        for(int i = min; i <= max; i++){
            xValues.add(i < 10 ? "0"+i: String.valueOf(i));
        }
        return xValues;
    }

    @Override
    protected ArrayList<String> getMultipleBarXLabels(FinancialList financialList){
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        Set<String> days = new HashSet<>();
        for(FinancialBean financialBean: financialList.getFinancialBeans()){
            if(StringUtil.isNotNull(financialBean.getAdditionTime())){
                days.add(financialBean.getAdditionTime().substring(5, 7));
            }
        }
        int max = 0;
        int min = 0;
        if(days.size() > 0){
            int i = 0;
            for(String s: days){
                if(i == 0){
                    max = Integer.parseInt(s);
                    min = Integer.parseInt(s);
                }else{
                    max = Math.max(max, Integer.parseInt(s));
                    min = Math.min(min, Integer.parseInt(s));
                }
                i ++;
            }
        }
        int size = days.size();
        for(int i = min; i <= max; i++){
            if(size > 9)
                xValues.add(i+"");
            else
                xValues.add(i + "月");
        }
        return xValues;
    }
}
