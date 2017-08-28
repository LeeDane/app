package com.leedane.cn.financial.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.leedane.cn.broadcast.CalculateFinancialReceiver;
import com.leedane.cn.financial.activity.FinancialListActivity;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.fragment.BaseFragment;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.ToastUtil;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * 处理记账的基本Fragment
 * Created by LeeDane on 2016/8/17.
 */
public class FinancialBaseFragment extends BaseFragment implements CalculateFinancialReceiver.CalculateFinancialListener
    , OnChartValueSelectedListener {
    protected CalculateFinancialReceiver calculateFinancialReceiver = new CalculateFinancialReceiver();

    protected Context mContext;

    protected FinancialDataBase financialDataBase;

    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    /**
     * 该页面所需要的基础数据
     */
    protected FinancialList mFinancialList;

    //柱状图的维度：0：日 ， 1： 月， 2： 年， 3：时
    protected int barType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private boolean mReceiverTag = false;   //广播接受者标识
    //代码中动态注册广播
    private void registerReceiver() {
        if (!mReceiverTag){     //在注册广播接受者的时候 判断是否已被注册,避免重复多次注册广播
            mReceiverTag = true;    //标识值 赋值为 true 表示广播已被注册
            //注册广播
            IntentFilter counterActionFilter = new IntentFilter("com.leedane.cn.broadcast.CalculateFinancialReceiver");
            mContext.registerReceiver(calculateFinancialReceiver, counterActionFilter);
        }
    }
    //注销广播
    private void unregisterReceiver() {
        if (mReceiverTag) {   //判断广播是否注册
            mReceiverTag = false;   //Tag值 赋值为false 表示该广播已被注销
            mContext.unregisterReceiver(calculateFinancialReceiver);   //注销广播
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        calculateFinancialReceiver.setCalculateFinancialListener(FinancialBaseFragment.this);
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void calculate(int model) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mContext == null)
            mContext = getActivity();
        mTfRegular = Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Light.ttf");

        financialDataBase = new FinancialDataBase(mContext);
    }

    @Override
    public void onDestroy() {
        financialDataBase.destroy();
        unregisterReceiver();
        super.onDestroy();
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


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        //ToastUtil.success(mContext, "Selected: " + e.toString() + ", dataSet: " + h.getDataSetIndex());
        /*if(barChart.getData() != null) {
            barChart.getData().setHighlightEnabled(!barChart.getData().isHighlightEnabled());
            barChart.invalidate();
        }*/
        Object data = e.getData();
        float x = e.getX();
        float y = e.getY();
        int dataIndex = h.getDataSetIndex();

        BigDecimal b = new BigDecimal(x);
        int f1 = (int)b.setScale(0,   BigDecimal.ROUND_FLOOR).floatValue();
        //   b.setScale(2,   BigDecimal.ROUND_HALF_UP)   表明四舍五入，保留两位小数
        //总数有值的才去查看列表
        if(y > 0.0f){
            FinancialBean financialBean = mFinancialList.getFinancialBeans().get(0);
            String additionTime = financialBean.getAdditionTime();//2017-08-17 00:00:00
            String start, startTime = null, endTime = null;
            switch (barType) {
                case 0: //日
                    start = additionTime.substring(0, 7);
                    startTime = start + "-" + (f1 > 9 ? f1: "0"+ f1) +" 00:00:00";
                    endTime = start + "-" + (f1 > 9 ? f1: "0"+ f1) +" 23:59:59";
                    break;
                case 1: //月
                    start = additionTime.substring(0, 4);
                    startTime = start + "-" + (f1 > 9 ? f1: "0"+ f1) +"-01" +" 00:00:00";
                    // 获取Calendar
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DateUtil.stringToDate(startTime));
                    // 获取该月最大日期
                    endTime = start + "-" + (f1 > 9 ? f1: "0"+ f1) +"-"+ calendar.getActualMaximum(Calendar.DATE) +" 23:59:59";
                    break;
                case 2: //年
                    start = additionTime.substring(0, 4);
                    startTime = start + "-01-01" +" 00:00:00";
                    endTime = start + "-12-31" +" 23:59:59";
                    break;
                case 3: //时
                    start = additionTime.substring(0, 10);
                    startTime = start + " "+ (f1 > 9 ? f1: "0"+ f1) +":00:00";
                    endTime = start + " "+ (f1 > 9 ? f1: "0"+ f1) +":59:59";
                    break;
            }

            //ToastUtil.success(getContext(), "startTime="+ startTime +", endTime="+ endTime);
            Intent intent = new Intent(getContext(), FinancialListActivity.class);
            intent.putExtra("startTime", startTime);
            intent.putExtra("endTime", endTime);
            intent.putExtra("model", (dataIndex == 0 ? IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME: IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND));
            startActivity(intent);
        }

    }

    @Override
    public void onNothingSelected() {
        ToastUtil.success(mContext, "Nothing selected.");
    }


    /**
     * 设置柱状图的x轴类型
     * @param barType
     */
    public void setBarType(int barType) {
        this.barType = barType;
    }
}
