package com.leedane.cn.financial.fragment;

import android.content.Context;
import android.graphics.Color;
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
import com.leedane.cn.app.R;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.charts.bar.MultipleBarObject;
import com.leedane.cn.financial.charts.pie.PieObject;
import com.leedane.cn.financial.handler.MultipleBarHandler;
import com.leedane.cn.financial.handler.PieHandler;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 搜索图表的fragment类
 * Created by LeeDane on 2016/12/7.
 */
public class SearchChartDataFragment extends FinancialBaseFragment {
    private View mRootView;
    private Context mContext;
    private float showTotalIncome;
    private float showTotalSpend;
    private BarChart barChart;

    private String startTime = null, endTime = null;
    private int additionTimeSubstringStart = 0, additionTimeSubstringEnd = 0;

    public SearchChartDataFragment(){
    }

    public static final SearchChartDataFragment newInstance(Bundle bundle){
        SearchChartDataFragment fragment = new SearchChartDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    /**
     * 所在容器的ID
     * @return
     */
    private int getContainerId(){
        return R.layout.fragment_financial_chart_search;
    }

    /**
     * 所在饼状图的ID
     * @return
     */
    private int getPieId(){
        return R.id.search_pie_chart;
    }

    /**
     * 二级分类所在饼状图的ID
     * @return
     */
    private int getCategoryPieId(){
        return R.id.search_category_pie_chart;
    }

    /**
     * 所在柱状图图的ID
     * @return
     */
    private int getBarId(){
        return R.id.search_bar_chart;
    }

    /**
     * 获取bunde中的对象
     * @return
     */
    private String getFinancialListKey(){
        return "financialSearchList";
    }

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
        }

        if(mFinancialList == null || CommonUtil.isEmpty(mFinancialList.getFinancialBeans()))
            return;
        //构建柱状图的类型
        buildBarType();
        if(mContext == null)
            mContext = getActivity();
        Log.i("BaseChartData", "展示BaseChartData的");

        PieChart pieChart = (PieChart)mRootView.findViewById(getPieId());
        PieHandler pieHandler = new PieHandler(mContext, getPieObject(startTime, endTime));
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

                //分类饼状图展示
                PieChart categoryPieChart = (PieChart)mRootView.findViewById(getCategoryPieId());
                PieHandler categoryPieHandler = new PieHandler(mContext, getCategoryPieObject(list.getFinancialBeans()));
                categoryPieHandler.showPie(categoryPieChart);
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

        //分类饼状图展示
        PieChart categoryPieChart = (PieChart)mRootView.findViewById(getCategoryPieId());
        PieHandler categoryPieHandler = new PieHandler(mContext, getCategoryPieObject(mFinancialList.getFinancialBeans()));
        categoryPieHandler.showPie(categoryPieChart);
    }

    /**
     * 获取列表的类型，0：天轴，1是月轴，2是年轴，3：小时轴,以及开始和结束时间
     */
    private void buildBarType() {
        List<FinancialBean> financialBeans = mFinancialList.getFinancialBeans();
        if(!CommonUtil.isEmpty(financialBeans)){
            Set<Integer> years = new HashSet<>();//年的集合
            Set<Integer> months = new HashSet<>();//月的集合
            Set<Integer> days = new HashSet<>();//小时的集合
            for(FinancialBean financialBean: financialBeans){
                years.add(StringUtil.changeObjectToInt(financialBean.getAdditionTime().substring(0, 4)));
                months.add(StringUtil.changeObjectToInt(financialBean.getAdditionTime().substring(5, 7)));
                days.add(StringUtil.changeObjectToInt(financialBean.getAdditionTime().substring(8, 10)));
            }
            if(years.size() > 1){
                barType = 2;
                endTime = DateUtil.DateToString(DateUtil.stringToDate(financialBeans.get(0).getAdditionTime()), "yyyy年");
                startTime = DateUtil.DateToString(DateUtil.stringToDate(financialBeans.get(financialBeans.size() -1).getAdditionTime()), "yyyy年");
                additionTimeSubstringStart = 0;
                additionTimeSubstringEnd = 4;
                return;
            }

            if(months.size() > 1){
                barType = 1;
                endTime = DateUtil.DateToString(DateUtil.stringToDate(financialBeans.get(0).getAdditionTime()), "yyyy年MM月");
                startTime = DateUtil.DateToString(DateUtil.stringToDate(financialBeans.get(financialBeans.size() - 1).getAdditionTime()), "yyyy年MM月");
                additionTimeSubstringStart = 5;
                additionTimeSubstringEnd = 7;
                return;
            }
            if(days.size() > 1){
                barType = 0;
                endTime = DateUtil.DateToString(DateUtil.stringToDate(financialBeans.get(0).getAdditionTime()), "MM月dd日");
                startTime = DateUtil.DateToString(DateUtil.stringToDate(financialBeans.get(financialBeans.size() - 1).getAdditionTime()), "MM月dd日");
                additionTimeSubstringStart = 8;
                additionTimeSubstringEnd = 10;
                return;
            }
        }
        barType = 3;
        endTime = DateUtil.DateToString(DateUtil.stringToDate(financialBeans.get(0).getAdditionTime()), "dd日HH时");
        startTime = DateUtil.DateToString(DateUtil.stringToDate(financialBeans.get(financialBeans.size() - 1).getAdditionTime()), "dd日HH时");
        additionTimeSubstringStart = 11;
        additionTimeSubstringEnd = 13;
        setBarType(barType);
        return;
    }

    private PieObject getPieObject(String startTime, String endTime){
        PieObject pieObject = new PieObject();
        pieObject.setyValues(getPieYValues());
        pieObject.setCenterDesc("从" + startTime + "至" + endTime + "共收入:" + showTotalIncome + ",共支出:" + showTotalSpend);
        //pieObject.setTitle("从" +startTime +"至" + endTime + "收支统计饼状图");
        pieObject.setTitle("收支统计饼状图");
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
                if(StringUtil.isNotNull(financialBean.getAdditionTime()) && xValues.get(i).equals(financialBean.getAdditionTime().substring(additionTimeSubstringStart, additionTimeSubstringEnd))){
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
    private ArrayList<String> getMultipleBarXValues(FinancialList financialList){
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        Set<String> days = new HashSet<>();
        for(FinancialBean financialBean: financialList.getFinancialBeans()){
            if(StringUtil.isNotNull(financialBean.getAdditionTime())){
                days.add(financialBean.getAdditionTime().substring(additionTimeSubstringStart, additionTimeSubstringEnd));
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

    /**
     * 构建x轴展示的文字
     * @param financialList
     * @return
     */
    private ArrayList<String> getMultipleBarXLabels(FinancialList financialList){
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        Set<String> days = new HashSet<>();
        for(FinancialBean financialBean: financialList.getFinancialBeans()){
            if(StringUtil.isNotNull(financialBean.getAdditionTime())){
                days.add(financialBean.getAdditionTime().substring(additionTimeSubstringStart, additionTimeSubstringEnd));
            }
        }

        String barTypeName = null;
        switch (barType){
            case 0:
                barTypeName = "日";
                break;
            case 1:
                barTypeName = "月";
                break;
            case 2:
                barTypeName ="年";
                break;
            case 3:
                barTypeName ="时";
                break;
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
        int size = max - min;
        for(int i = min; i <= max; i++){
            if(size > 9)
                xValues.add(i +"");
            else
                xValues.add(i + barTypeName);
        }
        return xValues;
    }

    /**
     * 获取分类饼状图对象
     * @return
     */
    private PieObject getCategoryPieObject(List<FinancialBean> financialBeans){
        PieObject pieObject = new PieObject();
        pieObject.setyValues(getCategoryPieYValues(financialBeans));
        pieObject.setCenterDesc("二级分类统计Top5");
        //pieObject.setTitle("从" +startTime +"至" + endTime + "收支统计饼状图");
        pieObject.setTitle("收支统计饼状图");
        pieObject.setColors(getCategoryPieColor());
        return pieObject;
    }

    /**
     * 获取分类饼状图数据
     * @return
     */
    private ArrayList<PieEntry> getCategoryPieYValues(List<FinancialBean> financialBeans){
        ArrayList<PieEntry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
        //分类名称跟总数的集合
        Map<String, BigDecimal> categorys = new HashMap<>();
        String categoryName;
        BigDecimal newBigDecimal;
        for(FinancialBean bean: financialBeans){
            categoryName = bean.getTwoLevel();
            newBigDecimal = BigDecimal.valueOf(bean.getMoney());
            if(StringUtil.isNotNull(categoryName)){
                if(categorys.containsKey(categoryName)){
                    newBigDecimal = newBigDecimal.add(categorys.get(categoryName));
                }
                categorys.put(categoryName, newBigDecimal);
            }else{
                newBigDecimal = BigDecimal.valueOf(bean.getMoney());
                if(categorys.containsKey("其他")){
                    newBigDecimal = newBigDecimal.add(categorys.get("其他"));
                }
                categorys.put("其他", newBigDecimal);
            }
        }

        if(categorys.size() > 0){
            //排序
            List<Map.Entry<String, BigDecimal>> list = new ArrayList<>(categorys.entrySet());
            sortMapDesc(list);
            int i = 0;
            for(Map.Entry<String, BigDecimal> entry: list){
                if(i < 5)
                    yValues.add(new PieEntry(entry.getValue().setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue(), entry.getKey()));
                else
                    break;//只获取最大金额前面10位的
                i++;
            }
        }
        return yValues;
    }

    /**
     * 排序map,从大到小
     * @param list
     */
    private void sortMapDesc(List<Map.Entry<String, BigDecimal>> list) {
        Collections.sort(list, new Comparator<Map.Entry<String, BigDecimal>>() {
            public int compare(Map.Entry<String, BigDecimal> o1, Map.Entry<String, BigDecimal> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
    }

    private ArrayList<Integer> getCategoryPieColor(){
        ArrayList<Integer> colors = new ArrayList<Integer>();
        // 饼图颜色(一定要使用rgb格式的颜色)
        colors.add(Color.rgb(129, 129, 247));
        colors.add(Color.rgb(255, 0, 0));
        colors.add(Color.rgb(216,191,216));
        colors.add(Color.rgb(255,0,255));
        colors.add(Color.rgb(48,0,211));
        colors.add(Color.rgb(138,43,226));
        colors.add(Color.rgb(72,61,139));
        colors.add(Color.rgb(0,0,139));
        colors.add(Color.rgb(100,149,237));
        colors.add(Color.rgb(112,128,144));
        colors.add(Color.rgb(30,144,255));
        colors.add(Color.rgb(135,206,250));
        colors.add(Color.rgb(95,158,160));
        colors.add(Color.rgb(0,255,255));
        colors.add(Color.rgb(0,139,139));
        colors.add(Color.rgb(127,255,212));
        colors.add(Color.rgb(127,255,0));
        colors.add(Color.rgb(154,205,50));
        return colors;
    }
}
