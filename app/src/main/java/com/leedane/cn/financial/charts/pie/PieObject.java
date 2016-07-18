package com.leedane.cn.financial.charts.pie;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

/**
 * 构建饼状图的对象
 * Created by LeeDane on 2016/7/13.
 */
public class PieObject {

   // private ArrayList<String> xValues;  //xVals用来表示每个饼块上的内容
    private ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();  //yVals用来表示封装每个饼块的实际数据
    private String pieDesc;//显示在比例图上
    private String title; //显示饼状图的主题
    private String centerDesc; //显示在饼状图中间的描述信息
    private ArrayList<Integer> colors = new ArrayList<Integer>(); // 饼图颜色

    public ArrayList<PieEntry> getyValues() {
        return yValues;
    }

    public void setyValues(ArrayList<PieEntry> yValues) {
        this.yValues = yValues;
    }

    public String getPieDesc() {
        return pieDesc;
    }

    public void setPieDesc(String pieDesc) {
        this.pieDesc = pieDesc;
    }

    public ArrayList<Integer> getColors() {
        return colors;
    }

    public void setColors(ArrayList<Integer> colors) {
        this.colors = colors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCenterDesc() {
        return centerDesc;
    }

    public void setCenterDesc(String centerDesc) {
        this.centerDesc = centerDesc;
    }
}
