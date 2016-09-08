package com.leedane.cn.financial.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.charts.bar.MultipleBarObject;
import com.leedane.cn.financial.charts.pie.PieObject;
import com.leedane.cn.financial.handler.MultipleBarHandler;
import com.leedane.cn.financial.handler.PieHandler;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.util.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本图表数据的fragment
 * Created by LeeDane on 2016/9/7.
 */
public abstract class BaseChartDataFragment extends FinancialBaseFragment {

    protected FinancialList mFinancialList;
    protected View mRootView;
    protected Context mContext;
    protected float showTotalIncome;
    protected float showTotalSpend;
    protected BarChart barChart;

    /**
     * 所在容器的ID
     * @return
     */
    protected abstract int getContainerId();

    /**
     * 所在饼状图的ID
     * @return
     */
    protected abstract int getPieId();

    /**
     * 所在柱状图图的ID
     * @return
     */
    protected abstract int getBarId();

    /**
     * 获取bunde中的对象
     * @return
     */
    protected abstract String getFinancialListKey();

    /**
     * 对AdditionTime进行截取的开始位置
     * @return
     */
    protected abstract int additionTimeSubstringStart();

    /**
     * 对AdditionTime进行截取的结束位置
     * @return
     */
    protected abstract int additionTimeSubstringEnd();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(getContainerId(), container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            mFinancialList = (FinancialList) bundle.getSerializable(getFinancialListKey());
        }else{
            Log.i("BaseChartData", "bundle为空");

        }
        if(mContext == null)
            mContext = getActivity();
        Log.i("BaseChartData", "展示BaseChartData的");

        PieChart pieChart = (PieChart)mRootView.findViewById(getPieId());
        PieHandler pieHandler = new PieHandler(mContext, getPieObject());
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null || barChart == null)
                    return;
                FinancialList list = new FinancialList();
                List<FinancialBean> financialBeans = new ArrayList<>();
                list.setFinancialBeans(financialBeans);
                if(h.getX() == 0.0){//点击的是收入
                    for(FinancialBean financialBean: mFinancialList.getFinancialBeans()){
                        if(financialBean.getModel() == IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME){
                            financialBeans.add(financialBean);
                        }
                    }
                }else if(h.getX() == 1.0){//点击的是支出
                    for(FinancialBean financialBean: mFinancialList.getFinancialBeans()){
                        if(financialBean.getModel() == IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND){
                            financialBeans.add(financialBean);
                        }
                    }
                }
                MultipleBarHandler handler = new MultipleBarHandler(mContext, getMultipleBarObject(list));
                handler.setmTfLight(mTfLight);
                handler.showMultipleBar(barChart);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieHandler.showPie(pieChart);

        barChart = (BarChart)mRootView.findViewById(getBarId());
        barChart.setOnChartValueSelectedListener(this);
        MultipleBarHandler handler = new MultipleBarHandler(mContext, getMultipleBarObject(mFinancialList));
        handler.setmTfLight(mTfLight);
        handler.showMultipleBar(barChart);
    }

    private PieObject getPieObject(){
        PieObject pieObject = new PieObject();
        pieObject.setyValues(getPieYValues());
        pieObject.setCenterDesc(EnumUtil.getFinancialModelValue(mFinancialList.getModel()) + "收支统计,收入:" + showTotalIncome + ",支出:" + showTotalSpend);
        pieObject.setTitle(EnumUtil.getFinancialModelValue(mFinancialList.getModel()) + "收支统计饼状图");
        pieObject.setColors(getPieColor());
        return pieObject;
    }

    /**
     * 获取饼状图的x轴
     * @return
     */
    private ArrayList<String> getPieXValues(){
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        xValues.add("收入");
        xValues.add("支出");
        return xValues;
    }

    private ArrayList<PieEntry> getPieYValues(){
        ArrayList<PieEntry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
        List<FinancialBean> financialBeans = mFinancialList.getFinancialBeans();
        BigDecimal totalIncome = new BigDecimal(0.0f);
        BigDecimal totalSpend = new BigDecimal(0.0f);
        for(FinancialBean bean: financialBeans){
            if(bean.getModel() == IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME){//收入
                totalIncome = totalIncome.add(BigDecimal.valueOf(bean.getMoney()));
            }else{//支出
                float f = bean.getMoney();
                totalSpend = totalSpend.add(BigDecimal.valueOf(f));
            }
        }

        showTotalIncome = totalIncome.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
        showTotalSpend = totalSpend.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
        yValues.add(new PieEntry(showTotalIncome, getPieXValues().get(0)));
        yValues.add(new PieEntry(showTotalSpend, getPieXValues().get(1)));
        return yValues;
    }

    private ArrayList<Integer> getPieColor(){
        ArrayList<Integer> colors = new ArrayList<Integer>();

        // 饼图颜色(一定要使用rgb格式的颜色)
        colors.add(IncomeOrSpendActivity.FINANCIAL_INCOME_COLOR);
        colors.add(IncomeOrSpendActivity.FINANCIAL_SPEND_COLOR);
        return colors;
    }

    private MultipleBarObject getMultipleBarObject(FinancialList financialList){
        MultipleBarObject barObject = new MultipleBarObject();
        ArrayList<IBarDataSet> barDataSets = new ArrayList<>();
        BarDataSet incomeBarDataSet, spendBarDataSet;//收入列表 //支出列表
        ArrayList<String> xValues = getMultipleBarXValues(financialList);
        Map<String, Float> incomeMoneys = new HashMap<>(); //年跟钱的关系
        Map<String, Float> spendMoneys = new HashMap<>(); //年跟钱的关系
        for(int i = 0; i < xValues.size(); i++){
            for(FinancialBean financialBean: financialList.getFinancialBeans()){
                if(StringUtil.isNotNull(financialBean.getAdditionTime()) && xValues.get(i).equals(financialBean.getAdditionTime().substring(additionTimeSubstringStart(), additionTimeSubstringEnd()))){
                    BigDecimal money = new BigDecimal(financialBean.getMoney());
                    if(financialBean.getModel() == IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME){//收入
                        if(incomeMoneys.containsKey(xValues.get(i))){//已经存在
                            money = money.add(new BigDecimal(incomeMoneys.get(xValues.get(i))));
                        }
                        incomeMoneys.put(xValues.get(i), money.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                    }else{//支出
                        if(spendMoneys.containsKey(xValues.get(i))){//已经存在
                            money = money.add(new BigDecimal(spendMoneys.get(xValues.get(i))));
                        }
                        spendMoneys.put(xValues.get(i), money.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue());
                    }
                }
            }
        }
        ArrayList<BarEntry> incomeBarEntrys = new ArrayList<>();
        ArrayList<BarEntry> spendBarEntrys = new ArrayList<>();
        float max = 0.0f;
        for(int i = 0; i < xValues.size(); i++){
            //获取最大数
            if(incomeMoneys.get(xValues.get(i)) != null)
                max = Math.max(max, incomeMoneys.get(xValues.get(i)));
            if(spendMoneys.get(xValues.get(i)) != null)
                max = Math.max(max, spendMoneys.get(xValues.get(i)));

            incomeBarEntrys.add(new BarEntry(Float.parseFloat(xValues.get(i)), incomeMoneys.get(xValues.get(i)) != null ? incomeMoneys.get(xValues.get(i)): 0.0f));
            spendBarEntrys.add(new BarEntry(Float.parseFloat(xValues.get(i)), spendMoneys.get(xValues.get(i)) != null ? spendMoneys.get(xValues.get(i)) :0.0f));
        }
        incomeBarDataSet = new BarDataSet(incomeBarEntrys, "收入统计");
        incomeBarDataSet.setColor(IncomeOrSpendActivity.FINANCIAL_INCOME_COLOR);
        incomeBarDataSet.setValues(incomeBarEntrys);
        incomeBarDataSet.setDrawValues(true);
        spendBarDataSet = new BarDataSet(spendBarEntrys, "支出统计");
        spendBarDataSet.setColor(IncomeOrSpendActivity.FINANCIAL_SPEND_COLOR);
        spendBarDataSet.setValues(spendBarEntrys);
        spendBarDataSet.setDrawValues(true);
        //spendBarDataSet.setValues(spendBarEntrys);
        barDataSets.add(incomeBarDataSet);
        barDataSets.add(spendBarDataSet);
        barObject.setBarDataSets(barDataSets);
        barObject.setMinValue(0.0f);
        barObject.setMaxValue(max);
        barObject.setxValues(getMultipleBarXValues(financialList));
        barObject.setxLabels(getMultipleBarXLabels(financialList));
        return barObject;
    }

    /**
     * 构建x轴的数字
     * @param financialList
     * @return
     */
    protected abstract ArrayList<String> getMultipleBarXValues(FinancialList financialList);

    /**
     * 构建x轴展示的文字
     * @param financialList
     * @return
     */
    protected abstract ArrayList<String> getMultipleBarXLabels(FinancialList financialList);
}
