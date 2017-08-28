package com.leedane.cn.financial.handler;

import android.content.Context;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.leedane.cn.app.R;
import com.leedane.cn.customview.MyMarkerView;
import com.leedane.cn.financial.charts.bar.MultipleBarObject;

import java.util.List;

/**
 * 多维度柱状图图形处理工具类
 * Created by LeeDane on 2016/8/15.
 */
public class MultipleBarHandler {
    private MultipleBarObject multipleBarObject;
    private Context mContext;
    private Typeface mTfLight;
    public void setmTfLight(Typeface mTfLight) {
        this.mTfLight = mTfLight;
    }

    public MultipleBarHandler(Context context, MultipleBarObject multipleBarObject){
        this.multipleBarObject = multipleBarObject;
        this.mContext = context;
    }

    /**
     * 展示折线图
     * @param barChart
     */
    public void showMultipleBar(BarChart barChart){
        barChart.setDescription("");

//        mChart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawBarShadow(false);

        barChart.setDrawGridBackground(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(mContext, R.layout.custom_marker_view);

        // set the marker to the chart
        barChart.setMarkerView(mv);

        setData(barChart);

        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
        //l.setTypeface(mTfLight);
        l.setYOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xl = barChart.getXAxis();
        xl.setTypeface(mTfLight);
        //xl.setGranularity(1f);
        xl.setDrawGridLines(true);//去掉网格
        xl.setCenterAxisLabels(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);//让x轴显示在下方
        final float startValue;
        if(multipleBarObject.getxValues().size() > 0){
            startValue = Float.parseFloat(multipleBarObject.getxValues().get(0));
        }else{
            startValue = 10000f;
        }
        xl.setValueFormatter(new AxisValueFormatter() {
            int i = -1;
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value >= startValue && value < startValue + multipleBarObject.getxValues().size() && multipleBarObject.getxLabels().size() > 0){
                    float a = value - startValue;
                    int index = (int)a;
                    return multipleBarObject.getxLabels().get(index);
                }
               return "";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

       YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        //leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false); //保留y轴网格
        //leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)*/
        leftAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf(value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        barChart.getAxisRight().setEnabled(false);
    }

    private void setData(BarChart barChart){

        float groupSpace = 0.0f;
        float barSpace = 0.1f; // x3 dataset
        float barWidth = 0.4f; // x3 dataset
        BarData data = new BarData(multipleBarObject.getBarDataSets());
        data.setValueTypeface(mTfLight);
        barChart.setData(data);

        List<String> multipleBarXValues = multipleBarObject.getxValues();
        float start;
        if(multipleBarXValues.size() > 0){
            start = Float.parseFloat(multipleBarXValues.get(0));
        }else{
            start = 0.0f;
        }
        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinValue(start);
        barChart.getXAxis().setDrawLabels(true);
        barChart.getXAxis().setLabelCount(multipleBarXValues.size());
        float f = barChart.getBarData().getGroupWidth(groupSpace, barSpace);
        float m = f * multipleBarXValues.size()  + start;
        barChart.getXAxis().setAxisMaxValue(m);
        barChart.groupBars(start, groupSpace, barSpace);
        barChart.invalidate();
    }

}
