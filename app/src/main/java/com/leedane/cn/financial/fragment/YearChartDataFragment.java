package com.leedane.cn.financial.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.leedane.cn.app.R;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.charts.bar.MultipleBarObject;
import com.leedane.cn.financial.charts.line.LineObject;
import com.leedane.cn.financial.charts.pie.PieObject;
import com.leedane.cn.financial.handler.LineHandler;
import com.leedane.cn.financial.handler.MultipleBarHandler;
import com.leedane.cn.financial.handler.PieHandler;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.fragment.BaseFragment;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 图表数据的fragment
 * Created by LeeDane on 2016/8/7.
 */
public class YearChartDataFragment extends FinancialBaseFragment {

    private FinancialList mFinancialList;
    private View mRootView;
    private Context mContext;
    private float totalIncome = 0;
    private float totalSpend = 0;
    private BarChart barChart;
    public YearChartDataFragment(){
    }

    public static final YearChartDataFragment newInstance(Bundle bundle){
        YearChartDataFragment fragment = new YearChartDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_financial_chart_year_list, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            mFinancialList = (FinancialList) bundle.getSerializable("financialYearList");
        }else{
            Log.i("YeardayChartData", "bundle为空");

        }
        if(mContext == null)
            mContext = getActivity();
        Log.i("YeardayChartData", "展示本年的");

        PieChart pieChart = (PieChart)mRootView.findViewById(R.id.year_pie_chart);
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

       /* LineChart lineChart = (LineChart)mRootView.findViewById(R.id.year_line_chart);
        LineHandler lineHandler = new LineHandler(mContext, getLineObject());
        lineHandler.showLine(lineChart);*/

        barChart = (BarChart)mRootView.findViewById(R.id.year_bar_chart);
        /*MultipleBarHandler multipleBarHandler = new MultipleBarHandler(mContext, getMultipleBarObject());
        multipleBarHandler.showMultipleBar(barChart);*/
        barChart.setOnChartValueSelectedListener(this);
        MultipleBarHandler handler = new MultipleBarHandler(mContext, getMultipleBarObject(mFinancialList));
        handler.setmTfLight(mTfLight);
        handler.showMultipleBar(barChart);
    }

    private PieObject getPieObject(){
        PieObject pieObject = new PieObject();
        pieObject.setyValues(getPieYValues());
        //pieObject.setxValues(getPieXValues());
        pieObject.setCenterDesc(EnumUtil.getFinancialModelValue(mFinancialList.getModel()) + "收支统计,收入:" + totalIncome + ",支出:" + totalSpend);
        pieObject.setTitle(EnumUtil.getFinancialModelValue(mFinancialList.getModel()) + "收支统计饼状图");
        pieObject.setColors(getPieColor());
        return pieObject;
    }

    /**
     * 获取饼状图的x轴
     * @return
     */
    private ArrayList<String> getPieXValues(){
        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
        xValues.add("收入");
        xValues.add("支出");
        return xValues;
    }

    private ArrayList<PieEntry> getPieYValues(){
        ArrayList<PieEntry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
        //float quarterlyIncome = 0;
        //float quarterlySpend = 0;


        List<FinancialBean> financialBeans = mFinancialList.getFinancialBeans();
        for(FinancialBean bean: financialBeans){
            if(bean.getModel() == IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME){//收入
                totalIncome += bean.getMoney();
            }else{//支出
                totalSpend += bean.getMoney();
            }
        }

        //计算收入所占的百分比
        //quarterlyIncome = totalIncome /((totalIncome + totalSpend) * 100);
        //直接通过相减获取支出的百分比
        //quarterlySpend = 100 - quarterlyIncome;
        //ToastUtil.success(mContext, "总收入："+totalIncome+", 总支出:"+totalSpend);
        yValues.add(new PieEntry(totalIncome, getPieXValues().get(0)));
        yValues.add(new PieEntry(totalSpend, getPieXValues().get(1)));
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
        BarDataSet incomeBarDataSet = null;//收入列表
        BarDataSet spendBarDataSet = null;//支出列表
        ArrayList<String> xValues = getMultipleBarXValues(financialList);
        Map<String, Float> incomeMoneys = new HashMap<>(); //年跟钱的关系
        Map<String, Float> spendMoneys = new HashMap<>(); //年跟钱的关系
        for(int i = 0; i < xValues.size(); i++){
            for(FinancialBean financialBean: financialList.getFinancialBeans()){
                if(StringUtil.isNotNull(financialBean.getAdditionTime()) && xValues.get(i).equals(financialBean.getAdditionTime().substring(5, 7))){
                    Float money = financialBean.getMoney();
                    if(financialBean.getModel() == IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME){//收入
                        if(incomeMoneys.containsKey(xValues.get(i))){//已经存在
                            money = incomeMoneys.get(xValues.get(i)) + money;
                        }
                        incomeMoneys.put(xValues.get(i), money);
                    }else{//支出
                        if(spendMoneys.containsKey(xValues.get(i))){//已经存在
                            money = spendMoneys.get(xValues.get(i)) + money;
                        }
                        spendMoneys.put(xValues.get(i), money);
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

    private ArrayList<String> getMultipleBarXValues(FinancialList financialList){
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

    private ArrayList<String> getMultipleBarXLabels(FinancialList financialList){
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

    @Override
    protected void sendFirstLoading() {

    }

    @Override
    protected void sendUpLoading() {

    }

    @Override
    protected void sendLowLoading() {

    }

    @Override
    protected void sendLoadAgain(View view) {

    }
}
