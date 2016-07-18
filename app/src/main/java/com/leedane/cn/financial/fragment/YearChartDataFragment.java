package com.leedane.cn.financial.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.leedane.cn.app.R;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.charts.line.LineObject;
import com.leedane.cn.financial.charts.pie.PieObject;
import com.leedane.cn.financial.handler.LineHandler;
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
public class YearChartDataFragment extends BaseFragment {

    private FinancialList mFinancialList;
    private View mRootView;
    private Context mContext;
    private float totalIncome = 0;
    private float totalSpend = 0;
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
            Log.i("YearChatData", "bundle为空");
        }
        if(mContext == null)
            mContext = getActivity();

        Log.i("YearChatData", "展示今年的");
        PieChart pieChart = (PieChart)mRootView.findViewById(R.id.year_pie_chart);
        PieHandler pieHandler = new PieHandler(mContext, getPieObject());
        pieHandler.showPie(pieChart);

        LineChart lineChart = (LineChart)mRootView.findViewById(R.id.year_line_chart);
        LineHandler lineHandler = new LineHandler(mContext, getLineObject());
        lineHandler.showLine(lineChart);
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
        lineObject.setLineDesc(DateUtil.DateToString(DateUtil.getThisYearStart(), "yyyy-MM-dd") + "至" + DateUtil.DateToString(new Date(), "yyyy-MM-dd") +"收支统计");
        return lineObject;
    }

    private ArrayList<String> getLineXValues(){
        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
        Set<String> months = new HashSet<>();
        for(FinancialBean financialBean: mFinancialList.getFinancialBeans()){
            if(StringUtil.isNotNull(financialBean.getAdditionTime())){
                months.add(financialBean.getAdditionTime().substring(5, 7));
            }
        }
        if(months.size() > 0){
            for(String s: months){
                xValues.add(s +"月");
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
                if(StringUtil.isNotNull(financialBean.getAdditionTime()) && xValues.get(i).equals(financialBean.getAdditionTime().substring(5, 7) +"月")){
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
