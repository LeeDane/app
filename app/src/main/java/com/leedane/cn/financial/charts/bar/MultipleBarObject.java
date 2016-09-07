package com.leedane.cn.financial.charts.bar;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建多柱状图图的对象
 * Created by LeeDane on 2016/8/15.
 */
public class MultipleBarObject {


    private List<IBarDataSet> barDataSets;//多个柱子的数据
    private List<String> xValues; //x轴
    private List<String> xLabels; //x轴的显示文字
    private float minValue; //最小值
    private float maxValue; //最大值

    public List<IBarDataSet> getBarDataSets() {
        return barDataSets;
    }

    public void setBarDataSets(List<IBarDataSet> barDataSets) {
        this.barDataSets = barDataSets;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public List<String> getxValues() {
        return xValues;
    }

    public void setxValues(List<String> xValues) {
        this.xValues = xValues;
    }

    public List<String> getxLabels() {
        return xLabels;
    }

    public void setxLabels(List<String> xLabels) {
        this.xLabels = xLabels;
    }
}
