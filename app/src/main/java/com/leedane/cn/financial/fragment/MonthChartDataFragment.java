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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.leedane.cn.app.R;
import com.leedane.cn.customview.MyMarkerView;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
public class MonthChartDataFragment extends BaseFragment {

    private FinancialList mFinancialList;
    private View mRootView;
    private Context mContext;
    private float totalIncome = 0;
    private float totalSpend = 0;
    public MonthChartDataFragment(){
    }

    public static final MonthChartDataFragment newInstance(Bundle bundle){
        MonthChartDataFragment fragment = new MonthChartDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_financial_chart_month_list, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            mFinancialList = (FinancialList) bundle.getSerializable("financialMonthList");
        }else{
            Log.i("MonthChartData", "bundle为空");
        }
        if(mContext == null)
            mContext = getActivity();
        Log.i("MonthChartData", "展示本月的");

        PieChart pieChart = (PieChart)mRootView.findViewById(R.id.month_pie_chart);
        PieHandler pieHandler = new PieHandler(mContext, getPieObject());
        pieHandler.showPie(pieChart);

        /*LineChart lineChart = (LineChart)mRootView.findViewById(R.id.month_line_chart);
        LineHandler lineHandler = new LineHandler(mContext, getLineObject());
        lineHandler.showLine(lineChart);*/

        BarChart barChart = (BarChart)mRootView.findViewById(R.id.month_bar_chart);
        /*MultipleBarHandler multipleBarHandler = new MultipleBarHandler(mContext, getMultipleBarObject());
        multipleBarHandler.showMultipleBar(barChart);*/
        //mChart.setOnChartValueSelectedListener(this);
        MultipleBarHandler handler = new MultipleBarHandler(mContext, getMultipleBarObject());
        handler.showMultipleBar(barChart);
    }

    private PieObject getPieObject(){
        PieObject pieObject = new PieObject();
        pieObject.setyValues(getPieYValues());
        //pieObject.setxValues(getPieXValues());
        pieObject.setCenterDesc(EnumUtil.getFinancialModelValue(mFinancialList.getModel()) + "本月收支统计,收入:" + totalIncome + ",支出:" + totalSpend);
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

        // 饼图颜色
        colors.add(R.color.bluePrimary);
        colors.add(R.color.red);
        return colors;
    }

    /**
     * 获取折线图的对象
     * @return
     */
    private LineObject getLineObject(){
        LineObject lineObject = new LineObject();
        lineObject.setxValues(getLineXValues());
        lineObject.setyValues(getLineYValues());
        lineObject.setColor(Color.rgb(104, 241, 175));
        lineObject.setHighLightColor(Color.rgb(244, 117, 117));
        lineObject.setLineDesc(DateUtil.DateToString(DateUtil.getThisMonthStart(), "yyyy-MM-dd") + "至" + DateUtil.DateToString(new Date(), "yyyy-MM-dd") + "收支统计");
        return lineObject;
    }

    private ArrayList<String> getLineXValues(){
        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
        Set<String> days = new HashSet<>();
        for(FinancialBean financialBean: mFinancialList.getFinancialBeans()){
            if(StringUtil.isNotNull(financialBean.getAdditionTime())){
                days.add(financialBean.getAdditionTime().substring(8, 10));
            }
        }
        if(days.size() > 0){
            for(String s: days){
                xValues.add(s +"日");
            }
        }
        return xValues;
    }

    private ArrayList<Entry> getLineYValues(){
        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<String> xValues = getLineXValues();
        Map<String, Float> moneys = new HashMap<>(); //年跟钱的关系
        for(int i = 0; i < xValues.size(); i++){
            for(FinancialBean financialBean: mFinancialList.getFinancialBeans()){
                if(StringUtil.isNotNull(financialBean.getAdditionTime()) && xValues.get(i).equals(financialBean.getAdditionTime().substring(8, 10) +"日")){
                    Float money = financialBean.getMoney();
                    if(moneys.containsKey(xValues.get(i))){//已经存在
                        money = moneys.get(xValues.get(i)) + money;
                    }
                    moneys.put(xValues.get(i), money);
                }
            }
        }
        for(int i = 0; i < xValues.size(); i++){
            yValues.add(new Entry(moneys.containsKey(xValues.get(i)) ? moneys.get(xValues.get(i)) : 0f, i));
        }
        return  yValues;
    }


    private MultipleBarObject getMultipleBarObject(){
        MultipleBarObject barObject = new MultipleBarObject();
        ArrayList<IBarDataSet> barDataSets = new ArrayList<>();
        BarDataSet incomeBarDataSet = null;//收入列表
        BarDataSet spendBarDataSet = null;//支出列表
        ArrayList<String> xValues = getMultipleBarXValues();
        Map<String, Float> incomeMoneys = new HashMap<>(); //年跟钱的关系
        Map<String, Float> spendMoneys = new HashMap<>(); //年跟钱的关系
        for(int i = 0; i < xValues.size(); i++){
            for(FinancialBean financialBean: mFinancialList.getFinancialBeans()){
                if(StringUtil.isNotNull(financialBean.getAdditionTime()) && xValues.get(i).equals(financialBean.getAdditionTime().substring(8, 10))){
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

            incomeBarEntrys.add(new BarEntry(Float.parseFloat(xValues.get(i)), incomeMoneys.get(xValues.get(i)) != null ? incomeMoneys.get(xValues.get(i)): 3700.0f));
            spendBarEntrys.add(new BarEntry(Float.parseFloat(xValues.get(i)), spendMoneys.get(xValues.get(i)) != null ? spendMoneys.get(xValues.get(i)) :3700.0f));
            /*if(i == 0){
                incomeBarEntrys.add(new BarEntry(Float.parseFloat(xValues.get(i)), 6));
                spendBarEntrys.add(new BarEntry(Float.parseFloat(xValues.get(i)), 9));
            }else if(i == 1){
                incomeBarEntrys.add(new BarEntry(Float.parseFloat(xValues.get(i)), 4));
                spendBarEntrys.add(new BarEntry(Float.parseFloat(xValues.get(i)), 7));
            }else{
                incomeBarEntrys.add(new BarEntry(Float.parseFloat(xValues.get(i)), 2));
                spendBarEntrys.add(new BarEntry(Float.parseFloat(xValues.get(i)), 8));
            }*/


        }
        incomeBarDataSet = new BarDataSet(incomeBarEntrys, "收入统计");
        incomeBarDataSet.setColor(Color.rgb(104, 241, 175));
        incomeBarDataSet.setValues(incomeBarEntrys);
        incomeBarDataSet.setDrawValues(true);
        spendBarDataSet = new BarDataSet(spendBarEntrys, "支出统计");
        spendBarDataSet.setColor(Color.rgb(164, 228, 251));
        spendBarDataSet.setValues(spendBarEntrys);
        spendBarDataSet.setDrawValues(true);
        //spendBarDataSet.setValues(spendBarEntrys);
        barDataSets.add(incomeBarDataSet);
        barDataSets.add(spendBarDataSet);
        barObject.setBarDataSets(barDataSets);
        barObject.setMinValue(0.0f);
        barObject.setMaxValue(max);
        barObject.setxValues(getMultipleBarXValues());
        return barObject;
    }
    private ArrayList<String> getMultipleBarXValues(){
        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
        Set<String> days = new HashSet<>();
        for(FinancialBean financialBean: mFinancialList.getFinancialBeans()){
            if(StringUtil.isNotNull(financialBean.getAdditionTime())){
                days.add(financialBean.getAdditionTime().substring(8, 10));
            }
        }
        if(days.size() > 0){
            for(String s: days){
                xValues.add(s);
            }
        }
       //xValues.clear();
        sortList(xValues);
        /*for(int i = 5; i < 15; i++){
            String v = i < 10 ? "0"+i: String.valueOf(i);
            xValues.add(v);
        }*/
        //sortList(xValues);
        return xValues;
    }

    /**
     * 从小到大排序
     * @param list
     */
    private void sortList(ArrayList<String> list){
        // 按排查时间倒序models
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                if (StringUtil.isNull(s1))
                    return 0;
                if (StringUtil.isNull(s2))
                    return 0;
                int st1 = Integer.parseInt(s1);
                long st2 = Integer.parseInt(s2);
                return st1 == st2 ? 0 :
                        (st1 > st1 ? 1 : -1);
            }
        });
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
