package com.leedane.cn.financial.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * 收入activity
 * Created by LeeDane on 2016/7/21.
 */
public class IncomeActivity extends BaseActivity {

    private LineChartView lineChartView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(IncomeActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.IncomeActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_financial_income);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(getStringResource(R.string.financial));
        backLayoutVisible();

        initView();
    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        lineChartView = (LineChartView)findViewById(R.id.chart);
        List<PointValue> pointValueList = new ArrayList<PointValue>();
        PointValue pointValue1 = new PointValue(10,30);
        pointValueList.add(pointValue1);
        PointValue pointValue2 = new PointValue(20,20);
        pointValueList.add(pointValue2);
        PointValue pointValue3 = new PointValue(30,70);
        pointValueList.add(pointValue3);
        PointValue pointValue4 = new PointValue(40,69);
        pointValueList.add(pointValue4);
        PointValue pointValue5 = new PointValue(50,64);
        pointValueList.add(pointValue5);
        PointValue pointValue6 = new PointValue(60,31);
        pointValueList.add(pointValue6);
        PointValue pointValue7 = new PointValue(70,22);
        pointValueList.add(pointValue7);
        PointValue pointValue8 = new PointValue(80,100);
        pointValueList.add(pointValue8);

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(pointValueList).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        lineChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, PointValue pointValue) {
                ToastUtil.success(IncomeActivity.this, "value:"+pointValue.getY());
            }

            @Override
            public void onValueDeselected() {

            }
        });
        lineChartView.setLineChartData(data);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
