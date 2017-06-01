package com.leedane.cn.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 记账统计的接收器
 * Created by LeeDane on 2016/8/17.
 */
public class CalculateFinancialReceiver extends BroadcastReceiver {
    private static final String TAG = "CalculateFinancialReceiver";

    /**
     * 用户数据有更新的接口监听器
     */
    public interface CalculateFinancialListener{
        void calculate(int model);
    }

    private CalculateFinancialListener calculateFinancialListener;

    public void setCalculateFinancialListener(CalculateFinancialListener calculateFinancialListener) {
        this.calculateFinancialListener = calculateFinancialListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;
        try{
            FinancialList financialList = null;
            int model = intent.getIntExtra("model", 0);

            /*if(financialList != null)
                switch (model){
                    case 1://今日
                        CalculateUtil.toDayList = financialList;
                    case 2://昨日
                        CalculateUtil.yesterDayList = financialList;
                        break;
                    case 3://本周
                        CalculateUtil.weekList = financialList;
                        break;
                    case 4://本月
                        CalculateUtil.monthList = financialList;
                        break;
                    case 5://本年
                        CalculateUtil.yearList = financialList;
                        break;

                }*/

            if(null != calculateFinancialListener)
                calculateFinancialListener.calculate(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
