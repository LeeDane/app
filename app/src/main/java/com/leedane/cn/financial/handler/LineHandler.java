package com.leedane.cn.financial.handler;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.leedane.cn.financial.charts.line.LineObject;
import com.leedane.cn.util.StringUtil;

/**
 * 线形图图形处理工具类
 * Created by LeeDane on 2016/8/14.
 */
public class LineHandler {
    private LineObject mLineObject;
    private Context mContext;
    public LineHandler(Context context, LineObject lineObject){
        this.mLineObject = lineObject;
        this.mContext = context;
    }

    /**
     * 展示折线图
     * @param lineChart
     */
    public void showLine(LineChart lineChart){
        // 是否在折线图上添加边框
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        // 设置右下角描述
        lineChart.setDescription(StringUtil.changeNotNull(mLineObject.getLineDesc()));
        //设置透明度
        lineChart.setAlpha(0.8f);
        //设置网格底下的那条线的颜色
        lineChart.setBorderColor(Color.rgb(213, 216, 214));
        //设置高亮显示
        //lineChart.setHighlightEnabled(true);
        //设置是否可以触摸，如为false，则不能拖动，缩放等
        lineChart.setTouchEnabled(true);
        //设置是否可以拖拽
        lineChart.setDragEnabled(true);
        //设置是否可以缩放
        lineChart.setScaleEnabled(true);
        //设置是否能扩大扩小
        lineChart.setPinchZoom(true);
        /**
         * ====================2.布局点添加数据-自由布局===========================
         */
        // 折线图的点，点击战士的布局和数据
       /* MyMarkView mv = new MyMarkView(this);
        mChart.setMarkerView(mv);*/
        // 加载数据
        LineData data = getLineData();
        lineChart.setData(data);
        /**
         * ====================3.x，y动画效果和刷新图表等===========================
         */
        //从X轴进入的动画
        lineChart.animateX(4000);
        lineChart.animateY(3000);   //从Y轴进入的动画
        lineChart.animateXY(3000, 3000);    //从XY轴一起进入的动画
        //设置最小的缩放
        lineChart.setScaleMinima(0.5f, 1f);
        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);  //设置图最下面显示的类型
        l.setTextSize(15);
        l.setTextColor(Color.rgb(104, 241, 175));
        l.setFormSize(30f);
        // 刷新图表
        lineChart.invalidate();
    }

    private LineData getLineData() {
        LineDataSet set1 = new LineDataSet(mLineObject.getyValues(), null);
        //set1.setDrawCubic(true);  //设置曲线为圆滑的线
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(false);  //设置包括的范围区域填充颜色
        set1.setDrawCircles(true);  //设置有圆点
        set1.setLineWidth(2f);    //设置线的宽度
        set1.setCircleSize(5f);   //设置小圆的大小
        set1.setHighLightColor(mLineObject.getHighLightColor());
        set1.setColor(mLineObject.getColor());    //设置曲线的颜色
        set1.setValues(mLineObject.getyValues());
        return new LineData(set1);
    }

}
