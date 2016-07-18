package com.leedane.cn.financial.charts.line;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * 构建折线图的对象
 * Created by LeeDane on 2016/8/14.
 */
public class LineObject {
    private ArrayList<String> xValues;  //xVals用来表示每个x轴上的内容
    private ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个y轴的实际数据
    private String lineDesc;
    private int highLightColor;
    private int color; //设置曲线的颜色

    public ArrayList<String> getxValues() {
        return xValues;
    }

    public void setxValues(ArrayList<String> xValues) {
        this.xValues = xValues;
    }

    public ArrayList<Entry> getyValues() {
        return yValues;
    }

    public void setyValues(ArrayList<Entry> yValues) {
        this.yValues = yValues;
    }

    public String getLineDesc() {
        return lineDesc;
    }

    public void setLineDesc(String lineDesc) {
        this.lineDesc = lineDesc;
    }

    public int getHighLightColor() {
        return highLightColor;
    }

    public void setHighLightColor(int highLightColor) {
        this.highLightColor = highLightColor;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
