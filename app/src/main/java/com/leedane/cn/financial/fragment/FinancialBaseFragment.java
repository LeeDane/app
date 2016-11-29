package com.leedane.cn.financial.fragment;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.leedane.cn.broadcast.CalculateFinancialReceiver;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.fragment.BaseFragment;
import com.leedane.cn.util.ToastUtil;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        calculateFinancialReceiver.setCalculateFinancialListener(FinancialBaseFragment.this);
        //注册广播
        IntentFilter counterActionFilter = new IntentFilter("com.leedane.cn.broadcast.CalculateFinancialReceiver");
        mContext.registerReceiver(calculateFinancialReceiver, counterActionFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(calculateFinancialReceiver);
    }

    @Override
    public void calculate(FinancialList financialList, int model) {

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
        ToastUtil.success(mContext, "Selected: " + e.toString() + ", dataSet: " + h.getDataSetIndex());
        /*if(barChart.getData() != null) {
            barChart.getData().setHighlightEnabled(!barChart.getData().isHighlightEnabled());
            barChart.invalidate();
        }*/
    }

    @Override
    public void onNothingSelected() {
        ToastUtil.success(mContext, "Nothing selected.");
    }
}
